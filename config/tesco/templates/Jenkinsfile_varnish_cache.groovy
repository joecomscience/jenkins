def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId        = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def applicationName = "varnish-cache"

    stage('Build and Push image') {
      buildDocker(
        "${applicationName}",
        "${commitId}",
        "${region}",
      );
    }
  }
}

return this;