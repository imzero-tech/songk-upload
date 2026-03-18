package com.songk.upload.adapter.out.persistence

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface FileUploadR2dbcRepository : CoroutineCrudRepository<FileUploadEntity, Long> {
    fun findAllByUploadedBy(uploadedBy: String): Flow<FileUploadEntity>
    suspend fun findByIdAndUploadedBy(id: Long, uploadedBy: String): FileUploadEntity?
}
