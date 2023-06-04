package io.github.haruhisa_enomoto.backend.types

import io.github.haruhisa_enomoto.backend.algebra.Indec
import java.util.*

/**
 * A type representing indecomposable tau-rigid pair:
 * either of the form (`mM`, `null`) for `mM` indec tau-rigid, or
 * (`null`, `vtx`) for a vertex `vtx` of the quiver
 * (use vertices instead of indecomposable projectives).
 */
typealias IndecTauRigidPair<T> = Pair<Indec<T>?, T?>

fun <T> Collection<IndecTauRigidPair<T>>.toTauTiltingPair(): ModuleWithSupport<T> {
    return ModuleWithSupport(
        this.mapNotNull { it.first }.toSortedSet(compareBy { it.toString() }),
        this.mapNotNull { it.second }.toSortedSet(
            compareBy
                ({ it.toString().toIntOrNull() ?: Int.MAX_VALUE },
                { it.toString() })
        )
    )
}

/**
 * A data class for pairs of modules and the sets of vertices.
 * Will be used for tau-tilting (tau-rigid) pairs and its inverses.
 *
 * @property mMM modules, represented by sorted set of indecomposable modules.
 * @property support the support part: the set of vertices.
 */
data class ModuleWithSupport<T>(
    val mMM: SortedSet<Indec<T>>, val support: SortedSet<T>
) {
    override fun toString(): String {
        return listOf(mMM, support).toString()
    }
}

data class TauTiltingData<T>(
    val torsionClass: IndecSet<T>,
    val torsionFreeClass: IndecSet<T>,
    val wideTors: IndecSet<T>,
    val wideTorf: IndecSet<T>,
    val supportTauTilting: IndecSet<T>,
    val supportTauMinusTilting: IndecSet<T>,
    val semibrickTors: IndecSet<T>,
    val semibrickTorf: IndecSet<T>,
)