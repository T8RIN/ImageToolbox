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
import androidx.core.graphics.applyCanvas
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.data.getPaint
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.SpotHealMode
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiCinematicDreamFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiDreamPaintingFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiEqualizeHistogramPixelationFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiMightyDetailsFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.SideFadeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.TornEdgeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.filterCacheKey
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilterRenderingRegressionTest {
    private val provider = AndroidFilterProvider()

    @Test
    fun bitmapCacheKeysTrackContentsAndPixelChanges() {
        val first =
            Bitmap.createBitmap(7, 5, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        val second = first.copy(Bitmap.Config.ARGB_8888, true)
        try {
            val initial = filterCacheKey(ImageModel(first))
            assertEquals(initial, filterCacheKey(ImageModel(second)))
            first.setPixel(0, 0, Color.BLUE)
            assertNotEquals(initial, filterCacheKey(ImageModel(first)))
            assertEquals(initial, filterCacheKey(ImageModel(second)))
        } finally {
            first.recycle()
            second.recycle()
        }
    }

    @Test
    fun histogramGridLargerThanImageStillRenders() = runBlocking {
        assertRenders(UiEqualizeHistogramPixelationFilter(), 33, 31, expectChanges = false)
    }

    @Test
    fun gmicRecipesThatDependOnSmoothStillRender() = runBlocking {
        val filters =
            listOf(UiCinematicDreamFilter(), UiDreamPaintingFilter(), UiMightyDetailsFilter())
        filters.forEach { assertRenders(it, 97, 83) }
    }

    @Test
    fun spotHealCanBeCreatedFromItsDomainInterface() = runBlocking {
        val mask = Bitmap.createBitmap(33, 31, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.BLACK)
            for (y in 12..18) {
                for (x in 13..19) setPixel(x, y, Color.WHITE)
            }
        }
        try {
            val filter = object : Filter.SpotHeal {
                override val value =
                    FilterValues.spotHeal(mask = ImageModel(mask), mode = SpotHealMode.OpenCV)
            }
            assertRenders(filter, mask.width, mask.height)
        } finally {
            mask.recycle()
        }
    }

    @Test
    fun sideFadePreservesAbsolutePixelLengthsUsedByImageStitching() = runBlocking {
        val input = Bitmap.createBitmap(97, 83, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }
        try {
            for (side in FadeSide.entries) {
                val dimension = when (side) {
                    FadeSide.Start, FadeSide.End -> input.width
                    FadeSide.Top, FadeSide.Bottom -> input.height
                }
                for (length in listOf(1, 17, dimension, dimension + 13)) {
                    for (strength in listOf(0.35f, 1f)) {
                        val expected = input.copy(Bitmap.Config.ARGB_8888, true).apply {
                            setHasAlpha(true)
                        }.applyCanvas {
                            drawPaint(side.getPaint(input, length, strength))
                        }
                        val filter = SideFadeFilter(
                            FilterValues.sideFade(
                                side = side,
                                scale = length.toFloat() / dimension,
                                strength = strength
                            )
                        )
                        val actual = filter.transform(input, IntegerSize(input.width, input.height))
                        try {
                            assertTrue(
                                "$side length=$length strength=$strength",
                                expected.sameAs(actual)
                            )
                        } finally {
                            expected.recycle()
                            actual.recycle()
                        }
                    }
                }
            }
        } finally {
            input.recycle()
        }
    }

    @Test
    fun tornEdgeDoesNotUseTheValueImplementationAsItsRandomSeed() = runBlocking {
        val value = FilterValues.tornEdge(
            toothHeight = 21,
            horizontalToothRange = 31,
            verticalToothRange = 41,
            top = true,
            right = false,
            bottom = true,
            left = true
        )
        val sameFields = object : Filter.TornEdge.Value by value {
            override fun hashCode(): Int = value.hashCode() + 1
        }
        val input = Bitmap.createBitmap(97, 83, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.WHITE)
        }
        try {
            val size = IntegerSize(input.width, input.height)
            val first = TornEdgeFilter(value).transform(input, size)
            val second = TornEdgeFilter(sameFields).transform(input, size)
            try {
                assertTrue(first.sameAs(second))
            } finally {
                first.recycle()
                second.recycle()
            }
        } finally {
            input.recycle()
        }
    }

    private suspend fun assertRenders(
        filter: Filter<*>,
        width: Int,
        height: Int,
        expectChanges: Boolean = true
    ) {
        val input = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pixels = IntArray(width * height) { index ->
            val x = index % width
            val y = index / width
            Color.rgb((x * 17 + y * 5) and 255, (x * 3 + y * 11) and 255, (x * 7 + y * 13) and 255)
        }
        input.setPixels(pixels, 0, width, 0, 0, width, height)

        try {
            val result =
                provider.filterToTransformation(filter).transform(input, IntegerSize(width, height))
            try {
                assertEquals(width, result.width)
                assertEquals(height, result.height)
                if (expectChanges) assertFalse(input.sameAs(result))
            } finally {
                if (result !== input) result.recycle()
            }
        } finally {
            input.recycle()
        }
    }
}
