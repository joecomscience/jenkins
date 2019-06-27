#!/usr/bin/env groovy

def call(applicationName=null, commitId=null, location="") {
  def timezone  = TimeZone.getTimeZone("Asia/Bangkok")
  def today     = new Date().format('yyyy-MM-dd HH:mm:ss', timezone)
  sh "echo ${applicationName}-${today}-${commitId} > ${location}/version.txt"
}
