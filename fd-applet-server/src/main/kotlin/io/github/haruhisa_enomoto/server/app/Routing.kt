package io.github.haruhisa_enomoto.server.app

import io.github.haruhisa_enomoto.server.routes.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*

val clientIdKey = AttributeKey<String>("clientId")

fun Application.configureRouting() {
    routing {
        static("/") {
            staticBasePackage = "files"
            resources(".")
            defaultResource("index.html")
        }
        route("/api") {
            storageRoutes()
            intercept(Plugins) {
                val clientId = call.request.queryParameters["client_id"]
                if (clientId == null) {
                    call.respond(HttpStatusCode.BadRequest, "clientId is missing.")
                    finish()
                } else {
                    call.attributes.put(clientIdKey, clientId)
                }
            }
            algebraPostRoutes()
            calculatorRoutes()
            algebraInfoRoutes()
            quiverRoutes()
            enumeratorRoutes()
        }
    }
}