package io.github.haruhisa_enomoto.backend.algebra

/**
 * An abstract class representing an **indecomposable** module over an algebra.
 *
 * @param T the type of vertices in the algebra.
 */
abstract class Indec<T> {
    /**
     * The algebra over which this module is considered.
     */
    abstract val algebra: Algebra<T>

    /**
     * Returns the dimension of this module over a base field.
     *
     * @return the dimension of this module over a base field.
     */
    abstract fun dim(): Int

    /**
     * Returns whether this module is simple.
     *
     * @return whether this module is simple.
     */
    fun isSimple() = (dim() == 1)

    /**
     * Returns whether this module is projective.
     *
     * @return whether this module is projective.
     */
    abstract fun isProjective(): Boolean

    /**
     * Returns whether this module is injective.
     *
     * @return whether this module is injective.
     */
    abstract fun isInjective(): Boolean

    /**
     * Returns whether this module is a brick, that is,
     * the endomorphism ring of it is a division ring.
     * This is equivalent to that the dimension of its endomorphism ring is one.
     *
     * @return whether this module is a brick.
     */
    fun isBrick() = (algebra.hom(this, this) == 1)

    /**
     * Returns the dimension of `Hom(this, [other])`.
     *
     * @param other the other module.
     * @return the dimension of `Hom(this, [other])`.
     */
    abstract fun hom(other: Indec<T>): Int

    /**
     * Returns the dimension of projectively stable Hom space
     * `\overline{Hom}(this, [other])`.
     *
     * @param other the other module.
     * @return the dimension of `\overline{Hom}(this, [other])`.
     */
    abstract fun stableHom(other: Indec<T>): Int

    /**
     * Returns the dimension of injectively stable Hom space
     * `\overline{Hom}(this, [other])`.
     *
     * @param other the other module.
     * @return the dimension of `\overline{Hom}(this, [other])`.
     */
    abstract fun injStableHom(other: Indec<T>): Int

    /**
     * Returns the dimension of `Ext^1(this, [other])`.
     *
     * @param other the other module.
     * @return the dimension of `Ext^1(this, [other])`.
     */
    abstract fun ext1(other: Indec<T>): Int

    /**
     * Returns the dimension of `Ext^[n] (this, [other])`.
     *
     * @param other the other module.
     * @param n the degree of `Ext` (default: `1`).
     */
    fun ext(other: Indec<T>, n: Int = 1) = algebra.ext(this, other, n)

    /**
     * Returns whether this module is isomorphic to [other] or not.
     *
     * @param other the other module.
     * @return whether this module is isomorphic to [other].
     */
    abstract fun isIsomorphic(other: Indec<T>): Boolean

    /**
     * Returns the list of vertices in the top (with multiplicity).
     *
     * @return the list of vertices in the top (with multiplicity).
     */
    abstract fun topVertices(): List<T>

    /**
     * Returns the list of vertices in the socle (with multiplicity).
     *
     * @return the list of vertices in the socle (with multiplicity).
     */
    abstract fun socleVertices(): List<T>

    /** Returns the top of this module as a list of simple modules.
     *
     * @return the top of this module as a list of simple modules.
     */
    fun top(): List<Indec<T>> {
        return topVertices().map { algebra.simpleAt(it) }
    }

    /** Returns the socle of this module as a list of simple modules.
     *
     * @return the socle of this module as a list of simple modules.
     */
    fun socle(): List<Indec<T>> {
        return socleVertices().map { algebra.simpleAt(it) }
    }

    /**
     * Returns the radical of this module (as a list of indecomposable modules).
     *
     * @return the radical of this module.
     */
    abstract fun radical(): List<Indec<T>>


    /**
     * Returns the coradical of this module (`this`/socle).
     *
     * @return the coradical of this module.
     */
    abstract fun coradical(): List<Indec<T>>

    /**
     * Returns the syzygy of this module (as a list of indecomposable modules).
     * This is the kernel of the projective cover,
     * so possibly with projective direct summands.
     * This method is not intended to be used directly.
     * Use [syzygy] instead (for cache).
     *
     * @return the syzygy of this module.
     */
    abstract fun _syzygy(): List<Indec<T>>

    /**
     * Returns the syzygy of this module (as a list of indecomposable modules).
     * This is a kernel of the projective cover,
     * so possibly with projective direct summands.
     *
     * @return the syzygy of this module.
     */
    fun syzygy(): List<Indec<T>> {
        // We cache the result in [algebra.syzygyMap].
        return algebra.syzygyMap.getOrPut(this) { this._syzygy() }
    }

