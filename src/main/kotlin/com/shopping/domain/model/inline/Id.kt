package com.shopping.domain.model.inline

import com.shopping.AuthenticationError
import com.shopping.Errors
import java.util.*

inline class Id(val value: String) {

    companion object {

        fun generate() = Id(UUID.randomUUID().toString())

        fun create(id: String): Result<Id> =
            try {
                val uuid = UUID.fromString(id)
                Result.success(Id(uuid.toString()))
            } catch (e: Throwable) {
                Result.failure(Throwable(Errors.INVALID_ID))
            }

    }

    override fun toString(): String = value

}

fun String.validateId() = Id.create(this).getOrElse {
    throw AuthenticationError(Errors.INVALID_ID)
}