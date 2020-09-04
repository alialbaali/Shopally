package com.shopping

import com.cloudinary.Cloudinary
import com.shopping.domain.CloudDataSource
import java.io.File

private const val PUBLIC_ID = "public_id"
private const val FOLDER = "folder"
private const val RESULT = "result"
private const val SECURE_URL = "secure_url"
private const val OK = "ok"

class CloudinaryDataSource(private val cloudinary: Cloudinary) : CloudDataSource {

    override fun uploadImage(
        file: File,
        imageId: String?,
        folderName: String?,
        options: Map<String, String>
    ): Result<String> = runCatching {

        val response = cloudinary.uploader().upload(
            file,
            options.plus(
                mapOf(
                    PUBLIC_ID to imageId,
                    FOLDER to folderName,
                )
            )
        )

        val imageUrl = response[SECURE_URL] as String? ?: return Result.failure(Throwable())

        imageUrl
    }

    override fun deleteImage(imageId: String, options: Map<String, String>): Result<Unit> = runCatching {

        val response = cloudinary.uploader().destroy(imageId, options)

        val result = response[RESULT] as String? ?: return Result.failure(Throwable())

        return if (result.equals(OK, ignoreCase = true))
            Result.success(Unit)
        else
            Result.failure(Throwable())
    }
}
