package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.server.utils.getRFAlgebra
import io.github.haruhisa_enomoto.server.utils.getSubcatList
import io.github.haruhisa_enomoto.server.utils.setSubcatList
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun <T> Collection<Indec<T>>.toListString(): List<String> {
    return this.map { it.toString() }
}

fun <T> Collection<Collection<Indec<T>>>.toListListString(): List<List<String>> {
    return this.sortedBy { it.size }.map { mXX -> mXX.map { it.toString() } }
}

fun <T> Collection<Pair<Collection<Indec<T>>, Collection<Indec<T>>>>.toListPairListString(): List<Pair<List<String>, List<String>>> {
    return this.sortedBy { it.first.size }.map { (first, second) ->
        (first.map { it.toString() } to second.map { it.toString() })
    }
}

fun Route.enumeratorRoutes() {
    route("/module") {
        get("/sbrick") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.semibricks().toListListString())
        }

        get("/s-tau-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.supportTauTiltings().toListListString())
        }

        get("/tau-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.tauTiltings().toListListString())
        }

        get("/tau-rigid") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.tauRigids().toListListString())
        }

        get("/s-tau-minus-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.supportTauMinusTiltings().toListListString())
        }

        get("/tau-minus-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.tauMinusTiltings().toListListString())
        }

        get("/tau-minus-rigid") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.tauMinusRigids().toListListString())
        }

        get("/rigid") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.rigids().toListListString())
        }

        get("/excep") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.exceptionals().toListListString())
        }

        get("/self-ortho") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.selfOrthogonals().toListListString())
        }

        get("/partial-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.partialTiltings().toListListString())
        }

        get("/tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.tiltings().toListListString())
        }

        get("/partial-cotilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.partialCotiltings().toListListString())
        }

        get("/cotilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.cotiltings().toListListString())
        }

        get("/gen-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.generalizedTiltings().toListListString())
        }

        get("/gen-cotilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.generalizedCotiltings().toListListString())
        }

        get("/w-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            call.respond(rfAlgebra.wakamatsuTiltings().toListListString())
        }

        get("/pure-w-tilt") {
            val rfAlgebra = call.getRFAlgebra()
            val waks = rfAlgebra.wakamatsuTiltings()
            val pureWaks = waks.filter {
                rfAlgebra.projDim(it) == null && rfAlgebra.injDim(it) == null
            }
            call.respond(pureWaks.toListListString())
        }

        route("/n") {
            post("/cluster-tilt") {
                val rfAlgebra = call.getRFAlgebra()
                val n = call.receive<Int>()
                call.respond(rfAlgebra.clusterTiltings(n).toListListString())
            }

            post("/n-tilt") {
                val rfAlgebra = call.getRFAlgebra()
                val n = call.receive<Int>()
                call.respond(rfAlgebra.generalizedTiltings(n).toListListString())
            }

            post("/n-cotilt") {
                val rfAlgebra = call.getRFAlgebra()
                val n = call.receive<Int>()
                call.respond(rfAlgebra.generalizedCotiltings(n).toListListString())
            }
        }
    }

    route("/indec") {
        get("/all") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.indecs
            call.respond(subcat.toListString())
        }
        get("fpd") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.indecsWithFiniteProjDim()
            call.respond(subcat.toListString())
        }
        get("fid") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.indecsWithFiniteInjDim()
            call.respond(subcat.toListString())
        }

        get("proj") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.projs()
            call.respond(subcat.toListString())
        }

        get("inj") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.injs()
            call.respond(subcat.toListString())
        }

        get("simple") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.simples()
            call.respond(subcat.toListString())
        }

        get("brick") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.bricks()
            call.respond(subcat.toListString())
        }

        get("gp") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.gorensteinProjs()
            call.respond(subcat.toListString())
        }

        get("inf-torsless") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.infiniteTorsionless()
            call.respond(subcat.toListString())
        }

        get("refl") {
            val rfAlgebra = call.getRFAlgebra()
            val subcat = rfAlgebra.reflexives()
            call.respond(subcat.toListString())
        }
    }

    route("subcat") {
        get("tors") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.torsionClasses()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }
        get("torf") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.torsionFreeClasses()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }
        get("wide") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.wideSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }
        get("ie") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.ieClosedSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        get("ice") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.iceClosedSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        get("ike") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.ikeClosedSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        get("resolving") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.resolvingSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        get("resolving") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.resolvingSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        get("coresolving") {
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = rfAlgebra.coresolvingSubcats()
            call.setSubcatList(subcatList)
            call.respond(subcatList.toListListString())
        }

        post("{type?}") {
            val index = call.receive<Int>()
            val type = call.parameters["type"] ?: return@post call.respond(HttpStatusCode.BadRequest)
            val rfAlgebra = call.getRFAlgebra()
            val subcatList = call.getSubcatList()
            val subcat = subcatList[index]
            when (type) {
                "proj" -> {
                    call.respond(subcat.filter {
                        subcat.all { mX ->
                            rfAlgebra.ext(it, mX) == 0
                        }
                    }.map { it.toString() })

                }

                "inj" -> call.respond(subcat.filter {
                    subcat.all { mX ->
                        rfAlgebra.ext(mX, it) == 0
                    }
                }.map { it.toString() })

                else -> call.respondText("Invalid type of modules", status = HttpStatusCode.BadRequest)
            }
        }

        route("pair") {
            get("tors") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.torsionPairs().toListPairListString())
            }

            get("tau-tilt") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.tauTiltingPairs().toListPairListString())
            }

            get("cotors") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.cotorsionPairs().toListPairListString())
            }

            get("h-cotors") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.hereditaryCotorsionPairs().toListPairListString())
            }

            get("2-smc") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.twoSMCs().toListPairListString())
            }

            get("sbrick-full-rank") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.semibrickPairsFullRank().toListPairListString())
            }

            get("sbrick-maximal") {
                val rfAlgebra = call.getRFAlgebra()
                call.respond(rfAlgebra.semibrickPairsMaximal().toListPairListString())
            }
        }
    }
}