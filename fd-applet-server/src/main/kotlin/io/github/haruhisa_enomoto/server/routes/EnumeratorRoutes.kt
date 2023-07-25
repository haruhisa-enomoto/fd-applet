package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.server.utils.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.enumeratorRoutes() {
    get("/module/{param}") {
        val rfAlgebra = call.getRfAlgebra()
        val request = call.parameters["param"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = when (request) {
            "cotilt" -> rfAlgebra.cotiltings()
            "excep" -> rfAlgebra.exceptionals()
            "gen_cotilt" -> rfAlgebra.generalizedCotiltings()
            "gen_tilt" -> rfAlgebra.generalizedTiltings()
            "partial_tilt" -> rfAlgebra.partialTiltings()
            "partial_cotilt" -> rfAlgebra.partialCotiltings()
            "pure_w_tilt" -> {
                val waks = rfAlgebra.wakamatsuTiltings()
                waks.filter {
                    rfAlgebra.projDim(it) == null && rfAlgebra.injDim(it) == null
                }
            }

            "rigid" -> rfAlgebra.rigids()
            "sbrick" -> rfAlgebra.semibricks()
            "self_ortho" -> rfAlgebra.selfOrthogonals()
            "s_tau_tilt" -> rfAlgebra.supportTauTiltings()
            "s_tau_minus_tilt" -> rfAlgebra.supportTauMinusTiltings()
            "tau_tilt" -> rfAlgebra.tauTiltings()
            "tau_rigid" -> rfAlgebra.tauRigids()
            "tau_minus-tilt" -> rfAlgebra.tauMinusTiltings()
            "tau_minus-rigid" -> rfAlgebra.tauMinusRigids()
            "tilt" -> rfAlgebra.tiltings()
            "wide_tau_tilt" -> rfAlgebra.wideTauTiltings()
            "wide_tau_minus_tilt" -> rfAlgebra.wideTauMinusTiltings()
            "w_tilt" -> rfAlgebra.wakamatsuTiltings()
            else -> return@get call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(result.toListListString(shallow = true, deep = true))
    }

    post("/module/n/{param}") {
        val rfAlgebra = call.getRfAlgebra()
        val n = call.receive<Int>()
        val request = call.parameters["param"] ?: return@post call.respond(HttpStatusCode.BadRequest)
        val result = when (request) {
            "cluster_tilt" -> rfAlgebra.clusterTiltings(n)
            "n_cotilt" -> rfAlgebra.generalizedCotiltings(n)
            "n_tilt" -> rfAlgebra.generalizedTiltings(n)
            else -> return@post call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(result.toListListString(shallow = true, deep = true))
    }

    get("/indec/{param}") {
        val rfAlgebra = call.getRfAlgebra()
        val request = call.parameters["param"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = when (request) {
            "all" -> rfAlgebra.indecs
            "brick" -> rfAlgebra.bricks()
            "fpd" -> rfAlgebra.indecsWithFiniteProjDim()
            "fid" -> rfAlgebra.indecsWithFiniteInjDim()
            "gp" -> rfAlgebra.gorensteinProjs()
            "inf_torsless" -> rfAlgebra.infiniteTorsionless()
            "inj" -> rfAlgebra.injs()
            "proj" -> rfAlgebra.projs()
            "refl" -> rfAlgebra.reflexives()
            "simple" -> rfAlgebra.simples()
            else -> return@get call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(result.toListString())
    }

    get("/subcat/{param}") {
        val rfAlgebra = call.getRfAlgebra()
        val request = call.parameters["param"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = when (request) {
            "coresolving" -> rfAlgebra.coresolvingSubcats()
            "ice" -> rfAlgebra.iceClosedSubcats()
            "ike" -> rfAlgebra.ikeClosedSubcats()
            "ie" -> rfAlgebra.ieClosedSubcats()
            "resolving" -> rfAlgebra.resolvingSubcats()
            "tors" -> rfAlgebra.torsionClasses()
            "torf" -> rfAlgebra.torsionFreeClasses()
            "wide" -> rfAlgebra.wideSubcats()
            else -> return@get call.respond(HttpStatusCode.BadRequest)
        }
        val sortedResult = result.mySorted(shallow = true, deep = true)
        call.setSubcatList(sortedResult)
        call.respond(sortedResult.toListListString(shallow = false, deep = false))
    }

    get("/pair/{param}") {
        val rfAlgebra = call.getRfAlgebra()
        val request = call.parameters["param"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val result = when (request) {
            "2_smc" -> rfAlgebra.twoSMCs()
            "cotors" -> rfAlgebra.cotorsionPairs()
            "h_cotors" -> rfAlgebra.hereditaryCotorsionPairs()
            "sbrick_full_rank" -> rfAlgebra.semibrickPairsFullRank()
            "sbrick_maximal" -> rfAlgebra.semibrickPairsMaximal()
            "tors" -> rfAlgebra.torsionPairs()
            "tau_tilt" -> rfAlgebra.tauTiltingPairs()
            else -> return@get call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(result.toListPairListString())
    }

    post("/subcat/calculate/{type?}") {
        val index = call.receive<Int>()
        val type = call.parameters["type"] ?: return@post call.respond(HttpStatusCode.BadRequest)
        val rfAlgebra = call.getRfAlgebra()
        val subcatList = call.getSubcatList()
        val subcat = subcatList[index]
        val result = when (type) {
            "proj" -> subcat.filter { subcat.all { mX -> rfAlgebra.ext(it, mX) == 0 } }
            "inj" -> subcat.filter { subcat.all { mX -> rfAlgebra.ext(mX, it) == 0 } }
            else -> return@post call.respond(HttpStatusCode.BadRequest)
        }
        call.respond(result.toListString())
    }

    route("/others") {
        get("/mgs") {
            val rfAlgebra = call.getRfAlgebra()
            val result = rfAlgebra.maximalGreenSequences()
            call.respond(result.toListListString(shallow = true, deep = false))
        }

        get("/ice_seq") {
            val rfAlgebra = call.getRfAlgebra()
            val query = call.request.queryParameters
            val full = query["full"]?.toBoolean() ?: false
            val length = query["length"]?.toIntOrNull()
            val proper = query["proper"]?.toBoolean() ?: false
            val bound = query["bound"]?.toBoolean() ?: false
            val endsAtZero = query["ends_at_zero"]?.toBoolean() ?: false
            val result = if (full) {
                rfAlgebra.fullIceSequences(length, bound, proper)
            } else {
                rfAlgebra.iceSequences(null, length, bound, proper, endsAtZero)
            }
            call.respond(result.toListListListString())
        }
    }
}
