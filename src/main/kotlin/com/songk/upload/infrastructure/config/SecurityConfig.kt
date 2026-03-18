package com.songk.upload.infrastructure.config

import com.songk.upload.infrastructure.security.JwtAuthenticationConverter
import com.songk.upload.infrastructure.security.JwtAuthenticationManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationManager: JwtAuthenticationManager,
    private val jwtAuthenticationConverter: JwtAuthenticationConverter
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val authFilter = AuthenticationWebFilter(jwtAuthenticationManager).apply {
            setServerAuthenticationConverter(jwtAuthenticationConverter)
        }

        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange { spec ->
                spec
                    .pathMatchers("/auth/**").permitAll()
                    .pathMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/webjars/**"
                    ).permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(authFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}
