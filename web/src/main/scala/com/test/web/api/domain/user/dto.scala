package com.test.web.api.domain.user

case class ApiUser(
    id: Long,
    email: String,
    insurance: Option[String]
)

case class ApiUserList(
    data: List[ApiUser]
)
