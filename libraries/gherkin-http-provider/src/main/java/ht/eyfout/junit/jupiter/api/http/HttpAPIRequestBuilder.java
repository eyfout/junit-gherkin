package ht.eyfout.junit.jupiter.api.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpAPIRequestBuilder {
    Map<String, Object> headers = new HashMap<>();
    Map<String, Object> queryParams = new HashMap<>();
    Map<String, Object> pathParams = new HashMap<>();
    Optional<Object> body = Optional.empty();

    public HttpAPIRequestBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpAPIRequestBuilder queryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public HttpAPIRequestBuilder pathParam(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    public HttpAPIRequestBuilder body(Object body) {
        this.body = Optional.ofNullable(body);
        return this;
    }
}
