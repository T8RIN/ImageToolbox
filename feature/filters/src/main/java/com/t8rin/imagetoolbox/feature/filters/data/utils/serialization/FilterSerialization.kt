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

import com.t8rin.imagetoolbox.core.filters.domain.FilterFactory
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.utils.Logger
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.filters.data.model.GeneratedFilterRegistry
import javax.inject.Inject

internal class FilterSerialization @Inject constructor(
    private val factory: FilterFactory
) {
    fun encodeFilters(filters: List<Filter<*>>, includeValue: Boolean = false): String =
        filters.joinToString(separator = FILTERS_SEPARATOR) { filter ->
            (factory.key(filter)
                ?: requireNotNull(GeneratedFilterRegistry.transformationKey(filter)))
                .replace(REAL_PACKAGE, PACKAGE_ALIAS) + if (includeValue) {
                VALUE_SEPARATOR + GeneratedFilterRegistry.encodeParams(filter)
                    ?.let { it.first + VALUE_SEPARATOR + it.second }
            } else ""
        }.trim()

    fun decodeFilters(value: String, includeValue: Boolean): List<Filter<*>> =
        value.split(FILTERS_SEPARATOR).mapNotNull { line ->
            if (line.trim().isEmpty()) return@mapNotNull null

            val parts = line.split(VALUE_SEPARATOR, limit = 3)
            val key = parts[0].trim().replace(PACKAGE_ALIAS, REAL_PACKAGE)
            val params = when {
                !includeValue || parts.size == 1 -> null
                parts.size == 3 -> parts[1] to parts[2]
                else -> {
                    require(parts[1].trim() == "null") { "Missing filter parameter payload" }
                    "null" to ""
                }
            }
            val filter = GeneratedFilterRegistry.restoreStored(key, params, factory)
            if (includeValue) requireNotNull(filter) { "Unknown filter $key" } else filter
        }

    fun decodeTemplates(value: String, skipInvalid: Boolean = false): List<TemplateFilter> =
        value.split(TEMPLATES_SEPARATOR).filter(String::isNotBlank).mapNotNull { entry ->
            val result = runCatching {
                val splitData = entry.split(TEMPLATE_CONTENT_SEPARATOR, limit = 2)
                require(splitData.size == 2) { "Missing template contents" }
                TemplateFilter(
                    name = splitData[0],
                    filters = decodeFilters(splitData[1], true)
                )
            }
            if (skipInvalid) {
                result.onFailure { it.makeLog("Filter template restoration", Logger.Level.Warn) }
                    .getOrNull()
            } else result.getOrThrow()
        }

    fun encodeTemplates(templates: List<TemplateFilter>): String =
        templates.joinToString(separator = TEMPLATES_SEPARATOR) {
            it.name + TEMPLATE_CONTENT_SEPARATOR + encodeFilters(it.filters, true)
        }
}

private const val FILTERS_SEPARATOR = ","
private const val TEMPLATES_SEPARATOR = "\\"
private const val TEMPLATE_CONTENT_SEPARATOR = "+"
private const val VALUE_SEPARATOR = ":"

internal const val FILTER_FIELDS_SEPARATOR = "$"
internal const val REAL_PACKAGE = "com.t8rin.imagetoolbox"
internal const val PACKAGE_ALIAS = "^^"
