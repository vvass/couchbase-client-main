import sbt.Keys._
import sbt.Level

lazy val commonSettings = Seq(
  organization  := "44lab5",
  version       := "2.0",
  scalaVersion  := "2.11.8"
)

lazy val root = (project in file(".")).
  enablePlugins(PlayScala, DockerPlugin, PlayAkkaHttpServer).
  disablePlugins(PlayNettyServer).
  settings(commonSettings: _*).
  settings(
    name      := "couchbase-client-main",
    logLevel  := Level.Info
  )


resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
  "MVN repository" at "http://mvnrepository.com/artifact/com.spotify/docker-client"
)

libraryDependencies ++= {
  val iospray = "1.3.3"

  Seq(
    // -- main library
    "com.couchbase.client".%("java-client")                         % "2.2.7",
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
dockerExposedUdpPorts := Seq(8136, 8136)
dockerUpdateLatest := true

parallelExecution in run := true
fork in run := false

//This is where we set the port that we will use for the application
PlayKeys.devSettings := Seq("play.server.http.port" -> "8135")


