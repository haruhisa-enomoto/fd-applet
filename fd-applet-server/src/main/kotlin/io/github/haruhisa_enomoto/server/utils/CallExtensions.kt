package io.github.haruhisa_enomoto.server.utils

import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.types.Subcat
import io.github.haruhisa_enomoto.backend.types.TauTiltingData
import io.github.haruhisa_enomoto.server.storage.SessionStorage
import io.ktor.server.application.*
import io.ktor.util.*


val clientIdKey = AttributeKey<String>("clientId")

fun ApplicationCall.getAlgebra(): QuiverAlgebra<String, String> {
    val clientId = attributes[clientIdKey]
    return SessionStorage.getAlgebra(clientId) ?: throw IllegalArgumentException("Please click Update button.")
}

fun ApplicationCall.getRfAlgebra(): RfAlgebra<String> {
    val clientId = attributes[clientIdKey]
    return SessionStorage.getRfAlgebra(clientId) ?: throw IllegalArgumentException("Please click Update button.")
}

fun ApplicationCall.setSubcatList(subcatList: List<Subcat<String>>) {
    val clientId = attributes[clientIdKey]
    SessionStorage.setSubcatList(clientId, subcatList)
}

fun ApplicationCall.getSubcatList(): List<Subcat<String>> {
    val clientId = attributes[clientIdKey]
    return SessionStorage.getSubcatList(clientId)
}
