package com.test.common.domain

import io.circe.{Decoder, Encoder}
import io.estatico.newtype.Coercible
import io.estatico.newtype.macros.newtype
import zio.{Random, UIO}

object types {

  /** Derive circe codecs for newtypes and tagged types. */
  implicit def coercibleEncoder[R, N](implicit ev: Coercible[Encoder[R], Encoder[N]], R: Encoder[R]): Encoder[N] = ev(R)
  implicit def coercibleDecoder[R, N](implicit ev: Coercible[Decoder[R], Decoder[N]], R: Decoder[R]): Decoder[N] = ev(R)

  @newtype case class TenantId(value: Long)
  @newtype case class UserId(value: Long)
  @newtype case class XRequestId(value: String)
  case class Ctx(tenantId: TenantId, userId: UserId, requestId: XRequestId, payloadCtx: Option[String] = None)
  object Ctx {
    def systemCtx: UIO[Ctx] = Random.nextUUID.map(uuid => Ctx(TenantId(0), UserId(0), XRequestId(uuid.toString)))
  }
}
