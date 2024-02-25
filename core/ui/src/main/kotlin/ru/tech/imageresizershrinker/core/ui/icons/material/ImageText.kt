package ru.tech.imageresizershrinker.core.ui.icons.material

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

val Icons.Rounded.ImageText: ImageVector by lazy {
    Builder(
        name = "Image Text", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 13.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(13.0f)
            moveTo(22.0f, 7.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(7.0f)
            moveTo(14.0f, 17.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(17.0f)
            moveTo(12.0f, 9.0f)
            verticalLineTo(15.0f)
            curveTo(12.0f, 16.1f, 11.1f, 17.0f, 10.0f, 17.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 17.0f, 2.0f, 16.1f, 2.0f, 15.0f)
            verticalLineTo(9.0f)
            curveTo(2.0f, 7.9f, 2.9f, 7.0f, 4.0f, 7.0f)
            horizontalLineTo(10.0f)
            curveTo(11.1f, 7.0f, 12.0f, 7.9f, 12.0f, 9.0f)
            moveTo(10.5f, 15.0f)
            lineTo(8.3f, 12.0f)
            lineTo(6.5f, 14.3f)
            lineTo(5.3f, 12.8f)
            lineTo(3.5f, 15.0f)
            horizontalLineTo(10.5f)
            close()
        }
    }.build()
}