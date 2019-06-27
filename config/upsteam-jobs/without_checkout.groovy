#!/usr/bin/env groovy

@Library('pipeline-library') _

node() {
  stage('Refetch Script') {
    checkout([
      changelog: false,
      poll: false,
      scm: [
        \$class: 'GitSCM',
        branches: [
          [
            name: '*/master'
          ]
        ],
        doGenerateSubmoduleConfigurations: false,
        extensions: [
          [
            \$class: 'RelativeTargetDirectory',
            relativeTargetDir: 'jenkinsfile'
          ],
          [
            \$class: 'IgnoreNotifyCommit'
          ],
          [
            \$class: 'WipeWorkspace'
          ]
        ],
        submoduleCfg: [],
        userRemoteConfigs: [
          [
            credentialsId: 'github_credential',
            url: 'https://github.dev.global.tesco.org/SEA-Customer/jenkins.git'
          ]
        ]
      ]
    ]);
    // build(job: '$seedJob');
  }

  def jf = load('$template');
  jf.defaultPipeline(
    '$region',
    '$awsProfile',
    '$applicationId',
  );
}
