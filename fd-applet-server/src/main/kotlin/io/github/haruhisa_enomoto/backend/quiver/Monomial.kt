package io.github.haruhisa_enomoto.backend.quiver

import kotlinx.serialization.Serializable

/**
 * A data class representing a monomial: a path of length greater than 0.
 *
 * @param T the type of vertex labels.
 * @param U the type of arrow labels.
 * @property length the length of the monomial, which is the number of arrows.
 * @property from the source vertex of the first arrow in the monomial.
 * @property to the target vertex of the last arrow in the monomial.
 * @property arrows the list of arrows forming the monomial.
 * @constructor Creates a monomial with the given list of arrows.
 */
@Serializable
data class Monomial<T, U>(val arrows: List<Arrow<T, U>>) {
    init {
        require(arrows.isNotEmpty()) {
            "The length of the monomial must be positive."
        }
        require(arrows.all { it.label != null }) {
            "Arrow labels are required."
        }
        arrows.zipWithNext().forEach { (arrow1, arrow2) ->
            require(arrow1.to == arrow2.from) {
                "Target of $arrow1 and source of $arrow2 do not coincide."
            }
        }
    }

    val length = arrows.size
    val from = arrows.first().from
    val to = arrows.last().to
    
    /**
     * Converts this monomial to a word.
     *
     * @return a Word object representing this monomial.
     */
    fun toWord(): Word<T, U> {
        return Word.from(
            arrows.map { it.toLetter() }, arrows.first().from, arrows.last().to, check = false
        )
    }

    /**
     * Returns a list of labels for the arrows in this monomial.
     *
     * @return a list of arrow labels.
     */
    fun toList(): List<U> {
        return arrows.map { it.label!! }
    }

    override fun toString(): String {
        return arrows.joinToString(separator = "*") { it.toString() }
    }
}