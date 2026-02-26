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


@file:SuppressLint("ComposableNaming")
@file:Suppress("FunctionName")

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import android.annotation.SuppressLint
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.capsule.Continuity
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import sv.lib.squircleshape.CornerSmoothing
import sv.lib.squircleshape.SquircleShape

private val continuity = Continuity.Default
private val smoothing = CornerSmoothing.Medium

@Stable
fun AutoCornersShape(
    corner: CornerSize,
    shapesType: ShapeType
) = AutoCornersShape(
    topStart = corner,
    topEnd = corner,
    bottomEnd = corner,
    bottomStart = corner,
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    size: Dp,
    shapesType: ShapeType
) = AutoCornersShape(
    corner = CornerSize(size),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    size: Float,
    shapesType: ShapeType
) = AutoCornersShape(
    corner = CornerSize(size),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    percent: Int,
    shapesType: ShapeType
) = AutoCornersShape(
    corner = CornerSize(percent),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    shapesType: ShapeType
) = AutoCornersShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    topStart: Float = 0.0f,
    topEnd: Float = 0.0f,
    bottomEnd: Float = 0.0f,
    bottomStart: Float = 0.0f,
    shapesType: ShapeType
) = AutoCornersShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    topStartPercent: Int = 0,
    topEndPercent: Int = 0,
    bottomEndPercent: Int = 0,
    bottomStartPercent: Int = 0,
    shapesType: ShapeType
) = AutoCornersShape(
    topStart = CornerSize(topStartPercent),
    topEnd = CornerSize(topEndPercent),
    bottomEnd = CornerSize(bottomEndPercent),
    bottomStart = CornerSize(bottomStartPercent),
    shapesType = shapesType
)

@Stable
fun AutoCornersShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    shapesType: ShapeType
) = when (shapesType) {
    is ShapeType.Cut -> CutCornerShape(
        topStart = topStart.toAuto(shapesType),
        topEnd = topEnd.toAuto(shapesType),
        bottomEnd = bottomEnd.toAuto(shapesType),
        bottomStart = bottomStart.toAuto(shapesType),
    )

    is ShapeType.Rounded -> RoundedCornerShape(
        topStart = topStart.toAuto(shapesType),
        topEnd = topEnd.toAuto(shapesType),
        bottomEnd = bottomEnd.toAuto(shapesType),
        bottomStart = bottomStart.toAuto(shapesType),
    )

    is ShapeType.Smooth -> ContinuousRoundedRectangle(
        topStart = topStart.toAuto(shapesType),
        topEnd = topEnd.toAuto(shapesType),
        bottomEnd = bottomEnd.toAuto(shapesType),
        bottomStart = bottomStart.toAuto(shapesType),
        continuity = continuity
    )

    is ShapeType.Squircle -> SquircleShape(
        topStartCorner = topStart.toAuto(shapesType),
        topEndCorner = topEnd.toAuto(shapesType),
        bottomEndCorner = bottomEnd.toAuto(shapesType),
        bottomStartCorner = bottomStart.toAuto(shapesType),
        smoothing = smoothing
    )
}

@Stable
fun AutoCircleShape(shapesType: ShapeType) = when (shapesType) {
    is ShapeType.Cut -> CutCircleShape
    is ShapeType.Rounded -> CircleShape
    is ShapeType.Smooth -> SmoothCircleShape
    is ShapeType.Squircle -> SquircleCircleShape
}.let { shape ->
    if (shapesType.strength >= 1f) {
        shape
    } else {
        shape.copy(shape.topStart.toAuto(shapesType))
    }
}

@Stable
@Composable
fun AutoCornersShape(size: Dp) = rememberSettings(size) { shapesType ->
    AutoCornersShape(
        size = size,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(size: Float) = rememberSettings(size) { shapesType ->
    AutoCornersShape(
        size = size,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(percent: Int) = rememberSettings(percent) { shapesType ->
    AutoCornersShape(
        percent = percent,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
) = rememberSettings(topStart, topEnd, bottomEnd, bottomStart) { shapesType ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(
    topStart: Float = 0.0f,
    topEnd: Float = 0.0f,
    bottomEnd: Float = 0.0f,
    bottomStart: Float = 0.0f,
) = rememberSettings(topStart, topEnd, bottomEnd, bottomStart) { shapesType ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(
    topStartPercent: Int = 0,
    topEndPercent: Int = 0,
    bottomEndPercent: Int = 0,
    bottomStartPercent: Int = 0,
) = rememberSettings(
    topStartPercent,
    topEndPercent,
    bottomEndPercent,
    bottomStartPercent
) { shapesType ->
    AutoCornersShape(
        topStartPercent = topStartPercent,
        topEndPercent = topEndPercent,
        bottomEndPercent = bottomEndPercent,
        bottomStartPercent = bottomStartPercent,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCornersShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
) = rememberSettings(topStart, topEnd, bottomEnd, bottomStart) { shapesType ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        shapesType = shapesType
    )
}

@Stable
@Composable
fun AutoCircleShape() = rememberSettings { shapesType ->
    AutoCircleShape(shapesType)
}

@Stable
val SmoothCircleShape = ContinuousCapsule(continuity)

@Stable
val CutCircleShape = CutCornerShape(50)

@Stable
val SquircleCircleShape = SquircleShape(
    percent = 50,
    smoothing = smoothing
)

@Stable
@Composable
private fun rememberSettings(
    vararg keys: Any?,
    calculation: (type: ShapeType) -> CornerBasedShape
): CornerBasedShape {
    val shapesType = LocalSettingsState.current.shapesType

    return remember(*keys, shapesType) {
        calculation(shapesType)
    }
}

@Stable
private fun CornerSize.toAuto(shapeType: ShapeType) = toAuto(shapeType.strength)

@Stable
private fun CornerSize.toAuto(strength: Float) =
    if (strength == 1f) {
        this
    } else if (this is AutoCornerSize) {
        copy(
            strength = strength
        )
    } else {
        AutoCornerSize(
            parent = this,
            strength = strength
        )
    }

@Stable
@Immutable
private data class AutoCornerSize(
    private val parent: CornerSize,
    private val strength: Float
) : CornerSize {

    override fun toPx(shapeSize: Size, density: Density): Float =
        (parent.toPx(shapeSize, density) * strength).coerceAtLeast(0f)

}