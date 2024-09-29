package ht.eyfout.example.http

import ht.eyfout.junit.jupiter.api.GivenState
import ht.eyfout.junit.jupiter.api.http.HttpAPIWhenScope

class ExampleWhenScope(givenState: GivenState): HttpAPIWhenScope(givenState) {

    val authorization: String
        get() = (givenState as ExampleGivenState).authorization
    val manufacturerID: String
        get() = (givenState as ExampleGivenState).manufacturerID
    val vehicleID: String
        get() = (givenState as ExampleGivenState).vehicleID

}