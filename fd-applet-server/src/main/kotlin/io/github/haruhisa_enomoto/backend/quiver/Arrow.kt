package io.github.haruhisa_enomoto.backend.quiver

import kotlinx.serialization.Serializable

/**
 * A data class representing arrows in a quiver.
 *
 * @param T the type of vertex labels.
 * @param U the type of arrow labels.
 * @property from the source vertex of the arrow.
 * @property to the target vertex of the arrow.
 * @property label the label of an arrow. If `null`, the arrow is considered anonymous,
 * which is especially useful for translation quivers.
 * @property isTau if true, this arrow represents a translation arrow in the translation quiver.
 *
 * @constructor Creates an arrow with the given source, target, and label.
 */
@Serializable
data class Arrow<T, U>(
    val label: U? = null, val from: T, val to: T, val isTau: Boolean = false
) {
    /**
     * Converts this arrow into a letter.
     *
     * @return a Letter object representing this arrow.
     */
    fun toLetter(): Letter<T, U> = Letter(this, isArrow = true)

    /**
     * Converts this arrow into a single-letter word.
     *
     * @return a Word object representing this arrow as a single-letter word.
     */
    fun toWord(): Word<T, U> = toLetter().toWord()

    /**
     * Returns the negation (inverse) of this arrow as a letter.
     *
     * @return the negation of this arrow as a Letter object.
     */
    operator fun not(): Letter<T, U> = toLetter().not()

    /**
     * Concatenates this arrow with [other] arrow, forming a new word.
     *
     * @param other the arrow to be concatenated with this arrow.
     * @return a Word object representing the concatenation of this arrow and the other arrow.
     */
    operator fun times(other: Arrow<T, U>): Word<T, U> {
        return this.toWord() * other.toWord()
    }

    /**
     * Concatenates this arrow with [other] letter, forming a new word.
     *
     * @param other the letter to be concatenated with this arrow.
     * @return a Word object representing the concatenation of this arrow and the other letter.
     */
    operator fun times(other: Letter<T, U>): Word<T, U> {
        return this.toWord() * other.toWord()
    }

    /**
     * Concatenates this arrow with [other] word, forming a new word.
     *
     * @param other the word to be concatenated with this arrow.
     * @return a Word object representing the concatenation of this arrow and the other word.
     */
    operator fun times(other: Word<T, U>): Word<T, U> {
        return this.toWord() * other
    }

    override fun toString(): String {
        return label.toString()
    }


    /**
     * Returns a string representation of this arrow with source, target, and label information.
     *
     * @return a string representation of this letter with source, target, and label information.
     */
    fun infoString(): String = when {
        label == null && !isTau -> "$from ----> $to"
        label == null -> "(translation) $from ----> $to"
        else -> "$label: $from ----> $to"
    }
}