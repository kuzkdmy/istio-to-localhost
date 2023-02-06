package com.test.connector_hub.metadata

import com.test.common.BaseApp
import com.test.common.domain.types.Ctx
import com.test.common.grpc.{GrpcServerConfig, GrpcServiceList, GrpcServices}
import com.test.connector_hub.metadata.config.AppConfig
import com.test.connector_hub.metadata.service.ConnectorHubMetadataGrpcServerImpl
import com.test.transport_protocol.connector_hub_metadata.ZioConnectorHubMetadata.ZConnectorHubMetadataService
import zio._

object ConnectorHubMetadataMain extends ZIOAppDefault {
  override def run = mainApp.provide(appEnv)

  lazy val mainApp = BaseApp.run(grpcServicesF = Some(for {
    svc      <- ZIO.service[ZConnectorHubMetadataService[Ctx]]
    grpcConf <- ZIO.service[GrpcServerConfig]
  } yield GrpcServices(GrpcServiceList(grpcConf).add(svc))))

  lazy val appEnv = AppConfig.live >+> serverLayerLive

  lazy val serverLayerLive = ZLayer.succeed(ConnectorHubMetadataGrpcServerImpl)
}
