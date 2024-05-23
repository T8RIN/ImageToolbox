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

package ru.tech.imageresizershrinker.core.filters.presentation.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter

val LocalFavoriteFiltersInteractor = compositionLocalOf<FavoriteFiltersInteractor<Bitmap>> {
    error("No FavoriteFiltersInteractor<Bitmap> present")
}

@Composable
fun ProvidableCompositionLocal<FavoriteFiltersInteractor<Bitmap>>.getFavoriteFiltersAsUiState(
    context: Context = LocalContext.current
): State<List<UiFilter<*>>> = current.getFavoriteFilters()
    .map { list ->
        list.map {
            it.toUiFilter()
        }.sortedBy {
            context.getString(it.title)
        }
    }
    .collectAsState(emptyList())

@Composable
fun ProvidableCompositionLocal<FavoriteFiltersInteractor<Bitmap>>.getTemplateFiltersAsUiState(
    context: Context = LocalContext.current
): State<List<TemplateFilter<Bitmap>>> = current.getTemplateFilters()
    .map { list ->
        list.sortedBy { it.name }
    }
    .collectAsState(emptyList())