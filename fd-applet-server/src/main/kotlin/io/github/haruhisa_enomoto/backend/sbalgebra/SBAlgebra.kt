package io.github.haruhisa_enomoto.backend.sbalgebra

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Word
import io.github.haruhisa_enomoto.backend.quiver.toTrivialWord
import io.github.haruhisa_enomoto.backend.stringalg.BinomialAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringIndec

class SBAlgebra<T, U>(
    overAlgebra: StringAlgebra<T, U>, biRelations: List<Pair<Monomial<T, U>, Monomial<T, U>>>
) : BinomialAlgebra<T, U>(overAlgebra, biRelations) {

    val biserialTopVertices = biRelations.map { it.first.from }
    val biserialSocleVertices = biRelations.map { it.first.to }

    init {
        require(biserialTopVertices.size == biRelations.size) {
            "Duplicate relations."
        }
        require(biserialSocleVertices.size == biRelations.size) {
            "Duplicate relations."
        }
    }

    constructor(algebra: BinomialAlgebra<T, U>) : this(StringAlgebra(algebra.overAlgebra), algebra.biRelations)

    override val isWordFinite: Boolean = overAlgebra.isWordFinite

    val reduction: StringAlgebra<T, U>
        get() {
            return StringAlgebra(
                overAlgebra.quiver,
                overAlgebra.relations + biRelations.flatMap { listOf(it.first, it.second) })
        }

    override fun isStringAlgebra(): Boolean {
        return biRelations.isEmpty()
    }

    override fun isGentleAlgebra(): Boolean {
        return biRelations.isEmpty() && overAlgebra.isGentleAlgebra()
    }

    override fun isLegal(word: Word<T, U>, checkOnlyLast: Boolean): Boolean {
        return reduction.isLegal(word, checkOnlyLast)
    }

    override fun numberOfIndecs(): Int? {
        val numReduction = reduction.numberOfIndecs()
        return if (numReduction == null) null
        else numReduction + biserialTopVertices.size

    }

    override fun dim(): Int? {
        if (reduction.dim() == null) return null
        return vertices.sumOf { projAt(it).dim() }
    }

    override fun isRepFinite(): Boolean {
        return reduction.isRepFinite()
    }

    override fun projAt(vtx: T): Indec<T> {
        return if (vtx in biserialTopVertices) {
            val com = biRelations.first { it.first.from == vtx }
            BiserialIndec(this, com)
        } else {
            StringIndec.from(this, reduction.projAt(vtx).word)
        }
    }

    override fun injAt(vtx: T): Indec<T> {
        return if (vtx in biserialSocleVertices) {
            val com = biRelations.first { it.first.to == vtx }
            BiserialIndec(this, com)
        } else {
            StringIndec.from(this, reduction.injAt(vtx).word)
        }
    }

    override fun simpleAt(vtx: T): Indec<T> {
        return StringIndec.from(this, vtx.toTrivialWord())
    }

    override fun stringIndecs(lengthBound: Int?, nonIsomorphic: Boolean): List<StringIndec<T, U>> {
        return reduction.stringIndecs(lengthBound, nonIsomorphic).map { StringIndec.from(this, it.word) }
    }

    fun biserialModules(): List<BiserialIndec<T, U>> {
        return biRelations.map { com -> BiserialIndec(this, com) }
    }

    override fun toRfAlgebra(): RfAlgebra<T> {
        require(isRepFinite()) {
            "Not representation-finite."
        }
        val allModules = stringIndecs(nonIsomorphic = true) + biserialModules()
        fun normalize(mX: Indec<T>): Indec<T> {
            if (mX is StringIndec<*, *>) {
                mX as StringIndec<T, *>
                return if (mX in allModules) mX else mX.not()
            }
            // Now [mX] should be a biserial module.
            mX as BiserialIndec<T, *>
            return if (mX in allModules) mX else mX.flip()
        }
        return RfAlgebra(this, allModules) { normalize(it) }
    }
}