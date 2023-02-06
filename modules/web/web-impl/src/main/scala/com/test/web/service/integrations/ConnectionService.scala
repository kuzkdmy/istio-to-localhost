package com.test.web.service.integrations

import com.test.common.domain.types.Ctx
import com.test.transport_protocol.connector_hub_connections.ZioConnectorHubConnections.ConnectorHubConnectionsServiceClient
import com.test.web.api.domain.integrations.connection.ApiConnectionList
import com.test.web.api.endpoint.integrations.ApiConnectionsEndpoints.ApiListConnections
import com.test.web.converter.integrations.ApiConnectionsConverter
import zio.{Task, ZIO, ZLayer}

trait ConnectionService {
  def listConnections(filter: ApiListConnections)(implicit ctx: Ctx): Task[ApiConnectionList]
}

case class ConnectionServiceLive(svc: ConnectorHubConnectionsServiceClient.ZService[Ctx]) extends ConnectionService {
  override def listConnections(filter: ApiListConnections)(implicit ctx: Ctx): Task[ApiConnectionList] = {
    svc
      .listConnections(ApiConnectionsConverter.toListRequestProto(filter))
      .map(ApiConnectionsConverter.toApiConnectionList)
      .provide(ZLayer.succeed(ctx))
      .catchAll(s => ZIO.fail(s.asException))
  }
}

object ConnectionServiceLive {
  lazy val layer = ZLayer.fromFunction(ConnectionServiceLive.apply _)
}
