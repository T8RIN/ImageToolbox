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

        val BROKEN by lazy {
            listOf(
                "IES",
                "SM2",
                "ML-KEM",
                "1.2.840.113533.7.66.10",
                "CAST5",
                "ElGAMAL",
                "KW",
                "RC2WRAP",
                "1.2.840.113549.1.1.7",
                "RSA",
                "OID.2.5.8.1.1",
                "RC5-64",
                "NTRU",
                "1.2.804.2",
                "GRAINV1",
                "1.3.14.3.2.7",
                "DSTU7624",
                "1.2.840.113549.1.1.1",
                "ETSIKEMWITHSHA256",
                "2.5.8.1.1",
                "GOST3412-2015",
                "SEEDWRAP",
                "2.16.840.1.101.3.4.1",
                "WRAP",
                "ARIACCM",
                "AES_128/ECB/NOPADDING",
                "AES_128/ECB/PKCS5PADDING",
                "DESEDE/ECB/NOPADDING",
                "1.2.392.200011.61.1.1.3",
                "AES/CBC/NOPADDING",
                "AES/ECB/NOPADDING",
                "AES/GCM-SIV/NOPADDING",
                "AES_128/CBC/NOPADDING",
                "AES_128/GCM-SIV/NOPADDING",
                "AES_128/GCM/NOPADDING",
                "AES_256/CBC/NOPADDING",
                "AES_256/ECB/NOPADDING",
                "AES_256/GCM-SIV/NOPADDING",
                "AES_256/GCM/NOPADDING",
                "1.2.840.113549.1.9.16.3.6",
                "DESEDE/CBC/NOPADDING",
                "CHACHA20-POLY1305",
                "CHACHA20/POLY1305"
            )
        }

        private var securityCiphers: List<CipherType>? = null

        fun registerSecurityCiphers(ciphers: List<CipherType>) {
            if (!securityCiphers.isNullOrEmpty()) {
                throw IllegalArgumentException("SecurityCiphers already registered")
            }
            securityCiphers = ciphers.distinctBy { it.cipher.replace("OID.", "").uppercase() }
        }

        val entries: List<CipherType> by lazy {
            val available = securityCiphers?.mapNotNull { cipher ->
                val oid = cipher.cipher

                fun checkForBadOid(
                    oid: String
                ) = oid.isEmpty() || oid.contains("BROKEN", true) || oid.contains("OLD", true)

                if (checkForBadOid(oid)) null
                else {
                    val strippedCipher = oid.replace("OID.", "")
                    SecureAlgorithmsMapping.findMatch(strippedCipher)?.let { mapping ->
                        if (checkForBadOid(oid + mapping.algorithm)) return@mapNotNull null

                        CipherType(
                            cipher = oid,
                            name = mapping.algorithm
                        )
                    } ?: cipher
                }
            }?.sortedBy { it.name } ?: emptyList()

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

        fun getInstance(
            cipher: String,
            name: String = cipher
        ): CipherType = CipherType(
            cipher = cipher,
            name = name
        )
    }
}