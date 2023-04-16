package io.github.haruhisa_enomoto.backend.stringalg

import io.github.haruhisa_enomoto.backend.algebra.Indec
import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.quiver.Word
import io.github.haruhisa_enomoto.backend.sbalgebra.BiserialIndec
import io.github.haruhisa_enomoto.backend.sbalgebra.SBAlgebra


/**
 * A class for finite-dimensional string modules over [algebra].
 * Note that [word] is not identified with its inverse here.
 *
 * @property word the legal word (string) corresponding to the module.
 * @throws IllegalArgumentException if [word] is illegal.
 */
class StringIndec<T, U> private constructor(
    override val algebra: QuiverAlgebra<T, U>, val word: Word<T, U>
) : Indec<T>() {

    companion object {
        /**
         * A factory to create an instance of [StringIndec].
         *
         * @param T the type of vertices.
         * @param U the type of arrows.
         * @param algebra the algebra we
         * @param word the word corresponding to the string module.
         * @param check whether to check [word] is legal or not.
         * Default to true. Set to false if you are sure it's legal.
         * @return
         */
        fun <T, U> from(algebra: QuiverAlgebra<T, U>, word: Word<T, U>, check: Boolean = true): StringIndec<T, U> {
            require(!check || algebra.isLegal(word)) {
                "A word should be legal."
            }
            return StringIndec(algebra, word)
        }
    }

    /**
     * Returns a string module with an inverse word.
     */
    operator fun not(): StringIndec<T, U> {
        return StringIndec(algebra, !word)
    }

    override fun toString(): String = word.toString()


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StringIndec<*, *>

        if (algebra != other.algebra) return false
        if (word != other.word) return false

        return true
    }

    /**
     * Hash code. Since we rarely change algebras, only consider [word].
     */
    override fun hashCode(): Int {
        return word.hashCode()
    }

    override fun dim(): Int = word.length + 1

    override fun vertexList() = word.vertexList()
