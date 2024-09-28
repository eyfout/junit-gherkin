package com.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GherkinDynamicTest;
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExampleTests {

    @Test
    void doIt(){
        assertEquals("Yes", "No");
    }

    @TestFactory
    Stream<DynamicTest> plus(){
       return List.of(DynamicTest.dynamicTest("do it dynamic", ()->{
           doIt();
       })).stream() ;
    }
    @TestFactory
    Stream<DynamicTest> api() {
        return  GherkinDynamicTest.dynamicTest(new ExampleStateScopeProvider())
                .given("a workspace", it -> {
                    it.isolationID = "iso";
                    it.applicationID = "application";
                }).when("query", it -> {
                    it.request(new ExampleHttpAPI())
                            .queryParam("from", "apple")
                            .header("Keep-Alive", "timeout=4000");
                }).then("exec", it -> {
                    assertEquals(200,  it.httpResponse().statusCode());
                });

    }
}
