def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId          = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def authorName        = sh(script: "git --no-pager show -s --format='%an' HEAD", returnStdout: true).trim()
    def applicationName   = "seacust-${applicationId}"
    def artifactName      = "${applicationName}-${commitId}.zip"
    def workspace         = pwd()
    def resourceDir       = "${workspace}/src/main/resources"

    try {
      stage('Tag Version') {
        versioning(
                "${applicationName}",
                "${commitId}",
                "${resourceDir}"
        );
      }

      stage('Build') {
        javaBuild();
      }

      stage('Build Docker') {
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
                true
        );

        uploadFileToS3(
                "${region}",
                "template.zip",
                "${artifactName}",
        );
        uploadFileToS3(
                "${region}",
                "build/libs/${applicationId}.jar",
                "dev/${applicationId}.jar",
        );
      }

      stage('Deploy Artifact') {
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