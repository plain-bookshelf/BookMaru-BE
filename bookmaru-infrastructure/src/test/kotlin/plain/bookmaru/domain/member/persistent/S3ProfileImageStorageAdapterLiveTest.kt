package plain.bookmaru.domain.member.persistent

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import plain.bookmaru.global.properties.ProfileImageStorageProperties
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.HttpURLConnection
import java.net.URI
import java.time.Duration
import java.util.Base64
import java.util.UUID

class S3ProfileImageStorageAdapterLiveTest {

    @Test
    fun `upload profile image to real s3 and verify public url`() {
        assumeTrue(
            System.getProperty("bookmaru.liveS3") == "true" ||
                System.getenv("BOOKMARU_LIVE_S3") == "true"
        )
        assumeTrue(!System.getenv("AWS_ACCESS_KEY_ID").isNullOrBlank())
        assumeTrue(!System.getenv("AWS_SECRET_ACCESS_KEY").isNullOrBlank())

        val region = System.getenv("AWS_REGION") ?: "ap-northeast-2"
        val bucket = requireNotNull(System.getenv("AWS_S3_BUCKET")) {
            "AWS_S3_BUCKET is required for live S3 test."
        }
        val publicBaseUrl = requireNotNull(System.getenv("IMAGE_PUBLIC_BASE_URL")) {
            "IMAGE_PUBLIC_BASE_URL is required for live S3 test."
        }

        val s3Client = S3Client.builder()
            .region(Region.of(region))
            .httpClientBuilder(
                UrlConnectionHttpClient.builder()
                    .connectionTimeout(Duration.ofSeconds(10))
                    .socketTimeout(Duration.ofSeconds(20))
            )
            .build()
        val s3Presigner = S3Presigner.builder()
            .region(Region.of(region))
            .build()
        val adapter = S3ProfileImageStorageAdapter(
            s3Client = s3Client,
            s3Presigner = s3Presigner,
            properties = ProfileImageStorageProperties(
                bucket = bucket,
                publicBaseUrl = publicBaseUrl,
                uploadUrlTtl = Duration.ofMinutes(10)
            )
        )

        val imageKey = "members/codex-live-test/profile/${UUID.randomUUID()}.png"
        val pngBytes = Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/p9sAAAAASUVORK5CYII="
        )
        var uploaded = false

        try {
            adapter.upload(imageKey, pngBytes, "image/png")
            uploaded = true

            assertTrue(adapter.exists(imageKey), "Uploaded image must exist in S3.")

            val connection = URI.create(adapter.toPublicUrl(imageKey)).toURL().openConnection() as HttpURLConnection
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.requestMethod = "GET"

            assertEquals(200, connection.responseCode)
            assertTrue(connection.contentType.startsWith("image/png"))
            assertTrue(connection.inputStream.use { it.readBytes() }.isNotEmpty())
        } finally {
            if (uploaded) {
                adapter.delete(imageKey)
            }
            s3Client.close()
            s3Presigner.close()
        }
    }
}
