package ht.eyfout.pets.http

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIStateScopeProvider
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIThenScope
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIWhenScope
import jakarta.inject.Inject

class PetsStateScopeProvider(@Inject val client: PetClient) :
    HttpAPIStateScopeProvider<PetGivenState, HttpAPIWhenScope, HttpAPIThenScope>() {
    override fun givenState(): PetGivenState {
        return PetGivenState(client)
    }
}