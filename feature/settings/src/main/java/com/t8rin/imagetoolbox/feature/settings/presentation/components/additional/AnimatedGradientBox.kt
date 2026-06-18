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

package com.t8rin.imagetoolbox.feature.settings.presentation.components.additional

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.animation.AlphaEasing
import com.t8rin.imagetoolbox.core.ui.utils.animation.SoftEasing
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalContainerShape
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import kotlinx.coroutines.isActive
import kotlin.math.hypot
import kotlin.random.Random

@Composable
internal fun AnimatedGradientBox(
    colors: ColorScheme.() -> List<Color>,
    content: @Composable () -> Unit = {}
) {
    var size by remember { mutableStateOf(Size.Zero) }

    val progress = remember { Animatable(1f) }

    var from by remember { mutableStateOf(Offset.Zero) }
    var to by remember { mutableStateOf(Offset.Zero) }
    var control by remember { mutableStateOf(Offset.Zero) }
    val motionDurationScale = LocalSettingsState.current.motionDurationScale

    LaunchedEffect(size, motionDurationScale) {
        if (size == Size.Zero || motionDurationScale <= 0f) return@LaunchedEffect

        from = Offset(size.width / 2f, size.height / 2f)
        to = randomOffset(size, from)
        control = arcControlPoint(from, to, size)

        while (isActive) {
            progress.snapTo(0f)

            val distance = (to - from).getDistance()

            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = distance
                        .coerceIn(500f, 1800f)
                        .toInt() + 1200,
                    easing = SoftEasing
                )
            )

            from = to
            to = randomOffset(size, from)
            control = arcControlPoint(from, to, size)
        }
    }

    val transition = rememberInfiniteTransition(label = "gradient-radius")

    val radius by transition.animateFloat(
        initialValue = 400f,
        targetValue = 700f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 3600,
                easing = AlphaEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radius"
    )

    val center = quadraticBezier(
        from = from,
        control = control,
        to = to,
        t = progress.value
    )

    Box(
        modifier = Modifier.onSizeChanged {
            size = Size(
                width = it.width.toFloat(),
                height = it.height.toFloat()
            )
        }
    ) {
        val colorScheme = MaterialTheme.colorScheme

        ProvideContainerDefaults(
            brush = Brush.radialGradient(
                colors = remember(colorScheme) {
                    colors(colorScheme)
                },
                center = center,
                radius = radius
            ),
            shape = LocalContainerShape.current,
            content = content
        )
    }
}

private fun randomOffset(
    size: Size,
    from: Offset,
    minDistanceFactor: Float = 0.45f
): Offset {
    val paddingX = size.width * 0.15f
    val paddingY = size.height * 0.15f

    val minDistance = minOf(size.width, size.height) * minDistanceFactor

    repeat(12) {
        val offset = Offset(
            x = Random.nextFloat() * (size.width + paddingX * 2f) - paddingX,
            y = Random.nextFloat() * (size.height + paddingY * 2f) - paddingY
        )

        if ((offset - from).getDistance() >= minDistance) {
            return offset
        }
    }

    return Offset(
        x = size.width - from.x + Random.nextFloat() * paddingX - paddingX / 2f,
        y = size.height - from.y + Random.nextFloat() * paddingY - paddingY / 2f
    )
}

private fun arcControlPoint(
    from: Offset,
    to: Offset,
    size: Size
): Offset {
    val mid = Offset(
        x = (from.x + to.x) / 2f,
        y = (from.y + to.y) / 2f
    )

    val dx = to.x - from.x
    val dy = to.y - from.y

    val distance = hypot(dx, dy).coerceAtLeast(1f)
    val maxArcPower = minOf(size.width, size.height) * 0.45f
    val arcPower = (distance * Random.nextFloatIn(0.35f, 0.75f))
        .coerceAtMost(maxArcPower)

    val direction = if (Random.nextBoolean()) 1f else -1f

    val normalX = -dy / distance
    val normalY = dx / distance

    return Offset(
        x = mid.x + normalX * arcPower * direction,
        y = mid.y + normalY * arcPower * direction
    )
}

private fun Random.nextFloatIn(
    from: Float,
    to: Float
): Float {
    return from + nextFloat() * (to - from)
}

private fun quadraticBezier(
    from: Offset,
    control: Offset,
    to: Offset,
    t: Float
): Offset {
    val oneMinusT = 1f - t

    return Offset(
        x = oneMinusT * oneMinusT * from.x +
                2f * oneMinusT * t * control.x +
                t * t * to.x,
        y = oneMinusT * oneMinusT * from.y +
                2f * oneMinusT * t * control.y +
                t * t * to.y
    )
}