//
//    override fun compareTo(other: StringModule<T, U>): Int {
//        return compareValuesBy(this, other, { it.dim() }, { it.toString().replace("!", "~") })
//    }

    /**
     * Returns a string module corresponding to the subword of a given index.
     * See [Word.subWord] for subwords.
     */
    fun subWord(i: Int, j: Int): StringIndec<T, U> {
        return StringIndec(algebra, word.subWord(i, j))
    }

    fun subWord(range: IntRange): StringIndec<T, U> {
        return subWord(range.first, range.last)
    }

    /**
     * Returns a string module of dropping fist [n] letters.
     * See [Word.drop].
     */
    private fun drop(n: Int): StringIndec<T, U> {
        return StringIndec(algebra, word.drop(n))
    }

    /**
     * Returns a string module of dropping fist [n] letters.
     * See [Word.dropLast].
     */
    private fun dropLast(n: Int): StringIndec<T, U> {
        return StringIndec(algebra, word.dropLast(n))
    }

    /**
     * Returns a string module of taking fist [n] letters.
     * See [Word.drop].
     */
    private fun take(n: Int): StringIndec<T, U> {
        return StringIndec(algebra, word.take(n))
    }

    /**
     * Returns a string module of taking last fist [n] letters.
     * See [Word.drop].
     */
    private fun takeLast(n: Int): StringIndec<T, U> {
        return StringIndec(algebra, word.takeLast(n))
    }

    override fun isIsomorphic(other: Indec<T>): Boolean {
        require(this.algebra == other.algebra) { "Not over the same algebra." }
        if (other is StringIndec<*, *>) {
            if (word.length != other.word.length) return false
            return word == other.word || word == !(other.word)
        } else if (other is BiserialIndec<*, *>) return false
        TODO()
    }

    /**
     * Returns the list of indices `i` such that `subWord(i, i)` is one of the tops.
     */
    fun topIndices(): List<Int> {/* Create a Boolean list whether each letter is arrow or inverse,
        then add `false` to left and `true` to most. e.g.
        `[false, true, true, false, true, false, false, true]`.
        Then by using `zipWithNext()`, search indices of `(false, true)`, e.g.
        `[0, 3, 6]` in the above example.
        Then `subWord(i, i)` is the desired tops for this list.
         */
        val checkList = listOf(false) + word.letters.map { it.isArrow } + true
        return checkList.zipWithNext().withIndex().filter { it.value == Pair(false, true) }.map { it.index }
    }

    /**
     * Returns the list of indices `i` such that `subWord(i, i)` is one of the socles.
     */
    fun socleIndices(): List<Int> {
        val checkList = listOf(true) + word.letters.map { it.isArrow } + false
        return checkList.zipWithNext().withIndex().filter { it.value == Pair(true, false) }.map { it.index }
    }

    override fun topVertices(): List<T> {
        return topIndices().map { word.getVertexAt(it) }
    }

    override fun socleVertices(): List<T> {
        return socleIndices().map { word.getVertexAt(it) }
    }

    /**
     * Returns the list of "graphical submodules" of the module.
     * For example, `word` := a_0 a_1 a_2.
     * Consider `word.subWord(i, j)` for 0 <= i <= j <= 3.
     * This is a "submodule word" if the following conditions are satisfied:
     * - i = 0 or a_{i-1} is an arrow.
     * - j = 3 (= word.size) or a_j is an inverse arrow.
     */
    private fun subModuleWords(): List<Word<T, U>> {
        return subRanges().map { word.subWord(it) }
    }

    private fun subRanges(): List<IntRange> {
        val results = mutableListOf<IntRange>()
        for (i in 0..word.length) {
            if (i != 0 && word[i - 1].isArrow.not()) continue
            for (j in i..word.length) {
                if (j != word.length && word[j].isArrow) continue
                results.add(i..j)
            }
        }
        return results
    }

    /**
     * For example, `word` := a_0 a_1 a_2. Consider `word.subWord(i, j)` for 0 <= i <= j <=
     * `word.size` = 3. This is a "quotient module word" if the following conditions are satisfied:
     * - i = 0 or a_{i-1} is an inverse arrow.
     * - j = 3 (= word.size) or a_j is an arrow.
     */
    private fun quotientModuleWords(): List<Word<T, U>> {
        return quotientRanges().map { word.subWord(it) }
    }

    private fun quotientRanges(): List<IntRange> {
        val results = mutableListOf<IntRange>()
        for (i in 0..word.length) {
            if (i != 0 && word[i - 1].isArrow) continue
            for (j in i..word.length) {
                if (j != word.length && !word[j].isArrow) continue
                results.add(i..j)
            }
        }
        return results
    }

    private fun homBasis(other: StringIndec<T, U>): List<GraphHom<T, U>> {
        val results = mutableListOf<GraphHom<T, U>>()
        val thisQuotRanges = this.quotientRanges()
        val otherSubRanges = other.subRanges()
        for (quotRange in thisQuotRanges) {
            val word1 = this.word.subWord(quotRange)
            for (subRange in otherSubRanges) {
                val word2 = other.word.subWord(subRange)
                if (word1.length != word2.length) continue
                if (word1 == word2 || word1 == (!word2)) {
                    results.add(
                        GraphHom(
                            this, other, Pair(quotRange, subRange)
                        )
                    )
                }
            }
        }
        return results
    }

    /**
     * Returns dim Hom(this, [other]).
     * We **assume** that [other] is over the same algebra for performance.
     */
    private fun privateHom(other: StringIndec<T, U>): Int {
        val thisQuotRanges = this.quotientRanges()
        val otherSubRanges = other.subRanges()
        var count = 0
        for (quotRange in thisQuotRanges) {
            val word1 = this.word.subWord(quotRange)
            for (subRange in otherSubRanges) {
                val word2 = other.word.subWord(subRange)
                if (word1.length != word2.length) continue
                if (word1 == word2 || word1 == (!word2)) {
                    count += 1
                }
            }
        }
        return count
//        return homBasis(other).size
    }

    override fun hom(other: Indec<T>): Int {
        require(this.algebra == other.algebra) { "Not over the same algebra." }
        if (other is StringIndec<*, *>) {
            // Since algebras are equal, the type check should be OK.
            @Suppress("UNCHECKED_CAST") return privateHom(other as StringIndec<T, U>)
        } else if (other is BiserialIndec<*, *>) {
            return this.vertexList().filter { it == other.socle }.size
        }
        TODO(
            "Only supported for morphisms between string modules," +
                    "or between string and biserial modules over special biserial algebras."
        )
    }

    override fun ext1(other: Indec<T>): Int {
        /*
        Take a proj cover and syzygy
        0 -> syzygy -> P -> this -> 0.
        Then
        0 -> (this, other) -> (P, other) -> (syzygy, other) -> Ext^1(this, other) -> 0.
        Using this just calculate dimension.
         */
        val syzygy = this.syzygy()
        val topVertices = this.topVertices()
        val otherVertices = other.vertexList()
        val first = algebra.hom(this, other)
        val second = topVertices.sumOf { vtx -> // (P, other)
            otherVertices.filter { it == vtx }.size
        }
        val third = algebra.hom(syzygy, other)
        return first - second + third
    }

    // Methods related to string combinatorics like hook and cohook.

    /**
     * Returns whether `this` ends on a peak. This means that for every possible arrow `ar`, `this *
     * !ar` is "illegal".
     */
    private fun endsPeak(): Boolean {
        for (ar in algebra.arrows.filter { word.to == it.to }) {
            if (algebra.isLegal(word * !ar)) {
                return false
            }
        }
        return true
    }

    /**
     * Returns whether `this` ends in a deep. This means that for every possible arrow `ar`, `this *
     * ar` is "illegal".
     */
    private fun endsDeep(): Boolean {
        for (ar in algebra.arrows.filter { word.to == it.from }) {
            if (algebra.isLegal(word * ar)) {
                return false
            }
        }
        return true
    }

    /**
     * Returns whether `this` starts on a peak. This means that for every possible arrow `ar`, `ar *
     * this` is "illegal".
     */
    private fun startsPeak(): Boolean {
        return (!this).endsPeak()
    }

    /**
     * Returns whether `this` starts in a deep. This means that for every possible arrow `ar`, `!ar
     * * this` is "illegal".
     */
    private fun startsDeep(): Boolean {
        return (!this).endsDeep()
    }

    /** Returns the list of maximal words of the form `this * ar1 * ar2 * ...`. */
    private fun makeEndDeep(): List<StringIndec<T, U>> {
        return algebra.wordsStartingWith(word, addOnlyArrow = true, onlyMaximal = true)
            .map { StringIndec(algebra, it) }.toList()
    }

    private fun makeStartDeep(): List<StringIndec<T, U>> {
        return (!this).makeEndDeep().map { !it }
    }

    /** Returns the list of maximal words of the form `this * !ar1 * !ar2 * ...`. */
    private fun makeEndPeak(): List<StringIndec<T, U>> {
        return algebra.wordsStartingWith(word, addOnlyInverse = true, onlyMaximal = true)
            .map { StringIndec(algebra, it) }.toList()
    }

    private fun makeStartPeak(): List<StringIndec<T, U>> {
        return (!this).makeEndPeak().map { !it }
    }

    /**
     * If `!a !b c d` which starts in a deep, returns `d`. Returns `null` if `this` does not start
     * in a deep or not contain a hook (i.e. arrow).
     */
    private fun removeLeftHook(): StringIndec<T, U>? {
        require(startsDeep()) { "Should start in a deep." }
        val index = word.letters.indexOfFirst { it.isArrow }
        if (index == -1) return null
        return subWord(index + 1, word.length)
    }

    private fun removeLeftCohook(): StringIndec<T, U>? {
        require(startsPeak()) { "Should start on a peak." }
        val index = word.letters.indexOfFirst { !it.isArrow }
        if (index == -1) return null
        return subWord(index + 1, word.length)
    }

    private fun removeRightHook(): StringIndec<T, U>? {
        val invResult = (!this).removeLeftHook()
        return invResult?.not()
    }

    private fun removeRightCohook(): StringIndec<T, U>? {
        val invResult = (!this).removeLeftCohook()
        //if (invResult == null) return null
        return invResult?.not()
    }

    private fun addRightHook(): List<StringIndec<T, U>> {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            "Only makes sense for special biserial algebras."
        }
        return algebra.arrows.filter { word.to == it.to && algebra.isLegal(word * !it) }.map {
            StringIndec(algebra, word * !it).makeEndDeep()[0]
        }
    }

    private fun addLeftHook(): List<StringIndec<T, U>> {
        return (!this).addRightHook().map { !it }
    }

    private fun addRightCohook(): List<StringIndec<T, U>> {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            "Only makes sense for special biserial algebras."
        }
        return algebra.arrows.filter { word.to == it.from && algebra.isLegal(word * it) }.map {
            StringIndec(algebra, word * it).makeEndPeak()[0]
        }
    }

    private fun addLeftCohook(): List<StringIndec<T, U>> {
        return (!this).addRightCohook().map { !it }
    }

    override fun isProjective(): Boolean {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            TODO("Only supported for special biserial algebras.")
        }
        return if (algebra is StringAlgebra) (top().size == 1) && startsDeep() && endsDeep()
        else {
            algebra as SBAlgebra
            if (topVertices()[0] in algebra.biserialTopVertices) false
            else (top().size == 1) && startsDeep() && endsDeep()
        }
    }

    override fun isInjective(): Boolean {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            TODO("Only supported for special biserial algebras.")
        }
        return if (algebra is StringAlgebra) (socle().size == 1) && startsPeak() && endsPeak()
        else {
            algebra as SBAlgebra
            if (socleVertices()[0] in algebra.biserialSocleVertices) false
            else (socle().size == 1) && startsPeak() && endsPeak()
        }
    }

    override fun sourceSequence(): Pair<List<Indec<T>>, Indec<T>?> {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            TODO("Only supported for special biserial algebras.")
        }
        val socles = socleVertices()
        // Check if the module is radical of a biserial module.
        val isBSRadical = if (algebra is StringAlgebra) false else {
            algebra as SBAlgebra
            (socles.size == 1 && socles[0] in algebra.biserialSocleVertices && dim() + 1 == algebra.injAt(socles[0])
                .dim())
        }
        if (!isBSRadical) {
            val left = startsPeak()
            val right = endsPeak()
            val middle: List<StringIndec<T, U>>
            val tauInverse: StringIndec<T, U>?

            if (!left && !right) {
                // Can add left hook and right hook.
                // Divide case whether `this` is simple or not.
                middle = when (isSimple()) {
                    // If simple, we only have to consider left-sided hook(s).
                    true -> addLeftHook()
                    false -> addLeftHook() + addRightHook()
                }
                tauInverse = when (middle.size) {
                    2 -> addLeftHook()[0].addRightHook()[0]
                    // If 1, then `this` is simple, and tau-inverse is obtained by
                    // dropping first of `addRightHook()`.
                    1 -> addRightHook()[0].drop(1)
                    else -> throw IllegalStateException("Something is wrong")
                }
            } else if (left && !right) {
                middle = (addRightHook() + removeLeftCohook()).filterNotNull()
                tauInverse = addRightHook()[0].removeLeftCohook()!!
            } else if (!left) {
                middle = (addLeftHook() + removeRightCohook()).filterNotNull()
                tauInverse = addLeftHook()[0].removeRightCohook()!!
            } else {
                // Starts and ends in a peak.
                // Irreducible maps are only surjections to cohook-removed ones.
                middle = listOfNotNull(removeLeftCohook(), removeRightCohook())
                tauInverse = if (socleVertices().size == 1) { // Injective case
                    null
                } else {
                    removeLeftCohook()!!.removeRightCohook()!!
                }
            }
            return Pair(middle, tauInverse)
        } else {
            algebra as SBAlgebra
            // Now `this` is rad P for biserial proj-inj [proj].
            // thus 0 -> rad P -> (rad P)/(soc P) + P -> P/soc P -> 0
            // is AR sequence.
            val proj = algebra.injAt(socles[0])
            val corad = proj.coradical()[0]
            return (corad.radical() + proj) to corad
        }
    }

    override fun sinkSequence(): Pair<List<Indec<T>>, Indec<T>?> {
        require(algebra is StringAlgebra || algebra is SBAlgebra) {
            TODO("Only supported for special biserial algebras.")
        }
        val tops = topVertices()
        // Check if the module is coradical of a biserial module.
        val isBSCoradical = if (algebra is StringAlgebra) false else {
            algebra as SBAlgebra
            (tops.size == 1 && tops[0] in algebra.biserialTopVertices && dim() + 1 == algebra.projAt(tops[0]).dim())
        }
        if (!isBSCoradical) {
            val left = startsDeep()
            val right = endsDeep()
            val middle: List<StringIndec<T, U>>
            val tau: StringIndec<T, U>?

            if (!left && !right) {
                middle = when (isSimple()) {
                    true -> addLeftCohook()
                    false -> addLeftCohook() + addRightCohook()
                }
                tau = when (middle.size) {
                    2 -> addLeftCohook()[0].addRightCohook()[0]
                    1 -> addLeftCohook()[0].dropLast(1)
                    else -> throw IllegalStateException("Something is wrong")
                }
            } else if (left && !right) {
                middle = (addRightCohook() + removeLeftHook()).filterNotNull()
                tau = addRightCohook()[0].removeLeftHook()!!
            } else if (!left) {
                middle = (addLeftCohook() + removeRightHook()).filterNotNull()
                tau = addLeftCohook()[0].removeRightHook()!!
            } else {
                middle = listOfNotNull(removeLeftHook(), removeRightHook())
                tau = if (topVertices().size == 1) {
                    null
                } else {
                    removeLeftHook()!!.removeRightHook()!!
                }
            }
            return Pair(middle, tau)
        } else {
            algebra as SBAlgebra
            // Now `this` is P/soc P for biserial proj-inj [proj].
            // thus 0 -> rad P -> (rad P)/(soc P) + P -> P/soc P -> 0
            // is AR sequence.
            val proj = algebra.projAt(topVertices()[0])
            val rad = proj.radical()[0]
            return (rad.coradical() + proj) to rad
        }
    }

    override fun radical(): List<StringIndec<T, U>> {
        val indices = topIndices()
        val result = mutableListOf<StringIndec<T, U>>()
        if (indices[0] != 0) {
            result.add(subWord(0, indices[0] - 1))
        }
        if (indices.last() != word.length) {
            result.add(subWord(indices.last() + 1, word.length))
        }
        for ((i, j) in indices.zipWithNext()) {
            result.add(subWord(i + 1, j - 1))
        }
        return result
    }

    override fun coradical(): List<StringIndec<T, U>> {
        val indices = socleIndices()
        val result = mutableListOf<StringIndec<T, U>>()
        if (indices[0] != 0) {
            result.add(subWord(0, indices[0] - 1))
        }
        if (indices.last() != word.length) {
            result.add(subWord(indices.last() + 1, word.length))
        }
        for ((i, j) in indices.zipWithNext()) {
            result.add(subWord(i + 1, j - 1))
        }
        return result
    }

    /**
     * Helper function to compute syzygies. Assume [mX] has a simple top and is non-simple.
     * Draw a projective cover `P` of [mX] in circle-like form, and
     * try to remove [mX] from P and consider the resulting left and right legs.
     * If `P` is not biserial, then returns the list of two words:
     * left leg (path) and right leg (inverse path).
     * If `P` is biserial, then returns the list of one word:
     * combination of left and right legs (of the form -->--> <--<--).
     */
    private fun mountainToValley(mX: StringIndec<T, U>): List<Word<T, U>> {
        require(mX.topVertices().size == 1)
        require(mX.dim() > 1)
        require(algebra is SBAlgebra || algebra is StringAlgebra)
        val top = mX.topVertices()[0]
        if (algebra is StringAlgebra ||
            top !in (algebra as SBAlgebra).biserialTopVertices
        ) {
            val leftLeg = algebra.wordsEndingWith(
                mX.word, addOnlyInverse = true, onlyMaximal = true
            ).first().not().drop(mX.word.length)
            val rightLeg = algebra.wordsStartingWith(
                mX.word, addOnlyArrow = true, onlyMaximal = true
            ).first().not().dropLast(mX.word.length)
            return listOf(leftLeg, rightLeg)
        } else {
            val com = algebra.biRelations.first { it.first.from == top }.toList().map { it.toWord() }
            val topIndex = mX.topIndices()[0]
            val leftArm = mX.word.take(topIndex)
            val rightArm = mX.word.drop(topIndex)
            val leftProj: Word<T, U>
            val rightProj: Word<T, U>
            if (rightArm.length != 0) {
                rightProj = com.first { it.take(rightArm.length) == rightArm }
                leftProj = com.first { it != rightProj }
            } else {
                leftProj = com.first { it.take(leftArm.length) == !leftArm }
                rightProj = com.first { it != leftProj }
            }
            return listOf(
                leftProj.drop(leftArm.length) * rightProj.drop(rightArm.length).not()
            )
        }
    }

    private fun syzygyWords(): List<Word<T, U>> {
        val indices = (listOf(0) + socleIndices() + listOf(word.length)).distinct()
        val valleyList = indices.zipWithNext().map { (i, j) ->// (i, j) is mountain.
            mountainToValley(subWord(i, j))
        }
        val syzygies = mutableListOf<Word<T, U>>()
        var intermediate: Word<T, U> = when (valleyList[0].size) {
            // Initialize by the left-most part.
            1 -> valleyList[0][0]
            2 -> {
                syzygies.add(valleyList[0][0]) // Later modify
                valleyList[0][1]
            }

            else -> throw IllegalStateException("Wrong valley size.")
        }
        for (valley in valleyList.drop(1)) {
            when (valley.size) {
                1 -> intermediate *= valley[0]
                2 -> {
                    intermediate *= valley[0]
                    syzygies.add(intermediate)
                    intermediate = valley[1]
                }
            }
        }
        syzygies.add(intermediate)
        // Modify left-most and right-most part.
        if (syzygies[0].length != 0) {
            syzygies[0] = syzygies[0].drop(1)
        } else {
            syzygies.removeAt(0)
        }
        if (syzygies[syzygies.lastIndex].length != 0) {
            syzygies[syzygies.lastIndex] = syzygies[syzygies.lastIndex].dropLast(1)
        } else {
            syzygies.removeAt(syzygies.lastIndex)
        }
        return syzygies
    }

    override fun _syzygy(): List<Indec<T>> {
        if (isProjective()) return listOf()
        if (word.length == 0) {// Separately treat simple module case.
            return algebra.projAt(word.from).radical()
        }
//        if (algebra is StringAlgebra) {
//            val syzygies = mutableListOf<StringModule<T, U>>()
//            // First deal with left most part.
//            val startDeepen = this.makeStartDeep()[0]
//            if (startDeepen.word.length > word.length) {
//                syzygies.add(
//                    startDeepen.take(startDeepen.word.length - word.length - 1)
//                )
//            }
//            val endDeepen = this.makeEndDeep()[0]
//            if (endDeepen.word.length > word.length) {
//                syzygies.add(
//                    endDeepen.takeLast(endDeepen.word.length - word.length - 1)
//                )
//            }
//            val indices = topIndices()
//            // Suppose word: 0 <--a-- 1 --b--> 2 <--c-- 3 --d--> 4.
//            // then `indices` is [1, 3].
//            for ((i, j) in indices.zipWithNext()) {
//                // cut by subWord(i, j), to obtain valleys [b*!c]
//                val valley = word.subWord(i, j)
//                // Consider middle valley = b*!c: 0 --b--> 1 <--c-- 2.
//                // (Note that indexing changes!)
//                val bottomIndex = valley.letters.indexOfFirst { !(it.isArrow) } // = 1.
//                // Find a maximal path starting at the source of [valley]
//                // overlapping the letter (b in this case.)
//                val rightProj = algebra.wordsStartingWith(
//                    valley.take(1), addOnlyArrow = true, onlyMaximal = true
//                ).first()
//                // Suppose it is [rightProj] = b*d*e = --b-->--d-->--e-->.
//                // Then drop the overlapping part.
//                val syzygyRight = rightProj.drop(bottomIndex) // = --d-->--e-->.
//                // Do the same thing for left part.
//                // Suppose overlapping path : [leftProj] = !g*!c = <--g--<--c--.
//                val leftProj = algebra.wordsEndingWith(
//                    valley.takeLast(1), addOnlyInverse = true, onlyMaximal = true
//                ).first()
//                val syzygyLeft = leftProj.dropLast(valley.length - bottomIndex)
//                // = <--g--.
//                // Then connect [syzygyLeft] and [syzygyRight].
//                val syzygy = syzygyLeft * syzygyRight // = <--g--|--d-->--e-->
//                // This is an indecomposable summand of syzygy!
//                syzygies.add(StringModule(algebra, syzygy))
//            }
//            return syzygies
//        } else {
        // Cut [this] into mountains.
        // Compute socle indices, and add 0 and last if necessary.
        return syzygyWords().map { StringIndec(algebra, it) }
    }




    /**
     * Helper function to compute cosyzygies. Assume [mX] has a simple socle and is non-simple.
     * Draw an injective hull `I` of [mX] in circle-like form, and
     * try to remove [mX] from I and consider the resulting left and right legs.
     * If `I` is not biserial, then returns the list of two words:
     * left arm (inverse path) and right arm (path).
     * If `I` is biserial, then returns the list of one word:
     * combination of left and right arms (of the form <--<-- -->-->).
     */
    fun valleyToMountain(mX: StringIndec<T, U>): List<Word<T, U>> {
        require(algebra is StringAlgebra || algebra is SBAlgebra)
        require(mX.socleVertices().size == 1)
        require(mX.dim() > 1)
        val socle = mX.socleVertices()[0]
        if (algebra is StringAlgebra || socle !in (algebra as SBAlgebra).biserialSocleVertices) {
            val leftArm = algebra.wordsEndingWith(
                mX.word, addOnlyArrow = true, onlyMaximal = true
            ).first().dropLast(mX.word.length).not()
            val rightArm = algebra.wordsStartingWith(
                mX.word, addOnlyInverse = true, onlyMaximal = true
            ).first().drop(mX.word.length).not()
            return listOf(leftArm, rightArm)
        } else {
            val com = algebra.biRelations.first { it.first.to == socle }.toList().map { it.toWord() }
            val socleIndex = mX.socleIndices()[0]
            val leftLeg = mX.word.take(socleIndex)
            val rightLeg = mX.word.drop(socleIndex)
            val leftInj: Word<T, U>
            val rightInj: Word<T, U>
            if (rightLeg.length != 0) {
                rightInj = com.first { it.takeLast(rightLeg.length) == !rightLeg }
                leftInj = com.first { it != rightInj }
            } else {
                leftInj = com.first { it.takeLast(leftLeg.length) == leftLeg }
                rightInj = com.first { it != leftInj }
            }
            return listOf(
                leftInj.dropLast(leftLeg.length).not() * rightInj.dropLast(rightLeg.length)
            )
        }
    }


    private fun cosyzygyWords(): List<Word<T, U>> {
        val indices = (listOf(0) + topIndices() + listOf(word.length)).distinct()
        val mountainList = indices.zipWithNext().map { (i, j) ->// (i, j) is valley.
            valleyToMountain(subWord(i, j))
        }
        val cosyzygies = mutableListOf<Word<T, U>>()
        var intermediate: Word<T, U> = when (mountainList[0].size) {
            // Initialize by the left-most part.
            1 -> mountainList[0][0]
            2 -> {
                cosyzygies.add(mountainList[0][0])// Later modify.
                mountainList[0][1]
            }
            else -> throw IllegalStateException("Wrong mountain size.")
        }
        for (mountain in mountainList.drop(1)) {
            when (mountain.size) {
                1 -> intermediate *= mountain[0]
                2 -> {
                    intermediate *= mountain[0]
                    cosyzygies.add(intermediate)
                    intermediate = mountain[1]
                }
            }
        }
        cosyzygies.add(intermediate)
        // Modify left-most and right-most part.
        if (cosyzygies[0].length != 0) {
            cosyzygies[0] = cosyzygies[0].drop(1)
        } else {
            cosyzygies.removeAt(0)
        }
        if (cosyzygies[cosyzygies.lastIndex].length != 0) {
            cosyzygies[cosyzygies.lastIndex] = cosyzygies[cosyzygies.lastIndex].dropLast(1)
        } else {
            cosyzygies.removeAt(cosyzygies.lastIndex)
        }
        return cosyzygies
    }

    override fun cosyzygy(): List<Indec<T>> {
        if (isInjective()) return listOf()
        if (word.length == 0) {// Separately treat simple module case.
            return algebra.injAt(word.from).coradical()
        }
//        val cosyzygies = mutableListOf<StringModule<T, U>>()
//        // First deal with left most part.
//        val startPeaked = this.makeStartPeak()[0]
//        if (startPeaked.word.length > word.length) {
//            cosyzygies.add(
//                startPeaked.take(startPeaked.word.length - word.length - 1)
//            )
//        }
//        val endPeaked = this.makeEndPeak()[0]
//        if (endPeaked.word.length > word.length) {
//            cosyzygies.add(
//                endPeaked.takeLast(endPeaked.word.length - word.length - 1)
//            )
//        }
//        val indices = socleIndices()
//        for ((i, j) in indices.zipWithNext()) {
//            val mountain = word.subWord(i, j)
//            // Consider mountain = !b*c: 0 <--b-- 1 --c--> 2.
//            // (Note that indexing changes!)
//            val topIndex = mountain.letters.indexOfFirst { it.isArrow } // = 1.
//            // Find a maximal path ending at the source of [valley]
//            // overlapping the letter (!b in this case.)
//            // Suppose it is [rightInj] = !b*!d*!e = <--b--<--d--<--e--.
//            val rightInj = algebra.wordsStartingWith(
//                mountain.take(1), addOnlyInverse = true, onlyMaximal = true
//            ).first()
//            // Then drop the overlapping part.
//            val cosyzygyRight = rightInj.drop(topIndex) // = <--d--<--e--.
//            // Do the same thing for left part.
//            // Suppose overlapping path : [leftInj] = g*c = --g-->--c-->.
//            val leftInj = algebra.wordsEndingWith(
//                mountain.takeLast(1), addOnlyArrow = true, onlyMaximal = true
//            ).first()
//            val cosyzygyLeft = leftInj.dropLast(mountain.length - topIndex)
//            // = <--g--.
//            // Then connect [syzygyLeft] and [syzygyRight].
//            val cosyzygy = cosyzygyLeft * cosyzygyRight // = <--g--|--d-->--e-->
//            // This is an indecomposable summand of syzygy!
//            cosyzygies.add(StringModule(algebra, cosyzygy))
//        }
        return cosyzygyWords().map { StringIndec(algebra, it) }
    }

    /**
     * Suppose `this` is 0 --a--> 1 <--b-- 2 <--c-- 3 --d--> 4
     * Slice this into mountains:
     * --a--> || <--b-- <--c-- --d-->
     */
    fun projCoverHom(): List<GraphHom<T, U>> {
        // Cut `this` into pieces of mountains!
        require(algebra is StringAlgebra) {
            "Currently only supported for string algebras."
        }
        val indices = socleIndices().toMutableList()
        if (indices.first() != 0) indices.add(0, 0)
        if (indices.last() != word.length) indices.add(word.length)
        if (indices == listOf(0)) indices.add(0) // For simple case.
        val result = mutableListOf<GraphHom<T, U>>()
        for ((i, j) in indices.zipWithNext()) {
            // Looks like i <---<---...<---topIndex--->...--->j.
            val extendLeft = subWord(i, j).makeStartDeep()[0]
            val leftNum = extendLeft.word.length - (j - i)
            val proj = extendLeft.makeEndDeep()[0]
            if (!proj.isProjective()) throw IllegalStateException("Something is wrong!")
            result.add(
                GraphHom(
                    proj, this, Pair(leftNum..(leftNum + j - i), i..j)
                )
            )
        }
        return result
    }

