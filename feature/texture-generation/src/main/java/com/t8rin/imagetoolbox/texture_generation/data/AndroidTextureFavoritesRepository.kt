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

package com.t8rin.imagetoolbox.texture_generation.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.t8rin.imagetoolbox.texture_generation.domain.TextureFavoritesRepository
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AndroidTextureFavoritesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TextureFavoritesRepository {

    override val favorites: Flow<Set<TextureFilterType>> = dataStore.data.map { preferences ->
        preferences[FAVORITE_TEXTURES].orEmpty().mapNotNullTo(mutableSetOf()) { name ->
            TextureFilterType.entries.find { it.name == name }
        }
    }

    override suspend fun toggleFavorite(type: TextureFilterType) {
        val current = favorites.first()
        dataStore.edit { preferences ->
            preferences[FAVORITE_TEXTURES] = if (type in current) {
                current.minus(type).mapTo(mutableSetOf()) { it.name }
            } else {
                current.plus(type).mapTo(mutableSetOf()) { it.name }
            }
        }
    }
}

private val FAVORITE_TEXTURES = stringSetPreferencesKey("FAVORITE_TEXTURES")