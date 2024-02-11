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

@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.feature.filters.data

import android.graphics.Bitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.di.FilterInteractorDataStore
import javax.inject.Inject
import kotlin.reflect.full.primaryConstructor

internal class FavoriteFiltersInteractorImpl @Inject constructor(
    @FilterInteractorDataStore private val dataStore: DataStore<Preferences>
) : FavoriteFiltersInteractor<Bitmap> {

    override fun getFavoriteFilters(): Flow<List<Filter<Bitmap, *>>> = dataStore.data.map { prefs ->
        prefs[FAVORITE_FILTERS]?.toFiltersList() ?: emptyList()
    }

    override suspend fun toggleFavorite(filter: Filter<Bitmap, *>) {
        val currentFilters = getFavoriteFilters().first()
        if (currentFilters.filterIsInstance(filter::class.java).isEmpty()) {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = (currentFilters + filter).toDatastoreString()
            }
        } else {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = currentFilters
                    .filter {
                        !it::class.java.isInstance(filter)
                    }
                    .toDatastoreString()
            }
        }
    }

    private fun List<Filter<Bitmap, *>>.toDatastoreString(): String =
        joinToString(separator = ",") {
            it::class.qualifiedName!!
        }.trim()

    private fun String.toFiltersList(): List<Filter<Bitmap, *>> = split(",").mapNotNull {
        runCatching {
            val filterClass = Class.forName(it.trim()) as Class<Filter<Bitmap, *>>
            filterClass.kotlin.primaryConstructor?.callBy(emptyMap())
        }.getOrNull()
    }

}

private val FAVORITE_FILTERS = stringPreferencesKey("FAVORITE_FILTERS")