    /**
     * Returns the [n]-th syzygy of this module (n should be non-negative).
     *
     * @param n the degree of syzygy.
     * @return the [n]-th syzygy of this module.
     */
    fun syzygy(n: Int): List<Indec<T>> {
        require(n >= 0)
        var result = listOf(this)
        for (i in 1..n) {
            result = algebra.syzygy(result)
        }
        return result
    }

    /**
     * Returns the cosyzygy of this module (as a list of indecomposable modules).
     *
     * @return the cosyzygy of this module.
     */
    abstract fun cosyzygy(): List<Indec<T>>


    /**
     * Returns the [n]-th cosyzygy of this module (n should be non-negative).
     *
     * @param n the degree of cosyzygy.
     * @return the [n]-th cosyzygy of this module.
     */
    fun cosyzygy(n: Int): List<Indec<T>> {
        require(n >= 0)
        var result = listOf(this)
        for (i in 1..n) {
            result = algebra.cosyzygy(result)
        }
        return result
    }

    /**
     * Returns the projective cover of this module.
     *
     * @return the projective cover of this module.
     */
    fun projCover(): List<Indec<T>> {
        return topVertices().map { algebra.projAt(it) }
    }

    /**
     * Returns the injective hull of this module.
     *
     * @return the injective hull of this module.
     */
    fun injHull(): List<Indec<T>> {
        return socleVertices().map { algebra.injAt(it) }
    }

    /**
     * Returns the minimal projective presentation `P_1 -> P_0` of this module
     * as the pair of vertices (with first: `P_0`, second: `P_1`).
     *
     * @return the minimal projective presentation of this module.
     */
    fun projPresentationAsVertices(): Pair<List<T>, List<T>> {
        return Pair(topVertices(), algebra.topVertices(syzygy()))
    }

    /**
     * Returns the pair of [thetaPlus] and [tauPlus].
     *
     * @see thetaPlus
     * @see tauPlus
     */
    abstract fun sinkSequence(): Pair<List<Indec<T>>, Indec<T>?>

    /**
     * Let `X` be this module. Then returns the domain `E`
     * of the sink map (right minimal almost split) `E -> X` of `X`.
     */
    fun thetaPlus() = sinkSequence().first

    /** Returns the Auslander-Reiten translation of this module,
     * or `null` if this module is projective.
     *
     * @return the Auslander-Reiten translation of this module.
     */
    fun tauPlus() = sinkSequence().second

    /**
     * Returns the pair of [thetaMinus] and [tauMinus].
     *
     * @see thetaMinus
     * @see tauMinus
     */
    abstract fun sourceSequence(): Pair<List<Indec<T>>, Indec<T>?>

    /**
     * Let `X` be this module. Then returns the codomain `E`
     * of the source map (left minimal almost split) `X -> E` of `X`.
     */
    fun thetaMinus() = sourceSequence().first

    /** Returns the inverse of the Auslander-Reiten translation of this module,
     * or `null` if this module is injective.
     *
     * @return the inverse of the Auslander-Reiten translation of this module.
     */
    fun tauMinus() = sourceSequence().second

    /**
     * Returns the list of support vertices of modules **with multiplicity**,
     * so it has the same information as the dimension vector.
     *
     * @return the list of support vertices of modules with multiplicity.
     * @see support
     */
    abstract fun vertexList(): List<T>

    /**
     * Returns the set of support vertices of this module: the set of vertices
     * on which this module is non-zero.
     *
     * @return the set of support vertices of this module.
     */
    fun support() = vertexList().toSet()

