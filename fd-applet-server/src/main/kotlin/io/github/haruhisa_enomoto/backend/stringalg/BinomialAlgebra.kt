package io.github.haruhisa_enomoto.backend.stringalg


import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Word
import io.github.haruhisa_enomoto.backend.sbalgebra.SbAlgebra

open class BinomialAlgebra<T, U>(
    val overAlgebra: MonomialAlgebra<T, U>,
    val biRelations: List<Pair<Monomial<T, U>, Monomial<T, U>>>
) : QuiverAlgebra<T, U>(overAlgebra.quiver) {
    init {
        for (com in biRelations) {
            require(com.first.from == com.second.from) {
                "Sources of ${com.first} and ${com.second} don't coincide."
            }
            require(com.first.to == com.second.to) {
                "Targets of ${com.first} and ${com.second} don't coincide."
            }
            require(overAlgebra.isLegal(com.first.toWord())) {
                "${com.first} vanishes."
            }
            require(overAlgebra.isLegal(com.second.toWord())) {
                "${com.first} vanishes."
            }
            require(com.first.length >= 2) {
                "${com.first} is an arrow."
            }
            require(com.second.length >= 2) {
                "${com.second} is an arrow."
            }
            require(com.first != com.second) {
                "Trivial relations."
            }
        }
    }

    override fun printInfo() {
        println("A binomial algebra with quiver:")
        quiver.printInfo()
        println("---- and monomial relations ----")
        println(overAlgebra.relations)
        println("---- and commutative relations ----")
        println(biRelations)
    }

    override val isWordFinite: Boolean = overAlgebra.isWordFinite

    override fun isLegal(word: Word<T, U>, checkOnlyLast: Boolean): Boolean {
        TODO("Not yet implemented")
    }

    override fun stringIndecs(lengthBound: Int?, nonIsomorphic: Boolean): List<StringIndec<T, U>> {
        TODO("Not yet implemented")
    }

    override fun isStringAlgebra(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isGentleAlgebra(): Boolean {
        TODO("Not yet implemented")
    }

    override fun numberOfIndecs(): Int? {
        TODO("Not yet implemented")
    }

    override fun dim(): Int? {
        TODO("Not yet implemented")
    }

    override fun isRepFinite(): Boolean {
        TODO("Not yet implemented")
    }

    override fun toRfAlgebra(): RfAlgebra<T> {
        TODO("Not yet implemented")
    }

    override fun injAt(vtx: T): Indec<T> {
        TODO("Not yet implemented")
    }

    override fun projAt(vtx: T): Indec<T> {
        TODO("Not yet implemented")
    }

    override fun simpleAt(vtx: T): Indec<T> {
        TODO("Not yet implemented")
    }

    fun make(): QuiverAlgebra<T, U> {
        if (biRelations.isEmpty()) {
            return overAlgebra.make()
        }
        return try {
            SbAlgebra(this)
        } catch (e: IllegalArgumentException) {
            this
        }
    }
}