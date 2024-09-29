package ht.eyfout.example.controller

import ht.eyfout.example.client.DMVClient
import ht.eyfout.example.client.Vehicle
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import java.util.stream.Stream

@Controller("v1/")
class VehiclesController(private val dmvClient: DMVClient) {

    @Get("vehicles")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun vehicles(
        @Header("Authorization") authorization: String,
        @QueryValue("make") make: String
    ): List<Vehicle> {
        return dmvClient.carManufacturers(authorization).body().stream().filter {
            it.name == make
        }.flatMap {
            dmvClient.vehicles(authorization, it.id).body().stream()
        }.toList()
    }


}