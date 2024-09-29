package ht.eyfout.example

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.http.ControllerAPI
import ht.eyfout.example.http.ExampleStateScopeProvider
import ht.eyfout.junit.jupiter.api.GherkinDynamicTest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory

@MicronautTest
class CascadeWhenTests {

    @MockBean(DMVClient::class)
    val dmvClient: DMVClient = mockk()

    @Inject
    lateinit var provider: ExampleStateScopeProvider

    @TestFactory
    fun `cascaded when`() = GherkinDynamicTest
        .dynamicTest(provider)
        .given("an unauthorized request") {
            it.GETManufacturerAnswer(null) {
                HttpResponse.unauthorized()
            }
            it.GETVehiclesAnswer(null, null) {
                HttpResponse.unauthorized()
            }
        }.`when`("GET vehicles using manufacturer name") {
            it.httpRequest(ControllerAPI.VehiclesByManufacturerName)
                .queryParam("make", "Nissan")
                .header("Authorization", "other")
        }.`when`("GET vehicles using manufacturer ID") {
            it.httpRequest(ControllerAPI.VehiclesByManufacturerID)
                .header("Authorization", "other")
                .pathParam("manufacturerID", "Nissan-#1")
        }.`when`("GET manufactures") {
            it.httpRequest(ControllerAPI.Manufacturers)
                .header("Authorization", "other")
        }.then("Http 401") {
            assertEquals(HttpStatus.UNAUTHORIZED.code, it.httpResponse().statusCode())
        }
}