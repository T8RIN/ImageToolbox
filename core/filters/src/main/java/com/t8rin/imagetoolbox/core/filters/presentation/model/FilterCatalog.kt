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

package com.t8rin.imagetoolbox.core.filters.presentation.model

import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.appContextOrNull
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

interface FilterCatalog {
    fun filters(group: String): List<UiFilter<*>>

    fun restore(key: String, value: Any?): UiFilter<*>?

    fun toUiFilter(filter: Filter<*>): UiFilter<*>?

    fun create(type: KClass<out Filter<*>>, value: Any?): UiFilter<*>?
}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface FilterCatalogEntryPoint {
    val filterCatalog: FilterCatalog
}

internal val filterCatalog: FilterCatalog by lazy {
    EntryPointAccessors.fromApplication<FilterCatalogEntryPoint>(appContext).filterCatalog
}

internal fun restoreCatalogFilter(key: String, value: Any?): UiFilter<*>? =
    appContextOrNull?.let { filterCatalog.restore(key, value) }

@Suppress("UNCHECKED_CAST")
fun <T : Any> createUiFilter(
    type: KClass<out Filter<T>>,
    value: T? = null
): UiFilter<T> = requireNotNull(filterCatalog.create(type, value)) {
    "No UiFilter registered for ${type.qualifiedName}"
} as UiFilter<T>