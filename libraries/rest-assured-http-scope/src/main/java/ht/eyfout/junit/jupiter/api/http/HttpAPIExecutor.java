package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;

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

    @Override
    public <R> R exec() {
        if (httpResponse == null) {
            httpResponse = RestAssured.given()
                    .basePath(api.getBasePath())
                    .headers(builder.headers)
                    .queryParams(builder.queryParams)
                    .pathParams(builder.pathParams)
                    .request(Method.valueOf(api.getHttpMethod().toUpperCase()));

        }
        return (R) httpResponse;
    }
}
