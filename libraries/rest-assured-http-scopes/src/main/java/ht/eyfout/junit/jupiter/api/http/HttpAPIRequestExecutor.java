package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.net.ConnectException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

final public class HttpAPIRequestExecutor implements WhenScopeExecutor {
    private final HttpAPI api;
    private final HttpAPIRequestBuilder builder;
    private final GivenState givenState;
    private Response httpResponse;

    HttpAPIRequestExecutor(HttpAPI api, HttpAPIRequestBuilder builder, GivenState givenState) {
        this.api = api;
        this.builder = builder;
        this.givenState = givenState;
    }


    private Map<String, Object> pathParams() {
        Map<String, Object> given = givenState.asMap();
        return Arrays.stream(api.getBasePath().split("/"))
                .filter(it -> it.startsWith("{") && it.endsWith("}"))
                .map(it -> it.substring(1, it.length() - 1))
                .map(it -> new AbstractMap.SimpleEntry<String, Object>(it, given.get(it)))
                .filter(it -> it.getValue() != null)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @Override
    public Optional<String> displayName() {

        String queryParams = "?";
        if (!builder.queryParams.isEmpty()) {
            queryParams += builder.queryParams.entrySet().stream()
                    .map(it -> it.getKey() + "=" + it.getValue())
                    .collect(Collectors.joining("&"));
        }

        return Optional.of(api.getHttpMethod().toUpperCase() + " " + api.getBasePath() + queryParams);
    }

    @Override
    final public <R> R exec() {
        if (httpResponse == null) {
            Map<String, Object> pathParams = pathParams();
            pathParams.putAll(builder.pathParams);
            try {
                RequestSpecification spec = RestAssured.given()
                        .basePath(api.getBasePath())
                        .headers(builder.headers)
                        .queryParams(builder.queryParams)
                        .pathParams(pathParams);
                builder.body.ifPresent(spec::body);
                httpResponse = spec.request(Method.valueOf(api.getHttpMethod().toUpperCase()));
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
