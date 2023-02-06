package com.test.prototype.monolith

import com.test.common.BaseApp
import com.test.common.domain.types.Ctx
import com.test.common.grpc.{GrpcServerConfig, GrpcServiceList, GrpcServices}
import com.test.connector_hub.connections.ConnectorHubConnectionsMain
import com.test.connector_hub.metadata.ConnectorHubMetadataMain
import com.test.prototype.monolith.config.AppConfig
import com.test.transport_protocol.connector_hub_connections.ZioConnectorHubConnections.ZConnectorHubConnectionsService
import com.test.transport_protocol.connector_hub_metadata.ZioConnectorHubMetadata.ZConnectorHubMetadataService
import com.test.web
import com.test.web.WebMain
import zio.{ZIO, ZIOAppDefault}

object MonolithMain extends ZIOAppDefault {
  override def run = BaseApp
    .run(
      appRoutesF = ZIO.succeed(WebMain.endpoints),
      grpcServicesF = Some(
        for {
          grpcConf         <- ZIO.service[GrpcServerConfig]
          chMetadataSvc    <- ZIO.service[ZConnectorHubMetadataService[Ctx]]
          chConnectionsSvc <- ZIO.service[ZConnectorHubConnectionsService[Ctx]]
        } yield GrpcServices(GrpcServiceList(grpcConf).add(chMetadataSvc).add(chConnectionsSvc))
      )
    )
    .provide(appLayer)

  lazy val appLayer = AppConfig.live >+>
    web.service.grpc.GrpcClients.layer >+>
    web.WebMain.endpointsLayerLive >+>
    ConnectorHubMetadataMain.serverLayerLive >+>
    ConnectorHubConnectionsMain.serverLayerLive
}
