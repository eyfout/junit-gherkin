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

    override fun <B : HttpRequestBuilder?, R : Any?> match(endpoint: HttpEndpoint<B>?,
                                                           httpRequest: B,
                                                           httpResponse: Supplier<R>?) {
        when(endpoint){
            else -> throw IllegalStateException(javaClass.name + "#match does not support" + httpRequest)
        }
    }


    override fun asMap(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }
}