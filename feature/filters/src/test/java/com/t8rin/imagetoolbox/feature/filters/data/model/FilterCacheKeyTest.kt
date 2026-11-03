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

package com.t8rin.imagetoolbox.feature.filters.data.model

import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class FilterCacheKeyTest {

    @Test
    fun contentKeysIgnoreMapInsertionOrderAndFrameEveryValue() {
        val first = linkedMapOf("amount" to "0.5", "mode" to "Clamp")
        val reordered = linkedMapOf("mode" to "Clamp", "amount" to "0.5")
        assertEquals(filterCacheKey(first), filterCacheKey(reordered))
        assertNotEquals(filterCacheKey(listOf("a", "bc")), filterCacheKey(listOf("ab", "c")))
        assertNotEquals(filterCacheKey("null"), filterCacheKey(null))
        assertNotEquals(filterCacheKey(1), filterCacheKey("1"))
        assertEquals("Aa".hashCode(), "BB".hashCode())
        assertNotEquals(filterCacheKey("Aa"), filterCacheKey("BB"))
    }

    @Test
    fun auxiliaryImagesAffectKeysByContentAndOrder() {
        val filter = RawGmicFilter(FilterValues.rawGmic("command"))
        val initial = filter.cacheKey
        filter.auxiliaryImages = listOf(ImageModel(byteArrayOf(1, 2, 3)))
        val first = filter.cacheKey
        assertNotEquals(initial, first)
        filter.auxiliaryImages = listOf(ImageModel(byteArrayOf(1, 2, 3)))
        assertEquals(first, filter.cacheKey)
        filter.auxiliaryImages = listOf(ImageModel(byteArrayOf(1, 2, 4)))
        assertNotEquals(first, filter.cacheKey)
        assertNotEquals(
            filterCacheKey(listOf(ImageModel("first"), ImageModel("second"))),
            filterCacheKey(listOf(ImageModel("second"), ImageModel("first")))
        )
    }

    @Test
    fun customFieldCacheInputsStayInTheSchemaWithoutEnablingTemplates() {
        val parameters = structuredParameters<Value> {
            Value::custom.parameter(
                editorParam(
                default = { Custom(0.5f) },
                cache = { it.amount }
            ))
        }
        assertEquals(
            parameters.cacheKey(Value(Custom(0.5f))),
            parameters.cacheKey(Value(Custom(0.5f)))
        )
        assertNotEquals(
            parameters.cacheKey(Value(Custom(0.5f))),
            parameters.cacheKey(Value(Custom(0.6f)))
        )
        assertThrows(IllegalStateException::class.java) { filterCacheKey(Custom(0.5f)) }
    }

    @Test
    fun nonSerializableSizeAndMaskInputsHaveContentKeys() {
        fun filter(width: Int, mask: String) = SeamCarvingFilter(
            FilterValues.seamCarving(
                size = IntegerSize(width, 30),
                maskFile = FileModel(mask),
                useBackwardEnergy = false,
                useMaskAsRemoval = false
            )
        )
        assertEquals(filter(20, "mask").cacheKey, filter(20, "mask").cacheKey)
        assertNotEquals(filter(20, "mask").cacheKey, filter(21, "mask").cacheKey)
        assertNotEquals(filter(20, "mask").cacheKey, filter(20, "another").cacheKey)
    }

    @Test
    fun shaderValueKindsAndComponentsRemainDistinct() {
        assertNotEquals(
            filterCacheKey(ShaderValue.FloatValue(1f)),
            filterCacheKey(ShaderValue.IntValue(1))
        )
        assertNotEquals(
            filterCacheKey(ShaderValue.Vec2Value(1f, 2f)),
            filterCacheKey(ShaderValue.Vec2Value(2f, 1f))
        )
        assertNotEquals(
            filterCacheKey(ShaderValue.ColorValue(1, 2, 3, 4)),
            filterCacheKey(ShaderValue.ColorValue(1, 2, 3, 5))
        )
    }

    private class Custom(val amount: Float)
    private data class Value(val custom: Custom) : Filter.ParameterValue
}
