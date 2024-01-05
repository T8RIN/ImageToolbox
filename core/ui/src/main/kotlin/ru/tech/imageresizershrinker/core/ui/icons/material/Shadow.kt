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

val Icons.Filled.Shadow: ImageVector by lazy {
    Builder(
        name = "Shadow", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(3.0f, 3.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(3.0f)
            verticalLineTo(3.0f)
            moveTo(19.0f, 19.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(19.0f)
            moveTo(19.0f, 16.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(18.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(16.0f)
            moveTo(19.0f, 13.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(13.0f)
            moveTo(19.0f, 10.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(12.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(10.0f)
            moveTo(19.0f, 7.0f)
            horizontalLineTo(21.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(19.0f)
            verticalLineTo(7.0f)
            moveTo(16.0f, 19.0f)
            horizontalLineTo(18.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(16.0f)
            verticalLineTo(19.0f)
            moveTo(13.0f, 19.0f)
            horizontalLineTo(15.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(13.0f)
            verticalLineTo(19.0f)
            moveTo(10.0f, 19.0f)
            horizontalLineTo(12.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(10.0f)
            verticalLineTo(19.0f)
            moveTo(7.0f, 19.0f)
            horizontalLineTo(9.0f)
            verticalLineTo(21.0f)
            horizontalLineTo(7.0f)
            verticalLineTo(19.0f)
            close()
        }
    }.build()
}