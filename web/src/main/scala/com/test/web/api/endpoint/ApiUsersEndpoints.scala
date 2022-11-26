package com.test.web.api
package endpoint

import com.test.common.types.UserId
import com.test.web.api.SecureEndpoint
import com.test.web.api.domain.user.{ApiUser, ApiUserList}
import derevo.derive
import io.circe.generic.auto._
import sttp.model.StatusCode
import sttp.tapir
import sttp.tapir._
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

object ApiUsersEndpoints {
  private val basePath: EndpointInput[Unit] = "api" / "v1" / "user"

  val getE: SecureEndpoint[UserId, ApiGetErr, ApiUser] = endpoint.get
    .in(basePath / path[UserId]("uId"))
    .securedEndpoint()
    .out(jsonBody[ApiUser])
    .errorOut(
      tapir.oneOf[ApiGetErr](
        oneOfVariantFromMatchType(StatusCode.NotFound, jsonBody[ApiGetErr.NotFound])
      )
    )

  val listE: SecureEndpoint[ApiListUsers, Unit, ApiUserList] = endpoint.get
    .in(basePath)
    .securedEndpoint()
    .in(query[List[Long]]("ids"))
    .in(query[List[String]]("emails"))
    .in(query[List[String]]("insuranceIds"))
    .mapInTo[ApiListUsers]
    .out(jsonBody[ApiUserList])

  sealed trait ApiGetErr

  object ApiGetErr {
    @derive(schema) case class NotFound(message: String) extends ApiGetErr
  }

  case class ApiListUsers(ids: List[Long], emails: List[String], insuranceIds: List[String])
}
