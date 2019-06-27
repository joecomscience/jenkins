#!/usr/bin/env groovy

def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId          = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def authorName        = sh(script: "git --no-pager show -s --format='%an' HEAD", returnStdout: true).trim()
    def applicationName   = "seacust-${applicationId}"
    def artifactName      = "${applicationName}-${commitId}.zip"

    try {
      stage('Build Step') {
        sh "echo build golang"
      }

      stage('Unit test') {
        sh "echo 'unit test'"
      }

      stage('Build and Push Image ') {
        buildDocker(
          "${applicationName}",
          "${commitId}",
          "${region}",
        );
      }

      stage('Upload Artifact') {
        prepareBeanstalkConfig(
          "${applicationName}",
          "${commitId}",
        );

        uploadFileToS3(
          "${region}",
          "template.zip",
          "${artifactName}",
        );
      }

      stage('Deploy Beantalks') {
        beanstalkCreateAppVersion(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${artifactName}",
        );

        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${applicationId}-dev",
          "${artifactName}",
        );
      }
    } catch (Exception e) {
      msTeamsNotify("fail","${commitId}","${authorName}")
      throw e
    }
  }
}

return this;