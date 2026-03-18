package com.songk.upload.infrastructure.security

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtProvider: JwtProvider
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials as? String ?: return Mono.empty()
        return if (jwtProvider.validateToken(token)) {
            Mono.just(authentication)
        } else {
            Mono.error(BadCredentialsException("Invalid or expired JWT token"))
        }
    }
}
