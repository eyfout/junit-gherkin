package ht.eyfout.dmv.http

import ht.eyfout.example.client.dmv.DMVClient
import ht.eyfout.junit.jupiter.gherkin.api.http.*
import jakarta.inject.Singleton

@Singleton
class DMVStateScopeProvider(private val client: DMVClient) :
    HttpStateScopeProvider<ClientGivenState, HttpWhenScope, HttpThenScope>() {
    override fun givenState(): ClientGivenState {
        return ClientGivenState(client = client)
    }
}