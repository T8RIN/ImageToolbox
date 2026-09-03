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

package com.t8rin.imagetoolbox.feature.fractal_generation.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class FractalViewportTest {

    @Test
    fun anchoredZoomKeepsComplexPointUnderAnchor() {
        val viewport = FractalViewport.of(
            centerReal = "-0.7436438870371510000000000000000000000001",
            centerImaginary = "0.1318259042053300000000000000000000000001",
            span = "1E-80"
        )
        val anchorX = 0.173
        val anchorY = 0.811
        val aspectRatio = 16.0 / 9.0
        val before = viewport.pointAt(anchorX, anchorY, aspectRatio)
        val zoomed = viewport.zoomBy(
            factor = 13.0,
            anchorX = anchorX,
            anchorY = anchorY,
            aspectRatio = aspectRatio
        )
        val after = zoomed.pointAt(anchorX, anchorY, aspectRatio)

        assertBigDecimalNear(before.real, after.real, BigDecimal("1E-112"))
        assertBigDecimalNear(before.imaginary, after.imaginary, BigDecimal("1E-112"))
    }

    @Test
    fun panThenInversePanRestoresDeepViewportCenter() {
        val viewport = FractalViewport.of(
            centerReal = "-0.743643887037151",
            centerImaginary = "0.131825904205330",
            span = "1E-150"
        )
        val moved = viewport
            .panBy(normalizedDeltaX = 0.123, normalizedDeltaY = -0.456, aspectRatio = 1.7)
            .panBy(normalizedDeltaX = -0.123, normalizedDeltaY = 0.456, aspectRatio = 1.7)

        assertBigDecimalNear(viewport.centerReal, moved.centerReal, BigDecimal("1E-184"))
        assertBigDecimalNear(viewport.centerImaginary, moved.centerImaginary, BigDecimal("1E-184"))
        assertEquals(0, viewport.span.compareTo(moved.span))
    }

    @Test
    fun coordinateMappingUsesVerticalSpanAndScreenYAxis() {
        val viewport = FractalViewport.of(
            centerReal = "1",
            centerImaginary = "2",
            span = "4"
        )

        val topLeft = viewport.pointAt(
            normalizedX = 0.0,
            normalizedY = 0.0,
            aspectRatio = 2.0
        )
        val bottomRight = viewport.pointAt(
            normalizedX = 1.0,
            normalizedY = 1.0,
            aspectRatio = 2.0
        )

        assertEquals(0, BigDecimal("-3").compareTo(topLeft.real))
        assertEquals(0, BigDecimal("4").compareTo(topLeft.imaginary))
        assertEquals(0, BigDecimal("5").compareTo(bottomRight.real))
        assertEquals(0, BigDecimal.ZERO.compareTo(bottomRight.imaginary))
    }

    @Test
    fun viewportClampsAtPracticalDeepZoomLimit() {
        val viewport = FractalViewport.of(
            centerReal = "0",
            centerImaginary = "0",
            span = "1E-500"
        )

        assertEquals(0, FractalViewport.MIN_SPAN.compareTo(viewport.span))
        assertTrue(viewport.decimalZoomDepth >= 300.0)
    }

    private fun assertBigDecimalNear(
        expected: BigDecimal,
        actual: BigDecimal,
        tolerance: BigDecimal
    ) {
        assertTrue(
            "Expected $actual to be within $tolerance of $expected",
            expected.subtract(actual).abs() <= tolerance
        )
    }
}
