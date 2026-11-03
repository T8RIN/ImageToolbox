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

import com.t8rin.imagetoolbox.feature.filters.data.model.FilterField
import java.nio.charset.CharacterCodingException
import kotlin.io.encoding.Base64
import kotlin.reflect.KProperty1

internal class StoredParameterValues(
    value: String,
    properties: List<KProperty1<*, *>>,
    legacyOrder: List<KProperty1<*, *>>
) {
    private val named = isNamed(value)

    private val values: Map<String, String?> = if (named) {
        require(value.startsWith(PREFIX)) { "Unsupported filter parameter format" }
        val entries = value.removePrefix(PREFIX).takeIf(String::isNotEmpty)
            ?.split('|').orEmpty()
        buildMap {
            entries.forEach { entry ->
                val separator = entry.indexOf('=')
                require(separator > 0) { "Invalid stored filter parameter" }
                val name = entry.substring(0, separator)
                require(properties.any { it.name == name }) { "Unknown filter parameter $name" }
                require(name !in this) { "Duplicate filter parameter $name" }
                val encoded = entry.substring(separator + 1)
                val decoded = if (encoded == "~") null else try {
                    Base64.UrlSafe.decode(encoded).decodeToString(throwOnInvalidSequence = true)
                } catch (exception: CharacterCodingException) {
                    throw IllegalArgumentException(
                        "Invalid text in filter parameter $name",
                        exception
                    )
                }
                put(name, decoded)
            }
        }
    } else {
        val entries = value.split(FILTER_FIELDS_SEPARATOR)
        require(entries.size <= legacyOrder.size) { "Too many stored filter parameters" }
        entries.mapIndexed { index, field -> legacyOrder[index].name to field }.toMap()
    }

    fun <T> read(
        property: KProperty1<*, T>,
        field: FilterField<T>,
        nullable: Boolean = false
    ): T {
        if (property.name !in values) return field.default
        val value = values[property.name]
        if (value == null || !named && nullable && value == "~") {
            require(nullable) { "Filter parameter ${property.name} cannot be null" }
            @Suppress("UNCHECKED_CAST")
            return null as T
        }
        return field.decodeValue(value)
    }

    companion object {
        private const val PREFIX = "v1|"
        private val versioned = Regex("v[0-9]+\\|.*", RegexOption.DOT_MATCHES_ALL)

        fun isNamed(value: String): Boolean = value.matches(versioned)

        fun encode(values: Map<String, String?>): String =
            values.toSortedMap().entries.joinToString(
                separator = "|",
                prefix = PREFIX
            ) { (name, value) ->
                "$name=${value?.let { Base64.UrlSafe.encode(it.encodeToByteArray()) } ?: "~"}"
            }
    }
}
