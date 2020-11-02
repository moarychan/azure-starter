# Spring Cloud Stream Event Hub Transformer

A simple project which demonstrates how to configure an Event Hub event 
transformer which uses Spring-Cloud-Stream with the
[Azure Event Hub Stream Binder](https://github.com/Azure/azure-sdk-for-java/tree/master/sdk/spring/azure-spring-cloud-eventhubs-stream-binder).

The transformer retrieves a message from Event Hub, changes it, and sends it to
the next Event Hub.

This project also contains example for Spring Cloud Stream unit tests.

## Example Configurations

Example configuration can be found under `src/main/resources`:

* `application-example-local` - Example configuration to run the consumer locally.

* `application-example-msi` - Example configuration to how to connect to the
  Event Hub Namespace directly via MSI. This can be done only when running on
  top of Azure, e.g. on top of AKS.
  