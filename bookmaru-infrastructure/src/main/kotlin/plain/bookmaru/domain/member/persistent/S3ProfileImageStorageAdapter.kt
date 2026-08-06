package plain.bookmaru.domain.member.persistent

import org.springframework.stereotype.Component
import plain.bookmaru.domain.member.port.out.MemberProfileImageStoragePort
import plain.bookmaru.global.properties.ProfileImageStorageProperties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Component
class S3ProfileImageStorageAdapter(
    private val s3Client: S3Client,
    private val properties: ProfileImageStorageProperties
) : MemberProfileImageStoragePort {

    override fun upload(imageKey: String, content: ByteArray, contentType: String) {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(properties.bucket)
            .key(imageKey)
            .contentType(contentType)
            .build()

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content))
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
