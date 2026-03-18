package com.songk.upload.infrastructure.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider(
    @Value("\${jwt.secret}") private val secret: String,
    @Value("\${jwt.expiration}") private val expiration: Long
) {
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private val verifier = JWT.require(algorithm).build()

    fun generateToken(username: String): String =
        JWT.create()
            .withSubject(username)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + expiration))
            .sign(algorithm)

    fun validateToken(token: String): Boolean =
        runCatching { verifier.verify(token) }.isSuccess

    fun extractSubject(token: String): String =
        verifier.verify(token).subject
}
