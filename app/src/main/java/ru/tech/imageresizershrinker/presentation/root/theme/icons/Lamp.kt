package ru.tech.imageresizershrinker.presentation.root.theme.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Lamp: ImageVector
    get() {
        if (_lamp != null) {
            return _lamp!!
        }
        _lamp = ImageVector.Builder(
            name = "Lightbulb-night", defaultWidth = 24.0.dp, defaultHeight
            = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
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
                moveTo(6.0f, 21.0f)
                curveTo(6.0f, 21.55f, 6.45f, 22.0f, 7.0f, 22.0f)
                horizontalLineTo(11.0f)
                curveTo(11.55f, 22.0f, 12.0f, 21.55f, 12.0f, 21.0f)
                verticalLineTo(20.0f)
                horizontalLineTo(6.0f)
                verticalLineTo(21.0f)
                moveTo(13.0f, 16.32f)
                verticalLineTo(17.0f)
                curveTo(13.0f, 17.55f, 12.55f, 18.0f, 12.0f, 18.0f)
                horizontalLineTo(6.0f)
                curveTo(5.45f, 18.0f, 5.0f, 17.55f, 5.0f, 17.0f)
                verticalLineTo(14.74f)
                curveTo(3.19f, 13.47f, 2.0f, 11.38f, 2.0f, 9.0f)
                curveTo(2.0f, 5.13f, 5.13f, 2.0f, 9.0f, 2.0f)
                curveTo(10.65f, 2.0f, 12.16f, 2.57f, 13.35f, 3.5f)
                curveTo(10.8f, 4.57f, 9.0f, 7.07f, 9.0f, 10.0f)
                curveTo(9.0f, 12.79f, 10.64f, 15.19f, 13.0f, 16.32f)
                moveTo(20.92f, 9.94f)
                lineTo(19.5f, 9.03f)
                lineTo(18.1f, 10.0f)
                lineTo(18.5f, 8.35f)
                lineTo(17.17f, 7.32f)
                lineTo(18.85f, 7.21f)
                lineTo(19.41f, 5.6f)
                lineTo(20.04f, 7.18f)
                lineTo(21.72f, 7.22f)
                lineTo(20.42f, 8.3f)
                lineTo(20.92f, 9.94f)
                moveTo(19.39f, 13.0f)
                curveTo(17.5f, 15.27f, 14.03f, 15.19f, 12.22f, 12.95f)
                curveTo(10.0f, 10.13f, 11.56f, 6.0f, 15.0f, 5.34f)
                curveTo(15.34f, 5.29f, 15.64f, 5.63f, 15.5f, 5.97f)
                curveTo(15.05f, 7.25f, 15.12f, 8.71f, 15.85f, 9.97f)
                curveTo(16.58f, 11.24f, 17.79f, 12.03f, 19.12f, 12.25f)
                curveTo(19.47f, 12.3f, 19.62f, 12.74f, 19.39f, 13.0f)
                close()
            }
        }
            .build()
        return _lamp!!
    }

private var _lamp: ImageVector? = null