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

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.utils.runSuspendCatching
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.feature.filters.data.utils.serialization.PACKAGE_ALIAS
import ru.tech.imageresizershrinker.feature.filters.data.utils.serialization.toDatastoreString
import ru.tech.imageresizershrinker.feature.filters.data.utils.serialization.toFiltersList
import ru.tech.imageresizershrinker.feature.filters.data.utils.serialization.toTemplateFiltersList
import java.io.File
import javax.inject.Inject

internal class AndroidFavoriteFiltersInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>
) : FavoriteFiltersInteractor {

    override fun getFavoriteFilters(): Flow<List<Filter<*>>> = dataStore.data.map { prefs ->
        prefs[FAVORITE_FILTERS]?.toFiltersList(false, context) ?: emptyList()
    }

    override suspend fun toggleFavorite(filter: Filter<*>) {
        val currentFilters = getFavoriteFilters().first()
        if (currentFilters.filterIsInstance(filter::class.java).isEmpty()) {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] =
                    (listOf(filter) + currentFilters).toDatastoreString(context = context)
            }
        } else {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] = currentFilters
                    .filter {
                        !it::class.java.isInstance(filter)
                    }
                    .toDatastoreString(context = context)
            }
        }
    }

    override fun getTemplateFilters(): Flow<List<TemplateFilter>> =
        dataStore.data.map { prefs ->
            prefs[TEMPLATE_FILTERS]?.takeIf { it.isNotEmpty() }?.toTemplateFiltersList(context)
                ?: emptyList()
        }

    override suspend fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit,
    ) {
        runSuspendCatching {
            if (isValidTemplateFilter(string)) {
                runSuspendCatching {
                    string.removePrefix(LINK_HEADER).toTemplateFiltersList(context).firstOrNull()
                }.getOrNull()?.let {
                    addTemplateFilter(it)
                    onSuccess(it.name, it.filters.size)
                } ?: onFailure()
            } else onFailure()
        }.onFailure { onFailure() }
    }

    override fun isValidTemplateFilter(
        string: String,
    ): Boolean =
        (context.applicationInfo.packageName in string || PACKAGE_ALIAS in string) && "Filter" in string && LINK_HEADER in string

    override suspend fun reorderFavoriteFilters(newOrder: List<Filter<*>>) {
        dataStore.edit { prefs ->
            prefs[FAVORITE_FILTERS] = newOrder.toDatastoreString(context = context)
        }
    }

    override fun getFilterPreviewModel(): Flow<ImageModel> = dataStore.data.map { prefs ->
        prefs[PREVIEW_MODEL]?.let {
            when (it) {
                "0" -> R.drawable.filter_preview_source
                "1" -> R.drawable.filter_preview_source_3
                else -> it
            }.toImageModel()
        } ?: R.drawable.filter_preview_source.toImageModel()
    }

    override suspend fun setFilterPreviewModel(uri: String) {
        if (uri.any { !it.isDigit() }) {
            imageGetter.getImage(
                data = uri,
                size = 300
            )?.let { image ->
                fileController.writeBytes(
                    File(context.filesDir, "filterPreview.png").apply {
                        createNewFile()
                    }.toUri().toString()
                ) { writeable ->
                    writeable.writeBytes(
                        imageCompressor.compress(
                            image = image,
                            imageFormat = ImageFormat.Png.Lossless,
                            quality = Quality.Base(100)
                        )
                    )
                }
            }
        }
        dataStore.edit {
            it[PREVIEW_MODEL] = uri
        }
    }

    override suspend fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit,
    ) {
        addTemplateFilterFromString(
            string = fileController.readBytes(uri).decodeToString(),
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }

    override suspend fun removeTemplateFilter(templateFilter: TemplateFilter) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = currentFilters.filter {
                convertTemplateFilterToString(it) != convertTemplateFilterToString(templateFilter)
            }.toDatastoreString(context)
        }
    }

    override suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter,
    ): String = "$LINK_HEADER${listOf(templateFilter).toDatastoreString(context)}"

    override suspend fun addTemplateFilter(templateFilter: TemplateFilter) {
        val currentFilters = getTemplateFilters().first()
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = currentFilters.let { filters ->
                val index = filters.indexOfFirst {
                    convertTemplateFilterToString(it) == convertTemplateFilterToString(
                        templateFilter
                    )
                }
                if (index != -1) filters else filters + templateFilter
            }.toDatastoreString(context)
        }
    }
}

private const val LINK_HEADER: String = "https://github.com/T8RIN/ImageToolbox?"

private val FAVORITE_FILTERS = stringPreferencesKey("FAVORITE_FILTERS")
private val TEMPLATE_FILTERS = stringPreferencesKey("TEMPLATE_FILTERS")
private val PREVIEW_MODEL = stringPreferencesKey("PREVIEW_MODEL")