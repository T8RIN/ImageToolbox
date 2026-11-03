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

import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.StructuredFilterParameters
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.FILTER_FIELDS_SEPARATOR
import kotlin.reflect.KProperty1

internal class StructuredParameters<T : Filter.ParameterValue>(
    val fields: Map<KProperty1<in T, *>, FilterField<*>>
) : StructuredFilterParameters<T> {

    override fun get(property: KProperty1<in T, *>): FilterParam = field(property).info

    fun number(property: KProperty1<in T, Float>): FilterField.Number =
        field(property) as FilterField.Number

    fun integer(property: KProperty1<in T, Int>): FilterField.Integer =
        field(property) as FilterField.Integer

    fun toggle(property: KProperty1<in T, Boolean>): FilterField.Toggle =
        field(property) as FilterField.Toggle

    fun color(property: KProperty1<in T, ColorModel>): FilterField.Color =
        field(property) as FilterField.Color

    fun array(property: KProperty1<in T, FloatArray>): FilterField.Array =
        field(property) as FilterField.Array

    @Suppress("UNCHECKED_CAST")
    fun <V> custom(property: KProperty1<in T, V>): FilterField.Value<V> =
        field(property) as FilterField.Value<V>

    fun gmic(property: KProperty1<in T, GmicFilterParams>): GmicParameters =
        field(property) as GmicParameters

    override val controls: List<FilterParam> by lazy {
        fields.values.flatMap { it.controls }
    }

    override val isSerializable: Boolean = fields.values.all { it.isSerializable }

    fun orderedFields(vararg properties: KProperty1<in T, *>): List<KProperty1<in T, *>> {
        val expected = properties.toSet()
        require(fields.keys == expected) {
            val missing = expected - fields.keys
            val extra = fields.keys - expected
            "Invalid filter parameters: missing ${missing.map { it.name }}, extra ${extra.map { it.name }}"
        }
        return fields.keys.toList()
    }

    fun encode(value: T): String? {
        if (!isSerializable) return null
        val values = fields.map { (property, field) ->
            field.encode(property.get(value)) ?: return null
        }
        return values.joinToString(FILTER_FIELDS_SEPARATOR)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <V> FilterField<V>.encode(value: Any?): String? =
        if (value == null) "~" else encodeValue(value as V)

    private fun field(property: KProperty1<in T, *>): FilterField<*> =
        requireNotNull(fields[property]) { "Missing filter parameter ${property.name}" }
}

internal class StructuredParametersBuilder<T : Filter.ParameterValue> {
    private val fields = mutableMapOf<KProperty1<in T, *>, FilterField<*>>()

    fun field(property: KProperty1<in T, Float>, field: FilterField.Number) = bind(property, field)

    fun field(property: KProperty1<in T, Int>, field: FilterField.Integer) = bind(property, field)

    fun field(property: KProperty1<in T, Boolean>, field: FilterField.Toggle) =
        bind(property, field)

    fun field(property: KProperty1<in T, ColorModel>, field: FilterField.Color) =
        bind(property, field)

    fun <V> field(property: KProperty1<in T, V>, field: FilterField.Value<V>) =
        bind(property, field)

    private fun <V> bind(property: KProperty1<in T, V>, field: FilterField<V>) {
        require(property !in fields) { "Duplicate filter parameter ${property.name}" }
        fields[property] = field
    }

    fun build(): StructuredParameters<T> = StructuredParameters(fields.toMap())
}

internal fun <T : Filter.ParameterValue> structuredParameters(
    block: StructuredParametersBuilder<T>.() -> Unit
): StructuredParameters<T> = StructuredParametersBuilder<T>().apply(block).build()
