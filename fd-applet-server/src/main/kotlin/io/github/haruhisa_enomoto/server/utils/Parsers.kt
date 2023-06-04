package io.github.haruhisa_enomoto.server.utils

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.quiver.*
import io.github.haruhisa_enomoto.backend.sbalgebra.BiserialIndec
import io.github.haruhisa_enomoto.backend.sbalgebra.SbAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringIndec

/** Trim a list of string `[" a ", " bb ", " "]` to `["a", "bb"]` */
fun List<String>.myTrim(): List<String> {
    return this.map { it.trim() }.filter { it != "" }
}

fun <T> Quiver<T, String>.strToWord(string: String): Word<T, String> {
    val myList = string.split(" ", "*").myTrim()
    require(myList.isNotEmpty()) { "Invalid string." }
    if (myList.size == 1) { // Search for vertices first.
        val vtx = this.vertices.find { it.toString() == myList[0] }
        if (vtx != null) {
            return vtx.toTrivialWord()
        }
    }
    val result = mutableListOf<Letter<T, String>>()
    for (str in myList) {
        val isArrow = !str.startsWith("!")
        val label = if (isArrow) str else str.drop(1)
        val ar = if (isArrow) arrowOfLabel(label).toLetter() else !arrowOfLabel(label)
        result.add(ar)
    }
    return Word.from(result, result.first().from, result.last().to)
}

fun <T> Quiver<T, String>.strToMonomial(string: String): Monomial<T, String> {
    val myList = string.split(" ", "*").myTrim()
    require(myList.isNotEmpty()) { "Invalid string." }
    return Monomial(myList.map { arrowOfLabel(it) })
}

fun <T> QuiverAlgebra<T, String>.strToIndec(string: String): Indec<T> {
    if (string.contains("=")) {
        if (this !is SbAlgebra) {
            throw IllegalArgumentException(
                    "Not Special Biserial Algebra, so cannot make biserial modules."
            )
        }
        val (left, right) = string.split("=")
        val leftMonomial = this.quiver.strToMonomial(left)
        val rightMonomial = this.quiver.strToMonomial(right)
        return BiserialIndec(this, leftMonomial to rightMonomial)
    }
    return StringIndec.from(this, this.quiver.strToWord(string))
}


