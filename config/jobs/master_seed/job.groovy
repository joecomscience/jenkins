#!/usr/bin/env groovy

def entities = [
  [
    name: 'tesco'
  ]
].each { item ->

  entity = item['name']

  def jobname = 'seed_job'

  folder("${entity}")
  job("${entity}/${jobname}") {
    label('master-node')
    description "Seed Job for ${entity}"
    disabled(false)
    concurrentBuild(false)
    logRotator(-1, 5)

    scm {
      git {
        remote {
          url('https://github.dev.global.tesco.org/SEA-Customer/jenkins.git')
          credentials('github_credential')
        }
        branch('master')
      }
    }

    steps {
      jobDsl {
        targets("${entity}/seeds/*.groovy")
        sandbox(false)
        ignoreExisting(false)
      }
    }
  }
}