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

package com.t8rin.archive

import android.os.ParcelFileDescriptor
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class RarEncryptionTest {

    @Test
    fun reportsUnsupportedRar5Encryption() = assertUnsupportedEncryption(EncryptedRar, "junrar")

    @Test
    fun reportsUnsupportedRar4DataEncryption() =
        assertUnsupportedEncryption(EncryptedRar4Data, "12345678")

    @Test
    fun reportsUnsupportedRar4HeaderEncryption() =
        assertUnsupportedEncryption(EncryptedRar4Headers, "12345678")

    private fun assertUnsupportedEncryption(fixture: String, passphrase: String) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val archive = File(context.cacheDir, "encrypted.rar")
        try {
            archive.writeBytes(Base64.decode(fixture, Base64.DEFAULT))
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertEquals(
                    ArchiveEncryptionStatus.Unsupported,
                    ArchiveEngine.encryptionStatus(
                        inputFileDescriptor = input.fd,
                        preferRar = true
                    )
                )
            }
            listOf(passphrase, "wrong password").forEach { passphrase ->
                ParcelFileDescriptor.open(
                    archive,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ).use { input ->
                    assertFalse(
                        ArchiveEngine.verifyPassphrase(
                            inputFileDescriptor = input.fd,
                            passphrase = passphrase,
                            preferRar = true
                        )
                    )
                }
            }
        } finally {
            archive.delete()
        }
    }
}