package io.github.haruhisa_enomoto.backend.algebra

import io.github.haruhisa_enomoto.backend.utils.ListWithLeq
import io.github.haruhisa_enomoto.backend.types.Subcat
import io.github.haruhisa_enomoto.backend.utils.powerSetList
import io.github.haruhisa_enomoto.backend.quiver.TranslationQuiver
import io.github.haruhisa_enomoto.backend.graph.almostMaximalCliques
import io.github.haruhisa_enomoto.backend.graph.cliques
import io.github.haruhisa_enomoto.backend.graph.maximalCliques
import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.types.IndecTauRigidPair
import io.github.haruhisa_enomoto.backend.types.ModuleWithSupport
import io.github.haruhisa_enomoto.backend.types.TauTiltingData
import io.github.haruhisa_enomoto.backend.types.toTauTiltingPair

/**
 * A class for representation-finite algebras [algebra],
 * together with a list [indecs] of the complete set of indecomposable modules.
 * Throughout this class, all "modules" are finitely generated **basic** modules,
 * and all "subcategories" are assumed to be subcategories
 * of the category of finitely generated modules.
 *
 * @param T the type of vertex labels.
 * @property algebra the algebra.
 * @property indecs the list of all indecomposable modules.
 * @property normalize a function that normalizes a given indecomposable module to the one in [indecs].
 * @constructor Creates a representation-finite algebra.
 * @throws IllegalArgumentException if [algebra] is not representation-finite.
 */
