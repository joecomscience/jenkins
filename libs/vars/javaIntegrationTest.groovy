#!/usr/bin/env groovy

def call(profile=null, optional='') {
  sh "./gradlew -Dspring.profile.active=${profile} integrationTest ${optional}"
}