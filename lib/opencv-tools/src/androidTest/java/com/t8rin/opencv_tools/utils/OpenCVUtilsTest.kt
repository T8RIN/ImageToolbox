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

package com.t8rin.opencv_tools.utils

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import kotlin.system.measureNanoTime

class OpenCVUtilsTest {
    @Test
    fun conversionPreservesPixelsAndDoesNotRecycleItsInput() {
        assertTrue(OpenCVLoader.initLocal())
        val source = Bitmap.createBitmap(37, 29, Bitmap.Config.ARGB_8888)
        source.setPixels(
            IntArray(37 * 29) { ((it % 256) shl 24) or ((it * 53197) and 0xffffff) },
            0,
            37,
            0,
            0,
            37,
            29
        )
        val configs =
            mutableListOf(Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565, Bitmap.Config.ALPHA_8)
        if (Build.VERSION.SDK_INT >= 26) configs += listOf(
            Bitmap.Config.RGBA_F16,
            Bitmap.Config.HARDWARE
        )
        try {
            for (config in configs) {
                val input = requireNotNull(source.copy(config, false))
                try {
                    val expected = copiedMat(input).toBitmap()
                    val mat = input.toMat()
                    val actual = mat.toBitmap()
                    try {
                        assertFalse(input.isRecycled)
                        assertTrue(mat.empty())
                        assertTrue("Different pixels for $config", expected.sameAs(actual))
                    } finally {
                        expected.recycle()
                        actual.recycle()
                    }
                } finally {
                    input.recycle()
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun resizingKeepsTopLeftAlignmentAndTransparentPadding() {
        assertTrue(OpenCVLoader.initLocal())
        for ((width, height) in listOf(12 to 6, 6 to 12, 300 to 1, 1 to 300)) {
            val color = Scalar(21.0, 42.0, 63.0, 84.0)
            val source = Mat(height, width, CvType.CV_8UC4, color)
            try {
                val padded = source.resizeAndPad(Size(18.0, 18.0))
                try {
                    assertEquals(18, padded.rows())
                    assertEquals(18, padded.cols())
                    assertArrayEquals(color.`val`, padded.get(0, 0), 0.0)
                    assertArrayEquals(DoubleArray(4), padded.get(17, 17), 0.0)
                    assertFalse(source.empty())
                } finally {
                    padded.release()
                }
            } finally {
                source.release()
            }
        }
    }

    @Test
    fun conversionBenchmark() {
        assertTrue(OpenCVLoader.initLocal())
        val image = Bitmap.createBitmap(2048, 1536, Bitmap.Config.ARGB_8888)
        val before = mutableListOf<Long>()
        val after = mutableListOf<Long>()
        try {
            repeat(12) { pass ->
                val oldTime = measureNanoTime { copiedMat(image).release() }
                val newTime = measureNanoTime { image.toMat().release() }
                if (pass >= 4) {
                    before += oldTime
                    after += newTime
                }
            }
            Log.i(
                "BitmapMatBenchmark",
                "copyMedianMs=${before.sorted()[before.size / 2] / 1e6} " +
                        "directMedianMs=${after.sorted()[after.size / 2] / 1e6} " +
                        "avoidedBytes=${image.byteCount}"
            )
        } finally {
            image.recycle()
        }
    }

    private fun copiedMat(image: Bitmap): Mat {
        val copy = requireNotNull(image.copy(Bitmap.Config.ARGB_8888, false))
        return try {
            Mat().also { Utils.bitmapToMat(copy, it) }
        } finally {
            copy.recycle()
        }
    }
}