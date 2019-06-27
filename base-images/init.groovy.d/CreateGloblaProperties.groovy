import hudson.EnvVars;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;

def globalProperties = [
  [
    key: "AWS_ACCOUNT_ID",
    value: "393437166688"
  ],
  [
    key: "AWS_ECR_URI",
    value: "393437166688.dkr.ecr.ap-southeast-1.amazonaws.com"
  ],
  [
    key: "AWS_UPLOAD_ARTIFACT_BUCKET",
    value: "com.tescolotus.seacust-upload-artifact"
  ],
  [
    key: "MS_WEBHOOKS_URL",
    value: "https://outlook.office.com/webhook/ab90b744-b20e-41d0-9c7f-70a81639c2b2@3928808b-8a46-426b-8f87-051a36bb2f91/JenkinsCI/54bfc79a3e6e40ddab1c0942f7ef7905/b7391f0e-d5cd-421e-885d-f26a8e5dc117"
  ]
].each{ item -> 
  def key   = item['key']
  def value = item['value']

  Jenkins instance = Jenkins.getInstance();

  DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties = instance.getGlobalNodeProperties();
  List<EnvironmentVariablesNodeProperty> envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);

  EnvironmentVariablesNodeProperty newEnvVarsNodeProperty = null;
  EnvVars envVars = null;

  if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
    newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
    globalNodeProperties.add(newEnvVarsNodeProperty);
    envVars = newEnvVarsNodeProperty.getEnvVars();
  } else {
    envVars = envVarsNodePropertyList.get(0).getEnvVars();
  }
  envVars.put(key, value)
  instance.save()
}
