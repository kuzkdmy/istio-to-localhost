package com.test.users.config

import com.test.common.{AppGrpcServerConf, AppHttpServerConf}
import zio.ZLayer

import scala.util.control.NoStackTrace

case class AppConfig(httpServer: AppHttpServerConf, grpcServer: AppGrpcServerConf)

object AppConfig {
  import pureconfig._
  import pureconfig.generic.auto._

  val live = ConfigSource.default.at("app").load[AppConfig] match {
    case Left(err)   => ZLayer.fail(new IllegalArgumentException(err.prettyPrint()) with NoStackTrace)
    case Right(conf) => ZLayer.succeed(conf.httpServer) ++ ZLayer.succeed(conf.grpcServer)
  }
}
