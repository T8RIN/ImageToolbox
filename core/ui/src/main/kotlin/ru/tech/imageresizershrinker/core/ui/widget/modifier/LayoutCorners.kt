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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import android.content.Context
import android.os.Build
import android.view.RoundedCorner
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun Modifier.withLayoutCorners(
    block: Modifier.(LayoutCorners) -> Modifier
): Modifier = composed {
    val context = LocalContext.current
    val density = LocalDensity.current
    val screenInfo = remember(context) { context.getScreenInfo(density) }

    if (screenInfo != null) {
        val rootView = LocalView.current
        val layoutDirection = LocalLayoutDirection.current
        var positionOnScreen by remember { mutableStateOf<Rect?>(null) }
        val corners = getLayoutCorners(screenInfo, positionOnScreen, layoutDirection)

        onGloballyPositioned { cords ->
            positionOnScreen =
                getBoundsOnScreen(rootView = rootView, boundsInRoot = cords.boundsInRoot())
        }.block(corners)
    } else {
        block(LayoutCorners())
    }
}

private fun Context.getScreenInfo(density: Density): ScreenInfo? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
        return null
    }

    val windowMetrics =
        getSystemService(WindowManager::class.java)?.maximumWindowMetrics ?: return null
    val insets = windowMetrics.windowInsets

    return with(density) {
        ScreenInfo(
            cornerRadii = CornerRadii(
                topLeft = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius?.toDp(),
                topRight = insets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius?.toDp(),
                bottomRight = insets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_RIGHT)?.radius?.toDp(),
                bottomLeft = insets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius?.toDp(),
            ),
            width = windowMetrics.bounds.width(),
            height = windowMetrics.bounds.height(),
        )
    }
}

private fun getLayoutCorners(
    screenInfo: ScreenInfo,
    positionOnScreen: Rect?,
    layoutDirection: LayoutDirection,
): LayoutCorners {
    if (positionOnScreen == null) {
        return LayoutCorners()
    }

    val (cornerRadius, screenWidth, screenHeight) = screenInfo
    val (left, top, right, bottom) = positionOnScreen

    val topLeft = getLayoutCorner(
        radius = cornerRadius.topLeft,
        isFixed = (left <= 0) && (top <= 0)
    )
    val topRight = getLayoutCorner(
        radius = cornerRadius.topRight,
        isFixed = (right >= screenWidth) && (top <= 0)
    )
    val bottomRight = getLayoutCorner(
        radius = cornerRadius.bottomRight,
        isFixed = (right >= screenWidth) && (bottom >= screenHeight)
    )
    val bottomLeft = getLayoutCorner(
        radius = cornerRadius.bottomLeft,
        isFixed = (left <= 0) && (bottom >= screenHeight)
    )

    return when (layoutDirection) {
        LayoutDirection.Ltr -> LayoutCorners(
            topStart = topLeft,
            topEnd = topRight,
            bottomEnd = bottomRight,
            bottomStart = bottomLeft
        )

        LayoutDirection.Rtl -> LayoutCorners(
            topStart = topRight,
            topEnd = topLeft,
            bottomEnd = bottomLeft,
            bottomStart = bottomRight
        )
    }
}

private fun getLayoutCorner(
    radius: Dp?,
    isFixed: Boolean
): LayoutCorner =
    if (radius == null) {
        LayoutCorner()
    } else {
        LayoutCorner(
            radius = radius,
            isFixed = isFixed
        )
    }

private fun getBoundsOnScreen(
    rootView: View,
    boundsInRoot: Rect
): Rect {
    val rootViewLeftTopOnScreen = IntArray(2)
    rootView.getLocationOnScreen(rootViewLeftTopOnScreen)
    val (rootViewLeftOnScreen, rootViewTopOnScreen) = rootViewLeftTopOnScreen

    return Rect(
        left = rootViewLeftOnScreen + boundsInRoot.left,
        top = rootViewTopOnScreen + boundsInRoot.top,
        right = rootViewLeftOnScreen + boundsInRoot.right,
        bottom = rootViewTopOnScreen + boundsInRoot.bottom,
    )
}

private data class ScreenInfo(
    val cornerRadii: CornerRadii,
    val width: Int,
    val height: Int,
)

private data class CornerRadii(
    val topLeft: Dp? = null,
    val topRight: Dp? = null,
    val bottomRight: Dp? = null,
    val bottomLeft: Dp? = null,
)

data class LayoutCorners(
    val topStart: LayoutCorner = LayoutCorner(),
    val topEnd: LayoutCorner = LayoutCorner(),
    val bottomEnd: LayoutCorner = LayoutCorner(),
    val bottomStart: LayoutCorner = LayoutCorner(),
)

data class LayoutCorner(
    val radius: Dp = 16.dp,
    val isFixed: Boolean = false,
)

fun LayoutCorners.toShape(progress: Float): RoundedCornerShape =
    RoundedCornerShape(
        topStart = topStart.getProgressRadius(progress),
        topEnd = topEnd.getProgressRadius(progress),
        bottomEnd = bottomEnd.getProgressRadius(progress),
        bottomStart = bottomStart.getProgressRadius(progress),
    )

private fun LayoutCorner.getProgressRadius(progress: Float): Dp =
    if (isFixed && progress > 0f) radius else radius * progress
