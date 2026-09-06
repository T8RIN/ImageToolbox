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
import android.graphics.Canvas
import android.graphics.Color
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.ImageLoader
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Outline
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.AssetRegistry
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupMapper
import com.t8rin.imagetoolbox.feature.markup_layers.data.project.MarkupProjectFile
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.LayersRenderer
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.drawShapeLayer
import com.t8rin.imagetoolbox.feature.markup_layers.data.utils.resolveShapeLayerRenderData
import com.t8rin.imagetoolbox.feature.markup_layers.domain.DropShadow
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerPosition
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProject
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectHistorySnapshot
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupProjectResult
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ProjectBackground
import com.t8rin.imagetoolbox.feature.markup_layers.domain.ShapeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.reflect.Proxy
import kotlin.math.ceil
import androidx.compose.ui.graphics.Canvas as ComposeCanvas

@RunWith(AndroidJUnit4::class)
class GradientLayerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext.apply {
        initAppContext()
    }
    private val palette =
        GradientPalette.Custom(listOf(ColorModel(Color.RED), ColorModel(Color.BLUE)))
    private val position = LayerPosition(
        currentCanvasSize = IntegerSize(512, 512),
        coerceToBounds = false,
        isVisible = true
    )

    @Test
    fun shapePreviewMatchesExportForEveryMode() {
        val transparentPalette = GradientPalette.Custom(
            listOf(ColorModel(Color.TRANSPARENT), ColorModel(0x800000FF.toInt()))
        )
        for (mode in ShapeMode.entries) {
            for (gradient in listOf(null, palette, transparentPalette)) {
                val type = LayerType.Shape.Default.copy(
                    shapeMode = mode,
                    color = Color.GREEN,
                    gradientPalette = gradient
                )
                val data = resolveShapeLayerRenderData(type, 512f)
                val width = ceil(data.contentWidth).toInt()
                val height = ceil(data.contentHeight).toInt()
                val exported = createBitmap(width, height)
                Canvas(exported).drawShapeLayer(type, data)
                val preview = createBitmap(width, height)
                CanvasDrawScope().draw(
                    density = Density(1f),
                    layoutDirection = LayoutDirection.Ltr,
                    canvas = ComposeCanvas(Canvas(preview)),
                    size = Size(width.toFloat(), height.toFloat())
                ) {
                    drawShapeLayer(type, data)
                }
                assertTrue("Preview differs for ${mode.kind}, $gradient", exported.sameAs(preview))
                assertTrue("Empty ${mode.kind}", exported.pixels().any { Color.alpha(it) > 0 })
                val pixels = exported.pixels().filter { Color.alpha(it) == 255 }
                if (gradient == palette) {
                    assertTrue(pixels.any { Color.red(it) > Color.blue(it) + 80 })
                    assertTrue(pixels.any { Color.blue(it) > Color.red(it) + 80 })
                } else if (gradient == null) {
                    assertTrue(pixels.isNotEmpty())
                    assertTrue(pixels.all { it == Color.GREEN })
                } else {
                    assertTrue(exported.pixels().all { Color.alpha(it) <= 128 })
                }
                exported.recycle()
                preview.recycle()
            }
        }
    }

    @Test
    fun textExportKeepsGradientWithOutlineShadowAndTransforms() = runBlocking {
        val dispatchers = object : DispatchersHolder {
            override val uiDispatcher = Dispatchers.Main
            override val ioDispatcher = Dispatchers.IO
            override val encodingDispatcher = Dispatchers.Default
            override val decodingDispatcher = Dispatchers.Default
            override val defaultDispatcher = Dispatchers.Default
        }
        val imageLoader = ImageLoader.Builder(context).build()
        val renderer = LayersRenderer(context, imageLoader, dispatchers)
        val background = createBitmap(512, 512)
        try {
            for (decorated in listOf(false, true)) {
                val text = LayerType.Text.Default.copy(
                    text = "MMMMMMMM\nMMMMMMMM",
                    size = 1f,
                    gradientPalette = palette,
                    outline = if (decorated) Outline(Color.GREEN, 3f) else null,
                    shadow = if (decorated) DropShadow.Default else null
                )
                val layer = MarkupLayer(
                    type = text,
                    position = position.copy(rotation = if (decorated) 25f else 0f, alpha = 0.7f)
                )
                val result = renderer.render(background, listOf(layer), fontScale = 1f)
                val pixels = result.pixels().filter { Color.alpha(it) > 50 }
                assertTrue(pixels.any { Color.red(it) > Color.blue(it) + 80 })
                assertTrue(pixels.any { Color.blue(it) > Color.red(it) + 80 })
                if (decorated) assertTrue(pixels.any { Color.green(it) > 220 })
                assertTrue(pixels.all { Color.alpha(it) <= 180 })
                val solid = renderer.render(
                    background, listOf(
                        layer.copy(
                            type = text.copy(
                                gradientPalette = null,
                                color = Color.RED
                            )
                        )
                    ), fontScale = 1f
                )
                assertTrue(solid.pixels().none { Color.blue(it) > Color.red(it) + 80 })
                result.recycle()
                solid.recycle()
            }
        } finally {
            background.recycle()
            imageLoader.shutdown()
        }
    }

    @Test
    fun projectRoundTripPreservesPalettesInLayersGroupsAndHistory() = runBlocking {
        val mapper = MarkupMapper(
            Proxy.newProxyInstance(
            SettingsManager::class.java.classLoader,
            arrayOf(SettingsManager::class.java)
        ) { _, method, _ -> error("Unexpected settings access: ${method.name}") } as SettingsManager)
        val text = MarkupLayer(LayerType.Text.Default.copy(gradientPalette = palette), position)
        val shape = MarkupLayer(
            LayerType.Shape.Default.copy(gradientPalette = GradientPalette.Fire), position
        )
        val layers = listOf(text, shape, shape.copy(groupedLayers = listOf(text, shape)))
        val project = MarkupProject(
            background = ProjectBackground.None,
            layers = layers,
            lastLayers = listOf(text),
            undoneLayers = listOf(shape),
            history = listOf(MarkupProjectHistorySnapshot(ProjectBackground.None, layers)),
            redoHistory = listOf(
                MarkupProjectHistorySnapshot(
                    ProjectBackground.None,
                    layers.reversed()
                )
            )
        )
        val adapter = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            .adapter(MarkupProjectFile::class.java)
        val json = adapter.toJson(mapper.map(project, AssetRegistry()))
        val restored =
            mapper.map(adapter.fromJson(json)!!, context.cacheDir) as MarkupProjectResult.Success
        assertEquals(project, restored.project)

        val legacyJson = json.replace(Regex(",\"gradientPalette\":\"[^\"]*\""), "")
        val legacy = mapper.map(
            adapter.fromJson(legacyJson)!!,
            context.cacheDir
        ) as MarkupProjectResult.Success
        assertNull((legacy.project.layers[0].type as LayerType.Text).gradientPalette)
        assertNull((legacy.project.layers[1].type as LayerType.Shape).gradientPalette)
    }

    private fun Bitmap.pixels(): List<Int> = IntArray(width * height).also {
        getPixels(it, 0, width, 0, 0, width, height)
    }.toList()
}