    /**
     * Returns the projective dimension of this module, or `null` if infinite.
     *
     * @return the projective dimension of this module, or `null` if infinite.
     */
    fun projDim(): Int? {
        /*
        Create a (small) syzygy quiver from [this] (only records paths).
        Search all paths of this quiver, and returns `null` if cycle found.
        If no cycle found, returns the maximum length of path - 1.
        */
        val queue = ArrayDeque<List<Indec<T>>>()
        val maximalPaths = mutableListOf<List<Indec<T>>>()
        queue.add(listOf(this))
        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst()
            val nextVertices = currentPath.last().syzygy().distinct()
            if (nextVertices.isEmpty()) maximalPaths.add(currentPath)
            for (next in nextVertices) {
                if (currentPath.any { it.isIsomorphic(next) }) return null
                queue.add(currentPath + next)
            }
        }
        return maximalPaths.maxOf { it.size - 1 }
    }

    /**
     * Returns the injective dimension of this module, or `null` if infinite.
     *
     * @return the injective dimension of this module, or `null` if infinite.
     */
    fun injDim(): Int? {
        val queue = ArrayDeque<List<Indec<T>>>()
        val maximalPaths = mutableListOf<List<Indec<T>>>()
        queue.add(listOf(this))
        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst()
            val nextVertices = currentPath.last().cosyzygy().distinct()
            if (nextVertices.isEmpty()) maximalPaths.add(currentPath)
            for (next in nextVertices) {
                if (currentPath.any { it.isIsomorphic(next) }) return null
                queue.add(currentPath + next)
            }
        }
        return maximalPaths.maxOf { it.size - 1 }
    }

    /**
     * Returns the dominant dimension of this module, determined using the minimal injective resolution:
     * `0 -> this -> I^0 -> I^1 -> I^2 -> ...`
     * The dominant dimension is the least n such that I^n is not projective. If all are projective (e.g., this module is a proj-injective module),
     * it returns `null` for infinity.
     *
     * @return the dominant dimension of this module, or `null` if infinite.
     */
    fun dominantDim(): Int? {
        /*
        Let's call a module "good" if its inj hull is projective, and bad else.
        Starting from [mX], find all vertices in cosyzygy quiver.
        Do BFS (so smaller length to larger), and if it reaches bad vertices,
        then one can obtain dom.dim.
        If this does not yield bad vertices, then dom.dim is infinity.
         */
        fun isGood(mX: Indec<T>) = mX.injHull().all { it.isProjective() }

        val queue = ArrayDeque<List<Indec<T>>>()
        val visited = mutableListOf(this)
        queue.add(listOf(this))
        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst() // BFS, so length 1, 1, 1, 2, 2, 2, ...
            if (!isGood(currentPath.last())) {
                // If the last vertex is not good, then we found dom.dim!
                return currentPath.size - 1
            }// Now all vertices in [currentPath] are good, so take cosyzygy and add paths.
            val nextVertices = currentPath.last().cosyzygy().distinct()
            for (next in nextVertices) {
                val already = visited.any { it.isIsomorphic(next) }
                // If [next] already appeared (so it's a good vertex), stop creating.
                if (already) continue
                queue.add(currentPath + next)
                visited.add(next)
            }
        }
        return null
    }

    /**
     * Returns the co-dominant dimension of this module, determined using the minimal projective resolution:
     * `... -> P_2 -> P_1 -> P_0 -> this -> 0`
     * The dominant dimension is the least n such that P_n is not injective.
     * If all are injective e.g., (this module is a proj-injective module),
     * it returns `null` for infinity.
     *
     * @return the dominant dimension of this module, or `null` if infinite.
     */
    fun coDominantDim(): Int? {
        fun isGood(mX: Indec<T>) = mX.projCover().all { it.isInjective() }

        val queue = ArrayDeque<List<Indec<T>>>()
        val visited = mutableListOf(this)
        queue.add(listOf(this))
        while (queue.isNotEmpty()) {
            val currentPath = queue.removeFirst() // BFS, so length 1, 1, 1, 2, 2, 2, ...
            if (!isGood(currentPath.last())) {
                // If the last vertex is not good, then we found dom.dim!
                return currentPath.size - 1
            }// Now all vertices in [currentPath] are good, so take cosyzygy and add paths.
            val nextVertices = currentPath.last().syzygy().distinct()
            for (next in nextVertices) {
                val already = visited.any { it.isIsomorphic(next) }
                // If [next] already appeared (so it's a good vertex), stop creating.
                if (already) continue
                queue.add(currentPath + next)
                visited.add(next)
            }
        }
        return null
    }

    /**
     * Returns whether this module `X` is [n]-torsionless, that is,
     * `Ext^i(Tr X, A) = 0` for `i = 1 .. [n]`.
     * This is equivalent to `Ext^i(DA, D Tr X) = 0`.
     *
     * @param n the degree.
     * @return whether this module is [n]-torsionless.
     */
    fun isTorsionLess(n: Int = 1): Boolean {
        return (1..n).all { i ->
            algebra.ext(algebra.injs(), this.tauPlus(), i) == 0
        }
    }

    /**
     * Returns whether this module X is infinite-torsionless, that is,
     * `Ext^i(Tr X, A) = 0` for all `i > 0`.
     * This is equivalent to `Ext^i(DA, D Tr X) = 0`.
     *
     * @return whether this module is infinite-torsionless.
     */
    fun isInfiniteTorsionless(): Boolean {
        return algebra.higherExtZero(algebra.injs(), this.tauPlus())
    }

    /**
     * Returns whether this module is semi-Gorenstein-projective,
     * that is, `Ext^i(X, A) = 0` for all `i > 0`.
     *
     * @return whether this module is semi-Gorenstein-projective.
     */
    fun isSemiGorensteinProj(): Boolean {
        return algebra.higherExtZero(this, algebra.projs())
    }

    /**
     * Returns whether this module is Gorenstein-projective.
     * There are many characterization and names (MCM, totally reflexive).
     * Here we adopt the characterization that this module X is GP
     * iff `Ext^{>0}(X, A) = Ext^{>0}(Tr X, A) = 0`, that is,
     * semi-Gorenstein-projective and infinite-torsion-free.
     *
     * @return whether this module is Gorenstein-projective.
     */
    fun isGorensteinProj(): Boolean {
        return isSemiGorensteinProj() && isInfiniteTorsionless()
    }

    /**
     * Returns the list of all indecomposable summands of the higher syzygies (`\Omega^i(X)`) for this module `X`, where i >= 0 (including `X`).
     * Useful for checking if `Ext^{>0}(X, Y) = 0`.
     * Note: This function may not terminate for non-monomial algebras, as there could be infinitely many syzygies.
     * For monomial algebras, the function will eventually stop.
     *
     * @return the list of all indecomposable summands of higher syzygies for this module.
     */
    fun allSyzygies(): List<Indec<T>> {
        // Simple BFS search for nodes in the graph.
        val queue: ArrayDeque<Indec<T>> = ArrayDeque()
        val visited = mutableListOf<Indec<T>>()
        queue.add(this)
        visited.add(this)
        while (queue.isNotEmpty()) {
            val currentVertex = queue.removeFirst()
            val nextVertices = currentVertex.syzygy()
            for (next in nextVertices) {
                if (visited.any { it.isIsomorphic(next) }) continue
                queue.add(next)
                visited.add(next)
            }
        }
        return visited
    }

    /**
     * Returns whether this module is self-orthogonal, that is,
     * `Ext^i(this, this) = 0` for all `i > 0`.
     *
     * @return whether this module is self-orthogonal.
     */
    fun isSelfOrthogonal(): Boolean {
        return algebra.higherExtZero(this, this)
    }

    /*
    Methods related to syzygy-inverse, torsionless, Gorenstein-proj, etc.
    Largely based on Ringel-Zhang's paper
    "Gorenstein-projective and semi-Gorenstein-projective modules."
     */

    /**
     * Return the syzygy inverse operator of this module, that is,
     * the cokernel of the left minimal proj-approximation.
     * This is equal to the composition `Tr \Omega Tr`.
     * Thus, this is `(Tr D) D \Omega D (D Tr) = \tau^{-} \Sigma \tau`.
     *
     * @return the syzygy inverse operator of this module.
     */
    fun syzygyInverse(): List<Indec<T>> {
        return this.tauPlus()?.cosyzygy()?.mapNotNull { it.tauMinus() } ?: listOf()
    }

    /**
     * Return the composition of the syzygy inverse operator [n] times.
     * This is equal to `\tau^{-} \Sigma^{n} \tau`.
     *
     * @param n the number of times to compose.
     * @return the composition of the syzygy inverse operator [n] times.
     */
    fun syzygyInverse(n: Int): List<Indec<T>> {
        require(n > 0)
        return this.tauPlus()?.cosyzygy(n)?.mapNotNull { it.tauMinus() } ?: listOf()
    }

    /**
     * Returns whether this module is torsionless, that is,
     * a submodule of some projective module.
     *
     * @return whether this module is torsionless.
     */
    fun isTorsionLess(): Boolean {
        /*
        This is equivalent to that this module `X` is isomorphic to
        `\Omega (\Omega^{-} X)`.
        If `X` is indec non-projective torsionless,
        then `Z := \Omega^{-} X` should be indec.
        Moreover, since we always have a right exact sequence
        `0 -> Ker -> X -> P -> Z -> 0`,
        and since the right-most map is checked to be proj cover,
        it suffices to check the dimension for Ker to vanish.
        */
        if (this.isProjective()) return true
        val omegaMinus = this.syzygyInverse()
        if (omegaMinus.size != 1) return false
        val mZ = omegaMinus[0]
        val cover = mZ.projCover()
        return this.dim() + mZ.dim() == cover.sumOf { it.dim() }
    }

    /**
     * Returns whether this module is reflexive, that is,
     * the canonical map to its double ring-dual is isomorphism.
     * This is equivalent to 2-torsionless.
     *
     * @return whether this module is reflexive.
     */
    fun isReflexive(): Boolean {
        return isTorsionLess(2)
    }
}