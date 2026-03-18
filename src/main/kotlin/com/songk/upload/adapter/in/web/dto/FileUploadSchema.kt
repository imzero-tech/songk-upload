package com.songk.upload.adapter.`in`.web.dto

import io.swagger.v3.oas.annotations.media.Schema

/** Swagger UI multipart 파일 업로드 스키마 */
class FileUploadSchema {
    @Schema(type = "string", format = "binary", description = "업로드할 파일 (image/*, application/pdf, text/plain, 최대 50MB)")
    lateinit var file: ByteArray
}
