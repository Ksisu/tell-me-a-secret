name := "tell-me-a-secret"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"            % "10.1.1",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
  "com.typesafe.akka" %% "akka-stream"          % "2.5.12",
  "ch.megard"         %% "akka-http-cors"       % "0.3.0",
  "io.spray"          %% "spray-json"           % "1.3.3",
  "com.typesafe.akka" %% "akka-http-testkit"    % "10.1.1" % Test,
  "com.typesafe.akka" %% "akka-stream-testkit"  % "2.5.12" % Test
)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerExposedPorts := Seq(80)