package io.github.haruhisa_enomoto.backend.algebra

import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Quiver

/**
 * A class representing bound quiver algebras of the form kQ/I
 * for a finite quiver Q and a two-sided ideal I.
 *
 * @param T the type of vertices of Q.
 * @property vertices the list of vertices of Q.
 */
abstract class Algebra<T> {
    abstract val vertices: List<T>

    // Private properties to cache the results.
    private val homMap = mutableMapOf<Pair<Indec<T>, Indec<T>>, Int>()
    private val ext1Map = mutableMapOf<Pair<Indec<T>, Indec<T>>, Int>()
    private val tauPlus = mutableMapOf<Indec<T>, Indec<T>?>()
    private val tauMinus = mutableMapOf<Indec<T>, Indec<T>?>()
    val syzygyMap = mutableMapOf<Indec<T>, List<Indec<T>>>()

    /**
     * Returns whether the algebra is a string algebra or not.
     *
     * @return `true` if it is a string algebra, `false` otherwise.
     */
    abstract fun isStringAlgebra(): Boolean

    /**
     * Returns whether the algebra is a gentle algebra or not.
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
     * Returns whether the algebra is finite-dimensional or not.
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
     * Returns whether Hom([mX], [mY]) vanishes.
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
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
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
     * Returns the dimension of Ext^1([mX], [mYY]).
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of Ext^1([mX], [mYY]).
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

    /**
     * Returns the dimension of Ext^1([mX], [mYY]).
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of Ext^1([mX], [mYY]).
     */
    fun ext(mX: Indec<T>?, mYY: Collection<Indec<T>?>, n: Int = 1): Int {
        return mYY.sumOf { ext(mX, it, n) }
    }

    /**
     * Returns the dimension of Ext^1([mXX], [mY]).
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mY an indecomposable module (`null` represents 0).
     * @return the dimension of Ext^1([mXX], [mY]).
     */
    fun ext(mXX: Collection<Indec<T>?>, mY: Indec<T>?, n: Int = 1): Int {
        return mXX.sumOf { ext(it, mY, n) }
    }

    /**
     * Returns the dimension of Ext^1([mXX], [mYY]).
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mYY a module as a collection of indecomposables.
     * @return the dimension of Ext^1([mXX], [mYY]).
     */
    fun ext(mXX: Collection<Indec<T>?>, mYY: Collection<Indec<T>?>, n: Int = 1): Int {
        return mXX.sumOf { ext(it, mYY, n) }
    }

    /**
     * Returns whether Ext^i([mX], [mY]) = 0 for all i > 0.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mY an indecomposable module (`null` represents 0).
     * @return whether Ext^i([mX], [mY]) = 0 for all i > 0.
     */
    fun higherExtZero(mX: Indec<T>?, mY: Indec<T>?): Boolean {
        if (mX == null || mY == null) return true
        val syzygies = mX.allSyzygies()
        // The list of all modules appearing in \Omega^{>0}([mX]).
        return ext(syzygies, mY, 1) == 0
    }

    /**
     * Returns whether Ext^i([mXX], [mY]) = 0 for all i > 0.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mY an indecomposable module (`null` represents 0).
     */
    fun higherExtZero(mXX: Collection<Indec<T>?>, mY: Indec<T>?): Boolean {
        return mXX.all { higherExtZero(it, mY) }
    }

    /**
     * Returns whether Ext^i([mX], [mYY]) = 0 for all i > 0.
     *
     * @param mX an indecomposable module (`null` represents 0).
     * @param mYY a module as a collection of indecomposables.
     */
    fun higherExtZero(
        mX: Indec<T>?,
        mYY: Collection<Indec<T>?>
    ): Boolean {
        return mYY.all { higherExtZero(mX, it) }
    }

    /**
     * Returns whether Ext^i([mXX], [mYY]) = 0 for all i > 0.
     *
     * @param mXX a module as a collection of indecomposables.
     * @param mYY a module as a collection of indecomposables.
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
     *
     * @param mCC a collection of modules as a collection of indecomposables.
     */
    fun extProj(mCC: Collection<Indec<T>>): List<Indec<T>> {
        return mCC.filter { ext(it, mCC) == 0 }
    }

