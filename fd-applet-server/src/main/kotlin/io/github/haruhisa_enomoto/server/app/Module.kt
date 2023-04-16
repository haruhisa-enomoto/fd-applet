package io.github.haruhisa_enomoto.server.app

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.module() {
    configureRouting()

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<OutOfMemoryError> { call, _ ->
            call.respondText(
                "Out of Memory",
                status = HttpStatusCode.InternalServerError
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(
                cause.message ?: "Unknown error",
                status = HttpStatusCode.InternalServerError
            )
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respondText(
                text = "Sorry, it seems like this feature has not been implemented on the app.",
                status = status
            )
        }

    }

    install(ShutDownUrl.ApplicationCallPlugin) {
        shutDownUrl = "/shutdown"
        exitCodeSupplier = { 0 }
    }
}