package ht.eyfout.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpGivenState;

import java.util.Optional;

public interface HttpEndpoint<B extends HttpRequestBuilder> {
    String getHttpMethod();

    String getBasePath();

    Optional<String> getDescription();

    default B builder() {
        return builder(null);
    }

    @SuppressWarnings("unchecked")
    default B builder(HttpGivenState givenState) {
        return (B) new HttpRequestBuilder(this, givenState);
    }

    @SuppressWarnings("unchecked")
    default <Exec extends HttpRequestExecutor> Exec executor(HttpRequestBuilder builder) {
        return (Exec) new HttpRequestExecutor(this, builder);
    }
}
