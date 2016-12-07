
val additionalScalacOptions = Seq("-deprecation", "-unchecked", "-feature")

val projectSettings = Seq(
  name := "wordcount",
  description := "Wordcount with a Playframework body parser and akka-streams",
  version := "1.0",
  scalaVersion := "2.11.8",
  organization := "Sebastian Bach",
  scalacOptions ++= additionalScalacOptions,
  routesGenerator := InjectedRoutesGenerator
)

val dependencies = Seq(
  "joda-time" % "joda-time" % "2.9.3",
  "com.eaio.uuid" % "uuid" % "3.4",
  "org.scalaz" %% "scalaz-core" % "7.2.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

lazy val root = (project in file("."))
  .settings(projectSettings: _*)
  .settings(libraryDependencies ++= dependencies)
  .enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator
