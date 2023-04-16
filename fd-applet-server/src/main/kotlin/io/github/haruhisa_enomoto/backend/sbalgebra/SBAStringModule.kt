package io.github.haruhisa_enomoto.backend.sbalgebra
//
//import basic.Module
//import graph.*
//import stringalg.StringModule
//
//class SBAStringModule<T, U>(
//    override val algebra: SBAlgebra<T, U>, val word: Word<T, U>
//) : Module<T>() {
//    val reduced = StringModule.from(algebra.reduction, word)
//
//    override fun dim() = reduced.dim()
//
//    override fun topVertices(): List<T> = reduced.topVertices()
//
//    override fun socleVertices(): List<T> = reduced.socleVertices()
//
//    override fun vertexList(): List<T> = reduced.vertexList()
//
//    override fun radical(): List<Module<T>> {
//        return reduced.radical().map { SBAStringModule(algebra, it.word) }
//    }
//
//    override fun coradical(): List<Module<T>> {
//        return reduced.coradical().map { SBAStringModule(algebra, it.word) }
//    }
//
//    override fun isProjective(): Boolean {
//        if (topVertices().size != 1) return false
//        if (topVertices()[0] in algebra.biserialProjVertices) return false
//        return reduced.isProjective()
//    }
//
//    override fun isInjective(): Boolean {
//        if (socleVertices().size != 1) return false
//        if (socleVertices()[0] in algebra.biserialInjVertices) return false
//        return reduced.isInjective()
//    }
//
//    override fun syzygy(): List<Module<T>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun cosyzygy(): List<Module<T>> {
//        TODO("Not yet implemented")
//    }
//
//    fun isPinCoradical(): Boolean {
//        val tops = topVertices()
//        return (tops.size == 1 && tops[0] in algebra.biserialProjVertices && dim() + 1 == algebra.projAt(tops[0]).dim())
//    }
//
//    fun isPinRadical(): Boolean {
//        val socles = socleVertices()
//        return (socles.size == 1 && socles[0] in algebra.biserialInjVertices && dim() + 1 == algebra.injAt(socles[0]).dim())
//    }
//
//    override fun sinkSequence(): Pair<List<Module<T>>, Module<T>?> {
//        return if (!isPinCoradical()) {
//            val redSeq = reduced.sinkSequence()
//            redSeq.first.map { SBAStringModule(algebra, it.word) } to redSeq.second?.let {
//                SBAStringModule(
//                    algebra, it.word
//                )
//            }
//        } else {// Now `this` is P/soc P for biserial proj-inj [proj].
//            // thus 0 -> rad P -> (rad P)/(soc P) + P -> P/soc P -> 0
//            // is AR sequence.
//            val proj = algebra.projAt(topVertices()[0])
//            val rad = proj.radical()[0]
//            (rad.coradical() + proj) to rad
//        }
//    }
//
//    override fun sourceSequence(): Pair<List<Module<T>>, Module<T>?> {
//        return if (!isPinRadical()) {
//            val redSeq = reduced.sourceSequence()
//            redSeq.first.map { SBAStringModule(algebra, it.word) } to redSeq.second?.let {
//                SBAStringModule(
//                    algebra, it.word
//                )
//            }
//        } else {// Now `this` is rad P for biserial proj-inj [proj].
//            // thus 0 -> rad P -> (rad P)/(soc P) + P -> P/soc P -> 0
//            // is AR sequence.
//            val proj = algebra.projAt(topVertices()[0])
//            val corad = proj.coradical()[0]
//            (corad.radical() + proj) to corad
//        }
//    }
//
//
//    override fun isIsomorphic(other: Module<T>): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override fun injStableHom(other: Module<T>?): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun stableHom(other: Module<T>?): Int {
//        TODO("Not yet implemented")
//    }
//
//    override fun hom(other: Module<T>?): Int {
//        TODO("Not yet implemented")
//    }
//}