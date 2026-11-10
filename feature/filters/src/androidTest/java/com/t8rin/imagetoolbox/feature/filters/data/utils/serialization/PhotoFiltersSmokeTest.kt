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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AdaptiveBinarizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AgxToneMappingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AutostereogramParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BayerSensorParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DaltonizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DctGlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GuidedSmoothingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LcdSubpixelsParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RetroHardwareParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RisographParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SignedDistanceFieldParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SpectralNotchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SstvReceptionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WienerDeconvolutionParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiAdaptiveBinarizationFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPalCompositeFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiSpectralNotchFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.feature.filters.data.AndroidFilterProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

@RunWith(AndroidJUnit4::class)
class PhotoFiltersSmokeTest {
    private val provider = AndroidFilterProvider()

    @Test
    fun allFiltersRenderFromRegistryAndRoundTripParameters() = runBlocking {
        val groups = listOf(
            UiFilter.Group.Glitch,
            UiFilter.Group.Pixelation,
            UiFilter.Group.Effects,
            UiFilter.Group.Color,
            UiFilter.Group.Light,
            UiFilter.Group.Enhancement,
            UiFilter.Group.Artistic
        )
        val names = setOf(
            "UiAdaptiveBinarizationFilter",
            "UiAgxToneMappingFilter",
            "UiAnamorphicStreaksFilter",
            "UiAnisotropicKuwaharaFilter",
            "UiAutostereogramFilter",
            "UiBayerSensorFilter",
            "UiCapillaryInkBleedFilter",
            "UiCmykPressFilter",
            "UiContourHatchingFilter",
            "UiContrastPreservingGrayscaleFilter",
            "UiDaltonizationFilter",
            "UiDctGlitchFilter",
            "UiDiffractionPsfFilter",
            "UiDirectionalEmbroideryFilter",
            "UiDomainTransformFilter",
            "UiFilmGrainFilter",
            "UiFilmHalationFilter",
            "UiFilmNegativeFilter",
            "UiFourierPhaseScrambleFilter",
            "UiGuidedSmoothingFilter",
            "UiL0SmoothingFilter",
            "UiLayeredPaperCutFilter",
            "UiLcdSubpixelsFilter",
            "UiNonLocalMeansFilter",
            "UiPalCompositeFilter",
            "UiPaletteRemapFilter",
            "UiPhotoCrossStitchFilter",
            "UiRetroHardwareFilter",
            "UiRfReceptionFilter",
            "UiRgbChannelRegistrationFilter",
            "UiRisographFilter",
            "UiRollingGuidanceFilter",
            "UiSecamFilter",
            "UiSignedDistanceFieldFilter",
            "UiSingleLineDrawingFilter",
            "UiSpectralNotchFilter",
            "UiSstvReceptionFilter",
            "UiThermalReceiptFilter",
            "UiWaveletDenoiseFilter",
            "UiWienerDeconvolutionFilter",
            "UiXdogFilter"
        )
        val filters = groups.flatMap { it.filters(canAddTemplates = true) }.filter {
            it.javaClass.simpleName in names
        }
        assertEquals(41, filters.size)
        assertEquals(41, filters.map { provider.filterToTransformation(it).cacheKey }.toSet().size)
        for ((width, height) in listOf(135 to 97, 1 to 3, 8 to 8)) {
            val input = diagnostic(width, height)
            val size = IntegerSize(width, height)
            for (filter in filters) {
                val name = filter.javaClass.simpleName
                val variant = when (val value = filter.value) {
                    is AdaptiveBinarizationParams -> value.copy(algorithm = 3, invert = true)
                    is AgxToneMappingParams -> value.copy(look = 2)
                    is AutostereogramParams -> value.copy(invert = true)
                    is BayerSensorParams -> value.copy(pattern = 3, edgeAware = true)
                    is DaltonizationParams -> value.copy(visionType = 2)
                    is DctGlitchParams -> value.copy(blockSize = 1, operation = 2)
                    is GuidedSmoothingParams -> value.copy(guide = 1)
                    is LcdSubpixelsParams -> value.copy(subpixelOrder = 1)
                    is RetroHardwareParams -> value.copy(system = 1)
                    is RisographParams -> value.copy(inks = 2)
                    is SignedDistanceFieldParams -> value.copy(
                        alphaMask = true,
                        mode = 2,
                        invert = true
                    )

                    is SstvReceptionParams -> value.copy(transmissionMode = 1)
                    is WienerDeconvolutionParams -> value.copy(psf = 2)
                    else -> null
                }
                val configured = variant?.let { filter.copy(it) }
                for (source in listOfNotNull(filter, configured)) {
                    val serialized = listOf(source).toDatastoreString(includeValue = true)
                    val restored = serialized.toFiltersList(includeValue = true).single()
                    assertEquals(name, restored.javaClass.simpleName)
                    assertEquals(source.value, restored.value)
                    val transformation = provider.filterToTransformation(restored)
                    val savedTransformation =
                        listOf(transformation as Filter<*>).toDatastoreString(includeValue = true)
                    val restoredTransformation =
                        savedTransformation.toFiltersList(includeValue = true).single()
                    assertEquals(transformation.javaClass, restoredTransformation.javaClass)
                    assertEquals(restored.value, restoredTransformation.value)
                    assertEquals(restored.value, restoredTransformation.toUiFilter().value)
                    assertEquals(
                        restored.value,
                        UiFilter.fromSerializationKey(
                            (restored as UiFilter<*>).serializationKey,
                            restored.value
                        )?.value
                    )
                    val output = try {
                        transformation.transform(input, size)
                    } catch (e: Exception) {
                        throw AssertionError(
                            "$name failed at ${width}x$height with ${restored.value}",
                            e
                        )
                    }
                    assertEquals(name, width, output.width)
                    assertEquals(name, height, output.height)
                    val alphaMask = name == "UiSignedDistanceFieldFilter" && source === configured
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            assertEquals(
                                "$name alpha ($x, $y)",
                                if (alphaMask) 255 else Color.alpha(input.getPixel(x, y)),
                                Color.alpha(output.getPixel(x, y))
                            )
                        }
                    }
                    if (width > 100 && source === filter && name != "UiRgbChannelRegistrationFilter") {
                        assertFalse("$name produced unchanged output", input.sameAs(output))
                    }
                    if (output !== input) output.recycle()
                }
            }
            input.recycle()
        }
    }

    @Test
    fun cancellationInterruptsNativeFilterAndTheBitmapCanBeUsedAgain() = runBlocking {
        val source = diagnostic(4500, 3000)
        val size = IntegerSize(source.width, source.height)
        val filter = provider.filterToTransformation(UiPalCompositeFilter())
        repeat(3) {
            val start = System.nanoTime()
            val job = launch(Dispatchers.Default) {
                filter.transform(source, size)
                error("Large image completed before cancellation")
            }
            delay(25)
            job.cancelAndJoin()
            assertTrue(job.isCancelled)
            assertTrue(
                "Native work did not stop promptly",
                System.nanoTime() - start < 900_000_000L
            )
        }
        assertEquals(43, Color.alpha(source.getPixel(0, 0)))
        val small = diagnostic(79, 57)
        assertFalse(small.sameAs(filter.transform(small, IntegerSize(79, 57))))
        source.recycle()
        small.recycle()
    }

    @Test
    fun palDelayLineReducesAlternatingChromaErrors() = runBlocking {
        val input = Bitmap.createBitmap(144, 32, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.rgb(110, 140, 185))
        }
        val size = IntegerSize(input.width, input.height)
        suspend fun render(compensation: Float): Bitmap {
            val value = UiPalCompositeFilter().value.copy(
                noise = 0f, phaseError = 30, lumaFilter = 1f, lineCompensation = compensation
            )
            return provider.filterToTransformation(UiPalCompositeFilter(value))
                .transform(input, size)
        }

        fun lineError(image: Bitmap): Int =
            abs(Color.red(image.getPixel(72, 1)) - Color.red(image.getPixel(72, 2)))

        val raw = render(0f)
        val corrected = render(1f)
        assertTrue(lineError(raw) > 5)
        assertTrue(lineError(corrected) < lineError(raw) / 4)
        listOf(input, raw, corrected).forEach(Bitmap::recycle)
    }

    @Test
    fun spectralNotchRejectsItsBandAndRetainsFineDetailAtFullSize() = runBlocking {
        val width = 3072
        val height = 16
        val cycles = 64
        val wave = DoubleArray(width) { sin(2.0 * PI * cycles * it / width) }
        val row = IntArray(width) { x ->
            val level = (128 + 42 * wave[x]).roundToInt() + if (x % 2 == 0) 22 else -22
            Color.rgb(level, level, level)
        }
        val input = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            for (y in 0 until height) setPixels(row, 0, width, 0, y, width, 1)
        }
        val filter = UiSpectralNotchFilter(
            SpectralNotchParams(
                frequencyX = cycles / 2048f,
                frequencyY = 0f,
                bandwidth = 0.002f,
                strength = 1f
            )
        )
        val output =
            provider.filterToTransformation(filter).transform(input, IntegerSize(width, height))

        fun amplitude(image: Bitmap, basis: (Int) -> Double): Double {
            return abs((0 until width).sumOf { x ->
                (Color.red(image.getPixel(x, height / 2)) - 128) * basis(x)
            }) / width
        }
        assertTrue(amplitude(output) { wave[it] } < amplitude(input) { wave[it] } * 0.2)
        assertTrue(amplitude(output) { if (it % 2 == 0) 1.0 else -1.0 } >
                amplitude(input) { if (it % 2 == 0) 1.0 else -1.0 } * 0.9)
        listOf(input, output).forEach(Bitmap::recycle)
    }

    @Test
    fun nickThresholdUsesTheVariancePlusTheLocalSecondMoment() = runBlocking {
        val levels = intArrayOf(0, 0, 255, 255, 82, 0, 255, 0, 255)
        val input = Bitmap.createBitmap(
            IntArray(9) { Color.rgb(levels[it], levels[it], levels[it]) },
            3, 3, Bitmap.Config.ARGB_8888
        )
        val params = UiAdaptiveBinarizationFilter().value.copy(algorithm = 3, coefficient = 0.22f)
        val output = provider.filterToTransformation(UiAdaptiveBinarizationFilter(params))
            .transform(input, IntegerSize(3, 3))
        assertEquals(Color.WHITE, output.getPixel(1, 1))
        listOf(input, output).forEach(Bitmap::recycle)
    }

    private fun diagnostic(width: Int, height: Int): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val pixels = IntArray(width)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val edge = x > width / 2 && y > height / 2
                    pixels[x] = Color.argb(
                        if (x % 7 == 0) 43 else 255,
                        if (edge) 255 else (x * 11 + y * 5) and 255,
                        if (edge) 252 else (x * 5 + y * 13) and 255,
                        if (edge) 247 else (x * 3 + y * 7) and 255
                    )
                }
                setPixels(pixels, 0, width, 0, y, width, 1)
            }
        }
}
