package com.songk.upload.adapter.`in`.web

import com.songk.upload.adapter.`in`.web.dto.TokenRequest
import com.songk.upload.adapter.`in`.web.dto.TokenResponse
import com.songk.upload.infrastructure.security.JwtProvider
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "JWT 인증 API")
@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtProvider: JwtProvider
) {

    @Operation(
        summary = "JWT 토큰 발급",
        description = "username/password로 Bearer 토큰을 발급합니다. (개발용: user / password)",
        responses = [
            ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
            ApiResponse(responseCode = "401", description = "인증 실패", content = [Content(schema = Schema(hidden = true))])
        ]
    )
    @PostMapping("/token")
    suspend fun token(@RequestBody request: TokenRequest): ResponseEntity<Any> {
        if (request.username != "user" || request.password != "password") {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("error" to "Invalid credentials"))
        }

        val accessToken = jwtProvider.generateToken(request.username)
        return ResponseEntity.ok(TokenResponse(accessToken = accessToken, expiresIn = 3600))
    }
}
