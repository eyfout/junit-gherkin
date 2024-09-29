package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.StateScopeProvider;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;

public abstract class HttpAPIStateScopeProvider<Given extends GivenState, When extends HttpAPIWhenScope, Then extends HttpAPIThenScope> implements StateScopeProvider<Given, HttpAPIWhenScope, HttpAPIThenScope> {
    public HttpAPIStateScopeProvider() {

    }

    @Override
    public HttpAPIWhenScope whenScope(Given givenState) {
        return new HttpAPIWhenScope(givenState);
    }

    @Override
    public HttpAPIThenScope thenScope(WhenScopeExecutor executor) {
        return new HttpAPIThenScope(executor);
    }
}
