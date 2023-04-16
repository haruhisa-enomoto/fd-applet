package io.github.haruhisa_enomoto.makeNakayamaDataset

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets

/**
 * gl.dim: finite -> label: 0
 * gl.dim: infinite -> label: 1
 */
@Serializable
data class AlgebraData(val sequence: List<Int>, val label: Int)

fun main() {
    println("Hello World!")

    val rank = readln().toInt()

    val outputFile = File("NakayamaFinGlDimDataset${rank}.json")
    FileOutputStream(outputFile).use { fos ->
        OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
            val json = Json
            for (sequence in kupischGenerator(rank, minBound = rank, firstMinimal = false)) {
                val label =
                    if (sequence.min() > rank) 1 else if (kupischToNakayama(sequence).globalDim() == null) 1 else 0
                println("$sequence, $label")
                val data = AlgebraData(sequence, label)
                val jsonString = json.encodeToString(data)
                writer.write(jsonString)
                writer.write("\n")
            }
            writer.flush()
        }
    }
}

