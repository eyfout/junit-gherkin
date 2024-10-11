package ht.eyfout.example.client.dmv

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class Vehicle(val id: String, val manufacturer: String, val model: String)