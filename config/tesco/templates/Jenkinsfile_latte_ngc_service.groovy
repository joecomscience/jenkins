def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId          = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def authorName        = sh(script: "git --no-pager show -s --format='%an' HEAD", returnStdout: true).trim()
    def applicationName   = "seacust-${applicationId}"
    def artifactName      = "${applicationName}-${commitId}.zip"
    def workspace         = pwd()
    def resourceDir       = "${workspace}/src/main/resources"
    try {

      stage("Tag Version") {
        versioning(
          "${applicationName}",
          "${commitId}",
          "${resourceDir}"
        );
      }

      stage("Build") {
        javaBuild();
      }

      stage("Integration Test") {
        javaIntegrationTest("test")
      }

      stage("Build and Push image") {
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

      stage("Deploy Artifact") {
        // Deploy Thailand
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
        
        // Deploy Malaysia
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
          "${applicationId}-malaysia-dev",
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