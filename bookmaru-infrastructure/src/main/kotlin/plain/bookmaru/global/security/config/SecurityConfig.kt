package plain.bookmaru.global.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import plain.bookmaru.global.security.jwt.JwtAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity) : SecurityFilterChain {
        http
            .csrf { csrfConfigurer -> csrfConfigurer.disable() }

            .cors { cors -> {} }

            .sessionManagement {configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            .headers { headers -> headers.frameOptions { it.sameOrigin() } }

            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                auth.requestMatchers(
                    /*
                    email
                     */
                    "/api/email/send",
                    "/api/email/verification",

                    /*
                    affiliation
                     */
                    "/affiliation/view",

                    /*
                    member
                     */
                    "/api/member/signup-member",

                    /*
                    auth
                     */
                    "/api/auth/login-member",
                    "/api/auth/reissue"
                ).permitAll()
                auth.anyRequest().hasAnyRole("USER", "OVERDUE", "LIBRARIAN", "MANAGER", "TEACHER", "ADMIN")
            }
        return http.build()
    }
}