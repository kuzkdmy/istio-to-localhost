package com.test.web.api.domain.insurrance

case class ApiInsurance(
    id: String
)

case class ApiInsuranceList(
    data: List[ApiInsurance]
)
