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

package com.t8rin.imagetoolbox.feature.fractal_generation.data

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalFormula
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalIterationPolicy
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalParams
import com.t8rin.imagetoolbox.feature.fractal_generation.domain.model.FractalViewport
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NativeFractalRendererBackendInstrumentedTest {

    @Test
    fun loadsMavenArtifactAndRendersThreeDimensionalFormula() = runBlocking {
        val renderer = NativeFractalRendererBackend()
        assertEquals(FractalFormula.entries.toSet(), renderer.supportedFormulas)

        val bitmap = renderer.render(
            FractalParams.Default
                .withFormula(FractalFormula.MengerSponge)
                .copy(iterations = 64)
                .toRenderRequest(width = 32, height = 32)
        )

        try {
            assertTrue(bitmap.containsMultipleColors())
        } finally {
            bitmap.recycle()
        }
    }

    @Test
    fun rendersExactDeepZoomViewport() = runBlocking {
        val renderer = NativeFractalRendererBackend()
        val bitmap = renderer.render(
            FractalParams.Default.copy(
                viewport = FractalViewport.of("-2", "0", "4E-100"),
                iterations = 600,
                iterationPolicy = FractalIterationPolicy.Fixed
            ).toRenderRequest(width = 32, height = 8)
        )

        try {
            assertTrue(bitmap.containsMultipleColors())
        } finally {
            bitmap.recycle()
        }
    }
}

private fun Bitmap.containsMultipleColors(): Boolean {
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    return pixels.any { it != pixels.first() }
}
