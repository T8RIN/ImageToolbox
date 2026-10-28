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

package com.t8rin.imagetoolbox.feature.markup_layers

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.AssetRegistry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupMapper
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectFile
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectVersion
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.LayersRenderer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerPosition
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectHistorySnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ProjectBackground
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode
import com.t8rin.imagetoolbox.feature.markup_layers.domain.asLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.asDomain
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.asUi
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.toSnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.toUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class ImageBackgroundLayerTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext.apply {
        initAppContext()
    }
    private val background = ProjectBackground.Image("file:///background.png")
    private val backgroundLayer = background.asLayer()
    private val canvasSize = IntegerSize(160, 96)
    private val underlay = MarkupLayer(
        type = LayerType.Shape.Default.copy(
            shapeMode = ShapeMode.Rect(),
            color = Color.BLUE,
            widthRatio = 1f,
            heightRatio = 1f,
            isPaddingEnabled = false
        ),
        position = LayerPosition(
            currentCanvasSize = canvasSize,
            coerceToBounds = true,
            isVisible = true
        ),
        contentSize = canvasSize
    )

    @Test
    fun backgroundCoversUnderlayExceptInTransparentAreas() = runBlocking {
        withRenderer { renderer, source ->
            val result = renderer.render(source, listOf(underlay, backgroundLayer))
            assertEquals(Color.GREEN, result.getPixel(40, 48))
            assertEquals(Color.BLUE, result.getPixel(120, 48))
            assertEquals(canvasSize.width, result.width)
            assertEquals(canvasSize.height, result.height)
            result.recycle()
        }
    }

    @Test
    fun movingLayerAboveBackgroundChangesComposition() = runBlocking {
        withRenderer { renderer, source ->
            val result = renderer.render(source, listOf(backgroundLayer, underlay))
            assertEquals(Color.BLUE, result.getPixel(40, 48))
            assertEquals(Color.BLUE, result.getPixel(120, 48))
            result.recycle()
        }
    }

    @Test
    fun untouchedTransparentAreasStayTransparent() = runBlocking {
        withRenderer { renderer, source ->
            val result = renderer.render(
                source,
                listOf(underlay.copy(contentSize = IntegerSize(80, 48)), backgroundLayer)
            )
            assertEquals(Color.GREEN, result.getPixel(20, 48))
            assertEquals(Color.BLUE, result.getPixel(90, 48))
            assertEquals(Color.TRANSPARENT, result.getPixel(150, 90))
            result.recycle()
        }
    }

    @Test
    fun backgroundKeepsExactPixelsRegardlessOfLayerGeometry() = runBlocking {
        withRenderer { renderer, source ->
            val result = renderer.render(
                source,
                listOf(
                    backgroundLayer.copy(
                        position = backgroundLayer.position.copy(
                            scale = 0.2f,
                            rotation = 35f,
                            offsetX = 30f,
                            offsetY = -10f,
                            currentCanvasSize = IntegerSize(2000, 1000)
                        )
                    )
                )
            )
            assertTrue(source.sameAs(result))
            result.recycle()
        }
    }

    @Test
    fun hiddenBackgroundIsNotDrawnUnderLayers() = runBlocking {
        withRenderer { renderer, source ->
            val hiddenBackground = backgroundLayer.copy(
                position = backgroundLayer.position.copy(isVisible = false)
            )
            val result = renderer.render(source, listOf(hiddenBackground))
            assertEquals(Color.TRANSPARENT, result.getPixel(40, 48))
            assertEquals(Color.TRANSPARENT, result.getPixel(120, 48))
            result.recycle()
        }
    }

    @Test
    fun backgroundOpacityAffectsExportAndCompositing() = runBlocking {
        withRenderer { renderer, source ->
            for (alpha in listOf(0f, 0.5f, 1f)) {
                val layer = backgroundLayer.copy(
                    position = backgroundLayer.position.copy(alpha = alpha)
                )
                val expectedAlpha = (alpha * 255).roundToInt()
                val transparent = renderer.render(source, listOf(layer))
                assertEquals(expectedAlpha, Color.alpha(transparent.getPixel(40, 48)))
                assertEquals(Color.TRANSPARENT, transparent.getPixel(120, 48))

                val composite = renderer.render(source, listOf(underlay, layer))
                val pixel = composite.getPixel(40, 48)
                assertEquals(255, Color.alpha(pixel))
                assertEquals(0, Color.red(pixel))
                assertEquals(expectedAlpha.toFloat(), Color.green(pixel).toFloat(), 1f)
                assertEquals((255 - expectedAlpha).toFloat(), Color.blue(pixel).toFloat(), 1f)
                assertEquals(Color.BLUE, composite.getPixel(120, 48))
                transparent.recycle()
                composite.recycle()
            }
        }
    }

    @Test
    fun backgroundOpacityMultipliesExistingPixelAlpha() = runBlocking {
        withRenderer { renderer, source ->
            source.setPixel(40, 48, Color.argb(128, 0, 255, 0))
            val result = renderer.render(
                source, listOf(
                    backgroundLayer.copy(
                        position = backgroundLayer.position.copy(alpha = 0.5f)
                    )
                )
            )
            assertEquals(64, Color.alpha(result.getPixel(40, 48)))
            assertEquals(255, Color.green(result.getPixel(40, 48)))
            result.recycle()
        }
    }

    @Test
    fun projectRoundTripKeepsBackgroundOrderVisibilityOpacityAndHistory() = runBlocking {
        val hiddenBackground = backgroundLayer.copy(
            position = backgroundLayer.position.copy(isVisible = false, alpha = 0.35f)
        )
        val translucentBackground = backgroundLayer.copy(
            position = backgroundLayer.position.copy(alpha = 0.65f)
        )
        val project = MarkupProject(
            background = background,
            layers = listOf(underlay, translucentBackground),
            lastLayers = listOf(backgroundLayer, underlay),
            undoneLayers = listOf(hiddenBackground, underlay),
            history = listOf(
                MarkupProjectHistorySnapshot(
                    background,
                    listOf(backgroundLayer, underlay)
                )
            ),
            redoHistory = listOf(
                MarkupProjectHistorySnapshot(
                    background,
                    listOf(underlay, hiddenBackground)
                )
            )
        )
        val registry = AssetRegistry()
        val restored = roundTrip(mapper().map(project, registry))

        assertEquals(listOf(false, true), restored.layers.map { it.isBackground })
        assertEquals(listOf(true, false), restored.lastLayers.map { it.isBackground })
        assertEquals(listOf(true, false), restored.history.single().layers.map { it.isBackground })
        assertEquals(
            listOf(false, true),
            restored.redoHistory.single().layers.map { it.isBackground })
        assertFalse(restored.redoHistory.single().layers.last().position.isVisible)
        assertEquals(1, registry.entries().size)
        val restoredBackground = restored.layers.last().asUi().toSnapshot().toUi().asDomain()
        assertTrue(restoredBackground.isBackground)
        assertTrue(restoredBackground.isLocked)
        assertEquals(0.65f, restoredBackground.position.alpha)
        assertEquals(1f, restored.history.single().layers.first().position.alpha)
        assertEquals(0.35f, restored.redoHistory.single().layers.last().position.alpha)
    }

    @Test
    fun legacyImageProjectsGainLockedBackgroundInAllHistoryStates() = runBlocking {
        val project = MarkupProject(
            background = background,
            layers = listOf(underlay),
            lastLayers = emptyList(),
            undoneLayers = listOf(underlay),
            history = listOf(MarkupProjectHistorySnapshot(background, emptyList())),
            redoHistory = listOf(MarkupProjectHistorySnapshot(background, listOf(underlay)))
        )
        val restored = roundTrip(mapper().map(project, AssetRegistry()).copy(version = 1))
        listOf(
            restored.layers, restored.lastLayers, restored.undoneLayers,
            restored.history.single().layers, restored.redoHistory.single().layers
        ).forEach { layers ->
            assertEquals(1, layers.count { it.isBackground })
            assertTrue(layers.first().isBackground)
            assertTrue(layers.first().isLocked)
        }
        assertEquals(2, MarkupProjectVersion)
    }

    private suspend fun roundTrip(file: MarkupProjectFile): MarkupProject {
        val adapter = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            .adapter(MarkupProjectFile::class.java)
        val json = adapter.toJson(file).let {
            if (file.version == 1) it.replace(",\"isBackground\":false", "") else it
        }
        val restored = mapper().map(adapter.fromJson(json)!!, context.cacheDir)
        return (restored as MarkupProjectResult.Success).project
    }

    private fun mapper() = MarkupMapper(
        Proxy.newProxyInstance(
            SettingsManager::class.java.classLoader,
            arrayOf(SettingsManager::class.java)
        ) { _, method, _ -> error("Unexpected settings access: ${method.name}") } as SettingsManager
    )

    private suspend fun withRenderer(block: suspend (LayersRenderer, Bitmap) -> Unit) {
        val dispatchers = object : DispatchersHolder {
            override val uiDispatcher = Dispatchers.Main
            override val ioDispatcher = Dispatchers.IO
            override val encodingDispatcher = Dispatchers.Default
            override val decodingDispatcher = Dispatchers.Default
            override val defaultDispatcher = Dispatchers.Default
        }
        val imageLoader = ImageLoader.Builder(context).build()
        val source = createBitmap(canvasSize.width, canvasSize.height).apply {
            for (y in 0 until height) {
                for (x in 0 until width / 2) setPixel(x, y, Color.GREEN)
            }
        }
        try {
            block(LayersRenderer(context, imageLoader, dispatchers), source)
        } finally {
            source.recycle()
            imageLoader.shutdown()
        }
    }
}