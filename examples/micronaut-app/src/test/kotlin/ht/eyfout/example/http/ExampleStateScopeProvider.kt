package ht.eyfout.example.http

import ht.eyfout.example.client.DMVClient
import ht.eyfout.junit.jupiter.api.http.HttpAPIStateScopeProvider
import ht.eyfout.junit.jupiter.api.http.HttpAPIThenScope
import ht.eyfout.junit.jupiter.api.http.HttpAPIWhenScope
import jakarta.inject.Singleton

@Singleton
class ExampleStateScopeProvider(val client: DMVClient) :
    HttpAPIStateScopeProvider<ExampleGivenState, HttpAPIWhenScope, HttpAPIThenScope>() {
    override fun givenState(): ExampleGivenState {
        return ExampleGivenState(client = client)
    }
}