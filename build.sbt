name := """play-java-intro"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  "com.novus" %% "salat" % "1.9.9",
  javaJpa,
  specs2 % Test
  
)

routesGenerator := InjectedRoutesGenerator
