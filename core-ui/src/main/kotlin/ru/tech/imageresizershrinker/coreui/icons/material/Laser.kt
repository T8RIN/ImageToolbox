package ru.tech.imageresizershrinker.coreui.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Laser: ImageVector by lazy {
    Builder(
        name = "Laser", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(9.0f, 13.0f)
            lineTo(5.0f, 16.0f)
            curveTo(4.0f, 16.88f, 3.86f, 18.12f, 4.0f, 19.0f)
            curveTo(4.13f, 20.0f, 4.91f, 21.22f, 6.0f, 21.68f)
            curveTo(7.57f, 22.35f, 9.09f, 21.9f, 10.04f, 20.92f)
            lineTo(19.0f, 13.0f)
            curveTo(20.86f, 11.62f, 20.0f, 9.0f, 18.0f, 9.0f)
            horizontalLineTo(12.0f)
            lineTo(19.46f, 4.61f)
            curveTo(19.9f, 4.29f, 20.08f, 3.82f, 20.06f, 3.37f)
            curveTo(20.0f, 2.67f, 19.46f, 2.0f, 18.6f, 2.0f)
            horizontalLineTo(18.54f)
            curveTo(18.19f, 2.0f, 17.86f, 2.11f, 17.56f, 2.29f)
            lineTo(5.0f, 9.0f)
            curveTo(4.19f, 9.46f, 3.94f, 10.24f, 4.0f, 11.0f)
            curveTo(4.05f, 12.03f, 4.74f, 13.0f, 6.0f, 13.0f)
            moveTo(5.0f, 18.5f)
            curveTo(5.0f, 17.12f, 6.12f, 16.0f, 7.5f, 16.0f)
            reflectiveCurveTo(10.0f, 17.12f, 10.0f, 18.5f)
            reflectiveCurveTo(8.88f, 21.0f, 7.5f, 21.0f)
            reflectiveCurveTo(5.0f, 19.88f, 5.0f, 18.5f)
            close()
        }
    }.build()
}
