package plain.bookmaru.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class S3Config {

    @Value("\${cloud.aws.region}")
    private lateinit var region: String

    @Value("\${cloud.aws.credentials.access-key:}")
    private lateinit var accessKey: String

    @Value("\${cloud.aws.credentials.secret-key:}")
    private lateinit var secretKey: String

    private fun credentialsProvider() =
        if (accessKey.isNotBlank() && secretKey.isNotBlank())
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        else
            DefaultCredentialsProvider.create()

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider())
            .build()
    }
}
