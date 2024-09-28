package com.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GherkinDynamicTest;
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public class ExampleTests {


    @TestFactory
    Stream<DynamicTest> api() {
        return GherkinDynamicTest.dynamicTest(new ExampleStateScopeProvider())
                .given("a workspace", it -> {
                    it.isolationID = "iso";
                }).when("query", it -> {
                    it.request()
                            .queryParam("from", "apple")
                            .header("Keep-Alive", "timeout=4000");
                }).then("exec", it -> {
                    it.httpResponse();
                });
    }
}
