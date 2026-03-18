package com.songk.upload.application.command

data class UploadCommand(
    val originalFileName: String,
    val contentType: String,
    val fileSize: Long,
    val content: ByteArray,
    val uploadedBy: String
)
