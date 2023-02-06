package com.test.web.converter.integrations

import com.test.transport_protocol.connector_hub_metadata._
import com.test.web.api.domain.integrations.metadata.{ApiDestinationMetadata, ApiDestinationMetadataList, ApiIntegrationMetadata, ApiIntegrationMetadataList}
import com.test.web.api.domain.integrations.types.{DestinationId, DestinationName, IntegrationId, IntegrationName}
import com.test.web.api.endpoint.integrations.ApiMetadataEndpoints.{ApiListDestinationsMetadata, ApiListIntegrationsMetadata}

object ApiMetadataConverter {

  def toIntegrationMetadataProto(f: ApiListDestinationsMetadata): DestinationMetadataRequestProto = {
    DestinationMetadataRequestProto(
      destinationId = f.dIds.map(_.value)
    )
  }

  def toDestinationMetadataListApi(f: DestinationMetadataResponseProto): ApiDestinationMetadataList = {
    ApiDestinationMetadataList(
      data = f.data.map(toDestinationMetadataApi).toList
    )
  }

  def toDestinationMetadataApi(f: DestinationMetadataProto): ApiDestinationMetadata = {
    ApiDestinationMetadata(
      destinationId   = DestinationId(f.destinationId),
      destinationName = DestinationName(f.destinationName)
    )
  }

  def toIntegrationMetadataProto(f: ApiListIntegrationsMetadata): IntegrationMetadataRequestProto = {
    IntegrationMetadataRequestProto(
      destinationId = f.dId.value,
      integrationId = f.iIds.map(_.value)
    )
  }

  def toIntegrationMetadataListApi(f: IntegrationMetadataResponseProto): ApiIntegrationMetadataList = {
    ApiIntegrationMetadataList(
      data = f.data.map(toIntegrationMetadataApi).toList
    )
  }

  def toIntegrationMetadataApi(f: IntegrationMetadataProto): ApiIntegrationMetadata = {
    ApiIntegrationMetadata(
      integrationId   = IntegrationId(f.integrationId),
      integrationName = IntegrationName(f.integrationName),
      destinationId   = DestinationId(f.destinationId)
    )
  }

}
