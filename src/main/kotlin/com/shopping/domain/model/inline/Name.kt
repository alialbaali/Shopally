package com.shopping.domain.model.inline

import com.shopping.Errors

inline class Name(val value: String) {

    companion object {

        fun create(name: String): Result<Name> =
            if (name.isNotBlank())
                Result.success(Name(name.trim()))
            else
                Result.failure(Throwable(Errors.INVALID_NAME))

    }

}