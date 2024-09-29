package ht.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import ht.eyfout.junit.jupiter.api.http.HttpAPIThenScope;

public class ExampleStateScopeProvider extends HttpAPIStateScopeProvider<ExampleGivenState, ExampleWhenScope, HttpAPIThenScope> {
    public static HttpAPIStateScopeProvider<ExampleGivenState, ExampleWhenScope, HttpAPIThenScope> INSTANCE = new ExampleStateScopeProvider();

    @Override
    public ExampleGivenState givenState() {
        return new ExampleGivenState();
    }

    @Override
    public ExampleWhenScope whenScope(ExampleGivenState givenState) {
        return new ExampleWhenScope(givenState);
    }
}
