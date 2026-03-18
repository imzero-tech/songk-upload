package com.songk.upload.adapter.out.persistence

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("file_upload")
data class FileUploadEntity(
    @Id
    val id: Long? = null,

    @Column("original_file_name")
    val originalFileName: String,

    @Column("stored_file_name")
    val storedFileName: String,

    @Column("file_size")
    val fileSize: Long,

    @Column("content_type")
    val contentType: String,

    @Column("storage_path")
    val storagePath: String,

    @Column("uploaded_by")
    val uploadedBy: String,

    @Column("status")
    val status: String,

    @Column("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
