package ht.eyfout.http;

import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.net.ConnectException;

public class HttpRequestExecutor implements WhenScopeExecutor {
    private final HttpEndpoint<?> api;
    private final HttpRequestBuilder builder;
    private Response httpResponse;

    HttpRequestExecutor(HttpEndpoint<?> api, HttpRequestBuilder builder) {
        this.api = api;
        this.builder = builder;
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
