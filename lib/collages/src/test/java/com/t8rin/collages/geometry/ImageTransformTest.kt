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

package com.t8rin.collages.geometry

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ImageTransformTest {

    @Test
    fun rotatedImageCoversEveryCornerAfterSnapping() {
        val random = Random(17)
        repeat(500) {
            val angle = random.nextFloat() * 6.28f
            val scale = 0.1f + random.nextFloat() * 3f
            val matrix = floatArrayOf(
                cos(angle) * scale, -sin(angle) * scale, random.nextFloat() * 600f - 300f,
                sin(angle) * scale, cos(angle) * scale, random.nextFloat() * 600f - 300f,
                0f, 0f, 1f
            )
            val bounds = floatArrayOf(10f, 10f, 390f, 290f)
            val fitted = ImageTransform.fit(matrix, 400f, 300f, bounds)
            assertTrue(ImageTransform.covers(fitted, 400f, 300f, bounds))
            assertArrayEquals(fitted, ImageTransform.fit(fitted, 400f, 300f, bounds), 0.001f)
        }
    }

    @Test
    fun coveredImageIsNotMoved() {
        val matrix = floatArrayOf(2f, 0f, -50f, 0f, 2f, -70f, 0f, 0f, 1f)
        assertArrayEquals(
            matrix,
            ImageTransform.fit(matrix, 400f, 300f, floatArrayOf(0f, 0f, 400f, 300f)),
            0f
        )
    }

    @Test
    fun movingDividerPreservesImageScaleAndRotation() {
        val original = floatArrayOf(2f, -.4f, -200f, .4f, 2f, -250f, 0f, 0f, 1f)
        val resized = ImageTransform.resize(original, 400f, 300f, 480f, 350f, scaleImage = false)
        assertArrayEquals(
            floatArrayOf(2f, -.4f, -160f, .4f, 2f, -225f, 0f, 0f, 1f),
            resized,
            0.001f
        )
        assertArrayEquals(
            resized,
            ImageTransform.fit(resized, 600f, 600f, floatArrayOf(0f, 0f, 480f, 350f)),
            0.001f
        )
        assertArrayEquals(
            original,
            ImageTransform.resize(resized, 480f, 350f, 400f, 300f, scaleImage = false),
            0.001f
        )
    }

    @Test
    fun expandingSlotOnlyZoomsAsMuchAsNeededToCoverIt() {
        val resized = ImageTransform.resize(
            floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f),
            400f, 300f, 600f, 300f, scaleImage = false
        )
        val bounds = floatArrayOf(0f, 0f, 600f, 300f)
        val fitted = ImageTransform.fit(resized, 400f, 300f, bounds)
        assertArrayEquals(
            floatArrayOf(1.5f, 0f, 0f, 0f, 1.5f, -75f, 0f, 0f, 1f),
            fitted,
            0.001f
        )
        assertTrue(ImageTransform.covers(fitted, 400f, 300f, bounds))
    }

    @Test
    fun repeatedViewportChangesDoNotStretchOrDrift() {
        val original = floatArrayOf(1.2f, -0.3f, -53f, 0.3f, 1.2f, 28f, 0f, 0f, 1f)
        var matrix = original.copyOf()
        repeat(100) {
            matrix = ImageTransform.resize(matrix, 723f, 407f, 391f, 220f)
            matrix = ImageTransform.resize(matrix, 391f, 220f, 723f, 407f)
        }
        assertArrayEquals(original, matrix, 0.02f)
    }
}