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

package com.t8rin.imagetoolbox.feature.compare.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompareAnimationParamsTest {

    @Test
    fun everyDurationHasAnExactLoopAndTwoEndpointHolds() {
        for (duration in 2..8) {
            val params = CompareAnimationParams(durationSeconds = duration)
            val frames = params.frames()
            assertEquals(duration * 1000, frames.sumOf { it.durationMillis })
            assertEquals(
                listOf(0f, 1f),
                frames.filter { it.durationMillis == 500 }.map { it.position })
            assertTrue(frames.all { it.position in 0f..1f })
            assertTrue(frames.all { it.durationMillis > 0 && it.durationMillis % 10 == 0 })
        }
    }

    @Test
    fun dividerHoldsAtBothEdgesAndMovesSymmetrically() {
        val params = CompareAnimationParams()
        assertEquals(0f, params.positionAt(0), 0f)
        assertEquals(0f, params.positionAt(499), 0f)
        assertEquals(1f, params.positionAt(2000), 0f)
        assertEquals(1f, params.positionAt(2499), 0f)
        assertEquals(0f, params.positionAt(4000), 0f)
        for (time in 0..4000 step 50) {
            assertEquals(1f, params.positionAt(time) + params.positionAt(time + 2000), 0.00001f)
        }
        val outward = (500..2000 step 50).map(params::positionAt)
        val inward = (2500..4000 step 50).map(params::positionAt)
        assertTrue(outward.zipWithNext().all { (first, second) -> first <= second })
        assertTrue(inward.zipWithNext().all { (first, second) -> first >= second })
    }

    @Test
    fun exportedFramesSampleThePreviewTimeline() {
        val params = CompareAnimationParams(durationSeconds = 7)
        var time = 0
        for (frame in params.frames()) {
            assertEquals(params.positionAt(time), frame.position, 0f)
            time += frame.durationMillis
        }
    }

    @Test
    fun dimensionsStayBoundedWithoutUpscalingOrLosingThinImages() {
        assertEquals(2048 to 1024, CompareAnimationParams.outputSize(4000, 2000))
        assertEquals(1024 to 2048, CompareAnimationParams.outputSize(2000, 4000))
        assertEquals(120 to 80, CompareAnimationParams.outputSize(120, 80))
        assertEquals(2048 to 1, CompareAnimationParams.outputSize(Int.MAX_VALUE, 1))
        assertEquals(1 to 2048, CompareAnimationParams.outputSize(1, Int.MAX_VALUE))
    }

    @Test
    fun selectedResolutionKeepsTheSourceAspectRatio() {
        for (size in CompareAnimationParams.sizes) {
            assertEquals(size to size / 2, CompareAnimationParams.outputSize(8000, 4000, size))
            assertEquals(size / 2 to size, CompareAnimationParams.outputSize(4000, 8000, size))
            assertEquals(120 to 80, CompareAnimationParams.outputSize(120, 80, size))
        }
    }

    @Test
    fun invalidDurationsAreBoundedBeforeCalculatingFrames() {
        assertEquals(
            2000,
            CompareAnimationParams(Int.MIN_VALUE).frames().sumOf { it.durationMillis })
        assertEquals(
            8000,
            CompareAnimationParams(Int.MAX_VALUE).frames().sumOf { it.durationMillis })
    }
}