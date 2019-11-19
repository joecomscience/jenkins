#!/usr/bin/env groovy

def pipelineLists = [
  [
    repository: 'root/spring-boot',
    jobfolder: 'aycap',
    jobname: 'default',
    template: 'Jenkinsfile',
  ]
].each { item ->
  def repository      = item['repository']
  def jobfolder       = item['jobfolder']
  def jobname         = item['jobname']
  def branch          = item.containsKey('branch') ? item['branch'].toLowerCase() : 'master'
  def template        = item.containsKey('template') ? item['template'] : 'Jenkinsfile'

  // Jenkins template
  // http://docs.groovy-lang.org/docs/next/html/documentation/template-engines.html
  def workspace       = "${JENKINS_HOME}/workspace"
  def fileLocaltion   = "${workspace}/${jobfolder}/seed_job/upsteam-jobs/default.groovy"
  def engine          = new groovy.text.SimpleTemplateEngine()
  def text            = new File(fileLocaltion).text.stripIndent().trim()
  def binding         = [
                          "pipeLineRepo": "${GIT_PIPELINE_REPO}",
                          "gitBaseUrl": "${GIT_BASE_URL}",
                          "branch": "${branch}",
                          "repository": "${repository}",
                          "seedJob": "${jobfolder}/seed_job",
                          "template": "jenkinsfile/${jobfolder}/templates/${template}.groovy",
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
      githubProjectUrl("${GIT_PIPELINE_REPO}/${repository}")
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
