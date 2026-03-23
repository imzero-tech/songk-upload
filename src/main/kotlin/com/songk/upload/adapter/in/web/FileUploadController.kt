package com.songk.upload.adapter.`in`.web

import com.songk.upload.adapter.`in`.web.dto.FileUploadResponse
import com.songk.upload.adapter.`in`.web.dto.FileUploadSchema
import com.songk.upload.domain.port.`in`.UploadCommand
import com.songk.upload.domain.port.`in`.FileUploadUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.parameters.RequestBody
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@Tag(name = "File Upload", description = "파일 업로드 관리 API")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequestMapping("/api/v1/files")
class FileUploadController(
    private val fileUploadUseCase: FileUploadUseCase
) {

    @Operation(
        summary = "파일 업로드",
        description = "단일 파일을 업로드합니다. 허용 타입: image/*, application/pdf, text/plain (최대 50MB)",
        responses = [
            ApiResponse(responseCode = "201", description = "업로드 성공"),
            ApiResponse(responseCode = "400", description = "허용되지 않는 파일 타입 또는 누락된 파일", content = [Content(schema = Schema(hidden = true))]),
            ApiResponse(responseCode = "413", description = "파일 크기 초과", content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @RequestBody(
        content = [Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = Schema(implementation = FileUploadSchema::class)
        )]
    )
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun upload(
        @Parameter(hidden = true) @RequestPart("file") filePart: FilePart,
        authentication: Authentication
    ): FileUploadResponse {
        val contentType = filePart.headers().contentType?.toString() ?: "application/octet-stream"

        val dataBuffer = DataBufferUtils.join(filePart.content()).awaitSingle()
        val bytes = ByteArray(dataBuffer.readableByteCount())
        dataBuffer.read(bytes)
        DataBufferUtils.release(dataBuffer)

        val command = UploadCommand(
            originalFileName = filePart.filename(),
            contentType = contentType,
            fileSize = bytes.size.toLong(),
            content = bytes,
            uploadedBy = authentication.name
        )

        return FileUploadResponse.from(fileUploadUseCase.upload(command))
    }

    @Operation(
        summary = "업로드 목록 조회",
        description = "본인이 업로드한 파일 목록을 반환합니다.",
        responses = [
            ApiResponse(responseCode = "200", description = "조회 성공")
        ]
    )
    @GetMapping
    fun findAll(authentication: Authentication): Flow<FileUploadResponse> =
        fileUploadUseCase.findAll(authentication.name)
            .map { FileUploadResponse.from(it) }


    @Operation(
        summary = "파일 단건 조회",
        description = "요청한 파일을 조회 한다.",
        responses = [
            ApiResponse(responseCode = "204", description = "조회 성공"),
            ApiResponse(responseCode = "404", description = "파일 없음", content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @GetMapping("/{id}")
    suspend fun findById(
        @Parameter(description = "파일 ID") @PathVariable id: Long,
        authentication: Authentication
    ): FileUploadResponse =
        FileUploadResponse.from(fileUploadUseCase.findById(id, authentication.name))

    @Operation(
        summary = "파일 삭제",
        description = "요청한 파일을 삭제 한다.",
        responses = [
            ApiResponse(responseCode = "204", description = "삭제 성공"),
            ApiResponse(responseCode = "404", description = "파일 없음", content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    suspend fun delete(
        @Parameter(description = "파일 ID") @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        fileUploadUseCase.delete(id, authentication.name)
        return ResponseEntity.noContent().build()
    }
}
