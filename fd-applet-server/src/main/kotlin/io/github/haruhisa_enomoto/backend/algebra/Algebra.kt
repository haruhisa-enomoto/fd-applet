package io.github.haruhisa_enomoto.backend.algebra

import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Quiver

/**
 * A class representing bound quiver algebras of the form kQ/I
 * for a finite quiver Q and a two-sided ideal I.
 *
 * @param T the type of vertices of Q.
 */
abstract class Algebra<T> {
    /**
     * The list of vertices of the quiver.
     */
    abstract val vertices: List<T>

    // Private properties to cache the results.

    private val homMap = mutableMapOf<Pair<Indec<T>, Indec<T>>, Int>()
    private val ext1Map = mutableMapOf<Pair<Indec<T>, Indec<T>>, Int>()
    private val tauPlus = mutableMapOf<Indec<T>, Indec<T>?>()
    private val tauMinus = mutableMapOf<Indec<T>, Indec<T>?>()
    val syzygyMap = mutableMapOf<Indec<T>, List<Indec<T>>>()

    /**
     * Determines whether the algebra is a string algebra or not.
     *
     * @return `true` if it is a string algebra, `false` otherwise.
     */
    abstract fun isStringAlgebra(): Boolean

    /**
     * Determines whether the algebra is a gentle algebra or not.
     *
     * @return `true` if it is a gentle algebra, `false` otherwise.
     */
    abstract fun isGentleAlgebra(): Boolean

    /**
     * Returns the number of indecomposable modules.
     * Returns `null` if not representation-finite.
     *
     * @return the number of indecomposable modules, or null if not representation-finite.
     */
    abstract fun numberOfIndecs(): Int?

    /**
     * Returns the dimension of the algebra.
     * Returns `null` if not finite-dimensional.
     *
     * @return the dimension of the algebra, or null if not finite-dimensional.
     */
    abstract fun dim(): Int?

    /**
     * Returns the rank of the algebra, which is the number of vertices.
     *
     * @return the rank of the algebra.
     */
    fun rank() = vertices.size

    /**
     * Determines whether the algebra is finite-dimensional or not.
     *
     * @return `true` if the algebra is finite-dimensional, `false` otherwise.
     */
    fun isFiniteDimensional() = (dim() != null)

    /**
     * Returns the dimension of Hom([mX], [mY]).
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of Hom([mX], [mY])
     */
    fun hom(mX: Indec<T>?, mY: Indec<T>?): Int {
        if (mX == null || mY == null) return 0
        return homMap.getOrPut(mX to mY) { mX.hom(mY) }
    }

    /**
     * Determines whether Hom([mX], [mY]) vanishes.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
     * @return `true` if Hom([mX], [mY]) = 0, `false` otherwise.
     */
    fun homZero(mX: Indec<T>?, mY: Indec<T>?): Boolean {
        return hom(mX, mY) == 0
    }

    /**
     * Returns the dimension of Hom([mX], [mYY]).
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of Hom([mX], [mYY])
     */
    fun hom(mX: Indec<T>?, mYY: Collection<Indec<T>?>): Int {
        return mYY.sumOf { hom(mX, it) }
    }

    /**
     * Returns the dimension of Hom([mXX], [mY]).
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of Hom([mXX], [mY])
     */
    fun hom(mXX: Collection<Indec<T>?>, mY: Indec<T>?): Int {
        return mXX.sumOf { hom(it, mY) }
    }

    /**
     * Returns the dimension of Hom([mXX], [mYY]).
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of Hom([mXX], [mYY])
     */
    fun hom(mXX: Collection<Indec<T>?>, mYY: Collection<Indec<T>?>): Int {
        return mXX.sumOf { mX -> hom(mX, mYY) }
    }

    /**
     * Returns the dimension of \underline{Hom}([mX], [mY]),
     * the projectively stable Hom.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of \underline{Hom}([mX], [mY]).
     */
    fun stableHom(mX: Indec<T>?, mY: Indec<T>?): Int {
        if (mX == null || mY == null) return 0
        return mX.stableHom(mY)
    }

