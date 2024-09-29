package ht.eyfout.example.http

import ht.eyfout.junit.jupiter.api.http.HttpEndpoint
import java.util.*

enum class ControllerAPI : HttpEndpoint {
    VehiclesByManufacturerID {
        override fun getHttpMethod(): String = "GET"
        override fun getBasePath(): String = "v1/manufacturers/{manufacturerID}/vehicles"
        override fun getDescription(): Optional<String> = Optional.of("vehicles by manufacturer ID")
    },
    VehiclesByManufacturerName {
        override fun getHttpMethod(): String = "GET"
        override fun getBasePath(): String = "v1/vehicles"
        override fun getDescription(): Optional<String> = Optional.of("vehicles by manufacturer name")
    },
    Manufacturers {
        override fun getHttpMethod(): String = "GET"
        override fun getBasePath(): String = "v1/manufacturers"
        override fun getDescription(): Optional<String> = Optional.of("manufacturers")
    }

}