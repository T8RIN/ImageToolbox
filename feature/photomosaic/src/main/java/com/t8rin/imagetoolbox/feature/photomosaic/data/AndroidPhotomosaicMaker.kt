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
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.utils.getSuitableConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.feature.photomosaic.domain.AdaptiveTileLayout
import com.t8rin.imagetoolbox.feature.photomosaic.domain.LabColor
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicMaker
import com.t8rin.imagetoolbox.feature.photomosaic.domain.PhotomosaicParams
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs
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
        val requestedTiles = tileUris
            .asSequence()
            .distinct()
            .take(normalizedParams.maxTiles)
            .toList()

        val sampleSize = if (preview) {
            PREVIEW_TILE_SIZE
        } else {
            val desiredSize = (target.width / columns)
                .coerceIn(MIN_TILE_SIZE, MAX_TILE_SIZE)
            val memorySafeSize = sqrt(
                MAX_TILE_MEMORY_BYTES.toDouble() /
                    (requestedTiles.size.coerceAtLeast(1) * BYTES_PER_PIXEL)
            ).toInt()

            desiredSize.coerceAtMost(memorySafeSize.coerceAtLeast(MIN_TILE_SIZE))
        }

        val tiles = requestedTiles.mapNotNull { uri ->
            coroutineContext.ensureActive()
            val bitmap = imageGetter.getImage(
                data = uri,
                size = sampleSize * 2
            )?.let {
                imageScaler.scaleImage(
                    image = it,
                    width = sampleSize,
                    height = sampleSize,
                    resizeType = ResizeType.Flexible
                )
            }
            bitmap?.let {
                Tile(
                    bitmap = it,
                    color = it.averageLabColor(),
                    aspectRatio = it.width / it.height.toFloat()
                )
            }
        }
        if (tiles.isEmpty()) return@withContext null

        val colorSampleRadius = (minOf(target.width, target.height) / COLOR_SAMPLE_DIVISOR)
            .coerceAtLeast(1)
        val placements = AdaptiveTileLayout.create(
            width = target.width,
            height = target.height,
            columns = columns,
            tiles = tiles.map {
                AdaptiveTileLayout.Tile(
                    aspectRatio = it.aspectRatio,
                    color = it.color
                )
            },
            repeatDistance = normalizedParams.repeatDistance,
            colorAt = { x, y ->
                target.averageLabColorAt(
                    x = x,
                    y = y,
                    radius = colorSampleRadius
                )
            },
            detailAt = { y -> target.detailAt(y) }
        )
        val totalProgress = requestedTiles.size + placements.size
        onProgress(requestedTiles.size, totalProgress)

        createBitmap(
            width = target.width,
            height = target.height,
            config = getSuitableConfig()
        ).also { result ->
            val canvas = Canvas(result)
            val source = Rect(0, 0, target.width, target.height)
            val destination = Rect(0, 0, result.width, result.height)
            canvas.drawBitmap(
                target,
                source,
                destination,
                Paint(Paint.FILTER_BITMAP_FLAG)
            )
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)

            placements.forEachIndexed { index, placement ->
                coroutineContext.ensureActive()
                val bitmap = tiles[placement.tileIndex].bitmap
                canvas.drawBitmap(
                    bitmap,
                    Rect(0, 0, bitmap.width, bitmap.height),
                    RectF(
                        placement.left,
                        placement.top,
                        placement.right,
                        placement.bottom
                    ),
                    paint
                )
                onProgress(requestedTiles.size + index + 1, totalProgress)
            }

            if (normalizedParams.colorBlend > 0f) {
                paint.alpha = (normalizedParams.colorBlend * ALPHA_MAX).roundToInt()
                canvas.drawBitmap(
                    target,
                    source,
                    destination,
                    paint
                )
            }
        }
    }

    private data class Tile(
        val bitmap: Bitmap,
        val color: LabColor,
        val aspectRatio: Float
    )

    private fun Bitmap.averageLabColorAt(
        x: Int,
        y: Int,
        radius: Int
    ): LabColor {
        var red = 0L
        var green = 0L
        var blue = 0L
        var count = 0L

        for (sampleY in (y - radius).coerceAtLeast(0)..(y + radius).coerceAtMost(height - 1)) {
            for (sampleX in (x - radius).coerceAtLeast(0)..(x + radius).coerceAtMost(width - 1)) {
                val color = getPixel(sampleX, sampleY)
                red += Color.red(color)
                green += Color.green(color)
                blue += Color.blue(color)
                count++
            }
        }

        return rgbToLab(
            red = (red / count).toInt(),
            green = (green / count).toInt(),
            blue = (blue / count).toInt()
        )
    }

    private fun Bitmap.detailAt(y: Int): Float {
        val step = (width / DETAIL_SAMPLE_COUNT).coerceAtLeast(1)
        val offset = (minOf(width, height) / DETAIL_OFFSET_DIVISOR).coerceAtLeast(1)
        val sampleY = y.coerceIn(0, height - 1)
        var difference = 0L
        var count = 0

        for (x in 0 until width step step) {
            val center = getPixel(x, sampleY).luminance()
            val horizontal = getPixel(
                (x + offset).coerceAtMost(width - 1),
                sampleY
            ).luminance()
            val vertical = getPixel(
                x,
                (sampleY + offset).coerceAtMost(height - 1)
            ).luminance()
            difference += abs(center - horizontal) + abs(center - vertical)
            count += 2
        }

        return (difference / (count * COLOR_CHANNEL_MAX).toFloat() * DETAIL_GAIN)
            .coerceIn(0f, 1f)
    }

    private fun Int.luminance(): Int = (
            Color.red(this) * RED_LUMINANCE_WEIGHT +
                    Color.green(this) * GREEN_LUMINANCE_WEIGHT +
                    Color.blue(this) * BLUE_LUMINANCE_WEIGHT
            ).roundToInt()

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
        const val COLOR_CHANNEL_MAX = 255
        const val COLOR_SAMPLE_DIVISOR = 256
        const val DETAIL_SAMPLE_COUNT = 24
        const val DETAIL_OFFSET_DIVISOR = 128
        const val DETAIL_GAIN = 4f
        const val RED_LUMINANCE_WEIGHT = 0.2126f
        const val GREEN_LUMINANCE_WEIGHT = 0.7152f
        const val BLUE_LUMINANCE_WEIGHT = 0.0722f
    }
}
