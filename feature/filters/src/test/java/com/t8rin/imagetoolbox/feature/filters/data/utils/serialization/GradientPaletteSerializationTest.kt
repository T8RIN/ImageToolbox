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

package com.t8rin.imagetoolbox.feature.filters.data.utils.serialization

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GradientMapFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GradientPaletteSerializationTest {
    private val custom = GradientPalette.Custom(
        listOf(0x00123456, 0x80ABCDEF.toInt(), 0xFFFF7700.toInt()).map(::ColorModel)
    )

    @Test
    fun customPalettesRoundTripThroughFilterTemplates() {
        val values = listOf(GradientPalette.SoftRainbow, custom, GradientPalette.Classic)
        val filters: List<Filter<*>> = values.map(::GradientMapFilter)
        val templates = listOf(
            TemplateFilter("Gradients", filters),
            TemplateFilter("Custom", filters.reversed())
        )
        val restored = templates.toDatastoreString().toTemplateFiltersList()
        assertEquals(templates.map { it.name }, restored.map { it.name })
        assertEquals(values, restored[0].filters.map { it.value })
        assertEquals(values.reversed(), restored[1].filters.map { it.value })
    }

    @Test
    fun oldPresetFilterValuesRemainReadable() {
        val value = "^^.feature.filters.data.model.GradientMapFilter:GradientPalette:SoftRainbow"
        assertEquals(GradientPalette.SoftRainbow, value.toFiltersList(true).single().value)
    }

    @Test
    fun differentCustomPalettesNeverShareTheirFilterCacheKey() {
        val other = GradientPalette.Custom(custom.colors.reversed())
        assertNotEquals(GradientMapFilter(custom).cacheKey, GradientMapFilter(other).cacheKey)
        assertNotEquals(custom.toString(), other.toString())
        assertEquals(
            GradientMapFilter(custom).cacheKey,
            GradientMapFilter(GradientPalette.Custom(custom.colors)).cacheKey
        )
    }
}