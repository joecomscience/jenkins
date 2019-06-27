#!/usr/bin/env groovy

import hudson.model.Result
import jenkins.model.CauseOfInterruption.UserInterruption

def call() {
  while(currentBuild.rawBuild.getPreviousBuildInProgress() != null) {
    currentBuild.rawBuild.getPreviousBuildInProgress().doKill()
  }
}
