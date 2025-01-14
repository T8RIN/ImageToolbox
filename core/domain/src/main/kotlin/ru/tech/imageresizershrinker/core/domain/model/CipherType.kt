/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.domain.model

@ConsistentCopyVisibility
/**
 * [CipherType] multiplatform domain wrapper for java Cipher, in order to add custom digests, you need to call [registerSecurityCiphers] when process created
 **/
data class CipherType private constructor(
    val cipher: String,
    val name: String = cipher
) {
    companion object {
        val AES_NO_PADDING = CipherType("AES/GCM/NoPadding")

        private var securityCiphers: List<String>? = null

        fun registerSecurityCiphers(ciphers: List<String>) {
            if (!securityCiphers.isNullOrEmpty()) {
                throw IllegalArgumentException("SecurityCiphers already registered")
            }
            securityCiphers = ciphers.distinctBy { it.replace("OID.", "").uppercase() }
        }

        val entries: List<CipherType> by lazy {
            val available = securityCiphers?.mapNotNull { cipher ->
                if (cipher.isEmpty()) null
                else {
                    val strippedCipher = cipher.replace("OID.", "")
                    SecureAlgorithmsMapping.findMatch(strippedCipher)?.let { mapping ->
                        CipherType(
                            cipher = cipher,
                            name = mapping.algorithm
                        )
                    } ?: CipherType(cipher = cipher)
                }
            }?.sortedBy { it.cipher } ?: emptyList()

            listOf(
                AES_NO_PADDING
            ).let {
                it + available
            }.distinctBy { it.name.replace("-", "") }
        }

        fun fromString(
            cipher: String?
        ): CipherType? = cipher?.let {
            entries.find {
                it.cipher == cipher
            }
        }
    }
}