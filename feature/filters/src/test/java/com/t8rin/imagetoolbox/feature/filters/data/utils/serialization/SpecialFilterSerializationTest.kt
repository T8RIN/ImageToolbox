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
import com.t8rin.imagetoolbox.core.domain.model.OffsetModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValues
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.FadeSide
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontType
import com.t8rin.imagetoolbox.core.settings.presentation.model.asFontType
import com.t8rin.imagetoolbox.feature.filters.data.model.AsciiFilter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlin.io.encoding.Base64

class SpecialFilterSerializationTest {

    @Test
    fun oldSideFadeTemplatesKeepTheirScaleAndUseFullStrength() {
        val filter = restore("SideFade", "SideFadeParams", "Start\$0.73") as Filter.SideFade

        assertEquals(FadeSide.Start, filter.value.side)
        assertEquals(0.73f, filter.value.scale)
        assertEquals(1f, filter.value.strength)
        assertRoundTrip(filter)
    }

    @Test
    fun oldNtscTemplatesKeepAllFieldsAndDisabledSections() {
        val payload = "0.7\$1.0\$1.0\$1.0\$1.0\$19\$0.5\$1.0\$4.0\$1.0\$" +
                "4\$1\$2\$2\$1\$2\$0\$13\$3\$72.0\$" +
                "0.95\$0.03\$12\$15.0\$0.025\$0.25\$0.25\$0.5\$0.05\$1\$" +
                "0.45\$4.0\$0.5\$0.01\$1\$0.05\$0.1\$2\$2.5E-4\$0.5\$" +
                "0.001\$0.0\$0.0\$0\$2\$2.5E-5\$0.25\$1.0\$0.5\$4.0\$" +
                "0.05\$2\$2\$1.0\$1.0\$1.0\$1.0\$false\$true\$true\$" +
                "true\$true\$false\$true\$false"
        val filter = restore("VHSNtsc", "NtscParams", payload) as Filter.VHSNtsc

        assertEquals(0.7f, filter.value.amount)
        assertEquals(19, filter.value.seed)
        assertEquals(13, filter.value.headSwitchingHeight)
        assertFalse(filter.value.headSwitchingEnabled)
        assertFalse(filter.value.chromaNoiseEnabled)
        assertFalse(filter.value.scaleEnabled)
        assertRoundTrip(filter)
    }

    @Test
    fun oldAsciiTemplatesKeepTheirFontColorsAndText() {
        val text = " .,:+\\$@"
        val encodedText = Base64.encode(text.encodeToByteArray())
        val filter = restore(
            "Ascii",
            "AsciiParams",
            "$encodedText\$17.5\$-13408615\$true\$1"
        ) as Filter.Ascii

        assertEquals(text, filter.value.gradient)
        assertEquals(17.5f, filter.value.fontSize)
        assertEquals(ColorModel(-13408615), filter.value.backgroundColor)
        assertEquals(true, filter.value.isGrayscale)
        assertEquals(DomainFontFamily.Montserrat.asFontType(), filter.value.font)
        assertRoundTrip(filter)
    }

    @Test
    fun asciiTemplatesSupportNullAndCustomFontsWithoutDelimiterCollisions() {
        val text = " .,:+\\$@"
        for (font in listOf(null, FontType.File("/fonts/custom:font,+\\.ttf"))) {
            val filter = AsciiFilter(
                FilterValues.ascii(
                    gradient = text,
                    fontSize = 17.5f,
                    backgroundColor = ColorModel(-13408615),
                    isGrayscale = true,
                    font = font
                )
            )
            val saved = listOf(filter).toDatastoreString(true)
            val restored = saved.toFiltersList(true).single() as Filter.Ascii

            assertEquals(text, restored.value.gradient)
            assertEquals(font, restored.value.font)
            assertEquals(saved, listOf(restored).toDatastoreString(true))
        }
    }

    @Test
    fun oldCropTemplatesKeepNamedCornersAndAbsoluteCoordinates() {
        val payload = "13.0*7.0\$240.0*11.0\$5.0*180.0\$230.0*190.0\$true"
        val filter = restore(
            "CropOrPerspective",
            "CropOrPerspectiveParams",
            payload
        ) as Filter.CropOrPerspective

        assertEquals(OffsetModel(13f, 7f), filter.value.topLeft)
        assertEquals(OffsetModel(240f, 11f), filter.value.topRight)
        assertEquals(OffsetModel(5f, 180f), filter.value.bottomLeft)
        assertEquals(OffsetModel(230f, 190f), filter.value.bottomRight)
        assertEquals(true, filter.value.isAbsolute)
        assertRoundTrip(filter)
    }

    @Test
    fun oldDistortTemplatesCombineCoordinateFieldsWithoutResettingThem() {
        val filter = restore(
            "DistortPerspective", "DistortPerspectiveParams",
            "0.13\$0.07\$0.92\$0.11\$0.05\$0.89\$0.81\$0.95\$true"
        ) as Filter.DistortPerspective

        assertEquals(OffsetModel(0.13f, 0.07f), filter.value.topLeft)
        assertEquals(OffsetModel(0.92f, 0.11f), filter.value.topRight)
        assertEquals(OffsetModel(0.05f, 0.89f), filter.value.bottomLeft)
        assertEquals(OffsetModel(0.81f, 0.95f), filter.value.bottomRight)
        assertEquals(true, filter.value.clip)
        assertRoundTrip(filter)
    }

    @Test
    fun oldToneCurvesTemplatesKeepEveryControlPoint() {
        val points = listOf(
            listOf(0f, 0.1f, 0.5f, 0.7f, 1f, 0.9f),
            listOf(0f, 0f, 1f, 0.8f),
            listOf(0.6f)
        )
        val payload = points.joinToString("$") { it.joinToString("*") }
        val filter = restore("ToneCurves", "ToneCurvesParams", payload) as Filter.ToneCurves

        assertEquals(points, filter.value.controlPoints)
        assertRoundTrip(filter)
    }

    private fun restore(name: String, params: String, payload: String): Filter<*> {
        val ui = "$prefix.Ui${name}Filter:$params:$payload".toFiltersList(true).single()
        val transformation = "^^.feature.filters.data.model.${name}Filter:$params:$payload"
            .toFiltersList(true).single()
        assertEquals(ui.value, transformation.value)
        return ui
    }

    private fun assertRoundTrip(filter: Filter<*>) {
        val saved = listOf(filter).toDatastoreString(true)
        val restored = saved.toFiltersList(true).single()
        assertEquals(filter.value, restored.value)
        assertEquals(saved, listOf(restored).toDatastoreString(true))
    }

    private companion object {
        const val prefix = "^^.core.filters.presentation.model"
    }
}
