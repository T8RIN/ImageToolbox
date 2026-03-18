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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.t8rin.snowfall.Constants.angleRange
import com.t8rin.snowfall.Constants.angleSeed
import com.t8rin.snowfall.Constants.incrementRange
import com.t8rin.snowfall.Constants.sizeRange
import com.t8rin.snowfall.types.AnimType
import kotlin.math.PI
import kotlin.math.roundToInt

internal data class SnowAnimState(
    var tickNanos: Long,
    val snowflakes: List<Snowflake>,
    val painters: List<Painter>,
    val animType: AnimType,
    val colors: List<Color>,
    val density: Double,
    val alpha: Float,
) {
    constructor(
        tick: Long,
        canvasSize: IntSize,
        painters: List<Painter>,
        animType: AnimType,
        colors: List<Color>,
        density: Double,
        alpha: Float
    ) : this(
        tickNanos = tick,
        snowflakes = createSnowFlakes(
            flakesProvider = painters,
            canvasSize = canvasSize,
            animType = animType,
            colors = colors,
            density = density,
            alpha = alpha
        ),
        painters = painters,
        animType = animType,
        colors = colors,
        density = density,
        alpha = alpha
    )

    fun draw(contentDrawScope: ContentDrawScope) {
        snowflakes.forEach {
            it.draw(contentDrawScope)
        }
    }

    fun resize(newSize: IntSize) = copy(
        snowflakes = createSnowFlakes(
            flakesProvider = painters,
            canvasSize = newSize,
            animType = animType,
            colors = colors,
            density = density,
            alpha = alpha
        )
    )

    companion object {

        fun createSnowFlakes(
            flakesProvider: List<Painter>,
            canvasSize: IntSize,
            animType: AnimType,
            colors: List<Color>,
            density: Double,
            alpha: Float,
        ): List<Snowflake> =
            when (animType) {
                AnimType.Falling -> createFallingSnowflakes(
                    canvasSize = canvasSize,
                    painters = flakesProvider,
                    colors = colors,
                    snowflakeDensity = density,
                    alpha = alpha
                )

                AnimType.Melting -> createMeltingSnowflakes(
                    canvasSize = canvasSize,
                    painters = flakesProvider,
                    colors = colors,
                    snowflakeDensity = density,
                )
            }

        private fun createMeltingSnowflakes(
            canvasSize: IntSize,
            painters: List<Painter>,
            colors: List<Color>,
            snowflakeDensity: Double,
        ): List<MeltingSnowflake> {
            if (canvasSize.height == 0 || canvasSize.width == 0 || snowflakeDensity == 0.0) {
                return emptyList()
            }

            val canvasArea = canvasSize.width * canvasSize.height
            val normalizedDensity = snowflakeDensity.coerceIn(0.0..1.0) / 2000.0
            val count = (canvasArea * normalizedDensity).roundToInt()
            val snowflakesCount = count.coerceIn(painters.size.coerceAtMost(count), count)

            return List(snowflakesCount) {
                MeltingSnowflake(
                    incrementFactor = incrementRange.random(),
                    canvasSize = canvasSize,
                    maxAlpha = (0.1f..0.7f).random(),
                    painter = painters[it % painters.size],
                    initialPosition = canvasSize.randomPosition(),
                    color = colors.random(),
                )
            }
        }

        private fun createFallingSnowflakes(
            canvasSize: IntSize,
            painters: List<Painter>,
            colors: List<Color>,
            snowflakeDensity: Double,
            alpha: Float,
        ): List<FallingSnowflake> {
            val canvasArea = canvasSize.width * canvasSize.height
            val normalizedDensity = snowflakeDensity.coerceIn(0.0..1.0) / 1000.0
            val snowflakesCount = (canvasArea * normalizedDensity).roundToInt()

            return List(snowflakesCount) {
                FallingSnowflake(
                    incrementFactor = incrementRange.random(),
                    size = sizeRange.random(),
                    canvasSize = canvasSize,
                    initialPosition = canvasSize.randomPosition(),
                    angle = angleSeed.random() / angleSeed * angleRange + (PI / 2.0) - (angleRange / 2.0),
                    painter = painters[it % painters.size],
                    color = colors.random(),
                    alpha = alpha,
                )
            }
        }
    }
}