package com.test.connector_hub.metadata.service

import com.test.common.domain.types.Ctx
import com.test.common.log.log
import com.test.transport_protocol.connector_hub_metadata.ZioConnectorHubMetadata.ZConnectorHubMetadataService
import com.test.transport_protocol.connector_hub_metadata._
import io.grpc.Status
import org.slf4j.{Logger, LoggerFactory}
import zio.ZIO

object ConnectorHubMetadataGrpcServerImpl extends ZConnectorHubMetadataService[Ctx] {
  implicit private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def integrationMetadata(request: IntegrationMetadataRequestProto): ZIO[Ctx, Status, IntegrationMetadataResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call get integration metadata")
    } yield IntegrationMetadataResponseProto(
      data = List(
        IntegrationMetadataProto(
          destinationId   = "1",
          integrationId   = "2",
          integrationName = "Google Custom Match"
        )
      )
    )
  }
  override def destinationMetadata(request: DestinationMetadataRequestProto): ZIO[Ctx, Status, DestinationMetadataResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call get destination metadata")
    } yield DestinationMetadataResponseProto(
      data = List(
        DestinationMetadataProto(
          destinationId   = "1",
          destinationName = "Google"
        )
      )
    )
  }
}
