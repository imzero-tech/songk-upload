package com.songk.upload.domain.port.out

import com.songk.upload.domain.model.FileUpload
import kotlinx.coroutines.flow.Flow

interface FileUploadRepository {
    suspend fun save(fileUpload: FileUpload): FileUpload
    fun findAllByUploadedBy(uploadedBy: String): Flow<FileUpload>
    suspend fun findByIdAndUploadedBy(id: Long, uploadedBy: String): FileUpload?
    suspend fun deleteById(id: Long)
}
