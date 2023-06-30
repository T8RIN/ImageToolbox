package ru.tech.imageresizershrinker.presentation.root.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Candle: ImageVector
    get() {
        if (_candle != null) {
            return _candle!!
        }
        _candle = Builder(
            name = "Candle", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = radialGradient(
                    0.088f to Color(0xB2FFE265), 0.398f to Color(0x72FFEC99),
                    0.949f to Color(0x00FFFFFF), center = Offset(63.79865f, 26.105745f), radius =
                    27.026165f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(90.91f, 26.63f)
                curveToRelative(0.0f, 13.56f, -9.16f, 25.96f, -27.18f, 25.58f)
                curveToRelative(-22.17f, -0.48f, -27.04f, -12.76f, -27.04f, -26.31f)
                reflectiveCurveTo(46.34f, 0.0f, 66.05f, 0.0f)
                curveToRelative(17.86f, 0.0f, 24.86f, 13.07f, 24.86f, 26.63f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFE0E0E0), 1.0f to Color(0xFFB0BEC5), start =
                    Offset(63.848f, 66.192f), end = Offset(63.848f, 132.535f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(45.0f, 56.23f)
                verticalLineToRelative(60.64f)
                curveToRelative(0.0f, 3.94f, 8.44f, 7.13f, 18.85f, 7.13f)
                reflectiveCurveToRelative(18.85f, -3.19f, 18.85f, -7.13f)
                verticalLineTo(56.23f)
                horizontalLineTo(45.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(84.18f, 66.0f)
                curveToRelative(-1.4f, -3.1f, -0.06f, -4.96f, -0.49f, -8.24f)
                curveToRelative(-0.16f, -1.21f, -0.07f, -3.18f, -3.1f, -4.77f)
                horizontalLineToRelative(-0.01f)
                curveToRelative(-0.24f, -0.13f, -0.5f, -0.25f, -0.77f, -0.37f)
                curveToRelative(-0.94f, -0.41f, -2.04f, -0.78f, -2.91f, -0.87f)
                horizontalLineToRelative(-0.01f)
                curveToRelative(-5.71f, -1.28f, -15.06f, -1.76f, -23.73f, -4.04f)
                curveToRelative(-4.19f, -1.1f, -6.52f, -0.77f, -7.43f, -0.24f)
                horizontalLineToRelative(-0.01f)
                curveToRelative(-2.19f, 1.14f, -1.99f, 5.79f, -1.64f, 9.35f)
                curveToRelative(0.35f, 3.56f, 0.72f, 7.37f, -1.0f, 10.5f)
                curveToRelative(-0.45f, 0.83f, -1.05f, 1.59f, -1.33f, 2.49f)
                curveToRelative(-0.58f, 1.86f, 0.47f, 4.07f, 2.28f, 4.79f)
                curveToRelative(-1.0f, 2.18f, -1.91f, 4.49f, -1.77f, 6.88f)
                curveToRelative(0.13f, 2.39f, 1.58f, 4.86f, 3.89f, 5.48f)
                curveToRelative(0.59f, 0.16f, 1.25f, 0.18f, 1.79f, -0.1f)
                curveToRelative(1.46f, -0.77f, 1.79f, -2.57f, 1.81f, -3.75f)
                curveToRelative(0.13f, -8.88f, -1.83f, -17.15f, -1.45f, -26.02f)
                curveToRelative(0.01f, -0.21f, 0.03f, -0.44f, 0.16f, -0.61f)
                curveToRelative(0.15f, -0.19f, 0.42f, -0.24f, 0.66f, -0.25f)
                curveToRelative(1.42f, -0.09f, 2.85f, 0.7f, 3.54f, 1.94f)
                curveToRelative(0.63f, 1.14f, 0.64f, 2.52f, 1.18f, 3.71f)
                curveToRelative(0.85f, 1.86f, 3.11f, 2.95f, 5.1f, 2.47f)
                curveToRelative(1.11f, -0.27f, 2.07f, -0.95f, 3.1f, -1.43f)
                curveToRelative(1.04f, -0.48f, 2.78f, -1.17f, 3.29f, -0.14f)
                curveToRelative(2.28f, 4.63f, 5.18f, 4.44f, 5.32f, 5.22f)
                curveToRelative(0.0f, 0.0f, 1.1f, 8.1f, 0.93f, 13.66f)
                curveToRelative(-0.2f, 6.52f, -1.27f, 12.75f, -0.37f, 14.96f)
                curveToRelative(1.02f, 2.51f, 2.98f, 2.17f, 3.74f, 1.67f)
                curveToRelative(3.37f, -2.21f, 0.72f, -10.5f, 0.96f, -12.13f)
                curveToRelative(0.0f, 0.0f, 2.53f, 0.96f, 3.23f, -1.83f)
                curveToRelative(0.72f, -2.89f, -2.33f, -9.22f, -2.19f, -14.45f)
                curveToRelative(0.09f, -3.2f, 1.54f, -4.44f, 3.23f, -3.04f)
                curveToRelative(0.22f, 0.19f, 1.27f, 0.59f, 1.68f, 2.89f)
                curveToRelative(0.42f, 2.38f, 0.05f, 6.13f, 0.81f, 7.89f)
                curveToRelative(0.76f, 1.76f, 3.77f, -0.22f, 3.77f, -4.04f)
                curveToRelative(-0.02f, -1.99f, -1.44f, -5.76f, -2.26f, -7.58f)
                close()
            }
            path(
                fill = radialGradient(
                    0.0f to Color(0xFFFFF176), 1.0f to Color(0x00FFF176), center
                    = Offset(64.359634f, 49.503895f), radius = 22.626122f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(53.16f, 47.73f)
                curveToRelative(-5.52f, -1.45f, -7.82f, -0.41f, -7.88f, 0.29f)
                curveToRelative(-0.31f, 3.9f, 18.46f, 8.43f, 26.41f, 9.4f)
                curveToRelative(7.48f, 0.92f, 11.01f, 0.03f, 10.6f, -2.43f)
                curveToRelative(-0.75f, -4.52f, -15.75f, -3.74f, -29.13f, -7.26f)
                close()
            }
            path(
                fill = radialGradient(
                    0.17f to Color(0xFFFF9616), 0.654f to Color(0xFFFFD429),
                    center = Offset(62.871f, 40.668f), radius = 31.156f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(70.19f, 42.93f)
                curveToRelative(-3.95f, 2.5f, -9.05f, 2.63f, -13.17f, 0.42f)
                curveToRelative(-4.55f, -2.44f, -9.34f, -8.95f, -5.21f, -20.63f)
                curveToRelative(3.99f, -11.3f, 11.34f, -16.7f, 14.55f, -18.61f)
                curveToRelative(0.54f, -0.32f, 1.18f, 0.16f, 1.05f, 0.76f)
                curveToRelative(-0.59f, 2.7f, -0.84f, 8.73f, 4.96f, 17.43f)
                curveToRelative(5.55f, 8.32f, 4.47f, 16.42f, -2.18f, 20.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF546E7A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(62.28f, 50.13f)
                reflectiveCurveToRelative(-1.34f, -3.08f, -0.94f, -7.46f)
                curveToRelative(0.22f, -2.42f, 1.02f, -4.31f, 1.44f, -6.03f)
                curveToRelative(0.54f, -2.16f, 0.57f, -3.85f, 0.57f, -3.85f)
                curveToRelative(0.79f, 0.2f, 1.83f, 1.73f, 1.59f, 4.58f)
                curveToRelative(-0.18f, 2.13f, -0.83f, 5.42f, -0.79f, 6.31f)
                curveToRelative(0.11f, 2.42f, 0.58f, 4.82f, 1.33f, 7.13f)
                curveToRelative(0.01f, 0.03f, 0.02f, 0.05f, 0.02f, 0.08f)
                curveToRelative(0.25f, 0.77f, -3.05f, -0.08f, -3.22f, -0.76f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFFFA726), 0.598f to Color(0x00FFA726), start
                    = Offset(63.983f, 34.004f), end = Offset(61.483f, 52.754f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(61.35f, 42.66f)
                curveToRelative(0.22f, -2.42f, 1.02f, -4.31f, 1.44f, -6.03f)
                curveToRelative(0.54f, -2.16f, 0.57f, -3.85f, 0.57f, -3.85f)
                curveToRelative(0.79f, 0.2f, 1.83f, 1.73f, 1.59f, 4.58f)
                curveToRelative(-0.18f, 2.13f, -0.83f, 5.42f, -0.79f, 6.31f)
                curveToRelative(0.09f, 2.42f, -3.21f, 3.38f, -2.81f, -1.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFDE7)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(49.13f, 48.59f)
                curveToRelative(1.97f, 0.92f, 3.55f, 2.49f, 5.43f, 3.58f)
                curveToRelative(1.35f, 0.78f, 2.85f, 1.3f, 4.39f, 1.52f)
                curveToRelative(0.97f, 0.14f, 2.0f, 0.18f, 2.84f, 0.68f)
                curveToRelative(0.51f, 0.31f, 0.95f, 1.0f, 0.6f, 1.49f)
                curveToRelative(-0.16f, 0.23f, -0.44f, 0.33f, -0.71f, 0.38f)
                curveToRelative(-1.02f, 0.19f, -2.05f, -0.23f, -3.09f, -0.32f)
                curveToRelative(-1.56f, -0.13f, -3.24f, 0.47f, -4.66f, -0.22f)
                curveToRelative(-1.26f, -0.61f, -1.92f, -2.08f, -3.17f, -2.71f)
                curveToRelative(-0.7f, -0.36f, -1.51f, -0.41f, -2.3f, -0.44f)
                curveToRelative(-1.5f, -0.07f, -4.2f, -0.65f, -3.94f, -2.74f)
                curveToRelative(0.29f, -2.18f, 3.22f, -1.86f, 4.61f, -1.22f)
                close()
                moveTo(72.03f, 59.47f)
                curveToRelative(2.65f, 0.87f, 5.74f, 0.79f, 8.01f, -0.83f)
                curveToRelative(0.88f, -0.63f, 1.65f, -1.56f, 1.71f, -2.64f)
                curveToRelative(0.07f, -1.08f, -0.82f, -2.24f, -1.9f, -2.15f)
                curveToRelative(-0.7f, 0.06f, -1.26f, 0.56f, -1.84f, 0.94f)
                curveToRelative(-2.57f, 1.66f, -5.17f, 0.65f, -7.94f, 0.96f)
                curveToRelative(-4.35f, 0.48f, 0.28f, 3.17f, 1.96f, 3.72f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.36f, 92.57f)
                curveToRelative(0.05f, 0.92f, 0.08f, 1.87f, -0.17f, 2.76f)
                curveToRelative(-0.06f, 0.21f, -0.71f, 1.2f, -1.55f, 0.53f)
                curveToRelative(-0.77f, -0.61f, -0.65f, -1.78f, -0.6f, -2.76f)
                curveToRelative(0.15f, -3.09f, 0.34f, -5.73f, 0.81f, -8.76f)
                curveToRelative(0.2f, -1.3f, 0.87f, -0.15f, 0.95f, 0.65f)
                curveToRelative(0.08f, 0.8f, 0.11f, 1.76f, 0.17f, 2.36f)
                curveToRelative(0.19f, 1.73f, 0.3f, 3.47f, 0.39f, 5.22f)
                close()
                moveTo(45.96f, 73.76f)
                curveToRelative(-0.2f, 0.06f, -0.41f, 0.1f, -0.61f, 0.04f)
                curveToRelative(-0.35f, -0.09f, -0.59f, -0.43f, -0.66f, -0.78f)
                curveToRelative(-0.07f, -0.35f, 0.0f, -0.71f, 0.08f, -1.06f)
                curveToRelative(0.36f, -1.42f, 1.07f, -2.73f, 1.76f, -4.03f)
                curveToRelative(0.96f, -1.77f, 1.51f, 1.4f, 1.48f, 2.13f)
                curveToRelative(-0.05f, 1.42f, -0.54f, 3.22f, -2.05f, 3.7f)
                close()
            }
        }
            .build()
        return _candle!!
    }

private var _candle: ImageVector? = null
