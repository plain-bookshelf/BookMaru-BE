package plain.bookmaru.global.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import plain.bookmaru.global.security.jwt.JwtAuthenticationFilter
import plain.bookmaru.global.security.jwt.JwtParser
import plain.bookmaru.global.security.oauth2.CustomOAuth2UserService
import plain.bookmaru.global.security.oauth2.OAuth2SuccessHandler

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val jwtParser: JwtParser,
    private val redisTemplate: StringRedisTemplate,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val oAuth2SuccessHandler: OAuth2SuccessHandler
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }


    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtParser, redisTemplate)

        http
            .csrf { it.disable() }

            .cors { cors -> {} }

            .logout { it.disable() }

            .sessionManagement {configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .headers { headers -> headers.frameOptions { it.sameOrigin() } }

            .oauth2Login {
                it.loginProcessingUrl("/login/oauth2/code/*")
                it.userInfoEndpoint { it.userService(customOAuth2UserService)  }
                it.successHandler(oAuth2SuccessHandler)
            }

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.requestMatchers(
                    /*
                    verification
                     */
                    "/api/verification/email/send",
                    "/api/verification/email/verify",
                    "/api/verification/find-id",
                    "/api/verification/find-password",
                    "/api/verification/password-reset",

                    /*
                    affiliation
                     */
                    "/affiliation/view",

                    /*
                    member
                     */
                    "/api/member/signup-member",
                    "/api/member/signup-official",
                    "/api/member/signup-official",

                    /*
                    auth
                     */
                    "/api/auth/login",
                    "/api/auth/reissue",

                    /*
                    error
                     */
                    "/error",
                    "/favicon.ico"
                ).permitAll()

                it.requestMatchers(
                    /*
                    verification
                     */
                    "/api/verification/officialCode/save"
                ).hasRole("ADMIN")

                it.anyRequest().authenticated()
            }
        return http.build()
    }
}