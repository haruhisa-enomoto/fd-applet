package io.github.haruhisa_enomoto.server.app

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    val port = try {
        System.getenv("PORT").toInt()
    } catch (e: NullPointerException) {
        8080
    }

    val server = embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
    server.start(wait = true)
}