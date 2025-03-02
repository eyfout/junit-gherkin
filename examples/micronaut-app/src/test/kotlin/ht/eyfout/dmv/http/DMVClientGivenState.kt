package ht.eyfout.dmv.http

import ht.eyfout.example.client.dmv.DMVClient
import ht.eyfout.example.client.dmv.Vehicle
import ht.eyfout.example.client.dmv.VehicleManufacturer
import ht.eyfout.http.HttpEndpoint
import ht.eyfout.http.HttpRequestBuilder
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpGivenState
import io.micronaut.http.HttpResponse
import io.mockk.every
import java.util.function.Supplier

class DMVClientGivenState(
    private val state: MutableMap<String, Any> = mutableMapOf(),
    val client: DMVClient
) : HttpGivenState() {

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

    fun copy(): DMVClientGivenState {
        return DMVClientGivenState(state.toMutableMap(), client)
    }

    override fun <B : HttpRequestBuilder?, R : Any?> match(p0: HttpEndpoint<B>?, p1: B, p2: Supplier<R>?) {
        when(p0){
            else -> throw IllegalStateException(javaClass.name + "#match does not support" + p1)
        }
    }


    override fun asMap(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }
}