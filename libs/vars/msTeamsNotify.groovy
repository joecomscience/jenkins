#!/usr/bin/env groovy

def call(status = null, commitId = null, authorName = null) {
  if (status == "success") {
    office365ConnectorSend(
      message: getNotificationMessage("Success", commitId, authorName),
      status: "Build Success",
      color: "0be725",
      webhookUrl: "${MS_WEBHOOKS_URL}"
    )
  } else {
    office365ConnectorSend(
      message: getNotificationMessage("Fail", commitId, authorName),
      status: "FAILURE",
      color: "d93232",
      webhookUrl: "${MS_WEBHOOKS_URL}"
    )
  }
}

def getNotificationMessage(statusMessage, commitId, authorName) {
    def jobName = "${JOB_NAME}"
    def job     = jobName.split('/')
    def jobPath = job[0] + '/job/' + job[1]
    def message = "${commitId} is committed by ${authorName} - ${JOB_NAME} - #${BUILD_NUMBER} ${statusMessage} after ${currentBuild.durationString} [Open](${JENKINS_URL}job/${jobPath}/)"
    return message
}
