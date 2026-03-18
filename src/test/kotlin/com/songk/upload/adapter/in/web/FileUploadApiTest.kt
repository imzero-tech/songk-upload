package com.songk.upload.adapter.`in`.web

import com.songk.upload.infrastructure.security.JwtProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FileUploadApiTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var jwtProvider: JwtProvider

    private fun bearerToken(username: String = "testuser") =
        "Bearer ${jwtProvider.generateToken(username)}"

    @Test
    fun `GET files should return 401 without JWT`() {
        webTestClient.get()
            .uri("/api/v1/files")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `POST auth token should return JWT for valid credentials`() {
        webTestClient.post()
            .uri("/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("username" to "user", "password" to "password"))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.accessToken").isNotEmpty
            .jsonPath("$.expiresIn").isEqualTo(3600)
    }

    @Test
    fun `POST auth token should return 401 for invalid credentials`() {
        webTestClient.post()
            .uri("/auth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("username" to "wrong", "password" to "wrong"))
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `POST files should return 201 after upload`() {
        val builder = MultipartBodyBuilder()
        builder.part("file", "hello file content".toByteArray())
            .filename("test.txt")
            .contentType(MediaType.TEXT_PLAIN)

        webTestClient.post()
            .uri("/api/v1/files")
            .header("Authorization", bearerToken())
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(builder.build()))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.originalFileName").isEqualTo("test.txt")
            .jsonPath("$.status").isEqualTo("COMPLETED")
    }

    @Test
    fun `GET files should return list with JWT`() {
        webTestClient.get()
            .uri("/api/v1/files")
            .header("Authorization", bearerToken())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
    }

    @Test
    fun `GET files id should return 404 for non-existent file`() {
        webTestClient.get()
            .uri("/api/v1/files/99999")
            .header("Authorization", bearerToken())
            .exchange()
            .expectStatus().isNotFound
    }
}
