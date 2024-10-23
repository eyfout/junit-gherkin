package ht.eyfout.dmv.http

import ht.eyfout.example.client.dmv.DMVClient
import ht.eyfout.example.client.dmv.Vehicle
import ht.eyfout.example.client.dmv.VehicleManufacturer
import ht.eyfout.junit.jupiter.gherkin.api.GivenState
import io.micronaut.http.HttpResponse
import io.mockk.every

data class ClientGivenState(
    private val state: MutableMap<String, Any> = mutableMapOf(),
    val client: DMVClient
) : GivenState() {
    fun GETVehiclesAnswer(
        authorization: String?,
        manufacturerID: String?,
        vehicles: () -> HttpResponse<Collection<Vehicle>>
    ) {
        every {
            client.vehicles(authorization ?: any(), manufacturerID ?: any())
        } returns vehicles.invoke()
    }

    fun GETManufacturerAnswer(
        authorization: String?,
        manufacturers: () -> HttpResponse<Collection<VehicleManufacturer>>
    ) {
        every {
            client.carManufacturers(authorization ?: any())
        } returns manufacturers.invoke()
    }

    fun copy(): ClientGivenState {
        return ClientGivenState(state.toMutableMap(), client)
    }

    override fun asMap(): MutableMap<String, Any> {
        return state.toMutableMap();
    }
}