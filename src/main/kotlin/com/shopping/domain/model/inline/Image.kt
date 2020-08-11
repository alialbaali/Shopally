package com.shopping.domain.model.inline

import com.shopping.Errors

inline class Image(val value: String) {

    companion object {

        val DEFAULT = Image("IMAGE URL")

        fun create(url: String): Result<Image> =
            if (url.isBlank())
                Result.success(Image(url))
            else
                Result.failure(Throwable(Errors.INVALID_IMAGE))

    }

}