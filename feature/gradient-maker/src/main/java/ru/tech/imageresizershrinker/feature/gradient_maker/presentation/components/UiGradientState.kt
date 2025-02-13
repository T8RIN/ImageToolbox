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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.smarttoolfactory.colorpicker.ui.Yellow400
import com.smarttoolfactory.extendedcolors.MaterialColor.Indigo700
import com.smarttoolfactory.extendedcolors.MaterialColor.Pink500
import com.smarttoolfactory.extendedcolors.MaterialColor.Teal900
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientState
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.GradientType
import ru.tech.imageresizershrinker.feature.gradient_maker.domain.MeshGradientState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun rememberGradientState(
    size: DpSize = DpSize.Zero
): UiGradientState {

    val density = LocalDensity.current

    return remember {
        val sizePx = if (size == DpSize.Zero) {
            Size.Zero
        } else {
            with(density) {
                Size(
                    size.width.toPx(),
                    size.height.toPx()
                )
            }
        }
        UiGradientState(sizePx)
    }
}

class UiGradientState(
    size: Size = Size.Zero
) : GradientState<ShaderBrush, Size, Color, TileMode, Offset> {

    override var size by mutableStateOf(size)

    override val brush: ShaderBrush?
        get() = getBrush(size)

    override fun getBrush(size: Size): ShaderBrush? {
        if (colorStops.isEmpty()) return null

        val colorStops = colorStops.sortedBy { it.first }.let { pairs ->
            if (gradientType != GradientType.Sweep) {
                pairs.distinctBy { it.first }
            } else pairs
        }.let {
            if (it.size == 1) {
                listOf(it.first(), it.first())
            } else it
        }.toTypedArray()

        val brush = runCatching {
            when (gradientType) {
                GradientType.Linear -> {
                    val angleRad = linearGradientAngle / 180f * PI
                    val x = cos(angleRad).toFloat()
                    val y = sin(angleRad).toFloat()

                    val radius = sqrt(size.width.pow(2) + size.height.pow(2)) / 2f
                    val offset = size.center + Offset(x * radius, y * radius)

                    val exactOffset = Offset(
                        x = min(offset.x.coerceAtLeast(0f), size.width),
                        y = size.height - min(offset.y.coerceAtLeast(0f), size.height)
                    )
                    Brush.linearGradient(
                        colorStops = colorStops,
                        start = Offset(size.width, size.height) - exactOffset,
                        end = exactOffset,
                        tileMode = tileMode
                    )
                }

                GradientType.Radial -> Brush.radialGradient(
                    colorStops = colorStops,
                    center = Offset(
                        x = size.width * centerFriction.x,
                        y = size.height * centerFriction.y
                    ),
                    radius = ((size.width.coerceAtLeast(size.height)) / 2 * radiusFriction)
                        .coerceAtLeast(0.01f),
                    tileMode = tileMode
                )

                GradientType.Sweep -> Brush.sweepGradient(
                    colorStops = colorStops,
                    center = Offset(
                        x = size.width * centerFriction.x,
                        y = size.height * centerFriction.y
                    )
                )
            } as ShaderBrush
        }.getOrNull()

        return brush
    }

    override var gradientType: GradientType by mutableStateOf(GradientType.Linear)

    override val colorStops = mutableStateListOf<Pair<Float, Color>>()

    override var tileMode by mutableStateOf(TileMode.Clamp)

    override var linearGradientAngle by mutableFloatStateOf(0f)

    override var centerFriction by mutableStateOf(Offset(.5f, .5f))
    override var radiusFriction by mutableFloatStateOf(.5f)
}

class UiMeshGradientState : MeshGradientState<Color, Offset> {

    override val points = mutableStateListOf(
        listOf(
            Offset(0f, 0f) to Teal900,
            Offset(.5f, 0f) to Teal900,
            Offset(1f, 0f) to Teal900,
        ),
        listOf(
            Offset(0f, .5f) to Indigo700,
            Offset(.5f, .9f) to Yellow400,
            Offset(1f, .5f) to Indigo700,
        ),
        listOf(
            Offset(0f, 1f) to Pink500,
            Offset(.5f, 1f) to Pink500,
            Offset(1f, 1f) to Pink500,
        )
    )

    override var resolutionX: Int by mutableIntStateOf(32)

    override var resolutionY: Int by mutableIntStateOf(32)

}