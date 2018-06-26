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
oc new-project hello-rest
```

## build the app locally

```
cd spring-boot-rest-service
```

```
mvn clean package
```

This will generate in ```target``` dir two artifacts: one is the fat jar, one is the simple jar file.

## Create the binary builder

We can create the builder for our application:

```
oc new-build --binary=true --name=hello-rest --image-stream=redhat-openjdk18-openshift:1.2 
```

```
oc env bc/hello-rest -e JAVA_APP_JAR=hello-rest-1.0-exec.jar
```
```
oc start-build hello-rest --from-dir=./target --follow
```

**NOTE:** the property ```JAVA_APP_JAR``` used in the build config will be injected into the future container running in the pod of our service, if you want to change the name of the jar in the future or change the approach you should read more about [build strategy](https://docs.openshift.com/container-platform/3.9/dev_guide/builds/build_strategies.html) , [how deployment works](https://docs.openshift.com/container-platform/3.9/dev_guide/deployments/how_deployments_work.html) and reference for [redhat-openjdk18-openshift](https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_java_s2i_for_openshift/index) image 


## Setup configmap

Move back to the root of git project:

```
cd ..
```

You now should see:

```
ls 
README.md                configuration            spring-boot-rest-service
```

Now we can the command to create the config-map:

```
oc create configmap hello-rest-config --from-file=configuration
```
Verify with:

```
oc get configmap hello-rest-config -o yaml
```

Should output something like:

```
apiVersion: v1
data:
  hello-rest.properties: externalgreetings=Hello from properties
kind: ConfigMap
metadata:
  creationTimestamp: 2018-06-25T20:10:45Z
  name: hello-rest-config
  namespace: hello-rest
  resourceVersion: "42818"
  selfLink: /api/v1/namespaces/hello-rest/configmaps/hello-rest-config
  uid: d823780c-78b3-11e8-8748-f6570a2ca167
```

### Replace config map

This is a bit tricky:

```
oc create configmap hello-rest-config --from-file=configuration --dry-run -o yaml|oc replace -f -
```

## Create the app 

Now we can create the app for the buil, this will create the DeploymentConfig for openshift.

```
oc new-app hello-rest
```

Now the application should start but, will crash becouse we need to configure it for runtime.

## Configure for runtime

### Set environment variables

We need to add the runtime properties to point the properties file:

```
oc set env dc/hello-rest JAVA_OPTIONS="-DPROPERTIES_PATH=/opt/hello-rest-config/hello-rest.properties"
```

### Mount config maps for runtime

```
oc volume dc/hello-rest --overwrite --add -t configmap  -m /opt/hello-rest-config --name=hello-rest-config --configmap-name=hello-rest-config
```
The first time you get an output similar to this:

```
warning: volume "hello-rest-config" did not previously exist and was not overriden. A new volume with this name has been created instead.deploymentconfig "hello-rest" updated
```

Now files stored in the config map will be mounted as regular files inside each pod on the path ```/opt/hello-rest-config```

## Change the name of the fat jar to run (optional if you change the mane of the jar)

This is an hint on how to change the jar runtime name in ```DeploymentConfig```, please refer to [note](#Create-the-binary- builder) on previous paragraphs to read more aboutn build and deploy configuration for Openshift.

To change the name of the jar to run add the environment variablem to DeploumentConfig:

```
JAVA_APP_JAR=<your-app>-exec.jar
```

## Expose the service as route.

```
oc get svc -o name
service/hello-rest
```

```
oc expose svc/hello-rest
route "hello-rest" exposed
```
Access the application.

# Reference

[s2i for jdk image documentation](https://access.redhat.com/documentation/en-us/red_hat_jboss_middleware_for_openshift/3/html-single/red_hat_java_s2i_for_openshift/index)