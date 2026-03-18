package com.songk.upload.infrastructure.exception

import com.songk.upload.domain.exception.FileNotFoundException
import com.songk.upload.domain.exception.FileSizeExceededException
import com.songk.upload.domain.exception.InvalidFileTypeException
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalExceptionHandler : WebExceptionHandler {

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        val (status, message) = when (ex) {
            is FileNotFoundException -> HttpStatus.NOT_FOUND to (ex.message ?: "File not found")
            is FileSizeExceededException -> HttpStatus.PAYLOAD_TOO_LARGE to (ex.message ?: "File too large")
            is InvalidFileTypeException -> HttpStatus.BAD_REQUEST to (ex.message ?: "Invalid file type")
            is AccessDeniedException -> HttpStatus.FORBIDDEN to (ex.message ?: "Access denied")
            is IllegalArgumentException -> HttpStatus.BAD_REQUEST to (ex.message ?: "Bad request")
            else -> return Mono.error(ex)
        }

        val response = exchange.response
        response.statusCode = status
        response.headers.contentType = MediaType.APPLICATION_JSON

        val body = """{"error":"$message"}""".toByteArray()
        val buffer = response.bufferFactory().wrap(body)
        return response.writeWith(Mono.just(buffer))
    }
}
