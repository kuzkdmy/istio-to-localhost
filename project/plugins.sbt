addSbtPlugin("com.dwijnand"     % "sbt-dynver"             % "4.1.1")
addSbtPlugin("com.thesamet"     % "sbt-protoc"             % "1.0.6")
addSbtPlugin("com.github.sbt"   % "sbt-native-packager"    % "1.9.13")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph"   % "0.10.0-RC1")
addSbtPlugin("org.jmotor.sbt"   % "sbt-dependency-updates" % "1.2.7")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"           % "2.5.0")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.0-test8"
libraryDependencies += "com.thesamet.scalapb"          %% "compilerplugin"   % "0.11.12"
libraryDependencies += "io.github.scalapb-json"        %% "protoc-lint"      % "0.6.0"
