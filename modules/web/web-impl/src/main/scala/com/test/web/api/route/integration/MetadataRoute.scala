package com.test.web.api.route.integration

import com.test.common.domain.types.Ctx
import com.test.web.api.domain.integrations.metadata.{ApiDestinationMetadataList, ApiIntegrationMetadataList}
import com.test.web.api.endpoint.integrations.ApiMetadataEndpoints._
import com.test.web.service.integrations.MetadataService
import sttp.capabilities.zio.ZioStreams
import sttp.tapir.ztapir.ZServerEndpoint
import zio._
import zio.macros.accessible

object MetadataRoute {
  def endpoints[R <: MetadataRouteService]: List[ZServerEndpoint[R, ZioStreams]] = List(
    listDestinationsMetadataE.serverLogicSecured((c, i) => MetadataRouteService.listDestinationsMetadata(i)(c)): ZServerEndpoint[R, ZioStreams],
    listIntegrationsMetadataE.serverLogicSecured((c, i) => MetadataRouteService.listIntegrationsMetadata(i)(c)): ZServerEndpoint[R, ZioStreams]
  )
}
@accessible
trait MetadataRouteService {
  def listDestinationsMetadata(input: ApiListDestinationsMetadata)(implicit ctx: Ctx): Task[Either[Unit, ApiDestinationMetadataList]]
  def listIntegrationsMetadata(input: ApiListIntegrationsMetadata)(implicit ctx: Ctx): Task[Either[Unit, ApiIntegrationMetadataList]]
}

case class MetadataRouteLive(metadataSvc: MetadataService) extends MetadataRouteService {
  override def listDestinationsMetadata(input: ApiListDestinationsMetadata)(implicit ctx: Ctx): Task[Either[Unit, ApiDestinationMetadataList]] = {
    metadataSvc.listDestinations(input).map(r => Right(r))
  }

  override def listIntegrationsMetadata(input: ApiListIntegrationsMetadata)(implicit ctx: Ctx): Task[Either[Unit, ApiIntegrationMetadataList]] = {
    metadataSvc.listIntegrations(input).map(r => Right(r))
  }
}
object MetadataRouteLive {
  val layer = ZLayer.fromFunction(MetadataRouteLive.apply _)
}
