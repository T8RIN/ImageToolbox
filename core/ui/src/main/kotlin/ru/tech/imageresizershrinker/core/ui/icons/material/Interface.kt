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

val Icons.Filled.Interface: ImageVector by lazy {
    Builder(
        name = "Interface", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(6.0f, 9.0f)
            verticalLineTo(4.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(23.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(16.0f)
            horizontalLineTo(1.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(6.0f)
            moveTo(16.0f, 16.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(19.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(16.0f)
            moveTo(8.0f, 9.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(6.0f)
            horizontalLineTo(8.0f)
            verticalLineTo(9.0f)
            moveTo(6.0f, 14.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(6.0f)
            moveTo(18.0f, 11.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(18.0f)
            moveTo(13.0f, 11.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(13.0f)
            moveTo(8.0f, 11.0f)
            verticalLineTo(14.0f)
            horizontalLineTo(11.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(8.0f)
            close()
        }
    }.build()
}