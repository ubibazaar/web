name := """ubi2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs
)

libraryDependencies += "com.google.code.gson" % "gson" % "1.7.1"

libraryDependencies += "com.adrianhurt" %% "play-bootstrap3" % "0.4"


