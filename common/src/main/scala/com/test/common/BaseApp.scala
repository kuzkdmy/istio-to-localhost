package com.test.common

import com.test.common.HealthRoute.Health
import com.test.common.types.Ctx
import io.grpc.ServerBuilder
import io.grpc.protobuf.services.ProtoReflectionService
import scalapb.zio_grpc.{ServerLayer, ServiceList}
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zhttp.service.server.ServerChannelFactory
import zhttp.service.{EventLoopGroup, Server => HttpServer}
import zio.{RIO, Scope, Task, URIO, ZIO}

object BaseApp {
  implicit private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  def run[R1, R2, R3](
      appRoutesF: Task[List[ZServerEndpoint[R1, ZioStreams]]] = ZIO.succeed(List.empty),
      grpcServicesF: Option[RIO[R2, GrpcServices[R3]]]        = None,
      httpConfF: URIO[AppHttpServerConf, AppHttpServerConf]   = ZIO.succeed(AppHttpServerConf.default),
      applicationForkProgram: Option[Task[Unit]]              = None,
      readinessCheck: ZIO[R1, Unit, Health]                   = ZIO.succeed(Health("up")),
      livenessCheck: ZIO[R1, Unit, Health]                    = ZIO.succeed(Health("up"))
  ) = {
    for {
      implicit0(ctx: Ctx) <- Ctx.systemCtx
      appRoutes           <- appRoutesF
      httpConf            <- httpConfF
      _ <- {
        val server = HttpServer.bind("0.0.0.0", httpConf.port) ++ HttpServer.app {
          ZioHttpInterpreter().toHttp(appRoutes ++ HealthRoute.endpoints(readinessCheck, livenessCheck))
        }
        server.start.provideSomeLayer[R1](Scope.default >+> EventLoopGroup.auto() >+> ServerChannelFactory.auto).fork
      }
      _ <- log.info(s"zio-http server started on port ${httpConf.port}")
      _ <- ZIO.whenCase(grpcServicesF) { case Some(svc) =>
             for {
               c <- svc
               _ <- (ServerLayer
                      .fromServiceList(
                        ServerBuilder
                          .forPort(c.config.port)
                          .addService(ProtoReflectionService.newInstance())
                          .asInstanceOf[ServerBuilder[_]],
                        c.services
                      )
                      .build *> ZIO.never).provideSomeLayer[R3](Scope.default).fork
               _ <- log.info(s"zio-grpc server started on port ${c.config.port}")
             } yield ()
           }
      _ <- ZIO.whenCase(applicationForkProgram) { case Some(mainProgram) =>
             mainProgram *> log.info(s"application fork program started")
           }
      _ <- log.info(s"base app, application started")
    } yield ()
  }
}

case class GrpcServices[-RR](
    services: ServiceList[RR],
    config: AppGrpcServerConf
)

case class AppGrpcServerConf(port: Int)
object AppGrpcServerConf {
  lazy val default: AppGrpcServerConf = AppGrpcServerConf(10000)
}
case class AppHttpServerConf(port: Int)
object AppHttpServerConf {
  lazy val default: AppHttpServerConf = AppHttpServerConf(9000)
}
