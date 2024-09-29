## JUnit Gherkin
An extension to the JUnit's
[Dynamic Test](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)
that allows you to write tests using Gherkin syntax.

```java
@TestFactory
Stream<DynamicTest> inventory() {
    return GherkinDynamicTest.dynamicTest(provider)
            .given("vehicle inventory", it -> { })
            .when("GET vehicles for Nissan", it -> { })
            .when("GET Altima and Rogue", it -> { })
            .then("contains Altima, Rogue", it -> { });
}
```
## Getting Started
To use this library you **MUST** implement
- [StateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/api/StateScopeProvider.java)

see [rest-assured http](../rest-assured-http-scopes) implementation for microservice testing.