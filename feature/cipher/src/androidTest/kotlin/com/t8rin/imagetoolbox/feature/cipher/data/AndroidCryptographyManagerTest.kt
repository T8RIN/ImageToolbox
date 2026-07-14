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

package com.t8rin.imagetoolbox.feature.cipher.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.CipherType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import java.security.Provider
import java.security.Security
import kotlin.coroutines.CoroutineContext

@RunWith(AndroidJUnit4::class)
class AndroidCryptographyManagerTest {

    @Test
    fun allVisibleCiphersCanEncryptAndDecrypt() {
        val manager = AndroidCryptographyManager(TestDispatchersHolder)
        val unsupported = CipherType.entries
            .filterNot(::isExtraExcluded)
            .filterNot { type ->
                runCatching {
                    val encrypted = runBlocking {
                        manager.encrypt(TEST_DATA, TEST_PASSWORD, type)
                    }
                    val decrypted = runBlocking {
                        manager.decrypt(encrypted, TEST_PASSWORD, type)
                    }
                    check(decrypted.contentEquals(TEST_DATA))
                }.isSuccess
            }
            .map { it.cipher }
            .sorted()

        assertTrue(
            "Add unsupported ciphers to CipherType.BROKEN:\n${unsupported.joinToString("\n")}",
            unsupported.isEmpty()
        )
    }

    private companion object {
        private val TEST_DATA =
            "ImageToolbox Add unsupported ciphers to CipherType.BROKEN".repeat(60)
                .encodeToByteArray()
        private const val TEST_PASSWORD = "ImageToolbox"

        @JvmStatic
        @BeforeClass
        fun registerBouncyCastleProvider() {
            Security.addProvider(BouncyCastleWorkaroundProvider())
            CipherType.registerSecurityCiphers(
                Security.getAlgorithms("Cipher")
                    .filterNot { cipher -> CipherType.BROKEN.any { cipher.contains(it, true) } }
                    .map(CipherType::getInstance)
            )
        }

        private fun isExtraExcluded(type: CipherType): Boolean =
            type.cipher == "DES"
                    || type.name == "DES/CBC"
                    || type.name == "THREEFISH-512"
                    || type.name == "THREEFISH-1024"
                    || type.name == "CCM"
    }
}

private class BouncyCastleWorkaroundProvider(
    bouncyCastleProvider: Provider = BouncyCastleProvider()
) : Provider(
    "BC_TEST",
    bouncyCastleProvider.version,
    bouncyCastleProvider.info
) {
    init {
        bouncyCastleProvider.entries.forEach { (key, value) ->
            put(key.toString(), value.toString())
        }
    }
}

private object TestDispatchersHolder : DispatchersHolder {
    override val uiDispatcher: CoroutineContext = Dispatchers.Unconfined
    override val ioDispatcher: CoroutineContext = Dispatchers.Unconfined
    override val encodingDispatcher: CoroutineContext = Dispatchers.Unconfined
    override val decodingDispatcher: CoroutineContext = Dispatchers.Unconfined
    override val defaultDispatcher: CoroutineContext = Dispatchers.Unconfined
}
