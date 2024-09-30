package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;

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
        return (B)new HttpAPIRequestBuilder((HttpAPI<HttpAPIRequestBuilder>) this, givenState);
    }

    @SuppressWarnings("unchecked")
    default <Exec extends HttpAPIRequestExecutor> Exec executor(HttpAPIRequestBuilder builder, GivenState givenState) {
        return (Exec) new HttpAPIRequestExecutor(this, builder, givenState);
    }
}
