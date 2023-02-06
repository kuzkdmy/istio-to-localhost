package com.test.prototype.monolith.config

import com.test.common.grpc.GrpcServerConfig
import com.test.common.http.HttpServerConf
import com.test.web.config.AppConfig.ServicesConf
import zio.ZLayer
import zio.ZLayer.succeed

import scala.util.control.NoStackTrace

case class AppConfig(
    httpServer: HttpServerConf,
    grpcServer: GrpcServerConfig,
    services: ServicesConf
)

object AppConfig {
  import pureconfig._
  import pureconfig.generic.auto._

  lazy val live = ConfigSource.default.at("app").load[AppConfig] match {
    case Left(err)   => ZLayer.fail(new IllegalArgumentException(err.prettyPrint()) with NoStackTrace)
    case Right(conf) => succeed(conf.httpServer) ++ succeed(conf.grpcServer) ++ succeed(conf.services)
  }
}
