import sbtrelease._
import ReleaseStateTransformations._

lazy val execScript = taskKey[Unit]("Execute the shell script")

execScript := {
  "./deploy.sh" !
}

ReleaseKeys.releaseProcess := Seq[ReleaseStep](
//  releaseTask(execScript),
  ReleaseStep(releaseTask(execScript)),
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
//    publishArtifacts,                       // : ReleaseStep, checks whether `publishTo` is properly set up
  setNextVersion,                         // : ReleaseStep
  commitNextVersion                      // : ReleaseStep
//  pushChanges,                            // : ReleaseStep, also checks that an upstream branch is properly configured
)
