package com.songk.upload.application.service

import com.songk.upload.application.command.UploadCommand
import com.songk.upload.domain.exception.FileNotFoundException
import com.songk.upload.domain.exception.FileSizeExceededException
import com.songk.upload.domain.exception.InvalidFileTypeException
import com.songk.upload.domain.model.FileUpload
import com.songk.upload.domain.model.FileUploadStatus
import com.songk.upload.domain.port.`in`.FileUploadUseCase
import com.songk.upload.domain.port.out.FileStoragePort
import com.songk.upload.domain.port.out.FileUploadRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FileUploadService(
    private val fileStoragePort: FileStoragePort,
    private val fileUploadRepository: FileUploadRepository
) : FileUploadUseCase {

    companion object {
        private const val MAX_FILE_SIZE = 50L * 1024 * 1024
    }

    override suspend fun upload(command: UploadCommand): FileUpload {
        validateFileSize(command.fileSize)
        validateContentType(command.contentType)

        val storagePath = fileStoragePort.store(
            command.originalFileName,
            command.content,
            command.contentType
        )
        val storedFileName = storagePath.substringAfterLast('/').substringAfterLast('\\')

        val fileUpload = FileUpload(
            originalFileName = command.originalFileName,
            storedFileName = storedFileName,
            fileSize = command.fileSize,
            contentType = command.contentType,
            storagePath = storagePath,
            uploadedBy = command.uploadedBy,
            status = FileUploadStatus.COMPLETED
        )
        return fileUploadRepository.save(fileUpload)
    }

    @Transactional(readOnly = true)
    override fun findAll(uploadedBy: String): Flow<FileUpload> =
        fileUploadRepository.findAllByUploadedBy(uploadedBy)

    @Transactional(readOnly = true)
    override suspend fun findById(id: Long, uploadedBy: String): FileUpload =
        fileUploadRepository.findByIdAndUploadedBy(id, uploadedBy)
            ?: throw FileNotFoundException("File not found with id: $id")

    override suspend fun delete(id: Long, uploadedBy: String) {
        val fileUpload = findById(id, uploadedBy)
        fileStoragePort.delete(fileUpload.storagePath)
        fileUploadRepository.deleteById(id)
    }

    private fun validateFileSize(fileSize: Long) {
        if (fileSize > MAX_FILE_SIZE) {
            throw FileSizeExceededException("File size exceeds maximum allowed size of 50MB")
        }
    }

    private fun validateContentType(contentType: String) {
        val baseType = contentType.substringBefore(';').trim()
        val allowed = baseType.startsWith("image/")
                || baseType == "application/pdf"
                || baseType == "text/plain"
        if (!allowed) {
            throw InvalidFileTypeException("File type '$contentType' is not allowed")
        }
    }
}
