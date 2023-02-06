package com.test.common.grpc

import io.grpc.netty.NettyServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import scalapb.zio_grpc.{ScopedServer, Server}
import zio.{RIO, Scope}

object GrpcServer {
  def buildGrpcServer[T](svc: GrpcServices[T]): RIO[T with Scope, Server] = {
    ScopedServer.fromServiceList(
      NettyServerBuilder
        .forPort(svc.port)
        .addService(ProtoReflectionService.newInstance())
        .maxInboundMetadataSize(svc.inboundMetadataSize),
      svc.serviceList.services
    )
  }
}
