import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.docker.DockerPlugin
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.{Docker, dockerBaseImage, dockerExposedPorts}
import sbt.Keys._
import sbt.{Project, _}

object ProjectOps {

  implicit class CommonOps(project: Project) {
    def dockerSettings(): Project = {
      project
        .enablePlugins(JavaAppPackaging, DockerPlugin)
        .settings(
          dockerBaseImage := sys.props.getOrElse("DOCKER_BASE_IMAGE", "openjdk:11"),
          dockerExposedPorts ++= Seq(9000),
          Docker / packageName := sys.props.getOrElse("DOCKER_IMAGE", s"test-istio/${name.value}"),
          Docker / version := sys.props.getOrElse("DOCKER_TAG", "latest")
        )
    }
  }

}
