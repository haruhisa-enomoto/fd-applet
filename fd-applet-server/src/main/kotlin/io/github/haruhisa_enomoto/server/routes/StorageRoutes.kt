package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.storage.SessionStorage
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.storageRoutes() {
    post("/kill") {
        val clientId = call.request.queryParameters["client_id"]
        if (clientId != null) {
            SessionStorage.delete(clientId)
        }
    }

    get("/active-ids") {
        call.respond(SessionStorage.getIdList())
    }
}