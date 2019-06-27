#!/usr/bin/env groovy

def call(status=null) {
  def channel = "${METTERMOST_CHANNEL}"

  if (status == "success") {
    mattermostSend(
      color: "#008000", 
      channel: "${channel}", 
      message: createMattermostMessage(":white_check_mark: Success")
    )
  } else {
    mattermostSend(
      color: "#FF0000", 
      channel: "${channel}", 
      message: createMattermostMessage(":beretparrot: Failure")
    )
  }
}

def createMattermostMessage(statusMessage) {
  def jobName = "${JOB_NAME}"
  def job     = jobName.split('/')
  def jobPath = job[0] + '/job/' + job[1]
  "${JOB_NAME} - #${BUILD_NUMBER} ${statusMessage} after ${currentBuild.durationString} [Open](${JENKINS_URL}job/${jobPath}/)" as Object
}
