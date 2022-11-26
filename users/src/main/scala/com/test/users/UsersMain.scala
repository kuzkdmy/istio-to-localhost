package com.test.users

import com.test.common.{AppGrpcServerConf, AppHttpServerConf, BaseApp, GrpcServices}
import com.test.transport_protocol.users.ZioUsers.ZUsersService
import com.test.transport_protocol.users.{ListUsersRequestProto, ListUsersResponseProto, UserProto}
import com.test.users.config.AppConfig
import io.grpc.Status
import scalapb.zio_grpc.ServiceList
import zio._

object UsersMain extends ZIOAppDefault {
  override def run: ZIO[Any with ZIOAppArgs with Scope, Any, Any] = {
    (mainProgram.provide(appEnv) *> ZIO.never).exitCode
  }

  val appEnv = AppConfig.live >+> GrpcServerImpl.live

  def mainProgram = {
    val program = for {
      svc      <- ZIO.service[ZUsersService[Any, Any]]
      grpcConf <- ZIO.service[AppGrpcServerConf]
    } yield GrpcServices(ServiceList.add(svc), grpcConf)
    BaseApp.run(
      grpcServicesF = Some(program),
      httpConfF     = ZIO.service[AppHttpServerConf]
    )
  }

  object GrpcServerImpl extends ZUsersService[Any, Any] {
    lazy val live = ZLayer.succeed[ZUsersService[Any, Any]](GrpcServerImpl)
    private val users = List(
      UserProto(id = 1L, email = "1@gmail.com", insuranceId = Some("1")),
      UserProto(id = 2L, email = "2@gmail.com", insuranceId = Some("2")),
      UserProto(id = 3L, email = "3@gmail.com", insuranceId = None)
    )

    override def listUsers(request: ListUsersRequestProto): ZIO[Any, Status, ListUsersResponseProto] = {
      for {
        idsF       <- ZIO.succeed(Some(request.ids.toSet).filter(_.nonEmpty))
        emailsF    <- ZIO.succeed(Some(request.emails.toSet).filter(_.nonEmpty))
        insuranceF <- ZIO.succeed(Some(request.insuranceIds.toSet).filter(_.nonEmpty))
        data <- ZIO.succeed {
                  users
                    .filter(u => idsF.fold(true)(_.contains(u.id)))
                    .filter(u => emailsF.fold(true)(_.contains(u.email)))
                    .filter(u => insuranceF.fold(true)(ids => u.insuranceId.fold(false)(ids.contains)))
                }
      } yield ListUsersResponseProto(data)
    }
  }
}
