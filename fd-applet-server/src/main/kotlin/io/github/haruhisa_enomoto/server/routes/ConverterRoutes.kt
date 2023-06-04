package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.utils.getAlgebra
import io.github.haruhisa_enomoto.server.utils.getRfAlgebra
import io.github.haruhisa_enomoto.server.utils.strToIndec
import io.github.haruhisa_enomoto.server.utils.toListString
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.converterRoutes() {
    post("converter/{param1}/{param2}") {
        val algebra = call.getAlgebra()
        val rfAlgebra = call.getRfAlgebra()
        val data = call.receive<List<String>>()
        val fromData = data.map { rfAlgebra.normalize(algebra.strToIndec(it)) }

        val param1 = call.parameters["param1"]
        val result = when (val param2 = call.parameters["param2"]) {
            "tors_closure" -> rfAlgebra.torsionClosure(fromData)
            "torf_closure" -> rfAlgebra.torsionFreeClosure(fromData)
            "wide_closure" -> rfAlgebra.wideClosure(fromData)
            "ice_closure" -> rfAlgebra.iceClosure(fromData)
            "ike_closure" -> rfAlgebra.ikeClosure(fromData)
            "ie_closure" -> rfAlgebra.ieClosure(fromData)
            "tors_as_perp" -> rfAlgebra.homLeftPerp(fromData)
            "torf_as_perp" -> rfAlgebra.homRightPerp(fromData)
            "ext_proj" -> rfAlgebra.extProj(fromData)
            "ext_inj" -> rfAlgebra.extInj(fromData)

            else -> {
                val fromSet = fromData.toSet()
                when (param1 to param2) {
                    "tors" to "torf" -> rfAlgebra.homRightPerp(fromData)
                    "tors" to "wide" -> rfAlgebra.wideClosure(rfAlgebra.torsToSbrick[fromSet]!!)
                    "tors" to "s_tau_tilt" -> rfAlgebra.extProj(fromData)
                    "tors" to "sbrick" -> rfAlgebra.torsToSbrick[fromSet]!!

                    "torf" to "tors" -> rfAlgebra.homLeftPerp(fromData)
                    "torf" to "wide" -> rfAlgebra.wideClosure(rfAlgebra.torfToSbrick[fromSet]!!)
                    "torf" to "s_tau_minus_tilt" -> rfAlgebra.extInj(fromData)
                    "torf" to "sbrick" -> rfAlgebra.torfToSbrick[fromSet]!!

                    "wide" to "tors" -> rfAlgebra.torsionClosure(fromData)
                    "wide" to "torf" -> rfAlgebra.torsionFreeClosure(fromData)
                    "wide" to "sbrick" -> rfAlgebra.wideToSbrick[fromSet]!!

                    "ice" to "wide" -> rfAlgebra.iceToWide(fromData)
                    "ike" to "wide" -> rfAlgebra.ikeToWide(fromData)

                    "s_tau_tilt" to "tors" -> rfAlgebra.torsionClosure(fromData)
                    "s_tau_tilt" to "sbrick" -> rfAlgebra.torsToSbrick[rfAlgebra.torsionClosure(fromData).toSet()]!!
                    "s_tau_tilt" to "s_tau_minus_tilt" -> rfAlgebra.sTauTiltToSTauMinusTilt(fromData)

                    "s_tau_minus_tilt" to "torf" -> rfAlgebra.torsionFreeClosure(fromData)
                    "s_tau_minus_tilt" to "sbrick" -> rfAlgebra.torfToSbrick[rfAlgebra.torsionFreeClosure(fromData).toSet()]!!
                    "s_tau_minus_tilt" to "s_tau_tilt" -> rfAlgebra.sTauMinusTiltToSTauTilt(fromData)

                    "sbrick" to "wide" -> rfAlgebra.sbrickToWide[fromSet]!!
                    "sbrick" to "tors" -> rfAlgebra.sbrickToTors[fromSet]!!
                    "sbrick" to "torf" -> rfAlgebra.sbrickToTorf[fromSet]!!
                    "sbrick" to "s_tau_tilt" -> rfAlgebra.extProj(rfAlgebra.sbrickToTors[fromSet]!!)
                    "sbrick" to "s_tau_minus_tilt" -> rfAlgebra.extInj(rfAlgebra.sbrickToTorf[fromSet]!!)
                    "sbrick" to "sbrick" -> rfAlgebra.torfToSbrick[rfAlgebra.homRightPerp(fromData).toSet()]!!
                    else -> TODO("Not implemented yet.")
                }
            }
        }
        call.respond(result.toListString())
    }
}
