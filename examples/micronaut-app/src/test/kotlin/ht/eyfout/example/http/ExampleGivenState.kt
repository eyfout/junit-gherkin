package ht.eyfout.example.http

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.client.Vehicle
import ht.eyfout.example.client.VehicleManufacturer
import ht.eyfout.junit.jupiter.api.GivenState
import io.micronaut.http.HttpResponse
import io.mockk.every

data class ExampleGivenState(
    private val state: MutableMap<String, Any> = mutableMapOf(),
    val client: DMVClient
) : GivenState() {
    fun GETVehiclesAnswer(
        authorization: String,
        manufacturerID: String,
        vehicles: () -> HttpResponse<Collection<Vehicle>>
    ) {
        every {
            client.vehicles(authorization, manufacturerID)
        } returns vehicles.invoke()
    }

    fun GETManufacturerAnswer(
        authorization: String,
        manufacturers: () -> HttpResponse<Collection<VehicleManufacturer>>
    ) {
        every {
            client.carManufacturers(authorization)
        } returns manufacturers.invoke()
    }

    override fun copy(): ExampleGivenState {
        return ExampleGivenState(state.toMutableMap(), client)
    }

    override fun asMap(): MutableMap<String, Any> {
        return state.toMutableMap();
    }
}