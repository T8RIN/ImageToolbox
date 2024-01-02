@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.data.repository

import android.content.Context
import android.net.Uri
import ru.tech.imageresizershrinker.coredomain.repository.CipherRepository
import java.io.DataInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class CipherRepositoryImpl : CipherRepository {

    private val HASHING_ALGORITHM = "SHA-256"
    private val ENCRYPTION_STANDARD = "AES/GCM/NoPadding"
    private val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private fun createKey(password: String): SecretKeySpec {
        val pwBytes = password.toByteArray(charset("UTF-8"))

        // Create secret Key factory based on the specified algorithm
        val md = MessageDigest.getInstance(HASHING_ALGORITHM)

        // digest the pwBytes to be a new key
        md.update(pwBytes, 0, pwBytes.size)
        val key = md.digest()
        return SecretKeySpec(key, ENCRYPTION_STANDARD)
    }

    override fun generateRandomString(len: Int): String {
        val sr = SecureRandom()
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(CHARS[sr.nextInt(CHARS.length)])
        }
        return sb.toString()
    }

    override suspend fun decrypt(data: ByteArray, key: String): ByteArray {
        val keySpec = createKey(key)
        val cipher = Cipher.getInstance(ENCRYPTION_STANDARD)
        cipher.init(
            Cipher.DECRYPT_MODE,
            keySpec,
            IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
        )
        return cipher.doFinal(data)
    }

    override suspend fun encrypt(data: ByteArray, key: String): ByteArray {
        val keySpec = createKey(key)
        val cipher = Cipher.getInstance(ENCRYPTION_STANDARD)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            keySpec,
            IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
        )
        return cipher.doFinal(data)
    }

}

private fun InputStream.toByteArray(): ByteArray {
    val bytes = ByteArray(this.available())
    val dis = DataInputStream(this)
    dis.readFully(bytes)
    return bytes
}

suspend fun CipherRepository.encrypt(context: Context, uri: Uri, key: String): ByteArray? {
    return context.contentResolver.openInputStream(uri)?.use {
        encrypt(it.toByteArray(), key)
    }
}

suspend fun CipherRepository.decrypt(context: Context, uri: Uri, key: String): ByteArray? {
    return context.contentResolver.openInputStream(uri)?.use {
        decrypt(it.toByteArray(), key)
    }
}