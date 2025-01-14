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

@file:Suppress("PrivatePropertyName")

package ru.tech.imageresizershrinker.feature.cipher.data

import ru.tech.imageresizershrinker.core.domain.model.CipherType
import ru.tech.imageresizershrinker.core.domain.model.HashingType
import ru.tech.imageresizershrinker.feature.cipher.domain.CryptographyManager
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

internal class AndroidCryptographyManager @Inject constructor() : CryptographyManager {

    private val HASHING_ALGORITHM = HashingType.SHA_256.digest
    private val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    private fun createKey(
        password: String,
        type: CipherType
    ): SecretKeySpec {
        val pwBytes = password.toByteArray(charset("UTF-8"))

        // Create secret Key factory based on the specified algorithm
        val md = MessageDigest.getInstance(HASHING_ALGORITHM)

        // digest the pwBytes to be a new key
        md.update(pwBytes, 0, pwBytes.size)
        val key = md.digest()
        return SecretKeySpec(key, type.cipher)
    }

    override fun generateRandomString(length: Int): String {
        val sr = SecureRandom()
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            sb.append(CHARS[sr.nextInt(CHARS.length)])
        }
        return sb.toString()
    }

    override suspend fun decrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray {
        val keySpec = createKey(
            password = key,
            type = type
        )
        val cipher = type.toCipher()
        try {
            cipher.init(
                Cipher.DECRYPT_MODE,
                keySpec,
                IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
            )
        } catch (_: Exception) {
            cipher.init(
                Cipher.DECRYPT_MODE,
                keySpec
            )
        }
        return cipher.doFinal(data)
    }

    override suspend fun encrypt(
        data: ByteArray,
        key: String,
        type: CipherType
    ): ByteArray {
        val keySpec = createKey(
            password = key,
            type = type
        )

        val cipher = type.toCipher()
        try {
            cipher.init(
                Cipher.ENCRYPT_MODE,
                keySpec,
                IvParameterSpec(keySpec.encoded, 0, cipher.blockSize)
            )
        } catch (_: Exception) {
            cipher.init(
                Cipher.ENCRYPT_MODE,
                keySpec
            )
        }
        return cipher.doFinal(data)
    }

    private fun CipherType.toCipher(): Cipher = Cipher.getInstance(cipher)

}