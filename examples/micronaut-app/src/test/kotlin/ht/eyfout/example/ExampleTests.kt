package ht.eyfout.example

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.client.Vehicle
import ht.eyfout.example.client.VehicleManufacturer
import ht.eyfout.example.http.ExampleHttpAPI
import ht.eyfout.example.http.ExampleStateScopeProvider
import ht.eyfout.junit.jupiter.api.GherkinDynamicTest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory

@MicronautTest
class ExampleTests {
    @MockBean(DMVClient::class)
    fun dmvClient(): DMVClient = mockk()
    @Inject
    lateinit var provider: ExampleStateScopeProvider

    @TestFactory
    fun `nissan inventory`() = GherkinDynamicTest.dynamicTest(provider)
        .given("an inventory of vehicles by manufacturer") {
            it.GETVehiclesAnswer(
                authorization = "eyfout",
                manufacturerID = "Nissan-#1") {
                HttpResponse.ok(listOf(
                    Vehicle("Vehicle#1", "Nissan-#1", "Maxima"),
                    Vehicle("Vehicle#1", "Nissan-#1", "Altima")
                ))
            }

            it.GETManufacturerAnswer(authorization = "eyfout") {
                HttpResponse.ok(listOf(
                    VehicleManufacturer("Nissan-#1", "Nissan", "1968"),
                    VehicleManufacturer("Ford-#2", "Ford", "1955"),
                    VehicleManufacturer("Subaru-#3", "Subaru", "1934")
                ))
            }

        }.`when`("GET Nissan vehicles") {
            it.httpRequest(ExampleHttpAPI.INSTANCE)
                .queryParam("make", "Nissan")
                .header("Authorization", "eyfout")
        }.then("Altima and Maxima") {

            assertEquals(HttpStatus.OK.code, it.httpResponse().statusCode())
            val vehicles = it.httpResponse().body().`as`(Array<Vehicle>::class.java)
            assertEquals(2, vehicles.size);
            assertEquals(setOf("Maxima", "Altima"), vehicles.map { it.model }.toSet())
        }
}