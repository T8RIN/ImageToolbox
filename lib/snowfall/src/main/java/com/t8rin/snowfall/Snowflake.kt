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

package com.t8rin.snowfall

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.t8rin.snowfall.Constants.angleDivisor
import com.t8rin.snowfall.Constants.angleSeedRange
import com.t8rin.snowfall.Constants.baseFrameDurationMillis
import kotlin.math.cos
import kotlin.math.sin

internal interface Snowflake {
    fun update(elapsedMillis: Long)
    fun draw(contentDrawScope: ContentDrawScope)
}

internal class MeltingSnowflake(
    private val initialPosition: Offset,
    private val incrementFactor: Float,
    private val canvasSize: IntSize,
    private val maxAlpha: Float,
    private val painter: Painter,
    private val color: Color,
) : Snowflake {

    init {
        require(maxAlpha in 0.1..1.0)
    }

    private var position by mutableStateOf(initialPosition)
    private var alpha by mutableFloatStateOf(0.001f)
    private var isIncreasing by mutableStateOf(true)

    override fun update(elapsedMillis: Long) {
        val increment = incrementFactor * (elapsedMillis / baseFrameDurationMillis) / 100f
        alpha = (if (isIncreasing) {
            alpha + increment
        } else {
            alpha - increment
        }).coerceIn(0f, maxAlpha)

        if (alpha == maxAlpha) {
            isIncreasing = false
        }

        if (alpha == 0f) {
            isIncreasing = true
            alpha = 0.001f
            position = canvasSize.randomPosition()
        }
    }

    override fun draw(contentDrawScope: ContentDrawScope) {
        with(contentDrawScope) {
            translate(
                left = position.x,
                top = position.y
            ) {
                with(painter) {
                    draw(
                        size = intrinsicSize,
                        alpha = alpha,
                        colorFilter = if (color == Color.Unspecified) null else ColorFilter.tint(
                            color
                        )
                    )
                }
            }
        }
    }
}

internal class FallingSnowflake(
    private val incrementFactor: Float,
    private val size: Float,
    private val canvasSize: IntSize,
    initialPosition: Offset,
    angle: Double,
    private val painter: Painter,
    private val color: Color,
    private val alpha: Float,
) : Snowflake {
    private val baseSpeedPxAt60Fps = 5
    private var position by mutableStateOf(initialPosition)
    private var angle by mutableDoubleStateOf(angle)

    override fun update(elapsedMillis: Long) {
        val increment =
            incrementFactor * (elapsedMillis / baseFrameDurationMillis) * baseSpeedPxAt60Fps
        val xDelta = (increment * cos(angle)).toFloat()
        val yDelta = (increment * sin(angle)).toFloat()
        position = Offset(position.x + xDelta, position.y + yDelta)
        angle += angleSeedRange.random() / angleDivisor

        if (position.y > canvasSize.height + size) {
            position =
                Offset(canvasSize.width.random().toFloat(), -size - painter.intrinsicSize.height)
        }
    }

    override fun draw(contentDrawScope: ContentDrawScope) {
        with(contentDrawScope) {
            translate(
                position.x,
                position.y
            ) {
                with(painter) {
                    draw(
                        size = intrinsicSize,
                        alpha = alpha,
                        colorFilter = if (color == Color.Unspecified) null else ColorFilter.tint(
                            color
                        )
                    )
                }
            }
        }
    }
}