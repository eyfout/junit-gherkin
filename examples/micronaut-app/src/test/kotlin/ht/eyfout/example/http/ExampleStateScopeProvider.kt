package ht.eyfout.example.http

import ht.eyfout.example.client.DMVClient
import ht.eyfout.junit.jupiter.api.WhenScopeExecutor
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider
import ht.eyfout.junit.jupiter.api.http.HttpAPIThenScope
import jakarta.inject.Singleton

@Singleton
class ExampleStateScopeProvider(val client: DMVClient): HttpAPIStateScopeProvider<ExampleGivenState, ExampleWhenScope, HttpAPIThenScope>() {

    override fun givenState(): ExampleGivenState {
        return ExampleGivenState(client = client);
    }

    override fun thenScope(executor: WhenScopeExecutor?): HttpAPIThenScope {
        TODO("Not yet implemented")
    }

    override fun whenScope(givenState: ExampleGivenState?): ExampleWhenScope {
        TODO("Not yet implemented")
    }

}