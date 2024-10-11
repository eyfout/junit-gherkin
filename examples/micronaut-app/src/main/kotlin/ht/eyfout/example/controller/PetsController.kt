package ht.eyfout.example.controller

import ht.eyfout.example.client.pets.PetClient
import ht.eyfout.junit.jupiter.gherkin.api.http.HttpEndpoint
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import java.util.*

@Controller
class PetsController(private val client: PetClient) {

    enum class APIEndpoint : HttpEndpoint {
        UserInfo {
            override fun getHttpMethod(): String = "GET"
            override fun getBasePath(): String = "v1/users/{userID}"
            override fun getDescription(): Optional<String> = Optional.of("user information")
        }
    }


    @Get("v1/users/{userID}")
    fun userLogic(
        @PathVariable userID: String,
    ): HttpResponse<String> {
        return client.userLogin(userID, "password");
    }
}