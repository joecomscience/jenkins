#!/usr/bin/env groovy

def call(applicationName=null, tag=null, ebextensions=null, command=null) {
  sh "mkdir -p template"

  if(ebextensions) {
    sh "cp -r '.ebextensions/.' 'template/.ebextensions'"
  }

  if(command) {
    sh "${command}"
  }

  script {
    template = readFile file: "Dockerrun.aws.json"
    template = template.replaceAll("<repository>", "${AWS_ECR_URI}/${applicationName}")
    template = template.replaceAll("<tag>", "${tag}")
  }

  script {
    writeFile(
      file: "template/Dockerrun.aws.json",
      text: template
    )
    zip(
      archive: false,
      dir:"template",
      glob: "",
      zipFile: "template.zip"
    )
  }

  sh "rm -rf template"
}