package io.github.haruhisa_enomoto.server.storage

import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.types.Subcat
import io.github.haruhisa_enomoto.backend.types.TauTiltingData

data class AlgebraSessionState(
    var algebra: QuiverAlgebra<String, String>,
    var rfAlgebra: RfAlgebra<String>? = null,
    var subcatList: List<Subcat<String>> = listOf()) {

    fun clear(newAlgebra: QuiverAlgebra<String, String>) {
        algebra = newAlgebra
        rfAlgebra = null
        subcatList = listOf()
    }
}