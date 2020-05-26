import org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings

lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
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
      Seq(
        "net.debasishg"        %% "redisclient"             % "3.20",
        "org.typelevel"        %% "cats-core"               % "2.1.1",
        "com.typesafe.akka"    %% "akka-http"               % "10.1.11",
        "com.typesafe.akka"    %% "akka-http-spray-json"    % "10.1.11",
        "com.typesafe.akka"    %% "akka-http-testkit"       % "10.1.11" % "it,test",
        "com.typesafe.akka"    %% "akka-actor"              % "2.6.4",
        "com.typesafe.akka"    %% "akka-stream"             % "2.6.4",
        "com.typesafe.akka"    %% "akka-slf4j"              % "2.6.4",
        "com.typesafe.akka"    %% "akka-testkit"            % "2.6.4" % "it,test",
        "ch.qos.logback"       % "logback-classic"          % "1.2.3",
        "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
        "org.slf4j"            % "jul-to-slf4j"             % "1.7.30",
        "ch.megard"            %% "akka-http-cors"          % "1.0.0",
        "org.scalatest"        %% "scalatest"               % "3.1.1" % "it,test"
      )
    }
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt it:scalafmt")
addCommandAlias("testAll", "test it:test")

enablePlugins(JavaAppPackaging)
enablePlugins(BuildInfoPlugin)
cancelable in Global := true

lazy val buildTime                       = java.time.ZonedDateTime.now(java.time.ZoneOffset.UTC)
lazy val builtAtMillis: SettingKey[Long] = SettingKey[Long]("builtAtMillis", "time of build")
ThisBuild / builtAtMillis := buildTime.toInstant.toEpochMilli
lazy val builtAtString: SettingKey[String] = SettingKey[String]("builtAtString", "time of build")
ThisBuild / builtAtString := buildTime.toString

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    BuildInfoKey.action("commitHash") {
      git.gitHeadCommit.value
    },
    builtAtString,
    builtAtMillis
  ),
  buildInfoPackage := "com.ksisu.secret"
)
