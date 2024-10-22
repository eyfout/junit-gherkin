package ht.eyfout.example.controller

import ht.eyfout.example.client.dmv.DMVClient
import ht.eyfout.example.client.dmv.Vehicle
import ht.eyfout.example.client.dmv.VehicleManufacturer
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*

@Controller("v1/")
class VehiclesController(private val dmvClient: DMVClient) {
    @Get("vehicles")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun vehicles(
        @Header("Authorization") authorization: String,
        @QueryValue("make") make: String
    ): HttpResponse<Collection<Vehicle>> {
        val httpResponse = dmvClient.carManufacturers(authorization)
        return when (httpResponse.code()) {
            HttpStatus.OK.code -> HttpResponse.ok(httpResponse.body().stream().filter {
                it.name == make
            }.flatMap {
                dmvClient.vehicles(authorization, it.id).body().stream()
            }.toList())

            else -> httpResponse as HttpResponse<Collection<Vehicle>>
        }
    }

    @Get("manufacturers/{manufacturerID}/vehicles")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun vehiclesByManufacturer(
        @Header("Authorization") authorization: String,
        @PathVariable("manufacturerID") manufacturerID: String
    ): HttpResponse<Collection<Vehicle>> {
        val httpResponse = dmvClient.vehicles(authorization, manufacturerID);
        return when (httpResponse.code()) {
            HttpStatus.OK.code -> HttpResponse.ok(httpResponse.body())
            else -> httpResponse
        }
    }


    @Get("manufacturers")
    @Produces(MediaType.APPLICATION_JSON)
    suspend fun manufacturers(
        @Header("Authorization") authorization: String,
    ): HttpResponse<Collection<VehicleManufacturer>> {


        val httpResponse = dmvClient.carManufacturers(authorization)
        return when (httpResponse.code()) {
            HttpStatus.OK.code -> HttpResponse.ok(httpResponse.body())
            else -> httpResponse
        }
    }

}