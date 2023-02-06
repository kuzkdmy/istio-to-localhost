package com.test.web.api.route.integration

import cats.implicits.catsSyntaxEitherId
import com.test.common.domain.types.Ctx
import com.test.web.api.domain.integrations.connection.{ApiConnection, ApiConnectionList}
import com.test.web.api.domain.integrations.types.ConnectionId
import com.test.web.api.endpoint.integrations.ApiConnectionsEndpoints.{ApiGetErr, ApiListConnections, getE, listE}
import com.test.web.service.integrations.ConnectionService
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.ZServerEndpoint
import zio._
import zio.macros.accessible

object ConnectionsRoute {
  def endpoints[R <: ConnectionsRouteService]: List[ZServerEndpoint[R, ZioStreams]] = List(
    getE.serverLogicSecured((c, i) => ConnectionsRouteService.getConnection(i)(c)): ZServerEndpoint[R, ZioStreams],
    listE.serverLogicSecured((c, i) => ConnectionsRouteService.listConnections(i)(c)): ZServerEndpoint[R, ZioStreams]
  )
}
@accessible
trait ConnectionsRouteService {
  def getConnection(input: ConnectionId)(implicit ctx: Ctx): Task[Either[ApiGetErr, ApiConnection]]
  def listConnections(input: ApiListConnections)(implicit ctx: Ctx): Task[Either[Unit, ApiConnectionList]]
}

case class ConnectionsRouteLive(connectionsSvc: ConnectionService) extends ConnectionsRouteService {
  override def getConnection(input: ConnectionId)(implicit ctx: Ctx): Task[Either[ApiGetErr, ApiConnection]] = {
    for {
      res <- connectionsSvc.listConnections(ApiListConnections(ids = List(input)))
    } yield res.data match {
      case Nil    => Left(ApiGetErr.NotFound(s"Connection not found, Connection id $input"))
      case x :: _ => Right(x)
    }
  }

  override def listConnections(input: ApiListConnections)(implicit ctx: Ctx): Task[Either[Unit, ApiConnectionList]] = {
    connectionsSvc.listConnections(input).map(_.asRight)
  }
}
object ConnectionsRouteLive {
  val layer = ZLayer.fromFunction(ConnectionsRouteLive.apply _)
}
