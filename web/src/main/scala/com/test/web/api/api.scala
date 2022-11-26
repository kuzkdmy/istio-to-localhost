package com.test.web

import cats.Applicative
import cats.implicits.catsSyntaxEitherId
import com.test.common.types.{Ctx, TenantId, UserId, XRequestId}
import com.test.common.ContextHeaders._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.{Endpoint, EndpointServerLogicOps, header}
import zio.{RIO, Task, ZIO}

import scala.util.control.NoStackTrace

package object api {
  type SecureEndpoint[I, E, O] = Endpoint[AuthCtx, I, E, O, ZioStreams]

  implicit final class EndpointServerLogicOpsExt[I, E, O](private val opts: EndpointServerLogicOps[AuthCtx, I, E, O, ZioStreams]) extends AnyVal {
    def serverLogicSecured[R](
        f: (Ctx, I) => RIO[R, Either[E, O]]
    ): ZServerEndpoint[R, ZioStreams] =
      customServerLogicSecured[AuthCtx, Ctx, R](toCtx)(opts, f)

    def customServerLogicSecured[IN_CTX, OUT_CTX, R](validateTransformContext: IN_CTX => Task[OUT_CTX])(
        opts: EndpointServerLogicOps[IN_CTX, I, E, O, ZioStreams],
        processServerLogic: (OUT_CTX, I) => ZIO[R, Throwable, Either[E, O]]
    ): ZServerEndpoint[R, ZioStreams] = {
      opts
        .serverSecurityLogic(inputCtx =>
          ZIO.suspendSucceed[R, Throwable, Either[E, OUT_CTX]](
            validateTransformContext(inputCtx).map(_.asRight[E])
          )
        )
        .serverLogic(ctx => input => processServerLogic(ctx, input))
    }

    private def toCtx(ctx: AuthCtx): Task[Ctx] = {
      Applicative[Option]
        .map3(ctx.tId, ctx.uId, ctx.requestId) { case (tId, uId, requestId) =>
          Ctx(tId, uId, requestId)
        } match {
        case Some(value) => ZIO.succeed(value)
        case None        => ZIO.fail(new RuntimeException(s"Security violation, auth not filled properly = $ctx") with NoStackTrace)
      }
    }

  }

  implicit final class EndpointTypeSyntax[I, E, O, -R](private val e: Endpoint[Unit, I, E, O, R]) extends AnyVal {
    import sttp.tapir.codec.newtype._
    def securedEndpoint(): Endpoint[AuthCtx, I, E, O, R] = {
      e.securityIn(header[Option[TenantId]](X_TENANT_ID))
        .securityIn(header[Option[UserId]](X_USER_ID))
        .securityIn(header[Option[XRequestId]](X_REQUEST_ID))
        .mapSecurityInTo[AuthCtx]
    }
  }
}
