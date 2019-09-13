import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

lazy val commonSettings = Seq(
  scalaVersion := "2.13.0",
  organization := "com.ksisu",
  scalafmtOnCompile := true,
  version := "1.0.0"
)

lazy val ItTest         = config("it") extend Test
lazy val itTestSettings = Defaults.itSettings ++ scalafmtConfigSettings

lazy val root = project
  .in(file("."))
  .aggregate(core)

lazy val core = (project in file("."))
  .configs(ItTest)
  .settings(inConfig(ItTest)(itTestSettings): _*)
  .settings(commonSettings: _*)
  .settings(buildInfoSettings: _*)
  .settings(
    name := "tell-me-a-secret",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-unchecked",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Ywarn-dead-code",
      "-Xlint"
    ),
    libraryDependencies ++= {
      val akkaHttpV = "10.1.9"
      val akkaV     = "2.5.25"
      Seq(
        "net.debasishg"        %% "redisclient"             % "3.10",
        "org.typelevel"        %% "cats-core"               % "2.0.0",
        "com.typesafe.akka"    %% "akka-http"               % akkaHttpV,
        "com.typesafe.akka"    %% "akka-http-spray-json"    % akkaHttpV,
        "com.typesafe.akka"    %% "akka-http-testkit"       % akkaHttpV % "it,test",
        "com.typesafe.akka"    %% "akka-actor"              % akkaV,
        "com.typesafe.akka"    %% "akka-stream"             % akkaV,
        "com.typesafe.akka"    %% "akka-slf4j"              % akkaV,
        "com.typesafe.akka"    %% "akka-testkit"            % akkaV % "it,test",
        "ch.qos.logback"       % "logback-classic"          % "1.2.3",
        "net.logstash.logback" % "logstash-logback-encoder" % "6.2",
        "org.slf4j"            % "jul-to-slf4j"             % "1.7.28",
        "ch.megard"            %% "akka-http-cors"          % "0.4.1",
        "org.scalatest"        %% "scalatest"               % "3.0.8" % "it,test"
      )
    }
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt it:scalafmt")
addCommandAlias("testAll", "test it:test")

enablePlugins(JavaAppPackaging)
enablePlugins(BuildInfoPlugin)
cancelable in Global := true

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    BuildInfoKey.action("commitHash") {
      git.gitHeadCommit.value
    }
  ),
  buildInfoOptions := Seq(BuildInfoOption.BuildTime),
  buildInfoPackage := "com.ksisu.secret"
)
