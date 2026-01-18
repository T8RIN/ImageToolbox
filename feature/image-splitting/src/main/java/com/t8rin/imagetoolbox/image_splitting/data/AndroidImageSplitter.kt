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
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.image_splitting.domain.ImageSplitter
import com.t8rin.imagetoolbox.image_splitting.domain.SplitParams
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
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

        if (params.rowsCount <= 1) {
            splitForColumns(
                image = image,
                count = params.columnsCount,
                imageFormat = params.imageFormat,
                quality = params.quality,
                columnPercentages = params.columnPercentages,
            )
        } else if (params.columnsCount <= 1) {
            splitForRows(
                image = image,
                count = params.rowsCount,
                imageFormat = params.imageFormat,
                quality = params.quality,
                rowPercentages = params.rowPercentages
            )
        } else {
            splitBoth(
                image = image,
                rowsCount = params.rowsCount,
                columnsCount = params.columnsCount,
                imageFormat = params.imageFormat,
                quality = params.quality,
                rowPercentages = params.rowPercentages,
                columnPercentages = params.columnPercentages
            )
        }
    }

    private suspend fun splitBoth(
        image: Bitmap,
        rowsCount: Int,
        columnsCount: Int,
        imageFormat: ImageFormat,
        quality: Quality,
        rowPercentages: List<Float> = emptyList(),
        columnPercentages: List<Float> = emptyList()
    ): List<String> = withContext(defaultDispatcher) {
        val rowHeights = calculatePartSizes(image.height, rowPercentages, rowsCount)
        val uris = mutableListOf<Deferred<List<String>>>()

        var currentY = 0
        for (row in 0 until rowsCount) {
            val height = rowHeights[row]
            val rowBitmap = Bitmap.createBitmap(image, 0, currentY, image.width, height)

            val rowUris = async {
                splitForColumns(
                    image = rowBitmap,
                    count = columnsCount,
                    imageFormat = imageFormat,
                    quality = quality,
                    columnPercentages = columnPercentages
                )
            }
            uris.add(rowUris)
            currentY += height
        }

        uris.flatMap { it.await() }
    }

    private suspend fun splitForRows(
        image: Bitmap,
        count: Int,
        imageFormat: ImageFormat,
        quality: Quality,
        rowPercentages: List<Float> = emptyList()
    ): List<String> = withContext(defaultDispatcher) {
        val rowHeights = calculatePartSizes(image.height, rowPercentages, count)
        val uris = mutableListOf<String?>()

        var currentY = 0
        for (i in 0 until count) {
            val height = rowHeights[i]
            val cell = Bitmap.createBitmap(image, 0, currentY, image.width, height)

            uris.add(
                shareProvider.cacheImage(
                    image = cell,
                    imageInfo = ImageInfo(
                        height = cell.height,
                        width = cell.width,
                        imageFormat = imageFormat,
                        quality = quality.coerceIn(imageFormat)
                    )
                )
            )
            currentY += height
        }

        uris.filterNotNull()
    }

    private suspend fun splitForColumns(
        image: Bitmap,
        count: Int,
        imageFormat: ImageFormat,
        quality: Quality,
        columnPercentages: List<Float> = emptyList()
    ): List<String> = withContext(defaultDispatcher) {
        val columnWidths = calculatePartSizes(image.width, columnPercentages, count)
        val uris = mutableListOf<String?>()

        var currentX = 0
        for (i in 0 until count) {
            val width = columnWidths[i]
            val cell = Bitmap.createBitmap(image, currentX, 0, width, image.height)

            uris.add(
                shareProvider.cacheImage(
                    image = cell,
                    imageInfo = ImageInfo(
                        height = cell.height,
                        width = cell.width,
                        imageFormat = imageFormat,
                        quality = quality.coerceIn(imageFormat)
                    )
                )
            )
            currentX += width
        }
        uris.filterNotNull()
    }

    private fun calculatePartSizes(
        totalSize: Int,
        percentages: List<Float>,
        count: Int
    ): List<Int> {
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

        return normalized.map { percentage ->
            (totalSize * percentage).toInt()
        }.let { sizes ->
            val calculatedTotal = sizes.sum()
            if (calculatedTotal != totalSize) {
                sizes.dropLast(1) + (totalSize - sizes.dropLast(1).sum())
            } else {
                sizes
            }
        }
    }

}