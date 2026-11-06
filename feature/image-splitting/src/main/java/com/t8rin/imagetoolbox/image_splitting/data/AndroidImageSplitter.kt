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

package com.t8rin.imagetoolbox.image_splitting.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.image_splitting.domain.ImageSplitter
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidImageSplitter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : ImageSplitter, DispatchersHolder by dispatchersHolder {

    override suspend fun split(
        imageUri: String,
        params: SplitParams
    ): List<String> = withContext(defaultDispatcher) {
        if (params.columnsCount <= 1 && params.rowsCount <= 1) {
            return@withContext listOf(imageUri)
        }

        val image = imageGetter.getImage(
            data = imageUri,
            originalSize = true
        ) ?: return@withContext emptyList()

        val rowHeights = calculatePartSizes(image.height, params.rowPercentages, params.rowsCount)
        val columnWidths =
            calculatePartSizes(image.width, params.columnPercentages, params.columnsCount)
        val uris = mutableListOf<String>()
        val quality = params.quality.coerceIn(params.imageFormat)
        var y = 0

        for (height in rowHeights) {
            var x = 0
            for (width in columnWidths) {
                ensureActive()
                val cell = Bitmap.createBitmap(image, x, y, width, height)
                try {
                    shareProvider.cacheImage(
                        image = cell,
                        imageInfo = ImageInfo(
                            width = width,
                            height = height,
                            imageFormat = params.imageFormat,
                            quality = quality
                        )
                    )?.let(uris::add)
                } finally {
                    if (cell !== image) cell.recycle()
                }
                x += width
            }
            y += height
        }

        uris
    }

    private fun calculatePartSizes(
        totalSize: Int,
        percentages: List<Float>,
        count: Int
    ): List<Int> {
        val count = count.coerceIn(1, totalSize)
        if (percentages.isEmpty()) {
            val partSize = totalSize / count
            return List(count) { index ->
                if (index == count - 1) {
                    totalSize - (partSize * (count - 1))
                } else {
                    partSize
                }
            }
        }

        val normalizedPercentages = if (percentages.size < count) {
            val remainingPercentage = 1f - percentages.sum()
            val remainingParts = count - percentages.size
            val equalPercentage = remainingPercentage / remainingParts
            percentages + List(remainingParts) { equalPercentage }
        } else if (percentages.size > count) {
            percentages.take(count)
        } else {
            percentages
        }

        val totalPercentage = normalizedPercentages.sum()
        val normalized = normalizedPercentages.map { it / totalPercentage }

        var remainingSize = totalSize
        return normalized.mapIndexed { index, percentage ->
            val remainingParts = count - index - 1
            val size = if (remainingParts == 0) {
                remainingSize
            } else {
                (totalSize * percentage).toInt().coerceIn(1, remainingSize - remainingParts)
            }
            remainingSize -= size
            size
        }
    }

}