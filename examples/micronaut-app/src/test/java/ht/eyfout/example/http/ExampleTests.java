package ht.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.GherkinDynamicTest;
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import ht.eyfout.junit.jupiter.api.http.HttpAPIThenScope;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@MicronautTest
public class ExampleTests {
    @Inject
    private EmbeddedApplication<?> app;

    @TestFactory
    Stream<DynamicTest> manufacturedByNissan(){
        HttpAPIStateScopeProvider<ExampleGivenState, ExampleWhenScope, HttpAPIThenScope> provider =   ExampleStateScopeProvider.INSTANCE;
        return GherkinDynamicTest.dynamicTest(provider)
                .given("Nissan inventory", it -> {
                }).when("GET Nissan vehicles", it -> {

                }).then("2 vehicles", it ->{

                });
    }
}
