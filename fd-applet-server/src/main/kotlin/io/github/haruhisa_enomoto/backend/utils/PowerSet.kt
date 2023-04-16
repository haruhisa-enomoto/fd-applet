package io.github.haruhisa_enomoto.backend.utils

/**
 * Returns the list of all subsets (as lists) of the list
 * including all elements in [include].
 * We do not check that [include] is a subset of the original list.
 */
fun <T> Collection<T>.powerSetList(include: Collection<T> = listOf()): List<List<T>> {
    return this.fold(listOf(include.toList())) { acc, elem ->
        if (elem in include) acc else acc + acc.map { it + elem }
    }
}

fun main() {
    val list = listOf(1, 2, 3, 4, 5)
    println(list.powerSetList(listOf(2, 4)))
}