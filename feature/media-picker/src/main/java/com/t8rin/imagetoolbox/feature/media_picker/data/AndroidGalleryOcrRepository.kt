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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.content.Context
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.size.Scale
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.static
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryOcrRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.GalleryOcrState
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.neural_tools.ocr.GalleryOcr
import com.t8rin.neural_tools.ocr.GalleryOcrModels
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

internal class AndroidGalleryOcrRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageLoader: ImageLoader,
    private val settings: SettingsProvider,
    dispatchers: DispatchersHolder
) : GalleryOcrRepository, DispatchersHolder by dispatchers {

    private val models = GalleryOcrModels(File(context.noBackupFilesDir, "gallery-ocr"))
    private val _state = MutableStateFlow(GalleryOcrState(modelsReady = models.isInstalled))
    override val state = _state.asStateFlow()

    override fun updateWorkState(isQueued: Boolean?, isWaitingForPermission: Boolean?) {
        _state.update {
            it.copy(
                isQueued = isQueued ?: it.isQueued,
                isWaitingForPermission = isWaitingForPermission ?: it.isWaitingForPermission
            )
        }
    }

    override suspend fun downloadModels() = withContext(ioDispatcher) {
        if (models.isInstalled) {
            _state.update { it.copy(modelsReady = true) }
            return@withContext
        }
        _state.update { it.copy(isDownloading = true, downloadProgress = 0f) }
        try {
            models.download { progress ->
                _state.update { it.copy(downloadProgress = progress) }
            }
        } finally {
            _state.update { it.copy(isDownloading = false, modelsReady = models.isInstalled) }
        }
    }

    override suspend fun index(
        media: List<Media>,
        pruneMissing: Boolean,
        shouldYield: () -> Boolean
    ): Boolean =
        withContext(ioDispatcher) {
            val images = media.filter { !it.isTrashed && it.mimeType.startsWith("image/") }
                .distinctBy(Media::uri)
            GalleryOcrIndex(context).use { index ->
                val pending =
                    index.pending(images, pruneMissing && context.hasFullGalleryPhotoAccess())
                _state.update {
                    it.copy(
                        isIndexing = pending.isNotEmpty(),
                        processedImages = images.size - pending.size,
                        totalImages = images.size
                    )
                }
                try {
                    var remaining = pending.size
                    if (pending.isNotEmpty()) GalleryOcr(models).use { ocr ->
                        for ((position, item) in pending.withIndex()) {
                            currentCoroutineContext().ensureActive()
                            if (shouldYield()) return@withContext true
                            if (!context.hasSemanticSearchPermission()) {
                                updateWorkState(isWaitingForPermission = true)
                                return@withContext false
                            }
                            if (pruneMissing && !context.hasFullGalleryPhotoAccess()) return@withContext true

                            val result = loadImage(item.media.uri)?.let { image ->
                                try {
                                    ocr.recognize(image, extended = item.attempts > 0)
                                } finally {
                                    image.recycle()
                                }
                            }
                            currentCoroutineContext().ensureActive()
                            val recognized = result?.text.orEmpty()
                            val status = when {
                                result?.partial == true || recognized.isBlank() && item.text.isNotBlank() ->
                                    GalleryOcrIndex.Status.Partial

                                result == null -> GalleryOcrIndex.Status.Unreadable
                                recognized.isBlank() -> GalleryOcrIndex.Status.NoText
                                else -> GalleryOcrIndex.Status.Text
                            }
                            val text =
                                if (status == GalleryOcrIndex.Status.Partial && item.text.length > recognized.length) {
                                    item.text
                                } else recognized
                            index.put(item, status, text)
                            if (status == GalleryOcrIndex.Status.Text || status == GalleryOcrIndex.Status.NoText ||
                                item.attempts + 1 >= GalleryOcrIndex.MAX_ATTEMPTS
                            ) remaining--
                            _state.update {
                                it.copy(
                                    processedImages = images.size - remaining,
                                    indexRevision = it.indexRevision + if ((position + 1) % 16 == 0) 1 else 0
                                )
                            }
                        }
                    }
                    if (remaining == 0) _state.update { it.copy(hasIndexed = true) }
                    remaining > 0
                } finally {
                    _state.update {
                        it.copy(
                            isIndexing = false,
                            indexRevision = it.indexRevision + 1
                        )
                    }
                }
            }
        }

    override suspend fun search(query: String?, media: List<Media>): List<Media> =
        withContext(ioDispatcher) {
            if (query?.isBlank() == true || media.isEmpty() || !canSearch()) return@withContext emptyList()
            GalleryOcrIndex(context).use { it.search(query, media) }
                .takeIf { canSearch() }.orEmpty()
        }

    override suspend fun hasPending(media: List<Media>): Boolean =
        withContext(ioDispatcher) {
            media.isNotEmpty() && canSearch() && GalleryOcrIndex(context).use { it.hasPending(media) }
        }

    private suspend fun canSearch() = settings.getSettingsState().isGalleryOcrEnabled &&
            context.hasSemanticSearchPermission()

    private suspend fun loadImage(uri: String) = (imageLoader.execute(
        ImageRequest.Builder(context)
            .data(uri.toUri())
            .static()
            .allowHardware(false)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .size(GalleryOcr.MAX_IMAGE_SIZE)
            .scale(Scale.FIT)
            .precision(Precision.INEXACT)
            .build()
    ) as? SuccessResult)?.image?.toBitmap()
}