//    fun stableHom(mY: StringModule<T, U>): Int {/*
//        Put `this` := mX.
//        Consider the projective cover of mY: P -> mY -> 0.
//        By applying (X,-), we obtain
//        (mX, P) -> (mX, mY) -> stableHom(mX, mY) -> 0.
//        To compute stableHom, we compute cokernel of (mX, P) -> (mX, mY).
//        Decompose P into indec summands P_i, so want to compute cokernel of
//        (mX, P_1) \oplus ... \oplus  (mX, P_t) -> (mX, mY).
//        The key observation is that the basis of left hand side, namely,
//        graph maps mX -> P_i, the compositions mX -> P_i -> mY are zero or graph maps.
//
//        Observe carefully. Suppose mX is not projective.
//        Then mX -> P_i is not surjective, and since P_i has simple top,
//        the image of mX -> P_i is contained in left or right arms of P_i.
//        So clearly goes to zero or submodule string in mY by composing P_i -> mY.
//
//        Then we have to check which graph maps mX -> mY arise in this way,
//        and just count the number of the rest.
//         */
//        require(algebra is StringAlgebra) {
//            "Currently only supported for string algebras."
//        }
//        val total = this.homBasis(mY) // The list of basis of Hom(mX, mY)
//        val image = mutableListOf<GraphHom<T, U>>()
//        for (g in mY.projCoverHom()) { // g: P_i -> mY
//            for (f in this.homBasis(g.from)) { // f: mX -> P_i
//                val gf = homCompose(f, g) ?: continue // composition mX -> P_i -> mY
//                image.add(gf)
//            }
//        }
//        return total.filter { it !in image }.size
//    }

    override fun stableHom(other: Indec<T>): Int {
        /*
        Take the proj cover of [other]:
        0 -> syzygy -> P -> other -> 0.
        By applying (this, -), we obtain
        0 -> (this, syzygy) -> (this, P) -> (this, other) -> stableHom(this, other) -> 0.
        Just calculate from it.
         */
        val syzygy = other.syzygy()
        val projCover = other.projCover()
        val first = algebra.hom(this, syzygy)
        val second = algebra.hom(this, projCover)
        val third = algebra.hom(this, other)
        return first - second + third
    }

    fun injHullHom(): List<GraphHom<T, U>> {
        require(algebra is StringAlgebra) {
            "Currently only supported for string algebras."
        }
        val indices = topIndices().toMutableList()
        if (indices.first() != 0) indices.add(0, 0)
        if (indices.last() != word.length) indices.add(word.length)
        if (indices == listOf(0)) indices.add(0) // For simple case.
        val result = mutableListOf<GraphHom<T, U>>()
        for ((i, j) in indices.zipWithNext()) {
            // Looks like i --->--->...---> <---...<---j.
            val extendLeft = subWord(i, j).makeStartPeak()[0]
            val leftNum = extendLeft.word.length - (j - i)
            val inj = extendLeft.makeEndPeak()[0]
            if (!inj.isInjective()) throw IllegalStateException("Something is wrong!")
            result.add(
                GraphHom(
                    this, inj, Pair(i..j, leftNum..(leftNum + j - i))
                )
            )
        }
        return result
    }


    override fun injStableHom(other: Indec<T>): Int {
        /*
        Take the inj hull of [this]:
        0 -> this -> I -> cosyzygy -> 0.
        By applying (-, other), we obtain
        0 -> (cosyzygy, other) -> (I, other) -> (this, other) -> injStableHom(this, other) -> 0.
        Just calculate from it.
         */
        val cosyzygy = this.cosyzygy()
        val injHull = this.injHull()
        val first = algebra.hom(cosyzygy, other)
        val second = algebra.hom(injHull, other)
        val third = algebra.hom(this, other)
        return first - second + third
    }


