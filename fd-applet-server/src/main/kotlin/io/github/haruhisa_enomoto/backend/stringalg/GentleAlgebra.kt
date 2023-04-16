package io.github.haruhisa_enomoto.backend.stringalg

import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Quiver

/**
 * A class for gentle algebras.
 * @throws IllegalArgumentException if the algebra is not a gentle algebra.
 */
class GentleAlgebra<T, U>(
    quiver: Quiver<T, U>, relations: List<Monomial<T, U>>
) : StringAlgebra<T, U>(quiver, relations) {
    init {
        for (rel in relations) {
            require(rel.length == 2) {
                "The length of each relation should be 2."
            }
        }
        for (ar in arrows) {
            require(arrows.filter { it.from == ar.to }.map { ar * it }
                .filter { !isLegal(it) }.size <= 1) { "Too many relations. Both two paths with length 2 beginning with $ar do vanish." }
            require(arrows.filter { it.to == ar.from }.map { it * ar }
                .filter { !isLegal(it) }.size <= 1) { "Too many relations. Both two paths with length 2 ending with $ar do vanish." }
        }
    }

    constructor(algebra: MonomialAlgebra<T, U>) : this(algebra.quiver, algebra.relations)
}