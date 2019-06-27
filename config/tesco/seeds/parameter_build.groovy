#!/usr/bin/env groovy

def pipelineLists = [
  [
    repository: 'SEA-Customer/varnish-cache-with-stunnel',
    jobfolder: 'tesco',
    jobname: 'scan_as_you_shop',
    template: 'Jenkinsfile_msays',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws',
    parameterName: 'options',
    choices: ['TH (default)', 'Malaysia'],
    optionDes: 'choose contry for deploy',
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
  def parameterName   = item.containsKey('parameterName') ? item['parameterName'] : ''
  def choices         = item.containsKey('choices') ? item['choices'] : ''
  def optionDes       = item.containsKey('optionDes') ? item['optionDes'] : ''

  def workspace       = "${JENKINS_HOME}/workspace"
  def fileLocation   = "${workspace}/${jobfolder}/seed_job/upsteam-jobs/default.groovy"
  def engine          = new groovy.text.SimpleTemplateEngine()
  def text            = new File(fileLocation).text.stripIndent().trim()
  def binding         = [
                          "branch": "${branch}", 
                          "repository": "${repository}",
                          "seedJob": "${jobfolder}/seed_job",
                          "template": "jenkinsfile/${jobfolder}/templates/${template}",
                          "region": "${region}",
                          "awsProfile": "${awsProfile}",
                          "applicationId": "${applicationId}",
                        ]
  def fileScript    = engine.createTemplate(text).make(binding)

  folder("${jobfolder}")

  pipelineJob("${jobfolder}/${jobname}") {
    description "Pipeline for ${jobname}"
    disabled(false)
    logRotator(-1, 5)
    concurrentBuild(false)

    parameters {
      choiceParam(
        parameterName,
        choices,
        optionDes
      )
    }

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
