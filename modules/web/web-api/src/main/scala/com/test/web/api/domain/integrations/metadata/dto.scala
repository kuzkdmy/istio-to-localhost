package com.test.web.api.domain.integrations.metadata

import com.test.web.api.domain.integrations.types.{DestinationId, DestinationName, IntegrationId, IntegrationName}
import derevo.derive
import io.circe._
import io.circe.generic.semiauto._
import sttp.tapir.derevo.schema

@derive(schema) case class ApiDestinationMetadata(
    destinationId: DestinationId,
    destinationName: DestinationName
)
object ApiDestinationMetadata {
  implicit val decoder: Decoder[ApiDestinationMetadata] = deriveDecoder[ApiDestinationMetadata]
  implicit val encoder: Encoder[ApiDestinationMetadata] = deriveEncoder[ApiDestinationMetadata]
}

@derive(schema) case class ApiIntegrationMetadata(
    integrationId: IntegrationId,
    integrationName: IntegrationName,
    destinationId: DestinationId
)
object ApiIntegrationMetadata {
  implicit val decoder: Decoder[ApiIntegrationMetadata] = deriveDecoder[ApiIntegrationMetadata]
  implicit val encoder: Encoder[ApiIntegrationMetadata] = deriveEncoder[ApiIntegrationMetadata]
}

@derive(schema) case class ApiDestinationMetadataList(
    data: List[ApiDestinationMetadata]
)
object ApiDestinationMetadataList {
  implicit val decoder: Decoder[ApiDestinationMetadataList] = deriveDecoder[ApiDestinationMetadataList]
  implicit val encoder: Encoder[ApiDestinationMetadataList] = deriveEncoder[ApiDestinationMetadataList]
}

@derive(schema) case class ApiIntegrationMetadataList(
    data: List[ApiIntegrationMetadata]
)
object ApiIntegrationMetadataList {
  implicit val decoder: Decoder[ApiIntegrationMetadataList] = deriveDecoder[ApiIntegrationMetadataList]
  implicit val encoder: Encoder[ApiIntegrationMetadataList] = deriveEncoder[ApiIntegrationMetadataList]
}
