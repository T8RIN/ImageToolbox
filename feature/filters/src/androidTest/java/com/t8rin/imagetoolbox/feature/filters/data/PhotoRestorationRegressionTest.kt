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

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.opencv_tools.photo_filters.ColorSpillSuppression
import com.t8rin.opencv_tools.photo_filters.GradientDomainToneMapping
import com.t8rin.opencv_tools.photo_filters.SpecularHighlightSuppression
import com.t8rin.opencv_tools.photo_filters.UnderwaterRestoration
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class PhotoRestorationRegressionTest {

    @Test
    fun neutralGradientSettingsReturnTheOriginalBitmap() = runBlocking {
        val input = bitmap(17, 13, Color.rgb(37, 90, 156))
        try {
            assertSame(input, GradientDomainToneMapping.apply(input, compression = 0f, detail = 1f))
        } finally {
            input.recycle()
        }
    }

    @Test
    fun poissonBoundaryHandlingPreservesConstantOddAndOneDimensionalImages() = runBlocking {
        for ((width, height) in listOf(1 to 1, 1 to 19, 17 to 1, 17 to 23)) {
            val color = Color.rgb(44, 100, 177)
            val input = bitmap(width, height, color)
            val output = GradientDomainToneMapping.apply(input, compression = 0.85f, detail = 1.6f)
            try {
                assertEquals(width, output.width)
                assertEquals(height, output.height)
                for (y in 0 until height) for (x in 0 until width) assertColorClose(
                    color,
                    output.getPixel(x, y)
                )
            } finally {
                input.recycle()
                if (output !== input) output.recycle()
            }
        }
    }

    @Test
    fun despillLeavesNeutralsAndWarmSkinColorsUnchanged() = runBlocking {
        val colors = intArrayOf(Color.BLACK, Color.WHITE, Color.GRAY, Color.rgb(194, 132, 95))
        val input = Bitmap.createBitmap(colors, colors.size, 1, Bitmap.Config.ARGB_8888)
        try {
            for (color in 0..1) {
                val output = ColorSpillSuppression.apply(input, color = color, tolerance = 0f)
                try {
                    for (x in colors.indices) assertEquals(colors[x], output.getPixel(x, 0))
                } finally {
                    if (output !== input) output.recycle()
                }
            }
        } finally {
            input.recycle()
        }
    }

    @Test
    fun despillRemovesOnlyTheSelectedExcessChannel() = runBlocking {
        val input = bitmap(3, 1, Color.rgb(40, 180, 60))
        val output = ColorSpillSuppression.apply(input, color = 0, tolerance = 0f, recovery = 0f)
        try {
            assertEquals(Color.rgb(40, 60, 60), output.getPixel(1, 0))
        } finally {
            input.recycle()
            if (output !== input) output.recycle()
        }
    }

    @Test
    fun despillRecoveryIsNeutralAndPreservesLuminance() = runBlocking {
        for ((selected, color) in listOf(0 to Color.GREEN, 1 to Color.BLUE)) {
            val input = bitmap(3, 1, color)
            val output =
                ColorSpillSuppression.apply(input, color = selected, tolerance = 0f, recovery = 1f)
            try {
                val actual = output.getPixel(1, 0)
                assertEquals(Color.red(actual), Color.green(actual))
                assertEquals(Color.red(actual), Color.blue(actual))
                assertTrue(abs(luminance(color) - luminance(actual)) <= 1f)
            } finally {
                input.recycle()
                if (output !== input) output.recycle()
            }
        }
    }

    @Test
    fun despillRecoveryDoesNotReverseTheDominantChannel() = runBlocking {
        for ((selected, color) in listOf(
            0 to Color.rgb(48, 200, 73),
            1 to Color.rgb(48, 73, 200)
        )) {
            val input = bitmap(3, 1, color)
            try {
                for (recovery in listOf(0.5f, 1f)) {
                    val output =
                        ColorSpillSuppression.apply(input, color = selected, recovery = recovery)
                    try {
                        val actual = output.getPixel(1, 0)
                        val dominant =
                            if (selected == 0) Color.green(actual) else Color.blue(actual)
                        val alternate =
                            if (selected == 0) Color.blue(actual) else Color.green(actual)
                        assertTrue(dominant >= Color.red(actual))
                        assertTrue(dominant >= alternate)
                        assertTrue(dominant - maxOf(Color.red(actual), alternate) <= 21)
                        if (recovery == 1f) assertTrue(abs(luminance(color) - luminance(actual)) <= 1f)
                    } finally {
                        if (output !== input) output.recycle()
                    }
                }
            } finally {
                input.recycle()
            }
        }
    }

    @Test
    fun underwaterPriorHandlesTinyAndTransparentInputs() = runBlocking {
        for ((width, height) in listOf(1 to 1, 1 to 17, 19 to 1, 17 to 13)) {
            val input = bitmap(width, height, Color.argb(173, 28, 112, 170))
            val original = input.getPixel(0, 0)
            val output = UnderwaterRestoration.apply(
                input,
                radius = Float.NaN,
                restoration = 1f,
                redRecovery = 1f
            )
            try {
                assertEquals(width, output.width)
                assertEquals(height, output.height)
                for (y in 0 until height) for (x in 0 until width) {
                    assertColorClose(original, output.getPixel(x, y), tolerance = 2)
                }
            } finally {
                input.recycle()
                if (output !== input) output.recycle()
            }
        }
    }

    @Test
    fun specularSuppressionDoesNotDarkenFlatWhiteSurfaces() = runBlocking {
        val input = bitmap(31, 29, Color.WHITE)
        val output = SpecularHighlightSuppression.apply(input, threshold = 0.3f)
        try {
            for (y in 0 until output.height) for (x in 0 until output.width) {
                assertEquals(Color.WHITE, output.getPixel(x, y))
            }
        } finally {
            input.recycle()
            if (output !== input) output.recycle()
        }
    }

    private fun luminance(color: Int): Float =
        0.2126f * Color.red(color) + 0.7152f * Color.green(color) + 0.0722f * Color.blue(color)

    private fun bitmap(width: Int, height: Int, color: Int): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

    private fun assertColorClose(expected: Int, actual: Int, tolerance: Int = 1) {
        assertEquals(Color.alpha(expected), Color.alpha(actual))
        assertTrue(abs(Color.red(expected) - Color.red(actual)) <= tolerance)
        assertTrue(abs(Color.green(expected) - Color.green(actual)) <= tolerance)
        assertTrue(abs(Color.blue(expected) - Color.blue(actual)) <= tolerance)
    }
}