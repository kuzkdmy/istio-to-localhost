package com.test.web.api.route

import cats.implicits.catsSyntaxEitherId
import com.test.common.types.{Ctx, UserId}
import com.test.web.api.domain.user.{ApiUser, ApiUserList}
import com.test.web.api.endpoint.ApiUsersEndpoints.{ApiGetErr, ApiListUsers}
import com.test.web.service.UserService
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.ZServerEndpoint
import zio._
import zio.macros.accessible

object UsersRoute {
  import com.test.web.api.endpoint.ApiUsersEndpoints._
  type Env = UsersRouteService
  def endpoints[R <: Env]: List[ZServerEndpoint[R, ZioStreams]] = List(
    getE.serverLogicSecured((c, i) => UsersRouteService.getUser(i)(c)): ZServerEndpoint[R, ZioStreams],
    listE.serverLogicSecured((c, i) => UsersRouteService.listUsers(i)(c)): ZServerEndpoint[R, ZioStreams]
  )
}
@accessible
trait UsersRouteService {
  def getUser(input: UserId)(implicit ctx: Ctx): Task[Either[ApiGetErr, ApiUser]]
  def listUsers(input: ApiListUsers)(implicit ctx: Ctx): Task[Either[Unit, ApiUserList]]
}

case class UsersRouteLive(usersSvc: UserService) extends UsersRouteService {
  override def getUser(input: UserId)(implicit ctx: Ctx): Task[Either[ApiGetErr, ApiUser]] = {
    for {
      uOpt <- usersSvc.getUser(input)
    } yield uOpt match {
      case Some(u) => Right(u)
      case None    => Left(ApiGetErr.NotFound(s"user not found, user id $input"))
    }
  }

  override def listUsers(input: ApiListUsers)(implicit ctx: Ctx): Task[Either[Unit, ApiUserList]] = {
    for {
      res <- usersSvc.listUsers(input)
    } yield res.asRight
  }
}
object UsersRouteLive {
  val layer = ZLayer.fromFunction(UsersRouteLive.apply _)
}
