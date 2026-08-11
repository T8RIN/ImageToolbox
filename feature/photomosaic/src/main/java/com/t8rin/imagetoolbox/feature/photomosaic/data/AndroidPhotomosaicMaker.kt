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

package com.t8rin.imagetoolbox.feature.photomosaic.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.utils.getSuitableConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.feature.photomosaic.domain.LabColor
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicMaker
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicParams
import com.t8rin.imagetoolbox.feature.photomosaic.domain.TileMatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.cbrt
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

internal class AndroidPhotomosaicMaker @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    private val imageScaler: ImageScaler<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, PhotomosaicMaker<Bitmap> {

    override suspend fun create(
        targetUri: String,
        tileUris: List<String>,
        params: PhotomosaicParams,
        preview: Boolean,
        onProgress: (Int, Int) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        val target = imageGetter.getImage(
            data = targetUri,
            size = if (preview) PREVIEW_TARGET_SIZE else MAX_OUTPUT_SIZE
        ) ?: return@withContext null

        create(
            target = target,
            tileUris = tileUris,
            params = params,
            preview = preview,
            onProgress = onProgress
        )
    }

    override suspend fun create(
        target: Bitmap,
        tileUris: List<String>,
        params: PhotomosaicParams,
        preview: Boolean,
        onProgress: (Int, Int) -> Unit
    ): Bitmap? = withContext(defaultDispatcher) {
        val normalizedParams = params.normalized()

        val columns = normalizedParams.columns.coerceAtMost(target.width)
        val rows = (columns * target.height.toFloat() / target.width)
            .roundToInt()
            .coerceIn(1, target.height)
        val requestedTiles = tileUris
            .asSequence()
            .distinct()
            .take(normalizedParams.maxTiles)
            .toList()
        val totalProgress = requestedTiles.size + rows

        val sampleSize = if (preview) {
            PREVIEW_TILE_SIZE
        } else {
            val desiredSize = maxOf(
                target.width / columns,
                target.height / rows
            ).coerceIn(MIN_TILE_SIZE, MAX_TILE_SIZE)
            val memorySafeSize = sqrt(
                MAX_TILE_MEMORY_BYTES.toDouble() /
                    (requestedTiles.size.coerceAtLeast(1) * BYTES_PER_PIXEL)
            ).toInt()

            desiredSize.coerceAtMost(memorySafeSize.coerceAtLeast(MIN_TILE_SIZE))
        }

        val tiles = requestedTiles.mapIndexedNotNull { index, uri ->
            coroutineContext.ensureActive()
            val bitmap = imageGetter.getImage(
                data = uri,
                size = sampleSize * 2
            )?.let {
                imageScaler.scaleImage(
                    image = it,
                    width = sampleSize,
                    height = sampleSize,
                    resizeType = ResizeType.CenterCrop(0x00000000)
                )
            }
            onProgress(index + 1, totalProgress)
            bitmap?.let { Tile(bitmap = it, color = it.averageLabColor()) }
        }
        if (tiles.isEmpty()) return@withContext null

        val colorMap = createBitmap(
            width = columns,
            height = rows,
            config = Bitmap.Config.ARGB_8888
        ).also { bitmap ->
            Canvas(bitmap).drawBitmap(
                target,
                Rect(0, 0, target.width, target.height),
                Rect(0, 0, columns, rows),
                Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            )
        }
        val targetColors = buildList(columns * rows) {
            for (row in 0 until rows) {
                for (column in 0 until columns) {
                    add(colorMap.getPixel(column, row).toLabColor())
                }
            }
        }
        colorMap.recycle()

        val matches = TileMatcher.match(
            targets = targetColors,
            tiles = tiles.map(Tile::color),
            columns = columns,
            repeatDistance = normalizedParams.repeatDistance
        )

        createBitmap(
            width = target.width,
            height = target.height,
            config = getSuitableConfig()
        ).also { result ->
            val canvas = Canvas(result)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

            for (row in 0 until rows) {
                coroutineContext.ensureActive()
                val top = row * result.height / rows
                val bottom = (row + 1) * result.height / rows

                for (column in 0 until columns) {
                    val left = column * result.width / columns
                    val right = (column + 1) * result.width / columns
                    val bitmap = tiles[matches[row * columns + column]].bitmap
                    canvas.drawBitmap(
                        bitmap,
                        Rect(0, 0, bitmap.width, bitmap.height),
                        Rect(left, top, right, bottom),
                        paint
                    )
                }
                onProgress(requestedTiles.size + row + 1, totalProgress)
            }

            if (normalizedParams.colorBlend > 0f) {
                paint.alpha = (normalizedParams.colorBlend * ALPHA_MAX).roundToInt()
                canvas.drawBitmap(
                    target,
                    Rect(0, 0, target.width, target.height),
                    Rect(0, 0, result.width, result.height),
                    paint
                )
            }
        }
    }

    private data class Tile(
        val bitmap: Bitmap,
        val color: LabColor
    )

    private fun Bitmap.averageLabColor(): LabColor {
        val pixels = IntArray(width * height)
        getPixels(pixels, 0, width, 0, 0, width, height)

        var red = 0L
        var green = 0L
        var blue = 0L
        var count = 0L
        pixels.forEach { color ->
            val alpha = Color.alpha(color)
            if (alpha > MIN_VISIBLE_ALPHA) {
                red += Color.red(color)
                green += Color.green(color)
                blue += Color.blue(color)
                count++
            }
        }

        if (count == 0L) return LabColor(0.0, 0.0, 0.0)
        return rgbToLab(
            red = (red / count).toInt(),
            green = (green / count).toInt(),
            blue = (blue / count).toInt()
        )
    }

    private fun Int.toLabColor(): LabColor = rgbToLab(
        red = Color.red(this),
        green = Color.green(this),
        blue = Color.blue(this)
    )

    private fun rgbToLab(
        red: Int,
        green: Int,
        blue: Int
    ): LabColor {
        fun linearize(channel: Int): Double {
            val value = channel / 255.0
            return if (value <= 0.04045) {
                value / 12.92
            } else {
                ((value + 0.055) / 1.055).pow(2.4)
            }
        }

        val r = linearize(red)
        val g = linearize(green)
        val b = linearize(blue)
        val x = (r * 0.4124564 + g * 0.3575761 + b * 0.1804375) / 0.95047
        val y = (r * 0.2126729 + g * 0.7151522 + b * 0.0721750)
        val z = (r * 0.0193339 + g * 0.1191920 + b * 0.9503041) / 1.08883

        fun lab(value: Double): Double = if (value > 0.008856) {
            cbrt(value)
        } else {
            7.787 * value + 16.0 / 116.0
        }

        val fx = lab(x)
        val fy = lab(y)
        val fz = lab(z)
        return LabColor(
            lightness = 116.0 * fy - 16.0,
            a = 500.0 * (fx - fy),
            b = 200.0 * (fy - fz)
        )
    }
    private companion object {
        const val PREVIEW_TILE_SIZE = 48
        const val PREVIEW_TARGET_SIZE = 1024
        const val MAX_OUTPUT_SIZE = 4096
        const val MIN_TILE_SIZE = 32
        const val MAX_TILE_SIZE = 192
        const val MAX_TILE_MEMORY_BYTES = 24 * 1024 * 1024
        const val BYTES_PER_PIXEL = 4
        const val ALPHA_MAX = 255
        const val MIN_VISIBLE_ALPHA = 16
    }
}
