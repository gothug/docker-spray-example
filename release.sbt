import sbtrelease._
import ReleaseStateTransformations._

lazy val execScript = taskKey[Unit]("Execute the shell script")

execScript := {
  "./deploy.sh" !
}

lazy val runScript : ReleaseStep = ReleaseStep(
  action = { st: State =>
    val extracted = Project.extract(st)
    val ref = extracted.get(thisProjectRef)
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
//  ReleaseStep(releaseTask(execScript), x => x, enableCrossBuild = false) : ReleaseStep
//  releaseTask(execScript) : ReleaseStep
  runScript
)
