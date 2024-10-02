package ht.eyfout.example

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.client.Vehicle
import ht.eyfout.example.client.VehicleManufacturer
import ht.eyfout.example.http.ControllerAPI
import ht.eyfout.example.http.ExampleStateScopeProvider
import ht.eyfout.junit.jupiter.gherkin.api.GherkinDynamicTest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory

@MicronautTest
class ForkStateDiffExpectationTests {

    @MockBean(DMVClient::class)
    val dmvClient: DMVClient = mockk()

    @Inject
    lateinit var provider: ExampleStateScopeProvider

    @TestFactory
    fun `nissan inventory`() = GherkinDynamicTest
        .dynamicTest(provider)
        .given("an inventory of vehicles by manufacturer") {
            it.GETVehiclesAnswer(
                authorization = "eyfout",
                manufacturerID = "Nissan-#1"
            ) {
                HttpResponse.ok(
                    listOf(
                        Vehicle(id = "Vehicle#1", manufacturer = "Nissan-#1", model = "Maxima"),
                        Vehicle(id = "Vehicle#1", manufacturer = "Nissan-#1", model = "Altima")
                    )
                )
            }

            it.GETManufacturerAnswer(authorization = "eyfout") {
                HttpResponse.ok(
                    listOf(
                        VehicleManufacturer(id = "Nissan-#1", name = "Nissan", established = "1968"),
                        VehicleManufacturer(id = "Ford-#2", name = "Ford", established = "1955"),
                        VehicleManufacturer(id = "Subaru-#3", name = "Subaru", established = "1934")
                    )
                )
            }
        }.fork({
            it.`when`("GET vehicles using manufacturer name") {
                it.httpRequest(ControllerAPI.VehiclesByManufacturerName)
                    .queryParam("make", "Nissan")
                    .header("Authorization", "eyfout")
            }.`when`("GET vehicles using manufacturer ID") {
                it.httpRequest(ControllerAPI.VehiclesByManufacturerID)
                    .header("Authorization", "eyfout")
                    .pathParam("manufacturerID", "Nissan-#1")
            }.then("Altima and Maxima") {
                assertEquals(HttpStatus.OK.code, it.httpResponse().statusCode())
                val vehicles = it.httpResponse().body().`as`(Array<Vehicle>::class.java)
                assertEquals(2, vehicles.size);
                assertEquals(setOf("Maxima", "Altima"), vehicles.map { it.model }.toSet())
            }
        }, {
            it.`when`("GET all manufacturers") {
                it.httpRequest(ControllerAPI.Manufacturers)
                    .header("Authorization", "eyfout")
            }.then("Nissan, Ford, Subaru") {
                assertEquals(HttpStatus.OK.code, it.httpResponse().statusCode())
                val manufacturers = it.httpResponse().body().`as`(Array<VehicleManufacturer>::class.java)
                assertEquals(3, manufacturers.size)
                assertEquals(
                    setOf("Nissan", "Ford", "Subaru"),
                    manufacturers.map(VehicleManufacturer::name).toSet()
                )
            }
        })
}