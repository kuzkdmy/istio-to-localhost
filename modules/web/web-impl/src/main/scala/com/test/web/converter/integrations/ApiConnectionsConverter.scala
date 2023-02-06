package com.test.web.converter.integrations

import com.test.common.domain.types.Ctx
import com.test.transport_protocol.connector_hub_connections.{ConnectionProto, ListConnectionsRequestProto, ListConnectionsResponseProto}
import com.test.web.api.domain.integrations.connection.{ApiConnection, ApiConnectionList}
import com.test.web.api.domain.integrations.types.{ConnectionId, DestinationId, IntegrationId}
import com.test.web.api.endpoint.integrations.ApiConnectionsEndpoints.ApiListConnections

object ApiConnectionsConverter {

  def toApiConnectionList(p: ListConnectionsResponseProto): ApiConnectionList = {
    ApiConnectionList(
      data = p.data.map(toApiConnection).toList
    )
  }
  def toApiConnection(e: ConnectionProto): ApiConnection = {
    ApiConnection(
      id            = ConnectionId(e.id),
      destinationId = DestinationId(e.destinationId),
      integrationId = IntegrationId(e.integrationId)
    )
  }

  def toListRequestProto(f: ApiListConnections)(implicit ctx: Ctx): ListConnectionsRequestProto = {
    ListConnectionsRequestProto(
      tenantId      = ctx.tenantId.value,
      integrationId = f.iIds.map(_.value),
      destinationId = f.dIds.map(_.value),
      ids           = f.ids.map(_.value)
    )
  }
}
