package app.klock.api.utils

import java.io.InputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

object KeyPairUtils {

  private const val PRIVATE_KEY_FILE = "key/private_key.der"
  private const val PUBLIC_KEY_FILE = "key/public_key.der"

  fun loadKeyPair(): java.security.KeyPair {
    val privateKey = loadPrivateKey()
    val publicKey = loadPublicKey()

    return java.security.KeyPair(publicKey, privateKey)
  }

  private fun loadPrivateKey(): PrivateKey {
    val resourceAsStream: InputStream = javaClass.classLoader.getResourceAsStream(PRIVATE_KEY_FILE)
    val privateKeyBytes = resourceAsStream.readBytes()
    val privateKeySpec = PKCS8EncodedKeySpec(privateKeyBytes)

    return KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec)
  }

  private fun loadPublicKey(): PublicKey {
    val resourceAsStream: InputStream = javaClass.classLoader.getResourceAsStream(PUBLIC_KEY_FILE)
    val publicKeyBytes = resourceAsStream.readBytes()
    val publicKeySpec = X509EncodedKeySpec(publicKeyBytes)

    return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec)
  }
}
