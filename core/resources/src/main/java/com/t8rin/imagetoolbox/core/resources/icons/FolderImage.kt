package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.FolderImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.FolderImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFE3E3E3))) {
            moveTo(6.667f, 15.733f)
            lineToRelative(10.667f, 0f)
            lineToRelative(-3.68f, -4.8f)
            lineToRelative(-2.453f, 3.2f)
            lineToRelative(-1.653f, -2.133f)
            lineToRelative(-2.88f, 3.733f)
            close()
        }
        path(fill = SolidColor(Color(0xFFE3E3E3))) {
            moveTo(20.973f, 6.76f)
            curveToRelative(-0.418f, -0.418f, -0.92f, -0.627f, -1.507f, -0.627f)
            horizontalLineToRelative(-7.467f)
            lineToRelative(-2.133f, -2.133f)
            horizontalLineToRelative(-5.333f)
            curveToRelative(-0.587f, 0f, -1.089f, 0.209f, -1.507f, 0.627f)
            reflectiveCurveToRelative(-0.627f, 0.92f, -0.627f, 1.507f)
            verticalLineToRelative(11.733f)
            curveToRelative(0f, 0.587f, 0.209f, 1.089f, 0.627f, 1.507f)
            reflectiveCurveToRelative(0.92f, 0.627f, 1.507f, 0.627f)
            horizontalLineToRelative(14.933f)
            curveToRelative(0.587f, 0f, 1.089f, -0.209f, 1.507f, -0.627f)
            reflectiveCurveToRelative(0.627f, -0.92f, 0.627f, -1.507f)
            verticalLineToRelative(-9.6f)
            curveToRelative(0f, -0.587f, -0.209f, -1.089f, -0.627f, -1.507f)
            close()
            moveTo(19.467f, 17.867f)
            horizontalLineTo(4.533f)
            verticalLineTo(6.133f)
            horizontalLineToRelative(4.453f)
            lineToRelative(2.133f, 2.133f)
            horizontalLineToRelative(8.347f)
            verticalLineToRelative(9.6f)
            close()
        }
    }.build()
}

val Icons.Rounded.FolderImage: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FolderImage",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFFE3E3E3))) {
            moveTo(20.973f, 6.76f)
            curveToRelative(-0.418f, -0.418f, -0.92f, -0.627f, -1.507f, -0.627f)
            horizontalLineToRelative(-7.467f)
            lineToRelative(-2.133f, -2.133f)
            horizontalLineToRelative(-5.333f)
            curveToRelative(-0.587f, 0f, -1.089f, 0.209f, -1.507f, 0.627f)
            curveToRelative(-0.418f, 0.418f, -0.627f, 0.92f, -0.627f, 1.507f)
            verticalLineToRelative(11.733f)
            curveToRelative(0f, 0.587f, 0.209f, 1.089f, 0.627f, 1.507f)
            curveToRelative(0.418f, 0.418f, 0.92f, 0.627f, 1.507f, 0.627f)
            horizontalLineToRelative(14.933f)
            curveToRelative(0.587f, 0f, 1.089f, -0.209f, 1.507f, -0.627f)
            curveToRelative(0.418f, -0.418f, 0.627f, -0.92f, 0.627f, -1.507f)
            verticalLineToRelative(-9.6f)
            curveToRelative(0f, -0.587f, -0.209f, -1.089f, -0.627f, -1.507f)
            close()
            moveTo(6.667f, 15.733f)
            lineToRelative(2.88f, -3.733f)
            lineToRelative(1.653f, 2.133f)
            lineToRelative(2.453f, -3.2f)
            lineToRelative(3.68f, 4.8f)
            horizontalLineTo(6.667f)
            close()
        }
    }.build()
}