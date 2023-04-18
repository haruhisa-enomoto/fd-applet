package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.backend.utils.toListWithLeq
import io.github.haruhisa_enomoto.server.utils.getRfAlgebra
import io.github.haruhisa_enomoto.server.utils.toStringQuiver
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.quiverRoutes() {
    route("/quiver") {
        get("/ar") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.arQuiver().toStringQuiver())
        }

        get("/syzygy") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.syzygyQuiver().toStringQuiver())
        }

        get("/cosyzygy") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.cosyzygyQuiver().toStringQuiver())
        }

        get("/s-tau-tilt") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.tauTiltingQuiverWithBrickLabel().toStringQuiver())
        }

        get("/w-tilt") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.wakamatsuTiltingsWithLeq().hasseQuiver().toStringQuiver())

        }

        get("/gen-tilt") {
            val rfAlgebra = call.getRfAlgebra()
            call.respond(rfAlgebra.generalizedTiltingPoset().hasseQuiver().toStringQuiver())
        }

        route("/subcat") {
            get("/tors") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.torsionClasses().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/torf") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.torsionFreeClasses().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/wide") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.wideSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/ice") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.iceClosedSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/ike") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.ikeClosedSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/ie") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.ieClosedSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/resolving") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.resolvingSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
            get("/coresolving") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.coresolvingSubcats().toListWithLeq().hasseQuiver().toStringQuiver())
            }
        }

        route("/pair") {
            get("/tors") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.torsionClasses().toListWithLeq().hasseQuiver().toStringQuiver())
            }

            get("/cotors") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.cotorsionPairs().map { it.first }.toListWithLeq().hasseQuiver().toStringQuiver())
            }

            get("/h-cotors") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(
                    rfAlgebra.hereditaryCotorsionPairs().map { it.first }.toListWithLeq().hasseQuiver().toStringQuiver()
                )
            }

            get("/2-smc") {
                val rfAlgebra = call.getRfAlgebra()
                call.respond(rfAlgebra.twoSMCs().map { it.first }.toListWithLeq().hasseQuiver().toStringQuiver())
            }
        }
    }

}