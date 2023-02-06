package com.test.web.service.grpc

import com.test.common.domain.types.Ctx
import com.test.common.grpc.GrpcClient.mkClient
import com.test.transport_protocol.connector_hub_connections.ZioConnectorHubConnections.ConnectorHubConnectionsServiceClient
import com.test.transport_protocol.connector_hub_metadata.ZioConnectorHubMetadata.ConnectorHubMetadataServiceClient
import com.test.web.config.AppConfig.ServicesConf
import io.grpc.{CallOptions, Status}
import scalapb.zio_grpc.{SafeMetadata, ZManagedChannel}
import zio._

object GrpcClients {
  lazy val layer = {
    def ch[B <: scalapb.zio_grpc.CallOptionsMethods[_]: Tag](
        urlFn: ServicesConf => String,
        managed: (ZManagedChannel, IO[Status, CallOptions], ZIO[Ctx, Status, SafeMetadata]) => ZIO[Scope, Throwable, B]
    ) = ZLayer.scoped(for {
      svc <- ZIO.service[ServicesConf]
      res <- mkClient(urlFn(svc), managed)
    } yield res)

    ch(_.connectorHubConnectionsUrl, ConnectorHubConnectionsServiceClient.scoped[Ctx]) ++
      ch(_.connectorHubMetadataUrl, ConnectorHubMetadataServiceClient.scoped[Ctx])
  }

}
