package ru.tech.imageresizershrinker.presentation.root.icons.material

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FileSettings: ImageVector by lazy {
    ImageVector.Builder(
        name = "File Settings", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)),
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(6.0f, 0.0f)
            curveTo(4.89f, 0.0f, 4.0f, 0.89f, 4.0f, 2.0f)
            verticalLineTo(18.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 6.0f, 20.0f)
            horizontalLineTo(18.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, false, 20.0f, 18.0f)
            verticalLineTo(6.0f)
            lineTo(14.0f, 0.0f)
            horizontalLineTo(6.0f)
            moveTo(13.0f, 1.5f)
            lineTo(18.5f, 7.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(1.5f)
            moveTo(7.0f, 22.0f)
            verticalLineTo(24.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(22.0f)
            horizontalLineTo(7.0f)
            moveTo(11.0f, 22.0f)
            verticalLineTo(24.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(22.0f)
            horizontalLineTo(11.0f)
            moveTo(15.0f, 22.0f)
            verticalLineTo(24.0f)
            horizontalLineTo(17.0f)
            verticalLineTo(22.0f)
            horizontalLineTo(15.0f)
            close()
        }
    }.build()
}