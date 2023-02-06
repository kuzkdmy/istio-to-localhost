package com.test.web.api.domain.integrations.connection

import com.test.web.api.domain.integrations.types.{ConnectionId, DestinationId, IntegrationId}
import derevo.derive
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.derevo.schema

@derive(schema) case class ApiConnection(
    id: ConnectionId,
    destinationId: DestinationId,
    integrationId: IntegrationId
)
object ApiConnection {
  implicit val decoder: Decoder[ApiConnection] = deriveDecoder[ApiConnection]
  implicit val encoder: Encoder[ApiConnection] = deriveEncoder[ApiConnection]
}

@derive(schema) case class ApiConnectionList(
    data: List[ApiConnection]
)
object ApiConnectionList {
  implicit val decoder: Decoder[ApiConnectionList] = deriveDecoder[ApiConnectionList]
  implicit val encoder: Encoder[ApiConnectionList] = deriveEncoder[ApiConnectionList]
}
