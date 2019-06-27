FROM openjdk:8-jdk

LABEL maintainer="Joewalker<joeteerawit@gmail.com>"

ENV JENKINS_HOME=/var/jenkins_home

RUN apt-get update && \
    wget http://mirrors.jenkins.io/war-stable/latest/jenkins.war && \
    mkdir -p /var/jenkins_home

RUN java -jar jenkins-cli.jar -s http://127.0.0.1:8080/ install-plugin <name>