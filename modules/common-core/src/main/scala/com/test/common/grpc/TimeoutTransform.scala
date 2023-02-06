package com.test.common.grpc

import io.grpc.Status
import scalapb.zio_grpc.ZTransform
import zio.ZIO
import zio.stream.ZStream

import scala.concurrent.duration.Duration

class TimeoutTransform[R, C](timeout: zio.Duration) extends ZTransform[R, Status, R with C] {
  override def effect[A](io: ZIO[R, Status, A]): ZIO[R with C, Status, A] =
    io.timeoutFail(Status.DEADLINE_EXCEEDED)(timeout)
  override def stream[A](io: ZStream[R, Status, A]): ZStream[R with C, Status, A] = io
}

object TimeoutTransform {
  def apply[R, C](implicit c: GrpcServerConfig): TimeoutTransform[R, C] = apply[R, C](c.serverTimeout)
  def apply[R, C](timeout: Duration): TimeoutTransform[R, C]            = apply[R, C](zio.Duration.fromScala(timeout))
  def apply[R, C](timeout: zio.Duration): TimeoutTransform[R, C]        = new TimeoutTransform[R, C](timeout)
}
