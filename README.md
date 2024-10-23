[![Build Status](https://github.com/eyfout/junit-gherkin/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/eyfout/junit-gherkin/actions)

# JUnit Gerkin
JUnit Gerkin is a framework built on JUnit that allows users to write JUnit tests in the form of

```
given("a known state")
    when("perform some action")
        then("expectation are meant")
```

Also known as Gherkin syntax. It is built using JUnit's DynamicTest feature, which enables
you to programmatically create tests. In order to execute these tests
you **MUST** use the *@TestFactory* annotation,
see [dynamic tests](https://junit.org/junit5/docs/current/user-guide/#writing-tests-dynamic-tests)
for more information on dynamic tests.

## Getting Started
To build this repository use
> ./gradlew build -Pno-examples

by default all examples are executed as part of the build.