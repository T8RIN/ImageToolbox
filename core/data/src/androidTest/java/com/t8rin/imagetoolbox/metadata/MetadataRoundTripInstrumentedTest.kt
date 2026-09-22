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

package com.t8rin.imagetoolbox.metadata

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.data.image.toMetadata
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.set
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import java.io.File

/**
 * Tests only the ImageToolbox [Metadata] abstraction after
 * [ExifInterface.toMetadata].
 *
 * The test contains three independent scenarios:
 *
 * 1. Save the existing metadata and verify it through fresh Metadata instances.
 * 2. Clear the metadata, save, reopen and verify the cleared state.
 * 3. Clear and save the file first, reopen it, write custom values to every
 *    writable [MetadataTag.entries] item, and verify protected tags stayed intact.
 *
 * Fixtures:
 *
 * core/data/src/androidTest/assets/metadata_round_trip/
 */
@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class MetadataRoundTripInstrumentedTest {

    private lateinit var context: Context
    private lateinit var workingDirectory: File

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        workingDirectory = File(
            context.cacheDir,
            "metadata_round_trip_${System.nanoTime()}"
        ).apply {
            check(mkdirs() || isDirectory) {
                "Could not create test directory: $absolutePath"
            }
        }

        check(CUSTOM_TAG_VALUES.keys == MetadataTag.entries.toSet()) {
            buildString {
                appendLine("CUSTOM_TAG_VALUES must cover MetadataTag.entries exactly.")
                appendLine(
                    "Missing: ${
                        (MetadataTag.entries.toSet() - CUSTOM_TAG_VALUES.keys)
                            .joinToString { it.key }
                    }"
                )
                append(
                    "Extra: ${
                        (CUSTOM_TAG_VALUES.keys - MetadataTag.entries.toSet())
                            .joinToString { it.key }
                    }"
                )
            }
        }
    }

    @After
    fun tearDown() {
        workingDirectory.deleteRecursively()
    }

    @Test
    fun test01_existingMetadataSurvivesSave() {
        runForEveryFixture(scenario = "preserve") { assetName, file ->
            val original = file.openMetadata().readEveryEntry()
            check(original.values.any { it != null }) {
                "$assetName contains no metadata readable through Metadata"
            }

            val metadata = file.openMetadata()
            original.forEach { (tag, value) ->
                if (value != null) metadata[tag] = value
            }
            metadata.saveAttributes()

            val fromFile = file.openMetadata().readEveryEntry()
            val fromStream = file.openStreamMetadata().readEveryEntry()
            val failures = mutableListOf<String>()

            original.forEach { (tag, expected) ->
                if (expected == null) return@forEach

                compareExistingValue(
                    source = "$assetName/File",
                    tag = tag,
                    expected = expected,
                    actual = fromFile[tag],
                    failures = failures
                )
                compareExistingValue(
                    source = "$assetName/InputStream",
                    tag = tag,
                    expected = expected,
                    actual = fromStream[tag],
                    failures = failures
                )
            }

            check(failures.isEmpty()) {
                failureReport(
                    title = "Existing metadata changed after save",
                    failures = failures,
                    expected = original,
                    fromFile = fromFile,
                    fromStream = fromStream
                )
            }
        }
    }

    @Test
    fun test02_metadataCanBeCleared() {
        runForEveryFixture(scenario = "clear") { assetName, file ->
            val before = file.openMetadata().readEveryEntry()

            file.openMetadata()
                .clearAllAttributes()
                .saveAttributes()

            val fromFile = file.openMetadata().readEveryEntry()
            val fromStream = file.openStreamMetadata().readEveryEntry()
            val failures = mutableListOf<String>()

            MetadataTag.entries.forEach { tag ->
                compareClearedValue(
                    assetName = assetName,
                    source = "$assetName/File",
                    tag = tag,
                    before = before[tag],
                    actual = fromFile[tag],
                    failures = failures
                )
                compareClearedValue(
                    assetName = assetName,
                    source = "$assetName/InputStream",
                    tag = tag,
                    before = before[tag],
                    actual = fromStream[tag],
                    failures = failures
                )
            }

            check(failures.isEmpty()) {
                failureReport(
                    title = "Metadata was not cleared correctly",
                    failures = failures,
                    expected = MetadataTag.entries.associateWith { null },
                    fromFile = fromFile,
                    fromStream = fromStream
                )
            }
        }
    }

    @Test
    fun test03_cleanFileAcceptsCustomValuesForEveryWritableMetadataTag() {
        runForEveryFixture(scenario = "custom_all_writable_tags") { assetName, file ->
            /*
             * Stage 1: physically save a cleared file, then reopen it.
             */
            file.openMetadata()
                .clearAllAttributes()
                .saveAttributes()

            assertFileIsStillPresent(assetName, "clear-before-custom-write", file)

            val baseline = file.openMetadata().readEveryEntry()
            val nonWritableTags = nonWritableTagsFor(assetName)
            val writableTags = MetadataTag.entries.filterNot(nonWritableTags::contains)

            val remainingAfterClear = baseline
                .filterValues { it != null }
                .filterKeys { tag ->
                    tag !in NON_REMOVABLE_OR_DERIVED_TAGS &&
                            tag !in nonWritableTags
                }

            check(remainingAfterClear.isEmpty()) {
                buildString {
                    appendLine("$assetName was not clean before writing custom tags:")
                    remainingAfterClear.forEach { (tag, value) ->
                        appendLine("  ${tag.key}=${value.quoted()}")
                    }
                }
            }

            /*
             * Stage 2: write a custom value to every tag that is actually writable
             * for this file. Structural TIFF tags and format-specific ORF tags are
             * deliberately excluded from custom writes.
             */
            val metadata = file.openMetadata()
            val expectedCustomValues = linkedMapOf<MetadataTag, String>()
            val rejected = mutableListOf<String>()

            writableTags.forEach { tag ->
                val requested = CUSTOM_TAG_VALUES.getValue(tag)
                metadata[tag] = requested

                /*
                 * ExifInterface can normalize accepted values in memory. The
                 * normalized value is the exact value expected after persistence.
                 */
                val accepted = metadata[tag]
                if (accepted == null) {
                    rejected += "${tag.key}: requested=${requested.quoted()}, accepted=<null>"
                } else {
                    expectedCustomValues[tag] = accepted
                }
            }

            check(rejected.isEmpty()) {
                buildString {
                    appendLine(
                        "$assetName rejected ${rejected.size} of " +
                                "${writableTags.size} writable custom tags before save:"
                    )
                    rejected.forEach { appendLine("  $it") }
                }
            }

            check(expectedCustomValues.keys == writableTags.toSet()) {
                "$assetName did not accept every writable MetadataTag"
            }

            metadata.saveAttributes()
            assertFileIsStillPresent(assetName, "custom-all-writable-tags", file)

            /*
             * Stage 3: compare custom writable tags exactly through completely new
             * Metadata wrappers. Non-writable tags must remain unchanged or absent.
             */
            val fromFile = file.openMetadata().readEveryEntry()
            val fromStream = file.openStreamMetadata().readEveryEntry()
            val failures = mutableListOf<String>()

            writableTags.forEach { tag ->
                val expectedValue = expectedCustomValues.getValue(tag)

                compareExact(
                    source = "$assetName/File",
                    tag = tag,
                    expected = expectedValue,
                    actual = fromFile[tag],
                    failures = failures
                )
                compareExact(
                    source = "$assetName/InputStream",
                    tag = tag,
                    expected = expectedValue,
                    actual = fromStream[tag],
                    failures = failures
                )
            }

            nonWritableTags.forEach { tag ->
                compareProtectedValue(
                    source = "$assetName/File",
                    tag = tag,
                    before = baseline[tag],
                    actual = fromFile[tag],
                    failures = failures
                )
                compareProtectedValue(
                    source = "$assetName/InputStream",
                    tag = tag,
                    before = baseline[tag],
                    actual = fromStream[tag],
                    failures = failures
                )
            }

            check(failures.isEmpty()) {
                failureReport(
                    title = "Custom writable-tags round-trip failed for $assetName",
                    failures = failures,
                    expected = expectedCustomValues,
                    fromFile = fromFile,
                    fromStream = fromStream
                )
            }

            Log.i(
                TAG,
                "CUSTOM PASS: $assetName — " +
                        "writable=${writableTags.size}, protected=${nonWritableTags.size}"
            )
        }
    }

    private fun runForEveryFixture(
        scenario: String,
        block: (assetName: String, file: File) -> Unit
    ) {
        val assetNames = testAssetNames()
        val failures = mutableListOf<String>()

        assetNames.forEach { assetName ->
            runCatching {
                val file = copyAssetToWritableFile(
                    assetName = assetName,
                    scenario = scenario
                )
                block(assetName, file)
                Log.i(TAG, "PASS [$scenario]: $assetName")
            }.onFailure { throwable ->
                failures += buildString {
                    append(assetName)
                    append(": ")
                    append(throwable.message ?: throwable::class.java.name)
                }
                Log.e(TAG, "FAIL [$scenario]: $assetName", throwable)
            }
        }

        if (failures.isNotEmpty()) {
            fail(
                buildString {
                    appendLine("Scenario '$scenario' failed:")
                    failures.forEach { appendLine("• $it") }
                }
            )
        }
    }

    private fun testAssetNames(): List<String> =
        InstrumentationRegistry.getInstrumentation().context.assets
            .list(ASSET_DIRECTORY)
            .orEmpty()
            .filter(::isSupportedTestImage)
            .sorted()
            .also { names ->
                if (names.isEmpty()) {
                    fail(
                        "No fixtures found in " +
                                "core/data/src/androidTest/assets/$ASSET_DIRECTORY/"
                    )
                }
            }

    private fun compareExistingValue(
        source: String,
        tag: MetadataTag,
        expected: String,
        actual: String?,
        failures: MutableList<String>
    ) {
        if (tag in VOLATILE_LAYOUT_OFFSET_TAGS) {
            if (actual == null) {
                failures += "$source: ${tag.key} disappeared"
            }
            return
        }

        compareExact(source, tag, expected, actual, failures)
    }

    private fun compareClearedValue(
        assetName: String,
        source: String,
        tag: MetadataTag,
        before: String?,
        actual: String?,
        failures: MutableList<String>
    ) {
        if (actual == null) return

        val maySurvive =
            tag in NON_REMOVABLE_OR_DERIVED_TAGS ||
                    (
                            assetName.isTiff() &&
                                    tag == MetadataTag.PlanarConfiguration &&
                                    before?.toIntOrNull() == 2
                            )

        if (!maySurvive) {
            failures += buildString {
                append(source)
                append(": ")
                append(tag.key)
                append(" was not cleared; before=")
                append(before.quoted())
                append(", actual=")
                append(actual.quoted())
            }
            return
        }

        if (
            before != null &&
            tag !in VOLATILE_LAYOUT_OFFSET_TAGS &&
            actual != before
        ) {
            failures += buildString {
                append(source)
                append(": protected/derived ")
                append(tag.key)
                append(" changed while clearing; before=")
                append(before.quoted())
                append(", actual=")
                append(actual.quoted())
            }
        }
    }

    private fun compareProtectedValue(
        source: String,
        tag: MetadataTag,
        before: String?,
        actual: String?,
        failures: MutableList<String>
    ) {
        if (tag in VOLATILE_LAYOUT_OFFSET_TAGS) {
            if (before != null && actual == null) {
                failures += "$source: protected ${tag.key} disappeared"
            }
            return
        }

        if (actual != before) {
            failures += buildString {
                append(source)
                append(": protected ")
                append(tag.key)
                append(" changed; before=")
                append(before.quoted())
                append(", actual=")
                append(actual.quoted())
            }
        }
    }

    private fun nonWritableTagsFor(assetName: String): Set<MetadataTag> =
        buildSet {
            addAll(FORMAT_SPECIFIC_RAW_TAGS)
            if (assetName.isTiff()) {
                addAll(TIFF_IMAGE_STRUCTURE_TAGS)
            }
        }

    private fun compareExact(
        source: String,
        tag: MetadataTag,
        expected: String,
        actual: String?,
        failures: MutableList<String>
    ) {
        if (actual != expected) {
            failures += buildString {
                append(source)
                append(": ")
                append(tag.key)
                append(" expected=")
                append(expected.quoted())
                append(", actual=")
                append(actual.quoted())
            }
        }
    }

    private fun File.openMetadata(): Metadata =
        ExifInterface(this).toMetadata()

    private fun File.openStreamMetadata(): Metadata =
        inputStream().buffered().use { input ->
            ExifInterface(input).toMetadata()
        }

    private fun Metadata.readEveryEntry(): Map<MetadataTag, String?> =
        MetadataTag.entries.associateWith { tag -> this[tag] }

    private fun copyAssetToWritableFile(
        assetName: String,
        scenario: String
    ): File {
        val scenarioDirectory = File(workingDirectory, scenario).apply {
            check(mkdirs() || isDirectory) {
                "Could not create scenario directory: $absolutePath"
            }
        }
        val destination = File(scenarioDirectory, assetName)

        InstrumentationRegistry.getInstrumentation().context.assets
            .open("$ASSET_DIRECTORY/$assetName").use { input ->
            destination.outputStream().buffered().use(input::copyTo)
        }

        assertFileIsStillPresent(assetName, "asset-copy", destination)
        return destination
    }

    private fun assertFileIsStillPresent(
        assetName: String,
        stage: String,
        file: File
    ) {
        check(file.exists() && file.length() > 0L) {
            "$assetName became empty or disappeared after $stage"
        }
    }

    private fun failureReport(
        title: String,
        failures: List<String>,
        expected: Map<MetadataTag, String?>,
        fromFile: Map<MetadataTag, String?>,
        fromStream: Map<MetadataTag, String?>
    ): String = buildString {
        appendLine(title)
        failures.forEach { appendLine("  $it") }
        appendLine("Expected populated: ${expected.count { it.value != null }}")
        appendLine("File populated: ${fromFile.count { it.value != null }}")
        append("InputStream populated: ${fromStream.count { it.value != null }}")
    }

    private fun isSupportedTestImage(name: String): Boolean =
        name.extensionLowercase() in SUPPORTED_EXTENSIONS

    private fun String.extensionLowercase(): String =
        substringAfterLast('.', missingDelimiterValue = "").lowercase()

    private fun String.isTiff(): Boolean =
        extensionLowercase() in setOf("tif", "tiff")

    private fun String?.quoted(): String = when (this) {
        null -> "<null>"
        else -> "\"${replace("\n", "\\n").replace("\r", "\\r")}\""
    }

    private companion object {
        const val TAG = "MetadataRoundTripTest"
        const val ASSET_DIRECTORY = "metadata_round_trip"

        val SUPPORTED_EXTENSIONS = setOf(
            "jpg",
            "jpeg",
            "png",
            "webp",
            "avif",
            "heic",
            "heif",
            "jxl",
            "tif",
            "tiff",
            "jp2",
            "j2k"
        )

        val CUSTOM_TAG_VALUES: Map<MetadataTag, String> by lazy(
            mode = LazyThreadSafetyMode.NONE
        ) {
            MetadataTag.entries.associateWith(::createCustomValue)
        }

        private fun createCustomValue(tag: MetadataTag): String {
            CUSTOM_VALUE_OVERRIDES[tag]?.let { return it }

            val index = MetadataTag.entries.indexOf(tag) + 1

            return when (tag) {
                in STRING_OR_UNDEFINED_TAGS ->
                    "ImageToolbox_${tag.key}_Custom_$index"

                in USHORT_TAGS ->
                    (100 + index).toString()

                in ULONG_TAGS ->
                    (100_000L + index).toString()

                in URATIONAL_TAGS ->
                    "${index + 10}/${index + 11}"

                in SRATIONAL_TAGS ->
                    "-${index + 10}/${index + 11}"

                else -> error("No custom test value defined for ${tag.key}")
            }
        }

        val CUSTOM_VALUE_OVERRIDES = mapOf(
            MetadataTag.BitsPerSample to "8,8,8",
            MetadataTag.Compression to "1",
            MetadataTag.PhotometricInterpretation to "2",
            MetadataTag.SamplesPerPixel to "3",
            MetadataTag.PlanarConfiguration to "1",
            MetadataTag.YCbCrSubSampling to "2,2",
            MetadataTag.YCbCrPositioning to "1",
            MetadataTag.XResolution to "300/1",
            MetadataTag.YResolution to "301/1",
            MetadataTag.ResolutionUnit to "2",
            MetadataTag.StripOffsets to "512",
            MetadataTag.RowsPerStrip to "48",
            MetadataTag.StripByteCounts to "9216",
            MetadataTag.JpegInterchangeFormat to "1024",
            MetadataTag.JpegInterchangeFormatLength to "256",
            MetadataTag.TransferFunction to "1,2,3,4",
            MetadataTag.WhitePoint to "3127/10000,3290/10000",
            MetadataTag.PrimaryChromaticities to
                    "640/1000,330/1000,300/1000,600/1000,150/1000,60/1000",
            MetadataTag.YCbCrCoefficients to "299/1000,587/1000,114/1000",
            MetadataTag.ReferenceBlackWhite to
                    "0/1,255/1,128/1,255/1,128/1,255/1",

            MetadataTag.Datetime to "2026:07:24 12:34:56",
            // Exif 3.x tags below require the serialized ExifVersion to be 0300.
            MetadataTag.ExifVersion to "0300",
            MetadataTag.FlashpixVersion to "0100",
            MetadataTag.UserComment to "ImageToolbox custom metadata comment",

            // Exif 3.0/3.1 tags.
            MetadataTag.LearningOptOutIn to "0,2,1,1,2,0,3,2,4,0",
            MetadataTag.DevelopmentType to "1,2",
            MetadataTag.DevelopmentTypeDescription to
                    "ImageToolbox custom development description",
            MetadataTag.DistortionCorrection to "1",
            MetadataTag.ChromaticAberrationCorrection to "1",
            MetadataTag.ShadingCorrection to "1",
            MetadataTag.NoiseReduction to "1",
            MetadataTag.ImageTitle to "ImageToolbox custom image title",
            MetadataTag.Photographer to "ImageToolbox custom photographer",
            MetadataTag.ImageEditor to "ImageToolbox custom image editor",
            MetadataTag.CameraFirmware to "ImageToolbox custom camera firmware",
            MetadataTag.RawDevelopingSoftware to
                    "ImageToolbox custom RAW developing software",
            MetadataTag.ImageEditingSoftware to
                    "ImageToolbox custom image editing software",
            MetadataTag.MetadataEditingSoftware to
                    "ImageToolbox custom metadata editing software",
            MetadataTag.DatetimeOriginal to "2026:07:23 10:11:12",
            MetadataTag.DatetimeDigitized to "2026:07:22 09:08:07",
            MetadataTag.OffsetTime to "+03:00",
            MetadataTag.OffsetTimeOriginal to "+04:00",
            MetadataTag.OffsetTimeDigitized to "+05:00",
            MetadataTag.ExposureTime to "0.008",
            MetadataTag.FNumber to "2.8",
            MetadataTag.SubjectDistance to "12.5",
            MetadataTag.SubjectArea to "12,34",
            MetadataTag.SubjectLocation to "21,43",
            MetadataTag.DigitalZoomRatio to "1.25",
            MetadataTag.LensSpecification to "24/1,70/1,28/10,40/10",

            MetadataTag.GpsVersionId to "2.3.0.0",
            MetadataTag.GpsLatitudeRef to "N",
            MetadataTag.GpsLatitude to "48/1,12/1,3456/100",
            MetadataTag.GpsLongitudeRef to "E",
            MetadataTag.GpsLongitude to "16/1,22/1,1234/100",
            MetadataTag.GpsAltitudeRef to "1",
            MetadataTag.GpsTimestamp to "12:34:56",
            MetadataTag.GpsStatus to "A",
            MetadataTag.GpsMeasureMode to "3",
            MetadataTag.GpsSpeedRef to "K",
            MetadataTag.GpsTrackRef to "T",
            MetadataTag.GpsImgDirectionRef to "T",
            MetadataTag.GpsDestLatitudeRef to "S",
            MetadataTag.GpsDestLatitude to "33/1,45/1,1200/100",
            MetadataTag.GpsDestLongitudeRef to "W",
            MetadataTag.GpsDestLongitude to "70/1,30/1,2500/100",
            MetadataTag.GpsDestBearingRef to "T",
            MetadataTag.GpsDestDistanceRef to "K",
            MetadataTag.GpsDatestamp to "2026:07:24",

            MetadataTag.DngVersion to "1.4.0.0",
            MetadataTag.DefaultCropSize to "64,48",
            MetadataTag.OrfAspectFrame to "0,0,63,47"
        )

        val STRING_OR_UNDEFINED_TAGS = setOf(
            MetadataTag.ImageDescription,
            MetadataTag.Make,
            MetadataTag.Model,
            MetadataTag.Software,
            MetadataTag.Artist,
            MetadataTag.Copyright,
            MetadataTag.MakerNote,
            MetadataTag.RelatedSoundFile,
            MetadataTag.SubsecTime,
            MetadataTag.SubsecTimeOriginal,
            MetadataTag.SubsecTimeDigitized,
            MetadataTag.SpectralSensitivity,
            MetadataTag.Oecf,
            MetadataTag.SpatialFrequencyResponse,
            MetadataTag.FileSource,
            MetadataTag.CfaPattern,
            MetadataTag.DeviceSettingDescription,
            MetadataTag.ImageUniqueId,
            MetadataTag.CameraOwnerName,
            MetadataTag.BodySerialNumber,
            MetadataTag.LensMake,
            MetadataTag.LensModel,
            MetadataTag.LensSerialNumber,
            MetadataTag.GpsSatellites,
            MetadataTag.GpsMapDatum,
            MetadataTag.GpsProcessingMethod,
            MetadataTag.GpsAreaInformation,
            MetadataTag.InteroperabilityIndex
        )

        val USHORT_TAGS = setOf(
            MetadataTag.ColorSpace,
            MetadataTag.ExposureProgram,
            MetadataTag.PhotographicSensitivity,
            MetadataTag.SensitivityType,
            MetadataTag.MeteringMode,
            MetadataTag.Flash,
            MetadataTag.FocalPlaneResolutionUnit,
            MetadataTag.SensingMethod,
            MetadataTag.CustomRendered,
            MetadataTag.ExposureMode,
            MetadataTag.WhiteBalance,
            MetadataTag.FocalLengthIn35mmFilm,
            MetadataTag.SceneCaptureType,
            MetadataTag.GainControl,
            MetadataTag.Contrast,
            MetadataTag.Saturation,
            MetadataTag.Sharpness,
            MetadataTag.SubjectDistanceRange,
            MetadataTag.GpsDifferential,
            MetadataTag.Rw2Iso
        )

        val ULONG_TAGS = setOf(
            MetadataTag.PixelXDimension,
            MetadataTag.PixelYDimension,
            MetadataTag.StandardOutputSensitivity,
            MetadataTag.RecommendedExposureIndex,
            MetadataTag.IsoSpeed,
            MetadataTag.IsoSpeedLatitudeYyy,
            MetadataTag.IsoSpeedLatitudeZzz,
            MetadataTag.OrfPreviewImageStart,
            MetadataTag.OrfPreviewImageLength,
            MetadataTag.Rw2SensorBottomBorder,
            MetadataTag.Rw2SensorLeftBorder,
            MetadataTag.Rw2SensorRightBorder,
            MetadataTag.Rw2SensorTopBorder
        )

        val URATIONAL_TAGS = setOf(
            MetadataTag.Gamma,
            MetadataTag.CompressedBitsPerPixel,
            MetadataTag.ApertureValue,
            MetadataTag.MaxApertureValue,
            MetadataTag.FocalLength,
            MetadataTag.FlashEnergy,
            MetadataTag.FocalPlaneXResolution,
            MetadataTag.FocalPlaneYResolution,
            MetadataTag.ExposureIndex,
            MetadataTag.GpsAltitude,
            MetadataTag.GpsDop,
            MetadataTag.GpsSpeed,
            MetadataTag.GpsTrack,
            MetadataTag.GpsImgDirection,
            MetadataTag.GpsDestBearing,
            MetadataTag.GpsDestDistance,
            MetadataTag.GpsHPositioningError
        )

        val SRATIONAL_TAGS = setOf(
            MetadataTag.ShutterSpeedValue,
            MetadataTag.BrightnessValue,
            MetadataTag.ExposureBiasValue
        )

        /**
         * Tags that directly describe or locate TIFF pixel data.
         *
         * Changing these values without also transcoding/repacking the pixel data
         * can make a TIFF unreadable or reinterpret its pixels incorrectly.
         */
        val TIFF_IMAGE_STRUCTURE_TAGS = setOf(
            MetadataTag.BitsPerSample,
            MetadataTag.Compression,
            MetadataTag.PhotometricInterpretation,
            MetadataTag.SamplesPerPixel,
            MetadataTag.PlanarConfiguration,
            MetadataTag.YCbCrSubSampling,
            MetadataTag.YCbCrPositioning,
            MetadataTag.StripOffsets,
            MetadataTag.RowsPerStrip,
            MetadataTag.StripByteCounts,
            MetadataTag.JpegInterchangeFormat,
            MetadataTag.JpegInterchangeFormatLength,
            MetadataTag.YCbCrCoefficients,
            MetadataTag.ReferenceBlackWhite
        )

        /**
         * Tags stored in ORF-specific IFDs. The generic EXIF serializer accepts
         * them in memory but does not persist them in ordinary image formats.
         */
        val FORMAT_SPECIFIC_RAW_TAGS = setOf(
            MetadataTag.OrfPreviewImageStart,
            MetadataTag.OrfPreviewImageLength,
            MetadataTag.OrfAspectFrame
        )

        val VOLATILE_LAYOUT_OFFSET_TAGS = setOf(
            MetadataTag.StripOffsets,
            MetadataTag.StripByteCounts,
            MetadataTag.JpegInterchangeFormat,
            MetadataTag.JpegInterchangeFormatLength,
            MetadataTag.OrfPreviewImageStart,
            MetadataTag.OrfPreviewImageLength
        )

        val NON_REMOVABLE_OR_DERIVED_TAGS = setOf(
            MetadataTag.BitsPerSample,
            MetadataTag.Compression,
            MetadataTag.PhotometricInterpretation,
            MetadataTag.SamplesPerPixel,
            MetadataTag.YCbCrSubSampling,
            MetadataTag.YCbCrPositioning,
            MetadataTag.StripOffsets,
            MetadataTag.RowsPerStrip,
            MetadataTag.StripByteCounts,
            MetadataTag.JpegInterchangeFormat,
            MetadataTag.JpegInterchangeFormatLength,
            MetadataTag.YCbCrCoefficients,
            MetadataTag.ReferenceBlackWhite,
            MetadataTag.PixelXDimension,
            MetadataTag.PixelYDimension,
            MetadataTag.OrfPreviewImageStart,
            MetadataTag.OrfPreviewImageLength
        )
    }
}
