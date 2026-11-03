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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBlackAndWhiteFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BrightnessFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BulgeDistortionFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BlackAndWhiteFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.ColorMatrix3x3Filter
import com.t8rin.imagetoolbox.feature.filters.data.model.ContrastFilter
import com.t8rin.imagetoolbox.feature.filters.presentation.model.GeneratedFilterCatalog
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class FilterRegistryTest {

    @Test
    fun emptyValuesDoNotContributeObjectIdentityToCacheKeys() {
        val first = BlackAndWhiteFilter(object : Filter.BlackAndWhite.Value {
            override fun hashCode(): Int = 1
        })
        val second = BlackAndWhiteFilter(object : Filter.BlackAndWhite.Value {
            override fun hashCode(): Int = 2
        })

        assertEquals(BlackAndWhiteFilter::class.java.name, first.cacheKey)
        assertEquals(first.cacheKey, second.cacheKey)
        assertEquals(first.cacheKey, BlackAndWhiteFilter(FilterValues.blackAndWhite()).cacheKey)
        assertSame(FilterValues.blackAndWhite(), UiBlackAndWhiteFilter().value)
    }

    @Test
    fun cacheKeysUseTheFilterTypeAndParameterContents() {
        assertNotEquals(
            BrightnessFilter(FilterValues.brightness(0.5f)).cacheKey,
            ContrastFilter(FilterValues.contrast(0.5f)).cacheKey
        )
        assertNotEquals(
            BrightnessFilter(FilterValues.brightness(0.5f)).cacheKey,
            BrightnessFilter(FilterValues.brightness(0.6f)).cacheKey
        )

        val matrix = FloatArray(9) { 1f }
        val filter = ColorMatrix3x3Filter(FilterValues.colorMatrix3x3(matrix))
        val copy = ColorMatrix3x3Filter(FilterValues.colorMatrix3x3(matrix.copyOf()))
        assertEquals(filter.cacheKey, copy.cacheKey)

        matrix[0] = 0.5f
        assertNotEquals(filter.cacheKey, copy.cacheKey)
    }

    @Test
    fun differentValuesWithTheSameHashCodeHaveDifferentCacheKeys() {
        val first = FilterValues.bulgeDistortion(radius = 0.5f, scale = 0.5f)
        val second = FilterValues.bulgeDistortion(radius = 0.50390625f, scale = 0.439453125f)
        assertEquals(first.hashCode(), second.hashCode())
        assertNotEquals(
            BulgeDistortionFilter(first).cacheKey,
            BulgeDistortionFilter(second).cacheKey
        )
    }

    @Test
    fun malformedKnownParametersRejectTheWholeTemplate() {
        val prefix = "^^.core.filters.presentation.model"
        val template = "Broken+$prefix.UiContrastFilter:Float:0.5," +
                "$prefix.UiBrightnessFilter:BrightnessParams:v1|bias=YmFk"
        assertThrows(IllegalArgumentException::class.java) {
            template.toTemplateFiltersList()
        }
        assertThrows(IllegalArgumentException::class.java) {
            "$prefix.UiBrightnessFilter:UnknownParams:0.5".toFiltersList(true)
        }
        assertThrows(IllegalArgumentException::class.java) {
            "$prefix.UiBrightnessFilter:null".toFiltersList(true)
        }
    }

    @Test
    fun corruptBooleanDoesNotBecomeAValidFalseParameter() {
        assertThrows(IllegalArgumentException::class.java) {
            ("Boolean" to "broken").fromPair()
        }
        assertThrows(IllegalArgumentException::class.java) {
            ("Pair{Float\$Boolean}" to "0.5\$broken").fromPair()
        }
    }

    @Test
    fun onlyPublishedMissingPayloadsRestoreTheirOriginalDefaults() {
        listOf(
            "Clustered2x2Dithering",
            "Clustered4x4Dithering",
            "Clustered8x8Dithering",
            "YililomaDithering"
        )
            .forEach { name ->
                val key = "^^.core.filters.presentation.model.Ui${name}Filter"
                val restored = "$key:null".toFiltersList(true).single()
                assertEquals(key.toFiltersList(false).single().value, restored.value)
                assertRoundTrip(listOf(restored))
            }
        assertThrows(IllegalArgumentException::class.java) {
            "^^.core.filters.presentation.model.UiBrightnessFilter:null".toFiltersList(true)
        }
    }

    @Test
    fun corruptPublishedNumericPayloadsAreRejected() {
        listOf("NaN", "Infinity", "-Infinity").forEach { value ->
            assertThrows(IllegalArgumentException::class.java) { ("Float" to value).fromPair() }
            assertThrows(IllegalArgumentException::class.java) {
                ("Pair{Float\$Float}" to "0.5\$$value").fromPair()
            }
        }
        assertThrows(IllegalArgumentException::class.java) {
            ("Pair{Float\$Float}" to "0.5\$0.75\$1.0").fromPair()
        }
    }

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
        assertEquals(0.35f, (filter as Filter.Brightness).value.bias)
        assertRoundTrip(listOf(filter))
    }

    @Test
    fun readsExistingStructuredTemplateParameters() {
        val saved = "^^.core.filters.presentation.model.UiAdaptiveBlurFilter:" +
                "Pair{Float\$Float}:7.0\$3.0," +
                "^^.core.filters.presentation.model.UiAnisotropicDiffusionFilter:" +
                "Triple{Float\$Float\$Float}:0.2\$0.4\$0.6," +
                "^^.core.filters.presentation.model.UiAutoPerspectiveFilter:Unit:Unit"
        val filters = saved.toFiltersList(includeValue = true)

        val blur = (filters[0] as Filter.AdaptiveBlur).value
        assertEquals(7f, blur.radius)
        assertEquals(3f, blur.threshold)

        val diffusion = (filters[1] as Filter.AnisotropicDiffusion).value
        assertEquals(0.2f, diffusion.repeatCount)
        assertEquals(0.4f, diffusion.conduction)
        assertEquals(0.6f, diffusion.diffusion)
        assertTrue(filters[2].value is Filter.AutoPerspective.Value)
        assertRoundTrip(filters)
    }

    @Test
    fun readsOldTextAndFloatArrayAndWritesTypedValues() {
        val prefix = "^^.core.filters.presentation.model"
        val saved = "$prefix.UiRawGmicFilter:String:ZngsMSsyOlxc," +
                "$prefix.UiColorMatrix3x3Filter:FloatArray:0.2\$0.1\$0.0\$0.0\$1.0\$0.0\$0.0\$0.0\$1.0"
        val filters = saved.toFiltersList(includeValue = true)
        val gmic = (filters[0] as Filter.RawGmic).value
        val matrix = (filters[1] as Filter.ColorMatrix3x3).value
        assertEquals("fx,1+2:\\\\", gmic.command)
        assertArrayEquals(floatArrayOf(0.2f, 0.1f, 0f, 0f, 1f, 0f, 0f, 0f, 1f), matrix.matrix, 0f)
        assertRoundTrip(filters)
    }

    @Test
    fun restoresOldEnumColorAndGmicFieldsWithTheirTypes() {
        val prefix = "^^.core.filters.presentation.model"
        val saved = "$prefix.UiMotionBlurFilter:Triple{Int\$Float\$BlurEdgeMode}:12\$45.0\$Clamp," +
                "$prefix.UiColorMaskFilter:FilterValueWrapper{ColorModel}:2131899478," +
                "$prefix.UiIsophotesFilter:GmicFilterParams:MTI=\$NS41\$VHJhbnNwYXJlbnQ="
        val filters = saved.toFiltersList(true)
        val motion = (filters[0] as Filter.MotionBlur).value
        val mask = (filters[1] as Filter.ColorMask).value
        val gmic = (filters[2] as Filter.Isophotes).value
        assertEquals(12, motion.kernelSize)
        assertEquals(45f, motion.angle)
        assertEquals(BlurEdgeMode.Clamp, motion.edgeMode)
        assertEquals(2131899478, mask.color.colorInt)
        assertEquals(listOf("12", "5.5", "Transparent"), gmic.settings.values)
        assertRoundTrip(filters)
    }

    @Test
    fun matrixTemplatesCompareByContent() {
        val value = "Matrix+^^.core.filters.presentation.model.UiColorMatrix3x3Filter:" +
                "FloatArray:1.0\$0.0\$0.0\$0.0\$1.0\$0.0\$0.0\$0.0\$1.0"
        val template: TemplateFilter = value.toTemplateFiltersList().single()
        val restored = listOf(template).toDatastoreString().toTemplateFiltersList().single()
        assertEquals(template, restored)
        assertEquals(template.hashCode(), restored.hashCode())
    }

    private fun assertRoundTrip(filters: List<Filter<*>>) {
        val serialized = filters.toDatastoreString(includeValue = true)
        assertEquals(serialized, serialized.toFiltersList(true).toDatastoreString(true))
    }

    @Test
    fun invalidParameterTypesUseConstructorDefaults() {
        val filter = GeneratedFilterCatalog.restore(
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

    @Test
    fun templatesCannotSilentlyLoseUnknownFilters() {
        val known = "^^.core.filters.presentation.model.UiBrightnessFilter:Float:0.5"
        assertThrows(IllegalArgumentException::class.java) {
            "Template+$known,missing.Filter:Float:0.5".toTemplateFiltersList()
        }
    }
}
