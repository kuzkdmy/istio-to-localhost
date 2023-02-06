package com.test.web.api
package endpoint.integrations

import com.test.web.api.SecureEndpoint
import com.test.web.api.domain.integrations.connection.{ApiConnection, ApiConnectionList}
import com.test.web.api.domain.integrations.types.{ConnectionId, DestinationId, IntegrationId}
import derevo.derive
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir
import sttp.tapir._
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema
import sttp.tapir.json.circe.jsonBody

object ApiConnectionsEndpoints {
  private val basePath: EndpointInput[Unit] = "api" / "v1" / "integration" / "connections"

  val getE: SecureEndpoint[ConnectionId, ApiGetErr, ApiConnection] = endpoint.get
    .in(basePath / path[ConnectionId]("id"))
    .securedEndpoint()
    .out(jsonBody[ApiConnection])
    .errorOut(
      tapir.oneOf[ApiGetErr](
        oneOfVariantFromMatchType(StatusCode.NotFound, jsonBody[ApiGetErr.NotFound])
      )
    )

  val listE: SecureEndpoint[ApiListConnections, Unit, ApiConnectionList] = endpoint.get
    .in(basePath)
    .securedEndpoint()
    .in(query[List[ConnectionId]]("id"))
    .in(query[List[DestinationId]]("destinationId"))
    .in(query[List[IntegrationId]]("integrationId"))
    .mapInTo[ApiListConnections]
    .out(jsonBody[ApiConnectionList])

  sealed trait ApiGetErr
  object ApiGetErr {
    @derive(schema) case class NotFound(message: String) extends ApiGetErr
  }

  case class ApiListConnections(
      ids: List[ConnectionId]   = List.empty,
      dIds: List[DestinationId] = List.empty,
      iIds: List[IntegrationId] = List.empty
  )
}
