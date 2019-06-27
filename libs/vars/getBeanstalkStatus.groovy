#!/usr/bin/env groovy

def call(profile=null, region=null, beanstalkEnv=null) {
  withAWS(credentials: "${profile}") {
    sh """
      timeout=\$((30*60));
      interval=5;
      counter=0;
      maxRetry=\$((\$timeout/\$interval));

      getBeanstalkStatus() {
        beanstalkStatus=\$(aws elasticbeanstalk describe-environment-health --region ${region} --environment-name ${beanstalkEnv} --attribute-names Color | jq '.Color' | sed 's/"//g')
      }

      # call function
      getBeanstalkStatus

      sleep 10s;
      while [ \$beanstalkStatus != "Green" ]
      do
        echo "--status: \$beanstalkStatus --"
        if [ \$counter -ge \$maxRetry ]
        then
          exit 1;
        fi
        counter=\$((\$counter+1))
        sleep \$interval;

        # call function again
        getBeanstalkStatus
      done

      echo "--All Green--"
      exit 0;
    """
  }
}