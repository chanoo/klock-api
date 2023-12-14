package app.klock.api.aws.s3.service

import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.nio.ByteBuffer
import java.util.*

private val logger = KotlinLogging.logger {}
@Service
class S3Service(private val s3AsyncClient: S3AsyncClient,
                @Value("\${cloud.aws.s3.bucket}") private val bucket: String) {

    fun uploadFile(filePath: String, file: ByteArray, originFileName: String): Mono<String> {
        val fileExtension = getActualFileExtension(originFileName) ?: "unknown"
        val contentType = getMediaType(fileExtension)
        val key = generateUniqueKey(filePath, fileExtension)

        val putObjectRequest: PutObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        val byteBuffer = ByteBuffer.wrap(file)
        val requestBody = AsyncRequestBody.fromByteBuffer(byteBuffer)

        return Mono.create { sink ->
            s3AsyncClient.putObject({ builder ->
                builder.bucket(putObjectRequest.bucket())
                builder.key(putObjectRequest.key())
                builder.contentType(putObjectRequest.contentType())
            }, requestBody).whenComplete { _, throwable ->
                if (throwable != null) {
                    sink.error(throwable)
                } else {
                    sink.success(key)
                }
            }
        }
    }

    private fun generateUniqueKey(filePath: String, fileExtension: String): String {
        return "$filePath/${UUID.randomUUID()}.$fileExtension"
    }

    private fun getActualFileExtension(fileName: String): String? {
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex != -1 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1)
        } else {
            null
        }
    }

    private fun getMediaType(fileExtension: String): String {
        return when (fileExtension) {
            "jpg", "jpeg" -> MediaType.IMAGE_JPEG_VALUE
            "png" -> MediaType.IMAGE_PNG_VALUE
            "gif" -> MediaType.IMAGE_GIF_VALUE
            else -> MediaType.APPLICATION_OCTET_STREAM_VALUE
        }
    }
}