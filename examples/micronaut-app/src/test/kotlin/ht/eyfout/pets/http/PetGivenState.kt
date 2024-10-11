package ht.eyfout.pets.http

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.junit.jupiter.gherkin.api.GivenState
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPI
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpAPIRequestBuilder
import ht.eyfout.junit.jupiter.gherkin.http.generated.warehouse.GETloginUserHttpAPI
import io.micronaut.http.HttpResponse
import io.mockk.every

class PetGivenState(private val client: PetClient) : GivenState() {
    fun <B : HttpAPIRequestBuilder> httpRequest(api: HttpAPI<B>, lambda: (B) -> Unit): B {
        return api.builder().apply(lambda)
    }
    fun <B: HttpAPIRequestBuilder, T> returns(builder: B, response: HttpResponse<T?>){
        when (builder.api) {
            GETloginUserHttpAPI::class -> {
                every {
                    client.userLogin(builder.queryParam("username"), builder.queryParam("password"))
                } returns response as HttpResponse<String>
            }
            else -> throw IllegalArgumentException("")
        }
    }

     infix fun <B: HttpAPIRequestBuilder> B.returns(response: ()-> HttpResponse<Any?>) {
        when (this.api) {
            GETloginUserHttpAPI::class -> {
                every {
                    client.userLogin(queryParam("username"), queryParam("password"))
                } returns response() as HttpResponse<String>
            }
            else -> throw IllegalArgumentException("")
        }
    }
}