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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.imagetoolbox.core.domain.utils.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.GmicParameterInfo
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.ShaderUniform
import com.t8rin.imagetoolbox.feature.filters.data.utils.gpu.uniforms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterFieldTest {

    @Test
    fun seedSemanticsDoNotDependOnTitle() {
        assertFalse(intParam(R.string.seed, 0, 0..100).info.isSeed)
        assertTrue(seedParam(R.string.amount, 0, 0..100).info.isSeed)
        assertTrue(seedParam(R.string.amount, 0.5f, 0f..1f).info.isSeed)

        val ordinary = gmicInteger(R.string.seed, 0, 0f..100f).control as GmicParameterInfo.Number
        val seed = gmicSeed(R.string.amount, 0, 0f..100f).control as GmicParameterInfo.Number
        assertFalse(ordinary.isSeed)
        assertTrue(seed.isSeed)
    }

    @Test
    fun enumLabelsAreProvidedByTheSchema() {
        val field = enumParam(
            label = { if (it == Mode.First) R.string.start else R.string.end },
            default = { Mode.Second }
        )

        assertEquals(R.string.start, field.label(Mode.First))
        assertEquals(Mode.Second, field.default)
        assertEquals(Mode.First, field.decodeValue("First"))
        assertEquals("Second", field.encodeValue(Mode.Second))
        assertThrows(NoSuchElementException::class.java) { field.decodeValue("Unknown") }
    }

    @Test
    fun roundingSupportsExistingOddAndDecimalPlaceModes() {
        val odd = intParam(R.string.amount, 25, 3..600, NEAREST_ODD_ROUNDING)
        val tens = floatParam(default = 20f, range = 0f..100f, roundTo = -1)

        assertEquals(NEAREST_ODD_ROUNDING, odd.info.roundTo)
        assertEquals(-1, tens.info.roundTo)
    }

    @Test
    fun invalidNumericDefaultsFailWhenTheSchemaIsDeclared() {
        assertThrows(IllegalArgumentException::class.java) {
            floatParam(default = Float.NaN, range = 0f..1f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            floatParam(default = 2f, range = 0f..1f)
        }
        assertThrows(IllegalArgumentException::class.java) {
            intParam(R.string.amount, 0, 1..10)
        }
        assertThrows(IllegalArgumentException::class.java) {
            floatParam(default = 0f, range = 1f..0f)
        }
    }

    @Test
    fun customValuesCanDefineStableCacheContentIndependentlyOfTemplates() {
        class Custom(val amount: Float)

        val field = editorParam(default = { Custom(1f) }, cache = { it.amount })

        assertFalse(field.isSerializable)
        assertFalse(field.hasEditor)
        assertEquals(2f, field.cacheValue(Custom(2f)))
    }

    @Test
    fun nonFiniteStoredValuesAreRejectedWithoutClampingHistoricalValues() {
        val number = floatParam(default = 0f, range = 0f..1f)
        val array = floatArrayParam(2) { floatArrayOf(0f, 0f) }
        val point = pointParam({ OffsetModel(0f, 0f) })
        val curves = curvesParam { emptyList() }

        for (invalid in listOf("NaN", "Infinity", "-Infinity")) {
            assertThrows(IllegalArgumentException::class.java) { number.decodeValue(invalid) }
            assertThrows(IllegalArgumentException::class.java) { array.decodeValue("0*$invalid") }
            assertThrows(IllegalArgumentException::class.java) { point.decodeValue("0*$invalid") }
            assertThrows(IllegalArgumentException::class.java) { curves.decodeValue("[0*$invalid]") }
        }
        assertThrows(IllegalArgumentException::class.java) { point.decodeValue("0*1*2") }
        assertEquals(2f, number.decodeValue("2"))
    }

    @Test
    fun shaderUniformsAcceptCustomNumericDescriptors() {
        val custom = editorParam(
            default = { 0.25f },
            encode = { it.toString() },
            decode = String::toFloat
        )
        val parameters = structuredParameters<ShaderValue> {
            ShaderValue::amount.parameter(custom)
        }

        assertEquals(0.25f, parameters.field(ShaderValue::amount).default)
        assertEquals(0.5f, parameters.field(ShaderValue::amount).decodeValue("0.5"))
        assertEquals(
            listOf(ShaderUniform.Number("amount", 0.5f)),
            parameters.uniforms(ShaderValue(0.5f))
        )
        assertThrows(IllegalArgumentException::class.java) {
            parameters.uniforms(ShaderValue(Float.NaN))
        }
    }

    private data class ShaderValue(val amount: Float) : Filter.ParameterValue

    private enum class Mode { First, Second }
}