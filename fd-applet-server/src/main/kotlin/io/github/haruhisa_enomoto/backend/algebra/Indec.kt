package io.github.haruhisa_enomoto.backend.algebra

/**
 * A class for **indecomposable** modules over [algebra].
 * Use `List<Module<T>>` for non-indecomposable modules.
 * Currently only for finite-dimensional modules.
 */
abstract class Indec<T> {
    /**
     * The algebra over which the module is considered.
     */
    abstract val algebra: Algebra<T>

    /**
     * Returns the dimension of the module.
     */
    abstract fun dim(): Int

    /**
     * Returns whether the module is simple.
     */
    fun isSimple() = (dim() == 1)

    /**
     * Returns whether the module is projective.
     */
    abstract fun isProjective(): Boolean

    /**
     * Returns whether the module is injective.
     */
    abstract fun isInjective(): Boolean

    /**
     * Returns whether the module is brick, that is,
     * the endomorphism ring of it is a division ring.
     */
    fun isBrick() = (algebra.hom(this, this) == 1)

    /**
     * Returns the dimension of Hom space from the module to [other].
     */
    abstract fun hom(other: Indec<T>): Int

    /**
     * Returns the dimension of projectively stable Hom space from the module to [other].
     */
    abstract fun stableHom(other: Indec<T>): Int

    /**
     * Returns the dimension of injectively stable Hom space from the module to [other].
     */
    abstract fun injStableHom(other: Indec<T>): Int

    /**
     * Returns the dimension of Ext^1 the module to [other].
     */
    abstract fun ext1(other: Indec<T>): Int

    /**
     * Returns the dimension of Ext^[n] the module to [other].
     */
    fun ext(other: Indec<T>, n: Int = 1) = algebra.ext(this, other, n)

    /**
     * Returns whether the module is isomorphic to [other] or not.
     */
    abstract fun isIsomorphic(other: Indec<T>): Boolean

    /**
     * Returns the list of vertices in the top (with multiplicity).
     */
    abstract fun topVertices(): List<T>

    /**
     * Returns the list of vertices in the socle (with multiplicity).
     */
    abstract fun socleVertices(): List<T>

    /** Returns the top of the module as a list of simple modules. */
    fun top(): List<Indec<T>> {
        return topVertices().map { algebra.simpleAt(it) }
    }

    /** Returns the socle of the module as a list of simple modules. */
    fun socle(): List<Indec<T>> {
        return socleVertices().map { algebra.simpleAt(it) }
    }


    /**
     * Returns the radical of the module (as a list of indecomposable modules).
     */
    abstract fun radical(): List<Indec<T>>


    /**
     * Returns the coradical of the module (`this`/socle).
     */
    abstract fun coradical(): List<Indec<T>>

    /**
     * Returns the syzygy of the module (as a list of indecomposable modules).
     * This is a kernel of the projective cover
     * (so possible with projective direct summands).
     * This is private and **not for use**.
     * Use [syzygy] instead (for cache).
     */
    abstract fun _syzygy(): List<Indec<T>>

    /**
     * Returns the syzygy of the module (as a list of indecomposable modules).
     * This is a kernel of the projective cover
     * (so possible with projective direct summands).
     */
    fun syzygy(): List<Indec<T>> {
        // We cache the result in [algebra.syzygyMap].
        return algebra.syzygyMap.getOrPut(this) { this._syzygy() }
    }

    /**
     * Returns the [n]-th syzygy of the module (n should be non-negative).
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
     * Returns the cosyzygy of the module (as a list of indecomposable modules).
     */
    abstract fun cosyzygy(): List<Indec<T>>


    /**
     * Returns the [n]-th cosyzygy of the module (n should be non-negative).
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
     * Returns the projective cover of the module.
     */
    fun projCover(): List<Indec<T>> {
        return topVertices().map { algebra.projAt(it) }
    }

    /**
     * Returns the injective hull of the module.
     */
    fun injHull(): List<Indec<T>> {
        return socleVertices().map { algebra.injAt(it) }
    }

    /**
     * Returns the minimal projective presentation of the module
     * as the pair of vertices (with first: 0-th, second: 1-th).
     */
    fun projPresentationAsVertices(): Pair<List<T>, List<T>> {
        return Pair(topVertices(), algebra.topVertices(syzygy()))
    }

    /**
     * Returns the pair of [thetaPlus] and [tauPlus].
     */
    abstract fun sinkSequence(): Pair<List<Indec<T>>, Indec<T>?>

    /**
     * Returns the domain `E` of the sink map `E -> X` of `X`.
     */
    fun thetaPlus() = sinkSequence().first

    /** Returns the AR translate of the module,
     * or `null` if `this` is projective. */
    fun tauPlus() = sinkSequence().second

    /**
     * Returns the pair of [thetaMinus] and [tauMinus].
     */
    abstract fun sourceSequence(): Pair<List<Indec<T>>, Indec<T>?>

    /**
     * Returns the codomain `E` of the source map `X -> E` of `X`.
     */
    fun thetaMinus() = sourceSequence().first

