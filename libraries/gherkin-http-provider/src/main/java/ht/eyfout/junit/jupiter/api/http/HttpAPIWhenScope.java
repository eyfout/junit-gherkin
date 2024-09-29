package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.WhenScope;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;

import java.util.*;
import java.util.stream.Stream;

public class HttpAPIWhenScope extends WhenScope {
    final protected List<Map.Entry<HttpAPI[], HttpAPIRequestBuilder>> httpRequests = new ArrayList<>();
    protected GivenState givenState;

    public HttpAPIWhenScope(GivenState givenState) {
        this.givenState = givenState;
    }

    final public <B extends HttpAPIRequestBuilder> B httpRequest(HttpAPI<B>... api) {
        if (api.length == 0) {
            throw new IllegalArgumentException("at least one Http API is required.");
        }
        B builder = api[0].builder();
        httpRequests.add(new AbstractMap.SimpleEntry<>(api, builder));
        return builder;
    }

    @Override
    final public Stream<WhenScopeExecutor> scopeExecutor(GivenState given) {
        return httpRequests.stream().flatMap(requests ->
                Arrays.stream(requests.getKey()).map(api -> api.executor(requests.getValue(), given))
        );
    }
}
