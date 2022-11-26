package com.test.web.api
package endpoint

import com.test.web.api.SecureEndpoint
import com.test.web.api.domain.insurrance.ApiInsuranceList
import io.circe.generic.auto._
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

object ApiInsuranceEndpoints {
  private val basePath: EndpointInput[Unit] = "api" / "v1" / "insurance"

  val listE: SecureEndpoint[ApiListInsurances, Unit, ApiInsuranceList] = endpoint.get
    .in(basePath)
    .securedEndpoint()
    .in(query[List[String]]("ids"))
    .mapInTo[ApiListInsurances]
    .out(jsonBody[ApiInsuranceList])

  case class ApiListInsurances(ids: List[String])
}
