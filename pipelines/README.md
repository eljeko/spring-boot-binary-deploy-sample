# Jenkins pipelines for binary deploy

## Create the project

```
oc new-project cicd
```


Add jenkins to the project (from the webconsole):

## Empty project

Verify you are in the right project:

```
oc project
```

If you need switch to the ```cicd``` project:

```
oc project cicd
```

## Setup grants and roles

Grant all the roles to jenkins service account for the other two projects (cool-app-dev and cool-app-prod):

system:serviceaccount:cicd:jenkins

oc login -u system:admin 

```
oc policy add-role-to-user edit system:serviceaccount:cicd:jenkins -n hello-rest
```

## Add the pipeline to the project

Login back to developer:

```
oc login -u developer
```

Create the pipeline:

```
oc create -f pipeline-binary-deploy-sample.yaml 
```

Now from the cicd project we can start the pipeline to run a new binary deploy

## Run the pipeline

Now you can start the pipeline from the web console or running:

```
oc start-build hello-rest-deploy
```