    /**
     * For a given collection [mCC] of modules, returns the list of modules
     * `mY` such that Ext^1([mCC], `mY`) = 0. Note that this only considers the first ext.
     *
     * @param mCC a collection of modules as a collection of indecomposables.
     */
    fun extInj(mCC: Collection<Indec<T>>): List<Indec<T>> {
        return mCC.filter { ext(mCC, it) == 0 }
    }

    /**
     * Returns the Auslander-Reiten translate of [mX],
     * or null if [mX] is projective.
     *
     * @param mX an indecomposable module.
     * @return the Auslander-Reiten translate of [mX], or null if [mX] is projective.
     */
    fun tauPlus(mX: Indec<T>): Indec<T>? {
        return tauPlus.getOrPut(mX) { mX.tauPlus() }
    }

    /**
     * Returns the inverse of the Auslander-Reiten translate of [mX],
     * or null if [mX] is injective.
     *
     * @param mX an indecomposable module.
     * @return the Auslander-Reiten translate of [mX], or null if [mX] is injective.
     */
    fun tauMinus(mX: Indec<T>): Indec<T>? {
        return tauMinus.getOrPut(mX) { mX.tauMinus() }
    }

    /**
     * Returns a simple module corresponding to [vtx].
     *
     * @param vtx a vertex of the graph.
     * @return a simple module corresponding to [vtx].
     */
    abstract fun simpleAt(vtx: T): Indec<T>

    /**
     * Returns the list of all simple modules.
     *
     * @return the list of all simple modules.
     */
    fun simples() = vertices.map { simpleAt(it) }

    /**
     * Returns an indecomposable projective module corresponding to [vtx].
     *
     * @param vtx a vertex of the graph.
     * @return an indecomposable projective module corresponding to [vtx].
     */
    abstract fun projAt(vtx: T): Indec<T>


    /**
     * Returns the list of all indecomposable projective modules.
     *
     * @return the list of all indecomposable projective modules.
     */
    fun projs() = vertices.map { projAt(it) }

    /**
     * Returns an indecomposable injective module corresponding to [vtx].
     *
     * @param vtx a vertex of the graph.
     * @return an indecomposable injective module corresponding to [vtx].
     */
    abstract fun injAt(vtx: T): Indec<T>

    /**
     * Returns the list of all indecomposable injective modules.
     *
     * @return the list of all indecomposable injective modules.
     */
    fun injs() = vertices.map { injAt(it) }

    /**
     * Returns whether the algebra is representation-finite:
     * there are only finitely many indecomposable modules up to isomorphisms.
     *
     * @return whether the algebra is representation-finite.
     */
    abstract fun isRepFinite(): Boolean


    /**
     * Returns the syzygy quiver starting from the list of modules [mXX].
     * (If [syzygy] is `false`, then returns the cosyzygy quiver).
     * This is a quiver where vertices are indecomposable modules,
     * and for each vertex `X`, draw an arrow from `X` to
     * indecomposable summands of the syzygy of `X`.
     * This will start drawing from modules in [mXX].
     *
     * @param mXX a list of indecomposable modules.
     * @param cosyzygy whether to draw cosyzygies instead of syzygies.
     * @return the (co)syzygy quiver starting from the list of modules [mXX].
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
     *
     * @param mXX a module as a list of indecomposables.
     * @return the projective dimension of [mXX], null if infinity.
     */
    fun projDim(mXX: List<Indec<T>>): Int? {
        if (mXX.isEmpty()) return 0
        return mXX.map { it.projDim() ?: return null }.max()
    }

    /**
     * Returns the injective dimension of [mXX], null if infinity.
     *
     * @param mXX a module as a list of indecomposables.
     * @return the injective dimension of [mXX], null if infinity.
     */
    fun injDim(mXX: List<Indec<T>>): Int? {
        if (mXX.isEmpty()) return 0
        return mXX.map { it.injDim() ?: return null }.max()
    }

    /**
     * Returns the dominant dimension of [mXX], defined as follows.
     * Take the minimal injective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> I^2 -> ...
     * The dominant dimension is the least n such that I^n is not projective, or null (infinity)
     * if all are projective (e.g. [mXX] is projective-injective module).
     *
     * @param mXX a module as a list of indecomposables.
     * @return the dominant dimension of [mXX], or null if infinity.
     */
    fun dominantDim(mXX: List<Indec<T>>): Int? {
        val injRemoved = mXX.mapNotNull { it.dominantDim() }
        if (injRemoved.isEmpty()) return null
        return injRemoved.min()
    }

