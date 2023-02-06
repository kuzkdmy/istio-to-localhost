package com.test.common.grpc

import com.test.common.domain.types.{Ctx, TenantId, UserId, XRequestId}
import com.test.common.grpc.GrpcContextMetadata.{X_REQUEST_ID_KEY, X_TENANT_ID_KEY, X_USER_ID_KEY}
import io.grpc.{Metadata, Status}
import org.slf4j.{Logger, LoggerFactory}
import scalapb.zio_grpc.TransformableService.TransformableServiceOps
import scalapb.zio_grpc.{RequestContext, ServiceList, TransformableService, ZBindableService}
import zio.{IO, ZIO}

import scala.concurrent.duration.{DurationInt, FiniteDuration}

final class GrpcServiceList[-R] private (val cfg: GrpcServerConfig, val services: ServiceList[R] = ServiceList.empty) {
  private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  def add[S[_]](service: S[Ctx])(implicit ts: TransformableService[S], b: ZBindableService[S[RequestContext]]): GrpcServiceList[R] = {
    new GrpcServiceList(
      cfg,
      services.add(
        new TransformableServiceOps(new TransformableServiceOps(service).transformContextZIO[RequestContext](toCtx))
          .transform[RequestContext](TimeoutTransform(cfg.serverTimeout))
      )(b)
    )
  }

  private def toCtx(rc: RequestContext): IO[Status, Ctx] = {
    val meta = rc.metadata
    val extractLong = (key: Metadata.Key[String]) =>
      meta
        .get(key)
        .someOrFail(Status.UNAUTHENTICATED.withDescription(s"no ${key.name()} in metadata"))
        .map(_.toLongOption)
        .someOrFail(Status.UNAUTHENTICATED.withDescription(s"${key.name()} is not a long value"))
    val extractCtxTask = for {
      tId <- extractLong(X_TENANT_ID_KEY)
      uId <- extractLong(X_USER_ID_KEY)
      rId <- meta
               .get(X_REQUEST_ID_KEY)
               .someOrFail(Status.UNAUTHENTICATED.withDescription("no x-request-Id in metadata"))
    } yield Ctx(TenantId(tId), UserId(uId), XRequestId(rId))

    extractCtxTask.tapError(s => ZIO.succeed(logger.error("failed extract context from grpc metadata", s.asException())))
  }
}

object GrpcServiceList {
  def apply(cfg: GrpcServerConfig) = new GrpcServiceList(cfg)
}

case class GrpcServices[-R](serviceList: GrpcServiceList[R]) {
  def port: Int                = serviceList.cfg.port
  def inboundMetadataSize: Int = serviceList.cfg.inboundMetadataSize
}
final case class GrpcServerConfig(
    port: Int                     = 10000,
    inboundMetadataSize: Int      = 32768,
    serverTimeout: FiniteDuration = 30.seconds
)