    /**
     * Returns the dimension of \underline{Hom}([mX], [mYY]),
     * the projectively stable Hom.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of \underline{Hom}([mX], [mYY]).
     */
    fun stableHom(mX: Indec<T>?, mYY: Collection<Indec<T>?>): Int {
        return mYY.sumOf { stableHom(mX, it) }
    }

    /**
     * Returns the dimension of \underline{Hom}([mXX], [mY]),
     * the projectively stable Hom.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of \underline{Hom}([mXX], [mY]).
     */
    fun stableHom(mXX: Collection<Indec<T>?>, mY: Indec<T>?): Int {
        return mXX.sumOf { stableHom(it, mY) }
    }

    /**
     * Returns the dimension of \underline{Hom}([mXX], [mY]),
     * the projectively stable Hom.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of \underline{Hom}([mXX], [mYY]).
     */
    fun stableHom(mXX: Collection<Indec<T>?>, mYY: Collection<Indec<T>?>): Int {
        return mXX.sumOf { mX -> stableHom(mX, mYY) }
    }

    /**
     * Returns the dimension of \overline{Hom}([mX], [mY]),
     * the injectively stable Hom.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of \overline{Hom}([mXX], [mYY]).
     */
    fun injStableHom(mX: Indec<T>?, mY: Indec<T>?): Int {
        if (mX == null || mY == null) return 0
        return mX.injStableHom(mY)
    }

    /**
     * Returns the dimension of \overline{Hom}([mX], [mYY]),
     * the injectively stable Hom.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of \overline{Hom}([mX], [mYY]).
     */
    fun injStableHom(mX: Indec<T>?, mYY: Collection<Indec<T>?>): Int {
        return mYY.sumOf { injStableHom(mX, it) }
    }

    /**
     * Returns the dimension of \overline{Hom}([mXX], [mY]),
     * the injectively stable Hom.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of \overline{Hom}([mXX], [mY]).
     */
    fun injStableHom(mXX: Collection<Indec<T>?>, mY: Indec<T>?): Int {
        return mXX.sumOf { injStableHom(it, mY) }
    }

    /**
     * Returns the dimension of \overline{Hom}([mXX], [mYY]),
     * the injectively stable Hom.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of \overline{Hom}([mXX], [mYY]).
     */
    fun injStableHom(mXX: Collection<Indec<T>?>, mYY: Collection<Indec<T>?>): Int {
        return mXX.sumOf { mX -> injStableHom(mX, mYY) }
    }

    /**
     * Returns the dimension of Ext^1([mX], [mY]).
     *
     * @param mX
     * @param mY
     * @return the dimension of Ext^1([mX], [mY]).
     */
    fun ext1(mX: Indec<T>?, mY: Indec<T>?): Int {
        if (mX == null || mY == null) return 0
        /**
         * By the AR duality, Ext^1(X, Y) = D stableHom(tauMinus Y, X).
         */
        return ext1Map.getOrPut(mX to mY) {
            stableHom(tauMinus(mY), mX)
        }
    }

    /**
     * Returns the dimension of Ext^[n] ([mX], [mY]) (n is default 1).
     *
     * @param mX
     * @param mY
     * @param n
     * @return the dimension of Ext^[n] ([mX], [mY]).
     */
    fun ext(mX: Indec<T>?, mY: Indec<T>?, n: Int = 1): Int {
        if (mX == null || mY == null) return 0
        /**
         * By the AR duality, Ext^1(X, Y) = D stableHom(tauMinus Y, X).
         * If n > 1, then Ext^n(X, Y) = Ext^1( \Omega^{n-1} X, Y)
         */
        if (n == 0) return hom(mX, mY)
        if (n == 1) return ext1(mX, mY)
        //        val num2 = ext(mX, mY.cosyzygy(n - 1))
//        if (num1 != num2) throw IllegalStateException("Ext fails!")
        return ext(mX.syzygy(n - 1), mY)
    }

    fun ext(mX: Indec<T>?, mYY: Collection<Indec<T>?>, n: Int = 1): Int {
        return mYY.sumOf { ext(mX, it, n) }
    }

