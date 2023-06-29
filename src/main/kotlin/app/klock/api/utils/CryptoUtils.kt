package app.klock.api.utils

import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

class CryptoUtils {

    fun main() {
        val key = generateKey(32)
        val text = "1234567890"

        val hashedKey = hashKey(key)

        val encrypted = encrypt(text, hashedKey)
        println("Encrypted: $encrypted")

        val decrypted = decrypt(encrypted, hashedKey)
        println("Decrypted: $decrypted")
    }

    fun encrypt(plainText: String, secretKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encrypted = cipher.doFinal(plainText.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encryptedText: String, secretKey: SecretKeySpec): String {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey)
        val decoder = Base64.getDecoder()
        val decrypted = cipher.doFinal(decoder.decode(encryptedText))
        return String(decrypted)
    }

    fun hashKey(key: String): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val keyByte = key.toByteArray(charset("UTF-8"))
        sha.update(keyByte, 0, keyByte.size)
        val keyBytes = sha.digest()
        return SecretKeySpec(keyBytes, "AES")
    }

    fun generateKey(size: Int): String {
        return (1..size).map { Random.nextInt(0, 256) }.joinToString("")
    }
}