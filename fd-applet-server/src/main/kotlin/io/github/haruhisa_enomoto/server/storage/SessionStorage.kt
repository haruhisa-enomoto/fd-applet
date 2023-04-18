package io.github.haruhisa_enomoto.server.storage

import io.github.haruhisa_enomoto.backend.algebra.QuiverAlgebra
import io.github.haruhisa_enomoto.backend.algebra.RfAlgebra
import io.github.haruhisa_enomoto.backend.types.Subcat
import java.util.concurrent.ConcurrentHashMap

object SessionStorage {
    private val storage = ConcurrentHashMap<String, AlgebraSessionState>()

    fun getAlgebra(id: String): QuiverAlgebra<String, String>? = storage[id]?.algebra

    fun addAlgebra(id: String, algebra: QuiverAlgebra<String, String>) {
        storage[id] = AlgebraSessionState(algebra)
    }

    fun delete(id: String) {
        storage.remove(id)
    }

    fun createRfAlgebra(id: String) {
        val sessionState = storage[id]
        require(sessionState != null) {
            "Please click Update button."
        }
        if (sessionState.rfAlgebra == null) {
            sessionState.rfAlgebra = sessionState.algebra.toRfAlgebra()
        }
    }

    fun getRfAlgebra(id: String): RfAlgebra<String>? {
        val sessionState = storage[id] ?: return null
        return if (sessionState.rfAlgebra != null) {
            sessionState.rfAlgebra
        } else {
            sessionState.rfAlgebra = sessionState.algebra.toRfAlgebra()
            sessionState.rfAlgebra
        }
    }

    fun setSubcatList(id: String, list: List<Subcat<String>>) {
        val sessionState = storage[id]
        require(sessionState != null) {
            "Please click Update button."
        }
        sessionState.subcatList = list
    }

    fun getSubcatList(id: String): List<Subcat<String>> {
        val sessionState = storage[id]
        require(sessionState != null) {
            "Please click Update button."
        }
        return sessionState.subcatList
    }

    fun getIdList(): List<String> {
        return storage.keys.toList()
    }
}