    fun ext(mXX: Collection<Indec<T>?>, mY: Indec<T>?, n: Int = 1): Int {
        return mXX.sumOf { ext(it, mY, n) }
    }

    fun ext(mXX: Collection<Indec<T>?>, mYY: Collection<Indec<T>?>, n: Int = 1): Int {
        return mXX.sumOf { ext(it, mYY, n) }
    }

    /**
     * Returns whether Ext^i([mX], [mY]) = 0 for all i > 0.
     */
    fun higherExtZero(mX: Indec<T>?, mY: Indec<T>?): Boolean {
        if (mX == null || mY == null) return true
        val syzygies = mX.allSyzygies()
        // The list of all modules appearing in \Omega^{>0}([mX]).
        return ext(syzygies, mY, 1) == 0
    }

    /**
     * Returns whether Ext^i([mXX], [mY]) = 0 for all i > 0.
     */
    fun higherExtZero(mXX: Collection<Indec<T>?>, mY: Indec<T>?): Boolean {
        return mXX.all { higherExtZero(it, mY) }
    }

    /**
     * Returns whether Ext^i([mX], [mYY]) = 0 for all i > 0.
     */
    fun higherExtZero(
        mX: Indec<T>?,
        mYY: Collection<Indec<T>?>
    ): Boolean {
        return mYY.all { higherExtZero(mX, it) }
    }

    /**
     * Returns whether Ext^i([mXX], [mYY]) = 0 for all i > 0.
     */
    fun higherExtZero(
        mXX: Collection<Indec<T>?>,
        mYY: Collection<Indec<T>?>
    ): Boolean {
        return mYY.all { higherExtZero(mXX, it) }
    }

    /**
     * For a given collection [mCC] of modules, returns the list of modules
     * `mX` such that Ext^1(mX, [mCC]) = 0. Note that this only considers the first ext.
     */
    fun extProj(mCC: Collection<Indec<T>>): List<Indec<T>> {
        return mCC.filter { ext(it, mCC) == 0 }
    }

    /**
     * For a given collection [mCC] of modules, returns the list of modules
     * `mY` such that Ext^1([mCC], `mY`) = 0. Note that this only considers the first ext.
     */
    fun extInj(mCC: Collection<Indec<T>>): List<Indec<T>> {
        return mCC.filter { ext(mCC, it) == 0 }
    }

    fun tauPlus(mX: Indec<T>): Indec<T>? {
        return tauPlus.getOrPut(mX) { mX.tauPlus() }
    }

    fun tauMinus(mX: Indec<T>): Indec<T>? {
        return tauMinus.getOrPut(mX) { mX.tauMinus() }
    }

    /**
     * Returns a simple module corresponding to [vtx].
     */
    abstract fun simpleAt(vtx: T): Indec<T>

    /**
     * Returns the list of all simple modules.
     */
    fun simples() = vertices.map { simpleAt(it) }

    /**
     * Returns an indecomposable projective module corresponding to [vtx].
     */
    abstract fun projAt(vtx: T): Indec<T>


    /**
     * Returns the list of all indecomposable projective modules.
     */
    fun projs() = vertices.map { projAt(it) }

    /**
     * Returns an indecomposable injective module corresponding to [vtx].
     */
    abstract fun injAt(vtx: T): Indec<T>

    /**
     * Returns the list of all indecomposable injective modules.
     */
    fun injs() = vertices.map { injAt(it) }

    /**
     * Returns whether the algebra is representation-finite:
     * there are only finitely many indecomposable modules up to isomorphisms.
     */
    abstract fun isRepFinite(): Boolean


