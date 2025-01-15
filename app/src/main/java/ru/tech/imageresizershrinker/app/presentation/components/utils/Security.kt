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

package ru.tech.imageresizershrinker.app.presentation.components.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.asn1.ASN1ObjectIdentifier
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.DefaultAlgorithmNameFinder
import ru.tech.imageresizershrinker.core.domain.model.CipherType
import ru.tech.imageresizershrinker.core.domain.model.HashingType
import java.security.Security
import kotlin.text.all

internal fun registerSecurityProviders() {
    Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
    Security.addProvider(BouncyCastleProvider())

    HashingType.registerSecurityMessageDigests(
        Security.getAlgorithms("MessageDigest").filterNotNull()
    )

    CoroutineScope(Dispatchers.Default).launch {
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
}