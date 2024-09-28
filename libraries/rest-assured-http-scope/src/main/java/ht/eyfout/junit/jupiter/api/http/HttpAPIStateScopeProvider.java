package ht.eyfout.junit.jupiter.api.http;

import ht.eyfout.junit.jupiter.api.GivenState;
import ht.eyfout.junit.jupiter.api.StateScopeProvider;
import ht.eyfout.junit.jupiter.api.ThenScope;
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor;

public abstract class HttpAPIStateScopeProvider<Given extends GivenState> implements StateScopeProvider<Given, HttpAPIWhenScope, HttpAPIThenScope> {
    public HttpAPIStateScopeProvider(){

    }
    @Override
    public HttpAPIWhenScope whenScope(GivenState givenState) {
        return new HttpAPIWhenScope();
    }

    @Override
    public HttpAPIThenScope thenScope(WhenScopeExecutor executor) {
        return new HttpAPIThenScope(executor);
    }
}
