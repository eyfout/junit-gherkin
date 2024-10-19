package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;

import java.util.Optional;

public interface HttpAPI<B extends HttpAPIRequestBuilder> {
    String getHttpMethod();

    String getBasePath();

    Optional<String> getDescription();

    default B builder(){
        return builder(null);
    }

    @SuppressWarnings("unchecked")
    default B builder(GivenState givenState){
        return (B)new HttpAPIRequestBuilder(this, givenState);
    }

    @SuppressWarnings("unchecked")
    default <Exec extends HttpAPIRequestExecutor> Exec executor(HttpAPIRequestBuilder builder) {
        return (Exec) new HttpAPIRequestExecutor(this, builder);
    }
}
