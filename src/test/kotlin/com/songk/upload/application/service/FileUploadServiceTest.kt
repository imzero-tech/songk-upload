package com.songk.upload.application.service

import com.songk.upload.application.command.UploadCommand
import com.songk.upload.domain.exception.FileSizeExceededException
import com.songk.upload.domain.exception.InvalidFileTypeException
import com.songk.upload.domain.model.FileUpload
import com.songk.upload.domain.model.FileUploadStatus
import com.songk.upload.domain.port.out.FileStoragePort
import com.songk.upload.domain.port.out.FileUploadRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class FileUploadServiceTest {

    @MockK
    lateinit var fileStoragePort: FileStoragePort

    @MockK
    lateinit var fileUploadRepository: FileUploadRepository

    @InjectMockKs
    lateinit var fileUploadService: FileUploadService

    @Test
    fun `upload should store file and return completed upload`() = runTest {
        val command = UploadCommand(
            originalFileName = "test.png",
            contentType = "image/png",
            fileSize = 1024L,
            content = ByteArray(1024),
            uploadedBy = "user"
        )
        val storagePath = "./uploads/uuid.png"
        val savedUpload = FileUpload(
            id = 1L,
            originalFileName = "test.png",
            storedFileName = "uuid.png",
            fileSize = 1024L,
            contentType = "image/png",
            storagePath = storagePath,
            uploadedBy = "user",
            status = FileUploadStatus.COMPLETED,
            createdAt = LocalDateTime.now()
        )

        coEvery { fileStoragePort.store("test.png", any(), "image/png") } returns storagePath
        coEvery { fileUploadRepository.save(any()) } returns savedUpload

        val result = fileUploadService.upload(command)

        assertEquals(FileUploadStatus.COMPLETED, result.status)
        assertEquals("user", result.uploadedBy)
        assertEquals("test.png", result.originalFileName)

        coVerify(exactly = 1) { fileStoragePort.store("test.png", any(), "image/png") }
        coVerify(exactly = 1) { fileUploadRepository.save(any()) }
    }

    @Test
    fun `upload should throw FileSizeExceededException when file exceeds 50MB`() = runTest {
        val command = UploadCommand(
            originalFileName = "big.pdf",
            contentType = "application/pdf",
            fileSize = 100L * 1024 * 1024,
            content = ByteArray(0),
            uploadedBy = "user"
        )

        assertFailsWith<FileSizeExceededException> {
            fileUploadService.upload(command)
        }
    }

    @Test
    fun `upload should throw InvalidFileTypeException for disallowed MIME type`() = runTest {
        val command = UploadCommand(
            originalFileName = "script.exe",
            contentType = "application/x-msdownload",
            fileSize = 512L,
            content = ByteArray(512),
            uploadedBy = "user"
        )

        assertFailsWith<InvalidFileTypeException> {
            fileUploadService.upload(command)
        }
    }

    @Test
    fun `upload should allow pdf content type`() = runTest {
        val command = UploadCommand(
            originalFileName = "doc.pdf",
            contentType = "application/pdf",
            fileSize = 2048L,
            content = ByteArray(2048),
            uploadedBy = "user"
        )
        val storagePath = "./uploads/uuid.pdf"
        val savedUpload = FileUpload(
            id = 2L,
            originalFileName = "doc.pdf",
            storedFileName = "uuid.pdf",
            fileSize = 2048L,
            contentType = "application/pdf",
            storagePath = storagePath,
            uploadedBy = "user",
            status = FileUploadStatus.COMPLETED,
            createdAt = LocalDateTime.now()
        )

        coEvery { fileStoragePort.store("doc.pdf", any(), "application/pdf") } returns storagePath
        coEvery { fileUploadRepository.save(any()) } returns savedUpload

        val result = fileUploadService.upload(command)

        assertEquals(FileUploadStatus.COMPLETED, result.status)
    }
}
