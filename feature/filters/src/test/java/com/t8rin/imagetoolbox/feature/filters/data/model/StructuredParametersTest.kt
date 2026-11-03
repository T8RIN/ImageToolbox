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
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StructuredParametersTest {

    @Test
    fun nullableDefaultDoesNotMakeAnUnsupportedFieldSerializable() {
        val parameters = structuredParameters<ImageValue> {
            field(ImageValue::image, editorParam<ImageModel?>({
                error("Template eligibility must not evaluate defaults")
            }))
        }

        assertFalse(parameters.isSerializable)
        assertNull(parameters.encode(ImageValue(null)))
    }

    @Test
    fun aTypedCodecEnablesTemplatesWithoutReadingTheDefault() {
        val image = editorParam<ImageModel?>(
            default = { error("Template eligibility must not evaluate defaults") },
            encode = { it?.data as? String },
            decode = { ImageModel(it) }
        )
        val parameters = structuredParameters<ImageValue> {
            field(ImageValue::image, image)
        }

        assertTrue(parameters.isSerializable)
        assertEquals("~", parameters.encode(ImageValue(null)))
        assertEquals("sample", parameters.encode(ImageValue(ImageModel("sample"))))
        assertEquals(ImageModel("sample"), image.decodeValue("sample"))
    }

    @Test
    fun everyFieldMustSupportSerialization() {
        val numberOnly = structuredParameters<NumberValue> {
            field(NumberValue::strength, floatParam(default = 1f, range = 0f..1f))
        }
        val withImage = structuredParameters<MixedValue> {
            field(MixedValue::strength, floatParam(default = 1f, range = 0f..1f))
            field(MixedValue::image, editorParam<ImageModel?>({ null }))
        }

        assertTrue(numberOnly.isSerializable)
        assertFalse(withImage.isSerializable)
    }

    @Test
    fun aCodecNeedsBothEncodingAndDecoding() {
        val encodeOnly = editorParam<ImageModel?>(
            default = { null },
            encode = { it?.data as? String }
        )
        val decodeOnly = editorParam<ImageModel?>(
            default = { null },
            decode = { ImageModel(it) }
        )

        assertFalse(encodeOnly.isSerializable)
        assertFalse(decodeOnly.isSerializable)
    }

    private data class ImageValue(val image: ImageModel?) : Filter.ParameterValue

    private interface NumberValue : Filter.ParameterValue {
        val strength: Float
    }

    private interface MixedValue : NumberValue {
        val image: ImageModel?
    }
}
