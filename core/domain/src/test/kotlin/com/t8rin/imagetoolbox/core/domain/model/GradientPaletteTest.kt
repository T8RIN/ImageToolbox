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
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class GradientPaletteTest {

    @Test
    fun containsAllSharedPalettes() {
        assertEquals(76, GradientPalette.entries.size)
        assertEquals(GradientPalette.Classic, GradientPalette.entries[0])
        assertEquals(GradientPalette.Grayscale, GradientPalette.entries[74])
        assertEquals(GradientPalette.SoftRainbow, GradientPalette.entries[75])
        assertEquals(
            GradientPalette.entries.size,
            GradientPalette.entries.map { it.name.lowercase() }.distinct().size
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

    @Test
    fun containsNewRgbAndAtmosphericPalettes() {
        val added = setOf(
            GradientPalette.Rgb,
            GradientPalette.Ryb,
            GradientPalette.Cmyk,
            GradientPalette.HsvWheel,
            GradientPalette.RedChannel,
            GradientPalette.GreenChannel,
            GradientPalette.BlueChannel,
            GradientPalette.Heatmap,
            GradientPalette.ColdFire,
            GradientPalette.Ultraviolet,
            GradientPalette.ToxicWaste,
            GradientPalette.BloodMoon,
            GradientPalette.Abyss,
            GradientPalette.ElectricCandy,
            GradientPalette.BlackGold,
            GradientPalette.Ghost
        )

        assertEquals(16, added.size)
        assertTrue(GradientPalette.entries.containsAll(added))
        listOf(
            GradientPalette.Rgb,
            GradientPalette.Cmyk,
            GradientPalette.HsvWheel,
            GradientPalette.ElectricCandy
        ).forEach { palette ->
            assertEquals(palette.colors.first(), palette.colors.last())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun sampleColorsRejectsEmptyOutput() {
        GradientPalette.Classic.sampleColors(0)
    }

    @Test
    fun storedPresetNamesStillRestoreTheSamePalettes() {
        GradientPalette.entries.forEach { palette ->
            assertEquals(palette.name, palette.toSerializedString())
            assertSame(palette, GradientPalette.fromSerializedString(palette.name))
        }
    }

    @Test
    fun customPalettesPreserveOrderDuplicatesAndAlpha() {
        val colors = listOf(0x00ABCDEF, 0x80403020.toInt(), 0xFFFFCC00.toInt(), 0x00ABCDEF)
            .map(::ColorModel)
        val palette = GradientPalette.Custom(colors)
        val restored = GradientPalette.fromSerializedString(palette.toSerializedString())
        assertEquals(palette, restored)
        assertEquals(palette.hashCode(), restored.hashCode())
        assertEquals(colors, restored?.colors)
        assertEquals(colors.first(), palette.sampleColors(17).first())
        assertEquals(colors.last(), palette.sampleColors(17).last())
    }

    @Test
    fun modifyingTheInputListDoesNotChangeAnExistingPalette() {
        val source = mutableListOf(ColorModel(0xFF102030.toInt()), ColorModel(0xFF4080C0.toInt()))
        val palette = GradientPalette.Custom(source)
        val original = palette.toSerializedString()
        val originalHash = palette.hashCode()
        source.reverse()
        source.clear()
        assertEquals(original, palette.toSerializedString())
        assertEquals(originalHash, palette.hashCode())
        assertEquals(2, palette.colors.size)
    }

    @Test
    fun editedColorsRecognizeBuiltInsWithoutChangingCustomSerialization() {
        val colors = GradientPalette.Classic.colors
        assertSame(GradientPalette.Classic, GradientPalette.fromColors(colors))
        val custom = GradientPalette.Custom(colors)
        assertEquals(custom, GradientPalette.fromSerializedString(custom.toSerializedString()))
    }

    @Test
    fun invalidStoredPalettesAreRejected() {
        for (value in listOf(
            "", "RemovedPalette", "custom;", "custom;ffffffff",
            "custom;ffffffff;notacolor", "custom;ffffffff;ffffff", "custom;ffffffff;"
        )) {
            assertNull(value, GradientPalette.fromSerializedString(value))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun customPaletteRequiresTwoColors() {
        GradientPalette.Custom(listOf(ColorModel(0)))
    }

}
