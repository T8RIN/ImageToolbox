/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

@file:Suppress("PrivatePropertyName", "SpellCheckingInspection")

package com.t8rin.imagetoolbox.feature.cipher.data

import com.t8rin.imagetoolbox.core.data.saving.io.StringReadable
import com.t8rin.imagetoolbox.core.data.utils.computeBytesFromReadable
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.CipherType
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.domain.model.WrongKeyException
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.IMAGE_TOOLBOX_ENCRYPTED_FILE_MAGIC
import com.t8rin.imagetoolbox.core.utils.getString
import com.t8rin.imagetoolbox.feature.cipher.domain.CryptographyManager
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

internal class AndroidCryptographyManager @Inject constructor(
    private val dispatchersHolder: DispatchersHolder
) : CryptographyManager, DispatchersHolder by dispatchersHolder {

    private val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val secureRandom = SecureRandom()

    private fun createKey(
        password: String,
        type: CipherType
    ): Key = if ("PBE" in type.name) {
        SecretKeyFactory
            .getInstance(type.cipher)
            .generateSecret(
                PBEKeySpec(password.toCharArray())
            )
    } else {
        hashingType(type.name)
            .computeBytesFromReadable(StringReadable(password))
            .let { key ->
                SecretKeySpec(key, type.cipher)
            }
    }

    override fun generateRandomString(length: Int): String {
        val sr = SecureRandom()
        return buildString {
            repeat(length) {
                append(CHARS[sr.nextInt(CHARS.length)])
            }
        }
    }

    override suspend fun decrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray = withContext(defaultDispatcher) {
        if (EncryptedFileHeader.hasMagic(data)) {
            decryptVersioned(data, key)
        } else {
            decryptLegacy(data, key, type)
        }
    }

    override suspend fun encrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray = withContext(defaultDispatcher) {
        val salt = randomBytes(SALT_SIZE)
        val nonce =
            if (type.isGcm()) randomBytes(GCM_NONCE_SIZE) else randomBytes(ivSize(type.name))
        val keySize = keySize(type)
        val header = EncryptedFileHeader(
            iterations = PBKDF2_ITERATIONS,
            keySize = keySize,
            salt = salt,
            nonce = nonce,
            cipher = type.cipher
        )
        val encodedHeader = header.encode()
        val cipher = type.toVersionedCipher(
            password = key,
            header = header,
            isEncrypt = true
        )

        if (type.isAuthenticated()) cipher.updateAAD(encodedHeader)

        encodedHeader + cipher.doOrThrow(data)
    }

    private fun decryptVersioned(
        data: ByteArray,
        password: String
    ): ByteArray = runCatching {
        val (header, headerSize) = EncryptedFileHeader.decode(data)
        val type = CipherType.fromString(header.cipher)
            ?: CipherType.getInstance(header.cipher)
        val cipher = type.toVersionedCipher(
            password = password,
            header = header,
            isEncrypt = false
        )

        if (type.isAuthenticated()) cipher.updateAAD(data, 0, headerSize)

        cipher.doOrThrow(data.copyOfRange(headerSize, data.size))
    }.getOrElse {
        throw Throwable(
            message = getString(R.string.decrypt_failed),
            cause = it
        )
    }

    private fun decryptLegacy(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray {
        val cipher = type.toLegacyCipher(
            keySpec = createKey(
                password = key,
                type = type
            ),
            isEncrypt = false
        )

        return cipher.doOrThrow(data)
    }

    private fun Cipher.init(
        keySpec: Key,
        isEncrypt: Boolean,
        type: CipherType
    ) {
        val mode = if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE
        try {
            val encodedKey = keySpec.encoded
            when {
                "PBE" in type.name -> {
                    init(
                        mode,
                        keySpec,
                        PBEParameterSpec(encodedKey, encodedKey.size)
                    )
                }

                else -> {
                    init(
                        mode,
                        keySpec,
                        IvParameterSpec(encodedKey, 0, blockSize)
                    )
                }
            }
        } catch (_: Throwable) {
            runCatching {
                init(
                    mode,
                    keySpec,
                    generateIV(ivSize(type.name))
                )
            }.getOrElse {
                init(
                    mode,
                    keySpec
                )
            }
        }
    }

    private fun CipherType.toLegacyCipher(
        keySpec: Key,
        isEncrypt: Boolean
    ): Cipher = Cipher.getInstance(cipher).apply {
        init(
            keySpec = keySpec,
            isEncrypt = isEncrypt,
            type = this@toLegacyCipher
        )
    }

    private fun CipherType.toVersionedCipher(
        password: String,
        header: EncryptedFileHeader,
        isEncrypt: Boolean
    ): Cipher {
        val keySpec = deriveKey(
            password = password,
            type = this,
            salt = header.salt,
            iterations = header.iterations,
            keySize = header.keySize
        )
        val mode = if (isEncrypt) Cipher.ENCRYPT_MODE else Cipher.DECRYPT_MODE

        return Cipher.getInstance(cipher).apply {
            when {
                "PBE" in this@toVersionedCipher.name -> init(
                    mode,
                    keySpec,
                    PBEParameterSpec(header.salt, header.iterations)
                )

                this@toVersionedCipher.isGcm() -> init(
                    mode,
                    keySpec,
                    GCMParameterSpec(GCM_TAG_SIZE_BITS, header.nonce)
                )

                else -> runCatching {
                    init(mode, keySpec, IvParameterSpec(header.nonce))
                }.getOrElse {
                    init(mode, keySpec)
                }
            }
        }
    }

    private fun deriveKey(
        password: String,
        type: CipherType,
        salt: ByteArray,
        iterations: Int,
        keySize: Int
    ): Key {
        val passwordChars = password.toCharArray()
        val spec = PBEKeySpec(passwordChars, salt, iterations, keySize)
        return try {
            val encoded = SecretKeyFactory
                .getInstance(PBKDF2_ALGORITHM)
                .generateSecret(spec)
                .encoded

            if ("PBE" in type.name) {
                val derivedPassword = encoded.joinToString(separator = "") { "%02x".format(it) }
                SecretKeyFactory
                    .getInstance(type.cipher)
                    .generateSecret(PBEKeySpec(derivedPassword.toCharArray()))
            } else {
                SecretKeySpec(encoded, type.cipher)
            }
        } finally {
            spec.clearPassword()
            passwordChars.fill('\u0000')
        }
    }

    private fun Cipher.doOrThrow(data: ByteArray): ByteArray {
        return try {
            doFinal(data)
        } catch (e: Throwable) {
            throw if (e.message?.contains("mac") == true && e.message?.contains("failed") == true) {
                WrongKeyException()
            } else e
        }
    }

    private fun generateIV(size: Int): IvParameterSpec {
        return IvParameterSpec(randomBytes(size))
    }

    private fun randomBytes(size: Int) = ByteArray(size).also(secureRandom::nextBytes)

    private fun CipherType.isGcm() = name.contains("GCM", ignoreCase = true)

    private fun CipherType.isAuthenticated() = isGcm() ||
            name.contains("CCM", ignoreCase = true) ||
            name.contains("POLY1305", ignoreCase = true)

    private fun keySize(type: CipherType) = if (MD5List.any { type.name.contains(it, true) }) {
        AES_128_KEY_SIZE
    } else {
        AES_256_KEY_SIZE
    }

    private fun ivSize(name: String): Int = when {
        name == "AESRFC5649WRAP"
                || name == "AESWRAPPAD"
                || name == "AES_128/KWP/NoPadding"
                || name == "AES_192/KWP/NoPadding"
                || name == "AES_256/KWP/NoPadding"
                || name == "ARIAWRAPPAD" -> 4

        name == "AESWRAP"
                || name == "AES_128/KW/NoPadding"
                || name == "AES_192/KW/NoPadding"
                || name == "CAMELLIAWRAP"
                || name == "AES_256/KW/NoPadding"
                || name == "ARIAWRAP"
                || name == "CHACHA"
                || "CBC" in name && name != "SEED/CBC"
                || name == "DESEDEWRAP"
                || name == "GRAINV1"
                || name == "SALSA20"
                || name.contains("wrap", true) -> 8

        name == "AES/ECB/PKCS7PADDING"
                || name == "AES/GCM-SIV/NOPADDING"
                || name == "AES128/CCM"
                || name == "AES192/CCM"
                || name == "AES256/CCM"
                || "CCM" in name
                || "CHACHA" in name
                || name == "AES_256/GCM-SIV/NOPADDING"
                || name == "AES_256/GCM/NOPADDING"
                || name == "GRAIN128"
                || name == "AES_128/CBC/NOPADDING"
                || name == "AES_128/CBC/PKCS5PADDING"
                || name == "AES_128/GCM/NOPADDING" -> 12

        name == "XSALSA20" -> 24
        name == "ZUC-256" -> 25

        "DSTU7624" in name && "512" in name -> 64

        else -> 16
    }

    private val MD5List = listOf(
        "SEED",
        "NOEKEON",
        "HC128",
        "AES_128/CBC/NOPADDING",
        "AES_128/CBC/PKCS5PADDING",
        "AES_128/GCM/NOPADDING",
        "DESEDE",
        "GRAIN128",
        "SM4",
        "TEA",
        "ZUC-128",
        "AES_128/ECB/NOPADDING",
        "AES_128/ECB/PKCS5PADDING",
        "AES_128/GCM/NOPADDING"
    )

    private fun hashingType(name: String): HashingType = when {
        MD5List.any { name.contains(it, true) } -> HashingType.MD5

        else -> HashingType.SHA_256
    }

    private class EncryptedFileHeader(
        val iterations: Int,
        val keySize: Int,
        val salt: ByteArray,
        val nonce: ByteArray,
        val cipher: String
    ) {
        fun encode(): ByteArray {
            val cipherBytes = cipher.encodeToByteArray()
            return ByteBuffer.allocate(FIXED_HEADER_SIZE + salt.size + nonce.size + cipherBytes.size)
                .put(magicBytes)
                .put(VERSION)
                .put(KDF_PBKDF2_SHA256)
                .putInt(iterations)
                .putShort(keySize.toShort())
                .put(salt.size.toByte())
                .put(nonce.size.toByte())
                .putShort(cipherBytes.size.toShort())
                .put(salt)
                .put(nonce)
                .put(cipherBytes)
                .array()
        }

        companion object {
            private val magicBytes = IMAGE_TOOLBOX_ENCRYPTED_FILE_MAGIC.encodeToByteArray()
            private const val VERSION: Byte = 1
            private const val KDF_PBKDF2_SHA256: Byte = 1
            private const val FIXED_HEADER_SIZE = 20
            private const val MAX_CIPHER_NAME_SIZE = 256
            private const val MAX_NONCE_SIZE = 64
            private const val BYTE_MASK = 0xFF
            private const val SHORT_MASK = 0xFFFF
            private const val PRINTABLE_ASCII_START = 0x20
            private const val PRINTABLE_ASCII_END = 0x7E

            fun hasMagic(data: ByteArray): Boolean {
                if (data.size < magicBytes.size) return false
                return data.copyOfRange(0, magicBytes.size).contentEquals(magicBytes)
            }

            fun decode(data: ByteArray): Pair<EncryptedFileHeader, Int> {
                require(data.size >= FIXED_HEADER_SIZE)
                val buffer = ByteBuffer.wrap(data)
                val magic = ByteArray(magicBytes.size).also(buffer::get)
                require(magic.contentEquals(magicBytes))
                require(buffer.get() == VERSION)
                require(buffer.get() == KDF_PBKDF2_SHA256)

                val iterations = buffer.int
                val keySize = buffer.short.toInt() and SHORT_MASK
                val saltSize = buffer.get().toInt() and BYTE_MASK
                val nonceSize = buffer.get().toInt() and BYTE_MASK
                val cipherSize = buffer.short.toInt() and SHORT_MASK
                val headerSize = FIXED_HEADER_SIZE + saltSize + nonceSize + cipherSize

                require(iterations in 1..PBKDF2_ITERATIONS)
                require(keySize in setOf(AES_128_KEY_SIZE, AES_256_KEY_SIZE))
                require(saltSize == SALT_SIZE)
                require(nonceSize in 0..MAX_NONCE_SIZE)
                require(cipherSize in 1..MAX_CIPHER_NAME_SIZE)
                require(headerSize < data.size)

                val salt = ByteArray(saltSize).also(buffer::get)
                val nonce = ByteArray(nonceSize).also(buffer::get)
                val cipherBytes = ByteArray(cipherSize).also(buffer::get)
                val cipher = cipherBytes.decodeToString(throwOnInvalidSequence = true)
                require(cipher.all { it.code in PRINTABLE_ASCII_START..PRINTABLE_ASCII_END })
                require(!cipher.contains('\u0000'))
                if (cipher.contains("GCM", ignoreCase = true)) {
                    require(nonceSize == GCM_NONCE_SIZE)
                }

                return EncryptedFileHeader(
                    iterations = iterations,
                    keySize = keySize,
                    salt = salt,
                    nonce = nonce,
                    cipher = cipher
                ) to headerSize
            }
        }
    }

    private companion object {
        const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"
        const val PBKDF2_ITERATIONS = 210_000
        const val SALT_SIZE = 16
        const val GCM_NONCE_SIZE = 12
        const val GCM_TAG_SIZE_BITS = 128
        const val AES_128_KEY_SIZE = 128
        const val AES_256_KEY_SIZE = 256
    }
}