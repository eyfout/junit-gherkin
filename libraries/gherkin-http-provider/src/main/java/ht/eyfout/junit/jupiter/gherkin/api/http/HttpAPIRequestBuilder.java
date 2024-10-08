package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpAPIRequestBuilder {
    final private Map<String, Object> headers = new HashMap<>();
    final private Map<String, Object> queryParams = new HashMap<>();
    final private Map<String, Object> pathParams = new HashMap<>();
    private Optional<Object> body = Optional.empty();
    final private Optional<GivenState> givenState;
    final HttpAPI<? extends HttpAPIRequestBuilder> api;
    private List<Consumer<HttpAPIRequestBuilder>> requiredChecks = new ArrayList<>();

    public HttpAPIRequestBuilder(HttpAPI<? extends HttpAPIRequestBuilder> api, GivenState givenState) {
        this.api = api;
        this.givenState = Optional.ofNullable(givenState);
    }

    public HttpAPIRequestBuilder header(String key, Object value) {
        headers.put(key, value);
        return this;
    }

    public HttpAPIRequestBuilder queryParam(String key, Object value) {
        queryParams.put(key, value);
        return this;
    }

    public HttpAPIRequestBuilder pathParam(String key, Object value) {
        pathParams.put(key, value);
        return this;
    }

    public Object pathParam(String key) {
        return pathParams.get(key);
    }

    public Object queryParam(String key) {
        return queryParams.get(key);
    }

    public Object header(String key) {
        return header(key);
    }

    public HttpAPIRequestBuilder body(Object body) {
        this.body = Optional.ofNullable(body);
        return this;
    }

    Map<String, Object> getPathParams() {
        Map<String, Object> params = pathParamFromGiven();
        params.putAll(pathParams);
        return params;
    }

    public void requireThat(Consumer<HttpAPIRequestBuilder> requiredCheck) {
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

    Map<String, Object> getQueryParams() {
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
    <Exec extends HttpAPIRequestExecutor> Exec execute() {
        return (Exec) new HttpAPIRequestExecutor(api, this);
    }
}
