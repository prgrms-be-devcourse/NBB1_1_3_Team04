package com.grepp.nbe1_3_team04.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.grepp.nbe1_3_team04.global.api.ApiResponse
import com.grepp.nbe1_3_team04.global.exception.ExceptionHandlerFilter
import com.grepp.nbe1_3_team04.member.domain.MemberRole
import com.grepp.nbe1_3_team04.member.jwt.JwtTokenFilter
import com.grepp.nbe1_3_team04.member.oauth2.CustomOAuth2LoginSuccessHandler
import com.grepp.nbe1_3_team04.member.oauth2.CustomOAuth2UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.client.RestTemplate

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(private val jwtTokenFilter: JwtTokenFilter,
                     private val exceptionHandlerFilter: ExceptionHandlerFilter,
                     private val customOAuth2UserService: CustomOAuth2UserService,
                     private val customOAuth2LoginSuccessHandler: CustomOAuth2LoginSuccessHandler) {

    private val loginUrls = arrayOf("/api/v1/members/join", "/api/v1/members/login")
    private val permitUrls = arrayOf("/api/v1/court/**", "/api/v1/stadium/**","/api/v1/team/{teamId}/info","/ws", "/h2-console", "/h2-console/**")
    private val merchantUrls = arrayOf("/api/v1/merchant/**")

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(*loginUrls).permitAll()
                    .requestMatchers(*permitUrls).permitAll()
                    .requestMatchers(*merchantUrls).hasAuthority(MemberRole.MERCHANT.text)
                    .anyRequest().authenticated()
            }
            .oauth2Login {
                it.userInfoEndpoint { it.userService(customOAuth2UserService) }
                    .successHandler(customOAuth2LoginSuccessHandler)
            }
            .headers { headerConfig ->
                headerConfig.frameOptions { it.sameOrigin() }
            }
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(exceptionHandlerFilter, JwtTokenFilter::class.java)
            .exceptionHandling { it.accessDeniedHandler(accessDeniedHandler()) }

        return http.build()
    }

    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { _: HttpServletRequest?, response: HttpServletResponse, _: AccessDeniedException? ->
            val objectMapper = ObjectMapper()
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = "application/json"
            val apiResponse: ApiResponse<Any?> = ApiResponse.of(
                HttpStatus.FORBIDDEN,
                "권한이 없습니다.",
                null
            )

            val jsonResponse = objectMapper.writeValueAsString(apiResponse)
            response.characterEncoding = "UTF-8"
            response.writer.write(jsonResponse)
        }
    }
}
