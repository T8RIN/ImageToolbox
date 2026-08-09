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

package com.t8rin.cropper.crop

import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Test

class CropRectRoundingTest {

    @Test
    fun squareCropKeepsRoundedSizeOnBothAxes() {
        val result = Rect(
            left = 560.4f,
            top = 0.6f,
            right = 1999.9f,
            bottom = 1440.1f
        ).roundedInBounds(width = 2560, height = 1440)

        assertEquals(1440, result.width)
        assertEquals(1440, result.height)
    }

    @Test
    fun roundedCropIsShiftedInsideImageWithoutShrinking() {
        val result = Rect(
            left = 1120.4f,
            top = 0.2f,
            right = 2560.2f,
            bottom = 1439.8f
        ).roundedInBounds(width = 2560, height = 1440)

        assertEquals(1120, result.left)
        assertEquals(0, result.top)
        assertEquals(2560, result.right)
        assertEquals(1440, result.bottom)
    }
}
