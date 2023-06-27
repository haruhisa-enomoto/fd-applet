package io.github.haruhisa_enomoto.server.routes

import io.github.haruhisa_enomoto.backend.algebra.Algebra
import io.github.haruhisa_enomoto.backend.sbalgebra.SbAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.GentleAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.MonomialAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringAlgebra
import io.github.haruhisa_enomoto.server.utils.getAlgebra
import io.github.haruhisa_enomoto.server.utils.toListString
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AlgebraInfo(
    val className: String,
    val dimInfo: List<Pair<String, Int?>>,
    val indecInfo: List<Pair<String, Int?>>,
    val namedModules: List<Pair<String, String>>
)

fun <T> Algebra<T>.dimInfo(): List<Pair<String, Int?>> {
    val dim: Int?
    try {
        dim = this.dim()
    } catch (e: NotImplementedError) {
        return listOf()
    }
    return if (dim == null) listOf(
        "rank A" to vertices.size, "dim A" to null
    ) else if (this is StringAlgebra<*, *> || this is SbAlgebra<*, *>) {
        listOf(
            "rank A" to vertices.size,
            "dim A" to dim,
            "gl.dim A" to this.globalDim(),
            "inj.dim A (as right)" to this.rightSelfInjDim(),
            "inj.dim A (as left)" to this.leftSelfInjDim(),
            "dom.dim A (as right)" to this.dominantDim(),
            "dom.dim A (as left)" to this.dominantDim(),
        )
    } else {
        listOf(
            "rank A" to vertices.size, "dim A" to dim
        )
    }
}

fun <T> Algebra<T>.indecInfo(): List<Pair<String, Int?>> {
    return when (this) {
        is StringAlgebra<*, *> -> {
            val bands = this.primitiveBands().size
            listOf(
                "mod A" to this.numberOfIndecs(),
                "String modules" to this.numberOfIndecs(),
                "Primitive band modules" to bands,
                "Band modules" to if (this.isBandFinite) bands else null
            )
        }

        is SbAlgebra<*, *> -> {
            val bands = this.reduction.primitiveBands().size
            listOf(
                "mod A" to this.numberOfIndecs(),
                "String modules" to this.numberOfIndecs()?.minus(this.biRelations.size),
                "Biserial proj-inj modules" to this.biRelations.size,
                "Primitive band modules" to bands,
                "Band modules" to if (this.reduction.isBandFinite) bands else null
            )
        }

        else -> listOf()
    }
}

fun <T> Algebra<T>.namedModules(): List<Pair<String, String>> {
    try {
        if (!this.isFiniteDimensional()) return listOf()
    } catch (e: NotImplementedError) {
        return listOf()
    }
    return when (this) {
        is StringAlgebra<*, *> -> {
            vertices.map { "P($it)" to projAt(it).toString() } + vertices.map { "I($it)" to injAt(it).toString() } + primitiveBands().map {
                "Primitive band" to it.toString()
            }
        }

        is SbAlgebra<*, *> -> {
            vertices.map { "P($it)" to projAt(it).toString() } + vertices.map { "I($it)" to injAt(it).toString() } + reduction.primitiveBands()
                .map {
                    "Primitive band" to it.toString()
                }
        }

        else -> listOf()
    }
}

fun <T> Algebra<T>.classString(): String {
    val className = when (this) {
        is GentleAlgebra<*, *> -> "Gentle Algebra"
        is StringAlgebra<*, *> -> "String Algebra"
        is SbAlgebra<*, *> -> "Special Biserial Algebra"
        is MonomialAlgebra<*, *> -> "Monomial Algebra"
        else -> "Binomial Algebra"
    }
    val algDimString = try {
        val dim = this.dim()
        if (dim == null) "Infinite-dim " else ""
    } catch (e: NotImplementedError) {
        ""
    }
    if (this !is StringAlgebra<*, *> && this !is SbAlgebra<*, *>) {
        return algDimString + className
    }
    if (!this.isFiniteDimensional()) return algDimString + className
    // Now [this] is a fin-dim string or sb alg.
    val repFinString = if (this.isRepFinite()) "Representation-finite " else "Representation-infinite "
    when (this.globalDim()) {
        0 -> return repFinString + algDimString + "Semisimple Algebra"
        1 -> return repFinString + algDimString + "Hereditary Algebra"
    }
    val glDim = this.globalDim()
    if (glDim != null) {
        return "$repFinString$algDimString$className with gl.dim $glDim"
    }
    val igDim = this.rightSelfInjDim()
    val igDimString = when (this.rightSelfInjDim()) {
        null -> ""
        0 -> "Self-injective "
        else -> "${igDim}-Iwanaga-Gorenstein "
    }
    return repFinString + igDimString + algDimString + className
}

fun <T> Algebra<T>.info(): AlgebraInfo {
    return AlgebraInfo(
        className = this.classString(),
        dimInfo = this.dimInfo(),
        indecInfo = this.indecInfo(),
        namedModules = this.namedModules()
    )
}

fun Route.algebraInfoRoutes() {
    get("/algebra-info") {
        val algebra = call.getAlgebra()
        call.respond(algebra.info())
    }
}