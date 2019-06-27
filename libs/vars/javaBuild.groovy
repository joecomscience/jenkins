#!/usr/bin/env groovy

def call(customBuild=null) {
  sh "chmod +x gradlew"

  if(customBuild) {
    sh "${customBuild}"
  } else {
    sh "./gradlew clean build"
  }
}