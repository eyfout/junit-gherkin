package ht.eyfout.example

import io.micronaut.runtime.Micronaut.run
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info

@OpenAPIDefinition(
    info = Info(title = "example application", description = "Yes")
)
open class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            run(*args)
        }
    }
}