    /**
     * Returns the co-dominant dimension of [mXX], defined as follows.
     * Take the minimal projective resolution of [mXX]:
     *  -> P_n -> ... -> P_1 -> P_0 -> this -> 0
     * The co-dominant dimension is the least n such that P_n is not injective, or null (infinity)
     * if all are projective-injective (e.g. [mXX] is proj-injective module).
     *
     * @param mXX a module as a list of indecomposables.
     * @return the co-dominant dimension of [mXX], or null if infinity.
     */
    fun coDominantDim(mXX: List<Indec<T>>): Int? {
        val projRemoved = mXX.mapNotNull { it.coDominantDim() }
        if (projRemoved.isEmpty()) return null
        return projRemoved.min()
    }

    /**
     * Returns the list of vertices in the top of [mXX] (with multiplicity).
     *
     * @param mXX a module as a list of indecomposables.
     * @return the list of vertices in the top of [mXX] (with multiplicity).
     */
    fun topVertices(mXX: Collection<Indec<T>>): List<T> {
        return mXX.map { it.topVertices() }.flatten()
    }

    /**
     * Returns the list of vertices in the socle of [mXX] (with multiplicity).
     *
     * @param mXX a module as a list of indecomposables.
     * @return the list of vertices in the socle of [mXX] (with multiplicity).
     */
    fun socleVertices(mXX: Collection<Indec<T>>): List<T> {
        return mXX.map { it.socleVertices() }.flatten()
    }

    /**
     * Returns the projective cover of [mXX].
     *
     * @param mXX a module as a list of indecomposables.
     * @return the projective cover of [mXX].
     */
    fun projCover(mXX: Collection<Indec<T>>): List<Indec<T>> {
        return topVertices(mXX).map { projAt(it) }
    }

    /**
     * Returns the injective hull of [mXX].
     *
     * @param mXX a module as a list of indecomposables.
     * @return the injective hull of [mXX].
     */
    fun injHull(mXX: Collection<Indec<T>>): List<Indec<T>> {
        return socleVertices(mXX).map { injAt(it) }
    }

    /**
     * Returns the syzygy of [mXX].
     *
     * @param mXX a module as a list of indecomposables.
     * @return the syzygy of [mXX].
     */
    fun syzygy(mXX: Collection<Indec<T>>): List<Indec<T>> {
        return mXX.flatMap { it.syzygy() }
    }

