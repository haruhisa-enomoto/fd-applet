package io.github.haruhisa_enomoto.backend.quiver

/**
 * A data class representing translation quivers.
 * No labels for each arrow, meaning all the labels should be `null`.
 *
 * @param T the type of vertices (e.g. if AR quiver of module cat, it's the type of modules).
 * @property quiver the underlying quiver (without translations).
 * @property tau the map of translation.
 * @property vertices the vertices of the quiver.
 * @property tauMinus the inverse of [tau].
 * @property projectives the list of projective vertices.
 * @property injectives the list of injective vertices.
 *
 * @constructor Creates a translation quiver with the given [quiver] and [tau].
 * @throws IllegalArgumentException if the axiom of translation quiver is not satisfied.
 */
data class TranslationQuiver<T>(
    val quiver: Quiver<T, Nothing>, val tau: Map<T, T>
) {
    val vertices = quiver.vertices
    val tauMinus = tau.entries.associate { (key, value) -> value to key }
    val projectives = vertices.filter { it !in tau.keys }
    val injectives = vertices.filter { it !in tauMinus.keys }

    init {
        require(vertices.containsAll(tau.keys) && vertices.containsAll(tau.values)) {
            "tau should be defined on the vertex set of a quiver."
        }
        require(tau.size == tauMinus.size) {
            "tau should be one-to-one."
        }
        // Check the axiom of a translation quiver.
        for (vtx in tau.keys) {
            val toVtx = quiver.arrows.filter { it.to == vtx }.map { it.from }
            val fromTauVtx = quiver.arrows.filter { it.from == tau[vtx] }.map { it.to }
            require(toVtx.groupBy { it } == fromTauVtx.groupBy { it }) {
                "Arrows to $vtx and arrows from ${tau[vtx]} do not correspond."
            }
        }
    }

    /**
     * Prints information about the translation quiver.
     */
    fun printInfo() {
        println("Vertices:")
        println(vertices)
        println("Arrows:")
        for (ar in quiver.arrows) {
            println(ar.toLetter().infoString())
        }
        println("Translations:")
        for (vtx in tau.keys) {
            println(vtx.toString() + " --tau--> " + tau[vtx].toString())
        }
    }

    /**
     * Converts the translation quiver to the underlying quiver.
     *
     * @return the underlying quiver of the translation quiver.
     */
    fun toQuiver(): Quiver<T, Nothing> {
        return Quiver(quiver.vertices,
            quiver.arrows +
                    tau.entries.map { (key, value) -> Arrow(null, key, value, isTau = true) })
    }
}
