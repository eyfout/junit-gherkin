package ht.eyfout.example.client

import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

@Client
interface DMVClient {
    @Get("v1/manufacturers")
    fun carManufacturers(
        @Header authorization: String
    ): Collection<VehicleManufacturer>

    @Get("v1/manufacturers/{manufacturerID}/vehicles")
    fun vehicles(
        @Header authorization: String,
        @PathVariable manufacturerID: String
    ): Collection<Vehicle>
}

