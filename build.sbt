import ProjectOps.CommonOps

lazy val root = (project in file("."))
  .settings(
    name := "istio-integration",
    publishArtifact := false,
    publish / skip := true
  )
  .aggregate(`common`, `transport-protocol`, `web`, `users`, `insurance`)

lazy val `common` = (project in file("common"))
  .dependsOn(`transport-protocol`)
  .settings(
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-circe-yaml"      % "1.0.0-M9",
      "com.softwaremill.sttp.tapir"   %% "tapir-zio-http-server"         % "1.2.2",
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"              % "1.2.2",
      "com.softwaremill.sttp.tapir"   %% "tapir-derevo"                  % "1.2.2",
      "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % "3.8.3",
      "dev.zio"                       %% "zio-macros"                    % "2.0.4",
      "ch.qos.logback"                 % "logback-classic"               % "1.4.5",
      "com.github.pureconfig"         %% "pureconfig"                    % "0.17.2",
      "dev.zio"                       %% "zio-logging-slf4j"             % "2.1.4"
    )
  )

lazy val `web`       = (project in file("web")).dependsOn(`common`).dockerSettings()
lazy val `users`     = (project in file("users")).dependsOn(`common`).dockerSettings()
lazy val `insurance` = (project in file("insurance")).dependsOn(`common`).dockerSettings()

lazy val `transport-protocol` = (project in file("transport-protocol")).settings(
  Compile / PB.targets := Seq(
    scalapb.gen(grpc = true)          -> (Compile / sourceManaged).value / "scalapb",
    scalapb.zio_grpc.ZioCodeGenerator -> (Compile / sourceManaged).value / "scalapb"
  ),
  libraryDependencies ++= Seq(
    "io.grpc"               % "grpc-netty"           % "1.51.0",
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime"      % scalapb.compiler.Version.scalapbVersion % "protobuf"
  )
)
