package com.test.web

import com.test.common.BaseApp
import com.test.web.api.route.integration._
import com.test.web.config.AppConfig
import com.test.web.service.grpc.GrpcClients
import com.test.web.service.integrations.{ConnectionServiceLive, MetadataServiceLive}
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.ZServerEndpoint
import zio.{ZIO, ZIOAppDefault}

object WebMain extends ZIOAppDefault {
  override def run = mainApp.provide(appLayer)
  lazy val mainApp = BaseApp.run(appRoutesF = ZIO.succeed(endpoints))
  lazy val appLayer = AppConfig.live >+>
    GrpcClients.layer >+>
    endpointsLayerLive

  private type R = ConnectionsRouteService with MetadataRouteService
  lazy val endpoints: List[ZServerEndpoint[R, ZioStreams]] =
    ConnectionsRoute.endpoints[R] ++
      MetadataRoute.endpoints[R]

  lazy val endpointsLayerLive =
    ConnectionServiceLive.layer >+> ConnectionsRouteLive.layer >+>
      MetadataServiceLive.layer >+> MetadataRouteLive.layer
}
