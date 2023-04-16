package io.github.haruhisa_enomoto.backend.types

import io.github.haruhisa_enomoto.backend.algebra.Indec
import kotlinx.serialization.Serializable
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
    val supportTauTilting: List<Indec<T>>,
    val support: List<T>,
    val torsionClass: Subcat<T>,
    val silting: Pair<List<T>, List<T>>,
    val semibrick: List<Indec<T>>,
    val wideSubcat: Subcat<T>,
    val supportTauMinusTilting: List<Indec<T>>,
    val support2: List<T>,
    val torsionFreeClass: Subcat<T>,
    val semibrick2: List<Indec<T>>,
    val cosilting: Pair<List<T>, List<T>>,
) {
    fun toSerializable(): SerialTauTiltingData {
        return SerialTauTiltingData(
            supportTauTilting = supportTauTilting.toSerializable(),
            support = support.map { it.toString() },
            torsionClass = torsionClass.toSerializable(),
            silting = silting.first.map { it.toString() } to silting.second.map { it.toString() },
            semibrick = semibrick.toSerializable(),
            wideSubcat = wideSubcat.toSerializable(),
            supportTauMinusTilting = supportTauMinusTilting.toSerializable(),
            support2 = support2.map { it.toString() },
            torsionFreeClass = torsionFreeClass.toSerializable(),
            semibrick2 = semibrick2.toSerializable(),
            cosilting = cosilting.first.map { it.toString() } to cosilting.second.map { it.toString() },
        )
    }
}

@Serializable
data class SerialTauTiltingData(
    val supportTauTilting: List<String>,
    val support: List<String>,
    val torsionClass: List<String>,
    val silting: Pair<List<String>, List<String>>,
    val semibrick: List<String>,
    val wideSubcat: List<String>,
    val supportTauMinusTilting: List<String>,
    val support2: List<String>,
    val torsionFreeClass: List<String>,
    val semibrick2: List<String>,
    val cosilting: Pair<List<String>, List<String>>,
)