## JUnit Gherkin
An extension to the JUnit's
[Dynamic Test](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)
that allows you to write tests using Gherkin syntax.


> ```java
> @TestFactory
> Stream<DynamicTest> inventory() {
>    return GherkinDynamicTest.dynamicTest(provider)
>            .given("vehicle inventory", it -> { })
>            .fork( 
>                    scenario -> scenario.when("GET vehicles for Nissan", it -> { })
>                        .when("GET Altima and Rogue", it -> { })
>                        .then("contains Altima, Rogue", it -> { }),
>                    scenario -> scenario.when("GET vehicles for Infinity", it -> { })
>                        .then("contains z400", it -> { })
>            );
>}
>```
> *For a given inventory of vehicles, you can test multiple scenarios by composing the `when`  api. 
> To accomodate scenarios that have different expectations `fork` the `given` state for each scenario.*

## Getting Started
To use this library you **MUST** implement the
[StateScopeProvider](src/main/java/ht/eyfout/junit/jupiter/gherkin/api/StateScopeProvider.java).
As an example see [http-api-provider](../http-api-provider), which
adds support for testing **HTTP** endpoints.
