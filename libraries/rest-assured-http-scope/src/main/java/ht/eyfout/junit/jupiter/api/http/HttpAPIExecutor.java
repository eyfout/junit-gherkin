package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.net.ConnectException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

final public class HttpAPIExecutor implements WhenScopeExecutor {
    private final HttpAPI api;
    private final HttpAPIBuilder builder;
    private final GivenState givenState;
    private Response httpResponse;

    HttpAPIExecutor(HttpAPI api, HttpAPIBuilder builder, GivenState givenState) {
        this.api = api;
        this.builder = builder;
        this.givenState = givenState;
    }


    Map<String, Object> pathParams() {
        Map<String, Object> given = givenState.asMap();
        return Arrays.stream(api.getBasePath().split("/"))
                .filter(it -> it.startsWith("{") && it.endsWith("}"))
                .map(it -> it.substring(1, it.length() - 1))
                .map(it -> new AbstractMap.SimpleEntry<String, Object>(it, given.get(it)))
                .filter(it -> it.getValue() != null)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public <R> R exec() {
        if (httpResponse == null) {
            Map<String, Object> pathParams = pathParams();
            pathParams.putAll(builder.pathParams);
            try {
                httpResponse = RestAssured.given()
                        .basePath(api.getBasePath())
                        .headers(builder.headers)
                        .queryParams(builder.queryParams)
                        .pathParams(pathParams)
                        .request(Method.valueOf(api.getHttpMethod().toUpperCase()));
            } catch (Throwable e) {
                if (e instanceof ConnectException) {
                    throw new IllegalStateException("Unable to connect to " + api.toString(), e);
                }
                throw e;

            }
        }
        return (R) httpResponse;
    }
}
