package plain.bookmaru.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("cloud.aws.s3.profile-image")
data class ProfileImageStorageProperties(
    val bucket: String,
    val publicBaseUrl: String,
    val uploadUrlTtl: Duration = Duration.ofMinutes(10)
)
