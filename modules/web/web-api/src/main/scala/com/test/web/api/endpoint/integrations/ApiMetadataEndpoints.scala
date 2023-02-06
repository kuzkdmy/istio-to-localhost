package com.test.web.api
package endpoint.integrations

import com.test.web.api.SecureEndpoint
import com.test.web.api.domain.integrations.metadata.{ApiDestinationMetadataList, ApiIntegrationMetadataList}
import com.test.web.api.domain.integrations.types.{DestinationId, IntegrationId}
import sttp.tapir._
import sttp.tapir.codec.newtype._
import sttp.tapir.json.circe.jsonBody

object ApiMetadataEndpoints {
  private val basePath: EndpointInput[Unit] = "api" / "v1" / "integration" / "metadata"

  val listDestinationsMetadataE: SecureEndpoint[ApiListDestinationsMetadata, Unit, ApiDestinationMetadataList] = endpoint.get
    .in(basePath / "destinations")
    .securedEndpoint()
    .in(query[List[DestinationId]]("destinationId"))
    .mapInTo[ApiListDestinationsMetadata]
    .out(jsonBody[ApiDestinationMetadataList])

  val listIntegrationsMetadataE: SecureEndpoint[ApiListIntegrationsMetadata, Unit, ApiIntegrationMetadataList] = endpoint.get
    .in(basePath / path[DestinationId]("dId") / "integrations")
    .securedEndpoint()
    .in(query[List[IntegrationId]]("integrationId"))
    .mapInTo[ApiListIntegrationsMetadata]
    .out(jsonBody[ApiIntegrationMetadataList])

  case class ApiListDestinationsMetadata(dIds: List[DestinationId])
  case class ApiListIntegrationsMetadata(dId: DestinationId, iIds: List[IntegrationId])
}
