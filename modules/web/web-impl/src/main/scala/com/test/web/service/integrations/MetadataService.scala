package com.test.web.service.integrations

import com.test.common.domain.types.Ctx
import com.test.transport_protocol.connector_hub_metadata.ZioConnectorHubMetadata.ConnectorHubMetadataServiceClient
import com.test.web.api.domain.integrations.metadata.{ApiDestinationMetadataList, ApiIntegrationMetadataList}
import com.test.web.api.endpoint.integrations.ApiMetadataEndpoints.{ApiListDestinationsMetadata, ApiListIntegrationsMetadata}
import com.test.web.converter.integrations.ApiMetadataConverter
import zio.{Task, ZIO, ZLayer}

trait MetadataService {
  def listDestinations(filter: ApiListDestinationsMetadata)(implicit ctx: Ctx): Task[ApiDestinationMetadataList]
  def listIntegrations(filter: ApiListIntegrationsMetadata)(implicit ctx: Ctx): Task[ApiIntegrationMetadataList]
}

case class MetadataServiceLive(svc: ConnectorHubMetadataServiceClient.ZService[Ctx]) extends MetadataService {
  override def listDestinations(filter: ApiListDestinationsMetadata)(implicit ctx: Ctx): Task[ApiDestinationMetadataList] = {
    svc
      .destinationMetadata(ApiMetadataConverter.toIntegrationMetadataProto(filter))
      .map(ApiMetadataConverter.toDestinationMetadataListApi)
      .provide(ZLayer.succeed(ctx))
      .catchAll(s => ZIO.fail(s.asException))
  }

  override def listIntegrations(filter: ApiListIntegrationsMetadata)(implicit ctx: Ctx): Task[ApiIntegrationMetadataList] = {
    svc
      .integrationMetadata(ApiMetadataConverter.toIntegrationMetadataProto(filter))
      .map(ApiMetadataConverter.toIntegrationMetadataListApi)
      .provide(ZLayer.succeed(ctx))
      .catchAll(s => ZIO.fail(s.asException))
  }
}

object MetadataServiceLive {
  lazy val layer = ZLayer.fromFunction(MetadataServiceLive.apply _)
}
