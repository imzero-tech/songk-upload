package com.songk.upload.domain.port.out

interface FileStoragePort {
    suspend fun store(fileName: String, content: ByteArray, contentType: String): String
    suspend fun delete(storagePath: String)
}
