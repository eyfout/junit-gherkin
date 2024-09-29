package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;

public interface HttpAPI<B> {
    String getHttpMethod();

    String getBasePath();

    String getDescription();

    default <B extends HttpAPIRequestExecutor> B executor(HttpAPIRequestBuilder builder, GivenState givenState) {
        return (B) new HttpAPIRequestExecutor(this, builder, givenState);
    }
}
