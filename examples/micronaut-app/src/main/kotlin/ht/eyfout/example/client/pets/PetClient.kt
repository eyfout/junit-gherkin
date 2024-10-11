package ht.eyfout.example.client.pets

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${pets.url}")
interface PetClient {
    @Get("/user/login")
    fun userLogin(
        @QueryValue username: String,
        @QueryValue password: String
    ) : HttpResponse<String>
}