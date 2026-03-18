package com.songk.upload.adapter.out.persistence

import com.songk.upload.domain.model.FileUpload
import com.songk.upload.domain.model.FileUploadStatus
import com.songk.upload.domain.port.out.FileUploadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class FileUploadPersistenceAdapter(
    private val r2dbcRepository: FileUploadR2dbcRepository
) : FileUploadRepository {

    override suspend fun save(fileUpload: FileUpload): FileUpload =
        r2dbcRepository.save(fileUpload.toEntity()).toDomain()

    override fun findAllByUploadedBy(uploadedBy: String): Flow<FileUpload> =
        r2dbcRepository.findAllByUploadedBy(uploadedBy).map { it.toDomain() }

    override suspend fun findByIdAndUploadedBy(id: Long, uploadedBy: String): FileUpload? =
        r2dbcRepository.findByIdAndUploadedBy(id, uploadedBy)?.toDomain()

    override suspend fun deleteById(id: Long) =
        r2dbcRepository.deleteById(id)

    private fun FileUpload.toEntity() = FileUploadEntity(
        id = id,
        originalFileName = originalFileName,
        storedFileName = storedFileName,
        fileSize = fileSize,
        contentType = contentType,
        storagePath = storagePath,
        uploadedBy = uploadedBy,
        status = status.name,
        createdAt = createdAt
    )

    private fun FileUploadEntity.toDomain() = FileUpload(
        id = id,
        originalFileName = originalFileName,
        storedFileName = storedFileName,
        fileSize = fileSize,
        contentType = contentType,
        storagePath = storagePath,
        uploadedBy = uploadedBy,
        status = FileUploadStatus.valueOf(status),
        createdAt = createdAt ?: LocalDateTime.now()
    )
}
