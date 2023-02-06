package com.test.web.api.domain.integrations

import derevo.derive
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.Coercible
import io.estatico.newtype.macros.newtype
import sttp.tapir.derevo.schema

object types {
  implicit def coercibleEncoder[R, N](implicit ev: Coercible[Encoder[R], Encoder[N]], R: Encoder[R]): Encoder[N] = ev(R)
  implicit def coercibleDecoder[R, N](implicit ev: Coercible[Decoder[R], Decoder[N]], R: Decoder[R]): Decoder[N] = ev(R)
  @derive(schema) @newtype case class DestinationId(value: String)
  @derive(schema) @newtype case class DestinationName(value: String)
  @derive(schema) @newtype case class IntegrationId(value: String)
  @derive(schema) @newtype case class IntegrationName(value: String)
  @derive(schema) @newtype case class ConnectionId(value: String)
}
