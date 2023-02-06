package com.test.common.grpc

import com.test.common.domain.types.Ctx
import com.test.common.grpc.GrpcContextMetadata.{X_REQUEST_ID_KEY, X_TENANT_ID_KEY, X_USER_ID_KEY}
import io.grpc.netty.NettyChannelBuilder
import io.grpc.{CallOptions, Status}
import scalapb.zio_grpc.{SafeMetadata, ZManagedChannel}
import zio._

import java.util.concurrent.TimeUnit

object GrpcClient {
  def mkClient[B <: scalapb.zio_grpc.CallOptionsMethods[_]](
      url: String,
      managed: (ZManagedChannel, zio.IO[Status, CallOptions], ZIO[Ctx, Status, SafeMetadata]) => ZIO[Scope, Throwable, B]
  ): ZIO[Scope, Throwable, B] = {
    managed(
      ZManagedChannel(builder = NettyChannelBuilder.forTarget(url).usePlaintext(), interceptors = Nil),
      zio.ZIO.succeed(CallOptions.DEFAULT.withDeadlineAfter(60000, TimeUnit.MILLISECONDS)),
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
