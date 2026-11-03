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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AgxToneMappingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PalCompositeParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBokehLightsFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiHelixWavesFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPalCompositeFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.AgxToneMappingFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.BokehLightsFilter
import com.t8rin.imagetoolbox.feature.filters.presentation.model.GeneratedFilterCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class DefinedFilterSerializationTest {

    @Test
    fun restoresClaheTemplatesThroughEachFilterSchema() {
        val keys = listOf(
            "ClaheHSL",
            "ClaheHSV",
            "ClaheJzazbz",
            "ClaheLAB",
            "ClaheLUV",
            "ClaheOklab",
            "ClaheOklch"
        )
        keys.forEach { name ->
            val value = "ClaheParams:0.65\$12\$16\$160"
            val template =
                "$uiPrefix.Ui${name}Filter:$value,$transformationPrefix.${name}Filter:$value"
            val restored = template.toFiltersList(includeValue = true)
            val encoded = restored.toDatastoreString(includeValue = true)
            val params = name + "Params:" + StoredParameterValues.encode(
                mapOf(
                    "threshold" to "0.65",
                    "gridSizeHorizontal" to "12",
                    "gridSizeVertical" to "16",
                    "binsCount" to "160"
                )
            )
            assertEquals(2, restored.size)
            assertEquals(
                "$uiPrefix.Ui${name}Filter:$params,$transformationPrefix.${name}Filter:$params",
                encoded
            )
            assertEquals(encoded, encoded.toFiltersList(true).toDatastoreString(true))
        }
    }

    @Test
    fun namedTemplatesKeepUiAndTransformationKeys() {
        val color = 0x804492cc.toInt()
        val saved = "Analog+" +
                "$uiPrefix.UiHelixWavesFilter:HelixWavesParams:${
                    StoredParameterValues.encode(
                        mapOf(
                            "frequency" to "17.5",
                            "mode" to "2",
                            "offsetX" to "-0.4"
                        )
                    )
                }," +
                "$transformationPrefix.BokehLightsFilter:BokehLightsParams:${
                    StoredParameterValues.encode(
                        mapOf("count" to "12", "lightColor" to color.toString())
                    )
                }," +
                "$uiPrefix.UiBrightnessFilter:Float:0.3"

        val template = saved.toTemplateFiltersList().single()
        val (helix, bokeh, brightness) = template.filters

        assertEquals("Analog", template.name)
        assertTrue(helix is UiHelixWavesFilter)
        assertEquals(
            UiHelixWavesFilter().value.copy(frequency = 17.5f, mode = 2, offsetX = -0.4f),
            helix.value
        )
        assertTrue(bokeh is BokehLightsFilter)
        assertEquals(
            UiBokehLightsFilter().value.copy(count = 12, lightColor = ColorModel(color)),
            bokeh.value
        )
        assertTrue(GeneratedFilterCatalog.toUiFilter(bokeh) is UiBokehLightsFilter)
        assertEquals(0.3f, (brightness as Filter.Brightness).value.bias)

        val migrated = listOf(template).toDatastoreString()
        assertFalse(migrated.contains(":ProceduralParams:"))
        val restored = migrated.toTemplateFiltersList().single()
        assertEquals(template.filters.map { it::class }, restored.filters.map { it::class })
        assertEquals(template.filters.map { it.value }, restored.filters.map { it.value })
        assertEquals(migrated, listOf(restored).toDatastoreString())
    }

    @Test
    fun oldFavoritesAndRecentsUseTheSameKeys() {
        val saved =
            "$uiPrefix.UiHelixWavesFilter,$uiPrefix.UiPalCompositeFilter,$uiPrefix.UiBrightnessFilter"
        val filters = saved.toFiltersList(includeValue = false)

        assertEquals(
            listOf(
                UiHelixWavesFilter::class,
                UiPalCompositeFilter::class,
                UiBrightnessFilter::class
            ),
            filters.map { it::class }
        )
        assertEquals(saved, filters.toDatastoreString())
    }

    @Test
    fun restoresPixelizationKeysInFavoritesAndTemplates() {
        val names = listOf(
            "Cross", "MicroMacro", "Nucleus", "Orbital", "PulseGrid",
            "RadialWeave", "SimpleWeave", "Staggered", "Vortex"
        )
        for (name in names) {
            val oldKey = "$uiPrefix.Ui${name}PixelizationFilter"
            val newKey = "$uiPrefix.Ui${name}PixelationFilter"
            val fullKey = oldKey.replace(PACKAGE_ALIAS, REAL_PACKAGE)
            assertEquals(newKey, oldKey.toFiltersList(false).toDatastoreString())
            assertEquals(
                newKey,
                listOfNotNull(GeneratedFilterCatalog.restore(fullKey, null)).toDatastoreString()
            )
        }

        val old = "$uiPrefix.UiSimpleWeavePixelizationFilter:Float:37.0"
        val expected = "$uiPrefix.UiSimpleWeavePixelationFilter:SimpleWeavePixelationParams:" +
                StoredParameterValues.encode(mapOf("layers" to "37.0"))
        assertEquals(expected, old.toFiltersList(true).toDatastoreString(true))
    }

    @Test
    fun readsOldParameterNamesThroughTheSavedFilterKey() {
        val triple = "Triple{Float\$Float\$Float}"
        val saved = listOf(
            "$uiPrefix.UiEnhancedGlitchFilter:GlitchParams:-0.12\$0.15\$0.2\$8\$0.1\$0.2",
            "$transformationPrefix.GlitchFilter:$triple:23.0\$17.0\$11.0",
            "$transformationPrefix.ClaheFilter:$triple:0.65\$12.0\$16.0",
            "$uiPrefix.UiWaterEffectFilter:WaterParams:0.3\$2.0\$1.0\$0.4\$0.5"
        ).joinToString(",")
        val restored = saved.toFiltersList(true)
        assertEquals(-0.12f, (restored[0] as Filter.EnhancedGlitch).value.channelsShiftX)
        assertEquals(23f, (restored[1] as Filter.Glitch).value.amount)
        assertEquals(12f, (restored[2] as Filter.Clahe).value.gridSizeX)
        assertEquals(2f, (restored[3] as Filter.WaterEffect).value.frequencyX)
        val encoded = restored.toDatastoreString(true)
        assertEquals(restored.map { it.value }, encoded.toFiltersList(true).map { it.value })
        assertEquals(encoded, encoded.toFiltersList(true).toDatastoreString(true))
    }

    @Test
    fun restoresReleasedEditorParametersAsTypedFields() {
        val samples = listOf(
            Triple("EnhancedZoomBlur", "EnhancedZoomBlurParams", "13\$7.5\$0.25\$0.75\$1.8\$220.0"),
            Triple("LinearGaussianBlur", "LinearGaussianParams", "37\$12.5\$Wrap\$GAMMA2P2"),
            Triple("BilaterialBlur", "BilaterialBlurParams", "17\$21.5\$42.5\$Reflect"),
            Triple(
                "VoronoiCrystallize",
                "VoronoiCrystallizeParams",
                "0.7\$41.0\$3.0\$2\$0.6\$145.0\$2.5\$0.8\$-13408615"
            ),
            Triple(
                "Pointillize",
                "VoronoiCrystallizeParams",
                "0.7\$41.0\$3.0\$2\$0.6\$145.0\$2.5\$0.8\$-13408615"
            ),
            Triple("RubberStamp", "RubberStampParams", "0.6\$0.2\$0.7\$-13408615\$-6750010"),
            Triple("Smear", "SmearParams", "130.0\$0.7\$0.4\$27\$2"),
            Triple("Sparkle", "SparkleParams", "35\$17\$0.6\$23\$0.25\$0.75\$-13408615"),
            Triple("Flare", "FlareParams", "0.4\$0.8\$0.6\$0.3\$2.2\$0.25\$0.75\$-13408615"),
            Triple("Shear", "ShearParams", "15.0\$-20.0\$false"),
            Triple("DropShadow", "DropShadowParams", "-13408615\$-11.5\$17.5\$23.5"),
            Triple("TornEdge", "TornEdgeParams", "21\$31\$41\$false\$true\$false\$true")
        )
        for ((name, oldParams, payload) in samples) {
            val keys = listOf("$uiPrefix.Ui${name}Filter", "$transformationPrefix.${name}Filter")
            val saved = keys.joinToString(",") { "$it:$oldParams:$payload" }
            val restored = saved.toFiltersList(includeValue = true)

            assertEquals(name, 2, restored.size)
            assertEquals(name, restored[0].value, restored[1].value)
            val encoded = restored.toDatastoreString(includeValue = true)
            assertEquals(
                name,
                restored.map { it.value },
                encoded.toFiltersList(true).map { it.value })
            assertEquals(name, encoded, encoded.toFiltersList(true).toDatastoreString(true))
        }
    }

    @Test
    fun namedTemplatesUseDefaultsForMissingFieldsAndCopiesKeepVisibility() {
        val saved = "$uiPrefix.UiHelixWavesFilter:HelixWavesParams:" +
                StoredParameterValues.encode(mapOf("frequency" to "17.5", "mode" to "2"))
        val filter = saved.toFiltersList(includeValue = true).single() as UiHelixWavesFilter
        assertEquals(UiHelixWavesFilter().value.copy(frequency = 17.5f, mode = 2), filter.value)
        assertEquals(UiHelixWavesFilter().value, filter.newInstance().value)
        filter.isVisible = false
        val copied = filter.copy(filter.value.copy(frequency = 10f))
        assertFalse(copied.isVisible)
        assertEquals(filter.value.copy(frequency = 10f), copied.value)
    }

    @Test
    fun generatedCodecRestoresPublishedTypedValuesAndWritesNamedFields() {
        val saved = "$uiPrefix.UiPalCompositeFilter:PalCompositeParams:" +
                "0.42\$5\$0.04\$-20\$640\$0.7\$9\$0.95"
        val filter = saved.toFiltersList(includeValue = true).single() as UiPalCompositeFilter
        assertEquals(
            PalCompositeParams(
                lumaFilter = 0.42f,
                chromaRadius = 5,
                noise = 0.04f,
                phaseError = -20,
                scanSamples = 640,
                lineCompensation = 0.7f,
                seed = 9,
                strength = 0.95f
            ),
            filter.value
        )
        val encoded = listOf(filter).toDatastoreString(includeValue = true)
        assertTrue(encoded.contains("PalCompositeParams:v1|"))
        assertEquals(filter.value, encoded.toFiltersList(true).single().value)
    }

    @Test
    fun unpublishedProceduralPayloadIsNotAcceptedAsACompatibilityFormat() {
        assertThrows(IllegalArgumentException::class.java) {
            "$uiPrefix.UiHelixWavesFilter:ProceduralParams:invalid!".toFiltersList(true)
        }
        assertThrows(IllegalArgumentException::class.java) {
            "$uiPrefix.UiBrightnessFilter:BrightnessParams:0.5".toFiltersList(true)
        }
    }

    @Test
    fun acceptsDomainValuesThroughTheGeneratedFactoriesAndCodec() {
        val value = object : Filter.AgxToneMapping.Value {
            override val exposure = 0.5f
            override val look = 1
            override val saturation = 0.8f
            override val strength = 0.25f
        }
        val params = AgxToneMappingParams(
            exposure = 0.5f,
            look = 1,
            saturation = 0.8f,
            strength = 0.25f
        )
        val filter = AgxToneMappingFilter(value)
        val ui = GeneratedFilterCatalog.toUiFilter(filter)

        assertEquals(params, ui?.value)
        assertEquals(
            params,
            GeneratedFilterCatalog.create(Filter.AgxToneMapping::class, value)?.value
        )
        assertEquals(
            params,
            listOf(filter).toDatastoreString(true).toFiltersList(true).single().value
        )
        assertEquals(AgxToneMappingFilter(params).cacheKey, filter.cacheKey)
    }

    private companion object {
        const val uiPrefix = "^^.core.filters.presentation.model"
        const val transformationPrefix = "^^.feature.filters.data.model"
    }
}
