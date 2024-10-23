package ht.eyfout.example.client.dmv

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class VehicleManufacturer(
    val id: String,
    val name: String,
    val established: String
)