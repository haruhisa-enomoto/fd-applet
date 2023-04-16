package io.github.haruhisa_enomoto.backend.stringalg

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RFAlgebra
import io.github.haruhisa_enomoto.backend.quiver.*

/**
 * A data class for monomial algebras: an algebra kQ/<R>
 * for R a finite set of "monomial" relations (i.e. paths).
 *
 * @param quiver the underlying quiver.
 * @param initialRelations the list of monomial relations.
 * Overlaps (both "a*b" and "a*b*c") or duplication is allowed,
 * but removed in [relations]
 * @property relations the list of monomial relations without overlaps.
 * @property vertices the list of vertices of the quiver.
 * @property arrows the list of arrows of the quiver.
 * @throws IllegalArgumentException if either of the following happens:
 * - Some of [relations] is not a path in [quiver].
 * - The length of some of [relations] is <= 1.
 * - There are overlaps in [relations].
 */
open class MonomialAlgebra<T, U>(
    quiver: Quiver<T, U>, initialRelations: List<Monomial<T, U>>
) : QuiverAlgebra<T, U>(quiver) {

    /** The list of monomial relations without overlaps. */
    val relations: List<Monomial<T, U>>

    init {
        val temp = mutableListOf<Monomial<T, U>>()
        for (rel in initialRelations) {
            if (rel in temp) continue
            require(rel.arrows.all { it in arrows }) {
                "Each arrow in relations should be in the quiver."
            }
            require(rel.length >= 2) { "The length of each relation must be >= 2." }
            // Construct [normalizedRelations]: delete overlapping relations.
            var minimal = true
            for (rel2 in initialRelations) {
                if (rel == rel2) continue
                // Check whether there [rel] contains smaller [rel2] for some [rel2].
                if (rel.arrows.windowed(rel2.length).any {
                        it.toMonomial() == rel2
                    }) {// If so, then [rel] is not needed.
                    minimal = false
                    break
                }
            }
            if (minimal) temp.add(rel)
        }
        relations = temp
    }

    private val doubledArrows = arrows.map { it.toLetter() } + arrows.map { !it }
    private val forbiddenWords =
        relations.map { it.toWord() } + relations.map { !it.toWord() } + arrows.map { it * !it } + arrows.map { !it * it }

    // lazy delegation for heavy functions.

    private val pathAutomaton by lazy { makePathAutomaton() }
    private val wordAutomaton by lazy { makeWordAutomaton() }
    private val isFD: Boolean by lazy { fdCheck() }
    val isBandFinite: Boolean by lazy { bandFiniteCheck() }
    override val isWordFinite: Boolean by lazy { wordFiniteCheck() }

    override fun simpleAt(vtx: T): Indec<T> {
        return StringIndec.from(this, vtx.toTrivialWord(), check = false)
    }

    override fun isStringAlgebra(): Boolean {
        return try {
            StringAlgebra(this)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun isGentleAlgebra(): Boolean {
        return try {
            GentleAlgebra(this)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun numberOfIndecs(): Int? {
        TODO("Only supported for special biserial algebras.")
    }

    override fun projAt(vtx: T): Indec<T> {
        TODO("Only supported for special biserial algebras.")
    }

    override fun injAt(vtx: T): Indec<T> {
        TODO("Only supported for special biserial algebras.")
    }

    override fun printInfo() {
        println("A monomial algebra with quiver:")
        quiver.printInfo()
        println("---- and relations ----")
        println(relations)
    }

    override fun isLegal(word: Word<T, U>, checkOnlyLast: Boolean): Boolean {
        require(word.letters.all { it in doubledArrows }) {
            "Each letter should be in the quiver."
        }
        if (checkOnlyLast) {
            return forbiddenWords.all { it.letters != word.letters.takeLast(it.length) }
        }
        for (rel in forbiddenWords) {
            if (word.letters.windowed(rel.length).any { it == rel.letters }) {
                return false
            }
        }
        return true
    }

    /** (Possibly infinite) sequence of paths starting at [vtx]
     * with length bounded by [lengthBound] (no restriction if `null`).
     * If [onlyMaximal], then only maximal paths are yield.
     * Breadth first search. */
    private fun pathsSequenceFrom(
        vtx: T, lengthBound: Int? = null, onlyMaximal: Boolean = false
    ): Sequence<Word<T, U>> {
        return wordsStartingWith(
            vtx.toTrivialWord(), lengthBound, addOnlyArrow = true, onlyMaximal = onlyMaximal
        )
    }

    /** (Possibly infinite) sequence of paths ending at [vtx]
     * with length bounded by [lengthBound] (no restriction if `null`).
     * If [onlyMaximal], then only maximal paths are yield.
     * Breadth first search. */
    private fun pathsSequenceTo(
        vtx: T, lengthBound: Int? = null, onlyMaximal: Boolean = false
    ): Sequence<Word<T, U>> {
        return wordsEndingWith(
            vtx.toTrivialWord(), lengthBound, addOnlyArrow = true, onlyMaximal = onlyMaximal
        )
    }

    /**
     * Returns the list of all paths starting at [vtx]
     * with length bounded by [lengthBound] (no restriction if `null`).
     *
     * @param onlyMaximal if true, then only reports maximal paths.
     */
    fun pathsFrom(vtx: T, lengthBound: Int? = null, onlyMaximal: Boolean = false): List<Word<T, U>> {
        require(lengthBound != null || fdCheck(vtx)) {
            "There are infinitely many paths!"
        }
        return pathsSequenceFrom(vtx, lengthBound, onlyMaximal).toList()
    }

    /**
     * Returns the list of all paths ending at [vtx]
     * with length bounded by [lengthBound] (no restriction if `null`).
     *
     * @param onlyMaximal if true, then only reports maximal paths.
     */
    fun pathsTo(vtx: T, lengthBound: Int? = null, onlyMaximal: Boolean = false): List<Word<T, U>> {
        require(lengthBound != null || isFiniteDimensional()) {
            "There are infinitely many paths!"
        }
        return pathsSequenceTo(vtx, lengthBound, onlyMaximal).toList()
    }

    override fun dim(): Int? {
        return if (isFD) {
            vertices.sumOf { pathsFrom(it).size }
        } else null
    }

    /**
     * Returns the list of legal words starting at [vtx]
     * with length bounded by [lengthBound].
     *
     * @throws IllegalArgumentException if there are infinitely many words.
     */
    private fun wordsFrom(vtx: T, lengthBound: Int? = null): List<Word<T, U>> {
        require(lengthBound != null || wordFiniteCheck(vtx)) {
            "There are infinitely many words!"
        }
        return wordsStartingWith(vtx.toTrivialWord(), lengthBound).toList()
    }

    /**
     * Returns the list of all legal words.
     * Note that a word and its inverse is distinguished.
     */
    private fun words(lengthBound: Int? = null): List<Word<T, U>> {
        return vertices.flatMap { wordsFrom(it, lengthBound) }
    }

    /**
     * Returns the list of all primitive bands.
     * It reports each equivalent band only once.
     */
    fun primitiveBands(): List<Word<T, U>> {
        val candidates = wordAutomaton.simpleCycles().map { cycle ->
            cycle.letters.map { it.label }.toWord()
        }.toList()
        // Now candidates contains, for each band, two words:
        // a word and its inverse (possibly rotated).
        // We must reduce equivalent bands.

        // Define a helper function to find rotated word.
        fun isRotation(list1: List<Any>, list2: List<Any>): Boolean {
            if (list1.size != list2.size) return false
            val joinedList = list2 + list2
            return joinedList.windowed(list1.size).any { it == list1 }
        }

        val result = mutableListOf<Word<T, U>>()
        for (word in candidates) {
            if (result.any {
                    isRotation(word.letters, (!it).letters)
                }) continue
            result.add(word)
        }
        return result
    }

    /**
     * A transition function for the word automaton.
     * For a state `(vtx, word)` and `letter`, the next transition is possible
     * if `longWord = word * letter` is well-defined and legal,
     * otherwise returns `null`.
     * If possible, the next state is `(letter.to, nextWord)`,
     * where `nextWord` is the longest suffix of `longWord` which is "dangerous".
     * Here a word is "dangerous" if it is a prefix of some [forbiddenWords],
     * i.e. relations, inverse relations, or `ar * !ar` or `!ar * ar`.
     * By the last two, `nextWord` has at least length 1.
     */
    private fun transition(state: Pair<T, Word<T, U>>, letter: Letter<T, U>): Pair<T, Word<T, U>>? {
        val longWord = try { // Try to see whether `word * letter` is well-defined.
            state.second * letter
        } catch (e: IllegalArgumentException) {
            null
        }
        if (longWord == null || !isLegal(longWord, checkOnlyLast = true)) {
            return null
        }
        // If `longWord` is like `a * b * !c` or `!a * b * c`, then `nextWord` is the last letter.
        // This part is to reduce computation.
        if (longWord.letters.first().isArrow != letter.isArrow) {
            return Pair(letter.to, letter.toWord())
        }
        for (i in 0 until longWord.length) {
            val temp = longWord.drop(i) // Consider a suffix of `longWord` from largest.
            // Then check whether there are any forbidden words which begin with it.
            if (forbiddenWords.any { it.letters.take(temp.length) == temp.letters }) {
                return Pair(letter.to, temp)
            }
        }
        // The above loop should return some value, because `letter` is always dangerous.
        throw IllegalStateException("Something is wrong")
    }

    /**
     * Makes a path automaton, a sub automaton of [wordAutomaton] where transition is
     * only allowed for arrows.
     * See [transition] for the construction of an automaton.
     */
    private fun makePathAutomaton(): Quiver<Pair<T, Word<T, U>>, Letter<T, U>> {
        val states = vertices.map { Pair<T, Word<T, U>>(it, it.toTrivialWord()) }.toMutableList()
        val queue = ArrayDeque<Pair<T, Word<T, U>>>()
        val transitionArrows = mutableListOf<Arrow<Pair<T, Word<T, U>>, Letter<T, U>>>()

        queue.addAll(states)
        while (queue.isNotEmpty()) {
            val currentState = queue.removeFirst()
            val vtx = currentState.first
            for (ar in arrows.filter { it.from == vtx }) {
                val nextState = transition(currentState, ar.toLetter()) ?: continue
                transitionArrows.add(Arrow(ar.toLetter(), currentState, nextState))
                if (nextState in states) continue
                states.add(nextState)
                queue.add(nextState)
            }
        }
        return Quiver(states, transitionArrows)
    }

    /**
     * Makes a word automaton (as a quiver). See [transition] for details.
     */
    private fun makeWordAutomaton(): Quiver<Pair<T, Word<T, U>>, Letter<T, U>> {
        val states = vertices.map { Pair<T, Word<T, U>>(it, it.toTrivialWord()) }.toMutableList()
        val queue = ArrayDeque<Pair<T, Word<T, U>>>()
        val transitionArrows = mutableListOf<Arrow<Pair<T, Word<T, U>>, Letter<T, U>>>()

        queue.addAll(states)
        while (queue.isNotEmpty()) {
            val currentState = queue.removeFirst()
            val vtx = currentState.first
            for (ar in doubledArrows.filter { it.from == vtx }) {
                val nextState = transition(currentState, ar) ?: continue
                transitionArrows.add(Arrow(ar, currentState, nextState))
                if (nextState in states) continue
                states.add(nextState)
                queue.add(nextState)
            }
        }
        return Quiver(states, transitionArrows)
    }

    private fun fdCheck(vtx: T? = null): Boolean {
        return if (vtx != null) {
            pathAutomaton.isAcyclic(Pair(vtx, vtx.toTrivialWord()))
        } else pathAutomaton.isAcyclic()
    }

    override fun isRepFinite(): Boolean {
        TODO("Only supported for special biserial algebras.")
    }

    override fun toRFAlgebra(): RFAlgebra<T> {
        TODO("Only supported for special biserial algebras.")
    }

    private fun wordFiniteCheck(vtx: T? = null): Boolean {
        return if (vtx != null) {
            wordAutomaton.isAcyclic(Pair(vtx, vtx.toTrivialWord()))
        } else wordAutomaton.isAcyclic()
    }

    private fun bandFiniteCheck(): Boolean {
        return wordAutomaton.primitiveCycleFinite()
    }


    override fun stringIndecs(
        lengthBound: Int?, nonIsomorphic: Boolean
    ): List<StringIndec<T, U>> {
        val wordList = mutableListOf<Word<T, U>>()
        for (word in words(lengthBound)) {
            if (!nonIsomorphic || (!word) !in wordList) {
                wordList.add(word)
            }
        }
        return wordList.map { StringIndec.from(this, it, check = false) }
    }

    fun make(): MonomialAlgebra<T, U> {
        return if (this.isGentleAlgebra()) GentleAlgebra(this)
        else if (this.isStringAlgebra()) StringAlgebra(this)
        else this
    }
}