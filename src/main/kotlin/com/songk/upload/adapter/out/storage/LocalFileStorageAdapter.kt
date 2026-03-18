package com.songk.upload.adapter.out.storage

import com.songk.upload.domain.port.out.FileStoragePort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

@Component
class LocalFileStorageAdapter(
    @Value("\${file.storage.path}") private val storagePath: String
) : FileStoragePort {

    override suspend fun store(fileName: String, content: ByteArray, contentType: String): String {
        val extension = fileName.substringAfterLast('.', "")
        val storedName = if (extension.isNotEmpty()) "${UUID.randomUUID()}.$extension"
                         else UUID.randomUUID().toString()
        val targetPath = Path.of(storagePath, storedName)

        withContext(Dispatchers.IO) {
            Files.createDirectories(targetPath.parent)
            Files.write(targetPath, content)
        }

        return targetPath.toString()
    }

    override suspend fun delete(storagePath: String) {
        withContext(Dispatchers.IO) {
            Files.deleteIfExists(Path.of(storagePath))
        }
    }
}
