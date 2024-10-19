package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.net.ConnectException;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpAPIRequestExecutor implements WhenScopeExecutor {
    private final HttpAPI<?> api;
    private final HttpAPIRequestBuilder builder;
    private Response httpResponse;

    HttpAPIRequestExecutor(HttpAPI<?> api, HttpAPIRequestBuilder builder) {
        this.api = api;
        this.builder = builder;
    }


    @Override
    public Optional<String> displayName() {
        StringBuilder sb = new StringBuilder();

        api.getDescription().ifPresent(it -> sb.append(" [[").append(it).append(" ]] => "));

        String queryParams = "?";
        if (!builder.getQueryParams().isEmpty()) {
            queryParams += builder.getQueryParams().entrySet().stream()
                    .map(it -> it.getKey() + "=" + it.getValue())
                    .collect(Collectors.joining("&"));
        }

        sb.append(api.getHttpMethod().toUpperCase())
                .append(" ")
                .append(api.getBasePath())
                .append(queryParams);
        return Optional.of(sb.toString());
    }

    final protected RequestSpecification specification() {
        RequestSpecification spec = RestAssured.given()
                .basePath(api.getBasePath())
                .headers(builder.getHeaders())
                .queryParams(builder.getQueryParams())
                .pathParams(builder.getPathParams());
        builder.getBody().ifPresent(spec::body);
        return spec;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> R exec() {
        if (httpResponse == null) {
            try {
                builder.enforceRequired();
                httpResponse = specification().request(Method.valueOf(api.getHttpMethod().toUpperCase()));
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
