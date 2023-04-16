package io.github.haruhisa_enomoto.server.utils

import io.github.haruhisa_enomoto.backend.quiver.TranslationQuiver
import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.stringalg.BinomialAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.MonomialAlgebra
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

fun <T, U> Quiver<T, U>.monomialFrom(list: List<U>): Monomial<T, U> {
    return Monomial(list.map { this.arrowOfLabel(it) })
}

@Serializable
data class SerialMonomialAlgebra<T, U>(
    val quiver: Quiver<T, U>, val relations: List<List<U>>
) {
    fun deserialize(): MonomialAlgebra<T, U> {
        return MonomialAlgebra(quiver, relations.map { quiver.monomialFrom(it) })
    }
}

@Serializable
data class SerialBinomialAlgebra<T, U>(
    val quiver: Quiver<T, U>,
    val monoRelations: List<List<U>>,
    val biRelations: List<Pair<List<U>, List<U>>>
) {
    private fun overAlgebra(): SerialMonomialAlgebra<T, U> {
        return SerialMonomialAlgebra(quiver, monoRelations)
    }

    fun deserialize(): BinomialAlgebra<T, U> {
        return BinomialAlgebra(overAlgebra().deserialize(),
            biRelations.map { (first, second) ->
                quiver.monomialFrom(first) to quiver.monomialFrom(second)
            }
        )
    }
}

fun <T, U> BinomialAlgebra<T, U>.toSerialBinomialAlgebra(): SerialBinomialAlgebra<T, U> {
    return SerialBinomialAlgebra(this.quiver,
        this.overAlgebra.relations.map { it.toList() },
        this.biRelations.map { it.first.toList() to it.second.toList() })
}

fun <T> TranslationQuiver<T>.toStringQuiver(): Quiver<String, Unit> {
    val quiver = this.toQuiver()
    return Quiver(quiver.vertices.map { it.toString() },
        quiver.arrows.map { Arrow(null, it.from.toString(), it.to.toString(), isTau = it.isTau) })
}


fun <T, U> Quiver<T, U>.toStringQuiver(): Quiver<String, String> {
    return Quiver(this.vertices.map { it.toString() },
        this.arrows.map { Arrow(it.label?.toString(), it.from.toString(), it.to.toString()) })
}

inline fun <reified T> T.toJsonString(): String {
    return Json.encodeToString(this)
}


fun main() {
    println("Hello")
    val filePath = "examples/2-cycle-with-loop.json"
    val data = Json.decodeFromString<MonomialAlgebra<String, String>>(File(filePath).readText())
    data.printInfo()
}