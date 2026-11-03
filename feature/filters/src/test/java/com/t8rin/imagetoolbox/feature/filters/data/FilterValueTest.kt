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

package com.t8rin.imagetoolbox.feature.filters.data

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.copy
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BrightnessParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ColorBalanceParams
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Test

class FilterValueTest {

    @Test
    fun factoriesAndGeneratedParamsShareValueEquality() {
        val value = FilterValues.ascii(
            backgroundColor = ColorModel(-13408615),
            font = null,
            fontSize = 17.5f,
            gradient = " .#@",
            isGrayscale = true
        )
        val params = AsciiParams(
            backgroundColor = value.backgroundColor,
            font = value.font,
            fontSize = value.fontSize,
            gradient = value.gradient,
            isGrayscale = value.isGrayscale
        )
        val copied = value.copy()

        assertNotSame(value, copied)
        assertEquals(value, copied)
        assertEquals(value, params)
        assertEquals(params, value)
        assertEquals(value.hashCode(), params.hashCode())
        assertEquals(value.hashCode(), copied.hashCode())
    }

    @Test
    fun copyChangesOnlySpecifiedFieldsAndPreservesNullableFields() {
        val original = FilterValues.ascii(
            backgroundColor = ColorModel(-13408615),
            font = null,
            fontSize = 17.5f,
            gradient = " .#@",
            isGrayscale = true
        )
        val changed = original.copy(fontSize = 23f)

        assertEquals(23f, changed.fontSize)
        assertEquals(original.backgroundColor, changed.backgroundColor)
        assertEquals(original.gradient, changed.gradient)
        assertEquals(original.isGrayscale, changed.isGrayscale)
        assertNull(changed.font)
        assertEquals(17.5f, original.fontSize)
        assertEquals(original, original.copy(font = FontType.File("/font.ttf")).copy(font = null))
    }

    @Test
    fun floatEqualityKeepsNaNAndSignedZeroConsistentWithHashCodes() {
        for (number in listOf(Float.NaN, Float.fromBits(0x7fc00001), 0f, -0f, 0.75f)) {
            val value = FilterValues.brightness(number)
            val params = BrightnessParams(bias = number)

            assertEquals(value, params)
            assertEquals(params, value)
            assertEquals(value.hashCode(), params.hashCode())
        }
        assertEquals(
            FilterValues.brightness(Float.NaN),
            BrightnessParams(bias = Float.fromBits(0x7fc00001))
        )
        assertNotEquals(FilterValues.brightness(0f), BrightnessParams(bias = -0f))
    }

    @Test
    fun arraysUseContentsAcrossFactoriesParamsAndCopies() {
        val adjustments = floatArrayOf(Float.NaN, -0f, 0f, 0.5f)
        val value = FilterValues.colorBalance(adjustments)
        val params = ColorBalanceParams(adjustments = adjustments.copyOf())
        val copied = value.copy(adjustments = adjustments.copyOf())

        assertNotSame(value.adjustments, copied.adjustments)
        assertEquals(value, params)
        assertEquals(params, value)
        assertEquals(value, copied)
        assertEquals(value.hashCode(), params.hashCode())
        assertEquals(value.hashCode(), copied.hashCode())

        val changed = value.copy(adjustments = floatArrayOf(Float.NaN, 0f, 0f, 0.5f))
        assertNotEquals(value, changed)
        assertArrayEquals(adjustments, value.adjustments, 0f)
    }
}
