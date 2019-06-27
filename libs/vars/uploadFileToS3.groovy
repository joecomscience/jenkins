#!/usr/bin/env groovy

def call(region=null, file=null, path=null, bucket="${AWS_UPLOAD_ARTIFACT_BUCKET}") {
  withAWS(region: "${region}") {
    s3Upload(
      file: "${file}",
      bucket: "${bucket}",
      path: "${path}",
    )
  }
}