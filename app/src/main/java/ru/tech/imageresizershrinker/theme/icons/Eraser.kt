package ru.tech.imageresizershrinker.theme.icons

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

val Icons.Rounded.Eraser: ImageVector
    get() {
        if (_eraser != null) {
            return _eraser!!
        }
        _eraser = Builder(
            name = "Eraser", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.14f, 3.0f)
                curveTo(14.63f, 3.0f, 14.12f, 3.2f, 13.73f, 3.59f)
                lineTo(2.59f, 14.73f)
                curveTo(1.81f, 15.5f, 1.81f, 16.77f, 2.59f, 17.56f)
                lineTo(5.03f, 20.0f)
                horizontalLineTo(12.69f)
                lineTo(21.41f, 11.27f)
                curveTo(22.2f, 10.5f, 22.2f, 9.23f, 21.41f, 8.44f)
                lineTo(16.56f, 3.59f)
                curveTo(16.17f, 3.2f, 15.65f, 3.0f, 15.14f, 3.0f)
                moveTo(17.0f, 18.0f)
                lineTo(15.0f, 20.0f)
                horizontalLineTo(22.0f)
                verticalLineTo(18.0f)
            }
        }
            .build()
        return _eraser!!
    }

private var _eraser: ImageVector? = null
