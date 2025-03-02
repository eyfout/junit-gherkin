## Micronaut Examples
Demonstrates the power and flexibility of the 
[http-api-provider](../libraries/http-api-provider). The application consist of a
[@Controller](src/main/kotlin/ht/eyfout/example/controller/VehiclesController.kt)
and a [@Client](src/main/kotlin/ht/eyfout/example/client/DMVClient.kt) 
that allows to get information about a vehicle and their manufacturer.

Also leverages the power of the [openapi http-api generator](../../libraries/openapi-http-api-generator)
by applying the [gradle plugin](../../plugins/openapi-http-api-plugin).
> plugins {
>       id("ht.eyfout.openapi.http.api")
> }


see [example tests](src/test/kotlin/ht/eyfout/example).

## Setup
These examples are written as a separate project. 

