package com.songk.upload.infrastructure.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationConverter(
    private val jwtProvider: JwtProvider
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val token = exchange.request.headers
            .getFirst(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith("Bearer ") }
            ?.removePrefix("Bearer ")
            ?.trim()
            ?: return Mono.empty()

        return runCatching {
            val username = jwtProvider.extractSubject(token)
            Mono.just(UsernamePasswordAuthenticationToken(username, token, emptyList()) as Authentication)
        }.getOrElse { Mono.empty() }
    }
}
