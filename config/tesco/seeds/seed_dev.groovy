#!/usr/bin/env groovy

def pipelineLists = [
  [
    repository: 'SEA-Customer/varnish-cache',
    jobfolder: 'tesco',
    jobname: 'default',
    template: 'Jenkinsfile',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws'
  ],
  [
    repository: 'SEA-Customer/estamp',
    jobfolder: 'tesco',
    jobname: 'estamp',
    template: 'Jenkinsfile_estamp',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'estamp'
  ],
  [
    repository: 'SEA-Customer/tescomobile-service',
    jobfolder: 'tesco',
    jobname: 'tescomobile_service',
    template: 'Jenkinsfile_tescomobile_service',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'tescomobile'
  ],
  [
    repository: 'SEA-Customer/malaysia-clubcard-registration',
    jobfolder: 'tesco',
    jobname: 'malaysia_clubcard_registration',
    template: 'Jenkinsfile_malaysia_clubcard_registration',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'malaysia-clubcard-registration'
  ],
  [
    repository: 'SEA-Customer/e-stamp-transaction-forwarder',
    jobfolder: 'tesco',
    jobname: 'e_stamp_transaction_forwarder',
    template: 'Jenkinsfile_e_stamp_transaction_forwarder',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'e-stamp-transaction-forwarder'
  ],
  [
    repository: 'SEA-Customer/latte-ngc-service',
    jobfolder: 'tesco',
    jobname: 'latte_ngc_service',
    template: 'Jenkinsfile_latte_ngc_service',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'latte-ngc-service'
  ],
  [
    repository: 'SEA-Customer/sms-service',
    jobfolder: 'tesco',
    jobname: 'sms_service',
    template: 'Jenkinsfile_sms_service',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'sms-service'
  ],
  [
    repository: 'SEA-Customer/varnish-cache',
    jobfolder: 'tesco',
    jobname: 'varnish_cache',
    template: 'Jenkinsfile_varnish_cache',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws'
  ],
  [
    repository: 'SEA-Customer/tescomobile-event-consumer',
    jobfolder: 'tesco',
    jobname: 'tescomobile-event-consumer',
    template: 'Jenkinsfile_tescomobile_event_consumer',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'tescomobile-event-consumer'
  ],
  [
    repository: 'SEA-Customer/marketplace-api-adapter',
    jobfolder: 'tesco',
    jobname: 'marketplace-api-adapter',
    template: 'Jenkinsfile_marketplace_api_adapter',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: 'marketplace-api-adapter'
  ],
  [
    repository: 'SEA-Customer/3pl-api-adapter-service',
    jobfolder: 'tesco',
    jobname: '3pl-api-adapter-service',
    template: 'Jenkinsfile_3pl_api_adapter_service',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    applicationId: '3pl-api-adapter-service'
  ]
].each { item ->
  def repository      = item['repository']
  def jobfolder       = item['jobfolder']
  def jobname         = item['jobname']
  def branch          = item.containsKey('branch') ? item['branch'].toLowerCase() : 'master'
  def template        = item.containsKey('template') ? item['template'] : 'Jenkinsfile'
  def region          = item.containsKey('region') ? item['region'] : ''
  def awsProfile      = item.containsKey('awsProfile') ? item['awsProfile'] : ''
  def applicationId   = item.containsKey('applicationId') ? item['applicationId'] : ''

  // Jenkins template
  // http://docs.groovy-lang.org/docs/next/html/documentation/template-engines.html
  def workspace       = "${JENKINS_HOME}/workspace"
  def fileLocaltion   = "${workspace}/${jobfolder}/seed_job/upsteam-jobs/default.groovy"
  def engine          = new groovy.text.SimpleTemplateEngine()
  def text            = new File(fileLocaltion).text.stripIndent().trim()
  def binding         = [
                          "branch": "${branch}",
                          "repository": "${repository}",
                          "seedJob": "${jobfolder}/seed_job",
                          "template": "jenkinsfile/${jobfolder}/templates/${template}.groovy",
                          "region": "${region}",
                          "awsProfile": "${awsProfile}",
                          "applicationId": "${applicationId}",
                        ]
  def fileScript    = engine.createTemplate(text).make(binding)

  folder("${jobfolder}")

  // Find document for setting as below link.
  // https://jenkinsci.github.io/job-dsl-plugin/#path/pipelineJob
  pipelineJob("${jobfolder}/${jobname}") {
    description "Pipeline for ${jobname}"
    disabled(false)
    concurrentBuild(false)
    logRotator(-1, 5)

    properties {
      githubProjectUrl("https://github.dev.global.tesco.org/${repository}")
    }

    triggers {
        githubPush()
    }

    definition {
      cps {
        sandbox(true)
        script(fileScript.toString())
      }
    }
  }
}
