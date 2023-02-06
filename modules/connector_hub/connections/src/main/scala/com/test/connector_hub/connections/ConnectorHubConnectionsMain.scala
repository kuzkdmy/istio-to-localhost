package com.test.connector_hub.connections

import com.test.common.BaseApp
import com.test.common.domain.types.Ctx
import com.test.common.grpc.{GrpcServerConfig, GrpcServiceList, GrpcServices}
import com.test.connector_hub.connections.config.AppConfig
import com.test.connector_hub.connections.service.ConnectorHubConnectionsGrpcServerImpl
import com.test.transport_protocol.connector_hub_connections.ZioConnectorHubConnections.ZConnectorHubConnectionsService
import zio._

object ConnectorHubConnectionsMain extends ZIOAppDefault {
  override def run = mainApp.provide(appEnv)

  lazy val mainApp = BaseApp.run(grpcServicesF = Some(for {
    svc      <- ZIO.service[ZConnectorHubConnectionsService[Ctx]]
    grpcConf <- ZIO.service[GrpcServerConfig]
  } yield GrpcServices(GrpcServiceList(grpcConf).add(svc))))

  lazy val appEnv = AppConfig.live >+> serverLayerLive

  lazy val serverLayerLive = ZLayer.succeed(ConnectorHubConnectionsGrpcServerImpl)
}
