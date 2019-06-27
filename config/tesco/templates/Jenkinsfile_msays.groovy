def defaultPipeline(region=null, awsProfile=null, applicationId=null) {
  dir('src') {
    def commitId          = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
    def applicationName   = "${params.COUNTRY}" == "TH" ? "seacust-scan-as-you-shop" : "seacust-scan-as-you-shop-malaysia"
    def artifactName      = "${applicationName}-${commitId}.zip"
    def workspace         = pwd()

    stage('Build and Push image') {
      echo ">>>joewalker<<<"
      echo "${params.COUNTRY}"
      echo "${applicationName}"
      // buildDocker(
      //   "${applicationName}",
      //   "${commitId}",
      //   "${region}",
      // );
    }
  }
}

return this;