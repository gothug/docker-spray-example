import sbtrelease._
import ReleaseStateTransformations._
//    import scala.sys.process._

import CommandExample._

lazy val execScript = taskKey[Unit]("Execute the shell script")

//lazy val hello = TaskKey[Unit]("hello") := println("hello world!")

execScript := {
//  "/Users/kojuhovskiy/github/docker-spray-example/deploy.sh" !
  println("hello world!")
}

lazy val runScript : ReleaseStep = ReleaseStep(
  action = { st: State =>
    val extracted = Project.extract(st)
    val ref = extracted.get(thisProjectRef)
    execScript
    extracted.runAggregated(execScript, st)
  }
)

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
//  releaseTask(execScript),
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
//    publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
//  pushChanges,                            // : ReleaseStep, also checks that an upstream branch is properly configured
//  releaseTask(execScript)
  ReleaseStep({ state =>
    "deploy.sh".!
//    println("Hi!")
    state
  })
//  releaseTask(execScript) : ReleaseStep
//  runScript
)
