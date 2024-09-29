package ht.eyfout.example.controller

import ht.eyfout.example.client.DMV
import ht.eyfout.example.client.Vehicle
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import java.util.stream.Stream

@Controller("v1/")
class VehiclesController(private val dmv: DMV) {

    @Get("vehicles")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun vehicles(
        @Header("Authorization") authorization: String,
        @QueryValue("make") make: String
    ): Stream<Vehicle> {
        return dmv.carManufacturers(authorization).stream().filter {
            it.name == make
        }.flatMap {
            dmv.vehicles(authorization, it.id).stream()
        }
    }


}