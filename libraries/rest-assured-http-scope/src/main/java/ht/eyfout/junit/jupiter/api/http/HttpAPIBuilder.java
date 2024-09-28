package ht.eyfout.junit.jupiter.api.http;

import java.util.HashMap;
import java.util.Map;

public class HttpAPIBuilder {
    Map<String, Object> headers = new HashMap<>();
    Map<String, Object> queryParams = new HashMap<>();
    Map<String, Object> pathParams = new HashMap<>();
    Object body;

    public HttpAPIBuilder header(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpAPIBuilder queryParam(String key, String value) {
        queryParams.put(key, value);
        return this;
    }

    public HttpAPIBuilder pathParam(String key, String value) {
        pathParams.put(key, value);
        return this;
    }

    public HttpAPIBuilder body(Object body) {
        this.body = body;
        return this;
    }
}
