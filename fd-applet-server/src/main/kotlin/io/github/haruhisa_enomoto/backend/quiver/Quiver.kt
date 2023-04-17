package io.github.haruhisa_enomoto.backend.quiver

import kotlinx.serialization.Serializable

/**
 * Type generics convention.
 * - `T`: a type for vertex labels.
 * - `U`: a type for arrow labels.
 */


/**
 * A data class representing a finite quiver with the list of [vertices] and [arrows].
 *
 * @param T the type of vertex labels.
 * @param U the type of arrow labels.
 * @property vertices the list of vertices of the quiver.
 * @property arrows the list of arrows of the quiver.
 * @throws IllegalArgumentException if some vertices in [arrows] are not in [vertices],
 * or if some letters in [arrows] are inverses.
 */
@Serializable
data class Quiver<T, U>(val vertices: Collection<T>, val arrows: Collection<Arrow<T, U>>) {
    init {
        for (arrow in arrows) {
            require(arrow.from in vertices) {
                "Source of $arrow: ${arrow.from} is not a vertex."
            }
            require(arrow.to in vertices) {
                "Target of $arrow: ${arrow.to} is not a vertex."
            }
        }
    }

    /**
     * Prints the quiver information, including its vertices and arrows.
     */
    fun printInfo() {
        println("Vertices:")
        println(vertices)
        println("Arrows:")
        for (ar in arrows) {
            println(ar.infoString())
        }
    }

    /**
     * Returns the arrow with the given [arrowLabel].
     *
     * @param arrowLabel the label of the arrow to search for.
     * @return the arrow with the given label.
     * @throws IllegalArgumentException if the arrow with the given label does not exist.
     */
    fun arrowOfLabel(arrowLabel: U): Arrow<T, U> {
        return arrows.firstOrNull { it.label == arrowLabel }
            ?: throw IllegalArgumentException("Arrow of label $arrowLabel doesn't exist.")
    }

    /**
     * Recursive depth-first search for finding all paths beginning from [currentPath].
     *
     * @param currentPath the current path in the depth-first search.
     * @return a sequence of all paths starting from the current path.
     */
    private fun pathDFS(currentPath: Word<T, U>): Sequence<Word<T, U>> = sequence {
        yield(currentPath)
        for (arrow in arrows.filter { currentPath.to == it.from }) {
            yieldAll(pathDFS(currentPath * arrow))
        }
    }

    /**
     * Returns a (possibly infinite) sequence of paths starting at [vtx].
     * Depth-first search is used for faster acyclicity check.
     *
     * @param vtx the starting vertex of the paths.
     * @return a sequence of paths starting at the given vertex.
     */
    private fun pathsSequenceFrom(vtx: T): Sequence<Word<T, U>> {
        return pathDFS(vtx.toTrivialWord())
    }

    /**
     * Recursive depth-first search for generating all cycles beginning with [currentPath].
     * If a path ending at some vertex in [visited] is obtained, the recursion stops.
     *
     * @param currentPath the current path in the depth-first search.
     * @param visited a list of visited vertices.
     * @return a sequence of all cycles starting from the current path.
     */
    private fun cycleDFS(currentPath: Word<T, U>, visited: List<T>): Sequence<Word<T, U>> = sequence {
        // Node `path` created
        // It's checked, do nothing, and suppose it's not checked.
        if (currentPath.to !in visited) {
            val vertexList = currentPath.vertexList()
            val index = vertexList.indexOf(currentPath.to)
            if (index != vertexList.lastIndex) {// if not a simple path
                if (index == 0) {// if cycle
                    yield(currentPath)
                }
            } else {// if simple path, create children.
                for (arrow in arrows.filter { currentPath.to == it.from }) {
                    yieldAll(cycleDFS(currentPath * arrow, visited))
                }
            }
        }
    }

    /**
     * Returns all simple cycles in the quiver.
     * A cycle is considered "simple" if no vertex is visited more than once, except for the first and last vertices.
     * This method returns only one representative for each cycle:
     * For example, if [a, b, c] is a cycle, this method will not report [b, c, a] and [c, a, b].
     *
     * @return a sequence of simple cycles in the quiver.
     */
    fun simpleCycles(): Sequence<Word<T, U>> = sequence {
        /*
        Starting from an empty path of `vtx`, yield all cycles starting at `vtx`.
        Then mark `vtx` ar `checked`, and when starting from another vertex
        `vtx2`, if we encounter with `vtx`, it stops creating children,
        because if there's a cycle containing `vtx2` and `vtx`, then it should be
        already found in the search of `vtx`.
        This would avoid producing "equivalent" cycles.
        */
        val checked = mutableListOf<T>()
        for (vtx in vertices) {
            if (vtx !in checked) {
                yieldAll(cycleDFS(vtx.toTrivialWord(), checked))
                checked.add(vtx)
            }
        }
    }

    /**
     * Returns whether the quiver has a finite number of primitive cycles:
     * cycles that cannot be expressed as a power of smaller cycles.
     * This condition holds if and only if none of the simple cycles share common vertices.
     *
     * @return true if the quiver has a finite number of primitive cycles, false otherwise.
     */
    fun primitiveCycleFinite(): Boolean {
        val seen = mutableSetOf<T>()
        for (vtx in simpleCycles().map { it.support() }.flatten()) {
            val isNew = seen.add(vtx)
            if (!isNew) return false
        }
        return true
    }

    /**
     * Returns whether the quiver is acyclic. If [vtx] is provided, this method checks
     * if there are only a finite number of paths starting from [vtx].
     *
     * @param vtx the vertex to check for acyclicity (optional).
     * @return true if the quiver is acyclic or if the specified vertex has only finite paths, false otherwise.
     */
    fun isAcyclic(vtx: T? = null): Boolean {
        val checkList = if (vtx == null) vertices else listOf(vtx)
        for (vtx2 in checkList) {
            // Find a path which begins with `vtx2` and contains some cycle.
            val myPath = pathsSequenceFrom(vtx2).find { it.support().size != it.vertexList().size }
            if (myPath != null) return false
        }
        return true
    }

    /**
     * Retrieves all paths starting from the given vertex [vtx].
     *
     * @param vtx the vertex from which to obtain paths.
     * @return a list of all paths originating from the specified vertex.
     * @throws IllegalArgumentException if there are infinitely many paths.
     */
    fun pathsFrom(vtx: T): List<Word<T, U>> {
        require(isAcyclic(vtx)) { "There are infinitely many paths." }
        return pathsSequenceFrom(vtx).toList()
    }
}
