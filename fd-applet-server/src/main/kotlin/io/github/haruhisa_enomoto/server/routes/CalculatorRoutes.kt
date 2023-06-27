package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.utils.getAlgebra
import io.github.haruhisa_enomoto.server.utils.strToIndec
import io.github.haruhisa_enomoto.server.utils.toListString
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.calculatorRoutes() {
    route("calculator") {
        get("candidates") {
            val algebra = call.getAlgebra()
            try {
                val rfAlgebra = algebra.toRfAlgebra()
                call.respond(rfAlgebra.indecs.toListString())
                return@get
            } catch (_: NotImplementedError) {
            } catch (_: IllegalArgumentException) {
            }
            if (algebra.isWordFinite) {
                call.respond(algebra.stringIndecs().toListString())
            } else {
                call.respond(algebra.stringIndecs(lengthBound = 10).toListString())
            }
        }

        post("2/{param}") {
            val algebra = call.getAlgebra()
            val data = call.receive<Pair<List<String>, List<String>>>()
            val mXX = data.first.map { algebra.strToIndec(it) }
            val mYY = data.second.map { algebra.strToIndec(it) }
            val param = call.parameters["param"]
            when (param) {
                "hom" -> call.respond(algebra.hom(mXX, mYY))
                "ext_zero" -> call.respond(algebra.higherExtZero(mXX, mYY))
                "proj_st_hom" -> call.respond(algebra.stableHom(mXX, mYY))
                "inj_st_hom" -> call.respond(algebra.injStableHom(mYY, mXX))
                else -> TODO("Unknown parameter $param")
            }
        }

        post("3/ext") {
            val algebra = call.getAlgebra()
            val data = call.receive<Triple<List<String>, List<String>, Int>>()
            val mXX = data.first.map { algebra.strToIndec(it) }
            val mYY = data.second.map { algebra.strToIndec(it) }
            call.respond(algebra.ext(mXX, mYY, data.third))
        }

        route("1") {
            post("resol/{param}") {
                val algebra = call.getAlgebra()
                val data = call.receive<Pair<List<String>, Int>>()
                val mXX = data.first.map { algebra.strToIndec(it) }
                val n = data.second
                val param = call.parameters["param"]
                val result = when (param) {
                    "proj" -> algebra.projResolutionWithSyzygy(mXX, n)
                    "inj" -> algebra.injResolutionWithCosyzygy(mXX, n)
                    else -> TODO("Unknown parameter $param")
                }
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