package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.http.HttpEndpoint;
import ht.eyfout.http.HttpRequestBuilder;
import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScope;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpWhenScope extends WhenScope {
    final protected List<Map.Entry<HttpEndpoint[], HttpRequestBuilder>> httpRequests = new ArrayList<>();
    protected GivenState givenState;

    public HttpWhenScope(GivenState givenState) {
        this.givenState = givenState;
    }

    final public <B extends HttpRequestBuilder> B httpRequest(HttpEndpoint<B>... api) {
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
                Arrays.stream(requests.getKey()).map(api -> new Executor(api, requests.getValue()))
        );
    }

    private record Executor(HttpEndpoint<?> api,
                            HttpRequestBuilder builder) implements WhenScopeExecutor {
        @Override
        public Optional<String> displayName() {
            StringBuilder sb = new StringBuilder();

            api.getDescription().ifPresent(it -> sb.append(" [[").append(it).append(" ]] => "));

            String queryParams = "?";
            if (!builder.getQueryParams().isEmpty()) {
                queryParams += builder.getQueryParams().entrySet().stream()
                        .map(it -> it.getKey() + "=" + it.getValue())
                        .collect(Collectors.joining("&"));
            }

            sb.append(api.getHttpMethod().toUpperCase())
                    .append(" ")
                    .append(api.getBasePath())
                    .append(queryParams);
            return Optional.of(sb.toString());
        }

        @Override
        public <R> R exec() {
            return api.executor(builder).exec();
        }
    }
}
