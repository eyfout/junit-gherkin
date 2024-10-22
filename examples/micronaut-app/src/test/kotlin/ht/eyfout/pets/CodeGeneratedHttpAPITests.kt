package ht.eyfout.pets

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.example.controller.PetsController
import ht.eyfout.http.openapi.generated.own.GETuserLogicHttpEndpoint
import ht.eyfout.http.openapi.generated.warehouse.GETloginUserHttpEndpoint
import ht.eyfout.junit.jupiter.gherkin.api.GherkinDynamicTest
import ht.eyfout.pets.http.PetsStateScopeProvider
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.mockk
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestFactory

@MicronautTest
class CodeGeneratedHttpAPITests {

    @MockBean(PetClient::class)
    val petClient: PetClient = mockk()

    @Inject
    lateinit var provider: PetsStateScopeProvider

    @TestFactory
    fun user() = GherkinDynamicTest.dynamicTest(provider)
        .given("eyfout profile") {
            it.httpRequest(GETloginUserHttpEndpoint.INSTANCE, {
                it.queryParams({
                    it.setPassword("password")
                    it.setUsername("eyfout")
                })
            }).respondsWith {
                HttpResponse.ok("{name:eyfout, org:junit-gherkin}")
            }
        }.`when`("request info for eyfout") {
            it.httpRequest(GETuserLogicHttpEndpoint.INSTANCE)
                .pathParam("userID", "eyfout")
        }.then("org information") {
            assertEquals("{name:eyfout, org:junit-gherkin}", it.httpResponse().body.asString())
        }
}