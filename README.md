# mpconfig-instanton

Example restful web service using Open Liberty features `restfulWS-3.1` and `mpConfig-3.0` features with Liberty InstantOn. There are three different rest service CDI beans:

1. `requestscope` - bean gets created each request to its paths
2. `appscope` - bean gets created once upon first request to one of its paths
3. `earlystart-appscope` - bean gets created once during application startup

Each rest service CDI bean has three paths:

1. `config1` - uses dynamic MP Config injection with a `Provider<String>` type.
2. `config2` - uses static MP Config injection
3. `config` - uses the MP `Config` object to lookup config values.

With Liberty InstantOn the configuration values may change between the checkpoint and the restore. The Liberty MP Config implementation is able to reflect the current values set after the process has been restored according to the current environment.

Some considerations are needed by CDI beans getting injected with MP Config:

For `@RequestScope` CDI beans, they will always get injected with the latest configuration values because they are constructed and injected with the config each time a new request comes in.  Liberty InstantOn only allows requests to be received by the application once the process has been restored and the latest configuration values have been applied.  This means that `@RequestScope` beans will always get the correct config and do not need to worry about stale values.

For `@ApplicationScope` beans, they will get created once upon the first time they are needed. This may happen as teh application is starting or in reaction to servicing a request to the application. If the bean is created as the application is starting, for example, to receive lifecycle notifications for the `ApplicationScope` getting created, then the bean may get injected with config that becomes stale when restoring from a checkpoint.

## Building the application

Podman is the easiest way to build the application.  To build you must run the build as root or be a `sudo` user. This is required to grant the container image build engine the required Linxu capabilities to checkpoint.

Docker is possible to build and run, but building requires a [three step process](https://openliberty.io/docs/latest/instanton.html#three_step_process) to build.

To build the InstantOn application image run the `build-container.sh` script. To run the InstantOn application image run the `run-container.sh` script.

## Example endpoints

The `mpconfig-instanton` application will have the following endpoints available:

### requestscope

- [requestscope/config1](http://localhost:9080/mpconfig-instanton/requestscope/config1) - output `config1=envValue1`
- [requestscope/config2](http://localhost:9080/mpconfig-instanton/requestscope/config2) - output `config2=envValue2`
- [requestscope/config](http://localhost:9080/mpconfig-instanton/requestscope/config) - output `config1=envValue1 config2=envValue2`

### appscope

- [appscope/config1](http://localhost:9080/mpconfig-instanton/appscope/config1) - output `config1=envValue1`
- [appscope/config2](http://localhost:9080/mpconfig-instanton/appscope/config2) - output `config2=envValue2`
- [appscope/config](http://localhost:9080/mpconfig-instanton/appscope/config) - output `config1=envValue1 config2=envValue2`

### earlystart-appscope

- [earlystart-appscope/config1](http://localhost:9080/mpconfig-instanton/earlystart-appscope/config1) - output `config1=envValue1`
- [earlystart-appscope/config2](http://localhost:9080/mpconfig-instanton/earlystart-appscope/config2) - output `config2=default` (stale default value)
- [earlystart-appscope/config](http://localhost:9080/mpconfig-instanton/earlystart-appscope/config) - output `config1=envValue1 config2=envValue2`

The `run-container.sh` sets to environment variables that configure the values used by the above endpoints for `config1` and `config2`. All but one endpoint above gets updated to the correct value on restore. The problematic endpoint is [earlystart-appscope/config2](http://localhost:9080/mpconfig-instanton/earlystart-appscope/config2) which is injected with the static, stale default value before the checkpoint happens for `config2`. This endpoint is provided by the [ConfigEarlyStartApplicationScope](src/main/java/io/openliberty/example/instanton/ConfigEarlyStartApplicationScope.java) bean. This CDI bean attempts to register itself as a `Resource` with `org.crac` so that it can lookup the latest value for `config2` upon restore.

The CRaC `Resource` will not get called unless the `crac-1.3` feature is enabled for Liberty. That is done by editing the [src/main/liberty/config/server.xml](src/main/liberty/config/server.xml) file and uncommenting the `<feature>crac-1.3</feature>` element of the `server.xml`.  Once this is done run the `build-container.sh` script again and then run the `run-container.sh` script and try the [earlystart-appscope/config2](http://localhost:9080/mpconfig-instanton/earlystart-appscope/config2) again.