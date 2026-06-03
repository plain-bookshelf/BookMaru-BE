package plain.bookmaru.global.security.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import plain.bookmaru.global.interceptor.LogInterceptor

@Configuration
class WebMvcFilter(
    private val logInterceptor: LogInterceptor
) : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:8080", "http://localhost:9200", "http://localhost:5173", "bookmaru-dsm-web.netlify.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
            .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "X-Refresh-Token")
            .exposedHeaders("Authorization", "X-Refresh-Token")
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(logInterceptor)
            .addPathPatterns("/**")
    }
}