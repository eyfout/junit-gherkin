package ht.eyfout.dmv.http

import ht.eyfout.example.client.dmv.DMVClient
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIStateScopeProvider
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIThenScope
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIWhenScope
import jakarta.inject.Singleton

@Singleton
class ExampleStateScopeProvider(private val client: DMVClient) :
    HttpAPIStateScopeProvider<ClientGivenState, HttpAPIWhenScope, HttpAPIThenScope>() {
    override fun givenState(): ClientGivenState {
        return ClientGivenState(client = client)
    }
}