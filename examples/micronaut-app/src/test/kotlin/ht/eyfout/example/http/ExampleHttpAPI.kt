package ht.eyfout.example.http

import ht.eyfout.junit.jupiter.api.http.HttpAPI
import ht.eyfout.junit.jupiter.api.http.HttpAPIRequestExecutor

class ExampleHttpAPI : HttpAPI<HttpAPIRequestExecutor>() {
    companion object {
        val INSTANCE = ExampleHttpAPI()
    }

    override fun getHttpMethod(): String = "GET"

    override fun getBasePath(): String = "v1/vehicles"

    override fun getDescription(): String = "vehicles"
}