package com.test.connector_hub.connections.service

import com.test.common.domain.types.Ctx
import com.test.common.log.log
import com.test.transport_protocol.connector_hub_connections.ZioConnectorHubConnections.ZConnectorHubConnectionsService
import com.test.transport_protocol.connector_hub_connections._
import io.grpc.Status
import org.slf4j.{Logger, LoggerFactory}
import zio.ZIO

object ConnectorHubConnectionsGrpcServerImpl extends ZConnectorHubConnectionsService[Ctx] {
  implicit private val logger: Logger = LoggerFactory.getLogger(this.getClass)

  override def createConnection(request: CreateConnectionRequestProto): ZIO[Ctx, Status, CreateConnectionResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call create connection")
    } yield CreateConnectionResponseProto()
  }

  override def deleteConnection(request: DeleteConnectionRequestProto): ZIO[Ctx, Status, DeleteConnectionResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call delete connections")
    } yield DeleteConnectionResponseProto()
  }

  override def updateConnection(request: UpdateConnectionRequestProto): ZIO[Ctx, Status, UpdateConnectionResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call update connection")
    } yield UpdateConnectionResponseProto()
  }

  override def listConnections(request: ListConnectionsRequestProto): ZIO[Ctx, Status, ListConnectionsResponseProto] = {
    for {
      implicit0(ctx: Ctx) <- ZIO.service[Ctx]
      _                   <- log.info("grpc call list connections")
    } yield ListConnectionsResponseProto(
      data = List(
        ConnectionProto(
          tenantId      = ctx.tenantId.value,
          integrationId = "11",
          destinationId = "1",
          id            = "123"
        )
      )
    )
  }

}
