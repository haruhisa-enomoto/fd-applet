package io.github.haruhisa_enomoto.backend.quiver

import kotlinx.serialization.Serializable

/**
 * A data class representing "letters": arrows and their inverses.
 * For example, consider `ar = Arrow("1", "2", "a")`, namely, "a: 1 -> 2".
 * Then `Letter(ar, true)` represents "a", and `Letter(ar, false)` represents the inverse of "a",
 * which is denoted by "!a" in a string.
 *
 * @param T the type of vertex labels.
 * @param U the type of arrow labels.
 * @property arrow the arrow associated with the letter.
 * Note that the same [arrow] is used for both the arrow and its inverse.
 * @property isArrow `true` if the letter represents the arrow, `false` if it represents the inverse of the arrow.
 * @property label the label of the letter. This is the same as [arrow]'s label.
 * @property from the source vertex of the letter (this is [arrow]'s target if [isArrow] is false).
 * @property to the target vertex of the letter (this is [arrow]'s source if [isArrow] is false).
 * @constructor Creates a letter with the given arrow with [isArrow].
 * @throws IllegalArgumentException if [arrow]'s label is `null`.
 */
@Serializable
data class Letter<T, U>(
    val arrow: Arrow<T, U>, val isArrow: Boolean = true
) {
    val label: U
        get() {
            require(arrow.label != null) { "A label is required for letters." }
            return arrow.label
        }
    val from = if (isArrow) arrow.from else arrow.to
    val to = if (isArrow) arrow.to else arrow.from

    override fun toString(): String = if (isArrow) label.toString() else "!" + label.toString()

    /**
     * Returns the inverse of this letter.
     *
     * @return a Letter object representing the inverse of this letter.
     */
    operator fun not(): Letter<T, U> {
        return Letter(arrow, !isArrow)
    }

    /**
     * Returns the concatenated word formed by this letter and [other] arrow.
     *
     * @param other the arrow to be concatenated with this letter.
     * @return a Word object representing the concatenation of this letter and the other arrow.
     */
    operator fun times(other: Arrow<T, U>): Word<T, U> {
        return this.toWord() * other
    }

    /**
     * Returns the concatenated word formed by this letter and [other] letter.
     *
     * @param other the letter to be concatenated with this letter.
     * @return a Word object representing the concatenation of this letter and the other letter.
     */
    operator fun times(other: Letter<T, U>): Word<T, U> {
        return this.toWord() * other
    }

    /**
     * Returns the concatenated word formed by this letter and [other] word.
     *
     * @param other the word to be concatenated with this letter.
     * @return a Word object representing the concatenation of this letter and the other word.
     */
    operator fun times(other: Word<T, U>): Word<T, U> {
        return this.toWord() * other
    }

    /**
     * Converts this letter to a single-letter word.
     *
     * @return a Word object representing this letter as a single-letter word.
     */
    fun toWord(): Word<T, U> {
        return Word.from(listOf(this), from, to, check = false)
    }

    /**
     * Returns a string representation of this letter with source, target, and label information.
     *
     * @return a string representation of this letter with source, target, and label information.
     */
    fun infoString(): String {
        return "${toString()}: $from ----> $to"
    }
}