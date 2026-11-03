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
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiAgxToneMappingFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiAnamorphicStreaksFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiCapillaryInkBleedFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiDctGlitchFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilmHalationFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilmNegativeFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFourierPhaseScrambleFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiGuidedSmoothingFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPaletteRemapFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPhotoCrossStitchFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiSignedDistanceFieldFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiWienerDeconvolutionFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBayerSensorFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBloomFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBokehLightsFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiBrightnessFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiCmykPressFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilmGrainFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiLcdSubpixelsFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiPalCompositeFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiRfReceptionFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiRisographFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiSstvReceptionFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiThermalReceiptFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiVHSNtscFilter
import io.github.alexzhirkevich.qrose.generateQrCodeMatrix
import io.github.alexzhirkevich.qrose.options.QrErrorCorrectionLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.DeflaterOutputStream
import kotlin.io.encoding.Base64

class TemplateLinkCodecTest {

    @Test
    fun releasedPlainLinksStillRestoreTheirOriginalValues() {
        val payload = "Before+^^.core.filters.presentation.model.UiBrightnessFilter:Float:0.35"
        assertEquals(payload, TemplateLinkCodec.decode(HEADER + payload))
        val restored = TemplateLinkCodec.decode(HEADER + payload).toTemplateFiltersList().single()
        assertEquals(0.35f, (restored.filters.single() as Filter.Brightness).value.bias)
        assertEquals("z1.name+value", TemplateLinkCodec.decode(HEADER + "z1.name+value"))
    }

    @Test
    fun smallLinksUsePlainTextWhenCompressionWouldMakeThemLonger() {
        val payload =
            listOf(TemplateFilter("Small", listOf(UiBrightnessFilter()))).toDatastoreString()
        val link = TemplateLinkCodec.encode(payload)
        assertEquals(HEADER + payload, link)
        assertEquals(payload, TemplateLinkCodec.decode(link))
    }

    @Test
    fun complexRecipesKeepFilterOrderAndValuesAndFitTheActualQrEncoder() {
        val mixed = listOf(
            UiVHSNtscFilter(),
            UiPalCompositeFilter(),
            UiFilmGrainFilter(),
            UiBloomFilter(),
            UiBokehLightsFilter(),
            UiCmykPressFilter(),
            UiRisographFilter(),
            UiThermalReceiptFilter(),
            UiSstvReceptionFilter(),
            UiRfReceptionFilter(),
            UiBayerSensorFilter(),
            UiLcdSubpixelsFilter()
        )
        val longRecipe = mixed + listOf(
            UiAgxToneMappingFilter(),
            UiFilmHalationFilter(),
            UiFilmNegativeFilter(),
            UiPaletteRemapFilter(),
            UiSignedDistanceFieldFilter(),
            UiDctGlitchFilter(),
            UiCapillaryInkBleedFilter(),
            UiPhotoCrossStitchFilter(),
            UiAnamorphicStreaksFilter(),
            UiGuidedSmoothingFilter(),
            UiWienerDeconvolutionFilter(),
            UiFourierPhaseScrambleFilter()
        )
        val samples = listOf(
            listOf(UiBrightnessFilter()),
            listOf(UiVHSNtscFilter()),
            mixed,
            longRecipe
        )
        samples.forEach { filters ->
            val template = TemplateFilter("Recipe", filters)
            val payload = listOf(template).toDatastoreString()
            val link = TemplateLinkCodec.encode(payload)
            val restored = TemplateLinkCodec.decode(link).toTemplateFiltersList().single()
            assertEquals(template.name, restored.name)
            assertEquals(filters.map { it::class }, restored.filters.map { it::class })
            assertEquals(filters.map { it.value }, restored.filters.map { it.value })
            assertEquals(payload, TemplateLinkCodec.decode(link))
            assertTrue(link.encodeToByteArray().size <= HEADER.length + payload.encodeToByteArray().size)
            val matrix = generateQrCodeMatrix(link, QrErrorCorrectionLevel.Auto)
            assertTrue(matrix.size in 21..177)
            println("Template QR: filters=${filters.size}, plain=${HEADER.length + payload.encodeToByteArray().size}, shared=${link.length}, modules=${matrix.size}")
        }
    }

    @Test
    fun compressedTransportPreservesUnicodeAndSeparatorCharacters() {
        val payload = "Тест+" + "радиус=1|цвет=~,$:++\\\n".repeat(100)
        val link = TemplateLinkCodec.encode(payload)
        assertTrue(link.startsWith(HEADER + "z1."))
        assertFalse('+' in link.removePrefix(HEADER))
        assertEquals(payload, TemplateLinkCodec.decode(link))
    }

    @Test
    fun corruptTruncatedTrailingAndUnknownCompressedFormatsAreRejected() {
        val link = compressed("Example+".repeat(100).encodeToByteArray())
        val bytes = Base64.UrlSafe.decode(link.removePrefix(HEADER + "z1."))
        val damaged = bytes.copyOf().apply { this[lastIndex] = (last().toInt() xor 1).toByte() }
        listOf(
            HEADER + "z2." + link.removePrefix(HEADER + "z1."),
            HEADER + "z1." + Base64.UrlSafe.encode(bytes.copyOf(bytes.size - 1)),
            HEADER + "z1." + Base64.UrlSafe.encode(bytes + byteArrayOf(0)),
            HEADER + "z1." + Base64.UrlSafe.encode(damaged),
            HEADER + "z1.!invalid!",
            "https://example.com/?" + link.removePrefix(HEADER)
        ).forEach { invalid ->
            assertThrows(IllegalArgumentException::class.java) { TemplateLinkCodec.decode(invalid) }
        }
        assertThrows(IllegalArgumentException::class.java) {
            TemplateLinkCodec.decode(compressed(byteArrayOf(-1)))
        }
    }

    @Test
    fun decompressionAndPlainInputAreBounded() {
        val oversized = ByteArray(1024 * 1024 + 1) { 'a'.code.toByte() }
        assertThrows(IllegalArgumentException::class.java) {
            TemplateLinkCodec.decode(
                compressed(
                    oversized
                )
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            TemplateLinkCodec.decode(HEADER + "Example+" + oversized.decodeToString())
        }
        assertThrows(IllegalArgumentException::class.java) {
            TemplateLinkCodec.encode("Example+" + oversized.decodeToString())
        }
    }

    private fun compressed(bytes: ByteArray): String {
        val output = ByteArrayOutputStream()
        DeflaterOutputStream(output).use { it.write(bytes) }
        return HEADER + "z1." + Base64.UrlSafe.encode(output.toByteArray())
    }

    private companion object {
        const val HEADER = "https://github.com/T8RIN/ImageToolbox?"
    }
}
