package io.github.haruhisa_enomoto.backend.types

import io.github.haruhisa_enomoto.backend.algebra.Indec

typealias Subcat<T> = Collection<Indec<T>>

fun <T> Subcat<T>.toSerializable(): List<String> {
    return this.map { it.toString() }
}
