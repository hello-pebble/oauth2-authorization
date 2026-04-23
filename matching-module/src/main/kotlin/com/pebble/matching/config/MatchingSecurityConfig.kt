package com.pebble.matching.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.AuthenticationEntryPoint

@Configuration
class MatchingSecurityConfig {

    @Bean
    fun matchingSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/actuator/health", "/", "/index.html", "/static/**").permitAll()
                    .requestMatchers("/api/v1/matching/**").authenticated()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
                oauth2.authenticationEntryPoint(jsonAuthenticationEntryPoint()) // JSON 응답 설정
            }
            .build()
    }

    @Bean
    fun jsonAuthenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { request, response, _ ->
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            val json = "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"인증이 필요한 매칭 서비스입니다. 게이트웨이 포털에서 로그인을 먼저 진행해주세요.\", \"path\": \"${request.requestURI}\"}"
            response.writer.write(json)
        }
    }
}
