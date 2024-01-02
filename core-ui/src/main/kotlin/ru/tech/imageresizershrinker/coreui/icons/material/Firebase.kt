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

val Icons.Rounded.Firebase: ImageVector by lazy {
    Builder(
        name = "Firebase", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(20.0f, 18.69f)
            lineTo(12.7f, 22.74f)
            curveTo(12.2f, 23.0f, 11.7f, 23.0f, 11.2f, 22.74f)
            lineTo(4.0f, 18.69f)
            lineTo(17.05f, 5.54f)
            lineTo(17.4f, 5.44f)
            curveTo(17.7f, 5.44f, 17.87f, 5.57f, 17.9f, 5.84f)
            lineTo(20.0f, 18.69f)
            moveTo(9.35f, 5.74f)
            lineTo(4.8f, 13.29f)
            lineTo(6.7f, 1.34f)
            curveTo(6.73f, 1.07f, 6.9f, 0.94f, 7.2f, 0.94f)
            curveTo(7.4f, 0.94f, 7.53f, 1.0f, 7.6f, 1.19f)
            lineTo(9.75f, 5.14f)
            lineTo(9.35f, 5.74f)
            moveTo(13.85f, 7.0f)
            lineTo(4.3f, 16.59f)
            lineTo(11.55f, 4.29f)
            curveTo(11.65f, 4.09f, 11.8f, 4.0f, 12.0f, 4.0f)
            curveTo(12.2f, 4.0f, 12.33f, 4.09f, 12.4f, 4.29f)
            lineTo(13.85f, 7.0f)
            close()
        }
    }.build()
}