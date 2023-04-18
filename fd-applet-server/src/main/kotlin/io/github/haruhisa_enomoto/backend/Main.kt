package io.github.haruhisa_enomoto.backend

import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.stringalg.StringAlgebra
import io.github.haruhisa_enomoto.backend.utils.toListWithLeq
import io.github.haruhisa_enomoto.server.utils.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    println("Hello World!")

    val algebraName = "A6"
    val filePath = "examples/$algebraName.json"

    val serialAlgebra: SerialBinomialAlgebra<String, String> = Json.decodeFromString(File(filePath).readText())
    val myAlg = serialAlgebra.deserialize().make()

    myAlg.printInfo()

    println("Dimensions.")

    if (myAlg.isFiniteDimensional()) {
        println("dim: ${myAlg.dim()}")
    } else {
        println("dim: infinity")
    }

    if (myAlg is StringAlgebra) {
        println("This is string algebra.")
    } else {
        println("This is not a string algebra.")
        return
    }

    println("Global dimension: ${myAlg.globalDim()}")
    println("Right self-inj dim: ${myAlg.rightSelfInjDim()}")
    println("Left self-inj dim: ${myAlg.leftSelfInjDim()}")


    val myRFAlg: RfAlgebra<String>
    if (myAlg.isRepFinite()) {
        myRFAlg = myAlg.toRfAlgebra()!!
        println("This is representation-finite with ${myRFAlg.indecs.size} indecomposables.")
        println("The number of brick is ${myRFAlg.bricks().size}.")
    } else {
        println("This is representation-infinite. Primitive bands:")
        println(myAlg.primitiveBands())
        if (myAlg.isBandFinite) {
            println("This is domestic.")
        } else {
            println("Non-domestic!")
        }
        return
    }
    println("Compute bands.")

    myAlg.primitiveBands()

    println("Compute AR quiver!")
    val myARquiver = myRFAlg.arQuiver()
    val outputFile = File("examples/results/$algebraName-ar.json")
//    myARquiver.toQuiver().printInfo()
    outputFile.writeText(myARquiver.toStringQuiver().toJsonString())

//
//    println("Compute semibricks!!")
//    val sbricks = myRFAlg.semibricks()
//    println("There are ${sbricks.size} semibricks.")
//    File("examples/$algebraName-sb.json").writeText(
//        sbricks.map { modules -> modules.map { it.toString() } }.toJsonString()
//    )

//    for (mX in myRFAlg.modules) {
//        for (mY in myRFAlg.modules) {
//            if (myRFAlg.stableHom(mX, mY) == myRFAlg.injStableHom(mX.tauPlus(), mY.tauPlus()))
////                println("OK!")
//            else throw IllegalStateException("Tau is not equivalence!!!")
//        }
//    }

//    println("Compute tau-tilting pairs!")
//    var time = measureTimeMillis {
//        val siltQuiver = myRFAlg.tauTiltingQuiver().toUnlabeledStringQuiver()
//        println("One construction")
//        println("Vertices: ${siltQuiver.vertices.size}, arrows: ${siltQuiver.arrows.size}")
////    myARquiver.toQuiver().printInfo()
//        File("examples/$algebraName-2-silt.json")
//            .writeText(siltQuiver.toJsonString())
//    }
//    println(time)

//    File("examples/$algebraName-2-silt-label.json")
//        .writeText(myRFAlg.tauTiltingQuiverWithBrickLabel().toStringQuiver().toJsonString())

//    for (pair in tauTiltPairs) {
//        println(pair)
//    }
//

//    println("Compute IE!")
//    val ies = myRFAlg.ieClosedSubcats()
//    println("There are ${ies.size} IEs!")

    Thread.sleep(10000)
    println("Compute ICE!")
    var time = measureTimeMillis {
        val ices = myRFAlg.iceClosedSubcats().toListWithLeq()
        println("There are ${ices.size} ICEs!")
        val hasse = ices.hasseQuiver()
        println("Hasse computed!")
    }
    println(time)


//    println("Test resol")
//    for (indec in myAlg.stringModules()) {
//        println(indec)
//        println(myAlg.projResolution(indec, 1))
//    }
//    File("examples/$algebraName-ie.json").writeText(ies.sortedBy { it.indecs.size }.map { it.toSerializable() }.toJsonString())


    println("Successfully finished!")


}