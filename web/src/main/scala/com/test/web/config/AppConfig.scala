package com.test.web.config

import com.test.common.AppHttpServerConf
import com.test.common.grpc.GrpcClientsConfig
import zio.ZLayer
import zio.ZLayer.succeed

import scala.util.control.NoStackTrace

case class AppConfig(
    httpServer: AppHttpServerConf,
    grpcClients: GrpcClientsConfig
)

object AppConfig {
  import pureconfig._
  import pureconfig.generic.auto._

  val live = ConfigSource.default.at("app").load[AppConfig] match {
    case Left(err)   => ZLayer.fail(new IllegalArgumentException(err.prettyPrint()) with NoStackTrace)
    case Right(conf) => succeed(conf.httpServer) ++ succeed(conf.grpcClients)
  }
}
