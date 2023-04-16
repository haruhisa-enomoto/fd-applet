package io.github.haruhisa_enomoto.server.app

import io.github.haruhisa_enomoto.backend.quiver.Arrow
import io.github.haruhisa_enomoto.backend.quiver.Quiver
import io.github.haruhisa_enomoto.backend.stringalg.MonomialAlgebra

val initialAlgebra = MonomialAlgebra(
    Quiver(
        listOf("1", "2", "3"),
        listOf(
            Arrow("a", "1", "2"),
            Arrow("b", "2", "3")
        )),
    listOf()).make()