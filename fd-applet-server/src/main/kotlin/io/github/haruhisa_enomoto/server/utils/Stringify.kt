package io.github.haruhisa_enomoto.server.utils

import io.github.haruhisa_enomoto.backend.algebra.Indec

/**
 * Returns a comparator that first compares lists by their sizes, then lexicographically
 * if the sizes are equal.
 */
fun <T : Comparable<T>> getMyComparator(): Comparator<List<T>> {
    return Comparator { o1, o2 ->
        // Compare by list size first
        val lengthCmp = o1.size.compareTo(o2.size)
        if (lengthCmp != 0) return@Comparator lengthCmp

        // If sizes are equal, compare elements lexicographically
        o1.zip(o2) { a, b -> a.compareTo(b) }.firstOrNull { it != 0 } ?: 0
    }
}

/**
 * Extension function to sort a Collection<Collection<T>> lexicographically.
 * The function first sorts each sublist, then sorts all lists by their lengths.
 * If the lengths are equal, it sorts them lexicographically.
 *
 * @return Sorted list of lists.
 */
fun <T : Comparable<T>> Collection<Collection<T>>.mySorted(): List<List<T>> {
    // Sort the elements of the original lists first
    val sortedLists = this.map { it.sorted() }

    // Apply the comparator to sort the list of lists
    return sortedLists.sortedWith(getMyComparator())
}


fun <T> Collection<Indec<T>>.toListString(sort: Boolean = true): List<String> {
    return if (sort) {
        this.sorted().map { it.toString() }
    } else {
        this.map { it.toString() }
    }
}


fun <T> Collection<Collection<Indec<T>>>.toListListString(sort: Boolean = true): List<List<String>> {
    return if (sort) {
        this.mySorted().map { it.map { indec -> indec.toString() } }
    } else {
        this.map { it.map { indec -> indec.toString() } }
    }
}

fun <T> Collection<Pair<Collection<Indec<T>>, Collection<Indec<T>>>>.toListPairListString(sort: Boolean = true): List<Pair<List<String>, List<String>>> {
    if (!sort) {
        return this.map { (first, second) ->
            (first.toListString(false) to second.toListString(false))
        }
    }
    val innerSortedPairs = this.map { (first, second) ->
        (first.sorted() to second.sorted())
    }

    val sortedPairs = innerSortedPairs.sortedWith { p1, p2 ->
        getMyComparator<Indec<T>>().compare(p1.first, p2.first)
    }

    return sortedPairs.map { (first, second) ->
        (first.toListString(false) to second.toListString(false))
    }
}