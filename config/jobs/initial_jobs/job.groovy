pipelineJob("backup") {
  description("joewalker")
  disabled(false)
  concurrentBuild(false)
  logRotator(-1, 5)

  properties {
    githubProjectUrl("https://github.dev.global.tesco.org/jenkins.git")
  }

  triggers {
      cron("H 3 1-31 * *")
  }

  definition {
    cps {
      sandbox(true)
      script(
        """
          node('master-node') {
            stage('zip file') {
                sh \"\"\"
                    rm -f jenkins_home.zip
                    zip -r jenkins_home.zip /var/jenkins_home
                \"\"\"
            }
            
            stage('upload to s3') {
                withAWS(credentials: "3ds_aws") {
                    sh \"\"\"
                      aws s3api put-object \
                      --bucket com.tescolotus.seacust-jenkins \
                      --key jenkins_home.zip \
                      --body  /var/jenkins_home/workspace/backup/jenkins_home.zip
                    \"\"\"
                }

                sh "rm -f jenkins_home.zip"
            }
        }
        """.trim()
      )
    }
  }
}

pipelineJob("build_base_image") {
  description("Pipeline for build Jenkins base images")
  disabled(false)
  concurrentBuild(false)
  logRotator(-1, 5)

  properties {
    githubProjectUrl("https://github.dev.global.tesco.org/jenkins.git")
  }

  triggers {
    githubPush()
  }

  definition {
    cps {
      sandbox(true)
      script(
        """
          node() {
            def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()

            stage('Build and Push image') {
                buildDocker(
                  "jenkins",
                  "\${commitId}",
                  "ap-southeast-1",
                );
              }
          }
        """.trim()
      )
    }
  }
}