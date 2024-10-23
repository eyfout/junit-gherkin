package ht.eyfout.example.controller

import ht.eyfout.example.client.pets.PetClient
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller
class PetsController(private val client: PetClient) {
    @Get("v1/users/{userID}")
    fun userLogic(
        @PathVariable userID: String,
    ): HttpResponse<String> {
        return client.userLogin(userID, "password");
    }
}