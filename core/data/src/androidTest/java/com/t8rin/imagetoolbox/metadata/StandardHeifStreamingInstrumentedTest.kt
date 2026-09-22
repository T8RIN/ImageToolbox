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

package com.t8rin.imagetoolbox.core.data.image

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.set
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.RandomAccessFile

/**
 * Verifies the two v7 guarantees through ImageToolbox Metadata:
 *
 * 1. HEIC/HEIF/AVIF contain a standard Exif item, not only the private recovery UUID.
 * 2. Large extended-format files can be saved without creating a byte[] for the whole file.
 *
 * Required fixtures:
 * core/data/src/androidTest/assets/metadata_round_trip/
 */
@RunWith(AndroidJUnit4::class)
class StandardHeifStreamingInstrumentedTest {

    private lateinit var context: Context
    private lateinit var directory: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        directory = File(
            context.cacheDir,
            "standard_heif_streaming_${System.nanoTime()}"
        ).apply {
            check(mkdirs() || isDirectory)
        }
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun heifAndAvifRemainReadableAfterPrivateFallbackIsDisabled() {
        listOf("rich.heic", "rich.heif", "rich.avif").forEach { assetName ->
            val file = copyAsset(assetName, "standard")
            val expected = writeTestMetadata(file, assetName)

            assertMetadata("$assetName/normal", file.openMetadata(), expected)

            // Damage only ImageToolbox's private UUID user type. The box remains structurally
            // valid, but our fallback reader no longer recognizes it. A successful read now proves
            // that the normal iinf/infe/iloc Exif item was written correctly.
            corruptLastPrivateFallbackUuid(file)

            assertMetadata("$assetName/standard-item", file.openMetadata(), expected)
            assertMetadata("$assetName/standard-item-stream", file.openStreamMetadata(), expected)
        }
    }

    @Test
    fun largeExtendedFormatFilesRoundTripThroughMetadata() {
        listOf(
            "rich.heic",
            "rich.avif",
            "rich.jxl",
            "rich.jp2",
            "rich.tiff"
        ).forEach { assetName ->
            val file = copyAsset(assetName, "large")
            enlargeWithoutAllocatingHeapBuffer(file, assetName, LARGE_PADDING_BYTES)

            val before = file.length()
            val expected = writeTestMetadata(file, "large-$assetName")
            val after = file.length()

            check(before >= LARGE_PADDING_BYTES) {
                "$assetName was not enlarged: $before"
            }
            check(after >= LARGE_PADDING_BYTES) {
                "$assetName was truncated while saving: $after"
            }

            assertMetadata("$assetName/large-file", file.openMetadata(), expected)
            assertMetadata("$assetName/large-stream", file.openStreamMetadata(), expected)
        }
    }

    private fun writeTestMetadata(
        file: File,
        suffix: String
    ): Map<MetadataTag, String> {
        val expected = linkedMapOf(
            MetadataTag.Make to "ImageToolbox streaming",
            MetadataTag.Model to "Standard-$suffix",
            MetadataTag.ImageDescription to "Standard HEIF item + streaming copy",
            MetadataTag.UserComment to "API 24 ✓ Кириллица 中文 $suffix"
        )

        file.openMetadata().apply {
            expected.forEach { (tag, value) -> this[tag] = value }
        }.saveAttributes()

        return expected
    }

    private fun assertMetadata(
        source: String,
        metadata: Metadata,
        expected: Map<MetadataTag, String>
    ) {
        expected.forEach { (tag, value) ->
            assertEquals("$source: ${tag.key}", value, metadata[tag])
        }
    }

    private fun enlargeWithoutAllocatingHeapBuffer(
        file: File,
        assetName: String,
        padding: Long
    ) {
        RandomAccessFile(file, "rw").use { random ->
            val originalLength = random.length()
            random.seek(originalLength)

            if (assetName.endsWith(".tif") || assetName.endsWith(".tiff")) {
                // Classic TIFF readers ignore unreferenced trailing bytes.
                random.setLength(originalLength + padding)
            } else {
                // A size=0 top-level free box consumes the rest of the file. The streaming writer
                // converts it to an explicit-size box before appending new metadata.
                random.writeInt(0)
                random.writeBytes("free")
                random.setLength(originalLength + 8L + padding)
            }
        }
    }

    private fun corruptLastPrivateFallbackUuid(file: File) {
        RandomAccessFile(file, "rw").use { random ->
            val inspectedLength = minOf(random.length(), UUID_SEARCH_WINDOW_BYTES)
            val start = random.length() - inspectedLength
            val tail = ByteArray(inspectedLength.toInt())
            random.seek(start)
            random.readFully(tail)

            val markerIndex = tail.lastIndexOf(PRIVATE_UUID_PREFIX)
            check(markerIndex >= 0) {
                "ImageToolbox private fallback UUID was not found in ${file.name}"
            }
            random.seek(start + markerIndex)
            random.writeByte('X'.code)
        }
    }

    private fun ByteArray.lastIndexOf(needle: ByteArray): Int {
        outer@ for (index in size - needle.size downTo 0) {
            for (offset in needle.indices) {
                if (this[index + offset] != needle[offset]) continue@outer
            }
            return index
        }
        return -1
    }

    private fun File.openMetadata(): Metadata =
        ExifInterface(this).toMetadata()

    private fun File.openStreamMetadata(): Metadata =
        inputStream().buffered().use { input ->
            ExifInterface(input).toMetadata()
        }

    private fun copyAsset(assetName: String, scenario: String): File {
        val scenarioDirectory = File(directory, scenario).apply {
            check(mkdirs() || isDirectory)
        }
        return File(scenarioDirectory, assetName).also { destination ->
            InstrumentationRegistry.getInstrumentation().context.assets
                .open("metadata_round_trip/$assetName").use { input ->
                destination.outputStream().buffered().use(input::copyTo)
            }
            check(destination.length() > 0L)
        }
    }

    private companion object {
        const val LARGE_PADDING_BYTES = 96L * 1024L * 1024L
        const val UUID_SEARCH_WINDOW_BYTES = 1024L * 1024L
        val PRIVATE_UUID_PREFIX = "ITBX-EXIF-V".encodeToByteArray()
    }
}
