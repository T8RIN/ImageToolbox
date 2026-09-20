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

package com.t8rin.imagetoolbox.feature.gradient_maker

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientType
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.UiGradientState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SweepRotationTest {
    @Test
    fun rotatingSweepChangesExportedPixels() {
        val size = Size(120f, 120f)
        val state = UiGradientState(size).apply {
            gradientType = GradientType.Sweep
            colorStops.addAll(listOf(0f to Color.Red, 0.5f to Color.Green, 1f to Color.Blue))
        }

        fun render(): Bitmap = createBitmap(120, 120).apply {
            Canvas(this).drawRect(0f, 0f, 120f, 120f, Paint().apply {
                shader = checkNotNull(state.getBrush(size)).createShader(size)
            })
        }

        val initial = render()
        state.linearGradientAngle = 90f
        val rotated = render()
        assertFalse(initial.sameAs(rotated))
        state.linearGradientAngle = 0f
        val reset = render()
        assertTrue(initial.sameAs(reset))
        initial.recycle()
        rotated.recycle()
        reset.recycle()
    }
}