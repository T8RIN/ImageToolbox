package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

public val Emoji.Eyes: ImageVector
    get() {
        if (_eyes != null) {
            return _eyes!!
        }
        _eyes = Builder(
            name = "Eyes", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFAFAFA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.16f, 106.51f)
                curveTo(18.73f, 106.51f, 6.19f, 87.44f, 6.19f, 64.0f)
                curveToRelative(0.0f, -23.44f, 12.55f, -42.51f, 27.97f, -42.51f)
                curveToRelative(15.42f, 0.0f, 27.97f, 19.07f, 27.97f, 42.51f)
                curveToRelative(0.0f, 23.44f, -12.55f, 42.51f, -27.97f, 42.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0BEC5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.16f, 23.49f)
                curveToRelative(6.63f, 0.0f, 12.98f, 4.0f, 17.87f, 11.27f)
                curveToRelative(5.22f, 7.75f, 8.1f, 18.14f, 8.1f, 29.24f)
                reflectiveCurveToRelative(-2.88f, 21.49f, -8.1f, 29.24f)
                curveToRelative(-4.89f, 7.27f, -11.24f, 11.27f, -17.87f, 11.27f)
                reflectiveCurveToRelative(-12.98f, -4.0f, -17.87f, -11.27f)
                curveTo(11.06f, 85.49f, 8.19f, 75.1f, 8.19f, 64.0f)
                reflectiveCurveToRelative(2.88f, -21.49f, 8.1f, -29.24f)
                curveToRelative(4.89f, -7.27f, 11.23f, -11.27f, 17.87f, -11.27f)
                moveToRelative(0.0f, -4.0f)
                curveTo(17.61f, 19.49f, 4.19f, 39.42f, 4.19f, 64.0f)
                reflectiveCurveToRelative(13.42f, 44.51f, 29.97f, 44.51f)
                reflectiveCurveTo(64.13f, 88.58f, 64.13f, 64.0f)
                reflectiveCurveTo(50.71f, 19.49f, 34.16f, 19.49f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFF424242), 1.0f to Color(0xFF212121), start =
                    Offset(22.523f, 46.676f), end = Offset(22.523f, 82.083f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(25.63f, 59.84f)
                curveToRelative(-2.7f, -2.54f, -2.1f, -7.58f, 1.36f, -11.26f)
                curveToRelative(0.18f, -0.19f, 0.36f, -0.37f, 0.55f, -0.54f)
                curveToRelative(-1.54f, -0.87f, -3.23f, -1.36f, -5.01f, -1.36f)
                curveToRelative(-7.19f, 0.0f, -13.02f, 7.93f, -13.02f, 17.7f)
                reflectiveCurveToRelative(5.83f, 17.7f, 13.02f, 17.7f)
                reflectiveCurveToRelative(13.02f, -7.93f, 13.02f, -17.7f)
                curveToRelative(0.0f, -1.75f, -0.19f, -3.45f, -0.54f, -5.05f)
                curveToRelative(-3.24f, 2.33f, -7.11f, 2.64f, -9.38f, 0.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.87f, 64.0f)
                arcToRelative(29.97f, 44.51f, 0.0f, true, false, 59.94f, 0.0f)
                arcToRelative(29.97f, 44.51f, 0.0f, true, false, -59.94f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFAFAFA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.84f, 106.51f)
                curveToRelative(-15.42f, 0.0f, -27.97f, -19.07f, -27.97f, -42.51f)
                curveToRelative(0.0f, -23.44f, 12.55f, -42.51f, 27.97f, -42.51f)
                curveToRelative(15.42f, 0.0f, 27.97f, 19.07f, 27.97f, 42.51f)
                curveToRelative(0.0f, 23.44f, -12.54f, 42.51f, -27.97f, 42.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0BEC5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.84f, 23.49f)
                curveToRelative(6.63f, 0.0f, 12.98f, 4.0f, 17.87f, 11.27f)
                curveToRelative(5.22f, 7.75f, 8.1f, 18.14f, 8.1f, 29.24f)
                reflectiveCurveToRelative(-2.88f, 21.49f, -8.1f, 29.24f)
                curveToRelative(-4.89f, 7.27f, -11.24f, 11.27f, -17.87f, 11.27f)
                reflectiveCurveToRelative(-12.98f, -4.0f, -17.87f, -11.27f)
                curveToRelative(-5.22f, -7.75f, -8.1f, -18.14f, -8.1f, -29.24f)
                reflectiveCurveToRelative(2.88f, -21.49f, 8.1f, -29.24f)
                curveToRelative(4.89f, -7.27f, 11.24f, -11.27f, 17.87f, -11.27f)
                moveToRelative(0.0f, -4.0f)
                curveToRelative(-16.55f, 0.0f, -29.97f, 19.93f, -29.97f, 44.51f)
                reflectiveCurveToRelative(13.42f, 44.51f, 29.97f, 44.51f)
                reflectiveCurveTo(123.81f, 88.58f, 123.81f, 64.0f)
                reflectiveCurveToRelative(-13.42f, -44.51f, -29.97f, -44.51f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFF424242), 1.0f to Color(0xFF212121), start =
                    Offset(82.209f, 46.676f), end = Offset(82.209f, 82.083f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(85.31f, 59.84f)
                curveToRelative(-2.7f, -2.54f, -2.1f, -7.58f, 1.36f, -11.26f)
                curveToRelative(0.18f, -0.19f, 0.36f, -0.37f, 0.55f, -0.54f)
                curveToRelative(-1.54f, -0.87f, -3.23f, -1.36f, -5.01f, -1.36f)
                curveToRelative(-7.19f, 0.0f, -13.02f, 7.93f, -13.02f, 17.7f)
                reflectiveCurveToRelative(5.83f, 17.7f, 13.02f, 17.7f)
                curveToRelative(7.19f, 0.0f, 13.02f, -7.93f, 13.02f, -17.7f)
                curveToRelative(0.0f, -1.75f, -0.19f, -3.45f, -0.54f, -5.05f)
                curveToRelative(-3.23f, 2.33f, -7.11f, 2.64f, -9.38f, 0.51f)
                close()
            }
        }
            .build()
        return _eyes!!
    }

private var _eyes: ImageVector? = null
