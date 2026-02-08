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
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.image_cutting.domain.CutParams
import com.t8rin.imagetoolbox.image_cutting.domain.ImageCutter
import com.t8rin.imagetoolbox.image_cutting.domain.PivotPair
import kotlinx.coroutines.coroutineScope
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
            val verticalStart = params.vertical.takeIf { it != PivotPair(0f, 1f) }
                ?.let { (it.startRtlAdjusted * image.width).roundToInt() }
            val verticalEnd = params.vertical.takeIf { it != PivotPair(0f, 1f) }
                ?.let { (it.endRtlAdjusted * image.width).roundToInt() }
            val horizontalStart = params.horizontal.takeIf { it != PivotPair(0f, 1f) }
                ?.let { (it.startRtlAdjusted * image.height).roundToInt() }
            val horizontalEnd = params.horizontal.takeIf { it != PivotPair(0f, 1f) }
                ?.let { (it.endRtlAdjusted * image.height).roundToInt() }

            require(
                (verticalStart == null || verticalStart in 0..image.width) &&
                        (verticalEnd == null || verticalEnd in 0..image.width) &&
                        (horizontalStart == null || horizontalStart in 0..image.height) &&
                        (horizontalEnd == null || horizontalEnd in 0..image.height) &&
                        (verticalStart == null || verticalEnd == null || verticalStart < verticalEnd) &&
                        (horizontalStart == null || horizontalEnd == null || horizontalStart < horizontalEnd)
            ) { "Invalid cut range" }

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

    private suspend fun Bitmap.cutAndMerge(
        verticalStart: Int? = null,
        verticalEnd: Int? = null,
        horizontalStart: Int? = null,
        horizontalEnd: Int? = null,
        inverseVertical: Boolean = false,
        inverseHorizontal: Boolean = false
    ): Bitmap = coroutineScope {
        if (inverseVertical && inverseHorizontal) {
            Bitmap.createBitmap(
                this@cutAndMerge,
                verticalStart ?: 0,
                horizontalStart ?: 0,
                (verticalEnd ?: width) - (verticalStart ?: 0),
                (horizontalEnd ?: height) - (horizontalStart ?: 0)
            )
        } else {
            cutVertically(
                start = verticalStart,
                end = verticalEnd,
                inverse = inverseVertical
            ).cutHorizontally(
                start = horizontalStart,
                end = horizontalEnd,
                inverse = inverseHorizontal
            )
        }
    }

    private suspend fun Bitmap.cutHorizontally(
        start: Int?,
        end: Int?,
        inverse: Boolean
    ): Bitmap = coroutineScope {
        val source = this@cutHorizontally

        if (inverse) {
            if (start != null && end != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    0,
                    start,
                    source.width,
                    end - start
                )
            } else if (start == null && end != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    0,
                    0,
                    source.width,
                    end
                )
            } else if (start != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    0,
                    start,
                    source.width,
                    source.height - start
                )
            }
        }


        val parts = mutableListOf<Bitmap>()
        if (start != null || end != null) {
            if (start != null && start > 0) {
                parts.add(
                    Bitmap.createBitmap(
                        source,
                        0,
                        0,
                        source.width,
                        start
                    )
                )
            }
            if (end != null && end < source.height) {
                parts.add(
                    Bitmap.createBitmap(
                        source,
                        0,
                        end,
                        source.width,
                        source.height - end
                    )
                )
            }
        } else {
            parts.add(source.copy(source.safeConfig, true))
        }

        val mergedWidth = parts.maxOf { it.width }
        val mergedHeight = parts.sumOf { it.height }

        createBitmap(mergedWidth, mergedHeight, source.safeConfig)
            .applyCanvas {
                var offsetY = 0f
                for (part in parts) {
                    drawBitmap(part, 0f, offsetY)
                    offsetY += part.height
                    part.recycle()
                }
            }
    }

    private suspend fun Bitmap.cutVertically(
        start: Int?,
        end: Int?,
        inverse: Boolean
    ): Bitmap = coroutineScope {
        val source = this@cutVertically

        if (inverse) {
            if (start != null && end != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    start,
                    0,
                    end - start,
                    source.height
                )
            } else if (start == null && end != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    0,
                    0,
                    end,
                    source.height
                )
            } else if (start != null) {
                return@coroutineScope Bitmap.createBitmap(
                    source,
                    start,
                    0,
                    source.width - start,
                    source.height
                )
            }
        }

        val parts = mutableListOf<Bitmap>()
        if (start != null || end != null) {
            if (start != null && start > 0) {
                parts.add(
                    Bitmap.createBitmap(
                        source,
                        0,
                        0,
                        start,
                        source.height
                    )
                )
            }
            if (end != null && end < source.width) {
                parts.add(
                    Bitmap.createBitmap(
                        source,
                        end,
                        0,
                        source.width - end,
                        source.height
                    )
                )
            }
        } else {
            parts.add(source.copy(source.safeConfig, true))
        }

        val mergedWidth = parts.sumOf { it.width }
        val mergedHeight = parts.maxOf { it.height }

        createBitmap(mergedWidth, mergedHeight, source.safeConfig)
            .applyCanvas {
                var offsetX = 0f
                for (part in parts) {
                    drawBitmap(part, offsetX, 0f)
                    offsetX += part.width
                    part.recycle()
                }
            }
    }

}