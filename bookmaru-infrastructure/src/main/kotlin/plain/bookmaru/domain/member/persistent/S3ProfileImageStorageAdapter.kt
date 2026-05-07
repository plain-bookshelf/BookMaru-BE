package plain.bookmaru.domain.member.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort
import plain.bookmaru.domain.member.port.out.result.ProfileImageUploadUrlResult
import plain.bookmaru.global.properties.ProfileImageStorageProperties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Instant

@Component
class S3ProfileImageStorageAdapter(
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner,
    private val properties: ProfileImageStorageProperties
) : MemberProfileImageStoragePort {

    override fun createPresignedUploadUrl(imageKey: String, contentType: String): ProfileImageUploadUrlResult {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(properties.bucket)
            .key(imageKey)
            .contentType(contentType)
            .build()

        val presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(properties.uploadUrlTtl)
            .putObjectRequest(putObjectRequest)
            .build()

        val presignedRequest = s3Presigner.presignPutObject(presignRequest)

        return ProfileImageUploadUrlResult(
            uploadUrl = presignedRequest.url().toString(),
            imageKey = imageKey,
            publicUrl = toPublicUrl(imageKey),
            expiresAt = Instant.now().plus(properties.uploadUrlTtl)
        )
    }

    override fun upload(imageKey: String, content: ByteArray, contentType: String) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(properties.bucket)
            .key(imageKey)
            .contentType(contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content))
    }

    override fun exists(imageKey: String): Boolean {
        val headObjectRequest = HeadObjectRequest.builder()
            .bucket(properties.bucket)
            .key(imageKey)
            .build()

        return try {
            s3Client.headObject(headObjectRequest)
            true
        } catch (e: S3Exception) {
            if (e.statusCode() == 404) false else throw e
        }
    }

    override fun delete(imageKey: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(properties.bucket)
            .key(imageKey)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    override fun toPublicUrl(imageKey: String): String {
        val trimmedImageKey = imageKey.trim()
        if (trimmedImageKey.isBlank()) return ""
        if (trimmedImageKey.startsWith("http://") || trimmedImageKey.startsWith("https://")) {
            return trimmedImageKey
        }

        return "${properties.publicBaseUrl.trimEnd('/')}/${trimmedImageKey.trimStart('/')}"
    }
}
