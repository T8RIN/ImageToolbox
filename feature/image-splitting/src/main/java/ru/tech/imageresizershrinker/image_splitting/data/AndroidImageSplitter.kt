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

package ru.tech.imageresizershrinker.image_splitting.data

import android.graphics.Bitmap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.image_splitting.domain.ImageSplitter
import ru.tech.imageresizershrinker.image_splitting.domain.SplitParams
import javax.inject.Inject

internal class AndroidImageSplitter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val shareProvider: ImageShareProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : ImageSplitter, DispatchersHolder by dispatchersHolder {

    override suspend fun split(
        imageUri: String,
        params: SplitParams,
        onProgress: (Int) -> Unit
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
                quality = params.quality
            )
        } else if (params.columnsCount <= 1) {
            splitForRows(
                image = image,
                count = params.rowsCount,
                imageFormat = params.imageFormat,
                quality = params.quality
            )
        } else {
            splitBoth(
                image = image,
                rowsCount = params.rowsCount,
                columnsCount = params.columnsCount,
                imageFormat = params.imageFormat,
                quality = params.quality
            )
        }
    }

    private suspend fun splitBoth(
        image: Bitmap,
        rowsCount: Int,
        columnsCount: Int,
        imageFormat: ImageFormat,
        quality: Quality
    ): List<String> = withContext(defaultDispatcher) {
        val cellHeight = image.height / rowsCount.toFloat()
        val uris = mutableListOf<Deferred<List<String>>>()

        for (row in 0 until rowsCount) {
            val y = (row * cellHeight).toInt()
            val height = if (y + cellHeight.toInt() > image.height) {
                image.height - y
            } else cellHeight.toInt()

            val rowBitmap = Bitmap.createBitmap(image, 0, y, image.width, height)

            val rowUris = async {
                splitForColumns(
                    image = rowBitmap,
                    count = columnsCount,
                    imageFormat = imageFormat,
                    quality = quality
                )
            }
            uris.add(rowUris)
        }

        uris.flatMap { it.await() }
    }

    private suspend fun splitForRows(
        image: Bitmap,
        count: Int,
        imageFormat: ImageFormat,
        quality: Quality
    ): List<String> = withContext(defaultDispatcher) {
        val cellHeight = image.height / count.toFloat()

        val uris = mutableListOf<String?>()

        for (i in 0 until count) {
            val y = (i * cellHeight).toInt()
            val height = if (y + cellHeight.toInt() > image.height) {
                image.height - y
            } else cellHeight.toInt()

            val cell = Bitmap.createBitmap(image, 0, y, image.width, height)

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
        }

        uris.filterNotNull()
    }

    private suspend fun splitForColumns(
        image: Bitmap,
        count: Int,
        imageFormat: ImageFormat,
        quality: Quality
    ): List<String> = withContext(defaultDispatcher) {
        val cellWidth = image.width / count.toFloat()

        val uris = mutableListOf<String?>()

        for (i in 0 until count) {
            val x = (i * cellWidth).toInt()
            val width = if (x + cellWidth.toInt() > image.width) {
                image.width - x
            } else cellWidth.toInt()

            val cell = Bitmap.createBitmap(image, x, 0, width, image.height)

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
        }
        uris.filterNotNull()
    }

}