package com.songk.upload.application.service

import com.songk.upload.domain.exception.FileNotFoundException
import com.songk.upload.domain.model.FileUpload
import com.songk.upload.domain.model.FileUploadStatus
import com.songk.upload.domain.port.`in`.FileUploadUseCase
import com.songk.upload.domain.port.`in`.UploadCommand
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

    override suspend fun upload(command: UploadCommand): FileUpload {
        FileUpload.validate(command.fileSize, command.contentType)

        val storageResult = fileStoragePort.store(
            command.originalFileName,
            command.content,
            command.contentType
        )

        val fileUpload = FileUpload(
            originalFileName = command.originalFileName,
            storedFileName = storageResult.storedFileName,
            fileSize = command.fileSize,
            contentType = command.contentType,
            storagePath = storageResult.storagePath,
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
}
