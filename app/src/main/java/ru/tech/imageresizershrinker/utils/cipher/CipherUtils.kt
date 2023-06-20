package ru.tech.imageresizershrinker.utils.cipher

import android.content.Context
import android.net.Uri
import java.io.DataInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object CipherUtils {

    /*
    * Specification for encryption and decryption
    */
    private const val HASHING_ALGORITHM = "SHA-256"
    private const val ENCRYPTION_STANDARD = "AES/GCM/NoPadding"
    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val SALT_LEN = 4


    fun hash(str: String, salt: String): ByteArray? {
        return try {
            // append salt to password
            val cred = str + salt
            val credBytes = cred.toByteArray(charset("UTF-8"))

            // digest the credBytes
            val md =
                MessageDigest.getInstance(HASHING_ALGORITHM)
            md.update(credBytes, 0, credBytes.size)
            md.digest()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Generate random salt
     *
     * @param len length of salt
     * @return salt
     */
    fun randSalt(len: Int): String {
        val sr = SecureRandom()
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(CHARS[sr.nextInt(CHARS.length)])
        }
        return sb.toString()
    }

    /**
     * Generate random salt of the default length
     *
     * @return salt
     * @see {@link CryptoUtil.SALT_LEN}
     */
    fun randSalt(): String {
        return randSalt(SALT_LEN)
    }


    private fun createKey(password: String): SecretKeySpec {
        val pwBytes = password.toByteArray(charset("UTF-8"))

        // Create secret Key factory based on the specified algorithm
        val md = MessageDigest.getInstance(HASHING_ALGORITHM)

        // digest the pwBytes to be a new key
        md.update(pwBytes, 0, pwBytes.size)
        val key = md.digest()
        return SecretKeySpec(key, ENCRYPTION_STANDARD)
    }


    fun ByteArray.decrypt(key: String): ByteArray {
        val keySpec = createKey(key)
        val cipher = Cipher.getInstance(ENCRYPTION_STANDARD)
        cipher.init(
            Cipher.DECRYPT_MODE,
            keySpec,
            IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
        )
        return cipher.doFinal(this)
    }


    fun ByteArray.encrypt(key: String): ByteArray {
        val keySpec = createKey(key)
        val cipher = Cipher.getInstance(ENCRYPTION_STANDARD)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            keySpec,
            IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
        )
        return cipher.doFinal(this)
    }

    fun Context.encrypt(uri: Uri, key: String): ByteArray? {
        return contentResolver.openInputStream(uri)?.use {
            it.toByteArray().encrypt(key)
        }
    }

    fun Context.decrypt(uri: Uri, key: String): ByteArray? {
        return contentResolver.openInputStream(uri)?.use {
            it.toByteArray().decrypt(key)
        }
    }

    fun InputStream.toByteArray(): ByteArray {
        val bytes = ByteArray(this.available())
        val dis = DataInputStream(this)
        dis.readFully(bytes)
        return bytes
    }

}