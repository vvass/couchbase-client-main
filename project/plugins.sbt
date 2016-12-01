// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/maven-releases/"
resolvers += "MVN repository" at "http://mvnrepository.com/artifact/com.spotify/docker-client"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play".%("sbt-plugin") % "2.5.3")
//addSbtPlugin("com.spotify".%("docker-client") % "5.0.2")