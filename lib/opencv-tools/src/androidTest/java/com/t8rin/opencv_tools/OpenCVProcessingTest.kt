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

package com.t8rin.opencv_tools

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.t8rin.opencv_tools.expand.ImageExpander
import com.t8rin.opencv_tools.forensics.ImageForensics
import com.t8rin.opencv_tools.moire.Moire
import com.t8rin.opencv_tools.moire.model.MoireType
import com.t8rin.opencv_tools.seam_carving.SeamCarver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

class OpenCVProcessingTest {

    @Test
    fun moirePreservesAlphaAndInputPixelsForEveryMode() = runBlocking {
        val source = image(65, 49)
        val before = pixels(source)
        try {
            for (type in MoireType.entries) {
                val result = Moire.remove(source, type)
                try {
                    assertEquals(source.width, result.width)
                    assertEquals(source.height, result.height)
                    pixels(result).forEachIndexed { index, pixel ->
                        assertEquals(before[index] ushr 24, pixel ushr 24)
                    }
                    assertTrue(before.contentEquals(pixels(source)))
                    assertFalse(source.isRecycled)
                } finally {
                    result.recycle()
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun copyMoveKeepsImagesSmallerThanItsBlockSize() = runBlocking {
        for ((width, height) in listOf(1 to 1, 15 to 22, 22 to 15)) {
            val source = image(width, height)
            try {
                val result = ImageForensics.detectCopyMove(source)
                try {
                    assertTrue(source.sameAs(result))
                    assertFalse(source.isRecycled)
                } finally {
                    result.recycle()
                }
            } finally {
                source.recycle()
            }
        }
    }

    @Test
    fun copyMoveAcceptsPreviouslyStoredRetainAboveBlockSize() = runBlocking {
        val source = image(37, 29)
        try {
            val expected = ImageForensics.detectCopyMove(source, retain = 16)
            val actual = ImageForensics.detectCopyMove(source, retain = 40)
            try {
                assertTrue(expected.sameAs(actual))
            } finally {
                expected.recycle()
                actual.recycle()
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun filtersCanBeCancelledWithoutRecyclingTheirInput() = runBlocking {
        val source = image(1024, 768)
        try {
            val filters: List<suspend (Bitmap) -> Bitmap> = listOf(
                { Moire.remove(it) },
                { ImageForensics.detectCopyMove(it) },
                { ImageForensics.luminanceGradient(it) },
                { SeamCarver.carve(it, 512, 384) },
                { SeamCarver.carve(it, 512, 384, useBackwardEnergy = true) },
                { ImageExpander.expand(it, 128, 128, 128, 128) }
            )
            for (filter in filters) {
                val job = launch(Dispatchers.Default) { filter(source).recycle() }
                delay(20)
                withTimeout(5_000) { job.cancelAndJoin() }
                assertTrue(job.isCancelled)
                assertFalse(source.isRecycled)
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun seamCarvingPreservesConstantImagesAtThinAndExpandedSizes() = runBlocking {
        val source = Bitmap.createBitmap(17, 23, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.rgb(37, 90, 156))
        }
        try {
            for (backward in listOf(false, true)) {
                for ((width, height) in listOf(1 to 1, 1 to 27, 29 to 1, 43 to 37)) {
                    val result =
                        SeamCarver.carve(source, width, height, useBackwardEnergy = backward)
                    try {
                        assertEquals(width, result.width)
                        assertEquals(height, result.height)
                        assertTrue(pixels(result).all { it == source.getPixel(0, 0) })
                    } finally {
                        result.recycle()
                    }
                }
            }
        } finally {
            source.recycle()
        }
    }

    @Test
    fun seamMasksProtectAndRemoveTheMarkedStripe() = runBlocking {
        val source = Bitmap.createBitmap(21, 17, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.RED)
            Canvas(this).drawRect(9f, 0f, 12f, 17f, Paint().apply { color = Color.GREEN })
        }
        val mask = Bitmap.createBitmap(21, 17, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.BLACK)
            Canvas(this).drawRect(9f, 0f, 12f, 17f, Paint().apply { color = Color.WHITE })
        }
        try {
            for (backward in listOf(false, true)) {
                val protected = SeamCarver.carve(source, 14, 17, mask, backward)
                val removed = SeamCarver.carve(source, 21, 17, mask, backward, true)
                try {
                    assertEquals(3 * 17, pixels(protected).count { it == Color.GREEN })
                    assertTrue(pixels(removed).all { it == Color.RED })
                    assertEquals(3 * 17, pixels(source).count { it == Color.GREEN })
                    assertFalse(source.isRecycled)
                    assertFalse(mask.isRecycled)
                } finally {
                    protected.recycle()
                    removed.recycle()
                }
            }
        } finally {
            source.recycle()
            mask.recycle()
        }
    }

    private fun image(width: Int, height: Int): Bitmap =
        Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val random = Random(420)
            setPixels(IntArray(width * height) { random.nextInt() }, 0, width, 0, 0, width, height)
        }

    private fun pixels(bitmap: Bitmap): IntArray =
        IntArray(bitmap.width * bitmap.height).also {
            bitmap.getPixels(it, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        }

}