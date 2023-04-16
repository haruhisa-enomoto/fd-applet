package io.github.haruhisa_enomoto.backend.algebra

import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.quiver.Word
import io.github.haruhisa_enomoto.backend.stringalg.StringIndec

abstract class QuiverAlgebra<T, U>(
    val quiver: Quiver<T, U>
): Algebra<T>() {
    final override val vertices = quiver.vertices.toList()
    val arrows = quiver.arrows

    abstract fun printInfo()

    abstract val isWordFinite: Boolean

    /** Returns whether [word] is a "legal" word over the algebra.
     * A word is Legal if it does not contain "!ar * ar" or "ar * !ar",
     * and if one can define a string module from [word].
     * If [checkOnlyLast], then we check only the last part of a word.
     */
    abstract fun isLegal(word: Word<T, U>, checkOnlyLast: Boolean = false): Boolean

    /**
     * Returns the list of all string modules (with length bounded by [lengthBound]).
     * If [nonIsomorphic], then only reports non-isomorphic modules,
     * that is, only reports one of `word` and `!word` for each `word`.
     */
    abstract fun stringIndecs(
        lengthBound: Int? = null, nonIsomorphic: Boolean = true
    ): List<StringIndec<T, U>>

    /**
     * A sequence of legal words starting with [word] under some conditions.
     * By BFS.
     *
     * @param lengthBound only yield words with length bounded by this value.
     * @param addOnlyArrow only add arrows to [word].
     * @param addOnlyInverse only add inverse arrows to [word].
     * @param onlyMaximal only yield maximal words (under the above conditions)
     */
    fun wordsStartingWith(
        word: Word<T, U>,
        lengthBound: Int? = null,
        addOnlyArrow: Boolean = false,
        addOnlyInverse: Boolean = false,
        onlyMaximal: Boolean = false
    ): Sequence<Word<T, U>> = sequence {
        require(isLegal(word)) { "Invalid word." }
        require(lengthBound == null || lengthBound >= word.length) {
            "Length is smaller than the original word."
        }
        val queue = ArrayDeque<Word<T, U>>()
        queue.add(word)
        var isMaximal: Boolean
        while (queue.isNotEmpty()) {
            val currentWord = queue.removeFirst() // for BFS.
            if (!onlyMaximal) {
                if (lengthBound == null) {
                    yield(currentWord)
                } else if (currentWord.length <= lengthBound) {
                    yield(currentWord)
                } else break // if size > [lengthBound]
            }
            isMaximal = true
            if (!addOnlyInverse) {
                for (ar in arrows.filter { currentWord.to == it.from }) {
                    val candidate = currentWord * ar
                    if (isLegal(candidate, checkOnlyLast = true)) {
                        queue.add(candidate)
                        isMaximal = false
                    }
                }
            }
            if (!addOnlyArrow) {
                for (ar in arrows.filter { currentWord.to == it.to }) {
                    val candidate = currentWord * !ar
                    if (isLegal(candidate, checkOnlyLast = true)) {
                        queue.add(candidate)
                        isMaximal = false
                    }
                }
            }
            if (onlyMaximal && isMaximal) {
                yield(currentWord)
            }
        }
    }

    /**
     * A sequence of legal words ending with [word] under some conditions.
     * By BFS.
     *
     * @param lengthBound only yield words with length bounded by this value.
     * @param addOnlyArrow only add arrows to [word].
     * @param addOnlyInverse only add inverse arrows to [word].
     * @param onlyMaximal only yield maximal words (under the above conditions)
     */
    fun wordsEndingWith(
        word: Word<T, U>,
        lengthBound: Int? = null,
        addOnlyArrow: Boolean = false,
        addOnlyInverse: Boolean = false,
        onlyMaximal: Boolean = false
    ): Sequence<Word<T, U>> {
        return wordsStartingWith(
            !word, lengthBound, addOnlyArrow = addOnlyInverse, addOnlyInverse = addOnlyArrow, onlyMaximal
        ).map { !it }
    }
}