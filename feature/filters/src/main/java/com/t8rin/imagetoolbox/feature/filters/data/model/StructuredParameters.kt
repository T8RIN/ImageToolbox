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

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.filters.domain.model.StructuredFilterParameters
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.StoredParameterValues
import kotlin.reflect.KProperty1

internal class StructuredParameters<T : Filter.ParameterValue>(
    val fields: Map<KProperty1<in T, *>, FilterField<*>>
) : StructuredFilterParameters<T> {

    override fun get(property: KProperty1<in T, *>): FilterParam = field(property).info

    @Suppress("UNCHECKED_CAST")
    fun <V> field(property: KProperty1<in T, V>): FilterField<V> =
        requireNotNull(fields[property]) { "Missing filter parameter ${property.name}" } as FilterField<V>

    override val controls: List<FilterParam> by lazy {
        fields.values.flatMap { it.controls }
    }

    override val isSerializable: Boolean = fields.values.all { it.isSerializable }

    fun orderedFields(
        vararg properties: KProperty1<in T, *>
    ): List<KProperty1<in T, *>> {
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
            val current = property.get(value)
            property.name to if (current == null) null else field.encode(current) ?: return null
        }.toMap()
        return StoredParameterValues.encode(values)
    }

    fun cacheKey(value: T): String = filterCacheKey(
        fields.map { (property, field) ->
            property.name to field.cache(property.get(value))
        }.toMap()
    )

    @Suppress("UNCHECKED_CAST")
    private fun <V> FilterField<V>.encode(value: Any): String? = encodeValue(value as V)

    @Suppress("UNCHECKED_CAST")
    private fun <V> FilterField<V>.cache(value: Any?): Any? =
        if (value == null) null else cacheValue(value as V)
}

internal fun <T : Filter.ParameterValue> structuredParameters(
    block: StructuredParametersBuilder<T>.() -> Unit
): StructuredParameters<T> = StructuredParametersBuilder<T>().apply(block).build()
