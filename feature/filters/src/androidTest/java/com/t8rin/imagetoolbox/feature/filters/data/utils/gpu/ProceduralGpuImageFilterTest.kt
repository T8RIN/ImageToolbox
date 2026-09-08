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

package com.t8rin.imagetoolbox.feature.filters.data.utils.gpu

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ProceduralEffect
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import jp.co.cyberagent.android.gpuimage.GPUImage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs
import java.io.File

@RunWith(AndroidJUnit4::class)
class ProceduralGpuImageFilterTest {

    @Test
    fun allShadersRenderAtDefaultAndExtremeValuesWithoutLosingAlpha() {
        for ((width, height) in listOf(48 to 32, 32 to 48, 33 to 33)) {
            val source = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.argb(128, 80, 120, 160))
            }
            for (effect in ProceduralEffect.entries) {
                val settings = listOf(
                    ProceduralParams(),
                    ProceduralParams(effect.parameters.associate { it.name to it.range.start }),
                    ProceduralParams(effect.parameters.associate { it.name to it.range.endInclusive })
                )
                for (params in settings) {
                    val result = render(source, effect, params)
                    assertEquals(source.width, result.width)
                    assertEquals(source.height, result.height)
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            assertTrue(
                                "${effect.name} at ($x, $y): alpha lost with $params",
                                abs(Color.alpha(result.getPixel(x, y)) - 128) <= 1
                            )
                        }
                    }
                    result.recycle()
                }
            }
            source.recycle()
        }
    }

    @Test
    fun defaultEffectsChangeTheDiagnosticImage() {
        val source = source(128, 96)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (effect in ProceduralEffect.entries) {
            val result = render(source, effect, ProceduralParams())
            assertTrue("${effect.name} should change the diagnostic image", !result.sameAs(source))
            File(context.getExternalFilesDir(null), "${effect.name}.png").outputStream().use {
                result.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            result.recycle()
        }
        source.recycle()
    }

    @Test
    fun zeroDisplacementPreservesSourceEvenWithTransformedFields() {
        val source = source()
        val params = ProceduralParams(
            mapOf(
                "intensity" to 0f,
                "displacement" to 0f,
                "distortion" to 0f,
                "lighting" to 0f,
                "offsetX" to 0.23f,
                "offsetY" to -0.17f,
                "rotation" to 37f,
                "scale" to 1.7f
            )
        )
        for (effect in ProceduralEffect.entries.filter { it !in coordinateMappings }) {
            val result = render(source, effect, params)
            assertPixels(source, result, effect.name)
            result.recycle()
        }
        source.recycle()
    }

    @Test
    fun seededDistortionIsRepeatableAndRespondsToSeed() {
        val source = source()
        val effect = ProceduralEffect.BrokenGlass
        val first = render(source, effect, ProceduralParams(mapOf("seed" to 17f)))
        val repeat = render(source, effect, ProceduralParams(mapOf("seed" to 17f)))
        val changed = render(source, effect, ProceduralParams(mapOf("seed" to 91f)))
        assertPixels(first, repeat, "same seed")
        assertTrue((0 until source.height).any { y ->
            (0 until source.width).any { x -> first.getPixel(x, y) != changed.getPixel(x, y) }
        })
        listOf(source, first, repeat, changed).forEach(Bitmap::recycle)
    }

    @Test
    fun oddWidthIdentityKeepsTheOriginalPixels() {
        val source = source(33, 31)
        val params = ProceduralParams(
            mapOf(
                "intensity" to 0f, "displacement" to 0f,
                "distortion" to 0f, "lighting" to 0f
            )
        )
        for (effect in ProceduralEffect.entries.filter { it !in coordinateMappings }) {
            val result = render(source, effect, params)
            assertPixels(source, result, "odd width: ${effect.name}")
            result.recycle()
        }
        source.recycle()
    }

    private val coordinateMappings = setOf(
        ProceduralEffect.SpiralArms,
        ProceduralEffect.LogarithmicSpiral,
        ProceduralEffect.SpiralDroste,
        ProceduralEffect.SquareSpiralDroste,
        ProceduralEffect.HexKaleidoscope,
        ProceduralEffect.SmoothKaleidoscope,
        ProceduralEffect.RandomKaleidoscopeGrid,
        ProceduralEffect.SquareFresnel,
        ProceduralEffect.InversionFractal
    )

    private fun source(width: Int = 48, height: Int = 32) =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            for (y in 0 until height) {
                for (x in 0 until width) {
                    setPixel(
                        x, y, Color.argb(
                            255, x * 255 / (width - 1), y * 255 / (height - 1),
                            if ((x / 8 + y / 8) % 2 == 0) 40 else 210
                        )
                    )
                }
            }
        }

    private fun render(source: Bitmap, effect: ProceduralEffect, params: ProceduralParams): Bitmap =
        GPUImage(InstrumentationRegistry.getInstrumentation().targetContext).apply {
            setImage(source)
            setFilter(ProceduralGpuImageFilter(effect, params))
        }.bitmapWithFilterApplied

    private fun assertPixels(expected: Bitmap, actual: Bitmap, label: String) {
        for (y in 0 until expected.height) {
            for (x in 0 until expected.width) {
                for (shift in listOf(0, 8, 16, 24)) {
                    val a = (expected.getPixel(x, y) ushr shift) and 255
                    val b = (actual.getPixel(x, y) ushr shift) and 255
                    assertTrue("$label at ($x, $y), channel=$shift: $a != $b", abs(a - b) <= 2)
                }
            }
        }
    }
}