package com.songk.upload.domain.model

import com.songk.upload.domain.exception.FileSizeExceededException
import com.songk.upload.domain.exception.InvalidFileTypeException
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
) {
    companion object {
        private const val MAX_FILE_SIZE = 50L * 1024 * 1024
        private val ALLOWED_CONTENT_TYPES = setOf("application/pdf", "text/plain")

        fun validate(fileSize: Long, contentType: String) {
            if (fileSize > MAX_FILE_SIZE)
                throw FileSizeExceededException("File size exceeds maximum allowed size of 50MB")
            val baseType = contentType.substringBefore(';').trim()
            if (!baseType.startsWith("image/") && baseType !in ALLOWED_CONTENT_TYPES)
                throw InvalidFileTypeException("File type '$contentType' is not allowed")
        }
    }
}
