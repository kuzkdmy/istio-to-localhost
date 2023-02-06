import ProjectOps.CommonOps

lazy val root = (project in file("."))
  .settings(
    name := "istio-integration",
    publishArtifact := false,
    publish / skip := true
  )
  .aggregate(
    `common-core`,
    `transport-protocol`,
    `web-api`,
    `web-impl`,
    `connector_hub_metadata`,
    `connector_hub_connections`,
    `prototype_monolith`
  )

lazy val `common-core` = (project in file("modules/common-core"))
  .dependsOn(`transport-protocol`)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"      % "1.0.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-zio-http-server"         % "1.2.7",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "1.2.7",
      "com.softwaremill.sttp.tapir"   %% "tapir-derevo"                  % "1.2.7",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.8.10",
      "dev.zio"                       %% "zio-macros"                    % "2.0.6",
      "ch.qos.logback"                 % "logback-classic"               % "1.4.5",
      "com.github.pureconfig"         %% "pureconfig"                    % "0.17.2",
      "dev.zio"                       %% "zio-logging-slf4j"             % "2.1.8"
    )
  )

lazy val `web-api`                   = (project in file("modules/web/web-api")).dependsOn(`common-core`).dockerSettings()
lazy val `web-impl`                  = (project in file("modules/web/web-impl")).dependsOn(`web-api`).dockerSettings()
lazy val `connector_hub_metadata`    = (project in file("modules/connector_hub/metadata")).dependsOn(`common-core`).dockerSettings()
lazy val `connector_hub_connections` = (project in file("modules/connector_hub/connections")).dependsOn(`common-core`).dockerSettings()
lazy val `prototype_monolith` = (project in file("modules/prototype_monolith"))
  .dependsOn(
    `web-impl`,
    `connector_hub_metadata`,
    `connector_hub_connections`
  )
  .dockerSettings()

lazy val `transport-protocol` = (project in file("modules/transport-protocol")).settings(
  Compile / PB.targets := Seq(
    scalapb.gen(grpc = true)          -> (Compile / sourceManaged).value / "scalapb",
    scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "scalapb"
  ),
  libraryDependencies ++= Seq(
    "io.grpc"               % "grpc-netty"           % "1.51.3",
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime"      % scalapb.compiler.Version.scalapbVersion % "protobuf"
  )
)
