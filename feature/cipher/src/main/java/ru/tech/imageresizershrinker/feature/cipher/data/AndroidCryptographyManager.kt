/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.cipher.data

import ru.tech.imageresizershrinker.core.domain.model.CipherType
import ru.tech.imageresizershrinker.core.domain.model.HashingType
import ru.tech.imageresizershrinker.feature.cipher.domain.CryptographyManager
import ru.tech.imageresizershrinker.feature.cipher.domain.WrongKeyException
import java.security.Key
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.PBEParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

internal class AndroidCryptographyManager @Inject constructor() : CryptographyManager {

    private val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private fun createKey(
        password: String,
        type: CipherType
    ): Key {
        val pwBytes = password.toByteArray(charset("UTF-8"))

        // Create secret Key factory based on the specified algorithm
        val md = MessageDigest.getInstance(hashingType(type.name))

        // digest the pwBytes to be a new key
        md.update(pwBytes, 0, pwBytes.size)
        val key = md.digest()
        return if ("PBE" in type.name) {
            val keySpec = PBEKeySpec(password.toCharArray())
            val factory = SecretKeyFactory.getInstance(type.cipher)
            factory.generateSecret(keySpec)
        } else SecretKeySpec(key, type.cipher)
    }

    override fun generateRandomString(length: Int): String {
        val sr = SecureRandom()
        val sb = StringBuilder(length)
        repeat(length) {
            sb.append(CHARS[sr.nextInt(CHARS.length)])
        }
        return sb.toString()
    }

    override suspend fun decrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray {
        val cipher = type.toCipher(
            keySpec = createKey(
                password = key,
                type = type
            ),
            isEncrypt = false
        )

        return cipher.doOrThrow(data)
    }

    override suspend fun encrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray {
        val cipher = type.toCipher(
            keySpec = createKey(
                password = key,
                type = type
            ),
            isEncrypt = true
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
        } catch (_: Exception) {
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

    private fun CipherType.toCipher(
        keySpec: Key,
        isEncrypt: Boolean
    ): Cipher = Cipher.getInstance(cipher).apply {
        init(
            keySpec = keySpec,
            isEncrypt = isEncrypt,
            type = this@toCipher
        )
    }

    private fun Cipher.doOrThrow(data: ByteArray): ByteArray {
        return try {
            doFinal(data)
        } catch (e: Exception) {
            throw if (e.message?.contains("mac") == true && e.message?.contains("failed") == true) {
                WrongKeyException()
            } else e
        }
    }

    private fun generateIV(size: Int): IvParameterSpec {
        val iv = ByteArray(size)
        SecureRandom().nextBytes(iv)
        return IvParameterSpec(iv)
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

    private fun hashingType(name: String): String = when {
        MD5List.any { name.contains(it, true) } -> HashingType.MD5.digest

        else -> HashingType.SHA_256.digest
    }
}