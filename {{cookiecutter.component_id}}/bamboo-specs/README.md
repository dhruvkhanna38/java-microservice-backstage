# Bamboo Specs

Bamboo Specs is to store build plans configuration as code. Storing build plan configuration as code makes easier automation, change tracking, validation, and much more.

## How to use Bamboo Specs

Basic functionalities are ready to use in this repository so you just use this repository and update configuration file, bamboo_specs.json.

# Get Started!

##### Go to the root directory of a service.
```bash
git clone ssh://git@git02.ae.sda.corp.telstra.com/xen/bamboo-specs.git
cd bamboo-specs 
rm -rf .git*
``` 
##### Edit configuration file
```bash
vi vi src/main/resources/bamboo_specs.json
``` 

##### Commit and push the service repository
```bash
git add bamboo-specs
git commit -m "Add bamboo-specs"
git push
``` 

## bamboo_specs.json 
This configuration file is json format so you just update values.
```bash
{
  "projectName": "Bamboo Project Name",
  "projectKey": "Bamboo Project Key",
  "masterPlanKey": "Bamboo Master Plan Name",
  "planName": "Bamboo Plan Name",
  "planKey": "Bamboo Plan Key",
  "defaultLinkedRepository": "LINKED REPOSITORY",
  "linkedRepositories": ["LINKED REPOSITORY"],
  "groupPermissions": [
    {"name": "Tiger - admin", "type": "ADMIN"}
  ],
  "userPermissions": [
    {"name": "d904381", "type": "ADMIN"}
  ],
  "variables": [
    {"key": "BAMBOO_ENV_BUILDER", "value": "amp-bamboo-build:1.0.1"},
    {"key": "MAVEN_DOCKER_IMAGE", "value": "maven:3.6.3-jdk-8-slim"}
  ],
  "stages": [
    {"name": "LibBuildStage"},
    {"name": "BuildStage", "filePath": "./share/docker/buildDockerImage.sh"},
    {"name": "DeployStage", "env": "dev", "isManual": true, "filePath": "./share/service/deploy-service.shv"},
    {"name": "SVTCICDStage", "testType": ["shakeout"], "isManual": true}
  ],
  "deployment":{"env": ["DEV", "UAT", "SVT", "SVTCICD", "SPT", "PROD"], "filePath": "./share/service/deploy-service.sh"},
  "svtCicd": ["shakeout", "load", "soak", "linear", "auto_scaling", "mock_delay"]
}
```
#### Definition of fields

| Key  | Value | Type |
| :--- | :---- | :--- |
| projectName | Bamboo Project Name | String |
| projectKey | Bamboo Project Key | String |
| masterPlanKey | Team Master Plan Name | String |
| planName | Bamboo Plan Name | String |
| planKey | Bamboo Plan Key | String |
| defaultLinkedRepository | Default repository | String |
| linkedRepositories | Additional repositories | String Array |
| groupPermissions | Group permission on Bamboo Plan | Permission Object Array |
| userPermissions | Individual permission on Bamboo Plan | Permission Object Array |
| variables | Bamboo plan variables | Variable Object Array |
| stages | List of stages | Stage Object Array |
| deployment | Deployment project environments | Deployment Object |
| svtCicd | SVT CICD test configuration | String Array |
* Team Master Plan is to manage all kind of credentials on Bamboo UI console instead of store them in service repositories.
For example, aws credentials, svt_secret_key

#### Definition of Object types

| Type | Elements | Type |
| :--- | :------- | :--- |
| Permission Object| name, type | type: ADMIN, EDIT, BUILD, VIEW |
| Variable Object | key, value | |
| Stage Object | name, env, , filePath, testType | testType: shakeout, load, soak, linear, auto_scaling, mock_delay |
| Deployment Object | env, filePath | |
* Now SVT CICD Test supports three test types, shakeout, load, soak.

#### Definition of Stage types
| Type | Action | Elements |
| :--- | :------- | :--- |
| LibBuildStage | Build code and push artifacts into Nexus | None |
| BuildStage | Build service and create a docker image and puhs it into repository | filePath is option |
| DeployStage | Deploy a service with the docker image created by BuildStage | env, isManual, filePath
| SVTCICDStage | Run SVT CICD test | testType, isManual |
* filePath usually points to Team Master Plan, Team Master Plan can have any repositories and share them with Child plans. So filepath starts with share/.