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

import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Base64
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.di.entryPoint
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.copyTo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.readOnly
import com.t8rin.imagetoolbox.core.domain.image.set
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.settings.di.SettingsStateEntryPoint
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.ui.utils.ComposeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ClearMetadataInstrumentedTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val directory = File(
        instrumentation.targetContext.cacheDir,
        "clear_metadata_${System.nanoTime()}"
    ).apply { check(mkdirs()) }

    @After
    fun tearDown() {
        directory.deleteRecursively()
    }

    @Test
    fun detachedMetadataClearsExifAndXmpInAllWritableFormats() {
        listOf("jpg", "png", "webp", "heic", "avif", "jxl", "tiff", "jp2").forEach { extension ->
            val source = File(directory, "source.$extension")
            instrumentation.context.assets.open("metadata_round_trip/rich.$extension").use {
                source.writeBytes(it.readBytes())
            }
            ExifInterface(source).apply {
                setAttribute(ExifInterface.TAG_ARTIST, PRIVATE_DATA)
                if (extension != "heic" && extension != "avif") {
                    setAttribute(ExifInterface.TAG_XMP, XMP)
                }
                saveAttributes()
            }
            if (extension == "heic" || extension == "avif") {
                // The HEIF writer does not write separate XMP. Add an existing XML metadata box.
                source.appendBytes(ByteArrayOutputStream().apply {
                    DataOutputStream(this).apply {
                        val payload = XMP.toByteArray()
                        writeInt(payload.size + 8)
                        writeBytes("xml ")
                        write(payload)
                    }
                }.toByteArray())
            } else {
                assertEquals(
                    extension,
                    XMP,
                    ExifInterface(source).getAttribute(ExifInterface.TAG_XMP)
                )
            }
            assertTrue(extension, source.readText(Charsets.ISO_8859_1).contains(XMP))
            val original = source.readBytes()
            val before = BitmapFactory.decodeByteArray(original, 0, original.size)
            val metadata = source.readMetadata().clearAllAttributes()
            val target = source.copyTo(File(directory, "target.$extension"))

            writeMetadata(target, metadata)

            val reopened = ExifInterface(target)
            assertNull(extension, reopened.getAttribute(ExifInterface.TAG_ARTIST))
            assertNull(extension, reopened.getAttribute(ExifInterface.TAG_XMP))
            assertFalse(extension, target.readText(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
            assertArrayEquals("$extension/source", original, source.readBytes())
            if (before != null) {
                val after = checkNotNull(BitmapFactory.decodeFile(target.path))
                try {
                    assertEquals(extension, before.width, after.width)
                    assertEquals(extension, before.height, after.height)
                    assertArrayEquals(extension, before.pixels(), after.pixels())
                } finally {
                    before.recycle()
                    after.recycle()
                }
            }
        }
    }

    @Test
    fun selectingAllTagsRemovesContainerMetadataWithoutRecompressing() {
        val source = jpegFile()
        val encodedImage = source.readBytes()
        addMetadata(source)
        val original = source.readBytes()
        val metadata = source.readMetadata().clearAttributes(MetadataTag.entries.reversed())
        val target = source.copyTo(File(directory, "cleaned.jpg"))

        writeMetadata(target, metadata)

        assertArrayEquals(encodedImage, target.readBytes())
        assertArrayEquals(original, source.readBytes())
    }

    @Test
    fun selectiveRemovalKeepsOtherExifXmpAndC2pa() {
        val source = jpegFile()
        addMetadata(source)
        val metadata = source.readMetadata().clearAttributes(listOf(MetadataTag.Artist))
        val target = source.copyTo(File(directory, "selected.jpg"))

        assertFalse(metadata.shouldClearAllAttributes)
        writeMetadata(target, metadata)

        val reopened = ExifInterface(target)
        assertNull(reopened.getAttribute(ExifInterface.TAG_ARTIST))
        assertEquals("Test camera", reopened.getAttribute(ExifInterface.TAG_MODEL))
        assertEquals(XMP, reopened.getAttribute(ExifInterface.TAG_XMP))
        assertTrue(target.readText(Charsets.ISO_8859_1).contains("C2PA $PRIVATE_DATA"))
    }

    @Test
    fun cleanupSurvivesSnapshotAndWritesAttributesAddedAfterClearing() {
        val source = jpegFile()
        addMetadata(source)
        val metadata = ExifInterface(source).toMetadata()
            .clearAllAttributes()
            .readOnly()
            .apply {
                this[MetadataTag.Artist] = "New artist"
                clearAttributes(listOf(MetadataTag.Model))
            }
        val target = source.copyTo(File(directory, "edited.jpg"))

        assertTrue(metadata.shouldClearAllAttributes)
        writeMetadata(target, metadata)

        val reopened = ExifInterface(target)
        assertEquals("New artist", reopened.getAttribute(ExifInterface.TAG_ARTIST))
        assertNull(reopened.getAttribute(ExifInterface.TAG_MODEL))
        assertNull(reopened.getAttribute(ExifInterface.TAG_XMP))
        assertFalse(target.readText(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
    }

    @Test
    fun appFileControllerClearsMetadataWhenWritingAndCopyingToClipboard() =
        withFileController { files, settings ->
            val source = jpegFile().also(::addMetadata)
            val target = source.copyTo(File(directory, "cached.jpg"))
            val metadata = checkNotNull(files.readMetadata(Uri.fromFile(source).toString()))
                .clearAllAttributes()
            files.writeMetadata(Uri.fromFile(target).toString(), metadata)

            val previous = settings.getSettingsState()
            settings.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithoutSaving)
            if (!previous.keepDateTime) settings.toggleKeepDateTime()
            if (previous.isAlwaysClearExif) settings.toggleAlwaysClearExif()
            if (!previous.addImageToolboxMetadata) settings.toggleAddImageToolboxMetadata()
            withTimeout(5_000) {
                settings.settingsState.first {
                    it.copyToClipboardMode == CopyToClipboardMode.Enabled.WithoutSaving &&
                            it.keepDateTime && it.addImageToolboxMetadata && !it.isAlwaysClearExif
                }
            }

            val result = files.save(source.saveTarget(metadata), keepOriginalMetadata = false)
            assertTrue(result.toString(), result is SaveResult.Success)
            val bytes = clipboardBytes()
            val exif = ExifInterface(ByteArrayInputStream(bytes))
            assertNull(exif.getAttribute(ExifInterface.TAG_XMP))
            assertNull(exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL))
            assertNull(exif.getAttribute(ExifInterface.TAG_SOFTWARE))
            assertFalse(bytes.toString(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
            assertNull(ExifInterface(target).getAttribute(ExifInterface.TAG_XMP))
            assertFalse(target.readText(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
        }

    @Test
    fun explicitExifReplacementPreservesPixelsAndRespectsOnlyTheSuppliedTags() =
        withFileController { files, settings ->
            val source = jpegFile().also(::addMetadata)
            ExifInterface(source).apply {
                setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_ROTATE_90.toString()
                )
                saveAttributes()
            }
            val original = source.readBytes()
            val tags = mapOf(
                MetadataTag.Make to "Google",
                MetadataTag.Model to "Pixel 7",
                MetadataTag.Software to "HDR+",
                MetadataTag.DatetimeOriginal to "2024:04:12 14:22:05",
                MetadataTag.FocalLength to "681/100",
                MetadataTag.FocalLengthIn35mmFilm to "24",
                MetadataTag.LensModel to "Pixel 7 back camera 6.81mm f/1.85",
                MetadataTag.FNumber to "185/100",
                MetadataTag.ExposureTime to "1/125",
                MetadataTag.PhotographicSensitivity to "200",
                MetadataTag.GpsLatitude to "0/1,1/1,30/1",
                MetadataTag.GpsLatitudeRef to "S",
                MetadataTag.GpsLongitude to "0/1,6/1,0/1",
                MetadataTag.GpsLongitudeRef to "E",
                MetadataTag.GpsMapDatum to "WGS-84",
                MetadataTag.GpsDatestamp to "2024:04:12",
                MetadataTag.GpsTimestamp to "14:22:05"
            )
            val replacement = Metadata(tags)
            val target = source.copyTo(File(directory, "replacement.jpg"))
            files.writeMetadata(Uri.fromFile(target).toString(), replacement, replaceExif = true)

            val before = checkNotNull(BitmapFactory.decodeFile(source.path))
            val after = checkNotNull(BitmapFactory.decodeFile(target.path))
            try {
                assertEquals(before.width, after.width)
                assertEquals(before.height, after.height)
                assertArrayEquals(before.pixels(), after.pixels())
            } finally {
                before.recycle()
                after.recycle()
            }

            settings.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithoutSaving)
            if (!settings.getSettingsState().addImageToolboxMetadata) {
                settings.toggleAddImageToolboxMetadata()
            }
            for (alwaysClear in listOf(false, true)) {
                if (settings.getSettingsState().isAlwaysClearExif != alwaysClear) {
                    settings.toggleAlwaysClearExif()
                }
                withTimeout(5_000) {
                    settings.settingsState.first {
                        it.copyToClipboardMode == CopyToClipboardMode.Enabled.WithoutSaving &&
                                it.addImageToolboxMetadata && it.isAlwaysClearExif == alwaysClear
                    }
                }
                val result = files.save(
                    source.saveTarget(replacement),
                    keepOriginalMetadata = false,
                    replaceExif = true
                )
                assertTrue(result.toString(), result is SaveResult.Success)
                val copied = clipboardBytes()
                assertArrayEquals(target.readBytes(), copied)
                val exif = ExifInterface(ByteArrayInputStream(copied))
                tags.keys.forEach { assertTrue(it.key, exif.getAttribute(it.key) != null) }
                assertEquals("HDR+", exif.getAttribute(ExifInterface.TAG_SOFTWARE))
                assertEquals(
                    ExifInterface.ORIENTATION_ROTATE_90.toString(),
                    exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                )
                assertArrayEquals(doubleArrayOf(-0.025, 0.1), exif.latLong, 0.000001)
                assertNull(exif.getAttribute(ExifInterface.TAG_XMP))
                assertFalse(copied.toString(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
            }

            val ordinaryEdit = source.readMetadata().clearAllAttributes().apply {
                this[MetadataTag.Artist] = "New artist"
            }
            val result = files.save(source.saveTarget(ordinaryEdit), keepOriginalMetadata = false)
            assertTrue(result.toString(), result is SaveResult.Success)
            val edited = ExifInterface(ByteArrayInputStream(clipboardBytes()))
            assertNull(edited.getAttribute(ExifInterface.TAG_ARTIST))
            assertArrayEquals(original, source.readBytes())
        }

    @Test
    fun replacingExifInPlaceKeepsUnsupportedFilesIntact() =
        withFileController { files, settings ->
            val source = jpegFile().also(::addMetadata)
            val replacement = Metadata(mapOf(MetadataTag.Make to "Google"))
            val target = source.copyTo(File(directory, "replacement.jpg"))
            files.writeMetadata(Uri.fromFile(target).toString(), replacement, replaceExif = true)
            val expected = target.readBytes()
            val gif = Base64.decode(
                "R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",
                Base64.DEFAULT
            )
            val unsupported = File(directory, "source.gif").apply { writeBytes(gif) }

            settings.setCopyToClipboardMode(CopyToClipboardMode.Disabled)
            if (settings.getSettingsState().filenameBehavior !is FilenameBehavior.Overwrite) {
                settings.toggleOverwriteFiles()
            }
            withTimeout(5_000) {
                settings.settingsState.first {
                    it.copyToClipboardMode == CopyToClipboardMode.Disabled &&
                            it.filenameBehavior is FilenameBehavior.Overwrite
                }
            }
            val rejected = files.save(
                unsupported.saveTarget(replacement, ImageFormat.Gif),
                keepOriginalMetadata = false,
                replaceExif = true
            )
            assertTrue(rejected.toString(), rejected is SaveResult.Error)
            assertArrayEquals(gif, unsupported.readBytes())

            val overwritten = files.save(
                source.saveTarget(replacement),
                keepOriginalMetadata = false,
                replaceExif = true
            )
            assertTrue(
                overwritten.toString(),
                overwritten is SaveResult.Success && overwritten.isOverwritten
            )
            assertArrayEquals(expected, source.readBytes())
        }

    private fun File.saveTarget(metadata: Metadata, format: ImageFormat = ImageFormat.Jpg) =
        ImageSaveTarget(
            imageInfo = ImageInfo(width = 16, height = 12, imageFormat = format),
            originalUri = Uri.fromFile(this).toString(),
            sequenceNumber = null,
            metadata = metadata,
            data = ByteArray(0),
            readFromUriInsteadOfData = true
        )

    private fun clipboardBytes(): ByteArray {
        val context = instrumentation.targetContext
        val clipboard = context.getSystemService(ClipboardManager::class.java)
        val uri = checkNotNull(clipboard.primaryClip?.getItemAt(0)?.uri)
        return checkNotNull(context.contentResolver.openInputStream(uri)).use { it.readBytes() }
    }

    private fun withFileController(block: suspend (FileController, SettingsManager) -> Unit) {
        val context = instrumentation.targetContext
        val intent = Intent().setClassName(
            context,
            "com.t8rin.imagetoolbox.app.presentation.AppActivity"
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        var activity: ComposeActivity? = null
        val focused = CountDownLatch(1)
        val callback = ActivityLifecycleCallback { currentActivity, stage ->
            if (currentActivity is ComposeActivity && stage == Stage.RESUMED) {
                activity = currentActivity
                val view = currentActivity.window.decorView
                if (view.hasWindowFocus()) {
                    focused.countDown()
                } else {
                    view.viewTreeObserver.addOnWindowFocusChangeListener { hasFocus ->
                        if (hasFocus) focused.countDown()
                    }
                }
            }
        }
        val monitor = ActivityLifecycleMonitorRegistry.getInstance()
        monitor.addLifecycleCallback(callback)
        try {
            context.startActivity(intent)
            assertTrue("App activity did not gain focus", focused.await(15, TimeUnit.SECONDS))
            val fileController = checkNotNull(activity).fileController
            val settings = context.entryPoint<SettingsStateEntryPoint>().settingsManager
            val clipboard = context.getSystemService(ClipboardManager::class.java)
            val originalClip = clipboard.primaryClip
            runBlocking {
                val original = settings.getSettingsState()
                try {
                    block(fileController, settings)
                } finally {
                    val current = settings.getSettingsState()
                    if (current.filenameBehavior is FilenameBehavior.Overwrite) {
                        when (val behavior = original.filenameBehavior) {
                            is FilenameBehavior.None -> settings.toggleOverwriteFiles()
                            is FilenameBehavior.Random -> settings.toggleRandomizeFilename()
                            is FilenameBehavior.Checksum -> settings.setChecksumTypeForFilename(
                                behavior.hashingType
                            )

                            is FilenameBehavior.Overwrite -> Unit
                        }
                    }
                    if (current.keepDateTime != original.keepDateTime) settings.toggleKeepDateTime()
                    if (current.addImageToolboxMetadata != original.addImageToolboxMetadata) {
                        settings.toggleAddImageToolboxMetadata()
                    }
                    if (current.isAlwaysClearExif != original.isAlwaysClearExif) {
                        settings.toggleAlwaysClearExif()
                    }
                    settings.setCopyToClipboardMode(original.copyToClipboardMode)
                    if (originalClip == null) clipboard.clearPrimaryClip()
                    else clipboard.primaryClip = originalClip
                }
            }
        } finally {
            monitor.removeLifecycleCallback(callback)
            instrumentation.runOnMainSync { activity?.finish() }
        }
    }

    private fun File.readMetadata(): Metadata = inputStream().use {
        ExifInterface(it).toMetadata().readOnly()
    }

    private fun writeMetadata(file: File, metadata: Metadata) {
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE).use {
            metadata.copyTo(it.fileDescriptor.toMetadata())
            assertTrue(it.fileDescriptor.valid())
        }
    }

    private fun jpegFile(): File = File(directory, "source.jpg").apply {
        val bitmap = Bitmap.createBitmap(16, 12, Bitmap.Config.ARGB_8888)
        try {
            bitmap.eraseColor(0xffaabbcc.toInt())
            outputStream().use { assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 95, it)) }
        } finally {
            bitmap.recycle()
        }
    }

    private fun addMetadata(file: File) {
        ExifInterface(file).apply {
            setAttribute(ExifInterface.TAG_ARTIST, PRIVATE_DATA)
            setAttribute(ExifInterface.TAG_MODEL, "Test camera")
            setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL, "2026:10:03 12:00:00")
            setAttribute(ExifInterface.TAG_XMP, XMP)
            saveAttributes()
        }
        val bytes = file.readBytes()
        val manifest = "C2PA $PRIVATE_DATA".toByteArray()
        val jumbf = ByteArrayOutputStream().apply {
            DataOutputStream(this).apply {
                writeBytes("JP")
                writeShort(1)
                writeInt(1)
                writeInt(manifest.size + 8)
                writeBytes("jumb")
                write(manifest)
            }
        }.toByteArray()
        file.outputStream().use { output ->
            output.write(bytes, 0, 2)
            DataOutputStream(output).apply {
                listOf(
                    0xeb to jumbf,
                    0xe1 to ("http://ns.adobe.com/xmp/extension/" + 0.toChar() + PRIVATE_DATA).toByteArray(),
                    0xed to ("Photoshop 3.0" + 0.toChar() + PRIVATE_DATA).toByteArray(),
                    0xfe to PRIVATE_DATA.toByteArray()
                ).forEach { (marker, payload) ->
                    writeByte(0xff)
                    writeByte(marker)
                    writeShort(payload.size + 2)
                    write(payload)
                }
            }
            output.write(bytes, 2, bytes.size - 2)
        }
    }

    private fun Bitmap.pixels(): IntArray = IntArray(width * height).also {
        getPixels(it, 0, width, 0, 0, width, height)
    }

    companion object {
        private const val PRIVATE_DATA = "private metadata to remove"
        private const val XMP = "<x:xmpmeta>$PRIVATE_DATA</x:xmpmeta>"
    }
}