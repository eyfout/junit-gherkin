## rest-assured [StateScopeProvider]() 
Using ```io.rest-assured:rest-assured``` this project builds on
 [junit-gherkin](../junit-gherkin) to provide a baseline framework for
 testing a microservices via Http.

The [HttpAPIStateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpAPIStateScopeProvider.java)
makes available the following:
- [HttpAPIWhenScope](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpAPIWhenScope.java)
  -  exposes ```httpRequest(HttpAPI)``` for lazily building requests 
- [HttpAPIThenScope](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpAPIThenScope.java)
  - exposes ```httpResponse()``` for invoking the request

As [GivenState]() is dependent on the feature under test, consumers of this library are expected to implement
it and making available via the
[HttpAPIStateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/api/http/HttpAPIStateScopeProvider.java).

```java
@TestFactory
Stream<DynamicTest> inventory() {
    return GherkinDynamicTest.dynamicTest(provider)
            .given("vehicle inventory", it -> { })
            .when("GET vehicles for Nissan", it -> { 
                it.httpRequest(targetEndpoint)
                        .queryParams("Authorization", "eyfout");
            })
            .then("contains Altima, Rogue", it -> { 
                assertEquals(200, it.httpResponse().statusCode());
            });
}
```
 