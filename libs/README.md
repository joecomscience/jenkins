# jenkins-share-libraries
centralizing pipelines into one shared library.

## How to create a new library

- Create file in `vars` directory example `vars/installDependency.groovy`
- Create a function.
```
def call(args=null) {
  // TODO
}
```
- Copy file name then paste it to Jenkins stage.
```
stage("Build") {
  installDependency(true);
}
```
