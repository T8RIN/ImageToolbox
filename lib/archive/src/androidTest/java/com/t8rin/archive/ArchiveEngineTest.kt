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
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPOutputStream

@RunWith(AndroidJUnit4::class)
class ArchiveEngineTest {

    @Test
    fun roundTripsEveryWritableFormat() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = "ImageToolbox archive engine".toByteArray()

        ArchiveFormat.entries.forEach { format ->
            val archive = File(context.cacheDir, "round_trip.${format.extension}")
            try {
                try {
                    archive.outputStream().use { output ->
                        ArchiveEngine.create(
                            format = format,
                            sources = listOf(
                                ArchiveSource(
                                    name = "folder/page10.txt",
                                    size = expected.size.toLong(),
                                    openStream = { ByteArrayInputStream(expected) }
                                )
                            ),
                            outputStream = output
                        )
                    }
                    var extracted: ByteArray? = null
                    ParcelFileDescriptor.open(
                        archive,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    ).use { input ->
                        assertEquals(
                            ArchiveEncryptionStatus.None,
                            ArchiveEngine.encryptionStatus(input.fd)
                        )
                    }
                    ParcelFileDescriptor.open(
                        archive,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    ).use { input ->
                        val count = ArchiveEngine.extract(
                            inputFileDescriptor = input.fd,
                            onEntry = { entry, writeData ->
                                if (!entry.isDirectory && entry.path.endsWith("page10.txt")) {
                                    extracted = ByteArrayOutputStream().use { output ->
                                        writeData(output)
                                        output.toByteArray()
                                    }
                                }
                            }
                        )
                        check(count > 0)
                    }
                    assertArrayEquals(expected, extracted)
                } catch (throwable: Throwable) {
                    throw AssertionError(
                        "Failed round-trip for ${format.title}",
                        throwable
                    )
                }
            } finally {
                archive.delete()
            }
        }
    }

    @Test
    fun detectsAndExtractsPasswordProtectedFormats() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = "Password-protected archive".toByteArray()
        val passphrase = "correct horse battery staple"

        ArchiveFormat.entries.filter(ArchiveFormat::supportsEncryption).forEach { format ->
            val archive = File(context.cacheDir, "encrypted.${format.extension}")
            try {
                archive.outputStream().use { output ->
                    ArchiveEngine.create(
                        format = format,
                        sources = listOf(
                            ArchiveSource(
                                name = "secret.txt",
                                size = expected.size.toLong(),
                                openStream = { ByteArrayInputStream(expected) }
                            )
                        ),
                        outputStream = output,
                        passphrase = passphrase
                    )
                }
                ParcelFileDescriptor.open(
                    archive,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ).use { input ->
                    assertEquals(
                        ArchiveEncryptionStatus.PasswordRequired,
                        ArchiveEngine.encryptionStatus(input.fd)
                    )
                }

                var extracted: ByteArray? = null
                ParcelFileDescriptor.open(
                    archive,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ).use { input ->
                    ArchiveEngine.extract(
                        inputFileDescriptor = input.fd,
                        passphrase = passphrase,
                        onEntry = { entry, writeData ->
                            if (!entry.isDirectory) {
                                extracted = ByteArrayOutputStream().use { output ->
                                    writeData(output)
                                    output.toByteArray()
                                }
                            }
                        }
                    )
                }
                assertArrayEquals(expected, extracted)
            } catch (throwable: Throwable) {
                throw AssertionError(
                    "Failed encrypted round-trip for ${format.title}",
                    throwable
                )
            } finally {
                archive.delete()
            }
        }
    }

    @Test
    fun detectsSevenZipWithEncryptedHeaders() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val archive = File(context.cacheDir, "encrypted_headers.7z")
        try {
            archive.writeBytes(Base64.decode(EncryptedSevenZip, Base64.DEFAULT))
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertEquals(
                    ArchiveEncryptionStatus.Unsupported,
                    ArchiveEngine.encryptionStatus(input.fd)
                )
            }
        } finally {
            archive.delete()
        }
    }

    @Test
    fun limitsActualSizeForStreamsWithoutDeclaredSize() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val archive = File(context.cacheDir, "size_limit.gz")
        try {
            GZIPOutputStream(archive.outputStream()).use { output ->
                output.write(ByteArray(4096))
            }

            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                assertThrows(IllegalStateException::class.java) {
                    ArchiveEngine.extract(
                        inputFileDescriptor = input.fd,
                        forceRawFormat = true,
                        limits = ExtractionLimits(
                            maxEntries = 10,
                            maxEntrySizeBytes = 1024,
                            maxTotalSizeBytes = 1024
                        ),
                        onEntry = { _, writeData ->
                            writeData(ByteArrayOutputStream())
                        }
                    )
                }
            }
        } finally {
            archive.delete()
        }
    }
}

private const val EncryptedSevenZip =
    "N3q8ryccAAQ0kWamwAAAAAAAAAAwAAAAAAAAAM+JlTxMOi/JG1L1IUsFi36jAU3Q+Hi7xQUf9/w2" +
            "008PaS/z9HdBr2s4qTfEOcXN/OwiBgJAGJospJXaIWg86a1Z2lFH2UP6JRgsCD9z5hg4fp11DrFs" +
            "2XX3Ibk77rlzhECVr91WUr1WHDvKcPzz65m2k0uszXGLxWQL0BkyIYtqtKP6rgVaNzjgP45y4Xy" +
            "h5R3eNPLcMH0yIA6QfMwDM8BdOhblThp7/nxtncAUkVdB7Gzg99IxucWyWlgAc/L8tXvsr9YXBi" +
            "ABCYCgAAcLAQABJAbxBwESUw8ccJmnjLAuEYpfSI3tlhQDDICSCgESkd0NAAA="
