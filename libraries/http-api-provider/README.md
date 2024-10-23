## RestAssured [StateScopeProvider]() 
Using ```io.rest-assured:rest-assured``` this project builds on
 [junit-gherkin](../junit-gherkin) to provide a baseline framework for
 testing a **HTTP** endpoints, which is works exceptional well when 
developing microservices.

The [HttpStateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpStateScopeProvider.java)
makes available the following:
- [HttpGivenState](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpGivenState.java)
  - exposes `httpRequest ( HttpEndpoint ).respondsWith( Object )` to allow you to mock
  the response from provider services your service relies on.
- [HttpWhenScope](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpWhenScope.java)
  -  exposes ```httpRequest( HttpEndpoint )``` for lazily building requests 
- [HttpThenScope](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpThenScope.java)
  - exposes ```httpResponse()``` for invoking the request

The [HttpGivenState]() is dependent on the feature under test, 
consumers of this library are expected to extend it when making available via the
[HttpStateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpStateScopeProvider.java).

```java
@TestFactory
Stream<DynamicTest> inventory() {
    return GherkinDynamicTest.dynamicTest(provider)
            .given("vehicle inventory", g -> {                                               (1)
                g.httpRequest( providerEndpoint, request ->{                                 (2)
                    request.queryParams("Authorization", "eyfout");
                }).respondsWith( it -> HttpResponse.ok());                 
            }).when("GET vehicles for Nissan", w -> {                                        (3)
                w.httpRequest(targetEndpoint).queryParams("Authorization", "eyfout");        (4)
            }).then("contains Altima, Rogue", t -> {                                         (5)
                assertEquals(200, t.httpResponse().statusCode());
            });
}
```
- **(1)** Start of define the prerequisite state for the test to execute, **given**. 
- **(2)** Mock of the request used internally by **(4)** to satisfy the expected response **(5)**
- **(3)** Start of test case
- **(4)** Endpoint under test
- **(5)** expected response for **(4)**

In the above example *targetEndpoint* and *providerEndpoint* are both implementations
of [HttpEndpoint](src/main/java/ht/eyfout/http/HttpEndpoint.java).
[HttpEndpoint](src/main/java/ht/eyfout/http/HttpEndpoint.java) contains all
required information to make a request to a server, including Http Method and URI. 
To expedite the creation of HttpEndpoint, see [http api generator](../openapi-http-api-generator/),
it generates a HttpEndpoint for each path captured in an OpenAPI specification.