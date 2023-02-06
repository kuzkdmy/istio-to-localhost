package com.test.connector_hub.metadata.config

import com.test.common.grpc.GrpcServerConfig
import com.test.common.http.HttpServerConf
import zio.ZLayer

import scala.util.control.NoStackTrace

case class AppConfig(httpServer: HttpServerConf, grpcServer: GrpcServerConfig)

object AppConfig {
  import pureconfig._
  import pureconfig.generic.auto._

  lazy val live = ConfigSource.default.at("app").load[AppConfig] match {
    case Left(err)   => ZLayer.fail(new IllegalArgumentException(err.prettyPrint()) with NoStackTrace)
    case Right(conf) => ZLayer.succeed(conf.httpServer) ++ ZLayer.succeed(conf.grpcServer)
  }
}