//    fun injStableHom(mY: StringModule<T, U>): Int {/*
//        Put `this` := mX.
//        Take an injective hull mX -> I of mX.
//        By (-, mY), we obtain the exact sequence
//        (I, mY) -> (mX, mY) -> injStableHom(mX, mY) -> 0.
//        So delete the image from basis of (mX, mY).
//         */
//        require(algebra is StringAlgebra) {
//            "Currently only supported for string algebras."
//        }
//        val total = this.homBasis(mY) // The list of basis of Hom(mX, mY)
//        val image = mutableListOf<GraphHom<T, U>>()
//        for (g in this.injHullHom()) { // g: mX -> I_i
//            for (f in (g.to).homBasis(mY)) { // f: I_i -> mY
//                val fg = homCompose(g, f) ?: continue // composition mX -> I_i -> mY
//                image.add(fg)
//            }
//        }
//        return total.filter { it !in image }.size
//    }

//    override fun injStableHom(other: Module<T>): Int {
//        require(this.algebra == other.algebra) { "Not over the same algebra." }
//        if (other is StringModule<*, *>) {
//            // Since algebras are equal, the type check should be OK.
//            @Suppress("UNCHECKED_CAST") return injStableHom(other as StringModule<T, U>)
//        }
//        TODO()
//    }

}

