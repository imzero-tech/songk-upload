package com.songk.upload.domain.port.`in`

import com.songk.upload.application.command.UploadCommand
import com.songk.upload.domain.model.FileUpload
import kotlinx.coroutines.flow.Flow

interface FileUploadUseCase {
    suspend fun upload(command: UploadCommand): FileUpload
    fun findAll(uploadedBy: String): Flow<FileUpload>
    suspend fun findById(id: Long, uploadedBy: String): FileUpload
    suspend fun delete(id: Long, uploadedBy: String)
}
