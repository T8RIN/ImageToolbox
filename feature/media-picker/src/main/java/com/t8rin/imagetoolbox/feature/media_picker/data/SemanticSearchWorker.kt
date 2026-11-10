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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.GalleryOcrRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.SemanticSearchRepository
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class SemanticSearchWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        if (GalleryIndex.Semantic.tag !in tags) return Result.success()
        val dependencies = applicationContext.galleryIndexDependencies()
        val repository = dependencies.semanticSearch()
        dependencies.scheduler().runIndex(GalleryIndex.Semantic) {
            repository.refresh()
            val media = dependencies.mediaRetriever().getMediaByType(AllowedMedia.Photos(null))
                .first().getOrThrow()
            indexWithProgress(
                notification = GalleryIndexNotification(
                    context = applicationContext,
                    channelId = "semantic_search_channel",
                    workId = id,
                    title = R.string.gallery_search_auto
                ),
                progress = repository.state.filter { it.isIndexing }.map {
                    GalleryIndexProgress(
                        processed = it.processedImages,
                        total = it.totalImages,
                        text = R.string.gallery_search_indexing
                    )
                }
            ) {
                repository.index(media)
            }
            false
        }
        return Result.success()
    }

}

internal class FaceGroupingWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val dependencies = applicationContext.galleryIndexDependencies()
        val repository = dependencies.semanticSearch()
        dependencies.scheduler().runIndex(GalleryIndex.Faces) {
            val media = dependencies.mediaRetriever().getMediaByType(AllowedMedia.Photos(null))
                .first().getOrThrow()
            indexWithProgress(
                notification = GalleryIndexNotification(
                    context = applicationContext,
                    channelId = "gallery_faces_channel",
                    workId = id,
                    title = R.string.gallery_people_grouping
                ),
                progress = repository.state.filter { it.isFacesIndexing }.map {
                    GalleryIndexProgress(
                        processed = it.processedFaceImages,
                        total = it.totalFaceImages,
                        text = R.string.gallery_faces_indexing
                    )
                }
            ) {
                repository.indexFaces(media)
            }
            false
        }
        return Result.success()
    }

}

internal class SemanticSearchObserverWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val scheduler = applicationContext.galleryIndexDependencies().scheduler()
        scheduler.refreshIndex(mediaChanged = true)
        return Result.success()
    }

}

internal class GalleryIndexRefreshWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        applicationContext.galleryIndexDependencies().scheduler().refreshIndex()
        return Result.success()
    }

}

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface GalleryIndexWorkerEntryPoint {
    fun settingsManager(): SettingsManager
    fun semanticSearch(): SemanticSearchRepository
    fun galleryOcr(): GalleryOcrRepository
    fun mediaRetriever(): MediaRetriever
    fun scheduler(): AndroidGalleryIndexScheduler
}

internal fun Context.galleryIndexDependencies(): GalleryIndexWorkerEntryPoint =
    EntryPointAccessors.fromApplication(this, GalleryIndexWorkerEntryPoint::class.java)

internal fun Context.hasFullGalleryPhotoAccess(): Boolean = when {
    Build.VERSION.SDK_INT >= 30 && Environment.isExternalStorageManager() -> true
    Build.VERSION.SDK_INT >= 33 -> hasGalleryPermission(Manifest.permission.READ_MEDIA_IMAGES)
    else -> hasGalleryPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
}

fun Context.hasSemanticSearchPermission(): Boolean = hasFullGalleryPhotoAccess() ||
        Build.VERSION.SDK_INT >= 34 && hasGalleryPermission(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)

private fun Context.hasGalleryPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED