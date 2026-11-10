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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

internal class GalleryOcrWorker(
    context: Context,
    parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val dependencies = applicationContext.galleryIndexDependencies()
        val repository = dependencies.galleryOcr()

        dependencies.scheduler().runIndex(GalleryIndex.Ocr) {
            val fullAccess = applicationContext.hasFullGalleryPhotoAccess()
            val media = dependencies.mediaRetriever().getMediaByType(AllowedMedia.Photos(null))
                .first().getOrThrow()

            indexWithProgress(
                notification = GalleryIndexNotification(
                    context = applicationContext,
                    channelId = "gallery_ocr_channel",
                    workId = id,
                    title = R.string.gallery_ocr
                ),
                progress = repository.state.filter { it.isIndexing }.map {
                    GalleryIndexProgress(
                        processed = it.processedImages,
                        total = it.totalImages,
                        text = R.string.gallery_ocr_progress
                    )
                }
            ) {
                repository.index(media, pruneMissing = fullAccess)
            }
        }
        return Result.success()
    }

}