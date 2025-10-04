package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.TimerEdit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.TimerEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(13f, 14f)
            horizontalLineTo(11f)
            verticalLineTo(8f)
            horizontalLineTo(13f)
            verticalLineTo(14f)
            moveTo(15f, 1f)
            horizontalLineTo(9f)
            verticalLineTo(3f)
            horizontalLineTo(15f)
            verticalLineTo(1f)
            moveTo(5f, 13f)
            curveTo(5f, 9.13f, 8.13f, 6f, 12f, 6f)
            curveTo(15.29f, 6f, 18.05f, 8.28f, 18.79f, 11.34f)
            lineTo(19.39f, 10.74f)
            curveTo(19.71f, 10.42f, 20.1f, 10.21f, 20.5f, 10.1f)
            curveTo(20.18f, 9.11f, 19.67f, 8.19f, 19.03f, 7.39f)
            lineTo(20.45f, 5.97f)
            curveTo(20f, 5.46f, 19.55f, 5f, 19.04f, 4.56f)
            lineTo(17.62f, 6f)
            curveTo(16.07f, 4.74f, 14.12f, 4f, 12f, 4f)
            curveTo(7.03f, 4f, 3f, 8.03f, 3f, 13f)
            curveTo(3f, 17.63f, 6.5f, 21.44f, 11f, 21.94f)
            verticalLineTo(19.92f)
            curveTo(7.61f, 19.43f, 5f, 16.53f, 5f, 13f)
            moveTo(13f, 19.96f)
            verticalLineTo(22f)
            horizontalLineTo(15.04f)
            lineTo(21.17f, 15.88f)
            lineTo(19.13f, 13.83f)
            lineTo(13f, 19.96f)
            moveTo(22.85f, 13.47f)
            lineTo(21.53f, 12.15f)
            curveTo(21.33f, 11.95f, 21f, 11.95f, 20.81f, 12.15f)
            lineTo(19.83f, 13.13f)
            lineTo(21.87f, 15.17f)
            lineTo(22.85f, 14.19f)
            curveTo(23.05f, 14f, 23.05f, 13.67f, 22.85f, 13.47f)
            close()
        }
    }.build()
}
