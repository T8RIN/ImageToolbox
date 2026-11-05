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

package com.t8rin.imagetoolbox.instrumentation

import android.graphics.Bitmap
import android.net.Uri
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImagePreviewCreator
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.feature.image_stacking.data.AndroidImageStacker
import com.t8rin.imagetoolbox.feature.image_stacking.domain.ImageStacker
import com.t8rin.imagetoolbox.feature.image_stacking.domain.StackingParams
import com.t8rin.imagetoolbox.feature.image_stacking.domain.toStackImage
import com.t8rin.imagetoolbox.feature.image_stacking.presentation.screenLogic.ImageStackingComponent
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CopyOnWriteArrayList

class StackingRegressionTest {
    private val lifecycle = LifecycleRegistry()
    private val source = pixels(40, 30)
    private val settings = MutableStateFlow(SettingsState.Default)
    private val renders = CopyOnWriteArrayList<Unit>()
    private val previews = CopyOnWriteArrayList<ImageInfo>()
    private var render: suspend () -> Bitmap = { source }

    @After
    fun tearDown() = runBlocking {
        withContext(Dispatchers.Main) { lifecycle.destroy() }
        source.recycle()
    }

    @Test
    fun firstLayerIsDecodedOnceAndBorrowedBitmapsStayUsable() = runBlocking {
        val reads = mutableListOf<String>()
        val stacker = AndroidImageStacker(
            imageGetter = testProxy<ImageGetter<Bitmap>> { _, args ->
                reads += args[0] as String
                source
            },
            imageScaler = testProxy<ImageScaler<Bitmap>> { _, args -> args[0] },
            dispatchersHolder = TestDispatchers
        )
        for (size in listOf(null, IntegerSize(40, 30))) {
            reads.clear()
            val result = requireNotNull(
                stacker.stackImages(
                listOf("a".toStackImage(), "b".toStackImage()),
                StackingParams(size), { throw it }, {}
            ))
            assertEquals(listOf("a", "b"), reads)
            assertEquals(40, result.width)
            assertEquals(30, result.height)
            assertFalse(source.isRecycled)
            result.recycle()
        }
    }

    @Test
    fun encodingChangesReuseRenderButContentAndSettingsChangesInvalidateIt() = runBlocking {
        val component = component()
        withContext(Dispatchers.Main) { component.updateUris(uris) }
        await { previews.size == 1 }
        withContext(Dispatchers.Main) { component.setImageFormat(ImageFormat.Jpg) }
        await { previews.size == 2 }
        withContext(Dispatchers.Main) { component.setQuality(Quality.Base(75)) }
        await { previews.size == 3 }
        assertEquals(1, renders.size)
        assertEquals(ImageFormat.Jpg, previews.last().imageFormat)
        assertEquals(Quality.Base(75), previews.last().quality)
        assertEquals(40, previews.last().width)
        assertEquals(30, previews.last().height)
        assertEquals(12345, component.imageByteSize)

        withContext(Dispatchers.Main) {
            component.updateStackImage(component.stackImages.first().copy(alpha = 0.5f), 0)
        }
        await { previews.size == 4 }
        assertEquals(2, renders.size)
        settings.value = settings.value.copy(generatePreviews = !settings.value.generatePreviews)
        withContext(Dispatchers.Main) { component.setQuality(Quality.Base(60)) }
        await { previews.size == 5 }
        assertEquals(3, renders.size)
        withContext(Dispatchers.Main) { component.updateUris(uris) }
        await { previews.size == 6 }
        assertEquals(4, renders.size)
        withContext(Dispatchers.Main) { component.reorderUris(uris) }
        delay(500)
        assertEquals(4, renders.size)
        withContext(Dispatchers.Main) {
            component.updateParams(
                StackingParams(
                    IntegerSize(
                        20,
                        20
                    )
                )
            )
        }
        await { previews.size == 7 }
        assertEquals(5, renders.size)
        withContext(Dispatchers.Main) { component.undo() }
        await { previews.size == 8 }
        assertNull(component.stackingParams.size)
        assertEquals(6, renders.size)
    }

    @Test
    fun clearingSelectionRejectsLateRenderAndClearsSize() = runBlocking {
        val gate = CompletableDeferred<Unit>()
        render = { withContext(NonCancellable) { gate.await() }; source }
        val component = component()
        withContext(Dispatchers.Main) { component.updateUris(uris) }
        await { renders.size == 1 }
        withContext(Dispatchers.Main) { component.updateUris(emptyList()) }
        gate.complete(Unit)
        delay(500)
        assertNull(component.previewBitmap)
        assertNull(component.imageByteSize)
        assertTrue(previews.isEmpty())
        assertFalse(component.isImageLoading)
    }

    @Suppress("UNCHECKED_CAST")
    private suspend fun component() = withContext(Dispatchers.Main) {
        ImageStackingComponent(
            componentContext = DefaultComponentContext(lifecycle),
            initialUris = null, onGoBack = {}, onNavigate = {},
            shareProvider = testProxy { _, _ -> error("Unexpected share") },
            fileController = testProxy { _, _ -> error("Unexpected save") },
            imageCompressor = testProxy { _, _ -> error("Unexpected export") },
            imageStacker = object : ImageStacker<Bitmap> {
                override suspend fun stackImages(
                    stackImages: List<com.t8rin.imagetoolbox.feature.image_stacking.domain.StackImage>,
                    stackingParams: StackingParams,
                    onFailure: (Throwable) -> Unit,
                    onProgress: (Int) -> Unit
                ): Bitmap {
                    renders += Unit
                    return render()
                }
            },
            imagePreviewCreator = testProxy<ImagePreviewCreator<Bitmap>> { name, args ->
                check(name == "createPreview")
                assertSame(source, args[0])
                previews += args[1] as ImageInfo
                (args[3] as (Int) -> Unit)(12345)
                (args[4] as (Bitmap?) -> Unit)(source)
                Unit
            },
            settingsManager = testProxy { name, _ ->
                check(name == "getSettingsState")
                settings
            },
            dispatchersHolder = TestDispatchers
        )
    }

    private suspend fun await(condition: () -> Boolean) = withContext(Dispatchers.Main) {
        withTimeout(5000) { while (!condition()) delay(10) }
    }

    private val uris = listOf(Uri.parse("file:///a"), Uri.parse("file:///b"))
}