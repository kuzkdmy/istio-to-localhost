package com.test.web.api
import com.test.common.types.{TenantId, UserId, XRequestId}
import derevo.derive
import sttp.tapir.codec.newtype._
import sttp.tapir.derevo.schema

@derive(schema) case class AuthCtx(
    tId: Option[TenantId],
    uId: Option[UserId],
    requestId: Option[XRequestId]
)
