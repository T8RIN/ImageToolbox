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
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.feature.filters.data.model.GeneratedFilterCatalog

internal fun List<Filter<*>>.toDatastoreString(
    includeValue: Boolean = false
): String = joinToString(separator = FILTERS_SEPARATOR) { filter ->
    ((filter as? UiFilter<*>)?.serializationKey
        ?: requireNotNull(GeneratedFilterCatalog.transformationKey(filter))).replace(
        REAL_PACKAGE,
        PACKAGE_ALIAS
    ) + if (includeValue) {
        VALUE_SEPARATOR + GeneratedFilterCatalog.encodeParams(filter)
            ?.let { it.first + VALUE_SEPARATOR + it.second }
    } else ""
}.trim()


internal fun String.toFiltersList(
    includeValue: Boolean
): List<Filter<*>> = split(FILTERS_SEPARATOR).mapNotNull { line ->
    if (line.trim().isEmpty()) return@mapNotNull null

    val parts = line.split(VALUE_SEPARATOR, limit = 3)
    val key = parts[0].trim().replace(PACKAGE_ALIAS, REAL_PACKAGE)
    val params = if (includeValue && parts.size == 3) parts[1] to parts[2] else null
    GeneratedFilterCatalog.restoreStored(key, params)
}

internal fun String.toTemplateFiltersList(): List<TemplateFilter> =
    split(TEMPLATES_SEPARATOR).map {
        val splitData = it.split(TEMPLATE_CONTENT_SEPARATOR, limit = 2)
        val name = splitData[0]
        val filters = splitData[1].toFiltersList(true)

        TemplateFilter(
            name = name,
            filters = filters
        )
    }

internal fun List<TemplateFilter>.toDatastoreString(): String =
    joinToString(separator = TEMPLATES_SEPARATOR) {
        it.name + TEMPLATE_CONTENT_SEPARATOR + it.filters.toDatastoreString(true)
    }

private const val FILTERS_SEPARATOR = ","
private const val TEMPLATES_SEPARATOR = "\\"
private const val TEMPLATE_CONTENT_SEPARATOR = "+"
private const val VALUE_SEPARATOR = ":"

internal const val FILTER_FIELDS_SEPARATOR = "$"
internal const val REAL_PACKAGE = "com.t8rin.imagetoolbox"
internal const val PACKAGE_ALIAS = "^^"
