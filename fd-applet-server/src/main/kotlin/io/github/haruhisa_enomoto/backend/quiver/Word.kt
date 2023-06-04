package io.github.haruhisa_enomoto.backend.quiver

import kotlinx.serialization.Serializable

/**
 * A data class representing a sequence of letters with compatible sources and targets.
 *
 * Note: This allows a word of the form "a * !a".
 * This word is not allowed when checked in [MonomialAlgebra<T, U>.isLegal].
 *
 * @param T the type of vertex labels
 * @param U the type of arrow labels
 * @property letters a list of letters (possibly empty)
 * @property from the source vertex of the word
 * @property to the target vertex of the word
 * @property length the length of the word, not the number of visited vertices
 */
@Serializable
class Word<T, U> private constructor(
    val letters: List<Letter<T, U>>, val from: T, val to: T
): Comparable<Word<*, *>> {
    companion object {
        /**
         * Creates a new instance of the [Word] class.
         *
         * @param letters a list of letters (possibly empty)
         * @param from the source vertex of the word
         * @param to the target vertex of the word
         * @param check if true, validates the word before creating the instance
         * @return a new instance of the [Word] class
         * @throws IllegalArgumentException if [check] is true and the word is invalid
         */
        fun <T, U> from(
            letters: List<Letter<T, U>>, from: T, to: T, check: Boolean = true
        ): Word<T, U> {
            if (check) {
                if (letters.isEmpty()) {
                    require(from == to) { "Source and target of a trivial word should coincide." }
                } else {
                    require(letters.first().from == from && letters.last().to == to) {
                        "Source or target of a word doesn't match."
                    }
                    for ((ar1, ar2) in letters.zipWithNext()) {
                        require(ar1.to == ar2.from) { "Target of $ar1 and source of $ar2 don't coincide." }
                    }
                }
            }
            return Word(letters, from, to)
        }
    }

    /**
     * Retrieves the [n]-th letter of the word.
     *
     * @param n the index of the letter
     * @return the letter at the specified index
     */
    operator fun get(n: Int) = letters[n]

    val length = letters.size

    override fun toString(): String {
        if (letters.isEmpty()) return to.toString()
        return letters.joinToString(separator = "*") { it.toString() }
    }

    /**
     * Returns the string representation of the word with source, target, and letter information.
     *
     * @return the string representation of the word with source, target, and letter information.
     */
    fun infoString(): String {
        if (letters.isEmpty()) return from.toString() + " (trivial word)"
        return letters.joinToString(separator = " ") { it.toString() } + ": " + letters.fold(from.toString()) { str, ar ->
            str + " --" + ar.toString() + "--> " + ar.to
        }
    }

    /**
     * Concatenates `this` word with the given [other] arrow.
     *
     * @param other an arrow to concatenate with
     * @return the concatenated word
     */
    operator fun times(other: Arrow<T, U>): Word<T, U> {
        return this * other.toWord()
    }

    /**
     * Concatenates `this` word with the given [other] letter.
     *
     * @param other a letter to concatenate with
     * @return the concatenated word
     */
    operator fun times(other: Letter<T, U>): Word<T, U> {
        return this * other.toWord()
    }

    /**
     * Concatenates `this` word with the given [other] word.
     *
     * @param other a word to concatenate with
     * @return the concatenated word
     */
    operator fun times(other: Word<T, U>): Word<T, U> {
        require(this.to == other.from) { "Cannot concatenate." }
        return Word(this.letters + other.letters, this.from, other.to)
    }


    /**
     * Returns the inverse of `this` word.
     *
     * @return the inverse of the word
     */
    operator fun not(): Word<T, U> {
        return Word(letters.reversed().map { !it }, to, from)
    }

    /**
     * Retrieves the vertex visited by the word at the given [index].
     *
     * @param index the index of the visited vertex
     * @return the vertex visited at the specified index
     */
    fun getVertexAt(index: Int): T {
        return if (index == 0) from else letters[index - 1].to
    }

    /**
     * Returns a list of vertices visited by `this` word.
     *
     * @return a list of vertices visited by the word
     */
    fun vertexList(): List<T> {
        return letters.map { it.from } + to
    }

    /**
     * Returns a set of vertices visited by `this` word.
     *
     * @return a set of visited vertices
     */
    fun support(): Set<T> {
        return vertexList().toSet()
    }

    /**
     * Drops the first [n] letters from the word.
     * If [n] equals [length], returns the trivial word at [to].
     *
     * @param n the number of letters to drop
     * @return the word with the first [n] letters dropped
     */
    fun drop(n: Int): Word<T, U> {
        return subWord(n, length)
    }

    /**
     * Drops the last [n] letters from the word.
     * If [n] equals [length], returns the trivial word at [from].
     *
     * @param n the number of letters to drop
     * @return the word with the last [n] letters dropped
     */
    fun dropLast(n: Int): Word<T, U> {
        return subWord(0, length - n)
    }

    /**
     * Returns a "substring" of the word with indices from [i] to [j].
     *
     * Example: word = a_0 a_1 a_2.
     * Suppose 0 <= i <= j <= 3.
     * If i < j, this returns a_i ... a_{j-1} = [word.subList(i, j)].
     * If i = j, this returns the trivial path at "source of a_i" for i = j < 3,
     * and "target of a_2" if i = j = 3.
     *
     * @param i the starting index
     * @param j the ending index
     * @return the "substring" of the word from [i] to [j]
     */
    fun subWord(i: Int, j: Int): Word<T, U> {
        require(i in 0..j && j <= length) { "Invalid indices." }
        return if (length == 0) { // The case i = j = size = 0 and `this` is a trivial word.
            this
        } else if (i < j) {
            letters.subList(i, j).toWord(check = false)
        } else if (i == j && j == length) {
            letters.last().to.toTrivialWord()
        } else { // The case i = j < size.
            letters[i].from.toTrivialWord()
        }
    }

    fun subWord(range: IntRange): Word<T, U> {
        return subWord(range.first, range.last)
    }

    /**
     * Takes the first [i] letters of the word. If [i] equals 0, returns the trivial path at [from].
     *
     * @param i the number of letters to take
     * @return the word with the first [i] letters
     */
    fun take(i: Int): Word<T, U> {
        return subWord(0, i)
    }

    /**
     * Takes the last [i] letters of the word. If [i] equals [length], returns the trivial path at [to].
     *
     * @param i the number of letters to take
     * @return the word with the last [i] letters
     */
    fun takeLast(i: Int): Word<T, U> {
        return subWord(length - i, length)
    }

    override fun compareTo(other: Word<*, *>): Int {
        // Lexicographic order with respect to letters.
        if (length == 0 && other.length == 0) {
            val thisString = from.toString()
            val otherString = other.from.toString()
            return try {
                thisString.toInt().compareTo(otherString.toInt())
            } catch (e: NumberFormatException) {
                thisString.compareTo(otherString)
            }
        }
        if (length != other.length) return length.compareTo(other.length)
        for (i in 0 until minOf(length, other.length)) {
            val cmp = letters[i].compareTo(other.letters[i])
            if (cmp != 0) return cmp
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Word<*, *>

        if (letters != other.letters) return false
        if (from != other.from) return false
        if (to != other.to) return false

        return true
    }

    override fun hashCode(): Int {
        var result = letters.hashCode()
        result = 31 * result + (from?.hashCode() ?: 0)
        result = 31 * result + (to?.hashCode() ?: 0)
        return result
    }
}