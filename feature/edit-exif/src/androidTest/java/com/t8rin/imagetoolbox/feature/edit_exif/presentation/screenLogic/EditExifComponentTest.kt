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

package com.t8rin.imagetoolbox.feature.edit_exif.presentation.screenLogic

import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.data.image.toMetadata
import com.t8rin.imagetoolbox.core.data.saving.io.FileWriteable
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.copyTo
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.readOnly
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.saving.model.SaveResult
import com.t8rin.imagetoolbox.core.utils.initAppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.DataOutputStream
import java.io.File
import java.lang.reflect.Proxy
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class EditExifComponentTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext.apply {
        initAppContext()
    }
    private val directory = File(context.cacheDir, "edit_exif_${System.nanoTime()}").apply {
        check(mkdirs())
    }
    private val lifecycle = LifecycleRegistry()

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync { lifecycle.destroy() }
        directory.deleteRecursively()
    }

    @Test
    fun largeJpegCanBeOpenedEditedSavedAndSharedWithoutDecodingOrCachingOnOpen() = runBlocking {
        val source = File(directory, "large.jpg")
        val bitmap = createBitmap(3840, 2160)
        try {
            val random = Random(3259)
            val row = IntArray(bitmap.width)
            repeat(bitmap.height) { y ->
                row.indices.forEach { x -> row[x] = random.nextInt() or 0xff000000.toInt() }
                bitmap.setPixels(row, 0, row.size, 0, y, row.size, 1)
            }
            source.outputStream()
                .use { assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)) }
        } finally {
            bitmap.recycle()
        }
        if (source.length() <= 15_000_000L) {
            // APP15 padding keeps the fixture above 15 MB without adding EXIF.
            val jpeg = source.readBytes()
            DataOutputStream(source.outputStream()).use { output ->
                output.write(jpeg, 0, 2)
                var padding = 15_420_000 - jpeg.size
                while (padding > 0) {
                    val segment = ByteArray(minOf(padding, 65_533))
                    output.writeShort(0xffef)
                    output.writeShort(segment.size + 2)
                    output.write(segment)
                    padding -= segment.size
                }
                output.write(jpeg, 2, jpeg.size - 2)
            }
        }
        assertTrue("Fixture must exceed 15 MB", source.length() > 15_000_000L)
        assertNull(ExifInterface(source).getAttribute(ExifInterface.TAG_ARTIST))
        val original = source.readBytes()
        val saved = File(directory, "saved.jpg")
        val shared = File(directory, "shared.jpg")
        var reads = 0
        val component = component(dependency { name, args ->
            when (name) {
                "readMetadata" -> {
                    assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
                    reads++
                    ExifInterface(source).toMetadata().readOnly()
                }

                "save" -> {
                    val target = args[0] as ImageSaveTarget
                    assertEquals(3840, target.imageInfo.width)
                    assertEquals(2160, target.imageInfo.height)
                    assertEquals(ImageFormat.Jpg, target.imageFormat)
                    assertTrue(target.readFromUriInsteadOfData)
                    assertTrue(target.data.isEmpty())
                    source.copyTo(saved)
                    checkNotNull(target.metadata).copyTo(ExifInterface(saved).toMetadata())
                    SaveResult.Success(savingPath = directory.path)
                }

                "transferBytes" -> {
                    source.inputStream().use { it.copyTo((args[1] as StreamWriteable).stream) }
                    SaveResult.Success(savingPath = "")
                }

                "writeMetadata" -> {
                    assertNotEquals(Looper.getMainLooper(), Looper.myLooper())
                    (args[1] as Metadata).copyTo(ExifInterface(shared).toMetadata())
                    Unit
                }

                else -> error("Unexpected file operation: $name")
            }
        }, dependency { name, args ->
            check(name == "cacheData")
            @Suppress("UNCHECKED_CAST")
            val write = args[1] as suspend (Writeable) -> Unit
            runBlocking {
                val output = FileWriteable(shared)
                try {
                    write(output)
                } finally {
                    output.close()
                }
            }
            Uri.fromFile(shared).toString()
        })

        repeat(5) {
            withContext(Dispatchers.Main) { component.setUri(Uri.fromFile(source)) }
            await { !component.isImageLoading }
            assertNotNull(component.exif)
            assertEquals(
                source.length(),
                directory.walkTopDown().filter(File::isFile).sumOf(File::length)
            )
        }
        assertEquals(5, reads)

        withContext(Dispatchers.Main) {
            component.updateExifByTag(MetadataTag.Artist, "Image Toolbox")
            component.saveBitmap(null)
        }
        await { saved.exists() && !component.isSaving }
        assertEquals("Image Toolbox", ExifInterface(saved).getAttribute(ExifInterface.TAG_ARTIST))

        var sharedUri: Uri? = null
        withContext(Dispatchers.Main) { component.cacheCurrentImage { sharedUri = it } }
        await { sharedUri != null && !component.isSaving }
        assertEquals("Image Toolbox", ExifInterface(shared).getAttribute(ExifInterface.TAG_ARTIST))
        assertEquals(5, reads)
        assertArrayEquals(original, source.readBytes())
        val scan = original.copyOfRange(original.indexOfSos(), original.size)
        for (file in listOf(saved, shared)) {
            val bytes = file.readBytes()
            assertArrayEquals(scan, bytes.copyOfRange(bytes.indexOfSos(), bytes.size))
        }
    }

    @Test
    fun switchingImagesDiscardsPendingMetadata() = runBlocking {
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val component = component(dependency { name, args ->
            check(name == "readMetadata")
            if (args[0] == "file:///slow.jpg") {
                started.countDown()
                check(release.await(5, TimeUnit.SECONDS))
            }
            ExifInterface(
                byteArrayOf(
                    0xff.toByte(),
                    0xd8.toByte(),
                    0xff.toByte(),
                    0xd9.toByte()
                ).inputStream()
            )
                .toMetadata().readOnly().setAttribute(MetadataTag.Artist, args[0] as String)
        })
        try {
            withContext(Dispatchers.Main) { component.setUri(Uri.parse("file:///slow.jpg")) }
            assertTrue(started.await(5, TimeUnit.SECONDS))
            withContext(Dispatchers.Main) { component.setUri(Uri.parse("file:///current.png")) }
            await { component.exif != null && !component.isImageLoading }
            release.countDown()
            delay(100)
            assertEquals("file:///current.png", component.exif?.getAttribute(MetadataTag.Artist))
            assertEquals(ImageFormat.Png.Lossless, component.imageFormat)
        } finally {
            release.countDown()
        }
    }

    private suspend fun component(
        fileController: FileController,
        shareProvider: ShareProvider = dependency()
    ): EditExifComponent = withContext(Dispatchers.Main) {
        EditExifComponent(
            componentContext = DefaultComponentContext(lifecycle),
            initialUri = null,
            onGoBack = {},
            onNavigate = {},
            fileController = fileController,
            imageGetter = dependency<ImageGetter<Bitmap>> { name, args ->
                check(name == "getExtension") { "EXIF editing must not decode a bitmap: $name" }
                (args[0] as String).substringAfterLast('.')
            },
            shareProvider = shareProvider,
            filenameCreator = dependency<FilenameCreator> { name, _ ->
                check(name == "constructImageFilename")
                "shared.jpg"
            },
            dispatchersHolder = dependency<DispatchersHolder> { _, _ -> Dispatchers.IO }
        )
    }

    private suspend fun await(predicate: () -> Boolean) = withTimeout(10_000) {
        while (!withContext(Dispatchers.Main) { predicate() }) delay(20)
    }

    private fun ByteArray.indexOfSos(): Int = indices.first {
        this[it] == 0xff.toByte() && getOrNull(it + 1) == 0xda.toByte()
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> dependency(
        crossinline invoke: (String, Array<out Any?>) -> Any? = { name, _ -> error("Unexpected call: $name") }
    ): T = Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, method, args -> invoke(method.name, args ?: emptyArray()) } as T
}