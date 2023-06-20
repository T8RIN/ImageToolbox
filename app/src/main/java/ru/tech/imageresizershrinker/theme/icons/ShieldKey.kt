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

val Icons.Rounded.ShieldKey: ImageVector
    get() {
        if (_shieldKey != null) {
            return _shieldKey!!
        }
        _shieldKey = Builder(
            name = "Shield Key",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)),
                stroke = null,
                strokeLineWidth = 0.0f,
                strokeLineCap = Butt,
                strokeLineJoin = Miter,
                strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 8.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 13.0f, 9.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 12.0f, 10.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 11.0f, 9.0f)
                arcTo(1.0f, 1.0f, 0.0f, false, true, 12.0f, 8.0f)
                moveTo(21.0f, 11.0f)
                curveTo(21.0f, 16.55f, 17.16f, 21.74f, 12.0f, 23.0f)
                curveTo(6.84f, 21.74f, 3.0f, 16.55f, 3.0f, 11.0f)
                verticalLineTo(5.0f)
                lineTo(12.0f, 1.0f)
                lineTo(21.0f, 5.0f)
                verticalLineTo(11.0f)
                moveTo(12.0f, 6.0f)
                arcTo(3.0f, 3.0f, 0.0f, false, false, 9.0f, 9.0f)
                curveTo(9.0f, 10.31f, 9.83f, 11.42f, 11.0f, 11.83f)
                verticalLineTo(18.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(16.0f)
                horizontalLineTo(15.0f)
                verticalLineTo(14.0f)
                horizontalLineTo(13.0f)
                verticalLineTo(11.83f)
                curveTo(14.17f, 11.42f, 15.0f, 10.31f, 15.0f, 9.0f)
                arcTo(3.0f, 3.0f, 0.0f, false, false, 12.0f, 6.0f)
                close()
            }
        }.build()
        return _shieldKey!!
    }

private var _shieldKey: ImageVector? = null
