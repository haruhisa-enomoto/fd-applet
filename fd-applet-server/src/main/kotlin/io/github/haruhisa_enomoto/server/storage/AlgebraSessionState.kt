package io.github.haruhisa_enomoto.server.storage

import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RFAlgebra
import io.github.haruhisa_enomoto.backend.types.Subcat

data class AlgebraSessionState(
    var algebra: QuiverAlgebra<String, String>,
    var rfAlgebra: RFAlgebra<String>? = null,
    var subcatList: List<Subcat<String>> = listOf()) {

    fun clear(newAlgebra: QuiverAlgebra<String, String>) {
        algebra = newAlgebra
        rfAlgebra = null
        subcatList = listOf()
    }
}