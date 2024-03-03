package app.klock.api.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class CryptoUtils {

    private val keyStoreFile: String = "key/klock.jks"
    @Value("\${crypto.key.store-password}")
    private val keyStorePassword: String = ""
    @Value("\${crypto.key.alias}")
    private val keyAlias: String = ""

    fun getPublicKey(): String {
        val keyStore = KeyStore.getInstance("JKS")
        keyStore.load(javaClass.classLoader.getResourceAsStream(keyStoreFile), keyStorePassword.toCharArray())

        val publicKey = keyStore.getCertificate(keyAlias).publicKey
        return Base64.getEncoder().encodeToString(publicKey.encoded)
    }

    fun decryptData(encryptedAesKey: String, encryptedData: String): String {
        val aesKey = decryptAesKey(encryptedAesKey)
        return decryptDataWithAesKey(encryptedData, aesKey)
    }

    private fun decryptAesKey(encryptedKey: String): SecretKeySpec {
        val keyStore = KeyStore.getInstance("JKS")
        keyStore.load(javaClass.classLoader.getResourceAsStream(keyStoreFile), keyStorePassword.toCharArray())

        val privateKey = keyStore.getKey(keyAlias, keyStorePassword.toCharArray())
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)

        val decryptedKeyBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedKey))
        return SecretKeySpec(decryptedKeyBytes, "AES")
    }

    private fun decryptDataWithAesKey(encryptedData: String, aesKey: SecretKeySpec): String {
        // AES/GCM/NoPadding 모드를 사용하여 Cipher 인스턴스를 생성합니다.
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Base64로 인코딩된 암호화된 데이터를 디코딩합니다.
        val encryptedDataBytes = Base64.getDecoder().decode(encryptedData)

        // GCM 모드에서는 Nonce/IV가 필요합니다. 암호화된 데이터의 앞부분에서 IV를 추출하는 방식입니다
        // IV 길이는 일반적으로 12바이트입니다. 실제 IV 추출 방식은 암호화할 때 IV를 어떻게 처리했는지에 따라 달라집니다.
        val iv = encryptedDataBytes.copyOfRange(0, 12)
        val encryptedMessage = encryptedDataBytes.copyOfRange(12, encryptedDataBytes.size)

        // GCMParameterSpec을 사용하여 IV를 지정합니다.
        cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(128, iv))

        // 복호화를 수행합니다.
        val decryptedDataBytes = cipher.doFinal(encryptedMessage)
        return String(decryptedDataBytes)
    }
}