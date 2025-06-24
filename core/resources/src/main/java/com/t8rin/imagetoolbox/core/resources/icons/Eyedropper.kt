package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Eyedropper: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Eyedropper",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(19.35f, 11.72f)
            lineTo(17.22f, 13.85f)
            lineTo(15.81f, 12.43f)
            lineTo(8.1f, 20.14f)
            lineTo(3.5f, 22f)
            lineTo(2f, 20.5f)
            lineTo(3.86f, 15.9f)
            lineTo(11.57f, 8.19f)
            lineTo(10.15f, 6.78f)
            lineTo(12.28f, 4.65f)
            lineTo(19.35f, 11.72f)
            moveTo(16.76f, 3f)
            curveTo(17.93f, 1.83f, 19.83f, 1.83f, 21f, 3f)
            curveTo(22.17f, 4.17f, 22.17f, 6.07f, 21f, 7.24f)
            lineTo(19.08f, 9.16f)
            lineTo(14.84f, 4.92f)
            lineTo(16.76f, 3f)
            moveTo(5.56f, 17.03f)
            lineTo(4.5f, 19.5f)
            lineTo(6.97f, 18.44f)
            lineTo(14.4f, 11f)
            lineTo(13f, 9.6f)
            lineTo(5.56f, 17.03f)
            close()
        }
    }.build()
}

val Icons.TwoTone.Eyedropper: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.Eyedropper",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(16.76f, 3f)
            curveToRelative(1.17f, -1.17f, 3.07f, -1.17f, 4.24f, 0f)
            curveToRelative(1.17f, 1.17f, 1.17f, 3.07f, 0f, 4.24f)
            lineToRelative(-1.92f, 1.92f)
            lineToRelative(-4.24f, -4.24f)
            lineToRelative(1.92f, -1.92f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(5.56f, 17.03f)
            lineToRelative(-1.06f, 2.47f)
            lineToRelative(2.47f, -1.06f)
            lineToRelative(7.43f, -7.44f)
            lineToRelative(-1.4f, -1.4f)
            lineToRelative(-7.44f, 7.43f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12.28f, 4.65f)
            lineToRelative(-2.13f, 2.13f)
            lineToRelative(1.42f, 1.41f)
            lineToRelative(-7.71f, 7.71f)
            lineToRelative(-1.86f, 4.6f)
            lineToRelative(1.5f, 1.5f)
            lineToRelative(4.6f, -1.86f)
            lineToRelative(7.71f, -7.71f)
            lineToRelative(1.41f, 1.42f)
            lineToRelative(2.13f, -2.13f)
            lineToRelative(-7.07f, -7.07f)
            close()
            moveTo(6.97f, 18.44f)
            lineToRelative(-2.47f, 1.06f)
            lineToRelative(1.06f, -2.47f)
            lineToRelative(7.44f, -7.43f)
            lineToRelative(1.4f, 1.4f)
            lineToRelative(-7.43f, 7.44f)
            close()
        }
    }.build()
}