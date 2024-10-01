package ht.eyfout.example;

import ht.eyfout.junit.jupiter.api.GherkinDynamicTest;
import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import ht.eyfout.junit.jupiter.http.HttpAPIGenerator;
import ht.eyfout.junit.jupiter.http.Take2;
import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitHttpAPI;
import ht.eyfout.junit.jupiter.http.generated.GherkinJUnitRequestBuilder;
import org.junit.jupiter.api.Test;

import java.net.URL;

public class HttpAPIGeneratorTests {

    @Test
    void generate() {
        GherkinDynamicTest.dynamicTest(new HttpAPIStateScopeProvider<>() {
            @Override
            public GivenState givenState() {
                return null;
            }
        }).given("", it ->{}).when("", it ->{

            it.httpRequest(new GherkinJUnitHttpAPI<GherkinJUnitRequestBuilder.GherkinJUnitHeaderParam, GherkinJUnitRequestBuilder.GherkinJUnitPathParam, GherkinJUnitRequestBuilder.GherkinJUnitQueryParam>())
                    .header(h -> {

                    }).pathParams(p ->{

                    }).queryParams(q ->{

                    });

        });
        URL resource = HttpAPIGeneratorTests.class.getClassLoader().getResource("swagger.yml");
//        new HttpAPIGenerator().generate(resource);
        Take2.generate(resource);
    }
}
