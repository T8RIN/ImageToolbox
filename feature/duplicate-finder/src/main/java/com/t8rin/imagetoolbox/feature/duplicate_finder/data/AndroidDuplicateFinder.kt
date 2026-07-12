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

package com.t8rin.imagetoolbox.feature.duplicate_finder.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.saving.io.UriReadable
import com.t8rin.imagetoolbox.core.data.utils.computeFromReadable
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.model.HashingType
import com.t8rin.imagetoolbox.core.utils.distinctUris
import com.t8rin.imagetoolbox.core.utils.extension
import com.t8rin.imagetoolbox.core.utils.fileSize
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.core.utils.imageSize
import com.t8rin.imagetoolbox.core.utils.lastModified
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.DuplicateFinder
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DHash
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.helper.DuplicateGrouping
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisError
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisPhase
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisProgress
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateAnalysisResult
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import javax.inject.Inject

internal class AndroidDuplicateFinder @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DuplicateFinder, DispatchersHolder by dispatchersHolder {

    override suspend fun findDuplicates(
        uris: List<String>,
        sensitivity: Int,
        onProgress: (DuplicateAnalysisProgress) -> Unit
    ): DuplicateAnalysisResult = withContext(defaultDispatcher) {
        val targetUris = uris
            .map(String::toUri)
            .distinctUris()
            .map(Uri::toString)
        val items = mutableListOf<DuplicateItem>()
        val errors = mutableListOf<DuplicateAnalysisError>()
        var processed = 0

        fun report(phase: DuplicateAnalysisPhase) {
            onProgress(
                DuplicateAnalysisProgress(
                    phase = phase,
                    processed = processed,
                    total = targetUris.size
                )
            )
        }

        report(DuplicateAnalysisPhase.Reading)
        targetUris.forEachIndexed { sourceIndex, uri ->
            ensureActive()
            try {
                val item = readImage(
                    uri = uri.toUri(),
                    sourceIndex = sourceIndex
                )

                require(item.sha256.isNotBlank()) { "SHA-256 must not be empty" }

                items += item.copy(
                    uri = uri,
                    sourceIndex = sourceIndex,
                    distance = null
                )
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (throwable: Throwable) {
                ensureActive()
                errors += DuplicateAnalysisError(
                    uri = uri,
                    message = throwable.message
                        ?: throwable::class.simpleName
                        ?: "Failed to read URI"
                )
            }
            processed++
            report(DuplicateAnalysisPhase.Reading)
            yield()
        }

        report(DuplicateAnalysisPhase.GroupingExact)
        yield()
        report(DuplicateAnalysisPhase.GroupingSimilar)
        val groups = DuplicateGrouping.regroup(
            items = items,
            sensitivity = sensitivity
        )
        ensureActive()
        report(DuplicateAnalysisPhase.Completed)

        DuplicateAnalysisResult(
            items = items,
            groups = groups,
            errors = errors,
            requestedCount = targetUris.size
        )
    }

    private suspend fun readImage(
        uri: Uri,
        sourceIndex: Int
    ): DuplicateItem = withContext(ioDispatcher) {
        val sha256 = HashingType.SHA_256.computeFromReadable(UriReadable(uri, context))

        val thumbnail = imageGetter.getImage(
            data = uri,
            size = 128
        ) ?: error("Unable to decode $uri")
        val originalSize = uri.imageSize()
        val normalized = createBitmap(DHash.WIDTH, DHash.HEIGHT).apply {
            Canvas(this).drawBitmap(
                thumbnail,
                null,
                Rect(0, 0, DHash.WIDTH, DHash.HEIGHT),
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
        }
        val pixels = IntArray(DHash.PIXEL_COUNT)
        try {
            normalized.getPixels(
                pixels,
                0,
                DHash.WIDTH,
                0,
                0,
                DHash.WIDTH,
                DHash.HEIGHT
            )
        } finally {
            normalized.recycle()
        }

        DuplicateItem(
            uri = uri.toString(),
            name = uri.filename(context)
                ?.takeIf(String::isNotBlank)
                ?: uri.lastPathSegment?.substringAfterLast('/').orEmpty()
                    .ifBlank { "image_${sourceIndex + 1}" },
            width = originalSize?.width ?: thumbnail.width,
            height = originalSize?.height ?: thumbnail.height,
            sizeBytes = uri.fileSize() ?: 0L,
            format = uri.extension(context)
                ?.uppercase()
                ?: context.contentResolver.getType(uri)
                    ?.substringAfterLast('/')
                    ?.uppercase()
                    .orEmpty()
                    .ifBlank { "IMAGE" },
            lastModified = uri.lastModified(),
            sourceIndex = sourceIndex,
            sha256 = sha256,
            dHash = DHash.calculateFromArgb(pixels),
            isCorrectSize = originalSize != null
        )
    }

}
