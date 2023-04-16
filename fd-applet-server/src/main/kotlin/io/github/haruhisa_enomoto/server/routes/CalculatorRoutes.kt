package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.utils.getAlgebra
import io.github.haruhisa_enomoto.server.utils.strToIndec
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.calculatorRoutes() {
    route("calculator") {
        route("2") {
            post("hom") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, List<String>>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val mYY = data.second.map { algebra.strToIndec(it) }
                call.respond(algebra.hom(mXX, mYY))
            }

            post("ext") {
                val algebra = call.getAlgebra()
                val data = call.receive<Triple<List<String>, List<String>, Int>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val mYY = data.second.map { algebra.strToIndec(it) }
                call.respond(algebra.ext(mXX, mYY, data.third))
            }

            post("ext-zero") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, List<String>>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val mYY = data.second.map { algebra.strToIndec(it) }
                call.respond(algebra.higherExtZero(mXX, mYY))

            }

            post("proj-st-hom") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, List<String>>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val mYY = data.second.map { algebra.strToIndec(it) }
                call.respond(algebra.stableHom(mXX, mYY))
            }

            post("inj-st-hom") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, List<String>>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val mYY = data.second.map { algebra.strToIndec(it) }
                call.respond(algebra.injStableHom(mXX, mYY))
            }
        }
        route("1") {
            post("resol/proj") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, Int>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val n = data.second
                val result = algebra.projResolutionWithSyzygy(mXX, n)
                call.respond(result.map { pair ->
                    pair.first.sorted() to pair.second.map { it.toString() }
                })
            }

            post("resol/inj") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, Int>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val n = data.second
                val result = algebra.injResolutionWithCosyzygy(mXX, n)
                call.respond(result.map { pair ->
                    pair.first.sorted() to pair.second.map { it.toString() }
                })
            }
            post("dim") {
                val algebra = call.getAlgebra()
                val data = call.receive<List<String>>()
                val mXX = data.map { algebra.strToIndec(it) }
                call.respond(
                    listOf(
                        "proj.dim" to algebra.projDim(mXX),
                        "inj.dim" to algebra.injDim(mXX),
                        "dom.dim" to algebra.dominantDim(mXX),
                        "codom.dim" to algebra.coDominantDim(mXX)
                    )
                )
            }
        }

        route("subcat") {
            post("proj") {
                val algebra = call.getAlgebra()
                val data = call.receive<List<String>>()
                val cC = data.map { algebra.strToIndec(it) }
                val result = algebra.extProj(cC)
                call.respond(result.map { it.toString() })
            }

            post("inj") {
                val algebra = call.getAlgebra()
                val data = call.receive<List<String>>()
                val cC = data.map { algebra.strToIndec(it) }
                val result = algebra.extInj(cC)
                call.respond(result.map { it.toString() })
            }
        }
    }
}