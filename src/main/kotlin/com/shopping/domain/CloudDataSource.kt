package com.shopping.domain

import java.io.File

interface CloudDataSource {

    fun uploadImage(
        file: File,
        imageId: String? = null,
        folderName: String? = null,
        options: Map<String, String> = emptyMap()
    ): Result<String>

    fun deleteImage(imageId: String, options: Map<String, String> = emptyMap()): Result<Unit>
}
