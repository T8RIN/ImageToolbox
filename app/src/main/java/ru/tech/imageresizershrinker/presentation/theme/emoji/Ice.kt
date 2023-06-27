package ru.tech.imageresizershrinker.presentation.theme.emoji

import androidx.compose.ui.geometry.Offset
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
import ru.tech.imageresizershrinker.presentation.theme.Emoji

val Emoji.Ice: ImageVector
    get() {
        if (_ice != null) {
            return _ice!!
        }
        _ice = Builder(
            name = "Ice", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth =
            128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(63.03f, 5.38f)
                lineTo(16.79f, 20.85f)
                lineToRelative(-6.58f, 3.79f)
                lineToRelative(-0.83f, 24.84f)
                lineToRelative(5.95f, 43.37f)
                lineToRelative(30.91f, 16.7f)
                lineToRelative(19.7f, 7.1f)
                lineToRelative(14.53f, -5.95f)
                lineToRelative(34.17f, -15.67f)
                reflectiveCurveToRelative(3.34f, -0.27f, 1.78f, -5.38f)
                reflectiveCurveToRelative(0.42f, -53.34f, 0.42f, -53.34f)
                lineToRelative(0.18f, -11.58f)
                reflectiveCurveToRelative(-0.02f, -2.73f, -9.07f, -4.71f)
                reflectiveCurveTo(63.03f, 5.38f, 63.03f, 5.38f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF7EC8EE)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(9.31f, 25.82f)
                lineToRelative(54.45f, 24.9f)
                lineToRelative(52.26f, -23.8f)
                reflectiveCurveToRelative(0.27f, 63.47f, -0.55f, 63.75f)
                curveToRelative(-0.41f, 0.14f, -51.16f, -19.7f, -51.16f, -19.7f)
                lineToRelative(-49.79f, 19.7f)
                lineToRelative(-4.93f, -54.73f)
                lineToRelative(-0.28f, -10.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF63ABDE)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(65.95f, 116.65f)
                lineToRelative(-25.45f, -9.3f)
                lineToRelative(-25.17f, -14.5f)
                lineToRelative(-0.82f, -2.19f)
                lineToRelative(47.88f, -21.62f)
                lineToRelative(53.08f, 21.62f)
                lineToRelative(-0.82f, 4.38f)
                lineToRelative(-21.61f, 10.39f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0E4FF)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(62.94f, 5.02f)
                lineTo(15.33f, 21.16f)
                lineToRelative(-6.02f, 4.66f)
                lineToRelative(8.21f, 3.83f)
                lineTo(61.57f, 48.8f)
                lineToRelative(3.56f, -0.55f)
                lineToRelative(50.89f, -24.35f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF37B4E2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(116.84f, 89.59f)
                curveToRelative(-1.5f, -1.5f, -31.75f, -13.7f, -37.53f, -16.24f)
                curveToRelative(-5.78f, -2.54f, -8.33f, -4.16f, -9.37f, -6.71f)
                reflectiveCurveToRelative(-3.12f, -45.8f, -3.12f, -48.57f)
                reflectiveCurveToRelative(-0.33f, -10.85f, -3.46f, -10.86f)
                curveToRelative(-2.57f, -0.01f, -3.12f, 7.34f, -3.12f, 11.19f)
                curveToRelative(0.0f, 3.85f, -1.03f, 44.81f, -1.98f, 48.35f)
                curveToRelative(-1.16f, 4.36f, -12.04f, 7.97f, -21.39f, 12.37f)
                curveToRelative(-9.36f, 4.4f, -21.84f, 7.83f, -22.36f, 11.53f)
                curveToRelative(-0.51f, 3.7f, 2.06f, 4.62f, 6.83f, 7.92f)
                curveToRelative(1.95f, 1.35f, 8.17f, 4.83f, 15.41f, 8.42f)
                curveToRelative(10.49f, 5.2f, 23.16f, 10.42f, 28.38f, 10.51f)
                curveToRelative(7.35f, 0.13f, 18.19f, -5.83f, 29.67f, -11.2f)
                curveToRelative(11.27f, -5.27f, 21.34f, -9.5f, 22.15f, -11.46f)
                curveToRelative(1.25f, -3.01f, 1.4f, -3.75f, -0.11f, -5.25f)
                close()
                moveTo(93.88f, 102.26f)
                curveToRelative(-7.5f, 3.42f, -24.98f, 11.1f, -29.03f, 11.45f)
                curveToRelative(-4.05f, 0.35f, -18.52f, -6.52f, -24.98f, -9.6f)
                curveToRelative(-7.29f, -3.47f, -21.39f, -10.76f, -20.59f, -12.61f)
                curveToRelative(0.81f, -1.85f, 9.6f, -4.86f, 21.74f, -9.83f)
                curveToRelative(12.14f, -4.97f, 23.01f, -9.14f, 23.01f, -9.14f)
                reflectiveCurveToRelative(7.52f, 2.68f, 20.7f, 8.33f)
                curveTo(97.68f, 86.41f, 112.83f, 93.0f, 112.83f, 93.0f)
                reflectiveCurveToRelative(-9.81f, 5.09f, -18.95f, 9.26f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF58C4FD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.56f, 3.92f)
                curveToRelative(-4.11f, -0.57f, -12.68f, 3.86f, -24.4f, 7.86f)
                curveToRelative(-12.51f, 4.27f, -23.06f, 7.12f, -26.74f, 8.96f)
                curveToRelative(-3.03f, 1.52f, -4.55f, 4.0f, -4.69f, 7.31f)
                curveToRelative(-0.13f, 3.3f, -0.41f, 12.67f, 2.07f, 36.65f)
                curveToRelative(0.76f, 7.3f, 3.53f, 25.86f, 3.89f, 27.17f)
                curveToRelative(1.85f, 6.73f, 4.93f, 1.91f, 4.24f, -3.33f)
                curveToRelative(-0.49f, -3.7f, -2.07f, -12.13f, -3.45f, -26.33f)
                reflectiveCurveToRelative(-2.76f, -32.11f, -1.52f, -35.15f)
                reflectiveCurveToRelative(9.19f, -5.38f, 14.75f, -7.03f)
                curveToRelative(17.23f, -5.1f, 32.53f, -11.85f, 35.84f, -11.85f)
                reflectiveCurveToRelative(25.77f, 8.13f, 29.5f, 9.51f)
                curveToRelative(3.72f, 1.38f, 19.71f, 5.93f, 20.54f, 8.13f)
                reflectiveCurveToRelative(0.14f, 58.58f, 0.28f, 61.61f)
                curveToRelative(0.14f, 3.03f, -0.65f, 6.83f, 1.59f, 7.24f)
                curveToRelative(2.25f, 0.4f, 2.82f, -3.38f, 2.82f, -6.0f)
                reflectiveCurveToRelative(-0.14f, -27.7f, 0.14f, -39.14f)
                curveToRelative(0.28f, -11.44f, 2.07f, -22.6f, -1.1f, -26.6f)
                reflectiveCurveToRelative(-12.96f, -6.06f, -29.22f, -11.72f)
                curveTo(77.96f, 7.7f, 67.55f, 4.47f, 63.56f, 3.92f)
                close()
            }
            path(
                fill = radialGradient(
                    0.1f to Color(0xE5FFFFFF), 1.0f to Color(0x00FCFCFC), center
                    = Offset(61.841755f, 58.67145f), radius = 46.170475f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(15.32f, 29.69f)
                lineToRelative(47.96f, 21.92f)
                lineToRelative(2.48f, 61.2f)
                lineToRelative(-5.36f, 1.23f)
                lineToRelative(-36.54f, -16.26f)
                lineToRelative(-8.53f, -4.93f)
                lineToRelative(-5.74f, -56.91f)
                lineToRelative(1.31f, -8.31f)
                close()
            }
            path(
                fill = radialGradient(
                    0.152f to Color(0xE532AFE0), 0.963f to Color(0x0032AFE0),
                    center = Offset(69.46342f, 53.845097f), radius = 54.632507f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(64.52f, 51.89f)
                lineToRelative(0.97f, 63.54f)
                lineToRelative(43.41f, -17.51f)
                lineToRelative(6.48f, -4.55f)
                lineToRelative(-0.37f, -5.52f)
                lineToRelative(2.02f, -63.12f)
                lineToRelative(-6.61f, 1.66f)
                close()
            }
            path(
                fill = radialGradient(
                    0.256f to Color(0xE567CCF9),
                    0.416f to Color(0xAB6CCDF9),
                    0.616f to Color(0x637CD1F9),
                    0.837f to Color(0x1495D8F9),
                    0.895f to
                            Color(0x009DDAF9),
                    center = Offset(63.64822f, 49.48113f),
                    radius = 47.148094f
                ),
                stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(15.87f, 31.2f)
                lineToRelative(25.91f, 11.85f)
                lineToRelative(21.71f, 8.75f)
                lineToRelative(51.07f, -25.7f)
                lineToRelative(1.18f, -1.11f)
                lineTo(62.67f, 6.11f)
                lineTo(13.8f, 21.96f)
                lineToRelative(-4.96f, 3.59f)
                lineToRelative(0.14f, 3.17f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF37B5E1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(7.81f, 102.12f)
                curveToRelative(0.91f, 2.5f, 3.8f, 5.52f, 9.68f, 6.45f)
                curveToRelative(4.86f, 0.77f, 7.96f, -0.11f, 10.3f, -1.49f)
                curveToRelative(2.12f, -1.25f, 2.74f, -4.09f, 2.65f, -5.39f)
                curveToRelative(-0.12f, -1.63f, -1.83f, -4.04f, -3.8f, -5.05f)
                curveToRelative(-1.98f, -1.02f, -19.46f, 3.76f, -18.83f, 5.48f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0E3FD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 93.94f)
                curveToRelative(-3.24f, 1.88f, -6.55f, 6.11f, -3.61f, 9.05f)
                reflectiveCurveToRelative(13.82f, 3.71f, 18.0f, -0.1f)
                curveToRelative(4.19f, -3.8f, -0.77f, -7.89f, -4.14f, -9.39f)
                reflectiveCurveToRelative(-6.93f, -1.49f, -10.25f, 0.44f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFDFEFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(10.94f, 97.31f)
                curveToRelative(-1.22f, 0.08f, -3.09f, 2.86f, -0.14f, 4.91f)
                curveToRelative(3.66f, 2.55f, 8.76f, 1.01f, 8.42f, -0.53f)
                reflectiveCurveToRelative(-3.9f, -1.44f, -5.34f, -2.26f)
                curveToRelative(-1.45f, -0.82f, -1.55f, -2.22f, -2.94f, -2.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF37B5E1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.71f, 118.38f)
                curveToRelative(-1.01f, 2.27f, 0.05f, 4.89f, 4.66f, 6.03f)
                curveToRelative(7.22f, 1.78f, 11.83f, -0.8f, 13.37f, -2.29f)
                curveToRelative(1.54f, -1.49f, 1.73f, -3.85f, 0.91f, -4.62f)
                curveToRelative(-0.82f, -0.76f, -18.94f, 0.88f, -18.94f, 0.88f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0E4FF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(37.46f, 113.82f)
                curveToRelative(-5.82f, 0.3f, -8.46f, 3.15f, -8.7f, 4.5f)
                curveToRelative(-0.24f, 1.35f, 2.58f, 3.95f, 8.99f, 3.64f)
                curveToRelative(8.71f, -0.42f, 10.11f, -4.19f, 10.11f, -4.19f)
                reflectiveCurveToRelative(-2.07f, -4.38f, -10.4f, -3.95f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.78f, 116.32f)
                curveToRelative(-1.22f, -0.79f, -3.03f, -0.29f, -4.14f, 0.48f)
                curveToRelative(-0.89f, 0.62f, -1.34f, 2.07f, -0.72f, 2.84f)
                curveToRelative(1.01f, 1.25f, 3.03f, 0.87f, 4.24f, 0.1f)
                curveToRelative(1.19f, -0.77f, 1.96f, -2.55f, 0.62f, -3.42f)
                close()
                moveTo(26.41f, 21.75f)
                curveToRelative(-1.74f, -2.56f, -8.72f, 0.22f, -11.4f, 1.69f)
                reflectiveCurveToRelative(-4.25f, 3.33f, -3.98f, 5.4f)
                reflectiveCurveToRelative(3.76f, 3.11f, 7.58f, 4.8f)
                curveToRelative(3.82f, 1.69f, 32.87f, 14.9f, 35.06f, 16.08f)
                curveToRelative(3.12f, 1.69f, 6.47f, 3.86f, 7.47f, 10.41f)
                curveToRelative(0.65f, 4.31f, 2.44f, 45.69f, 2.51f, 47.11f)
                curveToRelative(0.16f, 3.36f, -0.13f, 8.23f, 1.9f, 8.15f)
                curveToRelative(1.78f, -0.07f, 1.42f, -4.5f, 1.37f, -8.21f)
                curveToRelative(-0.05f, -3.71f, 0.31f, -44.05f, 0.32f, -46.78f)
                curveToRelative(0.02f, -3.41f, 1.12f, -7.25f, 3.41f, -9.27f)
                reflectiveCurveToRelative(6.64f, -4.58f, 11.8f, -7.21f)
                curveToRelative(6.18f, -3.14f, 19.97f, -9.92f, 25.2f, -12.26f)
                reflectiveCurveToRelative(8.23f, -3.05f, 8.23f, -5.62f)
                reflectiveCurveToRelative(-11.89f, -6.11f, -13.3f, -6.22f)
                curveToRelative(-1.42f, -0.11f, -2.94f, 0.38f, -2.62f, 2.13f)
                curveToRelative(0.33f, 1.74f, 1.66f, 4.71f, -1.01f, 7.05f)
                reflectiveCurveToRelative(-8.45f, 5.52f, -11.75f, 7.18f)
                curveToRelative(-5.69f, 2.85f, -19.41f, 10.09f, -24.1f, 9.76f)
                curveToRelative(-4.69f, -0.33f, -7.63f, -1.15f, -14.67f, -4.09f)
                curveToRelative(-7.03f, -2.94f, -15.49f, -7.2f, -19.63f, -9.43f)
                curveToRelative(-2.94f, -1.59f, -4.96f, -3.93f, -4.8f, -6.38f)
                curveToRelative(0.18f, -2.44f, 3.92f, -2.08f, 2.41f, -4.29f)
                close()
                moveTo(43.75f, 15.86f)
                curveToRelative(0.67f, 1.73f, 6.71f, -0.05f, 9.6f, -0.93f)
                curveToRelative(2.89f, -0.87f, 7.85f, -2.45f, 10.8f, -2.62f)
                curveToRelative(2.94f, -0.16f, 12.81f, 2.84f, 15.32f, 3.33f)
                curveToRelative(2.51f, 0.49f, 4.69f, 0.6f, 5.23f, -0.27f)
                reflectiveCurveToRelative(-3.0f, -2.78f, -7.96f, -4.47f)
                reflectiveCurveTo(66.0f, 6.54f, 63.54f, 6.86f)
                curveToRelative(-2.54f, 0.34f, -7.99f, 2.83f, -12.32f, 4.36f)
                curveToRelative(-2.62f, 0.93f, -8.29f, 2.51f, -7.47f, 4.64f)
                close()
            }
        }
            .build()
        return _ice!!
    }

private var _ice: ImageVector? = null