package io.github.haruhisa_enomoto.backend.utils

import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Quiver

/**
 * A data class representing a list of elements and a binary relation [leqs], denoted as `<=`.
 *
 * @param T The type for elements.
 * @property elements The list of elements.
 * @property leqs The binary relation represented as `Set<Pair<T, T>>`.
 * For example, if `a to b in leq`, it is interpreted as `a <= b`.
 */
data class ListWithLeq<T>(
    val elements: List<T>,
    val leqs: Set<Pair<T, T>>,
    val alwaysPoset: Boolean = true
) : List<T> by elements {

    fun leq(x: T, y: T): Boolean {
        return x to y in leqs
    }

    fun geq(x: T, y: T): Boolean {
        return y to x in leqs
    }

    /**
     * Returns the set of elements <= [x].
     */
    fun down(x: T): List<T> {
        require(x in elements)
        return elements.filter { leq(it, x) }
    }

    /**
     * Returns the set of elements <= [x].
     */
    fun up(x: T): List<T> {
        require(x in elements)
        return elements.filter { geq(it, x) }
    }

    /**
     * Returns whether this order gives a poset structure:
     * reflexive, transitive, and antisymmetric.
     */
    fun isPoset(): Boolean {
        for (x in elements) {
            if (!leq(x, x)) return false
            for (y in down(x)) {// y <= x
                if (leq(x, y) && x != y) return false
                for (z in down(y)) {// z <= y
                    if (!leq(z, x)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    /**
     * Returns the Hasse quiver of the order:
     * Vertices are [elements],
     * and draw `x -> y` if x > y and there is no z with x > z > y.
     * This method does not check that the order is a poset if [alwaysPoset] is true.
     */
    fun hasseQuiver(): Quiver<T, Nothing> {
        require(alwaysPoset || isPoset()) { "This is not a poset!" }
        val arrows = mutableListOf<Arrow<T, Nothing>>()
        for (x in elements) {
            for (y in down(x)) {// y <= x
                if (x === y) continue // y < x
                val mid = elements.filter { leq(y, it) && leq(it, x) }
                if (mid.size != 2) continue
                arrows.add(Arrow(null, x, y))
            }
        }
        return Quiver(elements, arrows)
    }
}

fun <T> List<Collection<T>>.toListWithLeq(): ListWithLeq<Collection<T>> {
    val leqs = this.flatMap { c1 ->
        this.filter { c2 -> c2.containsAll(c1) }.map { c2 -> c1 to c2 }
    }.toSet()
    return ListWithLeq(this, leqs)
}
