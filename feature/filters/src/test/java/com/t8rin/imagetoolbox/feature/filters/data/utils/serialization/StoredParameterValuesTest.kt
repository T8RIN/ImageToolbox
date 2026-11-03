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
import com.t8rin.imagetoolbox.feature.filters.data.model.editorParam
import com.t8rin.imagetoolbox.feature.filters.data.model.floatParam
import com.t8rin.imagetoolbox.feature.filters.data.model.structuredParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class StoredParameterValuesTest {

    @Test
    fun displayOrderDoesNotChangeThePayloadOrRestoredValues() {
        val first = structuredParameters<Value> {
            Value::amount.parameter(floatParam(default = 0f, range = 0f..1f))
            Value::radius.parameter(floatParam(default = 0f, range = 0f..1f))
        }
        val reordered = structuredParameters<Value> {
            Value::radius.parameter(floatParam(default = 0f, range = 0f..1f))
            Value::amount.parameter(floatParam(default = 0f, range = 0f..1f))
        }
        val value = Value(0.25f, 0.75f)
        val payload = requireNotNull(first.encode(value))
        assertEquals(payload, reordered.encode(value))
        val stored = StoredParameterValues(payload, reordered.fields.keys.toList(), emptyList())
        assertEquals(value.amount, stored.read(Value::amount, reordered.field(Value::amount)))
        assertEquals(value.radius, stored.read(Value::radius, reordered.field(Value::radius)))
    }

    @Test
    fun publishedPositionsAreIndependentOfCurrentDisplayOrder() {
        val display =
            listOf(Filter.BulgeDistortion.Value::scale, Filter.BulgeDistortion.Value::radius)
        val stored = StoredParameterValues(
            "0.25$0.75",
            display,
            Filter.BulgeDistortion::class.storedFieldOrder()
        )
        val number = floatParam(default = 0f, range = -1f..1f)
        assertEquals(0.25f, stored.read(Filter.BulgeDistortion.Value::radius, number))
        assertEquals(0.75f, stored.read(Filter.BulgeDistortion.Value::scale, number))
    }

    @Test
    fun addingAFieldUsesItsDefaultForNamedAndPublishedPositionalPayloads() {
        val number = floatParam(default = 0.5f, range = 0f..1f)
        val properties = listOf(Value::radius, Value::amount)
        listOf("0.25", StoredParameterValues.encode(mapOf("amount" to "0.25"))).forEach { payload ->
            val stored = StoredParameterValues(payload, properties, listOf(Value::amount))
            assertEquals(0.25f, stored.read(Value::amount, number))
            assertEquals(0.5f, stored.read(Value::radius, number))
        }
    }

    @Test
    fun nullableValuesAreDistinctFromLiteralNullMarkersAndSeparators() {
        val field = editorParam<String?>(default = { "default" }, encode = { it }, decode = { it })
        listOf(null, "~", "", "a$|,+:\\=текст").forEach { value ->
            val payload = StoredParameterValues.encode(mapOf("text" to value))
            val stored = StoredParameterValues(payload, listOf(Text::text), emptyList())
            assertEquals(value, stored.read(Text::text, field, nullable = true))
        }
        val stored = StoredParameterValues("~", listOf(Text::text), listOf(Text::text))
        assertNull(stored.read(Text::text, field, nullable = true))
    }

    @Test
    fun duplicateUnknownAndUnsupportedFieldsFailInsteadOfBecomingDefaults() {
        listOf(
            "v1|amount=MA==|amount=MQ==",
            "v1|renamed=MA==",
            "v2|amount=MA==",
            "v1|amount=_w=="
        ).forEach { payload ->
            assertThrows(IllegalArgumentException::class.java) {
                StoredParameterValues(payload, listOf(Value::amount), emptyList())
            }
        }
        val number = floatParam(default = 0f, range = 0f..1f)
        val nullValue = StoredParameterValues("v1|amount=~", listOf(Value::amount), emptyList())
        assertThrows(IllegalArgumentException::class.java) {
            nullValue.read(Value::amount, number)
        }
    }

    private data class Value(val amount: Float, val radius: Float) : Filter.ParameterValue
    private data class Text(val text: String?)
}