    /**
     * Returns the cosyzygy of [mXX].
     *
     * @param mXX a module as a list of indecomposables.
     * @return the cosyzygy of [mXX].
     */
    fun cosyzygy(mXX: Collection<Indec<T>>): List<Indec<T>> {
        return mXX.flatMap { it.cosyzygy() }
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0.
     * This sequence yields:
     * Pair(P_0, \Omega X), Pair(P_1, \Omega^2 X), ...
     * where P_i is represented by top vertices, not modules.
     * If the sequence stops, the last element is (P_n, emptyList).
     *
     * @param mXX a module as a list of indecomposables.
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
     * If the sequence stops, the last element is (P_i, emptyList).
     *
     * @param mXX a module as a list of indecomposables.
     */
    fun projResolutionWithSyzygy(mXX: List<Indec<T>>, n: Int): List<Pair<List<T>, List<Indec<T>>>> {
        return projResolutionWithSyzygySequence(mXX).take(n + 1).toList()
    }

    /**
     * Take the minimal projective resolution of [mXX]:
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0.
     * This sequence yields:
     * P_0, P_1, P_2, ...
     * where P_i is represented by **top vertices, not modules**.
     * If [mXX] has finite projective dimension with last part 0 -> P_n, then
     * the last element is P_n.
     *
     * @param mXX a module as a list of indecomposables.
     */
    fun projResolutionSequence(mXX: List<Indec<T>>): Sequence<List<T>> {
        return projResolutionWithSyzygySequence(mXX).map { it.first }
    }

    /**
     * Suppose [mXX] has a projective resolution
     * 0 -> \Omega^{n+1} X -> P_n -> ... -> P_1 -> P_0 -> [mXX] -> 0
     * Then this returns
     * `listOf(P_0, P_1, ..., P_n)`
     * where P_i is represented by list of **top vertices, not modules**.
     * Even if computation stops, this always returns list with n + 1 elements,
     * with last part filled with empty list (= zero module)
     *
     * @param mXX a module as a list of indecomposables.
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
     * where I^i is represented by **socle vertices, not modules**.
     * If the sequence stops, the last element is (I^n, emptyList).
     *
     * @param mXX a module as a list of indecomposables.
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
     * where I^i is represented by **socle vertices, not modules**.
     *
     * @param mXX a module as a list of indecomposables.
     */
    fun injResolutionSequence(mXX: List<Indec<T>>): Sequence<List<T>> {
        return injResolutionWithCosyzygySequence(mXX).map { it.first }
    }

    /**
     * Take the minimal injective resolution of [mXX]:
     * 0 -> [mXX] -> I^0 -> I^1 -> ...
     * This sequence yields:
     * I^0, I^1, I^2, ..., I^n
     * where I^i is represented by **socle vertices, not modules**.
     * Even if this stops, this returns with last filled with listOf() = zero.
     *
     * @param mXX a module as a list of indecomposables.
     */
    fun injResolution(mXX: List<Indec<T>>, n: Int): List<List<T>> {
        val list = injResolutionSequence(mXX).take(n + 1).toList()
        return list + List(n + 1 - list.size) { listOf() }
    }

    /**
     * Suppose [mXX] has an injective resolution
     * 0 -> [mXX] -> I^0 -> I^1 -> ... -> I^n -> \Sigma^{n+1} X -> 0
     * Then this returns
     * `listOf(Pair(I^0, \Sigma X), Pair(I^1, \Sigma^2 X), ..., Pair(I^n, \Sigma^{n+1} X))`
     * where I^i is represented by list of **socle vertices, not modules**.
     * This stops computation if syzygy becomes 0 = empty list.
     *
     * @param mXX a module as a list of indecomposables.
     */
    fun injResolutionWithCosyzygy(mXX: List<Indec<T>>, n: Int): List<Pair<List<T>, List<Indec<T>>>> {
        return injResolutionWithCosyzygySequence(mXX).take(n + 1).toList()
    }

    /**
     * Returns the global dimension of the algebra, or null if it is infinite.
     *
     * @return the global dimension of the algebra, or null if it is infinite.
     */
    fun globalDim(): Int? {
        val val1 = projDim(simples())
        val val2 = injDim(simples())
        if (val1 != val2) throw IllegalStateException(
            "Bug! The proj and inj dimension of simples don't coincide!"
        )
        return val1
    }

    /**
     * Returns the right self-injective dimension of the algebra, that is,
     * the injective dimension of the regular module as a right module.
     *
     * @return the right self-injective dimension of the algebra, or null if it is infinite.
     */
    fun rightSelfInjDim(): Int? = injDim(projs())

    /**
     * Returns the left self-injective dimension of the algebra, that is,
     * the injective dimension of the regular module as a left module.
     *
     * @return the left self-injective dimension of the algebra, or null if it is infinite.
     */
    fun leftSelfInjDim(): Int? = projDim(injs())


    /**
     * Returns the dominant dimension of the algebra,
     * which is the dominant dimension of the regular module as a right module.
     *
     * @return the dominant dimension of the algebra, or null if it is infinite.
     */
    fun dominantDim(): Int? = dominantDim(projs())

    /**
     * Returns whether the algebra is Iwanaga-Gorenstein, that is,
     * whether the right and left self-injective dimensions are finite.
     *
     * @return whether the algebra is Iwanaga-Gorenstein.
     */
    fun isIG(): Boolean = (rightSelfInjDim() != null && leftSelfInjDim() != null)

    /**
     * Returns whether the algebra is self-injective.
     *
     * @return whether the algebra is self-injective.
     */
    fun isSelfInjective(): Boolean = projs().all { it.isInjective() }

    /**
     * Creates an instance of [RFAlgebra] (the class of representation-finite algebras) from this algebra.
     *
     * @throws IllegalArgumentException if this algebra is not representation-finite.
     */
    abstract fun toRFAlgebra(): RFAlgebra<T>
}
