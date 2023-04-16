package io.github.haruhisa_enomoto.backend.sbalgebra

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.stringalg.StringIndec

class BiserialIndec<T, U>(
    override val algebra: SBAlgebra<T, U>, val biRelations: Pair<Monomial<T, U>, Monomial<T, U>>
) : Indec<T>() {
    init {
        require(biRelations.first.from == biRelations.second.from) {
            "Sources of ${biRelations.first} and ${biRelations.second} don't coincide."
        }
        require(biRelations.first.to == biRelations.second.to) {
            "Targets of ${biRelations.first} and ${biRelations.second} don't coincide."
        }
        require(algebra.overAlgebra.isLegal(biRelations.first.toWord())) {
            "${biRelations.first} vanishes."
        }
        require(algebra.overAlgebra.isLegal(biRelations.second.toWord())) {
            "${biRelations.first} vanishes."
        }
        require(biRelations.first.length >= 2) {
            "${biRelations.first} is an arrow."
        }
        require(biRelations.second.length >= 2) {
            "${biRelations.second} is an arrow."
        }
        require(biRelations.first != biRelations.second) {
            "Trivial relations."
        }
    }

    val top = biRelations.first.from
    val socle = biRelations.first.to

    override fun dim(): Int = biRelations.first.length + biRelations.second.length

    override fun isProjective(): Boolean = true

    override fun isInjective(): Boolean = true
    override fun ext1(other: Indec<T>): Int = 0

    override fun topVertices(): List<T> = listOf(top)

    override fun socleVertices(): List<T> = listOf(socle)

    override fun radical(): List<StringIndec<T, U>> {
        val leftPart = biRelations.first.toWord().drop(1)
        val rightPart = biRelations.second.toWord().drop(1)
        return listOf(StringIndec.from(algebra, leftPart * (!rightPart)))
    }

    override fun coradical(): List<StringIndec<T, U>> {
        val leftPart = biRelations.first.toWord().dropLast(1)
        val rightPart = biRelations.second.toWord().dropLast(1)
        return listOf(StringIndec.from(algebra, (!leftPart) * rightPart))
    }

    override fun _syzygy(): List<StringIndec<T, U>> = listOf()

    override fun cosyzygy(): List<StringIndec<T, U>> = listOf()

    override fun sinkSequence(): Pair<List<StringIndec<T, U>>, StringIndec<T, U>?> {
        return listOf(radical()[0]) to null
    }

    override fun sourceSequence(): Pair<List<StringIndec<T, U>>, StringIndec<T, U>?> {
        return listOf(coradical()[0]) to null
    }

    override fun vertexList(): List<T> {
        return biRelations.first.toWord().dropLast(1).vertexList() + biRelations.second.toWord().drop(1).vertexList()
    }

    override fun isIsomorphic(other: Indec<T>): Boolean {
        require(this.algebra == other.algebra) { "Not over the same algebra." }
        if (other is StringIndec<*, *>) return false
        else if (other is BiserialIndec<*, *>) {
            return (biRelations == other.biRelations ||
                    biRelations.second to biRelations.first == other.biRelations)
        }
        TODO()
    }

    override fun injStableHom(other: Indec<T>): Int = 0

    override fun stableHom(other: Indec<T>): Int = 0

    override fun hom(other: Indec<T>): Int {
        return other.vertexList().filter { it == top }.size
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BiserialIndec<*, *>

        if (algebra != other.algebra) return false

        if (biRelations != other.biRelations) return false

        return true
    }

    override fun hashCode(): Int {
        return biRelations.hashCode()
    }

    fun flip(): BiserialIndec<T, U> {
        return BiserialIndec(
            algebra, biRelations.second to biRelations.first
        )
    }

    override fun toString(): String {
        return "${biRelations.first}=${biRelations.second}"
    }

}