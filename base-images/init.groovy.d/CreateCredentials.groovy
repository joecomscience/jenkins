// https://support.cloudbees.com/hc/en-us/articles/217708168-create-credentials-from-groovy
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import com.cloudbees.plugins.credentials.domains.*;
import com.cloudbees.jenkins.plugins.awscredentials.*;

def globalScrope = CredentialsScope.GLOBAL
def globalDomain = Domain.global()

def userCredentials = [
  [
    id: System.getenv("GIT_CREDENTIAL_ID"),
    username: System.getenv("GIT_USER"),
    password: System.getenv("GIT_TOKEN"),
    description: ''
  ]
].each { item ->
  def id          = item['id']
  def username    = item['username']
  def password    = item['password']
  def description = item['description']

  Credentials credential = (Credentials) new UsernamePasswordCredentialsImpl(globalScrope, id, description, username, password)
  SystemCredentialsProvider
  .getInstance()
  .getStore()
  .addCredentials(globalDomain, credential)
}

def awsId           = System.getenv("AWS_ID")
def awsAccessKey    = System.getenv("AWS_ACCESS_KEY")
def awsSecretKey    = System.getenv("AWS_SECRET_KEY")
def awsDescription  = System.getenv("AWS_DESCRIPTION") ==  null ? 'aws credential' : System.getenv("AWS_DESCRIPTION")
def awsCredential   = new AWSCredentialsImpl(globalScrope, awsId, awsAccessKey, awsSecretKey, awsDescription)

SystemCredentialsProvider
  .getInstance()
  .getStore()
  .addCredentials(globalDomain, awsCredential)
