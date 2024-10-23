package ht.eyfout.pets.http

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.http.HttpEndpoint
import ht.eyfout.http.HttpRequestBuilder
import ht.eyfout.http.openapi.generated.warehouse.GETloginUserHttpEndpoint
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpGivenState
import io.micronaut.http.HttpResponse
import io.mockk.every
import java.util.function.Supplier

class PetGivenState(private val client: PetClient) : HttpGivenState() {
    override fun match(endpoint: HttpEndpoint<*>, httpRequest: HttpRequestBuilder, httpResponse: Supplier<Any>) {
        with(httpRequest) {
            when (endpoint) {
                is GETloginUserHttpEndpoint -> {
                    every {
                        client.userLogin(
                            queryParam(GETloginUserHttpEndpoint.QueryParam.USERNAME),
                            queryParam(GETloginUserHttpEndpoint.QueryParam.PASSWORD)
                        )
                    } answers {
                        httpResponse.get() as HttpResponse<String>
                    }
                }

                else -> throw IllegalArgumentException("${endpoint.basePath} not yet implemented.")
            }
        }
    }
}