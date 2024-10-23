package ht.eyfout.example.client.dmv

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.client.annotation.Client

@Client("\${dmv.url}")
interface DMVClient {
    @Get("v1/manufacturers")
    fun carManufacturers(
        @Header authorization: String
    ): HttpResponse<Collection<VehicleManufacturer>>

    @Get("v1/manufacturers/{manufacturerID}/vehicles")
    fun vehicles(
        @Header authorization: String,
        @PathVariable manufacturerID: String
    ): HttpResponse<Collection<Vehicle>>
}