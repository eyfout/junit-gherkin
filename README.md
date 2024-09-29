# JUnit Gerkin

JUnit Gerkin is a framework built on JUnit that allows users to write JUnit tests in the form of

```
given()
    when()
        then()
```

Also known as Gherkin syntax. It is built using JUnit's DynamicTest feature, which enables
you to programmatically create tests. In order to execute these tests
you **MUST** use the *@TestFactory* annotation,
see [dynamic tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)
for more information.

The following is a psuedo-code is an example of how tests can be written using
this framework:

```java

@TestFactory
Stream<DynamicTest> inventory() {
    return GherkinDynamicTest.dynamicTest(provider)
            .given("vehicle inventory", it -> { })
            .when("request for vehicles by make", it -> { })
            .then("Altima, Rogue", it -> { });
}
```
 