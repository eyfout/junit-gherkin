package com.eyfout.example.http;

import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider;
import ht.eyfout.junit.jupiter.api.http.HttpAPIWhenScope;

public class ExampleStateScopeProvider extends HttpAPIStateScopeProvider<ExampleGivenState> {
    @Override
    public ExampleGivenState givenState() {
        return new ExampleGivenState();
    }
}
