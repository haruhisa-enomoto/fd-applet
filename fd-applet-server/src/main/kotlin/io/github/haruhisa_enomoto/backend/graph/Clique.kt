package io.github.haruhisa_enomoto.backend.graph

/**
 * Yields all maximal cliques using the Bron-Kerbosch algorithm.
 * Assumes a simple graph: no loop and no multiple edges.
 *
 * @param T the type of vertices of a graph.
 * @param neighbor the map (dictionary) of neighbors of each vertex.
 * @return a sequence of all maximal cliques.
 */
fun <T> maximalCliqueSequence(neighbor: Map<T, List<T>>): Sequence<List<T>>  = sequence {
    fun doBronKerbosch(
        clique: List<T>,
        candidates: MutableList<T>,
        excluded: MutableList<T>
    ): Sequence<List<T>> = sequence {
        if (candidates.isEmpty() && excluded.isEmpty()) {
            yield(clique)
        }
        if (candidates.isNotEmpty()) {
            val pivot = candidates.first()
            val search = candidates.filter { it !in neighbor[pivot]!! }
            for (v in search) {
                val newCandidates = candidates.filter { it in neighbor[v]!! }
                val newExcluded = excluded.filter { it in neighbor[v]!! }
                yieldAll(
                    doBronKerbosch(
                        clique + listOf(v),
                        newCandidates.toMutableList(),
                        newExcluded.toMutableList()
                    )
                )
                candidates.remove(v)
                excluded.add(v)
            }
        }
    }

    yieldAll(doBronKerbosch(emptyList(), neighbor.keys.toMutableList(), mutableListOf()).toList())
}


/**
 * Returns all maximal cliques using the Bron-Kerbosch algorithm.
 * Assumes a simple graph: no loop and no multiple edges.
 *
 * @param T the type of vertices of a graph.
 * @param neighbor the map (dictionary) of neighbors of each vertex.
 * @return a list of all maximal cliques.
 */
fun <T> maximalCliques(neighbor: Map<T, List<T>>): List<List<T>> {
    return maximalCliqueSequence(neighbor).toList()
}

/**
 * Yields all (not necessarily maximal) cliques using the Bron-Kerbosch algorithm.
 * Assumes a simple graph: no loop and no multiple edges.
 *
 * @param T the type of vertices of a graph.
 * @param neighbor the map (dictionary) of neighbors of each vertex.
 * @return a sequence of all cliques.
 */
fun <T> cliqueSequence(neighbor: Map<T, List<T>>): Sequence<List<T>> = sequence {
    fun doBronKerboschModified(
        clique: List<T>,
        candidates: MutableList<T>,
        excluded: MutableList<T>
    ): Sequence<List<T>> = sequence {
        yield(clique)
        if (candidates.isNotEmpty()) {
            for (v in candidates.toList()) {
                val newCandidates = candidates.filter { it in neighbor[v]!! }
                val newExcluded = excluded.filter { it in neighbor[v]!! }
                yieldAll(
                    doBronKerboschModified(
                        clique + listOf(v),
                        newCandidates.toMutableList(),
                        newExcluded.toMutableList()
                    )
                )
                candidates.remove(v)
                excluded.add(v)
            }
        }
    }

    yieldAll(doBronKerboschModified(emptyList(), neighbor.keys.toMutableList(), mutableListOf()).toList())
}

/**
 * Returns all (not necessarily maximal) cliques using the Bron-Kerbosch algorithm.
 * Assumes a simple graph: no loop and no multiple edges.
 *
 * @param T the type of vertices of a graph.
 * @param neighbor the map (dictionary) of neighbors of each vertex.
 * @return a list of all cliques.
 */
fun <T> cliques(neighbor: Map<T, List<T>>): List<List<T>> {
    return cliqueSequence(neighbor).toList()
}

/**
 * Assumes that the cardinality of every maximal clique is [rank].
 * Reports all cliques `clique` of rank [rank]-1,
 * and the list of vertices `list` which makes `clique` maximal.
 *
 * @param neighbor the map (dictionary) of neighbors of each vertex.
 * @param rank the cardinality of all maximal cliques.
 * @return a sequence of `Pair(clique, list)`.
 */
fun <T> almostMaximalCliques(
    neighbor: Map<T, List<T>>,
    rank: Int
): Sequence<Pair<List<T>, List<T>>> {
    fun doBronKerboschForAlmostComplete(
        clique: List<T>,
        candidates: MutableList<T>,
        excluded: MutableList<T>
    ): Sequence<Pair<List<T>, List<T>>> = sequence {
        if (clique.size == rank - 1) {
            yield(Pair(clique, candidates + excluded))
        } else if (candidates.isNotEmpty()) {
            for (v in candidates.toList()) {
                val newCandidates = candidates.filter { it in neighbor[v]!! }
                val newExcluded = excluded.filter { it in neighbor[v]!! }
                yieldAll(
                    doBronKerboschForAlmostComplete(
                        clique + listOf(v),
                        newCandidates.toMutableList(),
                        newExcluded.toMutableList()
                    )
                )
                candidates.remove(v)
                excluded.add(v)
            }
        }
    }

    return doBronKerboschForAlmostComplete(emptyList(), neighbor.keys.toMutableList(), mutableListOf())
}

//
//// For tests.
//fun main() {
//    val myGraph = mapOf(
//        "A" to listOf("B", "C", "E"),
//        "B" to listOf("A", "C", "D", "F"),
//        "C" to listOf("A", "B", "D", "F"),
//        "D" to listOf("B", "C", "E", "F"),
//        "E" to listOf("A", "D"),
//        "F" to listOf("B", "C", "D")
//    )
//    maximalCliques(myGraph).forEach { println(it) }
//    println("All cliques:")
//    cliques(myGraph).forEach { println(it) }
//}
//
