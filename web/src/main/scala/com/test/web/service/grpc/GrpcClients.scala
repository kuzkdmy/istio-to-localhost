package com.test.web.service.grpc

import com.test.common.grpc.GrpcClientsConfig
import com.test.common.grpc.GrpcContextMetadata.{X_REQUEST_ID_KEY, X_TENANT_ID_KEY, X_USER_ID_KEY}
import com.test.common.types.Ctx
import com.test.transport_protocol.users.ZioUsers.UsersServiceClient
import io.grpc.{CallOptions, ManagedChannelBuilder}
import scalapb.zio_grpc.ZChannel
import zio.{RLayer, ZIO}

object GrpcClients {
  lazy val layer: RLayer[GrpcClientsConfig, UsersServiceClient.ZService[Any, Ctx]] = {
    // TODO this is for simple test, ideally should be only one channel to localhost and istio should resolve target by grpc
    val channel = ManagedChannelBuilder.forAddress(s"users", 10000).usePlaintext.asInstanceOf[ManagedChannelBuilder[_]]
    val zManagedChannel = for {
      builder <- ZIO.succeed(channel)
      ch      <- ZIO.succeed(new ZChannel(builder.build(), Nil))
    } yield ch
    UsersServiceClient.live[Any, Ctx](
      zManagedChannel,
      zio.ZIO.succeed(CallOptions.DEFAULT),
      for {
        ctx  <- ZIO.service[Ctx]
        meta <- scalapb.zio_grpc.SafeMetadata.make
        _    <- meta.put(X_TENANT_ID_KEY, ctx.tenantId.value.toString)
        _    <- meta.put(X_USER_ID_KEY, ctx.userId.value.toString)
        _    <- meta.put(X_REQUEST_ID_KEY, ctx.requestId.value)
      } yield meta
    )
  }

}