    /** Returns the inverse of the AR translate of the module,
     * or `null` if `this` is injective. */
    fun tauMinus() = sourceSequence().second

    /**
     * The list of vertices of modules **with multiplicity**,
     * so has the same information as the dimension vector.
     */
    abstract fun vertexList(): List<T>

    /**
     * Returns the list of support of the module: the list of vertices
     * on which the module is non-zero.
     */
    fun support() = vertexList().toSet()

    /**
     * Returns the projective dimension of the module, null if infinity.
     */
    fun projDim(): Int? {/*
      Create a (small) syzygy quiver from [this] (only records paths).
      Search all paths of this quiver, and returns null if cycle found.
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
     * Returns the injective dimension of the module, null if infinity.
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
     * Take the minimal injective resolution of the module:
     * 0 -> this -> I^0 -> I^1 -> I^2 -> ...
     * Then returns the least n such that I^n is not projective, and null (infinity)
     * if all are projective-injective (e.g. `this` is proj-injective module).
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
     * Take the minimal projective resolution of the module:
     *  -> P_n -> ... -> P_1 -> P_0 -> this -> 0
     * Then returns the least n such that P_n is not injective, and null (infinity)
     * if all are projective-injective (e.g. `this` is proj-injective module).
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
     * Returns whether the module X is [n]-torsionless, that is,
     * Ext^i(Tr X, A) = 0 for i = 1 .. [n].
     * This is equivalent to Ext^i(DA, D Tr X) = 0.
     */
    fun isTorsionLess(n: Int = 1): Boolean {
        return (1..n).all { i ->
            algebra.ext(algebra.injs(), this.tauPlus(), i) == 0
        }
    }

    /**
     * Returns whether the module X is infinite-torsionless, that is,
     * Ext^i(Tr X, A) = 0 for all i > 0.
     * This is equivalent to Ext^i(DA, D Tr X) = 0.
     */
    fun isInfiniteTorsionless(): Boolean {
        return algebra.higherExtZero(algebra.injs(), this.tauPlus())
    }

    /**
     * Returns whether the module is semi-Gorenstein-projective, that is,
     * Ext^i(X, A) = 0 for all i > 0.
     */
    fun isSemiGorensteinProj(): Boolean {
        return algebra.higherExtZero(this, algebra.projs())
    }

    /**
     * Returns whether the module is Gorenstein-projective.
     * There are many characterization and names (MCM, totally reflexive).
     * Here we adopt the characterization that the module X is GP
     * iff Ext^{>0}(X, A) and Ext^{>0}(Tr X, A) vanish:
     * that is, semi-Gorenstein-projective and infinite-torsion-free.
     */
    fun isGorensteinProj(): Boolean {
        return isSemiGorensteinProj() && isInfiniteTorsionless()
    }

    /**
     * Returns the list of all modules which are indecomposable summands
     * of \Omega^i(X) for some i >= 0.
     * Will be useful to check Whether Ext^{>0}(X, Y) = 0.
     * Note that this may not terminate for non-monomial algebra,
     * since there may be infinitely many syzygies.
     * (But if the algebra is monomial, then this stops.)
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
     * Returns whether Ext^i(this, this) = 0 for all i > 0.
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
     * Return the cokernel of the left minimal proj-approximation.
     * Call this the **syzygy inverse operator** \Omega^{-}.
     * This is Tr \Omega Tr (Tr: Auslander-Bridger transpose).
     * Thus, it's (Tr D) D \Omega D (D Tr) = tau^{-} \Sigma tau.
     */
    fun syzygyInverse(): List<Indec<T>> {
        return this.tauPlus()?.cosyzygy()?.mapNotNull { it.tauMinus() } ?: listOf()
    }

    /**
     * Return the composition of \Omega^{-} [n] times.
     * This is tau^{-} \Sigma^{n} tau.
     */
    fun syzygyInverse(n: Int): List<Indec<T>> {
        require(n > 0)
        return this.tauPlus()?.cosyzygy(n)?.mapNotNull { it.tauMinus() } ?: listOf()
    }

    /**
     * Returns whether the module is a submodule of a projective module.
     * This is equivalent to that the module X is isomorphic to
     * \Omega (\Omega^{-} X).
     * If X is indec non-projective torsionless,
     * then Z := \Omega^{-} X should be indec.
     * Moreover, since we always have a right exact sequence
     * 0 \to Ker \to X \to P \to Z \to 0,
     * and since the right-most map is checked to be proj cover,
     * it suffices to check the dimension for Ker to vanish.
     */
    fun isTorsionLess(): Boolean {
        if (this.isProjective()) return true
        val omegaMinus = this.syzygyInverse()
        if (omegaMinus.size != 1) return false
        val mZ = omegaMinus[0]
        val cover = mZ.projCover()
        return this.dim() + mZ.dim() == cover.sumOf { it.dim() }
    }

    /**
     * Returns whether the module `mX` is reflexive:
     * the canonical map to double ring-dual is isomorphism.
     * This is equivalent to 2-torsionless
     */
    fun isReflexive(): Boolean {
        return isTorsionLess(2)
    }

}