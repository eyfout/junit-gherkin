package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;

import java.util.Optional;

public interface HttpAPI<B> {
    String getHttpMethod();

    String getBasePath();

    Optional<String> getDescription();

    @SuppressWarnings("unchecked")
    default <B extends HttpAPIRequestExecutor> B executor(HttpAPIRequestBuilder builder, GivenState givenState) {
        return (B) new HttpAPIRequestExecutor((HttpAPI)this, builder, givenState);
    }
}
