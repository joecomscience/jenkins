#!/usr/bin/env groovy

def call(profile=null, region=null, applicationName=null, beanstalkEnv=null, artifactName=null) {
  withAWS(credentials: "${profile}") {
    sh """
      aws elasticbeanstalk update-environment \
        --region ${region} \
        --application-name ${applicationName} \
        --environment-name ${beanstalkEnv} \
        --version-label ${artifactName}
    """
  }

  getBeanstalkStatus(
    "${profile}",
    "${region}",
    "${beanstalkEnv}",
  );
}