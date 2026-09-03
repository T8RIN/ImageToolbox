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

package com.t8rin.imagetoolbox.core.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GradientPaletteTest {

    @Test
    fun containsAllSharedPalettes() {
        assertEquals(59, GradientPalette.entries.size)
        assertEquals(
            GradientPalette.entries.size,
            GradientPalette.entries.map { it.name }.distinct().size
        )
    }

    @Test
    fun sampleColorsKeepsBothGradientEndpoints() {
        GradientPalette.entries.forEach { palette ->
            val colors = palette.sampleColors(10)

            assertEquals(palette.colors.first(), colors.first())
            assertEquals(palette.colors.last(), colors.last())
            assertEquals(10, colors.size)
            assertTrue(colors.all { it.colorInt ushr 24 == 0xFF })
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun sampleColorsRejectsEmptyOutput() {
        GradientPalette.Classic.sampleColors(0)
    }
}
