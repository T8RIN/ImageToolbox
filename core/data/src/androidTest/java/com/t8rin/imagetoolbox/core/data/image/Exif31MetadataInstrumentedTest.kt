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
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.set
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Exif 3.1 integration tests through ImageToolbox [Metadata].
 *
 * Fixtures must be located in:
 * core/data/src/androidTest/assets/metadata_round_trip/
 */
@RunWith(AndroidJUnit4::class)
class Exif31MetadataInstrumentedTest {

    private lateinit var context: Context
    private lateinit var directory: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        directory = File(
            context.cacheDir,
            "exif31_${System.nanoTime()}"
        ).apply {
            check(mkdirs() || isDirectory)
        }

        assertEquals(
            "The test must cover all fourteen Exif 3.0/3.1 tags",
            14,
            EXIF_31_VALUES.size
        )
        assertTrue(
            "MetadataTag.entries does not contain all Exif 3.1 tags",
            MetadataTag.entries.containsAll(EXIF_31_VALUES.keys)
        )
    }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun unicodeAndExif31TagsRoundTripInEveryContainer() {
        testAssets().forEach { assetName ->
            val file = copyAsset(assetName, "unicode")

            file.openMetadata()
                .clearAllAttributes()
                .saveAttributes()

            val metadata = file.openMetadata()
            UTF8_EXISTING_TAG_VALUES.forEach { (tag, value) -> metadata[tag] = value }
            EXIF_31_VALUES.forEach { (tag, value) -> metadata[tag] = value }
            // A caller may set an older value afterwards. saveAttributes() must
            // restore the mandatory Exif 3.x wire version before serialization.
            metadata[MetadataTag.ExifVersion] = "0232"
            metadata.saveAttributes()

            val reopenedFileExif = ExifInterface(file)
            val reopenedFile = reopenedFileExif.toMetadata()
            val reopenedStream = file.openStreamMetadata()

            (UTF8_EXISTING_TAG_VALUES + EXIF_31_VALUES).forEach { (tag, expected) ->
                assertEquals("$assetName/File/${tag.key}", expected, reopenedFile[tag])
                assertEquals("$assetName/InputStream/${tag.key}", expected, reopenedStream[tag])
            }

            assertEquals(
                "$assetName/ExifVersion",
                "0300",
                reopenedFile[MetadataTag.ExifVersion]
            )

            UTF8_WIRE_TAGS.forEach { tag ->
                assertEquals(
                    "$assetName/${tag.key} must use TIFF type 129",
                    IFD_FORMAT_UTF8,
                    reflectedAttribute(reopenedFileExif, tag.key).format
                )
            }

            val learning = reflectedAttribute(
                reopenedFileExif,
                MetadataTag.LearningOptOutIn.key
            )
            assertEquals(
                "$assetName/LearningOptOutIn format",
                IFD_FORMAT_UNDEFINED,
                learning.format
            )
            assertArrayEquals(
                "$assetName/LearningOptOutIn raw value",
                byteArrayOf(0, 2, 1, 1, 2, 0, 3, 2, 4, 0),
                learning.bytes
            )
        }
    }

    @Test
    fun asciiUsesClassicStringWhileUtf8OnlyFieldUsesType129() {
        testAssets().forEach { assetName ->
            val file = copyAsset(assetName, "ascii")

            file.openMetadata()
                .clearAllAttributes()
                .saveAttributes()

            file.openMetadata()
                .apply {
                    this[MetadataTag.ImageTitle] = "ASCII title"
                    this[MetadataTag.DevelopmentTypeDescription] = "ASCII description"
                }
                .saveAttributes()

            val reopened = ExifInterface(file)
            assertEquals(
                "$assetName/ImageTitle",
                IFD_FORMAT_STRING,
                reflectedAttribute(reopened, MetadataTag.ImageTitle.key).format
            )
            assertEquals(
                "$assetName/DevelopmentTypeDescription",
                IFD_FORMAT_UTF8,
                reflectedAttribute(
                    reopened,
                    MetadataTag.DevelopmentTypeDescription.key
                ).format
            )
            assertEquals(
                "$assetName/ExifVersion",
                "0300",
                reopened.toMetadata()[MetadataTag.ExifVersion]
            )
        }
    }

    @Test
    fun malformedExif31ValuesDoNotReplaceValidValues() {
        testAssets().forEach { assetName ->
            val file = copyAsset(assetName, "learning_validation")
            val valid = "0,2,1,1,2,0,3,2,4,0"

            file.openMetadata()
                .apply { this[MetadataTag.LearningOptOutIn] = valid }
                .saveAttributes()

            file.openMetadata()
                .apply {
                    // Usage 0 is duplicated, which Exif 3.x does not allow.
                    this[MetadataTag.LearningOptOutIn] = "0,2,0,1"
                }
                .saveAttributes()

            assertEquals(
                "$assetName must preserve the previous valid LearningOptOutIn",
                valid,
                file.openMetadata()[MetadataTag.LearningOptOutIn]
            )

            file.openMetadata()
                .apply {
                    this[MetadataTag.DevelopmentType] = "1,2"
                    this[MetadataTag.DistortionCorrection] = "1"
                }
                .saveAttributes()

            file.openMetadata()
                .apply {
                    this[MetadataTag.DevelopmentType] = "1"
                    this[MetadataTag.DistortionCorrection] = "2"
                }
                .saveAttributes()

            val reopened = file.openMetadata()
            assertEquals(
                "$assetName must reject malformed DevelopmentType",
                "1,2",
                reopened[MetadataTag.DevelopmentType]
            )
            assertEquals(
                "$assetName must reject correction values outside 0..1",
                "1",
                reopened[MetadataTag.DistortionCorrection]
            )
        }
    }

    private fun testAssets(): List<String> =
        InstrumentationRegistry.getInstrumentation().context.assets
            .list(ASSET_DIRECTORY)
            .orEmpty()
            .filter { name ->
                name.substringAfterLast('.', "").lowercase() in SUPPORTED_EXTENSIONS
            }
            .sorted()
            .also { names ->
                check(names.isNotEmpty()) {
                    "No fixtures in core/data/src/androidTest/assets/$ASSET_DIRECTORY/"
                }
            }

    private fun copyAsset(name: String, scenario: String): File {
        val outputDirectory = File(directory, scenario).apply {
            check(mkdirs() || isDirectory)
        }
        val destination = File(outputDirectory, name)
        InstrumentationRegistry.getInstrumentation().context.assets
            .open("$ASSET_DIRECTORY/$name").use { input ->
            destination.outputStream().buffered().use(input::copyTo)
        }
        check(destination.length() > 0L)
        return destination
    }

    private fun File.openMetadata(): Metadata =
        ExifInterface(this).toMetadata()

    private fun File.openStreamMetadata(): Metadata =
        inputStream().buffered().use { input ->
            ExifInterface(input).toMetadata()
        }

    @Suppress("UNCHECKED_CAST")
    private fun reflectedAttribute(
        exif: ExifInterface,
        key: String
    ): ReflectedAttribute {
        val attributesField = ExifInterface::class.java
            .getDeclaredField("mAttributes")
            .apply { isAccessible = true }

        val maps = attributesField.get(exif) as Array<HashMap<String, Any>>
        val attribute = maps.firstNotNullOfOrNull { it[key] }
            ?: error("Attribute $key was not found")

        val formatField = attribute.javaClass
            .getDeclaredField("format")
            .apply { isAccessible = true }
        val bytesField = attribute.javaClass
            .getDeclaredField("bytes")
            .apply { isAccessible = true }

        return ReflectedAttribute(
            format = formatField.getInt(attribute),
            bytes = (bytesField.get(attribute) as ByteArray).copyOf()
        )
    }

    private data class ReflectedAttribute(
        val format: Int,
        val bytes: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ReflectedAttribute

            if (format != other.format) return false
            if (!bytes.contentEquals(other.bytes)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = format
            result = 31 * result + bytes.contentHashCode()
            return result
        }
    }

    private companion object {
        const val ASSET_DIRECTORY = "metadata_round_trip"
        const val IFD_FORMAT_STRING = 2
        const val IFD_FORMAT_UNDEFINED = 7
        const val IFD_FORMAT_UTF8 = 129

        val SUPPORTED_EXTENSIONS = setOf(
            "jpg", "jpeg", "png", "webp", "avif", "heic", "heif",
            "jxl", "tif", "tiff", "jp2", "j2k"
        )

        val UTF8_EXISTING_TAG_VALUES = linkedMapOf(
            MetadataTag.ImageDescription to "Описание — وصف — 説明 🖼️",
            MetadataTag.Make to "Производитель — الشركة — 製造商",
            MetadataTag.Model to "Модель 设备 📷",
            MetadataTag.Software to "ImageToolbox — برنامج — 軟體",
            MetadataTag.Artist to "Художник — الفنان — 作者",
            MetadataTag.Copyright to "© 2026 Malik — الحقوق — 著作權",
            MetadataTag.CameraOwnerName to "Владелец — المالك — 所有者",
            MetadataTag.LensMake to "Оптика — عدسة — 鏡頭",
            MetadataTag.LensModel to "Модель объектива — 鏡頭型號"
        )

        val EXIF_31_VALUES = linkedMapOf(
            MetadataTag.LearningOptOutIn to "0,2,1,1,2,0,3,2,4,0",
            MetadataTag.DevelopmentType to "1,2",
            MetadataTag.DevelopmentTypeDescription to
                    "Проявка 🎞️ — تطوير — 現像",
            MetadataTag.DistortionCorrection to "1",
            MetadataTag.ChromaticAberrationCorrection to "1",
            MetadataTag.ShadingCorrection to "1",
            MetadataTag.NoiseReduction to "1",
            MetadataTag.ImageTitle to "Заголовок 📷 — العنوان — 標題",
            MetadataTag.Photographer to "Малик — مصور — 摄影师",
            MetadataTag.ImageEditor to "Редактор ✨ — محرر — 編輯者",
            MetadataTag.CameraFirmware to "Прошивка 3.1 — 固件",
            MetadataTag.RawDevelopingSoftware to "RAW проявка — تطوير",
            MetadataTag.ImageEditingSoftware to "ImageToolbox редактор 🖼️",
            MetadataTag.MetadataEditingSoftware to "ImageToolbox Metadata 3.1"
        )

        val UTF8_WIRE_TAGS =
            UTF8_EXISTING_TAG_VALUES.keys +
                    EXIF_31_VALUES.keys.filterNot { tag ->
                        tag == MetadataTag.LearningOptOutIn ||
                                tag == MetadataTag.DevelopmentType ||
                                tag == MetadataTag.DistortionCorrection ||
                                tag == MetadataTag.ChromaticAberrationCorrection ||
                                tag == MetadataTag.ShadingCorrection ||
                                tag == MetadataTag.NoiseReduction
                    }
    }
}
