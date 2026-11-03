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

package com.t8rin.imagetoolbox.feature.filters.data.model

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter.TornEdge.Value
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterInfo
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterDefinition
import com.t8rin.imagetoolbox.core.ksp.annotations.FilterGroups
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.feature.filters.data.transformation.FilterTransformation
import java.util.Random
import kotlin.math.ceil
import kotlin.math.min

@FilterDefinition(group = FilterGroups.FRAMES)
internal class TornEdgeFilter(
    override val value: Value
) : FilterTransformation, Filter.TornEdge {

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap {
        val paths = createTornPaths(input.width, input.height, value)

        return createBitmap(
            width = input.width,
            height = input.height
        ).apply {
            setHasAlpha(true)
        }.applyCanvas {
            val layer = saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            drawBitmap(
                input,
                0f,
                0f,
                Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    isFilterBitmap = true
                }
            )
            paths.forEach { path ->
                drawPath(
                    path,
                    Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                    }
                )
            }
            restoreToCount(layer)
        }
    }

    private fun createTornPaths(
        width: Int,
        height: Int,
        params: Value
    ): List<Path> {
        // Keep the seed independent of the generated Value implementation.
        val seed = with(params) {
            var hash = toothHeight
            hash = 31 * hash + horizontalToothRange
            hash = 31 * hash + verticalToothRange
            hash = 31 * hash + top.hashCode()
            hash = 31 * hash + right.hashCode()
            hash = 31 * hash + bottom.hashCode()
            31 * hash + left.hashCode()
        }
        val random = Random((31L * width + height) * 31L + seed)
        val toothHeight = params.toothHeight.coerceIn(1, min(width, height).coerceAtLeast(1))
        val horizontalToothRange = params.horizontalToothRange.coerceAtLeast(2)
        val verticalToothRange = params.verticalToothRange.coerceAtLeast(2)
        val horizontalRegions = ceil(width.toFloat() / horizontalToothRange)
            .toInt()
            .coerceAtLeast(2)
        val verticalRegions = ceil(height.toFloat() / verticalToothRange)
            .toInt()
            .coerceAtLeast(2)
        val paths = mutableListOf<Path>()

        if (params.top) {
            paths += Path().apply {
                moveTo(0f, 0f)
                lineTo(width.toFloat(), 0f)
                lineTo(width.toFloat(), random.tooth(toothHeight))

                for (i in horizontalRegions - 1 downTo 1) {
                    lineTo(
                        width * i / horizontalRegions.toFloat(),
                        random.tooth(toothHeight)
                    )
                }
                lineTo(
                    0f,
                    random.tooth(toothHeight)
                )

                close()
            }
        }

        if (params.right) {
            paths += Path().apply {
                moveTo(width.toFloat(), 0f)
                lineTo(width.toFloat(), height.toFloat())
                lineTo(
                    width - random.tooth(toothHeight),
                    height.toFloat()
                )

                for (i in verticalRegions - 1 downTo 1) {
                    lineTo(
                        width - random.tooth(toothHeight),
                        height * i / verticalRegions.toFloat()
                    )
                }
                lineTo(
                    width - random.tooth(toothHeight),
                    0f
                )

                close()
            }
        }

        if (params.bottom) {
            paths += Path().apply {
                moveTo(width.toFloat(), height.toFloat())
                lineTo(0f, height.toFloat())
                lineTo(
                    0f,
                    height - random.tooth(toothHeight)
                )

                for (i in 1 until horizontalRegions) {
                    lineTo(
                        width * i / horizontalRegions.toFloat(),
                        height - random.tooth(toothHeight)
                    )
                }
                lineTo(
                    width.toFloat(),
                    height - random.tooth(toothHeight)
                )

                close()
            }
        }

        if (params.left) {
            paths += Path().apply {
                moveTo(0f, height.toFloat())
                lineTo(0f, 0f)
                lineTo(random.tooth(toothHeight), 0f)

                for (i in 1 until verticalRegions) {
                    lineTo(
                        random.tooth(toothHeight),
                        height * i / verticalRegions.toFloat()
                    )
                }
                lineTo(
                    random.tooth(toothHeight),
                    height.toFloat()
                )

                close()
            }
        }

        return paths
    }

    private fun Random.tooth(
        toothHeight: Int
    ): Float = (nextInt(toothHeight) + 1).toFloat()

    companion object : Filter.Schema.Structured<Value> {
        override val info = FilterInfo(R.string.torn_edge, R.string.filter_help_torn_edge)

        override val parameters = structuredParameters {
            Value::toothHeight.int(R.string.tooth_height, 16, 1..96)
            Value::horizontalToothRange.int(R.string.horizontal_tooth_range, 22, 2..128)
            Value::verticalToothRange.int(R.string.vertical_tooth_range, 22, 2..128)
            Value::top.boolean(R.string.top_edge, true)
            Value::right.boolean(R.string.right_edge, true)
            Value::bottom.boolean(R.string.bottom_edge, true)
            Value::left.boolean(R.string.left_edge, true)
        }
    }
}