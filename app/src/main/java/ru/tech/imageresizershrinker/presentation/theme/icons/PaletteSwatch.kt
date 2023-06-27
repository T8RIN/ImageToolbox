package ru.tech.imageresizershrinker.presentation.theme.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.PaletteSwatch: ImageVector
    get() {
        if (_paletteSwatch != null) {
            return _paletteSwatch!!
        }
        _paletteSwatch = ImageVector.Builder(
            name = "Palette-swatch", defaultWidth = 24.0.dp, defaultHeight =
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
                moveTo(2.53f, 19.65f)
                lineTo(3.87f, 20.21f)
                verticalLineTo(11.18f)
                lineTo(1.44f, 17.04f)
                curveTo(1.03f, 18.06f, 1.5f, 19.23f, 2.53f, 19.65f)
                moveTo(22.03f, 15.95f)
                lineTo(17.07f, 4.0f)
                curveTo(16.76f, 3.23f, 16.03f, 2.77f, 15.26f, 2.75f)
                curveTo(15.0f, 2.75f, 14.73f, 2.79f, 14.47f, 2.9f)
                lineTo(7.1f, 5.95f)
                curveTo(6.35f, 6.26f, 5.89f, 7.0f, 5.87f, 7.75f)
                curveTo(5.86f, 8.0f, 5.91f, 8.29f, 6.0f, 8.55f)
                lineTo(11.0f, 20.5f)
                curveTo(11.29f, 21.28f, 12.03f, 21.74f, 12.81f, 21.75f)
                curveTo(13.07f, 21.75f, 13.33f, 21.7f, 13.58f, 21.6f)
                lineTo(20.94f, 18.55f)
                curveTo(21.96f, 18.13f, 22.45f, 16.96f, 22.03f, 15.95f)
                moveTo(7.88f, 8.75f)
                arcTo(
                    1.0f, 1.0f, 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 6.88f,
                    y1 = 7.75f
                )
                arcTo(
                    1.0f, 1.0f, 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = true,
                    x1 = 7.88f,
                    y1 = 6.75f
                )
                curveTo(8.43f, 6.75f, 8.88f, 7.2f, 8.88f, 7.75f)
                curveTo(8.88f, 8.3f, 8.43f, 8.75f, 7.88f, 8.75f)
                moveTo(5.88f, 19.75f)
                arcTo(
                    2.0f, 2.0f, 0.0f,
                    isMoreThanHalf = false,
                    isPositiveArc = false,
                    x1 = 7.88f,
                    y1 = 21.75f
                )
                horizontalLineTo(9.33f)
                lineTo(5.88f, 13.41f)
                verticalLineTo(19.75f)
                close()
            }
        }
            .build()
        return _paletteSwatch!!
    }

private var _paletteSwatch: ImageVector? = null