data class GraphHom<T, U>(
    val from: StringIndec<T, U>, val to: StringIndec<T, U>, val ranges: Pair<IntRange, IntRange>
) {
    private val fromQuot = from.subWord(ranges.first)
    private val toSub = to.subWord(ranges.second)
    val isStraight = (fromQuot == toSub)

    init {
        require(fromQuot == toSub || fromQuot == !toSub) {
            "Invalid map: $fromQuot, $toSub"
        }
    }
}

/**
 * Returns the composition of two graph maps, null if zero.
 * Be careful about the order: [hom1] is first, then [hom2].
 * @hom1 the first map
 * @hom2 the second map
 */
private fun <T, U> homCompose(
    hom1: GraphHom<T, U>,
    hom2: GraphHom<T, U>,
): GraphHom<T, U>? {
    require(hom1.to == hom2.from) {
        "Cannot compose since ${hom1.to} != ${hom2.from}"
    }
    val (mXquot, mYsub) = hom1.ranges
    val (mYquot, mZsub) = hom2.ranges
    val mYinter = mYsub.filter { it in mYquot }
    if (mYinter.isEmpty()) return null

    val mXresult =
        if (hom1.isStraight) mXquot.first + mYinter.first() - mYsub.first..mXquot.first + mYinter.last() - mYsub.first
        else mXquot.first + mYsub.last - mYinter.last()..mXquot.first + mYsub.last - mYinter.first()

    val mZresult =
        if (hom2.isStraight) mZsub.first + mYinter.first() - mYquot.first..mZsub.first + mYinter.last() - mYquot.first
        else mZsub.first + mYquot.last - mYinter.last()..mZsub.first + mYquot.last - mYinter.first()
    return GraphHom(hom1.from, hom2.to, Pair(mXresult, mZresult))
}


