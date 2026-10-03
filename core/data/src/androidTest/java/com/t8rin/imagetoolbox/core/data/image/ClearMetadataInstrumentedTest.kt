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
import com.t8rin.imagetoolbox.core.settings.domain.model.CopyToClipboardMode
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
    fun appFileControllerClearsMetadataWhenWritingAndCopyingToClipboard() {
        val source = jpegFile()
        addMetadata(source)
        val target = source.copyTo(File(directory, "cached.jpg"))
        val intent = Intent().setClassName(
            instrumentation.targetContext,
            "com.t8rin.imagetoolbox.app.presentation.AppActivity"
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        lateinit var fileController: FileController
        var activity: ComposeActivity? = null
        val focused = CountDownLatch(1)
        val callback = ActivityLifecycleCallback { currentActivity, stage ->
            if (currentActivity is ComposeActivity && stage == Stage.RESUMED) {
                activity = currentActivity
                fileController = currentActivity.fileController
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
            instrumentation.targetContext.startActivity(intent)
            assertTrue("App activity did not gain focus", focused.await(15, TimeUnit.SECONDS))
            runBlocking {
                val metadata = checkNotNull(
                    fileController.readMetadata(Uri.fromFile(source).toString())
                ).clearAllAttributes()
                fileController.writeMetadata(Uri.fromFile(target).toString(), metadata)

                val context = instrumentation.targetContext
                val settingsManager = context.entryPoint<SettingsStateEntryPoint>().settingsManager
                val originalSettings = settingsManager.getSettingsState()
                val clipboard = context.getSystemService(ClipboardManager::class.java)
                val originalClip = clipboard.primaryClip
                try {
                    settingsManager.setCopyToClipboardMode(CopyToClipboardMode.Enabled.WithoutSaving)
                    if (!originalSettings.keepDateTime) settingsManager.toggleKeepDateTime()
                    if (originalSettings.isAlwaysClearExif) settingsManager.toggleAlwaysClearExif()
                    if (!originalSettings.addImageToolboxMetadata) {
                        settingsManager.toggleAddImageToolboxMetadata()
                    }
                    withTimeout(5_000) {
                        settingsManager.settingsState.first {
                            it.copyToClipboardMode == CopyToClipboardMode.Enabled.WithoutSaving &&
                                    it.keepDateTime && it.addImageToolboxMetadata && !it.isAlwaysClearExif
                        }
                    }
                    val result = fileController.save(
                        saveTarget = ImageSaveTarget(
                            imageInfo = ImageInfo(
                                width = 16,
                                height = 12,
                                imageFormat = ImageFormat.Jpg
                            ),
                            originalUri = Uri.fromFile(source).toString(),
                            sequenceNumber = null,
                            metadata = metadata,
                            data = ByteArray(0),
                            readFromUriInsteadOfData = true
                        ),
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = null
                    )
                    assertTrue(result.toString(), result is SaveResult.Success)
                    val clipboardUri = checkNotNull(clipboard.primaryClip?.getItemAt(0)?.uri)
                    val bytes = checkNotNull(context.contentResolver.openInputStream(clipboardUri))
                        .use { it.readBytes() }
                    val reopened = ExifInterface(ByteArrayInputStream(bytes))
                    assertNull(reopened.getAttribute(ExifInterface.TAG_XMP))
                    assertNull(reopened.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL))
                    assertNull(reopened.getAttribute(ExifInterface.TAG_SOFTWARE))
                    assertFalse(bytes.toString(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
                } finally {
                    settingsManager.setCopyToClipboardMode(originalSettings.copyToClipboardMode)
                    val settings = settingsManager.getSettingsState()
                    if (settings.keepDateTime != originalSettings.keepDateTime) {
                        settingsManager.toggleKeepDateTime()
                    }
                    if (settings.addImageToolboxMetadata != originalSettings.addImageToolboxMetadata) {
                        settingsManager.toggleAddImageToolboxMetadata()
                    }
                    if (settings.isAlwaysClearExif != originalSettings.isAlwaysClearExif) {
                        settingsManager.toggleAlwaysClearExif()
                    }
                    if (originalClip != null) {
                        clipboard.setPrimaryClip(originalClip)
                    } else {
                        clipboard.clearPrimaryClip()
                    }
                }
            }
        } finally {
            monitor.removeLifecycleCallback(callback)
            instrumentation.runOnMainSync { activity?.finish() }
        }

        assertNull(ExifInterface(target).getAttribute(ExifInterface.TAG_XMP))
        assertFalse(target.readText(Charsets.ISO_8859_1).contains(PRIVATE_DATA))
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