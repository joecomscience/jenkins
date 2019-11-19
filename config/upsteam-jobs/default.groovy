#!/usr/bin/env groovy

@Library('pipeline-library') _

node() {
  stage('Refetch Script') {
    // abortRunningBuild()
    // document
    // https://support.cloudbees.com/hc/en-us/articles/226122247-How-to-Customize-Checkout-for-Pipeline-Multibranch-
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
            url: '$pipeLineRepo'
          ]
        ]
      ]
    ]);
    // build(job: '$seedJob');
  }

  stage('Checkout Source Code') {
    checkout([
      changelog: true,
      scm: [
        \$class: 'GitSCM',
        branches: [
          [
            name: '*/$branch'
          ]
        ],
        doGenerateSubmoduleConfigurations: false,
        extensions: [
          [
            // set timeout when clone huge repo
            \$class: 'CloneOption', 
            timeout: 60
          ],
          [
            \$class: 'RelativeTargetDirectory',
            relativeTargetDir: 'src'
          ],
          [
            \$class: 'WipeWorkspace'
          ]
        ],
        submoduleCfg: [],
        userRemoteConfigs: [
          [
            credentialsId: 'github_credential',
            url: '$gitBaseUrl/$repository' + '.git'
          ]
        ]
      ]
    ]);
  }

  def jf = load('$template');
  jf.defaultPipeline();
}
