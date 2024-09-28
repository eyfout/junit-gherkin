package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.WhenScope;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;

import java.util.*;
import java.util.stream.Stream;

public class HttpAPIWhenScope extends WhenScope {
    protected List<Map.Entry<HttpAPI[], HttpAPIBuilder>> httpRequests = new ArrayList<>();

    public HttpAPIBuilder request(HttpAPI... api) {
        HttpAPIBuilder builder = new HttpAPIBuilder();
        httpRequests.add(new AbstractMap.SimpleEntry(api, builder));
        return builder;
    }

    @Override
    public Stream<WhenScopeExecutor> scopeExecutor(GivenState given) {
        return httpRequests.stream().flatMap(requests ->
                Arrays.stream(requests.getKey()).map(api -> new HttpAPIExecutor(api, requests.getValue(), given))
        );
    }
}
