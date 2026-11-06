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

package com.t8rin.imagetoolbox.image_cutting.data

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.ImageCutter
import com.t8rin.imagetoolbox.image_cutting.domain.PivotPair
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.roundToInt

internal class AndroidImageCutter @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : ImageCutter<Bitmap>, DispatchersHolder by dispatchersHolder {

    override suspend fun cutAndMerge(
        imageUri: String,
        params: CutParams
    ): Bitmap? {
        return cutAndMerge(
            image = imageGetter.getImage(
                data = imageUri,
                originalSize = true
            ) ?: return null,
            params = params
        )
    }

    override suspend fun cutAndMerge(
        image: Bitmap,
        params: CutParams
    ): Bitmap = withContext(defaultDispatcher) {
        runSuspendCatching {
            val (verticalStart, verticalEnd) = params.vertical.toCutBounds(image.width)
            val (horizontalStart, horizontalEnd) = params.horizontal.toCutBounds(image.height)

            image.cutAndMerge(
                verticalStart = verticalStart,
                verticalEnd = verticalEnd,
                horizontalStart = horizontalStart,
                horizontalEnd = horizontalEnd,
                inverseVertical = params.inverseVertical,
                inverseHorizontal = params.inverseHorizontal
            )
        }.getOrNull() ?: image
    }

    private fun PivotPair?.toCutBounds(
        size: Int
    ): Pair<Int?, Int?> {
        val bounds = this
            ?.takeIf { it != PivotPair(0f, 1f) }
            ?.let {
                (it.startRtlAdjusted * size).roundToInt() to
                        (it.endRtlAdjusted * size).roundToInt()
            }
            ?: return null to null

        val (start, end) = bounds

        return if (start in 0..size && end in 0..size && start < end) {
            start to end
        } else {
            null to null
        }
    }

    private fun Bitmap.cutAndMerge(
        verticalStart: Int?,
        verticalEnd: Int?,
        horizontalStart: Int?,
        horizontalEnd: Int?,
        inverseVertical: Boolean,
        inverseHorizontal: Boolean
    ): Bitmap {
        val columns = cutRanges(width, verticalStart, verticalEnd, inverseVertical)
        val rows = cutRanges(height, horizontalStart, horizontalEnd, inverseHorizontal)
        val outputWidth = columns.sumOf { (start, end) -> end - start }
        val outputHeight = rows.sumOf { (start, end) -> end - start }
        if (outputWidth == width && outputHeight == height) return this

        val source = this
        val sourceRect = Rect()
        val targetRect = Rect()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        return createBitmap(outputWidth, outputHeight, safeConfig).applyCanvas {
            var y = 0
            for ((top, bottom) in rows) {
                var x = 0
                for ((left, right) in columns) {
                    sourceRect.set(left, top, right, bottom)
                    targetRect.set(x, y, x + right - left, y + bottom - top)
                    drawBitmap(source, sourceRect, targetRect, paint)
                    x += right - left
                }
                y += bottom - top
            }
        }
    }

    private fun cutRanges(
        size: Int,
        start: Int?,
        end: Int?,
        inverse: Boolean
    ): List<Pair<Int, Int>> = when {
        start == null || end == null -> listOf(0 to size)
        inverse -> listOf(start to end)
        else -> buildList {
            if (start > 0) add(0 to start)
            if (end < size) add(end to size)
        }
    }
}