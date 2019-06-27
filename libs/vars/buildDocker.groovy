#!/usr/bin/env groovy

def call(imageName=null, tag=null, region=null) {

  // https://jenkins.io/doc/book/pipeline/docker/
  docker.withRegistry("https://${AWS_ECR_URI}", "ecr:ap-southeast-1:jenkins_ecr") {
    dockerImage = docker.build("${imageName}:${tagName}", "--no-cache .")
    dockerImage.push()
    dockerImage.push('latest')
  }
  
  def buildImage      = "${imageName}:${tag}"
  def tagLatestImage  = "${AWS_ACCOUNT_ID}.dkr.ecr.${region}.amazonaws.com/${imageName}:latest"
  def tagCommitImage  = "${AWS_ACCOUNT_ID}.dkr.ecr.${region}.amazonaws.com/${buildImage}"

  sh """
    eval \$(aws ecr get-login --region ${region} --no-include-email)
    docker build -t ${buildImage} .

    docker tag ${buildImage} ${tagLatestImage}
    docker push ${tagLatestImage}
    docker rmi ${tagLatestImage}

    docker tag ${buildImage} ${tagCommitImage}
    docker push ${tagCommitImage}
    docker rmi ${tagCommitImage}
    
    docker rmi ${buildImage}
  """
}