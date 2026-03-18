package com.songk.upload.adapter.`in`.web.dto

import com.songk.upload.domain.model.FileUpload
import java.time.LocalDateTime

data class FileUploadResponse(
    val id: Long?,
    val originalFileName: String,
    val fileSize: Long,
    val contentType: String,
    val status: String,
    val uploadedBy: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(fileUpload: FileUpload) = FileUploadResponse(
            id = fileUpload.id,
            originalFileName = fileUpload.originalFileName,
            fileSize = fileUpload.fileSize,
            contentType = fileUpload.contentType,
            status = fileUpload.status.name,
            uploadedBy = fileUpload.uploadedBy,
            createdAt = fileUpload.createdAt
        )
    }
}
