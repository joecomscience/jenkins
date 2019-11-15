FROM jenkins/jenkins:lts

USER root
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

RUN apt-get update && \
    apt-get install -y git maven

COPY plugins.txt /tmp/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /tmp/plugins.txt

COPY init.groovy.d /usr/share/jenkins/ref/init.groovy.d
COPY ./scripts/docker-entrypoint.sh /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]