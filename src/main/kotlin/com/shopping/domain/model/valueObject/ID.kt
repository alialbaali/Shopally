package com.shopping.domain.model.valueObject

import com.shopping.AuthorizationError
import com.shopping.Errors
import java.util.*

inline class ID(private val value: String) {

    companion object {

        fun random() = ID(UUID.randomUUID().toString())

        fun from(id: String): Result<ID> =
            try {
                val uuid = UUID.fromString(id)
                Result.success(ID(uuid.toString()))
            } catch (e: Throwable) {
                Result.failure(Throwable(Errors.INVALID_ID))
            }

    }

    override fun toString(): String = value

}

fun String.asID(): ID = ID.from(this).getOrElse {
    throw AuthorizationError(Errors.INVALID_ID)
}