// Below: old approach using (heavy) equivalence-class.

//
///**
// * A class for equivalence classes of string modules.
// * "DSM" stands for "DoubledStringModule". Equality are checked by [equivClass],
// * so isomorphic modules are "equal" in this class.
// *
// * @property equivClass the set of equivalence classes of a word, which should be a word and its inverse.
// * @property entity the representative of [equivClass].
// * Chosen to be one with lexicographically smaller string (with "!" replaced with "~").
// */
//data class DSM<T, U>(val equivClass: Set<StringModule<T, U>>) : Module<T> {
//    private val temp = equivClass.first()
//    override val algebra = temp.algebra
//
//    init {
//        require(equivClass == setOf(temp, !temp)) { "Invalid construction." }
//    }
//
//    // We choose a representative to be one with more arrows than the other.
//    val entity: StringModule<T, U> = if (temp.word < !temp.word) {
//        temp
//    } else {
//        !temp
//    }
//
//
//    override fun dim(): Int = entity.dim()
//
//    override fun toString(): String = entity.toString()
//
//    override fun isSimple(): Boolean = entity.isSimple()
//
//    override fun isProjective(): Boolean = entity.isProjective()
//
//    override fun isInjective(): Boolean = entity.isInjective()
//
//    override fun top(): List<DSM<T, U>> = entity.top().map { it.toDSM() }
//
//    override fun socle(): List<DSM<T, U>> = entity.socle().map { it.toDSM() }
//
//    override fun hom(other: Module): Int = entity.hom(other)
//
//
//    override fun isIsomorphic(other: Module): Boolean {
//        return entity.isIsomorphic(other)
//    }
//
//    override fun isBrick(): Boolean = (this.hom(this) == 1)
//
//    override fun sinkSequence(): Pair<List<DSM<T, U>>, DSM<T, U>?> {
//        val seq = entity.sinkSequence()
//        return Pair(
//            seq.first.map { it.toDSM() }, seq.second?.toDSM()
//        )
//    }
//
//    override fun sourceSequence(): Pair<List<DSM<T, U>>, DSM<T, U>?> {
//        val seq = entity.sourceSequence()
//        return Pair(
//            seq.first.map { it.toDSM() }, seq.second?.toDSM()
//        )
//    }
//}
