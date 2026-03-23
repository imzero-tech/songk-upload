package com.songk.upload.domain.port.out

import com.songk.upload.domain.model.StorageResult

interface FileStoragePort {
    suspend fun store(fileName: String, content: ByteArray, contentType: String): StorageResult
    suspend fun delete(storagePath: String)
}
