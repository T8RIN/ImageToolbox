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
import com.t8rin.imagetoolbox.feature.filters.data.AndroidFilterProvider
import com.t8rin.imagetoolbox.feature.filters.presentation.model.GeneratedFilterCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NewPhotoFiltersTest {

    @Test
    fun newFiltersAreRegisteredAndRoundTripWithEditedValues() {
        val provider = AndroidFilterProvider()
        for (name in names) {
            val key = "com.t8rin.imagetoolbox.core.filters.presentation.model.Ui${name}Filter"
            val ui = requireNotNull(GeneratedFilterCatalog.restore(key, null)) { name }
            assertTrue(name, ui.canUseTemplate)
            assertEquals(name, ui.value, ui.newInstance().value)
            val initial = listOf(ui).toDatastoreString(true)
            val changed = initial.substringBeforeLast(':') + ":" +
                    StoredParameterValues.encode(mapOf("strength" to "0.375"))
            val restored = changed.toFiltersList(true).single()
            val encoded = listOf(restored).toDatastoreString(true)
            assertNotEquals(name, ui.value, restored.value)
            assertEquals(name, restored.value, encoded.toFiltersList(true).single().value)
            val transform = provider.filterToTransformation(restored)
            assertEquals(name, restored.value, (transform as Filter<*>).value)
            assertNotEquals(name, provider.filterToTransformation(ui).cacheKey, transform.cacheKey)
            val favorite = listOf(ui).toDatastoreString(false)
            assertEquals(name, favorite, favorite.toFiltersList(false).toDatastoreString(false))
        }
    }

    @Test
    fun fullNewFilterChainRoundTripsThroughSharingCodec() {
        val filters = names.map { name ->
            requireNotNull(
                GeneratedFilterCatalog.restore(
                    "com.t8rin.imagetoolbox.core.filters.presentation.model.Ui${name}Filter", null
                )
            )
        }
        val template = listOf(TemplateFilter("New photo effects", filters))
        val stored = template.toDatastoreString()
        val link = TemplateLinkCodec.encode(stored)
        assertEquals(stored, TemplateLinkCodec.decode(link))
        assertEquals(
            stored,
            TemplateLinkCodec.decode(link).toTemplateFiltersList().toDatastoreString()
        )
        assertTrue(link.length < stored.length)
    }

    private val names = listOf(
        "HeightAmbientOcclusion",
        "HeightCastShadows",
        "RefractiveCaustics",
        "ThinFilmIridescence",
        "DiffractionGrating",
        "FlowTexture",
        "HilbertRibbon",
        "MazePortrait",
        "KnittedFabric",
        "OrigamiRelief",
        "PageCurl",
        "CurlAdvection",
        "HistogramMosaic",
        "LocalBinaryPattern",
        "LocalEntropyMap",
        "FourierSpectrum",
        "PhaseOnlyReconstruction",
        "SpectralSaliency",
        "MorphologicalReconstruction",
        "NailStringArt",
        "FoldedBook",
        "PaperQuilling",
        "LenticularPrint",
        "PinArt",
        "CeramicGlaze",
        "Marquetry",
        "PhotoelasticStress",
        "Condensation",
        "SpecularHighlightSuppression",
        "GradientDomainToneMapping",
        "ShockSharpening",
        "ColorSpillSuppression",
        "UnderwaterRestoration"
    )
}