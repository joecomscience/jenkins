def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId          = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def authorName        = sh(script: "git --no-pager show -s --format='%an' HEAD", returnStdout: true).trim()
    def applicationName   = "seacust-${applicationId}"
    def artifactName      = "${applicationName}-${commitId}.zip"

    try {

      stage("Build Docker") {
        buildDocker(
          "${applicationName}",
          "${commitId}",
          "${region}",
        );
      }

      stage("Upload Artifact") {
        prepareBeanstalkConfig(
          "${applicationName}",
          "${commitId}",
          true,
          "cp cron.yaml template/cron.yaml"
        );

        uploadFileToS3(
          "${region}",
          "template.zip",
          "${artifactName}",
        );
      }

      stage("Deploy API Artifact") {
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
          "${applicationId}-api-dev",
          "${artifactName}",
        );

        beanstalkCreateAppVersion(
          "${awsProfile}",
          "${region}",
          "${applicationName}-malaysia",
          "${artifactName}",
        );

        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}-malaysia",
          "${applicationId}-malaysia-api-dev",
          "${artifactName}",
        );
      }
  
      stage("Deploy CMS Artifact") {
        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${applicationId}-cms-dev",
          "${artifactName}",
        );

        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${applicationId}-malaysia-cms-dev",
          "${artifactName}",
        );
      }

      stage("Deploy Cronjob Artifact") {
        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${applicationId}-cronjob-dev",
          "${artifactName}",
        );

        beanstalkUpdateEnv(
          "${awsProfile}",
          "${region}",
          "${applicationName}",
          "${applicationId}-malaysia-cronjob-dev",
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