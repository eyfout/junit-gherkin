package ht.eyfout.pets.http

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpStateScopeProvider
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpThenScope
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpWhenScope
import jakarta.inject.Inject

class PetsStateScopeProvider(@Inject val client: PetClient) :
    HttpStateScopeProvider<PetGivenState, HttpWhenScope, HttpThenScope>() {
    override fun givenState(): PetGivenState {
        return PetGivenState(client)
    }
}