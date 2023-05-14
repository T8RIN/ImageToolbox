package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

public val Emoji.Malesign: ImageVector
    get() {
        if (_malesign != null) {
            return _malesign!!
        }
        _malesign = Builder(name = "Malesign", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = SolidColor(Color(0xFF00796B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(106.7f, 6.9f)
                horizontalLineTo(76.53f)
                curveToRelative(-2.23f, 0.0f, -4.06f, 1.83f, -4.06f, 4.06f)
                verticalLineToRelative(8.13f)
                curveToRelative(0.0f, 2.23f, 1.83f, 4.06f, 4.06f, 4.06f)
                horizontalLineToRelative(16.26f)
                curveToRelative(0.91f, 0.0f, 1.32f, 1.12f, 0.71f, 1.73f)
                lineTo(71.25f, 46.03f)
                curveToRelative(-17.06f, -11.58f, -40.83f, -9.34f, -55.35f, 6.6f)
                curveToRelative(-15.03f, 16.36f, -14.83f, 41.85f, 0.3f, 58.0f)
                curveToRelative(16.56f, 17.57f, 44.28f, 17.88f, 61.24f, 0.91f)
                curveToRelative(14.73f, -14.73f, 16.45f, -37.48f, 5.18f, -54.03f)
                lineToRelative(22.25f, -21.15f)
                curveToRelative(0.61f, -0.61f, 1.73f, -0.2f, 1.73f, 0.71f)
                verticalLineToRelative(16.26f)
                curveToRelative(0.0f, 2.23f, 1.83f, 4.06f, 4.06f, 4.06f)
                horizontalLineToRelative(8.13f)
                curveToRelative(2.23f, 0.0f, 4.06f, -1.83f, 4.06f, -4.06f)
                verticalLineTo(10.96f)
                curveToRelative(0.0f, -2.23f, -1.83f, -4.06f, -4.06f, -4.06f)
                horizontalLineTo(106.7f)
                close()
                moveTo(65.46f, 99.45f)
                curveToRelative(-9.95f, 9.95f, -26.2f, 9.95f, -36.26f, 0.0f)
                reflectiveCurveToRelative(-9.95f, -26.2f, 0.0f, -36.16f)
                reflectiveCurveToRelative(26.2f, -9.95f, 36.26f, 0.0f)
                reflectiveCurveToRelative(9.95f, 26.21f, 0.0f, 36.16f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFF00BFA5), 0.705f to Color(0xFF00B29A), 1.0f
                    to Color(0xFF00AB94), start = Offset(63.803f,4.156f), end =
                    Offset(63.803f,119.779f)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap
                    = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType =
                    NonZero) {
                moveTo(106.71f, 3.87f)
                horizontalLineTo(76.54f)
                curveToRelative(-2.23f, 0.0f, -4.06f, 1.83f, -4.06f, 4.06f)
                verticalLineToRelative(7.09f)
                curveToRelative(0.0f, 2.23f, 1.83f, 4.06f, 4.06f, 4.06f)
                horizontalLineTo(92.8f)
                curveToRelative(0.91f, 0.0f, 2.27f, 1.28f, 0.71f, 2.76f)
                lineTo(71.26f, 43.0f)
                curveToRelative(-17.06f, -11.58f, -40.83f, -9.34f, -55.35f, 6.6f)
                curveToRelative(-15.03f, 16.35f, -14.83f, 40.81f, 0.3f, 56.96f)
                curveToRelative(16.56f, 17.57f, 44.28f, 17.88f, 61.24f, 0.91f)
                curveToRelative(14.73f, -14.73f, 15.58f, -37.54f, 4.31f, -54.1f)
                lineToRelative(22.49f, -21.24f)
                curveToRelative(1.35f, -1.2f, 2.56f, 0.62f, 2.36f, 1.9f)
                verticalLineToRelative(15.22f)
                curveToRelative(0.0f, 2.23f, 1.83f, 4.06f, 4.06f, 4.06f)
                horizontalLineToRelative(8.13f)
                curveToRelative(2.23f, 0.0f, 4.06f, -1.83f, 4.06f, -4.06f)
                verticalLineTo(7.94f)
                curveToRelative(0.0f, -2.23f, -1.83f, -4.06f, -4.06f, -4.06f)
                curveToRelative(0.1f, -0.01f, -12.09f, -0.01f, -12.09f, -0.01f)
                close()
                moveTo(65.47f, 96.43f)
                curveToRelative(-9.95f, 9.95f, -26.2f, 9.95f, -36.26f, 0.0f)
                reflectiveCurveToRelative(-9.95f, -27.24f, 0.0f, -37.19f)
                reflectiveCurveToRelative(26.2f, -9.95f, 36.26f, 0.0f)
                reflectiveCurveToRelative(9.95f, 27.23f, 0.0f, 37.19f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                    = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(86.19f, 7.33f)
                curveToRelative(0.0f, -0.91f, -0.61f, -1.22f, -4.27f, -1.02f)
                curveToRelative(-3.05f, 0.1f, -4.47f, 0.51f, -5.38f, 1.52f)
                reflectiveCurveToRelative(-1.12f, 3.35f, -1.22f, 5.99f)
                curveToRelative(0.0f, 1.93f, 0.0f, 3.66f, 1.02f, 3.66f)
                curveToRelative(1.42f, 0.0f, 1.42f, -3.15f, 2.44f, -4.88f)
                curveToRelative(2.03f, -3.34f, 7.41f, -4.05f, 7.41f, -5.27f)
                close()
                moveTo(109.1f, 40.59f)
                curveToRelative(-0.91f, 0.0f, -1.22f, 0.61f, -1.02f, 4.27f)
                curveToRelative(0.1f, 3.05f, 0.51f, 4.47f, 1.52f, 5.38f)
                reflectiveCurveToRelative(3.35f, 1.12f, 5.99f, 1.22f)
                curveToRelative(1.93f, 0.0f, 3.66f, 0.0f, 3.66f, -1.02f)
                curveToRelative(0.0f, -1.42f, -3.15f, -1.42f, -4.88f, -2.44f)
                curveToRelative(-3.34f, -2.02f, -4.05f, -7.41f, -5.27f, -7.41f)
                close()
                moveTo(17.07f, 53.61f)
                curveToRelative(3.82f, -5.8f, 11.93f, -11.96f, 20.77f, -13.45f)
                curveToRelative(2.19f, -0.4f, 4.37f, -0.5f, 6.26f, 0.2f)
                curveToRelative(1.39f, 0.5f, 2.58f, 1.89f, 1.79f, 3.38f)
                curveToRelative(-0.6f, 1.19f, -4.23f, 1.07f, -5.53f, 1.46f)
                curveToRelative(-4.54f, 1.16f, -8.26f, 2.51f, -11.22f, 4.41f)
                curveToRelative(-3.94f, 2.53f, -7.68f, 7.07f, -9.36f, 10.04f)
                curveToRelative(-3.19f, 5.63f, -4.27f, 10.71f, -6.85f, 8.82f)
                curveToRelative(-2.48f, -1.7f, 0.32f, -9.05f, 4.14f, -14.86f)
                close()
            }
        }
        .build()
        return _malesign!!
    }

private var _malesign: ImageVector? = null
