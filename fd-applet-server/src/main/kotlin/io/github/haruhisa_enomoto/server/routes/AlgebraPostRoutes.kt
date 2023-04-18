package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.app.clientIdKey
import io.github.haruhisa_enomoto.server.storage.SessionStorage
import io.github.haruhisa_enomoto.server.utils.SerialBinomialAlgebra
import io.github.haruhisa_enomoto.server.utils.toSerialBinomialAlgebra
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


fun Route.algebraPostRoutes() {
    post("/algebra") {
        val clientId = call.attributes[clientIdKey]
        val algString = call.receive<String>()
        val serialAlgebra: SerialBinomialAlgebra<String, String> = Json.decodeFromString(algString)
        val deserializedAlgebra = serialAlgebra.deserialize()
        SessionStorage.addAlgebra(clientId, deserializedAlgebra.make())
        call.respond(deserializedAlgebra.toSerialBinomialAlgebra())
    }

    get("/rf-algebra") {
        val clientId = call.attributes[clientIdKey]
        SessionStorage.createRfAlgebra(clientId)
        call.respond(HttpStatusCode.Created)
    }
}