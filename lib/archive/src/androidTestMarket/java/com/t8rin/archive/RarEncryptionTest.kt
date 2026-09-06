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
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(AndroidJUnit4::class)
class RarEncryptionTest {

    @Test
    fun detectsAndExtractsEncryptedRar5() = assertEncryptedRar(
        fixture = EncryptedRar,
        passphrase = "junrar",
        expected = mapOf("file1.txt" to 6L)
    )

    @Test
    fun detectsAndExtractsRar4WithEncryptedData() = assertEncryptedRar(
        fixture = EncryptedRar4Data,
        passphrase = "12345678",
        expected = mapOf("foo.txt" to 16L, "bar.txt" to 16L)
    )

    @Test
    fun detectsAndExtractsRar4WithEncryptedHeaders() = assertEncryptedRar(
        fixture = EncryptedRar4Headers,
        passphrase = "12345678",
        expected = mapOf("foo.txt" to 16L, "bar.txt" to 16L)
    )

    private fun assertEncryptedRar(
        fixture: String,
        passphrase: String,
        expected: Map<String, Long>
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val archive = File(context.cacheDir, "encrypted.rar")
        try {
            archive.writeBytes(Base64.decode(fixture, Base64.DEFAULT))
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertEquals(
                    ArchiveEncryptionStatus.PasswordRequired,
                    ArchiveEngine.encryptionStatus(
                        inputFileDescriptor = input.fd,
                        preferRar = true
                    )
                )
            }
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertTrue(
                    ArchiveEngine.verifyPassphrase(
                        inputFileDescriptor = input.fd,
                        passphrase = passphrase,
                        preferRar = true
                    )
                )
            }
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertFalse(
                    ArchiveEngine.verifyPassphrase(
                        inputFileDescriptor = input.fd,
                        passphrase = "wrong password",
                        preferRar = true
                    )
                )
            }

            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertEquals(
                    expected,
                    ArchiveEngine.listEntries(
                        inputFileDescriptor = input.fd,
                        passphrase = passphrase,
                        preferRar = true
                    ).associate { it.path to it.size }
                )
            }
            val extracted = mutableMapOf<String, Long>()
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                ArchiveEngine.extract(
                    inputFileDescriptor = input.fd,
                    passphrase = passphrase,
                    preferRar = true,
                    onEntry = { entry, writeData ->
                        if (!entry.isDirectory) {
                            extracted[entry.path] = ByteArrayOutputStream().use { output ->
                                writeData(output)
                                output.size().toLong()
                            }
                        }
                    }
                )
            }
            assertEquals(expected, extracted)
        } finally {
            archive.delete()
        }
    }
}