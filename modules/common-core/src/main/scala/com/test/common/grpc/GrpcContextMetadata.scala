package com.test.common.grpc

import com.test.common.domain.ContextHeaders.{X_REQUEST_ID, X_TENANT_ID, X_USER_ID}
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER

object GrpcContextMetadata {
  val X_TENANT_ID_KEY: Metadata.Key[String]  = Metadata.Key.of(X_TENANT_ID, ASCII_STRING_MARSHALLER)
  val X_USER_ID_KEY: Metadata.Key[String]    = Metadata.Key.of(X_USER_ID, ASCII_STRING_MARSHALLER)
  val X_REQUEST_ID_KEY: Metadata.Key[String] = Metadata.Key.of(X_REQUEST_ID, ASCII_STRING_MARSHALLER)
}
