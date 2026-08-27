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
import java.io.FileOutputStream
import java.util.zip.GZIPOutputStream
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class ArchiveEngineTest {

    @Test
    fun roundTripsEveryWritableFormat() {
        val expected = "ImageToolbox archive engine".toByteArray()

        ArchiveFormat.entries.forEach { format ->
            assertRoundTrip(format = format, expected = expected)
        }
    }

    @Test
    fun roundTripsBrotliAcrossNativeBufferBoundaries() {
        val expected = Random(42).nextBytes(512 * 1024)

        listOf(ArchiveFormat.Brotli, ArchiveFormat.TarBrotli).forEach { format ->
            assertRoundTrip(format = format, expected = expected)
        }
    }

    @Test
    fun roundTripsEveryZipCompressionMethod() {
        val expected = "ZIP compression methods".toByteArray()

        ZipCompressionMethod.entries.forEach { method ->
            assertRoundTrip(
                format = ArchiveFormat.Zip,
                expected = expected,
                zipCompressionMethod = method
            )
        }
    }

    @Test
    fun roundTripsEverySevenZipCompressionMethod() {
        val expected = "7Z compression methods".toByteArray()

        SevenZipCompressionMethod.entries.forEach { method ->
            assertRoundTrip(
                format = ArchiveFormat.SevenZip,
                expected = expected,
                sevenZipCompressionMethod = method
            )
        }
    }

    @Test
    fun roundTripsMultipleEntriesForEveryContainerFormat() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = mapOf(
            "first.txt" to "First entry".toByteArray(),
            "second.txt" to "Second entry".toByteArray()
        )

        ArchiveFormat.entries.filter(ArchiveFormat::supportsMultipleFiles).forEach { format ->
            val archive = File(context.cacheDir, "multiple.${format.extension}")
            try {
                try {
                    archive.outputStream().use { output ->
                        ArchiveEngine.create(
                            format = format,
                            sources = expected.map { (name, bytes) ->
                                ArchiveSource(
                                    name = name,
                                    size = bytes.size.toLong(),
                                    openStream = { ByteArrayInputStream(bytes) }
                                )
                            },
                            outputStream = output
                        )
                    }
                    val extracted = mutableMapOf<String, ByteArray>()
                    ParcelFileDescriptor.open(
                        archive,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    ).use { input ->
                        ArchiveEngine.extract(
                            inputFileDescriptor = input.fd,
                            forceBrotli = format.isBrotli,
                            onEntry = { entry, writeData ->
                                if (!entry.isDirectory) {
                                    extracted[entry.path] = ByteArrayOutputStream().use { output ->
                                        writeData(output)
                                        output.toByteArray()
                                    }
                                }
                            }
                        )
                    }
                    assertEquals(expected.keys, extracted.keys)
                    expected.forEach { (name, bytes) ->
                        assertArrayEquals(bytes, extracted[name])
                    }
                } catch (throwable: Throwable) {
                    throw AssertionError(
                        "Failed multiple-entry round-trip for ${format.title}",
                        throwable
                    )
                }
            } finally {
                archive.delete()
            }
        }
    }

    @Test
    fun rawFormatsRejectMultipleEntries() {
        ArchiveFormat.entries.filter(ArchiveFormat::isRaw).forEach { format ->
            assertThrows(IllegalArgumentException::class.java) {
                ArchiveEngine.create(
                    format = format,
                    sources = listOf(
                        ArchiveSource("first", 1) { ByteArrayInputStream(byteArrayOf(1)) },
                        ArchiveSource("second", 1) { ByteArrayInputStream(byteArrayOf(2)) }
                    ),
                    outputStream = ByteArrayOutputStream()
                )
            }
        }
    }

    @Test
    fun zipMethodsWithoutWorkingAesRejectPassphrases() {
        ZipCompressionMethod.entries.filterNot(ZipCompressionMethod::supportsEncryption)
            .forEach { method ->
                assertThrows(IllegalArgumentException::class.java) {
                    ArchiveEngine.create(
                        format = ArchiveFormat.Zip,
                        sources = listOf(
                            ArchiveSource("secret", 1) {
                                ByteArrayInputStream(byteArrayOf(1))
                            }
                        ),
                        outputStream = ByteArrayOutputStream(),
                        zipCompressionMethod = method,
                        passphrase = "password"
                    )
                }
            }
    }

    @Test
    fun sevenZipMethodsWithoutWorkingAesRejectPassphrases() {
        SevenZipCompressionMethod.entries
            .filterNot(SevenZipCompressionMethod::supportsEncryption)
            .forEach { method ->
                assertThrows(IllegalArgumentException::class.java) {
                    ArchiveEngine.create(
                        format = ArchiveFormat.SevenZip,
                        sources = listOf(
                            ArchiveSource("secret", 1) {
                                ByteArrayInputStream(byteArrayOf(1))
                            }
                        ),
                        sevenZipCompressionMethod = method,
                        passphrase = "password"
                    )
                }
            }
    }

    @Test
    fun detectsAndExtractsPasswordProtectedFormats() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = "Password-protected archive".toByteArray()
        val passphrase = "correct horse battery staple"

        ZipCompressionMethod.entries.filter(ZipCompressionMethod::supportsEncryption)
            .forEach { method ->
                val format = ArchiveFormat.Zip
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
                        zipCompressionMethod = method,
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
                    "Failed encrypted round-trip for ${format.title} ($method)",
                    throwable
                )
            } finally {
                archive.delete()
            }
        }
    }

    @Test
    fun createsDetectsAndExtractsEncryptedSevenZip() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val expected = "Encrypted 7Z content".toByteArray()
        val passphrase = "7z password"

        SevenZipCompressionMethod.entries
            .filter(SevenZipCompressionMethod::supportsEncryption)
            .forEach { method ->
                val archive = File(context.cacheDir, "encrypted-$method.7z")
                try {
                    FileOutputStream(archive).channel.use { output ->
                        ArchiveEngine.create(
                            format = ArchiveFormat.SevenZip,
                            sources = listOf(
                                ArchiveSource("secret.txt", expected.size.toLong()) {
                                    ByteArrayInputStream(expected)
                                }
                            ),
                            outputChannel = output,
                            sevenZipCompressionMethod = method,
                            passphrase = passphrase
                        )
                    }
                    ParcelFileDescriptor.open(
                        archive,
                        ParcelFileDescriptor.MODE_READ_ONLY
                    ).use { input ->
                        assertEquals(
                            ArchiveEncryptionStatus.PasswordRequired,
                            ArchiveEngine.encryptionStatus(
                                inputFileDescriptor = input.fd,
                                preferSevenZip = true
                            )
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
                            preferSevenZip = true,
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
                    throw AssertionError("Failed encrypted 7Z round-trip for $method", throwable)
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
                    ArchiveEncryptionStatus.PasswordRequired,
                    ArchiveEngine.encryptionStatus(
                        inputFileDescriptor = input.fd,
                        preferSevenZip = true
                    )
                )
            }
        } finally {
            archive.delete()
        }
    }

    @Test
    fun detectsAndExtractsEncryptedRar() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val archive = File(context.cacheDir, "encrypted.rar")
        try {
            archive.writeBytes(Base64.decode(EncryptedRar, Base64.DEFAULT))
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

            var extractedName: String? = null
            var extractedSize = 0
            ParcelFileDescriptor.open(
                archive,
                ParcelFileDescriptor.MODE_READ_ONLY
            ).use { input ->
                ArchiveEngine.extract(
                    inputFileDescriptor = input.fd,
                    passphrase = "junrar",
                    preferRar = true,
                    onEntry = { entry, writeData ->
                        if (!entry.isDirectory) {
                            extractedName = entry.path
                            extractedSize = ByteArrayOutputStream().use { output ->
                                writeData(output)
                                output.size()
                            }
                        }
                    }
                )
            }
            assertEquals("file1.txt", extractedName)
            assertEquals(6, extractedSize)
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

    private fun assertRoundTrip(
        format: ArchiveFormat,
        expected: ByteArray,
        zipCompressionMethod: ZipCompressionMethod = ZipCompressionMethod.Deflate,
        sevenZipCompressionMethod: SevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2
    ) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
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
                        outputStream = output,
                        zipCompressionMethod = zipCompressionMethod,
                        sevenZipCompressionMethod = sevenZipCompressionMethod
                    )
                }
                var extracted: ByteArray? = null
                ParcelFileDescriptor.open(
                    archive,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ).use { input ->
                    assertEquals(
                        ArchiveEncryptionStatus.None,
                        ArchiveEngine.encryptionStatus(
                            inputFileDescriptor = input.fd,
                            forceRawFormat = format.isRaw,
                            forceBrotli = format.isBrotli
                        )
                    )
                }
                ParcelFileDescriptor.open(
                    archive,
                    ParcelFileDescriptor.MODE_READ_ONLY
                ).use { input ->
                    val count = ArchiveEngine.extract(
                        inputFileDescriptor = input.fd,
                        forceRawFormat = format.isRaw,
                        forceBrotli = format.isBrotli,
                        onEntry = { entry, writeData ->
                            if (
                                !entry.isDirectory &&
                                (format.isRaw || entry.path.endsWith("page10.txt"))
                            ) {
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
                    "Failed round-trip for ${format.title}, " +
                            "ZIP=$zipCompressionMethod, 7Z=$sevenZipCompressionMethod",
                    throwable
                )
            }
        } finally {
            archive.delete()
        }
    }
}

