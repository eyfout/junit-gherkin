package ht.eyfout.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpRequestBuilder {
    public final HttpEndpoint<? extends HttpRequestBuilder> api;
    final private Map<String, Object> headers = new HashMap<>();
    final private Map<String, Object> queryParams = new HashMap<>();
    final private Map<String, Object> pathParams = new HashMap<>();
    final private Optional<GivenState> givenState;
    private Optional<Object> body = Optional.empty();
    private final List<Consumer<HttpRequestBuilder>> requiredChecks = new ArrayList<>();

    public HttpRequestBuilder(HttpEndpoint<? extends HttpRequestBuilder> api, GivenState givenState) {
        this.api = api;
        this.givenState = Optional.ofNullable(givenState);
    }

    public HttpRequestBuilder header(String key, Object value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequestBuilder queryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }

    public HttpRequestBuilder pathParam(String key, Object value) {
        pathParams.put(key, value);
        return this;
    }

    public <R> R pathParam(String key) {
        return (R) pathParams.get(key);
    }

    public <R> R queryParam(String key) {
        return (R) queryParams.get(key);
    }

    public <R> R header(String key) {
        return header(key);
    }

    public HttpRequestBuilder body(Object body) {
        this.body = Optional.ofNullable(body);
        return this;
    }

    public Map<String, Object> getPathParams() {
        Map<String, Object> params = pathParamFromGiven();
        params.putAll(pathParams);
        return params;
    }

    public void requireThat(Consumer<HttpRequestBuilder> requiredCheck) {
        this.requiredChecks.add(requiredCheck);
    }

    public void enforceRequired() {
        if (!requiredChecks.isEmpty()) {
            requiredChecks.forEach(it -> it.accept(this));
        }
    }


    Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueryParams() {
        return queryParams;
    }

    public Optional<Object> getBody() {
        return body;
    }

    private Map<String, Object> pathParamFromGiven() {
        Map<String, Object> given = new HashMap<>();
        givenState.ifPresent(it -> given.putAll(it.asMap()));

        return Arrays.stream(api.getBasePath().split("/"))
                .filter(it -> it.startsWith("{") && it.endsWith("}"))
                .map(it -> it.substring(1, it.length() - 1))
                .map(it -> new AbstractMap.SimpleEntry<>(it, given.get(it)))
                .filter(it -> it.getValue() != null)
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
    }

    @SuppressWarnings("unchecked")
    <Exec extends HttpRequestExecutor> Exec execute() {
        return (Exec) new HttpRequestExecutor(api, this);
    }
}
