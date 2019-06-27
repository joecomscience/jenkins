# Jenkins configuration as a code

### Architecture
When you start Jenkins using base image, Jenkins will create `master_seed` and `initial_jobs` jobs by read configuration in `jobs` directory.

- `master_seed`: This job will create the organization by separate from a folder. If you look at the directory diagram below you will see a `tesco` folder contain `seeds` and `templates` directory. explanation as below
  - `seeds`: This directory will contain a file that create jobs.
  - `templates`: This directory store the template file for each job.

**Note** That 2 directories are required when you create new pipeline organization.

- `initial_jobs`: This job will create `backup` and `build_base_image`.
  - `backup`: This job will zip `jenkins_home` directory (by default jenkins home directory should be `/var/jenkins_home` path) and upload it to s3 (bucket: `com.tescolotus.seacust-jenkins`).
  - `build_base_image`: This job will build jenkins base image then push it to ecr.

- `upsteam-jobs`: The upstream job will be included in every pipeline job, it will do the refresh jenkins script and git clone.

```
.
├── jobs
│   ├── initial_jobs
│   └── master_seed
├── tesco
│   ├── seeds
│   └── templates
└── upsteam-jobs
```

### How to create organization

- Open file `jobs/master_seed/job.groovy`.
- Add new orgainzation in the array list.
- Create a folder that has the same name as your organization that you just added and `seeds`, `templates` as a sub directories.
- Open Jenkins in browser then run build `master_seed` job.

### How to add new pipeline
Currently, there are 2 types of pipeline jobs `normal` and `build with parameters` jobs. The `build with parameters` job allows you to provide the options for build the job. Let take a look at how to add new pipeline as below.

- Open file `tesco/seeds/seed_dev.groovy`.
- Add new array list. As below example code, it will create new pipeline name `varnish_cache` to folder `tesco` using template file `Jenkinsfile_varnish_cache` in `tesco/templates` folder, this job will clone git repo `SEA-Customer/varnish-cache` in to workspace and using AWS profile name `3ds_aws` to deploy beanstalk application.

```
  [
    repository: 'SEA-Customer/varnish-cache',
    jobfolder: 'tesco',
    jobname: 'varnish_cache',
    template: 'Jenkinsfile_varnish_cache',
    region: 'ap-southeast-1',
    awsProfile: '3ds_aws'
  ]
```

Paramters

| Name          | Description                                                                     |
|:--------------|:--------------------------------------------------------------------------------|
| repository    | Github repo name.                                                               |
| jobfolder     | Job folder.                                                                     |
| jobname       | Disploy job name in Jenkins dashboard.                                          |
| branch        | Git branch "defalt: `master`"                                                   |
| template      | Template file name (Look at `templates` folder).                                |
| region        | AWS region.                                                                     |
| awsProfile    | AWS profile.                                                                    |
| applicationId | This value will use to tagging artifact name and specify beanstalk appplication |

- Run `seed_job`.

# Add new slave

- Go to `Jenkins -> Nodes -> New Node` then choose `Copy Existing Node` option.
- Fill information following below picture.
![Image](https://github.dev.global.tesco.org/SEA-Customer/jenkins/blob/master/configure-node.png)
- Before launch agent click `Trust SSH Host Key` first then click `Launch Agent`.
![Image](https://github.dev.global.tesco.org/SEA-Customer/jenkins/blob/master/launch-agent.png)

