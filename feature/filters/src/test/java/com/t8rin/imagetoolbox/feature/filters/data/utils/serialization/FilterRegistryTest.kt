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

import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBlackAndWhiteFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FilterRegistryTest {

    @Test
    fun readsExistingFavoriteAndRecentFilterKeys() {
        val saved = "^^.core.filters.presentation.model.UiBrightnessFilter," +
                "^^.core.filters.presentation.model.UiBlackAndWhiteFilter"
        val filters = saved.toFiltersList(includeValue = false)

        assertEquals(2, filters.size)
        assertTrue(filters[0] is UiBrightnessFilter)
        assertTrue(filters[1] is UiBlackAndWhiteFilter)
        assertEquals(saved, filters.toDatastoreString())
    }

    @Test
    fun readsExistingTemplateParameters() {
        val saved = "^^.core.filters.presentation.model.UiBrightnessFilter:Float:0.35"
        val filter = saved.toFiltersList(includeValue = true).single()

        assertTrue(filter is UiBrightnessFilter)
        assertEquals(0.35f, filter.value)
        assertEquals(saved, listOf(filter).toDatastoreString(includeValue = true))
    }

    @Test
    fun readsExistingStructuredTemplateParameters() {
        val saved = "^^.core.filters.presentation.model.UiAdaptiveBlurFilter:" +
                "Pair{Float\$Float}:7.0\$3.0," +
                "^^.core.filters.presentation.model.UiAnisotropicDiffusionFilter:" +
                "Triple{Float\$Float\$Float}:0.2\$0.4\$0.6," +
                "^^.core.filters.presentation.model.UiAutoPerspectiveFilter:Unit:Unit"
        val filters = saved.toFiltersList(includeValue = true)

        assertEquals(listOf(7f to 3f, Triple(0.2f, 0.4f, 0.6f), Unit), filters.map { it.value })
        assertEquals(saved, filters.toDatastoreString(includeValue = true))
    }

    @Test
    fun invalidParameterTypesUseConstructorDefaults() {
        val filter = UiFilter.fromSerializationKey(
            key = "com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter",
            value = "invalid"
        )

        assertTrue(filter is UiBrightnessFilter)
        assertEquals(UiBrightnessFilter().value, filter?.value)
    }

    @Test
    fun unknownFilterKeysAreIgnored() {
        assertNull(UiFilter.fromSerializationKey("missing.Filter"))
        assertTrue("missing.Filter".toFiltersList(includeValue = false).isEmpty())
    }
}