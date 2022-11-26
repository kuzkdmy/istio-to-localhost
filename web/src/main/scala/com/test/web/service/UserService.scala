package com.test.web.service

import com.test.common.types.{Ctx, UserId}
import com.test.transport_protocol.users.ListUsersRequestProto
import com.test.transport_protocol.users.ZioUsers.UsersServiceClient
import com.test.web.api.domain.user.{ApiUser, ApiUserList}
import com.test.web.api.endpoint.ApiUsersEndpoints.ApiListUsers
import com.test.web.converter.ApiUserConverter
import zio.{Task, ZIO, ZLayer}

trait UserService {
  def getUser(id: UserId)(implicit ctx: Ctx): Task[Option[ApiUser]]
  def listUsers(filter: ApiListUsers)(implicit ctx: Ctx): Task[ApiUserList]
}

case class UserServiceLive(svc: UsersServiceClient.ZService[Any, Ctx]) extends UserService {
  override def getUser(id: UserId)(implicit ctx: Ctx): Task[Option[ApiUser]] = {
    svc
      .listUsers(ListUsersRequestProto(ids = Seq(id.value)))
      .map(_.data.headOption.map(ApiUserConverter.toApiUser))
      .provide(ZLayer.succeed(ctx))
      .catchAll(s => ZIO.fail(s.asException))
  }

  override def listUsers(filter: ApiListUsers)(implicit ctx: Ctx): Task[ApiUserList] = {
    svc
      .listUsers(ApiUserConverter.toListUsersRequestProto(filter))
      .map(_.data.map(ApiUserConverter.toApiUser).toList)
      .map(ApiUserList.apply)
      .provide(ZLayer.succeed(ctx))
      .catchAll(s => ZIO.fail(s.asException))
  }
}

object UserServiceLive {
  lazy val layer = ZLayer.fromFunction(UserServiceLive.apply _)
}
