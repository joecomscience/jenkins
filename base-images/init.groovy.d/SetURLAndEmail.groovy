import jenkins.model.Jenkins
import jenkins.model.JenkinsLocationConfiguration

def jlc = JenkinsLocationConfiguration.get()
jlc.setUrl(System.getenv("JENKINS_URL"))
jlc.setAdminAddress('Jenkins Admin <admin@jenkins.com>')
jlc.save()