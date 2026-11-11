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

package com.t8rin.imagetoolbox.core.games.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.t8rin.imagetoolbox.core.games.ArcadeSprite
import com.t8rin.imagetoolbox.core.games.ArcadeStyle
import com.t8rin.imagetoolbox.core.games.drawArcadeSprite
import com.t8rin.imagetoolbox.core.games.pixelRect
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

internal class ArcadeEffects {
    private class Spark(
        var point: Offset,
        val velocity: Offset,
        var life: Float = 1f
    )

    private val sparks = ArrayList<Spark>(48)

    fun burst(
        point: Offset,
        count: Int = 8
    ) {
        repeat(count) { index ->
            if (sparks.size >= 48) sparks.removeAt(0)
            val angle = index * 2f * PI.toFloat() / count
            sparks += Spark(
                point = point,
                velocity = Offset(x = cos(angle), y = sin(angle)) * (32f + index % 3 * 13f)
            )
        }
    }

    fun advance(seconds: Float) {
        for (spark in sparks) {
            spark.point += spark.velocity * seconds
            spark.life -= seconds * 2.5f
        }
        sparks.removeAll { it.life <= 0f }
    }

    fun clear() = sparks.clear()

    fun draw(
        scope: DrawScope,
        color: Color
    ) = with(scope) {
        for (spark in sparks) {
            val size = (3f * spark.life).coerceAtLeast(1f)
            pixelRect(
                color = color.copy(alpha = spark.life.coerceIn(0f, 1f)),
                x = spark.point.x - size / 2f,
                y = spark.point.y - size / 2f,
                width = size,
                height = size
            )
        }
    }
}

private val ArcadeHeart = ArcadeSprite(
    width = 11, height = 10,
    data = "M1 0H4V1H5V2H6V1H7V0H10V1H11V5H10V6H9V7H8V8H7V9H6V10H5V9H4V8H3V7H2V6H1V5H0V1H1Z"
)

internal fun DrawScope.arcadeJoystick(
    style: ArcadeStyle,
    origin: Offset?,
    stick: Offset
) {
    if (origin == null || stick.getDistanceSquared() <= 64f) return
    drawCircle(
        color = style.detailColor.copy(alpha = 0.35f),
        radius = 23f,
        center = origin,
        style = Stroke(width = 1f)
    )
    val tint = style.playerColor.copy(alpha = 0.5f)
    drawLine(tint, origin, origin + stick, strokeWidth = 2f)
    drawCircle(tint, radius = 4f, center = origin + stick)
}

internal fun DrawScope.arcadeLives(
    style: ArcadeStyle,
    remaining: Int,
    y: Float
) {
    repeat(3) { index ->
        drawArcadeSprite(
            sprite = ArcadeHeart,
            tint = if (index < remaining) style.accentColor else style.detailColor.copy(alpha = 0.3f),
            x = 14f + index * 16f,
            y = y - 5f
        )
    }
}

internal fun arcadeAngle(angle: Float): Float = (angle % 360f + 360f) % 360f

internal fun arcadeAngleDistance(
    first: Float,
    second: Float
): Float =
    abs(arcadeAngle(first - second + 180f) - 180f)