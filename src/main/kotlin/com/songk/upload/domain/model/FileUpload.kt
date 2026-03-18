package com.songk.upload.domain.model

import java.time.LocalDateTime

data class FileUpload(
    val id: Long? = null,
    val originalFileName: String,
    val storedFileName: String,
    val fileSize: Long,
    val contentType: String,
    val storagePath: String,
    val uploadedBy: String,
    val status: FileUploadStatus = FileUploadStatus.PENDING,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
