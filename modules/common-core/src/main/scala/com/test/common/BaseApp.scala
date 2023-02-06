package com.test.common

import com.test.common.domain.types.Ctx
import com.test.common.grpc.{GrpcServer, GrpcServices}
import com.test.common.http.HealthRoute.Health
import com.test.common.http.{HealthRoute, HttpServerConf}
import com.test.common.log.log
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.ztapir.ZServerEndpoint
import zio.http.netty.server.NettyDriver
import zio.http.{Server, ServerConfig}
import zio.{RIO, Task, URIO, ZIO, ZLayer}

import java.net.InetSocketAddress
import scala.util.control.NoStackTrace

object BaseApp {
  implicit private val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  def run[R1, R2, R3](
      appRoutesF: Task[List[ZServerEndpoint[R1, ZioStreams]]] = ZIO.succeed(List.empty),
      grpcServicesF: Option[RIO[R2, GrpcServices[R2]]]        = None,
      httpConfF: URIO[HttpServerConf, HttpServerConf]         = ZIO.succeed(HttpServerConf()),
      applicationForkProgram: Option[RIO[R3, Unit]]           = None,
      readinessCheck: ZIO[R1, Unit, Health]                   = ZIO.succeed(Health("up")),
      livenessCheck: ZIO[R1, Unit, Health]                    = ZIO.succeed(Health("up"))
  ): RIO[R1 with R3 with R2 with HttpServerConf, Unit] = {
    val program = for {
      implicit0(ctx: Ctx) <- Ctx.systemCtx
      _                   <- log.info("starting application")
      appRoutes           <- appRoutesF
      httpConf            <- httpConfF
      _ <- Server
             .serve(
               ZioHttpInterpreter().toApp(appRoutes ++ HealthRoute.endpoints(readinessCheck, livenessCheck)),
               Some((th: Throwable) => log.error("Error on http server", th))
             )
             .provideSomeLayer[R1](
               ZLayer.succeed(ServerConfig.default.copy(address = new InetSocketAddress(httpConf.port))) >>>
                 NettyDriver.default >>>
                 Server.base
             )
             .fork
      _ <- log.info(s"zio-http server started on port ${httpConf.port}")
      _ <- ZIO.whenCase(grpcServicesF) { case Some(svc) =>
             for {
               config <- svc
               server <- GrpcServer.buildGrpcServer(config)
               _      <- server.start.fork
               _      <- log.info(s"zio-grpc server started on port ${config.port}")
             } yield ()
           }
      _ <- ZIO.whenCase(applicationForkProgram) { case Some(mainProgram) =>
             mainProgram *> log.info(s"application fork program started")
           }
      _ <- log.info(s"base app, application started")
    } yield ()
    ZIO.scoped[R1 with R2 with R3 with HttpServerConf] {
      program.orDieWith(err => new RuntimeException(s"Failed to start application, $err") with NoStackTrace) *> ZIO.never
    }
  }
}
