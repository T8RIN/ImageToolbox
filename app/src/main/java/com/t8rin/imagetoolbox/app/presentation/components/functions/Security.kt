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

package com.t8rin.imagetoolbox.app.presentation.components.functions

import com.t8rin.imagetoolbox.core.domain.model.CipherType
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.logger.makeLog
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.DefaultAlgorithmNameFinder
import java.security.Provider
import java.security.Security

internal fun registerSecurityProviders() {
    initBouncyCastle()

    HashingType.registerSecurityMessageDigests(
        Security.getAlgorithms("MessageDigest").filterNotNull()
    )

    val finder = DefaultAlgorithmNameFinder()

    CipherType.registerSecurityCiphers(
        Security.getAlgorithms("Cipher").filterNotNull().mapNotNull { cipher ->
            if (CipherType.BROKEN.any { cipher.contains(it, true) }) return@mapNotNull null

            val oid = cipher.removePrefix("OID.")
            if (oid.all { it.isDigit() || it.isWhitespace() || it == '.' }) {
                CipherType.getInstance(
                    cipher = cipher,
                    name = finder.getAlgorithmName(
                        ASN1ObjectIdentifier(oid)
                    )
                )
            } else {
                CipherType.getInstance(
                    cipher = cipher
                )
            }.also {
                val extraExclude = it.cipher == "DES"
                        || it.name == "DES/CBC"
                        || it.name == "THREEFISH-512"
                        || it.name == "THREEFISH-1024"
                        || it.name == "CCM"

                if (extraExclude) return@mapNotNull null
            }
        }
    )
}

private fun initBouncyCastle() {
    if (Security.getProvider(WORKAROUND_NAME) != null) return

    try {
        logProviders("OLD")

        Security.addProvider(BouncyCastleWorkaroundProvider())

        logProviders("NEW")
    } catch (e: Exception) {
        e.makeLog()
        "Failed to register BouncyCastleWorkaroundProvider".makeLog()
    }
}

private fun logProviders(tag: String): Int {
    val providers = Security.getProviders()
    providers.forEachIndexed { index, provider ->
        "$tag [$index]: ${provider.name} - ${provider.info}".makeLog("Providers")
    }
    return providers.size
}

private class BouncyCastleWorkaroundProvider(
    bouncyCastleProvider: Provider = BouncyCastleProvider()
) : Provider(
    WORKAROUND_NAME,
    bouncyCastleProvider.version,
    bouncyCastleProvider.info
) {
    init {
        for ((key, value) in bouncyCastleProvider.entries) {
            put(key.toString(), value.toString())
        }
    }
}

private const val WORKAROUND_NAME = "BC_WORKAROUND"