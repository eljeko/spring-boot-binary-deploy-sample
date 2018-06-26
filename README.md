# About the sample

This sample is based on [RHOAR Spring Boot](https://developers.redhat.com/products/rhoar/overview/), the goal of the sample is to show how to build locally a fat jar for [OCP](https://www.openshift.com/) and then deploy it usign the Binary Deploy approach.

The sample works both on [OCP](https://www.openshift.com/) and [Minishfit](https://developers.redhat.com/products/cdk/overview/).

The tutorial assumes that:
* You already have a running [OCP](https://www.openshift.com/) and/or [Minishfit](https://developers.redhat.com/products/cdk/overview/) installed.
* You have installed the [oc](https://docs.openshift.com/container-platform/3.9/cli_reference/get_started_cli.html) cli tool.

The first part is focused on manual deploy, the second part introduces the jenkins pipelines.

# Manual deploy

## Create the namespace

First login into openshift:

```
oc login -u <YOUR_USER>
```

On minishift (you can use whatever you want as password:

```
oc login -u developer
```

Then create our prject:

```
oc new-project rest-hello
```

## build the app locally

```
cd spring-boot-rest-service
```

```
mvn clean package
```

This will generate in target two artifacts, one is the fat jar, one is the simple jar file.

## Create the binary builder

With this command we can create the builder for our application:

```
oc new-build --binary=true --name=rest-hello --image-stream=redhat-openjdk18-openshift:1.2 
```

```
oc env bc/rest-hello -e JAVA_APP_JAR=hello-1.0-exec.jar
```
```
oc start-build rest-hello --from-dir=./[PATH_TO] --follow
```
## Create the app 
```
oc new-app rest-hello
```
## Setup the deployment config (optional if you change the mane of the jar)

Add the environment variablem to DeploumentConfig:

```
JAVA_APP_JAR=<your-app>-exec.jar
```

## Expose the service as route.

```
oc get svc -o name
service/rest-hello
```

```
oc expose svc/rest-hello
route "rest-hello" exposed
```
Access the application.


# Reference

[s2i for jdk image documentation](https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_java_s2i_for_openshift/index)