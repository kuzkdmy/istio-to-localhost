package com.test.web

import com.test.common.{AppHttpServerConf, BaseApp}
import com.test.common.types.Ctx
import com.test.transport_protocol.users.ZioUsers.UsersServiceClient
import com.test.web.api.route.UsersRouteLive
import com.test.web.config.AppConfig
import com.test.web.service.UserServiceLive
import com.test.web.service.grpc.GrpcClients
import zio.{Scope, ZIO, ZIOAppArgs, ZIOAppDefault}

object WebMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    type T =  AppHttpServerConf with UsersServiceClient.ZService[Any, Ctx]
    val p: ZIO[T, Throwable, Unit] = for {
      _ <- BaseApp.run[T,T,T]()
      _ <- ZIO.service[UsersServiceClient.ZService[Any, Ctx]]
    } yield ()

    (p.provide(appLayer) *> ZIO.never).exitCode
  }
  val appLayer = AppConfig.live >+>
    GrpcClients.layer >+>
    UserServiceLive.layer >+>
    UsersRouteLive.layer

}
