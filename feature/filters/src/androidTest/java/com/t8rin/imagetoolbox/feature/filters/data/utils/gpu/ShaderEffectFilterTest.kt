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
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.feature.filters.data.AndroidFilterProvider
import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import com.t8rin.imagetoolbox.feature.filters.data.model.GeneratedFilterRegistry
import com.t8rin.imagetoolbox.feature.filters.data.model.StructuredParameters
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.StoredParameterValues
import com.t8rin.imagetoolbox.feature.filters.presentation.model.GeneratedFilterCatalog
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class ShaderEffectFilterTest {

    private val provider = AndroidFilterProvider()

    private val shaders by lazy {
        UiFilter.groups.flatMap { it.filters(canAddTemplates = true) }
            .filter { provider.filterToTransformation(it) is ShaderEffectFilter<*> }
            .also { assertEquals(38, it.size) }
    }

    @Test
    fun allShadersRenderAtDefaultAndExtremeValuesWithoutLosingAlpha() = runBlocking {
        for ((width, height) in listOf(48 to 32, 32 to 48, 33 to 33)) {
            val source = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.argb(128, 80, 120, 160))
            }
            for (filter in shaders) {
                val settings = listOf(filter.value, extreme(filter, false), extreme(filter, true))
                for (params in settings) {
                    val result = render(source, filter, params)
                    assertEquals(source.width, result.width)
                    assertEquals(source.height, result.height)
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            assertTrue(
                                "${filter.javaClass.simpleName} at ($x, $y): alpha lost with $params",
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
    fun defaultEffectsChangeTheDiagnosticImage() = runBlocking {
        val source = source(128, 96)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (filter in shaders) {
            val result = render(source, filter, filter.value)
            assertTrue(
                "${filter.javaClass.simpleName} should change the diagnostic image",
                !result.sameAs(source)
            )
            File(
                context.getExternalFilesDir(null),
                "${filter.javaClass.simpleName}.png"
            ).outputStream().use {
                result.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            result.recycle()
        }
        source.recycle()
    }

    @Test
    fun zeroDisplacementPreservesSourceEvenWithTransformedFields() = runBlocking {
        val source = source()
        val settings = "intensity=0;displacement=0;distortion=0;lighting=0;" +
                "offsetX=0.23;offsetY=-0.17;rotation=37;scale=1.7"
        for (filter in shaders.filter { it.javaClass.simpleName !in coordinateMappings }) {
            val result = render(source, filter, withFields(filter, settings))
            assertPixels(source, result, filter.javaClass.simpleName)
            result.recycle()
        }
        source.recycle()
    }

    @Test
    fun seededDistortionIsRepeatableAndRespondsToSeed() = runBlocking {
        val source = source()
        val filter = shaders.single { it.javaClass.simpleName == "UiBrokenGlassFilter" }
        val first = render(source, filter, withFields(filter, "seed=17"))
        val repeat = render(source, filter, withFields(filter, "seed=17"))
        val changed = render(source, filter, withFields(filter, "seed=91"))
        assertPixels(first, repeat, "same seed")
        assertTrue((0 until source.height).any { y ->
            (0 until source.width).any { x -> first.getPixel(x, y) != changed.getPixel(x, y) }
        })
        listOf(source, first, repeat, changed).forEach(Bitmap::recycle)
    }

    @Test
    fun oddWidthIdentityKeepsTheOriginalPixels() = runBlocking {
        val source = source(33, 31)
        val settings = "intensity=0;displacement=0;distortion=0;lighting=0"
        for (filter in shaders.filter { it.javaClass.simpleName !in coordinateMappings }) {
            val result = render(source, filter, withFields(filter, settings))
            assertPixels(source, result, "odd width: ${filter.javaClass.simpleName}")
            result.recycle()
        }
        source.recycle()
    }

    private val coordinateMappings = setOf(
        "UiSpiralArmsFilter",
        "UiLogarithmicSpiralFilter",
        "UiSpiralDrosteFilter",
        "UiSquareSpiralDrosteFilter",
        "UiHexKaleidoscopeFilter",
        "UiSmoothKaleidoscopeFilter",
        "UiRandomKaleidoscopeGridFilter",
        "UiSquareFresnelFilter",
        "UiInversionFractalFilter"
    )

    private fun extreme(filter: UiFilter<*>, upper: Boolean): Filter.ParameterValue {
        val parameters = parameters(filter)
        val values = encodedFields(filter).toMutableMap()
        parameters.fields.forEach { (property, field) ->
            val range = field.info.valueRange
            val end = if (upper) range.endInclusive else range.start
            when (field) {
                is FilterField.Number -> values[property.name] = end.toString()
                is FilterField.Integer -> values[property.name] = end.toInt().toString()
                else -> Unit
            }
        }
        return restoreParams(filter, values)
    }

    private fun withFields(filter: UiFilter<*>, settings: String): Filter.ParameterValue {
        val fields = encodedFields(filter).toMutableMap()
        settings.split(';').forEach { setting ->
            val (name, value) = setting.split('=', limit = 2)
            if (name in fields) fields[name] = value
        }
        return restoreParams(filter, fields)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parameters(filter: UiFilter<*>): StructuredParameters<Filter.ParameterValue> =
        filter.schema.parameters as StructuredParameters<Filter.ParameterValue>

    @Suppress("UNCHECKED_CAST")
    private fun encodedFields(filter: UiFilter<*>): Map<String, String?> =
        parameters(filter).fields.map { (property, field) ->
            property.name to (field as FilterField<Any?>).encodeValue(
                property.get(filter.value as Filter.ParameterValue)
            )
        }.toMap()

    private fun restoreParams(
        filter: UiFilter<*>,
        fields: Map<String, String?>
    ): Filter.ParameterValue {
        val name = requireNotNull(GeneratedFilterRegistry.encodeParams(filter)).first
        return requireNotNull(
            GeneratedFilterRegistry.restoreStored(
                filter.serializationKey,
                name to StoredParameterValues.encode(fields),
                GeneratedFilterCatalog
            )?.value as? Filter.ParameterValue
        )
    }

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

    private suspend fun render(source: Bitmap, filter: UiFilter<*>, params: Any): Bitmap =
        provider.filterToTransformation(filter.copy(params))
            .transform(source, IntegerSize(source.width, source.height))

    private fun assertPixels(expected: Bitmap, actual: Bitmap, label: String) {
        assertEquals("$label width", expected.width, actual.width)
        assertEquals("$label height", expected.height, actual.height)
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