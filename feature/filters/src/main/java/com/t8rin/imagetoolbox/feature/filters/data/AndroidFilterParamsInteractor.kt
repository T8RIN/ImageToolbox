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

package com.t8rin.imagetoolbox.feature.filters.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.archive.ArchiveCompressionLevel
import com.t8rin.archive.ArchiveFormat
import com.t8rin.archive.SevenZipCompressionMethod
import com.t8rin.archive.ZipCompressionMethod
import com.t8rin.imagetoolbox.core.domain.TEMPLATE_EXT
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.saving.io.Writeable
import com.t8rin.imagetoolbox.core.filters.domain.FilterParamsInteractor
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.toImageModel
import com.t8rin.imagetoolbox.feature.archive_tools.domain.ArchiveManager
import com.t8rin.imagetoolbox.feature.archive_tools.domain.model.ArchiveExtractionOptions
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.PACKAGE_ALIAS
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.REAL_PACKAGE
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.toDatastoreString
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.toFiltersList
import com.t8rin.imagetoolbox.feature.filters.data.utils.serialization.toTemplateFiltersList
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.random.Random

internal class AndroidFilterParamsInteractor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStore<Preferences>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val imageGetter: ImageGetter<Bitmap>,
    private val archiveManager: ArchiveManager,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, FilterParamsInteractor {

    override fun getRecentFilters(): Flow<List<Filter<*>>> = dataStore.data.map { prefs ->
        prefs[RECENT_FILTERS]?.toFiltersList(false) ?: emptyList()
    }

    override suspend fun addRecentFilter(filter: Filter<*>) {
        dataStore.edit { prefs ->
            val currentFilters = prefs[RECENT_FILTERS]?.toFiltersList(false) ?: emptyList()
            val newList = listOf(filter) + currentFilters.filter {
                !it::class.java.isInstance(filter)
            }
            prefs[RECENT_FILTERS] = newList.take(10).toDatastoreString()
        }
    }

    override fun getFavoriteFilters(): Flow<List<Filter<*>>> = dataStore.data.map { prefs ->
        prefs[FAVORITE_FILTERS]?.toFiltersList(false) ?: emptyList()
    }

    override suspend fun toggleFavorite(filter: Filter<*>) {
        val currentFilters = getFavoriteFilters().first()
        if (currentFilters.filterIsInstance(filter::class.java).isEmpty()) {
            dataStore.edit { prefs ->
                prefs[FAVORITE_FILTERS] =
                    (listOf(filter) + currentFilters).toDatastoreString()
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

    override fun getTemplateFilters(): Flow<List<TemplateFilter>> =
        dataStore.data.map { prefs ->
            prefs[TEMPLATE_FILTERS]
                ?.takeIf(String::isNotEmpty)
                ?.toTemplateFiltersList()
                .orEmpty()
        }

    override suspend fun addTemplateFilter(
        templateFilter: TemplateFilter,
        replacing: TemplateFilter?
    ) {
        val currentTemplates = getTemplateFilters().first()
        val replacingContent = replacing?.let(::encodeTemplateFilter)
        val newContent = encodeTemplateFilter(templateFilter)
        val alreadyAdded = replacing == null && currentTemplates.any {
            encodeTemplateFilter(it) == newContent
        }

        if (alreadyAdded) return

        val nextTemplates = currentTemplates
            .filterNot { template ->
                val content = encodeTemplateFilter(template)
                content == replacingContent || content == newContent
            }
            .plus(templateFilter)

        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = nextTemplates.toDatastoreString()
        }
    }

    override suspend fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) {
        runCatching {
            decodeTemplateFilter(string).also { addTemplateFilter(it) }
        }.onSuccess { templateFilter ->
            onSuccess(templateFilter.name, templateFilter.filters.size)
        }.onFailure {
            onFailure()
        }
    }

    override suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter
    ): String = encodeTemplateFilter(templateFilter)

    override suspend fun removeTemplateFilter(templateFilter: TemplateFilter) {
        val content = encodeTemplateFilter(templateFilter)
        val nextTemplates = getTemplateFilters().first().filterNot {
            encodeTemplateFilter(it) == content
        }
        dataStore.edit { prefs ->
            prefs[TEMPLATE_FILTERS] = nextTemplates.toDatastoreString()
        }
    }

    override suspend fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onFailure: suspend () -> Unit
    ) {
        readText(uri)?.let { content ->
            addTemplateFilterFromString(content, onSuccess, onFailure)
        } ?: onFailure()
    }

    override suspend fun addTemplateFiltersFromUris(uris: List<String>): List<TemplateFilter> {
        return uris.flatMap { uri ->
            val sources = if (uri.toUri().filename(context).orEmpty().isZipArchive()) {
                runCatching {
                    archiveManager.extractToCache(
                        archive = uri,
                        passphrase = null,
                        options = ArchiveExtractionOptions(
                            createSubfolder = false,
                            preserveDirectoryStructure = false,
                            skipHiddenFiles = true
                        ),
                        onProgress = {}
                    )
                }.getOrDefault(emptyList())
            } else {
                listOf(uri)
            }

            sources.mapNotNull { source ->
                readText(source)?.let(::decodeTemplateFilterOrNull)
            }
        }.onEach { addTemplateFilter(it) }
    }

    override suspend fun exportTemplateFilters(
        templateFilters: List<TemplateFilter>,
        destination: Writeable
    ) {
        val temporaryFolder = withContext(ioDispatcher) {
            File(
                context.cacheDir,
                "filter_template_export_${Random.nextInt()}"
            ).apply { mkdirs() }
        }

        try {
            val files = withContext(ioDispatcher) {
                templateFilters.mapIndexed { index, templateFilter ->
                    File(
                        temporaryFolder,
                        templateFilter.archiveEntryName(index)
                    ).apply {
                        bufferedWriter().use { writer ->
                            writer.write(encodeTemplateFilter(templateFilter))
                        }
                    }.toUri().toString()
                }
            }
            archiveManager.archive(
                files = files,
                destination = destination,
                format = ArchiveFormat.Zip,
                zipCompressionMethod = ZipCompressionMethod.Deflate,
                sevenZipCompressionMethod = SevenZipCompressionMethod.Lzma2,
                compressionLevel = ArchiveCompressionLevel.Normal,
                passphrase = null,
                onProgress = {}
            )
        } finally {
            withContext(ioDispatcher) {
                temporaryFolder.deleteRecursively()
            }
        }
    }

    override fun isValidTemplateFilter(string: String): Boolean =
        (REAL_PACKAGE in string || PACKAGE_ALIAS in string) &&
                "Filter" in string &&
                LINK_HEADER in string

    override suspend fun reorderFavoriteFilters(newOrder: List<Filter<*>>) {
        dataStore.edit { prefs ->
            prefs[FAVORITE_FILTERS] = newOrder.toDatastoreString()
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

    override fun getCanSetDynamicFilterPreview(): Flow<Boolean> =
        dataStore.data.map { it[CAN_SET_DYNAMIC_FILTER_PREVIEW] == true }

    override suspend fun setCanSetDynamicFilterPreview(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[CAN_SET_DYNAMIC_FILTER_PREVIEW] = value
        }
    }

    private fun encodeTemplateFilter(templateFilter: TemplateFilter): String =
        "$LINK_HEADER${listOf(templateFilter).toDatastoreString()}"

    private fun decodeTemplateFilter(content: String): TemplateFilter {
        check(isValidTemplateFilter(content))
        return content
            .removePrefix(LINK_HEADER)
            .toTemplateFiltersList()
            .firstOrNull()
            ?: error("Invalid filter template")
    }

    private fun decodeTemplateFilterOrNull(content: String): TemplateFilter? =
        runCatching { decodeTemplateFilter(content) }.getOrNull()

    private suspend fun readText(uri: String): String? = withContext(ioDispatcher) {
        runCatching {
            context.contentResolver.openInputStream(uri.toUri())
                ?.bufferedReader()
                ?.use { it.readText() }
        }.getOrNull()
    }

    private fun TemplateFilter.archiveEntryName(index: Int): String {
        val safeName = name
            .trim()
            .replace(Regex("[^\\p{L}\\p{N}._-]+"), "_")
            .ifEmpty { "template" }
        return "${index + 1}_$safeName.$TEMPLATE_EXT"
    }
}

private fun String.isZipArchive(): Boolean {
    val filename = lowercase()
    return filename.endsWith(".zip") ||
            filename.endsWith(".zipx") ||
            filename.endsWith(".cbz")
}

private const val LINK_HEADER: String = "https://github.com/T8RIN/ImageToolbox?"

private val FAVORITE_FILTERS = stringPreferencesKey("FAVORITE_FILTERS")
private val RECENT_FILTERS = stringPreferencesKey("RECENT_FILTERS")
private val TEMPLATE_FILTERS = stringPreferencesKey("TEMPLATE_FILTERS")
private val PREVIEW_MODEL = stringPreferencesKey("PREVIEW_MODEL")
private val CAN_SET_DYNAMIC_FILTER_PREVIEW = booleanPreferencesKey("CAN_SET_DYNAMIC_FILTER_PREVIEW")