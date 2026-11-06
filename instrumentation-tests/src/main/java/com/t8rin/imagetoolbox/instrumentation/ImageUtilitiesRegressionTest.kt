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

package com.t8rin.imagetoolbox.instrumentation

import android.graphics.Bitmap
import android.graphics.Color
import com.t8rin.imagetoolbox.core.data.image.utils.healAlpha
import com.t8rin.imagetoolbox.core.data.saving.io.StreamWriteable
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.MirrorSide
import com.t8rin.imagetoolbox.feature.filters.data.model.MirrorFilter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import java.io.OutputStream

class ImageUtilitiesRegressionTest {
    @Test
    fun alphaRestorationKeepsOpaqueSourceRegionsAndRestoresTransparentOnes() = runBlocking {
        val original = pixels(256, 2)
        val processed = pixels(256, 2)
        for (x in 0 until 256) {
            original.setPixel(x, 0, Color.argb(x, 90, 120, 200))
            original.setPixel(x, 1, Color.WHITE)
            processed.setPixel(x, 0, Color.argb(255 - x, 170, 200, 80))
        }
        val expected = processed.copy(Bitmap.Config.ARGB_8888, true)
        for (x in 0 until 255) {
            expected.setPixel(x, 0, (x shl 24) or (processed.getPixel(x, 0) and 0xFFFFFF))
        }
        val actual = processed.healAlpha(original)
        assertTrue(expected.sameAs(actual))
        assertFalse(original.isRecycled)
        assertFalse(processed.isRecycled)
        expected.recycle()
        actual.recycle()
        processed.recycle()
        original.recycle()
    }

    @Test
    fun mirrorPreservesPixelsInEveryDirectionAndHandlesSinglePixelDimensions() = runBlocking {
        val input = pixels(37, 29).apply { density = Bitmap.DENSITY_NONE }
        for (side in MirrorSide.entries) for (center in listOf(0.1f, 0.5f, 0.78f)) {
            val horizontal = side == MirrorSide.LeftToRight || side == MirrorSide.RightToLeft
            val fromStart = side == MirrorSide.LeftToRight || side == MirrorSide.TopToBottom
            val dimension = if (horizontal) input.width else input.height
            val pivot = (dimension * center).toInt()
            val half = minOf(pivot, dimension - pivot)
            val result = MirrorFilter(FilterValues.mirror(center, side)).transform(
                input,
                IntegerSize(37, 29)
            )
            assertEquals(if (horizontal) half * 2 else 37, result.width)
            assertEquals(if (horizontal) 29 else half * 2, result.height)
            for (y in 0 until result.height) for (x in 0 until result.width) {
                val coordinate = if (horizontal) x else y
                val offset = minOf(coordinate, 2 * half - coordinate - 1)
                val source = if (fromStart) pivot - half + offset else pivot + half - offset - 1
                assertEquals(
                    input.getPixel(
                        if (horizontal) source else x,
                        if (horizontal) y else source
                    ), result.getPixel(x, y)
                )
            }
            result.recycle()
        }
        for ((width, height) in listOf(1 to 1, 1 to 20, 20 to 1)) {
            val tiny = pixels(width, height)
            val side = if (width == 1) MirrorSide.LeftToRight else MirrorSide.TopToBottom
            assertSame(
                tiny,
                MirrorFilter(FilterValues.mirror(0.5f, side)).transform(
                    tiny,
                    IntegerSize(width, height)
                )
            )
            tiny.recycle()
        }
        assertFalse(input.isRecycled)
        input.recycle()
    }

    @Test
    fun outputIsClosedEvenWhenFlushingFails() {
        val failure = IOException("flush")
        var closed = false
        val stream = object : OutputStream() {
            override fun write(value: Int) = Unit
            override fun flush(): Unit = throw failure
            override fun close() {
                closed = true
            }
        }
        val result = runCatching { StreamWriteable(stream).close() }
        assertSame(failure, result.exceptionOrNull())
        assertTrue(closed)
    }
}