package app.klock.api.utils

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.KeyStore
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

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
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, aesKey)

        val decryptedDataBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData))
        return String(decryptedDataBytes)
    }
}