private val ArchiveFormat.isBrotli: Boolean
    get() = this == ArchiveFormat.Brotli || this == ArchiveFormat.TarBrotli

private const val EncryptedSevenZip =
    "N3q8ryccAAQ0kWamwAAAAAAAAAAwAAAAAAAAAM+JlTxMOi/JG1L1IUsFi36jAU3Q+Hi7xQUf9/w2" +
            "008PaS/z9HdBr2s4qTfEOcXN/OwiBgJAGJospJXaIWg86a1Z2lFH2UP6JRgsCD9z5hg4fp11DrFs" +
            "2XX3Ibk77rlzhECVr91WUr1WHDvKcPzz65m2k0uszXGLxWQL0BkyIYtqtKP6rgVaNzjgP45y4Xy" +
            "h5R3eNPLcMH0yIA6QfMwDM8BdOhblThp7/nxtncAUkVdB7Gzg99IxucWyWlgAc/L8tXvsr9YXBi" +
            "ABCYCgAAcLAQABJAbxBwESUw8ccJmnjLAuEYpfSI3tlhQDDICSCgESkd0NAAA="

private const val EncryptedRar =
    "UmFyIRoHAQAYOJrPIQQAAAEPprqRs1Vs70VeAnJr65GiUWzJnBs88EB6pEDZCDMNebpM1FRWRid" +
            "OoP8NEKunwvQXSE6qyWZSmFdTmJz5B4PRrGmc/9wgf07nAr0VnT/SUD7KGRm04mC2+uJap3b" +
            "ok3fPNwjtWnVbqxga+30ke8uVJYZkiuuGhz7dmPmsjcbbifv8JRtif4lMcFsoiFclxaKGgHW" +
            "EAQ5iUr3A418NqLr87fq2lB4LpFyCVjVgrfNS3Ou5IdI1MBz0SPemsbqYy9mjOR0uTISWgDq" +
            "gl9qBApSZxzov0pm4HbMDGpR2jEAzLnvZFtWMyUYPoVpguDM="
