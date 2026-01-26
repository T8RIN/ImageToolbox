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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.capsule.Continuity
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState

fun AutoCornersShape(
    corner: CornerSize,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    topStart = corner,
    topEnd = corner,
    bottomEnd = corner,
    bottomStart = corner,
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    size: Dp,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    corner = CornerSize(size),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    size: Float,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    corner = CornerSize(size),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    percent: Int,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    corner = CornerSize(percent),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    topStart: Float = 0.0f,
    topEnd: Float = 0.0f,
    bottomEnd: Float = 0.0f,
    bottomStart: Float = 0.0f,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    topStart = CornerSize(topStart),
    topEnd = CornerSize(topEnd),
    bottomEnd = CornerSize(bottomEnd),
    bottomStart = CornerSize(bottomStart),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    topStartPercent: Int = 0,
    topEndPercent: Int = 0,
    bottomEndPercent: Int = 0,
    bottomStartPercent: Int = 0,
    isSmoothShapes: Boolean
) = AutoCornersShape(
    topStart = CornerSize(topStartPercent),
    topEnd = CornerSize(topEndPercent),
    bottomEnd = CornerSize(bottomEndPercent),
    bottomStart = CornerSize(bottomStartPercent),
    isSmoothShapes = isSmoothShapes
)

fun AutoCornersShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
    isSmoothShapes: Boolean
) = if (isSmoothShapes) {
    ContinuousRoundedRectangle(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        continuity = Continuity.Default
    )
} else {
    RoundedCornerShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart
    )
}

fun AutoCircleShape(isSmoothShapes: Boolean) = if (isSmoothShapes) {
    ContinuousCapsule
} else {
    CircleShape
}

@Composable
fun AutoCornersShape(size: Dp) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        size = size,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(size: Float) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        size = size,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(percent: Int) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        percent = percent,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(
    topStart: Dp = 0.dp,
    topEnd: Dp = 0.dp,
    bottomEnd: Dp = 0.dp,
    bottomStart: Dp = 0.dp,
) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(
    topStart: Float = 0.0f,
    topEnd: Float = 0.0f,
    bottomEnd: Float = 0.0f,
    bottomStart: Float = 0.0f,
) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(
    topStartPercent: Int = 0,
    topEndPercent: Int = 0,
    bottomEndPercent: Int = 0,
    bottomStartPercent: Int = 0,
) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        topStartPercent = topStartPercent,
        topEndPercent = topEndPercent,
        bottomEndPercent = bottomEndPercent,
        bottomStartPercent = bottomStartPercent,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCornersShape(
    topStart: CornerSize,
    topEnd: CornerSize,
    bottomEnd: CornerSize,
    bottomStart: CornerSize,
) = rememberSettings { isSmoothShapes ->
    AutoCornersShape(
        topStart = topStart,
        topEnd = topEnd,
        bottomEnd = bottomEnd,
        bottomStart = bottomStart,
        isSmoothShapes = isSmoothShapes
    )
}

@Composable
fun AutoCircleShape() = rememberSettings { isSmoothShapes ->
    AutoCircleShape(isSmoothShapes)
}

@Composable
private fun rememberSettings(
    calculation: (isSmoothShapes: Boolean) -> CornerBasedShape
): CornerBasedShape {
    val isSmoothShapes = LocalSettingsState.current.isSmoothShapes

    return remember(isSmoothShapes) {
        calculation(isSmoothShapes)
    }
}