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
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File

@RunWith(AndroidJUnit4::class)
class RarEngineTest {

    @Test
    fun listsAndExtractsUnencryptedRarAndCbr() = forEachArchive { archive ->
        // Archive tools selects RarEngine; CBR-to-PDF uses libarchive directly.
        listOf(true, false).forEach { preferRar ->
            archive.read { input ->
                assertEquals(
                    ArchiveEncryptionStatus.None,
                    ArchiveEngine.encryptionStatus(input.fd, preferRar = preferRar)
                )
            }
            archive.read { input ->
                assertEquals(
                    listOf(
                        ArchiveEntryInfo("pages", 0, true),
                        ArchiveEntryInfo("pages/page1.txt", 10, false),
                        ArchiveEntryInfo("pages/page2.txt", 11, false)
                    ),
                    ArchiveEngine.listEntries(input.fd, preferRar = preferRar)
                )
            }
            val extracted = mutableMapOf<String, ByteArray>()
            var chunks = 0
            var progress = 0
            archive.read { input ->
                assertEquals(
                    3,
                    ArchiveEngine.extract(
                        inputFileDescriptor = input.fd,
                        preferRar = preferRar,
                        onEntry = { entry, writeData ->
                            if (!entry.isDirectory) {
                                extracted[entry.path] = ByteArrayOutputStream().use { output ->
                                    writeData(output)
                                    output.toByteArray()
                                }
                            }
                        },
                        onChunk = { chunks++ },
                        onProgress = { progress++ }
                    )
                )
            }
            assertEquals(setOf("pages/page1.txt", "pages/page2.txt"), extracted.keys)
            assertArrayEquals("First page".toByteArray(), extracted["pages/page1.txt"])
            assertArrayEquals("Second page".toByteArray(), extracted["pages/page2.txt"])
            assertEquals(3, progress)
            assertTrue(chunks > 0)
        }
    }

    @Test
    fun extractsSelectedEntryToCacheAfterSkippingOthers() = forEachArchive { archive ->
        val outputFile = File(archive.parentFile, "selected.txt")
        try {
            archive.read { input ->
                ArchiveEngine.extract(
                    inputFileDescriptor = input.fd,
                    preferRar = true,
                    onEntry = { entry, writeData ->
                        if (entry.path == "pages/page2.txt") {
                            outputFile.outputStream().use(writeData)
                        }
                    }
                )
            }
            assertEquals("Second page", outputFile.readText())
        } finally {
            outputFile.delete()
        }
    }

    @Test
    fun enforcesRarExtractionAndListingLimits() = forEachArchive { archive ->
        archive.read { input ->
            assertThrows(IllegalStateException::class.java) {
                ArchiveEngine.listEntries(
                    inputFileDescriptor = input.fd,
                    preferRar = true,
                    limits = ExtractionLimits(maxEntries = 1)
                )
            }
        }
        listOf(
            ExtractionLimits(maxEntries = 1),
            ExtractionLimits(maxEntrySizeBytes = 9),
            ExtractionLimits(maxTotalSizeBytes = 20)
        ).forEach { limits ->
            archive.read { input ->
                assertThrows(IllegalStateException::class.java) {
                    ArchiveEngine.extract(
                        inputFileDescriptor = input.fd,
                        preferRar = true,
                        limits = limits,
                        onEntry = { entry, writeData ->
                            if (!entry.isDirectory) writeData(ByteArrayOutputStream())
                        }
                    )
                }
            }
        }
    }

    private fun forEachArchive(block: (File) -> Unit) {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        listOf(StoredRar4, StoredRar5).forEachIndexed { index, fixture ->
            listOf("rar", "cbr").forEach { extension ->
                val archive = File(context.cacheDir, "stored_rar$index.$extension")
                try {
                    archive.writeBytes(Base64.decode(fixture, Base64.DEFAULT))
                    block(archive)
                } catch (throwable: Throwable) {
                    throw AssertionError("Failed ${archive.name}", throwable)
                } finally {
                    archive.delete()
                }
            }
        }
    }

    private fun File.read(block: (ParcelFileDescriptor) -> Unit) {
        ParcelFileDescriptor.open(this, ParcelFileDescriptor.MODE_READ_ONLY).use(block)
    }
}