class RfAlgebra<T>(
    private val algebra: Algebra<T>, val indecs: List<Indec<T>>, private val normalize: (Indec<T>) -> Indec<T>,
) : Algebra<T>() {
    init {
        require(algebra.isRepFinite()) {
            "This is not representation-finite. "
        }
    }

    override val vertices = algebra.vertices

    // The following properties are for caching.

    private val _arQuiver by lazy { makeARQuiver() }
    private val tau by lazy { _arQuiver.tau }
    private val tauMinus by lazy { _arQuiver.tauMinus }
    private val _bricks by lazy { bricks() }
    private val _indecRigids by lazy { indecRigids() }
    private val _indecTauRigids by lazy { indecTauRigids() }
    private val _indecTauMinusRigids by lazy { indecTauMinusRigids() }
    private val _semibricks by lazy { obtainSemibricks() }
    private val _torfToSemibrick by lazy {
        _semibricks.associateBy { torsionFreeClosure(it) }
    }

    override fun isStringAlgebra() = algebra.isStringAlgebra()

    override fun isGentleAlgebra() = algebra.isGentleAlgebra()

    override fun numberOfIndecs() = indecs.size

    override fun dim() = algebra.dim()

    override fun simpleAt(vtx: T) = normalize(algebra.simpleAt(vtx))

    override fun projAt(vtx: T) = normalize(algebra.projAt(vtx))

    override fun injAt(vtx: T) = normalize(algebra.injAt(vtx))

    override fun isRepFinite() = true

    override fun toRfAlgebra() = this

    /**
     * Returns the finitistic dimension of [algebra], that is,
     * the maximum of the projective dimensions of indecomposable modules
     * with finite projective dimension.
     *
     * @return the finitistic dimension of [algebra].
     */
    fun finitisticDim(): Int = indecs.mapNotNull { it.projDim() }.max()

    /**
     * Returns the list of bricks.
     *
     * @return the list of bricks.
     */
    fun bricks(): List<Indec<T>> {
        return indecs.filter { it.isBrick() }
    }

    /**
     * Returns the Auslander-Reiten quiver of [algebra].
     *
     * @return the Auslander-Reiten quiver of [algebra].
     */
    fun arQuiver(): TranslationQuiver<Indec<T>> {
        return _arQuiver
    }

    private fun makeARQuiver(): TranslationQuiver<Indec<T>> {
        // For safety, we construct the AR quiver in two ways:
        // First by sink maps and second by source maps.
        val irreducibles = mutableListOf<Arrow<Indec<T>, Nothing>>()
        val irreducibles2 = mutableListOf<Arrow<Indec<T>, Nothing>>()
        val tau = mutableMapOf<Indec<T>, Indec<T>>()
        for (mX in indecs) {
            val middles = mX.thetaPlus()
            val mTauX = mX.tauPlus()
            if (mTauX != null) {
                tau[mX] = normalize(mTauX)
                if (tau[mX] !in indecs) throw IllegalStateException(
                    "The normalization of $mTauX does not belong to $indecs"
                )
            }
            for (mM in middles) {
                if (normalize(mM) !in indecs) throw IllegalStateException(
                    "The normalization of $mM does not belong to $indecs"
                )
                irreducibles.add(Arrow(null, normalize(mM), mX))
            }
        }
        for (mX in indecs) {
            val temp = mX.thetaMinus()
            for (mN in temp) {
                irreducibles2.add(Arrow(null, mX, normalize(mN)))
            }
        }
        if (irreducibles.toSet() != irreducibles2.toSet()) {
            throw IllegalStateException(
                "The two ways of constructing the AR quiver are different."
            )
        }
        return TranslationQuiver(Quiver(indecs, irreducibles), tau)
    }

    private fun obtainSemibricks(): List<List<Indec<T>>> {
        val neighbor = _bricks.associateWith { mX ->
            _bricks.filter {
                it !== mX && homZero(mX, it) && homZero(it, mX)
            }
        }
        return cliques(neighbor)
    }

    /**
     * Returns the list of semibricks, pair-wise Hom-orthogonal bricks.
     *
     * @return the list of semibricks.
     */
    fun semibricks(): List<List<Indec<T>>> {
        return _semibricks
    }

    /**
     * Returns the right hom-perpendicular category of [cC],
     * the subcategory consisting of `X` such that `Hom([cC], X) = 0`.
     *
     * @param cC a subcategory.
     * @return the right hom-perpendicular category of [cC].
     */
    fun homRightPerp(cC: Subcat<T>): Subcat<T> {
        return indecs.filter { hom(cC, it) == 0 }
    }

    /**
     * Returns the left hom-perpendicular category of [cC],
     * the subcategory consisting of `X` such that `Hom(X, [cC]) = 0`.
     *
     * @param cC a subcategory.
     * @return the left hom-perpendicular category of [cC].
     */
    fun homLeftPerp(cC: Subcat<T>): Subcat<T> {
        return indecs.filter { hom(it, cC) == 0 }
    }

    /**
     * Returns the list of bricks `X` such that `Hom([cC], X) = 0`.
     *
     * @param cC a subcategory.
     * @return the list of bricks `X` such that `Hom([cC], X) = 0`.
     */
    fun homRightPerpBricks(cC: Subcat<T>): List<Indec<T>> {
        return _bricks.filter { hom(cC, it) == 0 }
    }

    /**
     * Returns the list of bricks `X` such that `Hom(X, [cC]) = 0`.
     *
     * @param cC a subcategory.
     * @return the list of bricks `X` such that `Hom(X, [cC]) = 0`.
     */
    fun homLeftPerpBricks(cC: Subcat<T>): List<Indec<T>> {
        return _bricks.filter { hom(it, cC) == 0 }
    }

    /**
     * Generates the sequence of torsion classes of [algebra].
     *
     * @return the sequence of torsion classes of [algebra].
     */
    fun torsionClassSequence(): Sequence<Subcat<T>> {
        return _semibricks.asSequence().map { homLeftPerp(it) }
    }

    /**
     * Returns the list of torsion classes of [algebra].
     *
     * @return the list of torsion classes of [algebra].
     */
    fun torsionClasses(): List<Subcat<T>> {
        return torsionClassSequence().toList()
    }

    /**
     * Generates the sequence of torsion-free classes of [algebra].
     *
     * @return the sequence of torsion-free classes of [algebra].
     */
    fun torsionFreeClassSequence(): Sequence<Subcat<T>> {
        return _semibricks.asSequence().map { homRightPerp(it) }
    }

    /**
     * Returns the list of torsion-free classes of [algebra].
     *
     * @return the list of torsion-free classes of [algebra].
     */
    fun torsionFreeClasses(): List<Subcat<T>> {
        return torsionFreeClassSequence().toList()
    }

    /**
     * Generates the sequence of IE-closed subcategories of [algebra].
     * Here a subcategory is IE-closed if it is closed under taking images and extensions.
     *
     * @return the sequence of IE-closed subcategories of [algebra].
     */
    fun ieClosedSubcatsSequence(): Sequence<Subcat<T>> = sequence {
        val tors = _semibricks.map { homLeftPerp(it) }
        val torf = _semibricks.map { homRightPerp(it) }
        val result = mutableSetOf<Subcat<T>>()
        for (cTT in tors) {
            for (cFF in torf) {
                val candidate = cTT intersect cFF
                if (result.add(candidate)) yield(candidate)
//                if (result.size % 1000 == 0) println(result.size)
            }
        }
    }

    /**
     * Returns the list of IE-closed subcategories of [algebra].
     * Here a subcategory is IE-closed if it is closed under taking images and extensions.
     *
     * @return the list of IE-closed subcategories of [algebra].
     */
    fun ieClosedSubcats(): List<Subcat<T>> {
        return ieClosedSubcatsSequence().toList()
    }

    /**
     * Returns the IE-closure of [cC], that is, the smallest IE-closed subcategory containing [cC].
     * Here a subcategory is IE-closed if it is closed under taking images and extensions.
     *
     * @param cC a subcategory.
     * @return the IE-closure of [cC].
     */
    fun ieClosure(cC: Subcat<T>): Subcat<T> {
        return torsionClosure(cC) intersect torsionFreeClosure(cC)
    }

    /**
     * Generates the sequence of wide subcategories of [algebra].
     *
     * @return the sequence of wide subcategories of [algebra].
     */
    fun wideSubcatSequence(): Sequence<Subcat<T>> {
        return _semibricks.asSequence().map { ieClosure(it) }
    }

    /**
     * Returns the list of wide subcategories of [algebra].
     *
     * @return the list of wide subcategories of [algebra].
     */
    fun wideSubcats(): List<Subcat<T>> {
        return wideSubcatSequence().toList()
    }

    /**
     * Returns the torsion closure of [cC], that is, the smallest torsion class containing [cC].
     *
     * @param cC a subcategory.
     * @return the torsion closure of [cC].
     */
    fun torsionClosure(cC: Subcat<T>): Subcat<T> {
        return homLeftPerp(homRightPerp(cC))
    }

    /**
     * Returns the torsion-free closure of [cC], that is, the smallest torsion-free class containing [cC].
     *
     * @param cC a subcategory.
     * @return the torsion-free closure of [cC].
     */
    fun torsionFreeClosure(cC: Subcat<T>): Subcat<T> {
        return homRightPerp(homLeftPerp(cC))
    }

    /**
     * Returns the list of bricks contained in the torsion closure of [cC].
     *
     * @param cC a subcategory.
     * @return the list of bricks contained in the torsion closure of [cC].
     */
    fun torsionClosureBricks(cC: Subcat<T>): List<Indec<T>> {
        return homLeftPerpBricks(homRightPerpBricks(cC))
    }

    /**
     * Returns the list of bricks contained in the torsion-free closure of [cC].
     *
     * @param cC a subcategory.
     * @return the list of bricks contained in the torsion-free closure of [cC].
     */
    fun torsionFreeClosureBricks(cC: Subcat<T>): Subcat<T> {
        return homRightPerpBricks(homLeftPerpBricks(cC))
    }

    /**
     * Returns the set of supports of [cC], the union of supports of modules in [cC].
     *
     * @param cC a subcategory.
     * @return the set of supports of [cC].
     */
    fun support(cC: Subcat<T>): Set<T> {
        return cC.flatMap { it.support() }.toSet()
    }

    /**
     * Returns the list of ICE-closed subcategories of [algebra].
     * Here a subcategory is ICE-closed if it is closed
     * under taking images, cokernels, and extensions.
     *
     * @return the list of ICE-closed subcategories of [algebra].
     */
    fun iceClosedSubcats(): List<Subcat<T>> {
        val allSemibricks = _semibricks
        val result = mutableSetOf<Subcat<T>>()
        for (mS in allSemibricks) {
            // Consider wide subcat [cW] corresponding to [mS].
            val cW = ieClosure(mS)
            // We will compute torsion classes in [cW].
            // So loops over semibricks contained in [cW].
            for (mS2 in allSemibricks.filter { cW.containsAll(it) }) {
                result.add(cW.filter { hom(it, mS2) == 0 })
            }
        }
        return result.toList()
    }

    /**
     * Returns the list of IKE-closed subcategories of [algebra].
     * Here a subcategory is IKE-closed if it is closed
     * under taking images, kernels, and extensions.
     *
     * @return the list of IKE-closed subcategories of [algebra].
     */
    fun ikeClosedSubcats(): List<Subcat<T>> {
        val allSemibricks = _semibricks
        val result = mutableSetOf<Subcat<T>>()
        for (mS in allSemibricks) {
            // Consider wide subcat [cW] corresponding to [mS].
            val cW = ieClosure(mS)
            // We will compute torsion-free classes in [cW].
            // So loops over semibricks contained in [cW].
            for (mS2 in allSemibricks.filter { cW.containsAll(it) }) {
                result.add(cW.filter { hom(mS2, it) == 0 })
            }
        }
        return result.toList()
    }

    /**
     * Returns the subcategory consisting of modules with finite projective dimension.
     *
     * @return the subcategory consisting of modules with finite projective dimension.
     */
    fun indecsWithFiniteProjDim(): Subcat<T> {
        return indecs.filter { it.projDim() != null }
    }

    /**
     * Returns the subcategory consisting of modules with finite injective dimension.
     *
     * @return the subcategory consisting of modules with finite injective dimension.
     */
    fun indecsWithFiniteInjDim(): Subcat<T> {
        return indecs.filter { it.injDim() != null }
    }

    /**
     * Returns the syzygy quiver of [algebra].
     *
     * @return the syzygy quiver of [algebra].
     * @see Algebra.syzygyQuiverFrom
     */
    fun syzygyQuiver(): Quiver<Indec<T>, Nothing> {
        return syzygyQuiverFrom(indecs)
    }

    /**
     * Returns the cosyzygy quiver of [algebra].
     *
     * @return the cosyzygy quiver of [algebra].
     * @see Algebra.syzygyQuiverFrom
     */
    fun cosyzygyQuiver(): Quiver<Indec<T>, Nothing> {
        return syzygyQuiverFrom(indecs, cosyzygy = true)
    }

    /**
     * Returns the list of tau-rigid modules, that is,
     * modules `mX` such that `Hom(mX, \tau mX) = 0`.
     *
     * @return the list of tau-rigid modules.
     */
    fun tauRigids(): List<List<Indec<T>>> {
        val neighbor = _indecTauRigids.associateWith { mX ->
            _indecTauRigids.filter {
                it !== mX && homTauOrtho(it, mX)
            }
        }
        return cliques(neighbor)
    }

    fun tauMinusRigids(): List<List<Indec<T>>> {
        val neighbor = _indecTauMinusRigids.associateWith { mX ->
            _indecTauMinusRigids.filter {
                it !== mX && homTauMinusOrtho(it, mX)
            }
        }
        return cliques(neighbor)
    }

    fun supportTauTiltings(): List<List<Indec<T>>> {
        return tauRigids().filter { mMM ->
            mMM.size == mMM.flatMap { it.support() }.toSet().size
        }
    }

    fun supportTauMinusTiltings(): List<List<Indec<T>>> {
        return tauMinusRigids().filter { mMM ->
            mMM.size == mMM.flatMap { it.support() }.toSet().size
        }
    }

    fun tauTiltings(): List<List<Indec<T>>> {
        val neighbor = _indecTauRigids.associateWith { mX ->
            _indecTauRigids.filter {
                it !== mX && homTauOrtho(it, mX)
            }
        }
        return maximalCliques(neighbor)
    }

    fun tauMinusTiltings(): List<List<Indec<T>>> {
        val neighbor = _indecTauMinusRigids.associateWith { mX ->
            _indecTauMinusRigids.filter {
                it !== mX && homTauMinusOrtho(it, mX)
            }
        }
        return maximalCliques(neighbor)
    }

    fun semibrickToTauTiltingData(mS: List<Indec<T>>): TauTiltingData<T> {
        val cT = torsionClosure(mS) // torsion class
        val mM = _indecTauRigids.filter { mM ->
            mM in cT && mS.all { homZero(it, tau[mM]) }
        }
        val support = vertices.filter { vtx -> mM.all { vtx !in it.vertexList() } }
        val pres = projResolution(mM, 1)
        val cF = homRightPerp(mS)
        // Torsion-free class corresponding to [cT]: (cT)^\perp = (mS)^\perp.
        val mN = mM.mapNotNull { tau[it] } + support.map { injAt(it) }
        // Support tau^{-}-tilting module.
        val mS2 = _torfToSemibrick[cF]!!
        val support2 = vertices.filter { vtx -> mN.all { vtx !in it.vertexList() } }
        val copre = injResolution(mN, 1)
        return TauTiltingData(
            semibrick = mS,
            supportTauTilting = mM,
            support = support,
            torsionClass = cT,
            silting = pres[0] to (pres[1] + support),
            wideSubcat = cT.filter { it in torsionFreeClosure(mS) },
            supportTauMinusTilting = mN,
            torsionFreeClass = cF,
            semibrick2 = mS2,
            support2 = support2,
            cosilting = copre[0] to (copre[1] + support2)
        )
    }

    fun tauTiltingDataList(): List<TauTiltingData<T>> {
        val sbrickToTors = _semibricks.associateWith { torsionClosure(it) }
        val sbrickToTorf = _semibricks.associateWith { torsionFreeClosure(it) }
        val torfToSbrick = sbrickToTorf.entries.associate { (key, value) -> value to key }
        val result = mutableListOf<TauTiltingData<T>>()
        for ((mS, cT) in sbrickToTors) {/*
            For tau-rigid M and a semibrick S, we have
            Ext^1(M, T(S)) = 0 iff Ext^1(M, Filt Fac(S)) = 0 iff Ext^1(M, Fac S) = 0
            iff Hom(S, tau M) = 0.
             */
            val mM = _indecTauRigids.filter { mM ->
                mM in cT && mS.all { homZero(it, tau[mM]) }
            }
            val pres = projResolution(mM, 1)
            val support = vertices.filter { vtx -> mM.all { vtx !in it.vertexList() } }
            // Support part of [mM] = vertices not appearing in any of [mM].
            val cF = homRightPerp(mS)
            // Torsion-free class corresponding to [cT]: (cT)^\perp = (mS)^\perp.
            val mN = mM.mapNotNull { tau[it] } + support.map { injAt(it) }
            // Tau-minus tilting module corresponding to the torsion-free class.
            val support2 = vertices.filter { vtx -> mN.all { vtx !in it.vertexList() } }
            val copre = injResolution(mN, 1)

            result.add(
                TauTiltingData(
                    semibrick = mS,
                    supportTauTilting = mM,
                    support = support,
                    torsionClass = cT,
                    silting = pres[0] to (pres[1] + support),
                    wideSubcat = cT.filter { it in sbrickToTorf[mS]!! },
                    supportTauMinusTilting = mN,
                    torsionFreeClass = cF,
                    semibrick2 = torfToSbrick[cF]!!,
                    support2 = support2,
                    cosilting = copre[0] to (copre[1] + support2)
                )
            )
        }
        return result
    }

    fun semibrickToSupportTauTilting(mS: List<Indec<T>>): List<Indec<T>> {
        return _indecTauRigids.filter { mM ->
            mS.all { homZero(it, tau[mM]) } && mM in torsionClosure(mS)
        }
    }

    fun indecRigids(): List<Indec<T>> {
        return indecs.filter { ext1(it, it) == 0 }
    }

    fun rigids(): List<List<Indec<T>>> {
        val neighbor = _indecRigids.associateWith { mX ->
            _indecRigids.filter {
                it !== mX && ext1(it, mX) == 0 && ext1(mX, it) == 0
            }
        }
        return cliques(neighbor)
    }

    fun indecPartialTiltings(): List<Indec<T>> {
        return _indecRigids.filter {
            it.isProjective() || it.projDim() == 1
        }
    }

    fun partialTiltings(): List<List<Indec<T>>> {
        val nodes = indecPartialTiltings()
        val neighbor = nodes.associateWith { mX ->
            nodes.filter {
                it !== mX && ext1(it, mX) == 0 && ext1(mX, it) == 0
            }
        }
        return cliques(neighbor)
    }

    fun tiltings(): List<List<Indec<T>>> {
        val nodes = indecPartialTiltings()
        val neighbor = nodes.associateWith { mX ->
            nodes.filter {
                it !== mX && ext1(it, mX) == 0 && ext1(mX, it) == 0
            }
        }
        return maximalCliques(neighbor)
    }

    fun indecPartialCotiltings(): List<Indec<T>> {
        return _indecRigids.filter {
            it.isInjective() || it.injDim() == 1
        }
    }

    fun partialCotiltings(): List<List<Indec<T>>> {
        val nodes = indecPartialCotiltings()
        val neighbor = nodes.associateWith { mX ->
            nodes.filter {
                it !== mX && ext1(it, mX) == 0 && ext1(mX, it) == 0
            }
        }
        return cliques(neighbor)
    }

    fun cotiltings(): List<List<Indec<T>>> {
        val nodes = indecPartialCotiltings()
        val neighbor = nodes.associateWith { mX ->
            nodes.filter {
                it !== mX && ext1(it, mX) == 0 && ext1(mX, it) == 0
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns whether Hom([mX], tau[mY]) = Hom([mY], tau[mX]) = 0.
     * This is equivalent to that [mX] \oplus [mY] is tau-rigid if [mX] and [mY] are so.
     */
    fun homTauOrtho(mX: Indec<T>, mY: Indec<T>): Boolean {
        return homZero(mX, tau[mY]) && homZero(mY, tau[mX])
    }

    /**
     * Returns whether Hom(tau^{-}[mX], [mY]) = Hom(tau^{-}[mY], [mX]) = 0.
     */
    fun homTauMinusOrtho(mX: Indec<T>, mY: Indec<T>): Boolean {
        return homZero(tauMinus[mX], mY) && homZero(tauMinus[mY], mX)
    }

    fun isTauRigid(mX: Indec<T>): Boolean {
        return homZero(mX, tau[mX])
    }

    fun isTauMinusRigid(mX: Indec<T>): Boolean {
        return homZero(tauMinus[mX], mX)
    }

    fun indecTauRigids(): List<Indec<T>> {
        return indecs.filter { isTauRigid(it) }
    }

    fun indecTauMinusRigids(): List<Indec<T>> {
        return indecs.filter { isTauMinusRigid(it) }
    }


    /**
     * Returns the list of indecomposable tau-rigid pair:
     * either of the form (`mM`, `null`) for `mM` indec tau-rigid, or
     * (`null`, `vtx`) for `vtx` in [vertices]
     */
    fun indecTauRigidPairs(): List<IndecTauRigidPair<T>> {
        return _indecTauRigids.map { Pair(it, null) } + vertices.map { Pair(null, it) }
    }

    fun indecTauRigidPairsOrtho(
        pair1: IndecTauRigidPair<T>, pair2: IndecTauRigidPair<T>
    ): Boolean {
        val (mX, v) = pair1
        val (mY, w) = pair2
        return if (mX != null && mY != null) {
            homTauOrtho(mX, mY)
        } else if (v != null && mY != null) {
            v !in mY.support()
        } else if (mX != null && w != null) {
            w !in mX.support()
        } else if (v != null && w != null) {
            true
        } else throw IllegalArgumentException("Invalid form.")
    }

    /**
     * Returns the list of facets of support tau-tilting simplicial complex.
     */
    fun tauTiltingFacet(): List<List<IndecTauRigidPair<T>>> {
        val indecs = indecTauRigidPairs()
        val neighbor = indecs.associateWith { pair1 ->
            indecs.filter { pair2 ->
                pair1 !== pair2 && indecTauRigidPairsOrtho(pair1, pair2)
            }
        }
        return maximalCliques(neighbor)
    }

    fun tauTiltingPairs(): List<Pair<List<Indec<T>>, List<Indec<T>>>> {
        val indecs = indecTauRigidPairs()
        val neighbor = indecs.associateWith { pair1 ->
            indecs.filter { pair2 ->
                pair1 !== pair2 && indecTauRigidPairsOrtho(pair1, pair2)
            }
        }
        return maximalCliques(neighbor).map { pairList ->
            (pairList.mapNotNull { it.first }
                    to pairList.mapNotNull
            { it.second }.map { projAt(it) })
        }
    }

    fun tauTiltingQuiver(): Quiver<ModuleWithSupport<T>, Unit> {
        val indecs = indecTauRigidPairs()
        val neighbor = indecs.associateWith { pair1 ->
            indecs.filter { pair2 ->
                pair1 !== pair2 && indecTauRigidPairsOrtho(pair1, pair2)
            }
        }
        val siltings = mutableSetOf<ModuleWithSupport<T>>()
        val hasseArrows = mutableListOf<Arrow<ModuleWithSupport<T>, Unit>>()

        for ((codimOne, rest) in almostMaximalCliques(neighbor, algebra.rank())) {
            if (rest.size != 2) throw IllegalStateException("Mutation failed!")
            val (mM1, _) = rest[0]
            val (mM2, _) = rest[1]
            val one = (codimOne + rest[0]).toTauTiltingPair()
            siltings.add(one)
            val two = (codimOne + rest[1]).toTauTiltingPair()
            siltings.add(two)
            var larger = one
            var smaller = two // Modify them below.
            if (mM1 != null && mM2 == null) {
                larger = one; smaller = two
            } else if (mM1 == null && mM2 != null) {
                larger = two; smaller = one
            } else if (mM1 != null && mM2 != null) {
                if (homZero(mM1, tau[mM2])) {
                    larger = two; smaller = one
                } else if (homZero(mM2, tau[mM1])) {
                    larger = one; smaller = two
                }
            } else throw IllegalStateException("Mutation property fails!")
            hasseArrows.add(Arrow(null, larger, smaller))
        }
        return Quiver(siltings, hasseArrows)
    }

    fun inTorsOfTauTiltingPair(mX: Indec<T>, pair: ModuleWithSupport<T>): Boolean {
        return mX.support().all { it !in pair.support } && pair.mMM.all { homZero(mX, tau[it]) }
    }

    /**
     * Returns the list of brick contained in the interval of [pair1] > [pair2].
     * We don't check whether it's actually an interval or not.
     */
    fun bricksInTauTiltingPairInterval(
        pair1: ModuleWithSupport<T>, pair2: ModuleWithSupport<T>
    ): List<Indec<T>> {
        return _bricks.filter {
            inTorsOfTauTiltingPair(it, pair1) && pair2.mMM.all { rigid -> hom(rigid, it) == 0 }
        }
    }

    fun tauTiltingQuiverWithBrickLabel(): Quiver<ModuleWithSupport<T>, Indec<T>> {
        val indecs = indecTauRigidPairs()
        val neighbor = indecs.associateWith { pair1 ->
            indecs.filter { pair2 ->
                pair1 !== pair2 && indecTauRigidPairsOrtho(pair1, pair2)
            }
        }
        val siltings = mutableSetOf<ModuleWithSupport<T>>()
        val hasseArrows = mutableListOf<Arrow<ModuleWithSupport<T>, Indec<T>>>()

        for ((codimOne, rest) in almostMaximalCliques(neighbor, rank())) {
            if (rest.size != 2) throw IllegalStateException("Mutation failed!")
            val (mM1, _) = rest[0]
            val (mM2, _) = rest[1]
            val one = (codimOne + rest[0]).toTauTiltingPair()
            siltings.add(one)
            val two = (codimOne + rest[1]).toTauTiltingPair()
            siltings.add(two)
            var larger = one
            var smaller = two // Modify them below.
            if (mM1 != null && mM2 == null) {
                larger = one; smaller = two
            } else if (mM1 == null && mM2 != null) {
                larger = two; smaller = one
            } else if (mM1 != null && mM2 != null) {
                if (homZero(mM1, tau[mM2])) {
                    larger = two; smaller = one
                } else if (homZero(mM2, tau[mM1])) {
                    larger = one; smaller = two
                }
            } else throw IllegalStateException("Mutation property fails!")
            val labelCandidates = bricksInTauTiltingPairInterval(larger, smaller)
            if (labelCandidates.size != 1) throw IllegalStateException(
                "There are ${labelCandidates.size} brick labels!"
            )
            hasseArrows.add(Arrow(labelCandidates[0], larger, smaller))
        }
        return Quiver(siltings, hasseArrows)
    }

    /**
     * Returns the list of generalized (Miyashita) tilting modules
     * with projective dimension <= [n].
     *
     * Algorithm: they are precisely self-orthogonal modules with pd <= n
     * which are maximal with respect to this property.
     */
    fun generalizedTiltings(n: Int): List<List<Indec<T>>> {
        require(n >= 0)
        val nodes = indecs.filter { mX ->
            mX.projDim()?.let { it <= n } ?: false && (1..n).all { i -> ext(mX, mX, i) == 0 }
        }
        // Indec modules with pd <= n and self-orthogonal.
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && (1..n).all { i ->
                    ext(mX, mY, i) == 0 && ext(mY, mX, i) == 0
                }
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns the list of generalized (Miyashita) cotilting modules
     * with injective dimension <= [n]
     *
     * Algorithm: they are precisely self-orthogonal modules with id <= n
     * which are maximal with respect to this property.
     */
    fun generalizedCotiltings(n: Int): List<List<Indec<T>>> {
        require(n >= 0)
        val nodes = indecs.filter { mX ->
            mX.injDim()?.let { it <= n } ?: false && (1..n).all { i -> ext(mX, mX, i) == 0 }
        }
        // Indec modules with id <= n and self-orthogonal.
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && (1..n).all { i ->
                    ext(mX, mY, i) == 0 && ext(mY, mX, i) == 0
                }
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns the list of generalized (Miyashita) tilting modules
     * with finite projective dimension.
     *
     * Algorithm: they are precisely self-orthogonal modules with finite proj. dim.
     * which are maximal with respect to this property.
     */
    fun generalizedTiltings(): List<List<Indec<T>>> {
        val nodes = indecs.filter { mX ->
            mX.projDim() != null && higherExtZero(mX, mX)
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && higherExtZero(mX, mY) && higherExtZero(mY, mX)
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns the list of generalized (Miyashita) cotilting modules
     * with finite injective dimension.
     *
     * Algorithm: they are precisely self-orthogonal modules with finite inj. dim.
     * which are maximal with respect to this property.
     */
    fun generalizedCotiltings(): List<List<Indec<T>>> {
        val nodes = indecs.filter { mX ->
            mX.injDim() != null && higherExtZero(mX, mX)
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && higherExtZero(mX, mY) && higherExtZero(mY, mX)
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns the list of exceptional modules:
     * a module M which is sel-orthogonal and
     * has finite projective dimension.
     */
    fun exceptionals(): List<List<Indec<T>>> {
        val nodes = indecs.filter { mX ->
            mX.projDim() != null && higherExtZero(mX, mX)
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && higherExtZero(mX, mY) && higherExtZero(mY, mX)
            }
        }
        return cliques(neighbor)
    }

    /**
     * Returns the list of [n]-cluster tilting modules.
     *
     * mXX is [n]-cluster tilting if
     * - Ext^[1,n)(mXX, mXX) = 0
     * - Ext^[1,n)(mXX, mY) = 0 implies mY in add mXX.
     * - Ext^[1,n)(mY, mXX) = 0 implies mY in add mXX.
     *
     * Algorithm:
     * - First, find maximal Ext^[1,n)-orthogonal modules containing projs and injs.
     * - Then check whether each satisfies the above conditions.
     */
    fun clusterTiltings(n: Int): List<List<Indec<T>>> {
        require(n >= 1) {
            "n must be >= 1."
        }
        fun vanish(mX: Indec<T>, mY: Indec<T>): Boolean {
            return (1 until n).all { i -> ext(mX, mY, i) == 0 }
        }
        // Since n-CT module must contain all projs and injs,
        // we reduce possible indecomposable summands.
        val nodes = indecs.filter { mX ->
            vanish(mX, mX) && projs().all { vanish(mX, it) } && injs().all { vanish(it, mX) }
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mY !== mX && vanish(mX, mY) && vanish(mY, mX)
            }
        }
        val maximals = maximalCliques(neighbor)
        val result = mutableListOf<List<Indec<T>>>()
        // Now [maximals] is a maximal [1, n)-orthogonal modules.
        for (candidate in maximals) {
            // Check whether [candidate] is n-CT or not.
            var isCT = true
            for (mY in indecs) {
                if (mY in candidate) continue
                // Now [mY] is not in [candidate], so to be n-CT,
                // ext should not vanish.
                if (candidate.all { mX -> vanish(mX, mY) } || candidate.all { mX -> vanish(mY, mX) }) {
                    isCT = false
                    break
                }
            }
            if (isCT) result.add(candidate)
        }
        return result
    }

    fun gorensteinProjs(): List<Indec<T>> {
        return indecs.filter { it.isGorensteinProj() }
    }

    fun semiGorensteinProjs(): List<Indec<T>> {
        return indecs.filter { it.isSemiGorensteinProj() }
    }

    fun infiniteTorsionless(): List<Indec<T>> {
        return indecs.filter { it.isInfiniteTorsionless() }
    }

    fun nTorsionLess(n: Int = 1): List<Indec<T>> {
        return indecs.filter { it.isTorsionLess(n) }
    }

    fun reflexives(): List<Indec<T>> {
        return indecs.filter { it.isReflexive() }
    }

    /**
     * Returns the list of Wakamatsu tilting modules.
     * Since the algebra is representation-finite, these are precisely
     * maximal self-orthogonal modules.
     */
    fun wakamatsuTiltings(): List<List<Indec<T>>> {
        val nodes = indecs.filter { mX ->
            higherExtZero(mX, mX)
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && higherExtZero(mX, mY) && higherExtZero(mY, mX)
            }
        }
        return maximalCliques(neighbor)
    }

    /**
     * Returns the list of self-orthogonal modules,
     * that is, a module M with Ext^i(M, M) = 0
     * for all i > 0.
     */
    fun selfOrthogonals(): List<List<Indec<T>>> {
        val nodes = indecs.filter { mX ->
            higherExtZero(mX, mX)
        }
        val neighbor = nodes.associateWith { mX ->
            nodes.filter { mY ->
                mX !== mY && higherExtZero(mX, mY) && higherExtZero(mY, mX)
            }
        }
        return cliques(neighbor)
    }

    fun wakamatsuTiltingsWithLeq(): ListWithLeq<List<Indec<T>>> {
        val wTiltings = wakamatsuTiltings()
        val leqs = wTiltings.flatMap { mTT1 ->
            wTiltings.filter { mTT2 -> higherExtZero(mTT1, mTT2) }.map { mTT2 -> mTT2 to mTT1 }
        }.toSet()
        return ListWithLeq(wTiltings, leqs, alwaysPoset = false)
    }

    fun generalizedTiltingPoset(): ListWithLeq<List<Indec<T>>> {
        val tiltings = generalizedTiltings()
        val leqs = tiltings.flatMap { mTT1 ->
            tiltings.filter { mTT2 -> higherExtZero(mTT1, mTT2) }.map { mTT2 -> mTT2 to mTT1 }
        }.toSet()
        return ListWithLeq(tiltings, leqs)
    }

    fun wakamatsuTiltingCheck(): List<Pair<List<Indec<T>>, List<Indec<T>>>> {
        val elements = wakamatsuTiltings()
        val result = mutableListOf<Pair<List<Indec<T>>, List<Indec<T>>>>()
        for (mTT1 in elements) {
            for (mTT2 in elements) if (higherExtZero(mTT1, mTT2)) {
                // Ext^{>0} (mTT1, mTT2) = 0 ->
                // mTT1 >= mTT2.
                // Check whether (mTT1)^\perp >= (mTT2)^\perp
                val cC1 = indecs.filter { higherExtZero(mTT1, it) }
                val cC2 = indecs.filter { higherExtZero(mTT2, it) }
                if (!cC1.containsAll(cC2)) {
                    println(mTT1 to mTT2)
                    println(cC1)
                    println(cC2)
                    result.add(mTT1 to mTT2)
                }
            }
        }
        return result
    }

    fun extRightPerp(mX: Indec<T>?): List<Indec<T>> {
        return indecs.filter { higherExtZero(mX, it) }
    }

    fun extRightPerp(mXX: Collection<Indec<T>?>): List<Indec<T>> {
        return indecs.filter { higherExtZero(mXX, it) }
    }

    fun extLeftPerp(mX: Indec<T>?): List<Indec<T>> {
        return indecs.filter { higherExtZero(it, mX) }
    }

    fun extLeftPerp(mXX: Collection<Indec<T>?>): List<Indec<T>> {
        return indecs.filter { higherExtZero(it, mXX) }
    }

    fun extOneRightPerp(mX: Indec<T>?): List<Indec<T>> {
        return indecs.filter { ext(mX, it) == 0 }
    }

    fun extOneRightPerp(mXX: Collection<Indec<T>?>): List<Indec<T>> {
        return indecs.filter { ext(mXX, it) == 0 }
    }

    fun extOneLeftPerp(mX: Indec<T>?): List<Indec<T>> {
        return indecs.filter { ext(it, mX) == 0 }
    }

    fun extOneLeftPerp(mXX: Collection<Indec<T>?>): List<Indec<T>> {
        return indecs.filter { ext(it, mXX) == 0 }
    }

    fun twoSMCs(): List<Pair<List<Indec<T>>, List<Indec<T>>>> {
        val sbrickToTorfBricks = _semibricks.associateWith { torsionFreeClosureBricks(it) }
        val torfBricksToSbrick = sbrickToTorfBricks.entries.associate { (key, value) -> value to key }
        return _semibricks.map { it to torfBricksToSbrick[homRightPerpBricks(it)]!! }
    }

    fun torsionPairs(): List<Pair<Subcat<T>, Subcat<T>>> {
        return _semibricks.map { torsionClosure(it) to homRightPerp(it) }
    }

    fun semibrickPairsFullRank(): List<Pair<List<Indec<T>>, List<Indec<T>>>> {
        val result = mutableListOf<Pair<List<Indec<T>>, List<Indec<T>>>>()
        for (mX in _semibricks) {
            for (mY in _semibricks) {
                if (mX.size + mY.size == rank() && hom(mX, mY) == 0 && ext(mX, mY) == 0) {
                    result.add(mX to mY)
                }
            }
        }
        return result
    }

    fun semibrickPairsMaximal(): List<Pair<List<Indec<T>>, List<Indec<T>>>> {
        val nodes: List<Pair<Indec<T>, Int>> = _bricks.flatMap { listOf(it to 0, it to 1) }
        val neighbor = nodes.associateWith { (mX, degreeX) ->
            nodes.filter { (mY, degreeY) ->
                when (degreeX to degreeY) {
                    0 to 0 -> hom(mX, mY) == 0 && hom(mY, mX) == 0
                    1 to 1 -> hom(mX, mY) == 0 && hom(mY, mX) == 0
                    0 to 1 -> hom(mX, mY) == 0 && ext(mX, mY) == 0
                    1 to 0 -> hom(mY, mX) == 0 && ext(mY, mX) == 0
                    else -> throw IllegalStateException()
                }
            }
        }
        val result = maximalCliques(neighbor)
        return result.map { list ->
            Pair(list.filter { it.second == 0 }.map { it.first },
                list.filter { it.second == 1 }.map { it.first })
        }
    }

    fun cotorsionPairs(): List<Pair<Subcat<T>, Subcat<T>>> {
        val cotorsionFrees = indecs.powerSetList(include = projs()).map { extOneRightPerp(it) }.distinct()
        return cotorsionFrees.map { cYY -> extOneLeftPerp(cYY) to cYY }
    }

    fun hereditaryCotorsionPairs(): List<Pair<Subcat<T>, Subcat<T>>> {
        return cotorsionPairs().filter { higherExtZero(it.first, it.second) }
    }

    fun resolvingSubcats(): List<Subcat<T>> {
        return hereditaryCotorsionPairs().map { it.first }
    }

    fun coresolvingSubcats(): List<Subcat<T>> {
        return hereditaryCotorsionPairs().map { it.second }
    }
}


