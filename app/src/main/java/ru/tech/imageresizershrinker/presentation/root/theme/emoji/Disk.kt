package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Disk: ImageVector
    get() {
        if (_disk != null) {
            return _disk!!
        }
        _disk = Builder(
            name = "Disk", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFB9E4EA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(54.79f, 7.76f)
                curveTo(23.76f, 12.84f, 2.67f, 42.18f, 7.76f, 73.21f)
                reflectiveCurveToRelative(34.43f, 52.11f, 65.46f, 47.03f)
                reflectiveCurveToRelative(52.11f, -34.43f, 47.03f, -65.46f)
                curveTo(115.16f, 23.76f, 85.82f, 2.67f, 54.79f, 7.76f)
                close()
                moveTo(66.3f, 78.06f)
                curveToRelative(-7.78f, 1.27f, -15.09f, -3.98f, -16.36f, -11.76f)
                curveToRelative(-1.27f, -7.78f, 3.98f, -15.09f, 11.76f, -16.36f)
                curveToRelative(7.78f, -1.27f, 15.09f, 3.98f, 16.36f, 11.76f)
                curveToRelative(1.37f, 7.76f, -3.88f, 15.07f, -11.76f, 16.36f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x99FFF9C4), 0.95f to Color(0x00FFF9C4), start
                    = Offset(34.657f, 44.491f), end = Offset(31.029f, 50.352f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(10.77f, 43.61f)
                lineToRelative(39.28f, 17.38f)
                reflectiveCurveToRelative(0.37f, -1.46f, 1.05f, -3.05f)
                curveToRelative(0.68f, -1.59f, 1.77f, -2.87f, 1.77f, -2.87f)
                lineTo(17.9f, 30.45f)
                reflectiveCurveToRelative(-1.97f, 2.69f, -4.04f, 6.43f)
                curveToRelative(-2.06f, 3.75f, -3.09f, 6.73f, -3.09f, 6.73f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x99FFF9C4), 0.989f to Color(0x01FFF9C4), 1.0f
                            to Color(0x00FFF9C4), start = Offset(38.141f, 40.904f), end =
                    Offset(48.932f, 31.695f)
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap =
                Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(62.53f, 49.83f)
                lineTo(44.8f, 10.33f)
                reflectiveCurveToRelative(-7.16f, 2.24f, -14.78f, 7.91f)
                curveTo(22.61f, 23.76f, 17.9f, 30.45f, 17.9f, 30.45f)
                lineToRelative(34.97f, 24.62f)
                reflectiveCurveToRelative(1.33f, -1.82f, 4.05f, -3.45f)
                reflectiveCurveToRelative(5.61f, -1.79f, 5.61f, -1.79f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xCCFFC2C8), 0.93f to Color(0x00FFCDD2), start
                    = Offset(29.82969f, 54.09692f), end = Offset(28.455832f, 61.40844f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(7.35f, 57.71f)
                lineToRelative(42.4f, 7.51f)
                reflectiveCurveToRelative(-0.03f, -1.23f, 0.07f, -2.69f)
                curveToRelative(0.11f, -1.46f, 0.46f, -2.42f, 0.46f, -2.42f)
                lineTo(10.07f, 45.57f)
                reflectiveCurveToRelative(-0.92f, 2.29f, -1.71f, 5.83f)
                curveToRelative(-0.79f, 3.54f, -1.01f, 6.31f, -1.01f, 6.31f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xCCFFC2C8), 0.93f to Color(0x00FFCDD2), start
                    = Offset(30.630882f, 50.436768f), end = Offset(38.62138f, 38.657455f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(51.94f, 56.39f)
                curveToRelative(0.94f, -1.59f, 2.28f, -2.77f, 2.28f, -2.77f)
                lineTo(21.99f, 25.51f)
                reflectiveCurveToRelative(-3.63f, 3.88f, -6.87f, 9.32f)
                curveToRelative(-3.71f, 6.23f, -5.05f, 10.74f, -5.05f, 10.74f)
                lineToRelative(40.21f, 14.54f)
                reflectiveCurveToRelative(0.52f, -1.78f, 1.66f, -3.72f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7F80DEEA), 0.926f to Color(0x0080DEEA), start
                    = Offset(45.492f, 33.598f), end = Offset(39.352f, 38.202f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(28.4f, 19.46f)
                curveToRelative(-3.89f, 3.05f, -6.94f, 6.57f, -6.94f, 6.57f)
                lineTo(52.8f, 55.09f)
                reflectiveCurveToRelative(1.12f, -1.3f, 2.6f, -2.45f)
                curveToRelative(1.48f, -1.15f, 3.05f, -1.77f, 3.05f, -1.77f)
                lineTo(36.92f, 13.84f)
                reflectiveCurveToRelative(-4.64f, 2.57f, -8.52f, 5.62f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7F80DEEA), 0.947f to Color(0x0080DEEA), start
                    = Offset(53.405f, 31.356f), end = Offset(63.451f, 28.659f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(69.07f, 50.68f)
                lineTo(60.28f, 7.13f)
                reflectiveCurveToRelative(-5.7f, 0.33f, -11.57f, 1.96f)
                curveToRelative(-6.16f, 1.72f, -11.8f, 4.75f, -11.8f, 4.75f)
                lineToRelative(21.53f, 37.01f)
                reflectiveCurveToRelative(2.48f, -1.09f, 5.38f, -1.11f)
                reflectiveCurveToRelative(5.25f, 0.94f, 5.25f, 0.94f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7FFFFFFF), 0.907f to Color(0x00FFFFFF), start
                    = Offset(96.534f, 49.734f), end = Offset(99.371f, 60.432f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(114.72f, 38.01f)
                lineTo(75.94f, 55.93f)
                curveToRelative(3.67f, 5.3f, 2.34f, 10.38f, 2.34f, 10.38f)
                lineToRelative(42.58f, -6.2f)
                curveToRelative(-0.48f, -9.19f, -3.76f, -17.46f, -6.14f, -22.1f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7FFFFFFF), 0.907f to Color(0x00FFFFFF), start
                    = Offset(93.391f, 45.481f), end = Offset(91.112f, 41.481f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(103.42f, 22.86f)
                lineTo(71.37f, 51.81f)
                curveToRelative(1.72f, 1.04f, 3.41f, 2.44f, 4.57f, 4.12f)
                lineToRelative(38.78f, -17.92f)
                curveToRelative(-1.92f, -3.67f, -5.78f, -9.97f, -11.3f, -15.15f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7FFFFFFF), 0.907f to Color(0x00FFFFFF), start
                    = Offset(36.143f, 84.388f), end = Offset(37.352f, 86.341f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(52.95f, 72.99f)
                lineTo(15.85f, 94.5f)
                arcToRelative(57.09f, 57.09f, 0.0f, false, false, 11.8f, 13.35f)
                lineTo(58.1f, 76.98f)
                curveToRelative(-1.99f, -0.91f, -3.75f, -2.28f, -5.15f, -3.99f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7FFFFFFF), 0.907f to Color(0x00FFFFFF), start
                    = Offset(32.583f, 80.69f), end = Offset(28.862f, 69.621f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(49.94f, 66.3f)
                arcToRelative(13.65f, 13.65f, 0.0f, false, true, -0.15f, -3.18f)
                lineTo(7.62f, 72.21f)
                curveToRelative(0.05f, 0.33f, 0.08f, 0.67f, 0.14f, 1.0f)
                curveToRelative(1.28f, 7.81f, 4.1f, 15.0f, 8.1f, 21.29f)
                lineToRelative(37.1f, -21.51f)
                curveToRelative(-1.53f, -1.86f, -2.6f, -4.13f, -3.02f, -6.69f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x99FFF9C4), 0.95f to Color(0x00FFF9C4), start
                    = Offset(93.52714f, 82.98142f), end = Offset(97.056755f, 77.061646f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(117.16f, 83.31f)
                lineTo(78.02f, 67.03f)
                reflectiveCurveToRelative(-0.34f, 1.46f, -1.0f, 3.07f)
                reflectiveCurveToRelative(-1.73f, 2.9f, -1.73f, 2.9f)
                lineToRelative(34.96f, 23.58f)
                reflectiveCurveToRelative(1.93f, -2.72f, 3.93f, -6.5f)
                reflectiveCurveToRelative(2.98f, -6.77f, 2.98f, -6.77f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x99FFF9C4), 0.989f to Color(0x01FFF9C4), 1.0f
                            to Color(0x00FFF9C4), start = Offset(90.05286f, 86.70597f), end =
                    Offset(79.418175f, 96.0939f)
                ), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(65.72f, 78.4f)
                lineToRelative(17.96f, 38.75f)
                reflectiveCurveToRelative(7.12f, -2.36f, 14.65f, -8.16f)
                curveToRelative(7.33f, -5.65f, 11.93f, -12.42f, 11.93f, -12.42f)
                lineTo(75.3f, 72.99f)
                reflectiveCurveToRelative(-1.3f, 1.84f, -3.99f, 3.51f)
                curveToRelative(-2.7f, 1.68f, -5.59f, 1.9f, -5.59f, 1.9f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xCCFFC2C8), 0.93f to Color(0x00FFCDD2), start
                    = Offset(98.268814f, 73.197105f), end = Offset(99.51959f, 65.86259f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(120.35f, 69.15f)
                lineToRelative(-41.88f, -6.44f)
                reflectiveCurveToRelative(0.05f, 1.23f, -0.03f, 2.69f)
                reflectiveCurveToRelative(-0.42f, 2.43f, -0.42f, 2.43f)
                lineToRelative(39.81f, 13.51f)
                reflectiveCurveToRelative(0.88f, -2.3f, 1.61f, -5.86f)
                curveToRelative(0.74f, -3.56f, 0.91f, -6.33f, 0.91f, -6.33f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xCCFFC2C8), 0.93f to Color(0x00FFCDD2), start
                    = Offset(97.50697f, 77.01969f), end = Offset(89.71422f, 88.92969f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(76.46f, 71.57f)
                curveToRelative(-0.91f, 1.61f, -2.23f, 2.81f, -2.23f, 2.81f)
                lineToRelative(32.03f, 27.21f)
                reflectiveCurveToRelative(3.56f, -3.94f, 6.71f, -9.43f)
                curveToRelative(3.61f, -6.29f, 4.87f, -10.82f, 4.87f, -10.82f)
                lineTo(78.05f, 67.83f)
                reflectiveCurveToRelative(-0.48f, 1.78f, -1.59f, 3.74f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7F80DEEA), 0.926f to Color(0x0080DEEA), start
                    = Offset(82.70892f, 94.01565f), end = Offset(88.77015f, 89.308754f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(99.94f, 107.75f)
                curveToRelative(3.84f, -3.11f, 6.83f, -6.69f, 6.83f, -6.69f)
                lineToRelative(-31.6f, -28.07f)
                reflectiveCurveToRelative(-0.82f, 1.19f, -2.28f, 2.37f)
                curveToRelative(-1.46f, 1.17f, -3.52f, 1.85f, -3.52f, 1.85f)
                lineToRelative(22.15f, 36.29f)
                reflectiveCurveToRelative(4.59f, -2.64f, 8.42f, -5.75f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x7F80DEEA), 0.947f to Color(0x0080DEEA), start
                    = Offset(74.79534f, 96.44216f), end = Offset(64.79581f, 99.30757f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(58.75f, 77.57f)
                lineToRelative(9.51f, 43.04f)
                reflectiveCurveToRelative(5.7f, -0.42f, 11.53f, -2.16f)
                curveToRelative(6.13f, -1.82f, 11.72f, -4.95f, 11.72f, -4.95f)
                lineTo(69.37f, 77.21f)
                reflectiveCurveToRelative(-2.46f, 1.13f, -5.36f, 1.2f)
                curveToRelative(-2.9f, 0.07f, -5.26f, -0.84f, -5.26f, -0.84f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF6C9EB4)), stroke = null, fillAlpha = 0.4f, strokeAlpha
                = 0.4f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.0f, 41.2f)
                curveToRelative(-12.77f, 0.0f, -23.04f, 10.16f, -23.04f, 22.8f)
                reflectiveCurveTo(51.23f, 86.8f, 64.0f, 86.8f)
                reflectiveCurveTo(87.04f, 76.63f, 87.04f, 64.0f)
                reflectiveCurveTo(76.77f, 41.2f, 64.0f, 41.2f)
                close()
                moveTo(64.0f, 78.15f)
                curveToRelative(-7.97f, 0.0f, -14.4f, -6.36f, -14.4f, -14.25f)
                reflectiveCurveTo(56.03f, 49.64f, 64.0f, 49.65f)
                curveToRelative(7.97f, 0.01f, 14.4f, 6.36f, 14.4f, 14.25f)
                curveToRelative(0.09f, 7.89f, -6.34f, 14.25f, -14.4f, 14.25f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 9.86f)
                curveToRelative(29.83f, 0.0f, 54.14f, 24.32f, 54.14f, 54.14f)
                reflectiveCurveTo(93.83f, 118.14f, 64.0f, 118.14f)
                reflectiveCurveTo(9.86f, 93.83f, 9.86f, 64.0f)
                reflectiveCurveTo(34.17f, 9.86f, 64.0f, 9.86f)
                moveToRelative(0.0f, 71.24f)
                curveToRelative(9.4f, 0.0f, 17.1f, -7.69f, 17.1f, -17.1f)
                reflectiveCurveTo(73.4f, 46.9f, 64.0f, 46.9f)
                reflectiveCurveTo(46.9f, 54.6f, 46.9f, 64.0f)
                curveToRelative(0.0f, 9.4f, 7.7f, 17.1f, 17.1f, 17.1f)
                moveToRelative(0.0f, -74.09f)
                curveTo(32.56f, 7.01f, 7.01f, 32.56f, 7.01f, 64.0f)
                reflectiveCurveTo(32.56f, 120.99f, 64.0f, 120.99f)
                reflectiveCurveTo(120.99f, 95.44f, 120.99f, 64.0f)
                reflectiveCurveTo(95.44f, 7.01f, 64.0f, 7.01f)
                close()
                moveTo(64.0f, 78.25f)
                curveToRelative(-7.88f, 0.0f, -14.25f, -6.36f, -14.25f, -14.25f)
                reflectiveCurveTo(56.12f, 49.75f, 64.0f, 49.75f)
                reflectiveCurveTo(78.25f, 56.12f, 78.25f, 64.0f)
                curveToRelative(0.09f, 7.88f, -6.27f, 14.25f, -14.25f, 14.25f)
                close()
            }
        }
            .build()
        return _disk!!
    }

private var _disk: ImageVector? = null
