package io.github.haruhisa_enomoto.makeNakayamaDataset

import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Monomial
import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.quiver.toMonomial
import io.github.haruhisa_enomoto.backend.stringalg.MonomialAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringAlgebra

fun kupischGenerator(
    n: Int, bound: Int? = null, minBound: Int? = null, firstMinimal: Boolean = true
): Sequence<List<Int>> = sequence {
    val queue = ArrayDeque<List<Int>>()
    val initial = List(n) { 2 }
    queue.add(initial)
    val yielded = mutableSetOf<List<Int>>()
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (current in yielded) continue
        yield(current)
        yielded.add(current)
        for (i in 0 until n) {
            if (i == 0 && (firstMinimal && current[0] == current.subList(1, n).min())) continue
            val next = current.toMutableList()
            val left = if (i != 0) current[i - 1] else current.last()
            val right = if (i != n - 1) current[i + 1] else current.first()
            val middleTry = current[i] + 1
            if (bound != null && middleTry > bound) continue
            if (left > middleTry + 1 || middleTry > right + 1) continue
            next[i] = middleTry
            if (minBound != null && next.min() > minBound) continue
            queue.add(next)
        }
    }
}

fun kupischToNakayama(series: List<Int>): StringAlgebra<String, Int> {
    val rank = series.size
    val vertices = (1..rank).map { "v$it" }
    val arrows = (1..rank).map { Arrow(it, "v$it", if (it != rank) "v${it + 1}" else "v1") }
    val rels = mutableListOf<Monomial<String, Int>>()
    for ((index, length) in series.withIndex()) {
        val arrowList = (0 until length).map { arrows[(index + it) % rank] }
        rels.add(arrowList.toMonomial())
    }
    return StringAlgebra(MonomialAlgebra(Quiver(vertices, arrows), rels))
}

fun test() {
    println("Hello World!")

    val rank = 4
    val bound = 20
    for (series in kupischGenerator(rank, bound)) {
        val algebra = kupischToNakayama(series)
//        println(algebra.relations)
        val rfAlgebra = algebra.toRFAlgebra()!!
        if (rfAlgebra.isIG()) continue
//        val wak = rfAlgebra.wakamatsuTiltingProset()
//        val pureWak = wak.elements.filter {
//            rfAlgebra.projDim(it) == null &&
//                    rfAlgebra.injDim(it) == null
//        }
////        if (pureWak.isEmpty()) continue
//        val nonTiltingWak = wak.elements.filter { rfAlgebra.projDim(it) == null }
//        val nonCotiltingWak = wak.elements.filter { rfAlgebra.injDim(it) == null }
//        if (nonTiltingWak.isEmpty() || nonCotiltingWak.isEmpty()) continue
        val counterEx = rfAlgebra.wakamatsuTiltingCheck()
        if (counterEx.isNotEmpty()) {
            println(series)
            println("CounterExample!")
            for (ex in counterEx) println(ex)
        }
//        if (wak.isPoset()) {
////            println("It's a poset.")
//        } else {
//            println("There are ${wak.size} Wakamatsu tiltings.")
//            println("pure: ${pureWak.size}, non-tilting: ${nonTiltingWak.size}, non-cotilting: ${nonCotiltingWak.size},")
//            println("NOT POSET!!!")
//        }
    }

}

