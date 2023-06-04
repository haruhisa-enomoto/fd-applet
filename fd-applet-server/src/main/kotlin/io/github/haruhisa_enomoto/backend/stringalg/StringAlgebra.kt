package io.github.haruhisa_enomoto.backend.stringalg

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.sbalgebra.SbAlgebra

/**
 * A data class for string algebras.
 * @property isRepFinite if the algebra is representation-finite.
 * This is equivalent to that there are only finitely many legal words.
 * @throws IllegalArgumentException if the algebra is not a string algebra.
 */
open class StringAlgebra<T, U>(
    quiver: Quiver<T, U>, relations: List<Monomial<T, U>>
) : MonomialAlgebra<T, U>(quiver, relations) {
    init {
        for (vtx in vertices) {
            require(arrows.filter { it.from == vtx }.size <= 2) {
                "Too many arrows starting at $vtx"
            }
            require(arrows.filter { it.to == vtx }.size <= 2) {
                "Too many arrows ending at $vtx"
            }
        }
        for (ar in arrows) {
            require(arrows.filter { it.from == ar.to }.map { ar * it }
                .filter { isLegal(it) }.size <= 1) { "Few relations. Both two paths with length 2 beginning with $ar do not vanish." }
            require(arrows.filter { it.to == ar.from }.map { it * ar }
                .filter { isLegal(it) }.size <= 1) { "Few relations. Both two paths with length 2 ending with $ar do not vanish." }
        }
    }

    constructor(algebra: MonomialAlgebra<T, U>) : this(algebra.quiver, algebra.relations)

    override fun isRepFinite(): Boolean {
        return this.isWordFinite
    }

    override fun projAt(vtx: T): StringIndec<T, U> {
        val strings = pathsFrom(vtx, onlyMaximal = true)
        when (strings.size) {
            1 -> return StringIndec.from(this, strings[0], check = false)
            2 -> return StringIndec.from(this, (!strings[0]) * strings[1], check = false)
        }
        throw IllegalStateException("Something is wrong")
    }

    override fun injAt(vtx: T): StringIndec<T, U> {
        val strings = pathsTo(vtx, onlyMaximal = true)
        when (strings.size) {
            1 -> return StringIndec.from(this, strings[0], check = false)
            2 -> return StringIndec.from(this, strings[0] * !(strings[1]), check = false)
        }
        throw IllegalStateException("Something is wrong")
    }

    override fun numberOfIndecs(): Int? {
        return if (isRepFinite()) {
            stringIndecs().size
        } else null
    }

    override fun toRfAlgebra(): RfAlgebra<T> {
        require(isRepFinite()) {
            "Not representation-finite."
        }
        val allModules = stringIndecs(nonIsomorphic = true)
        fun normalize(mX: Indec<T>): Indec<T> {
            // Since rep-fin string algebra has no band modules,
            // every indec should be a string module,
            // so this cast should be possible.
            mX as StringIndec<T, *>
            return if (mX in allModules) mX else !mX
        }
        return RfAlgebra(this, allModules) { normalize(it) }
    }

    fun toSBAlgebra(): SbAlgebra<T, U> {
        return SbAlgebra(this, biRelations = listOf())
    }

}