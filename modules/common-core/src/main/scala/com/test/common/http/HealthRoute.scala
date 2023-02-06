package com.test.common.http

import io.circe.generic.auto._
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.PublicEndpoint
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody
import sttp.tapir.ztapir._
import zio._

object HealthRoute {
  case class Health(status: String)

  def endpoints[R](readiness: ZIO[R, Unit, Health], liveness: ZIO[R, Unit, Health]): List[ZServerEndpoint[R, ZioStreams]] = {
    List(
      readinessE.zServerLogic[R](_ => readiness),
      livenessE.zServerLogic[R](_ => liveness)
    )
  }

  private val readinessE: PublicEndpoint[Unit, Unit, Health, ZioStreams] = endpoint.get
    .in("health" / "readiness")
    .out(jsonBody[Health])

  private val livenessE: PublicEndpoint[Unit, Unit, Health, ZioStreams] = endpoint.get
    .in("health" / "liveness")
    .out(jsonBody[Health])

}
