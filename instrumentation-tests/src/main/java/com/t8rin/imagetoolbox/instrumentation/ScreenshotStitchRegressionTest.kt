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
import android.graphics.Matrix
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.feature.image_stitch.data.CvStitchHelper
import com.t8rin.imagetoolbox.feature.image_stitch.domain.CombiningParams
import com.t8rin.imagetoolbox.feature.image_stitch.domain.StitchMode
import com.t8rin.opencv_tools.utils.toMat
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.random.Random

class ScreenshotStitchRegressionTest {
    @Test
    fun shuffledAndReversedScreenshotsPreserveDocumentPixelsInBothDirections() = runBlocking {
        val random = Random(319)
        val pixels = IntArray(190 * 1120) { random.nextInt() or 0xFF000000.toInt() }
        val document = Bitmap.createBitmap(pixels, 190, 1120, Bitmap.Config.ARGB_8888)
        for (horizontal in listOf(false, true)) {
            val rotation = Matrix().apply { setRotate(90f) }
            val images = List(7) { index ->
                Bitmap.createBitmap(document, 0, index * 120, 190, 400).let { image ->
                    if (horizontal) Bitmap.createBitmap(
                        image,
                        0,
                        0,
                        image.width,
                        image.height,
                        rotation,
                        false
                    )
                        .also { image.recycle() }
                    else image
                }
            }
            val expected = if (horizontal) {
                Bitmap.createBitmap(
                    document,
                    0,
                    0,
                    document.width,
                    document.height,
                    rotation,
                    false
                )
            } else document
            val getter =
                testProxy<ImageGetter<Bitmap>> { _, args -> images[(args[0] as String).toInt()] }
            val helper =
                CvStitchHelper(getter, testProxy<ImageScaler<Bitmap>> { _, args -> args[0] })
            for (order in listOf(
                (0..6).toList(),
                (6 downTo 0).toList(),
                listOf(3, 0, 6, 1, 5, 2, 4)
            )) {
                val result = helper.cvCombine(
                    order.map(Int::toString),
                    CombiningParams(stitchMode = StitchMode.Screenshot(), outputScale = 1f)
                ).first
                assertEquals(expected.width, result.width)
                assertEquals(expected.height, result.height)
                assertTrue("horizontal=$horizontal order=$order", expected.sameAs(result))
                result.recycle()
            }
            images.forEach { assertFalse(it.isRecycled); it.recycle() }
            if (expected !== document) expected.recycle()
        }
        document.recycle()
    }

    @Test
    fun fallbackResizingKeepsInputsAliveAndUsesTheSamePixels() {
        val helper = CvStitchHelper(
            testProxy { _, _ -> error("Unexpected load") },
            testProxy { _, _ -> error("Unexpected scale") }
        )
        for (diff in listOf(false, true)) for (width in listOf(37, 51)) {
            val firstBitmap = pixels(37, 29)
            val secondBitmap = pixels(width, 33)
            val first = firstBitmap.toMat()
            val second = secondBitmap.toMat()
            val expected = Mat()
            Imgproc.resize(second, expected, Size(37.0, 29.0))
            val result =
                requireNotNull(helper.stitchBitmaps(first, second, homo = false, diff = diff))
            assertEquals(0.0, Core.norm(expected, result, Core.NORM_INF), 0.0)
            assertEquals(37, first.cols())
            assertEquals(width, second.cols())
            first.release()
            second.release()
            expected.release()
            result.release()
            firstBitmap.recycle()
            secondBitmap.recycle()
        }
    }
}