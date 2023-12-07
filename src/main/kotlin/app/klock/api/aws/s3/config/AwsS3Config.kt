package app.klock.api.aws.s3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AwsS3Config(
    @Value("\${cloud.aws.credentials.access-key}") private val accessKey: String,
    @Value("\${cloud.aws.credentials.secret-key}") private val secretKey: String,
    @Value("\${cloud.aws.region.static}") private val region: String
) {

    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        return S3AsyncClient.builder()
            .credentialsProvider { AwsBasicCredentials.create(accessKey, secretKey) }
            .region(Region.of(region))
            .build()
    }
}