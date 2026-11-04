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

package com.t8rin.opencv_tools.photo_filters

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.CancellationException
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class MorphologicalReconstructionTest {

    @Test
    fun levelQueuesMatchIteratedGeodesicDilation() {
        val random = Random(37)
        for (width in 1..13) {
            for (height in 1..11) {
                val mask = ByteArray(width * height) { random.nextInt(256).toByte() }
                val marker =
                    ByteArray(mask.size) { random.nextInt((mask[it].toInt() and 255) + 1).toByte() }
                val expected = reference(width, marker, mask)
                val actual = marker.copyOf()
                MorphologicalReconstruction.reconstruct(width, actual, mask) {}
                assertArrayEquals("${width}x$height", expected, actual)
                assertTrue(actual.indices.all {
                    (actual[it].toInt() and 255) in (marker[it].toInt() and 255)..(mask[it].toInt() and 255)
                })
            }
        }
    }

    @Test
    fun propagationReachesTheEndOfLongThinComponents() {
        val mask = ByteArray(10001) { 180.toByte() }
        val marker = ByteArray(mask.size).apply { this[0] = 120 }
        MorphologicalReconstruction.reconstruct(1, marker, mask) {}
        assertArrayEquals(ByteArray(mask.size) { 120 }, marker)
    }

    @Test
    fun reconstructionIsIdempotentAndCannotCrossDarkerBarriers() {
        val mask = byteArrayOf(100, 90, 30, 80, 100)
        val marker = byteArrayOf(80, 0, 0, 0, 0)
        MorphologicalReconstruction.reconstruct(5, marker, mask) {}
        assertArrayEquals(byteArrayOf(80, 80, 30, 30, 30), marker)
        val reconstructed = marker.copyOf()
        MorphologicalReconstruction.reconstruct(5, marker, mask) {}
        assertArrayEquals(reconstructed, marker)
    }

    @Test(expected = CancellationException::class)
    fun propagationChecksCancellation() {
        val mask = ByteArray(10000) { 127 }
        val marker = ByteArray(mask.size).apply { this[0] = 127 }
        var checks = 0
        MorphologicalReconstruction.reconstruct(100, marker, mask) {
            if (++checks == 5) throw CancellationException()
        }
    }

    private fun reference(width: Int, marker: ByteArray, mask: ByteArray): ByteArray {
        val height = marker.size / width
        var current = marker.copyOf()
        while (true) {
            val next = current.copyOf()
            for (y in 0 until height) {
                for (x in 0 until width) {
                    var maximum = 0
                    for (dy in -1..1) {
                        for (dx in -1..1) {
                            val row = y + dy
                            val column = x + dx
                            if (row in 0 until height && column in 0 until width) {
                                maximum =
                                    max(maximum, current[row * width + column].toInt() and 255)
                            }
                        }
                    }
                    next[y * width + x] = min(maximum, mask[y * width + x].toInt() and 255).toByte()
                }
            }
            if (next.contentEquals(current)) return next
            current = next
        }
    }
}