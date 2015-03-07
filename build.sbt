import sbtassembly.Plugin.AssemblyKeys._
import sbtrelease._
import ReleaseStateTransformations._

name := "docker-spray-example"

version := "1.0"

scalaVersion := "2.11.5"

resolvers += "spray repo" at "http://repo.spray.io"

mainClass := Some("mvgk.httpservice.DockedServer")

val sprayVersion = "1.3.1-20140423"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.2",
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "io.spray" %% "spray-json" % "1.2.6",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.jsoup" % "jsoup" % "1.8.1",
  "net.sourceforge.htmlunit" % "htmlunit" % "2.15",
  "org.seleniumhq.selenium" % "selenium-java" % "2.44.0" % "test",
  "org.seleniumhq.selenium" % "selenium-htmlunit-driver" % "2.44.0",
  "org.seleniumhq.selenium" % "selenium-firefox-driver" % "2.44.0",
  "com.typesafe.slick" %% "slick" % "2+",
  "com.typesafe.slick" %% "slick-codegen" % "2+",
  "com.github.tminglei" %% "slick-pg" % "0+",
  "org.liquibase" % "liquibase-core" % "2.0.5"
)

assemblySettings

releaseSettings

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies              // : ReleaseStep
//  inquireVersions,                        // : ReleaseStep
//  runTest,                                // : ReleaseStep
//  setReleaseVersion,                      // : ReleaseStep
//  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
//  tagRelease,                             // : ReleaseStep
//  publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
//  setNextVersion,                         // : ReleaseStep
//  commitNextVersion,                      // : ReleaseStep
//  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)
