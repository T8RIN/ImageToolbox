package ru.tech.imageresizershrinker.presentation.root.icons.material

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

val Icons.Filled.Numeric: ImageVector by lazy {
    Builder(
        name = "Numeric", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(4.0f, 17.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(2.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(6.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(4.0f)
            moveTo(22.0f, 15.0f)
            curveTo(22.0f, 16.11f, 21.1f, 17.0f, 20.0f, 17.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(13.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(20.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(20.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, true, 22.0f, 9.0f)
            verticalLineTo(10.5f)
            arcTo(1.5f, 1.5f, 0.0f, false, true, 20.5f, 12.0f)
            arcTo(1.5f, 1.5f, 0.0f, false, true, 22.0f, 13.5f)
            verticalLineTo(15.0f)
            moveTo(14.0f, 15.0f)
            verticalLineTo(17.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(13.0f)
            curveTo(8.0f, 11.89f, 8.9f, 11.0f, 10.0f, 11.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(7.0f)
            horizontalLineTo(12.0f)
            arcTo(2.0f, 2.0f, 0.0f, false, true, 14.0f, 9.0f)
            verticalLineTo(11.0f)
            curveTo(14.0f, 12.11f, 13.1f, 13.0f, 12.0f, 13.0f)
            horizontalLineTo(10.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(14.0f)
            close()
        }
    }.build()
}
