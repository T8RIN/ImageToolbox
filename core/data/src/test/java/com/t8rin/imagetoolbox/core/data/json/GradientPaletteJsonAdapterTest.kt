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

package com.t8rin.imagetoolbox.core.data.json

import com.t8rin.imagetoolbox.core.data.di.JsonModule
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GradientPaletteJsonAdapterTest {
    private val moshi = JsonModule.moshi()

    @Test
    fun oldEnumNamesRemainReadable() {
        val adapter = moshi.adapter(GradientPalette::class.java)
        GradientPalette.entries.forEach { palette ->
            assertSame(palette, adapter.fromJson("\"${palette.name}\""))
            assertEquals("\"${palette.name}\"", adapter.toJson(palette))
        }
    }

    @Test
    fun customPalettesRoundTripInsideSavedObjects() {
        val custom = GradientPalette.Custom(
            listOf(0x00ABCDEF, 0x80FFFFFF.toInt(), 0xFF162738.toInt()).map(::ColorModel)
        )
        val value = SavedGradient(custom, listOf(GradientPalette.Classic, custom))
        val adapter = moshi.adapter(SavedGradient::class.java)
        assertEquals(value, adapter.fromJson(adapter.toJson(value)))
        val legacy = """{"palette":"SoftRainbow","favorites":["Classic","Fire"]}"""
        assertEquals(
            SavedGradient(
                GradientPalette.SoftRainbow,
                listOf(GradientPalette.Classic, GradientPalette.Fire)
            ),
            adapter.fromJson(legacy)
        )
    }

    data class SavedGradient(val palette: GradientPalette, val favorites: List<GradientPalette>)
}