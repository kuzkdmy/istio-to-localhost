package com.test.web.converter

import com.test.transport_protocol.users.{ListUsersRequestProto, UserProto}
import com.test.web.api.domain.user.ApiUser
import com.test.web.api.endpoint.ApiUsersEndpoints.ApiListUsers

object ApiUserConverter {
  def toApiUser(u: UserProto): ApiUser = {
    ApiUser(
      id        = u.id,
      email     = u.email,
      insurance = u.insuranceId
    )
  }

  def toListUsersRequestProto(f: ApiListUsers): ListUsersRequestProto = {
    ListUsersRequestProto(
      ids          = f.ids,
      emails       = f.emails,
      insuranceIds = f.insuranceIds
    )
  }
}
