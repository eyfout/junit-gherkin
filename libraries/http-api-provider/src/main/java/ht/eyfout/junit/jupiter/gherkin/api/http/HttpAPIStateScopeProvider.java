package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.StateScopeProvider;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;

public abstract class HttpAPIStateScopeProvider<Given extends GivenState, When extends HttpAPIWhenScope, Then extends HttpAPIThenScope> implements StateScopeProvider<Given, When, Then> {
    public HttpAPIStateScopeProvider() {

    }

    @Override
    public Given givenState() {
        return (Given)new GivenState();
    }

    @Override
    public When whenScope(Given givenState) {
        return (When) new HttpAPIWhenScope(givenState);
    }

    @Override
    public Then thenScope(WhenScopeExecutor executor) {
        return (Then) new HttpAPIThenScope(executor);
    }
}
