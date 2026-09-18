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

package com.t8rin.imagetoolbox.core.ui.widget.value

import com.t8rin.imagetoolbox.core.domain.utils.NEAREST_ODD_ROUNDING
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.roundToInt

class ValueDialogTest {

    @Test
    fun stepMatchesSliderSteps() {
        assertEquals(
            10f,
            valueStep(
                valueState = "100",
                valueRange = 10f..500f,
                steps = 48,
                valueTransformation = { (it / 10).toInt() * 10 }
            ),
            0f
        )
        assertEquals(
            10f,
            valueStep(
                valueState = "20",
                valueRange = 0f..100f,
                steps = 9,
                valueTransformation = { it.roundToInt() }
            ),
            0f
        )
    }

    @Test
    fun stepMatchesValueTransformation() {
        assertEquals(1f, transformedStep("10", 1f..100f, Float::roundToInt), 0f)
        assertEquals(0.01f, transformedStep("0.1", 0f..1f) { it.roundTo(2) }, 0f)
        assertEquals(0.0001f, transformedStep("0.01", 0f..1f) { it.roundTo(4) }, 0f)
        assertEquals(10f, transformedStep("100", 10f..500f) {
            (it / 10).roundToInt() * 10
        }, 0f)
        assertEquals(2f, transformedStep("5", 1f..99f) {
            it.roundTo(NEAREST_ODD_ROUNDING)
        }, 0f)
    }

    @Test
    fun stepMatchesDisplayedPrecisionForContinuousValues() {
        assertEquals(1f, transformedStep("10", 0f..100f) { it }, 0f)
        assertEquals(0.1f, transformedStep("0.1", 0f..1f) { it }, 0f)
        assertEquals(0.01f, transformedStep("0.02", 0f..1f) { it }, 0f)
    }

    @Test
    fun stepByTransformsAndClampsValue() {
        assertEquals(0.13f, 0.12f.stepBy(1, 0f..1f, 0.01f) { it.roundTo(2) }, 0f)
        assertEquals(0f, 0f.stepBy(-1, 0f..1f, 0.01f) { it.roundTo(2) }, 0f)
        assertEquals(99f, 99f.stepBy(1, 1f..99f, 2f) {
            it.roundTo(NEAREST_ODD_ROUNDING)
        }, 0f)
    }

    private fun transformedStep(
        valueState: String,
        valueRange: ClosedFloatingPointRange<Float>,
        valueTransformation: (Float) -> Number
    ) = valueStep(
        valueState = valueState,
        valueRange = valueRange,
        steps = 0,
        valueTransformation = valueTransformation
    )
}