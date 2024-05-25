/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.filters.domain

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter

interface FavoriteFiltersInteractor<Image> {

    fun getFavoriteFilters(): Flow<List<Filter<Image, *>>>

    suspend fun toggleFavorite(filter: Filter<Image, *>)

    suspend fun addTemplateFilter(templateFilter: TemplateFilter<Image>)

    fun getTemplateFilters(): Flow<List<TemplateFilter<Image>>>

    suspend fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onError: suspend () -> Unit
    )

    suspend fun convertTemplateFilterToString(templateFilter: TemplateFilter<Image>): String

    suspend fun removeTemplateFilter(templateFilter: TemplateFilter<Image>)

    suspend fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onError: suspend () -> Unit
    )

}