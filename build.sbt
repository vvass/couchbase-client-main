import sbt.Keys._
import sbt.Level

lazy val commonSettings = Seq(
  organization  := "44lab5",
  version       := "1.3",
  scalaVersion  := "2.11.8"
)

lazy val root = (project in file(".")).
  enablePlugins(PlayScala, UniversalPlugin, DockerPlugin, PlayAkkaHttpServer).
  disablePlugins(PlayNettyServer).
  settings(commonSettings: _*).
  settings(
    name      := "couchbase-client-main",
    logLevel  := Level.Info
  )


resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= {
  val iospray = "1.3.3"

  Seq(
    // -- main library
    "com.couchbase.client".%("java-client")                         % "2.2.7",
    // -- metrics --
    "io.dropwizard.metrics".%("metrics-core")                       % "3.1.2",
    // -- package plugin --
    "javax.servlet".%("javax.servlet-api")                          % "3.0.1"         % "provided",
    // -- docker --
    "com.spotify".%("docker-client")                                % "5.0.2",
    // -- twitter --
    "org.twitter4j".%("twitter4j-core")                             % "4.0.5"
    
  )
}

//Settings for docker publish plugin
dockerExposedPorts := Seq(8135, 8135)

parallelExecution in run := true
fork in run := false

//This is where we set the port that we will use for the application
PlayKeys.devSettings := Seq("play.server.http.port" -> "8135")


