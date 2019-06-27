#!/usr/bin/env groovy

def call(profile=null, region=null, applicationName=null, artifactName=null) {
  withAWS(credentials: "${profile}") {
    sh """
      aws elasticbeanstalk create-application-version \
      --region ${region} \
      --application-name ${applicationName} \
      --version-label ${artifactName} \
      --source-bundle S3Bucket=${AWS_UPLOAD_ARTIFACT_BUCKET},S3Key=${artifactName} || true
    """
  }
}