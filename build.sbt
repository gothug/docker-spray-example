import sbtassembly.Plugin.AssemblyKeys._

name := "docker-spray-example"

version := "1.0"

scalaVersion := "2.11.2"

resolvers += "spray repo" at "http://repo.spray.io"

mainClass := Some("com.softwaremill.example.DockedServer")

val sprayVersion = "1.3.1-20140423"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-json" % "1.2.6",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.jsoup" % "jsoup" % "1.8.1",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.15",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0" % "test",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.44.0",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "2.44.0"
)

assemblySettings