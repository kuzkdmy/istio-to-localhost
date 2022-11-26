addSbtPlugin("com.dwijnand"     % "sbt-dynver"          % "4.1.1")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")
addSbtPlugin("org.scalameta"    % "sbt-scalafmt"        % "2.4.3")
addSbtPlugin("com.thesamet"     % "sbt-protoc"          % "1.0.6")

libraryDependencies += "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % "0.6.0-test4"
libraryDependencies += "com.thesamet.scalapb"          %% "compilerplugin"   % "0.11.12"
libraryDependencies += "io.github.scalapb-json"        %% "protoc-lint"      % "0.6.0"