    /**
     * Returns the syzygy quiver starting from the list of modules [mXX].
     * (If [syzygy] is `false`, then returns the cosyzygy quiver).
     * This is a quiver where vertices are indecomposable modules,
     * and for each vertex `X`, draw an arrow from `X` to
     * indecomposable summands of the syzygy of `X`.
     * This will start drawing from modules in [mXX].
     */
    fun syzygyQuiverFrom(mXX: List<Indec<T>>, cosyzygy: Boolean = false): Quiver<Indec<T>, Nothing> {
        val syzygyVertices = mXX.toMutableList()
        val syzygyArrows = mutableListOf<Arrow<Indec<T>, Nothing>>()
        val queue = ArrayDeque(mXX)
        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val nextVertices = if (!cosyzygy) current.syzygy() else current.cosyzygy()
            for (next in nextVertices) {
                val already = syzygyVertices.find { it.isIsomorphic(next) }
                if (already != null) {
                    syzygyArrows.add(Arrow(null, current, already))
                    continue
                }
                syzygyArrows.add(Arrow(null, current, next))
                syzygyVertices.add(next)
                queue.add(next)
            }
        }
        return Quiver(syzygyVertices, syzygyArrows)
    }

    /**
     * Returns the projective dimension of [mXX], null if infinity.
     */
    fun projDim(mXX: List<Indec<T>>): Int? {
        if (mXX.isEmpty()) return 0
        return mXX.map { it.projDim() ?: return null }.max()
    }

    /**
     * Returns the injective dimension of [mXX], null if infinity.
     */
    fun injDim(mXX: List<Indec<T>>): Int? {
        if (mXX.isEmpty()) return 0
        return mXX.map { it.injDim() ?: return null }.max()
    }

    /**
     * Take the minimal injective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> I^2 -> ...
     * Then returns the least n such that I^n is not projective, and null (infinity)
     * if all are projective (e.g. [mXX] is projective-injective module).
     */
    fun dominantDim(mXX: List<Indec<T>>): Int? {
        val injRemoved = mXX.mapNotNull { it.dominantDim() }
        if (injRemoved.isEmpty()) return null
        return injRemoved.min()
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     *  -> P_n -> ... -> P_1 -> P_0 -> this -> 0
     * Then returns the least n such that P_n is not injective, and null (infinity)
     * if all are projective-injective (e.g. [mXX] is proj-injective module).
     */
    fun coDominantDim(mXX: List<Indec<T>>): Int? {
        val projRemoved = mXX.mapNotNull { it.coDominantDim() }
        if (projRemoved.isEmpty()) return null
        return projRemoved.min()
    }

    /**
     * Returns the list of vertices in the top of [mMM] (with multiplicity).
     */
    fun topVertices(mMM: Collection<Indec<T>>): List<T> {
        return mMM.map { it.topVertices() }.flatten()
    }

    /**
     * Returns the list of vertices in the socle of [mMM] (with multiplicity).
     */
    fun socleVertices(mMM: Collection<Indec<T>>): List<T> {
        return mMM.map { it.socleVertices() }.flatten()
    }

    /**
     * Returns the projective cover of [mMM].
     */
    fun projCover(mMM: Collection<Indec<T>>): List<Indec<T>> {
        return topVertices(mMM).map { projAt(it) }
    }

    /**
     * Returns the injective hull of [mMM].
     */
    fun injHull(mMM: Collection<Indec<T>>): List<Indec<T>> {
        return socleVertices(mMM).map { injAt(it) }
    }

    /**
     * Returns the syzygy of [mMM].
     */
    fun syzygy(mMM: Collection<Indec<T>>): List<Indec<T>> {
        return mMM.flatMap { it.syzygy() }
    }

    /**
     * Returns the cosyzygy of [mMM].
     */
    fun cosyzygy(mMM: Collection<Indec<T>>): List<Indec<T>> {
        return mMM.flatMap { it.cosyzygy() }
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0.
     * This sequence yields:
     * Pair(P_0, \Omega X), Pair(P_1, \Omega^2 X), ...
     * where P_i is represented by top vertices, not modules.
     * If stops, the last element is (P_n, emptyList).
     */
    fun projResolutionWithSyzygySequence(mXX: List<Indec<T>>): Sequence<Pair<List<T>, List<Indec<T>>>> = sequence {
        var current = mXX
        while (true) {
            val syzygy = syzygy(current)
            yield(topVertices(current) to syzygy)
            if (syzygy.isEmpty()) break
            current = syzygy
        }
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0.
     * This sequence yields:
     * listOf(Pair(P_0, \Omega X), Pair(P_1, \Omega^2 X), ..., Pair(P_n, \Omega^{n+1} X)),
     * where P_i is represented by top vertices, not modules.
     * If stops, the last element is (P_i, emptyList).
     */
    fun projResolutionWithSyzygy(mXX: List<Indec<T>>, n: Int): List<Pair<List<T>, List<Indec<T>>>> {
        return projResolutionWithSyzygySequence(mXX).take(n + 1).toList()
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0.
     * This sequence yields:
     * P_0, P_1, P_2, ...
     * where P_i is represented by top vertices, not modules.
     * If [mXX] has finite projective dimension with last part 0 -> P_n, then
     * the last element is P_n.
     */
    fun projResolutionSequence(mXX: List<Indec<T>>): Sequence<List<T>> {
        return projResolutionWithSyzygySequence(mXX).map { it.first }
    }

    /**
     * Suppose [mXX] has a projective resolution
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0
     * Then this returns
     * `listOf(P_0, P_1, ..., P_n)`
     * where P_i is represented by list of top vertices, not modules.
     * Even if computation stops, this always returns list with n + 1 elements,
     * with last part filled with empty list (= zero module)
     */
    fun projResolution(mXX: List<Indec<T>>, n: Int): List<List<T>> {
        val list = projResolutionSequence(mXX).take(n + 1).toList()
        return list + List(n + 1 - list.size) { listOf() }
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> ...
     * This sequence yields:
     * Pair(I^0, \Sigma X), Pair(I^1, \Sigma^2 X), ...
     * where I^i is represented by socle vertices, not modules.
     * If stops, the last element is (I^n, emptyList).
     */
    fun injResolutionWithCosyzygySequence(mXX: List<Indec<T>>): Sequence<Pair<List<T>, List<Indec<T>>>> = sequence {
        var current = mXX
        while (true) {
            val cosyzygy = cosyzygy(current)
            yield(socleVertices(current) to cosyzygy)
            if (cosyzygy.isEmpty()) break
            current = cosyzygy
        }
    }

    /**
     * Take the minimal injective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> ...
     * This sequence yields:
     * I^0, I^1, I^2, ...
     * where I^i is represented by socle vertices, not modules.
     */
    fun injResolutionSequence(mXX: List<Indec<T>>): Sequence<List<T>> {
        return injResolutionWithCosyzygySequence(mXX).map { it.first }
    }

    /**
     * Take the minimal injective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> ...
     * This sequence yields:
     * I^0, I^1, I^2, ..., I^n
     * where I^i is represented by socle vertices, not modules.
     * Even if this stops, this returns with last filled with listOf() = zero.
     */
    fun injResolution(mXX: List<Indec<T>>, n: Int): List<List<T>> {
        val list = injResolutionSequence(mXX).take(n + 1).toList()
        return list + List(n + 1 - list.size) { listOf() }
    }

    /**
     * Suppose [mXX] has a projective resolution
     * 0 -> [mXX] -> I^0 -> I^1 -> ... -> I^n -> \Sigma^{n+1} X -> 0
     * Then this returns
     * `listOf(Pair(I^0, \Sigma X), Pair(I^1, \Sigma^2 X), ..., Pair(I^n, \Sigma^{n+1} X))`
     * where P_i is represented by list of top vertices, not modules.
     * This stops computation if syzygy becomes 0 = empty list.
     */
    fun injResolutionWithCosyzygy(mXX: List<Indec<T>>, n: Int): List<Pair<List<T>, List<Indec<T>>>> {
        return injResolutionWithCosyzygySequence(mXX).take(n + 1).toList()
    }

    fun globalDim(): Int? {
        val val1 = projDim(simples())
        val val2 = injDim(simples())
        if (val1 != val2) throw IllegalStateException(
            "Bug! The proj and inj dimension of simples don't coincide!"
        )
        return val1
    }

    fun rightSelfInjDim(): Int? = injDim(projs())

    fun leftSelfInjDim(): Int? = projDim(injs())

    fun dominantDim(): Int? = dominantDim(projs())

    fun isIG(): Boolean = (rightSelfInjDim() != null && leftSelfInjDim() != null)

    fun isSelfInjective(): Boolean = projs().all { it.isInjective() }

    abstract fun toRFAlgebra(): RFAlgebra<T>
}
