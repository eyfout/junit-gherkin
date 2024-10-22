package ht.eyfout.junit.jupiter.gherkin.api.http;

import ht.eyfout.junit.jupiter.gherkin.api.GivenState;
import ht.eyfout.junit.jupiter.gherkin.api.StateScopeProvider;
import ht.eyfout.junit.jupiter.gherkin.api.WhenScopeExecutor;

public abstract class HttpStateScopeProvider<Given extends GivenState, When extends HttpWhenScope, Then extends HttpThenScope> implements StateScopeProvider<Given, When, Then> {
    public HttpStateScopeProvider() {

    }

    @Override
    public Given givenState() {
        return (Given) new GivenState();
    }

    @Override
    public When whenScope(Given givenState) {
        return (When) new HttpWhenScope(givenState);
    }

    @Override
    public Then thenScope(WhenScopeExecutor executor) {
        return (Then) new HttpThenScope(executor);
    }
}
