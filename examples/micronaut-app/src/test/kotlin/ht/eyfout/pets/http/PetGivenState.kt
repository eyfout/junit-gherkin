package ht.eyfout.pets.http

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.junit.jupiter.gherkin.api.GivenState
import ht.eyfout.http.HttpEndpoint
import ht.eyfout.http.HttpRequestBuilder
import ht.eyfout.http.openapi.generated.warehouse.GETloginUserHttpEndpoint
import io.micronaut.http.HttpResponse
import io.mockk.every

class PetGivenState(private val client: PetClient) : GivenState() {
    fun <B : HttpRequestBuilder> httpRequest(api: HttpEndpoint<B>, lambda: (B) -> Unit): B {
        return api.builder().apply(lambda)
    }
    fun <B: HttpRequestBuilder, T> returns(builder: B, response: HttpResponse<T?>){
        when (builder.api) {
            is GETloginUserHttpEndpoint -> {
                every {
                    client.userLogin(builder.queryParam("username"), builder.queryParam("password"))
                } returns response as HttpResponse<String>
            }
            else -> throw IllegalArgumentException(builder.api.javaClass.name)
        }
    }

     infix fun <B: HttpRequestBuilder> B.returns(response: ()-> HttpResponse<Any?>) {
        when (this.api) {
            GETloginUserHttpEndpoint::class -> {
                every {
                    client.userLogin(queryParam("username"), queryParam("password"))
                } returns response() as HttpResponse<String>
            }
            else -> throw IllegalArgumentException("")
        }
    }
}