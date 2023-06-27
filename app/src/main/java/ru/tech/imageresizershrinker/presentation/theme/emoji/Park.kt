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

val Emoji.Park: ImageVector
    get() {
        if (_park != null) {
            return _park!!
        }
        _park = Builder(
            name = "Park", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = radialGradient(
                    0.283f to Color(0xFFAFE4FE), 0.702f to Color(0xFF84D4F9),
                    0.965f to Color(0xFF67C9F6), center = Offset(48.378f, 86.785f), radius =
                    81.003f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(116.62f, 124.26f)
                horizontalLineTo(11.32f)
                curveToRelative(-4.15f, 0.0f, -7.52f, -3.37f, -7.52f, -7.52f)
                verticalLineTo(11.44f)
                curveToRelative(0.0f, -4.15f, 3.37f, -7.52f, 7.52f, -7.52f)
                horizontalLineToRelative(105.3f)
                curveToRelative(4.15f, 0.0f, 7.52f, 3.37f, 7.52f, 7.52f)
                verticalLineToRelative(105.3f)
                curveToRelative(0.01f, 4.15f, -3.36f, 7.52f, -7.52f, 7.52f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB4D218)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(124.15f, 76.05f)
                verticalLineTo(117.0f)
                curveToRelative(0.0f, 4.15f, -3.37f, 7.52f, -7.52f, 7.52f)
                horizontalLineTo(11.32f)
                curveToRelative(-4.15f, 0.0f, -7.52f, -3.37f, -7.52f, -7.52f)
                verticalLineTo(75.65f)
                reflectiveCurveToRelative(38.09f, -1.0f, 64.1f, -1.4f)
                curveToRelative(26.0f, -0.4f, 56.25f, 1.8f, 56.25f, 1.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00C1ED)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(106.1f, 124.49f)
                horizontalLineTo(11.32f)
                curveToRelative(-3.82f, 0.0f, -6.97f, -2.85f, -7.46f, -6.53f)
                curveToRelative(0.0f, 0.0f, 5.14f, -2.57f, 10.16f, -3.98f)
                curveToRelative(5.01f, -1.41f, 19.27f, -5.17f, 18.8f, -11.75f)
                curveToRelative(-0.47f, -6.58f, -13.85f, -6.2f, -13.0f, -13.47f)
                curveToRelative(0.38f, -3.27f, 6.07f, -6.89f, 12.55f, -9.07f)
                curveToRelative(8.4f, -2.83f, 18.21f, -3.73f, 18.3f, -3.58f)
                curveToRelative(0.1f, 0.18f, -3.22f, 4.78f, -1.81f, 9.95f)
                curveToRelative(1.41f, 5.17f, 7.88f, 8.44f, 19.94f, 11.1f)
                reflectiveCurveToRelative(24.03f, 5.23f, 31.08f, 14.16f)
                curveToRelative(6.86f, 8.69f, 6.22f, 13.17f, 6.22f, 13.17f)
                close()
            }
            path(
                fill = radialGradient(
                    0.307f to Color(0xFF8CDBFC), 0.412f to Color(0xFF54D1F5),
                    0.514f to Color(0xFF23C7EE), 0.564f to Color(0xFF10C4EC), center =
                    Offset(55.276f, 66.733f), radius = 114.301f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(7.73f, 123.58f)
                curveToRelative(1.07f, 0.58f, 2.29f, 0.91f, 3.59f, 0.91f)
                horizontalLineToRelative(88.24f)
                reflectiveCurveToRelative(-1.09f, -6.37f, -11.28f, -14.43f)
                curveToRelative(-6.74f, -5.33f, -13.39f, -6.77f, -20.76f, -8.49f)
                curveToRelative(-12.02f, -2.81f, -20.32f, -4.55f, -22.99f, -13.16f)
                curveToRelative(-1.58f, -5.12f, 0.66f, -7.95f, 0.35f, -8.42f)
                curveToRelative(-0.31f, -0.47f, -19.78f, 3.68f, -19.95f, 8.75f)
                curveToRelative(-0.17f, 5.27f, 12.82f, 4.26f, 12.77f, 13.42f)
                curveToRelative(-0.05f, 8.62f, -11.97f, 12.8f, -16.32f, 14.32f)
                curveToRelative(-9.4f, 3.3f, -13.65f, 7.1f, -13.65f, 7.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF79A5AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(3.8f, 75.39f)
                lineToRelative(49.26f, 0.04f)
                lineToRelative(-5.24f, -7.95f)
                reflectiveCurveToRelative(-2.43f, -0.84f, -6.36f, -5.24f)
                reflectiveCurveToRelative(-14.31f, -20.1f, -17.11f, -24.59f)
                reflectiveCurveToRelative(-7.01f, -9.35f, -10.94f, -9.16f)
                curveToRelative(-3.93f, 0.19f, -9.61f, 2.52f, -9.61f, 2.52f)
                verticalLineToRelative(44.38f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(3.79f, 26.33f)
                reflectiveCurveToRelative(2.35f, -0.63f, 4.9f, -0.49f)
                curveToRelative(3.55f, 0.19f, 7.56f, 3.31f, 7.56f, 3.31f)
                reflectiveCurveToRelative(-4.61f, -0.04f, -7.47f, 2.58f)
                curveTo(6.37f, 33.94f, 3.8f, 37.7f, 3.8f, 37.7f)
                lineToRelative(-0.01f, -11.37f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF86B4BB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.08f, 42.75f)
                lineTo(51.1f, 70.74f)
                lineToRelative(1.95f, 3.98f)
                lineToRelative(18.39f, -1.42f)
                lineToRelative(11.63f, -38.05f)
                lineToRelative(-8.93f, -2.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF3E727B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(74.51f, 48.83f)
                lineToRelative(-3.08f, 9.08f)
                lineTo(70.0f, 68.79f)
                lineToRelative(34.0f, 2.93f)
                reflectiveCurveToRelative(14.48f, -33.09f, 14.41f, -33.32f)
                curveToRelative(-0.08f, -0.23f, -15.68f, -22.74f, -15.68f, -22.74f)
                lineTo(90.72f, 19.9f)
                lineToRelative(-9.83f, 12.53f)
                lineToRelative(-6.38f, 16.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF79A5AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(124.14f, 20.58f)
                reflectiveCurveToRelative(-5.21f, -4.62f, -8.21f, -5.67f)
                curveToRelative(-3.0f, -1.05f, -14.63f, -1.58f, -14.63f, -1.58f)
                lineToRelative(-10.2f, 3.69f)
                lineToRelative(-8.78f, 10.21f)
                lineTo(77.06f, 39.0f)
                lineToRelative(-2.7f, 10.13f)
                reflectiveCurveToRelative(1.75f, 2.02f, 2.59f, -1.71f)
                curveToRelative(0.66f, -2.9f, 1.53f, -6.62f, 5.21f, -14.72f)
                reflectiveCurveToRelative(11.27f, -15.49f, 17.56f, -14.71f)
                curveToRelative(4.92f, 0.61f, 7.24f, 7.79f, 8.36f, 12.97f)
                curveToRelative(1.13f, 5.18f, 1.69f, 15.32f, 1.69f, 15.32f)
                lineToRelative(-7.28f, 3.9f)
                lineToRelative(-4.35f, 12.46f)
                reflectiveCurveToRelative(-0.23f, 1.73f, -0.6f, 4.2f)
                curveToRelative(-0.38f, 2.48f, -0.38f, 5.18f, -0.38f, 5.18f)
                lineToRelative(20.19f, 0.68f)
                lineToRelative(6.79f, -4.87f)
                verticalLineTo(20.58f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0DF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(51.18f, 68.23f)
                lineToRelative(3.15f, 0.45f)
                reflectiveCurveToRelative(2.48f, -6.23f, 3.6f, -9.08f)
                curveToRelative(1.13f, -2.85f, 2.03f, -5.1f, 2.85f, -6.0f)
                curveToRelative(0.83f, -0.9f, 3.08f, -1.13f, 3.68f, -2.1f)
                curveToRelative(0.6f, -0.98f, 2.85f, -8.25f, 4.5f, -10.96f)
                curveToRelative(1.65f, -2.7f, 3.68f, -5.1f, 5.4f, -5.78f)
                curveToRelative(1.73f, -0.68f, 3.08f, -0.23f, 3.08f, -0.23f)
                reflectiveCurveToRelative(-2.1f, 5.78f, -3.15f, 10.36f)
                reflectiveCurveToRelative(-2.85f, 13.02f, -2.85f, 13.02f)
                reflectiveCurveToRelative(1.8f, -0.86f, 2.55f, -1.91f)
                reflectiveCurveToRelative(0.93f, -2.66f, 1.14f, -4.12f)
                curveToRelative(1.01f, -7.08f, 2.28f, -10.63f, 3.16f, -13.6f)
                curveToRelative(1.16f, -3.91f, 4.0f, -9.65f, 5.36f, -11.86f)
                curveToRelative(3.08f, -5.0f, 5.29f, -6.87f, 7.99f, -8.74f)
                curveToRelative(2.7f, -1.88f, 7.54f, -3.9f, 13.1f, -3.6f)
                curveToRelative(5.55f, 0.3f, 14.05f, 2.38f, 14.05f, 2.38f)
                reflectiveCurveToRelative(-6.21f, -4.82f, -14.69f, -4.74f)
                reflectiveCurveToRelative(-14.13f, 3.47f, -18.63f, 8.65f)
                reflectiveCurveToRelative(-6.98f, 12.31f, -6.98f, 12.31f)
                reflectiveCurveToRelative(-2.63f, -1.73f, -5.93f, -0.98f)
                reflectiveCurveToRelative(-6.38f, 1.95f, -9.68f, 7.58f)
                reflectiveCurveToRelative(-7.43f, 17.71f, -7.43f, 17.71f)
                lineToRelative(-4.27f, 11.24f)
                close()
                moveTo(111.66f, 51.05f)
                reflectiveCurveToRelative(-4.65f, -1.2f, -7.13f, 0.6f)
                curveToRelative(-1.77f, 1.29f, -3.22f, 3.93f, -4.73f, 8.93f)
                curveToRelative(-1.37f, 4.53f, -2.06f, 5.1f, -2.1f, 4.97f)
                curveToRelative(-0.18f, -0.54f, 0.59f, -8.53f, 1.5f, -12.17f)
                curveToRelative(0.68f, -2.7f, 2.54f, -6.94f, 6.68f, -7.88f)
                curveToRelative(4.65f, -1.06f, 5.63f, 2.92f, 5.78f, 5.55f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF79A5AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.72f, 26.66f)
                curveToRelative(-1.27f, -0.1f, -1.59f, 1.27f, -1.64f, 5.04f)
                curveToRelative(-0.06f, 4.13f, -0.38f, 7.65f, -0.38f, 7.65f)
                reflectiveCurveToRelative(-3.08f, 4.42f, -4.05f, 7.27f)
                curveToRelative(-0.98f, 2.85f, -1.88f, 5.63f, -2.4f, 8.33f)
                curveToRelative(-0.39f, 2.0f, -0.92f, 4.71f, 0.3f, 5.18f)
                curveToRelative(1.58f, 0.6f, 3.15f, -6.53f, 4.58f, -10.96f)
                curveToRelative(1.52f, -4.73f, 4.8f, -8.78f, 4.8f, -8.78f)
                reflectiveCurveToRelative(0.09f, -4.51f, 0.0f, -8.19f)
                curveToRelative(-0.04f, -1.27f, 0.12f, -5.44f, -1.21f, -5.54f)
                close()
                moveTo(86.29f, 30.71f)
                curveToRelative(-1.13f, 0.14f, -1.49f, 1.94f, -1.58f, 4.2f)
                curveToRelative(-0.09f, 2.25f, -0.08f, 3.83f, -0.08f, 3.83f)
                reflectiveCurveToRelative(-1.58f, 3.38f, -1.95f, 4.88f)
                reflectiveCurveToRelative(0.83f, 2.18f, 1.88f, 0.9f)
                curveToRelative(1.05f, -1.28f, 3.3f, -5.48f, 3.3f, -5.48f)
                reflectiveCurveToRelative(0.08f, -3.0f, 0.08f, -4.2f)
                curveToRelative(0.0f, -1.2f, 0.23f, -4.35f, -1.65f, -4.13f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFACE4FE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(14.21f, 41.36f)
                curveToRelative(-0.1f, 0.0f, 0.42f, 1.2f, 0.71f, 2.77f)
                curveToRelative(0.36f, 1.93f, 0.73f, 6.36f, 0.85f, 13.4f)
                reflectiveCurveToRelative(0.47f, 14.6f, 0.47f, 14.6f)
                lineToRelative(5.12f, -1.51f)
                reflectiveCurveToRelative(-0.17f, -10.65f, -0.76f, -16.17f)
                reflectiveCurveToRelative(-1.42f, -8.35f, -2.91f, -10.75f)
                curveToRelative(-0.71f, -1.14f, -2.56f, -2.36f, -3.48f, -2.34f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFCFDFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(16.78f, 50.62f)
                curveToRelative(0.93f, -0.12f, 0.92f, 1.74f, 0.99f, 4.83f)
                curveToRelative(0.06f, 2.68f, 0.17f, 4.3f, -0.47f, 4.36f)
                curveToRelative(-0.87f, 0.08f, -0.75f, -0.75f, -0.93f, -5.0f)
                curveToRelative(-0.12f, -2.73f, -0.27f, -4.1f, 0.41f, -4.19f)
                close()
                moveTo(18.86f, 52.47f)
                curveToRelative(-0.58f, 0.13f, -0.23f, 2.03f, -0.12f, 5.29f)
                curveToRelative(0.12f, 3.49f, -0.12f, 5.7f, 0.99f, 5.7f)
                curveToRelative(1.05f, 0.0f, 0.58f, -2.09f, 0.47f, -5.93f)
                curveToRelative(-0.11f, -3.37f, -0.3f, -5.29f, -1.34f, -5.06f)
                close()
                moveTo(15.6f, 72.37f)
                reflectiveCurveToRelative(1.11f, -1.63f, 2.5f, -1.92f)
                curveToRelative(1.4f, -0.29f, 1.92f, -0.12f, 1.92f, -0.12f)
                reflectiveCurveToRelative(0.81f, -2.33f, 3.9f, -2.27f)
                curveToRelative(3.08f, 0.06f, 4.13f, 4.36f, 4.13f, 4.36f)
                lineToRelative(-12.39f, 1.63f)
                lineToRelative(-0.06f, -1.68f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0A7E1D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.24f, 76.21f)
                reflectiveCurveToRelative(24.82f, -0.65f, 39.97f, -0.52f)
                curveToRelative(15.15f, 0.13f, 31.22f, 0.13f, 31.22f, 0.13f)
                reflectiveCurveToRelative(-8.62f, -17.24f, -9.28f, -17.11f)
                curveToRelative(-0.65f, 0.13f, -5.75f, 9.01f, -5.75f, 9.01f)
                reflectiveCurveToRelative(-5.55f, -9.64f, -6.07f, -9.9f)
                reflectiveCurveToRelative(-6.35f, 13.07f, -6.35f, 13.07f)
                lineToRelative(-2.53f, -3.92f)
                lineToRelative(-2.98f, 4.75f)
                lineToRelative(-3.88f, -8.43f)
                lineToRelative(-6.92f, 9.67f)
                reflectiveCurveToRelative(-1.37f, -0.43f, -3.07f, -0.51f)
                curveToRelative(-2.56f, -0.12f, -5.55f, 1.29f, -5.55f, 1.29f)
                reflectiveCurveToRelative(-5.49f, -1.83f, -9.28f, -1.83f)
                reflectiveCurveToRelative(-14.63f, 3.0f, -14.63f, 3.0f)
                lineToRelative(5.1f, 1.3f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF02AB47)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(50.49f, 61.52f)
                curveToRelative(0.58f, 0.14f, 1.58f, 2.82f, 1.58f, 2.82f)
                lineTo(48.11f, 74.0f)
                lineToRelative(-3.45f, -1.01f)
                curveToRelative(0.01f, 0.0f, 5.19f, -11.63f, 5.83f, -11.47f)
                close()
                moveTo(55.46f, 71.75f)
                lineToRelative(1.02f, 1.97f)
                lineToRelative(2.82f, -5.43f)
                reflectiveCurveToRelative(-0.85f, -1.57f, -1.04f, -1.56f)
                curveToRelative(-0.4f, 0.03f, -2.8f, 5.02f, -2.8f, 5.02f)
                close()
                moveTo(67.37f, 57.72f)
                curveToRelative(-1.09f, 0.05f, -6.39f, 13.14f, -6.39f, 13.14f)
                reflectiveCurveToRelative(1.4f, 2.04f, 1.52f, 2.06f)
                curveToRelative(0.76f, 0.11f, 5.95f, -13.53f, 5.95f, -13.53f)
                reflectiveCurveToRelative(-0.81f, -1.69f, -1.08f, -1.67f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0A7E1D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(82.64f, 78.76f)
                reflectiveCurveToRelative(-0.46f, 1.19f, 0.28f, 1.38f)
                reflectiveCurveToRelative(6.25f, 1.01f, 15.15f, 0.64f)
                curveToRelative(8.91f, -0.37f, 26.07f, -0.73f, 26.07f, -0.73f)
                lineToRelative(0.01f, -12.22f)
                reflectiveCurveToRelative(-7.35f, -16.99f, -8.27f, -16.81f)
                curveToRelative(-0.92f, 0.18f, -7.29f, 15.14f, -7.29f, 15.14f)
                reflectiveCurveToRelative(-2.58f, -4.72f, -3.03f, -4.9f)
                curveToRelative(-0.46f, -0.18f, -7.68f, 8.78f, -7.68f, 8.78f)
                reflectiveCurveToRelative(-4.68f, -13.32f, -5.42f, -13.41f)
                curveToRelative(-0.73f, -0.09f, -9.82f, 22.13f, -9.82f, 22.13f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF02AB47)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(91.41f, 54.25f)
                curveToRelative(-0.82f, 0.0f, -6.85f, 14.14f, -7.68f, 16.16f)
                curveToRelative(-0.83f, 2.02f, -3.65f, 8.57f, -3.28f, 9.12f)
                curveToRelative(0.37f, 0.55f, 3.8f, 0.82f, 3.8f, 0.82f)
                reflectiveCurveToRelative(3.89f, -11.32f, 5.54f, -15.0f)
                curveToRelative(1.65f, -3.67f, 3.51f, -7.31f, 3.51f, -7.31f)
                reflectiveCurveToRelative(-1.04f, -3.79f, -1.89f, -3.79f)
                close()
                moveTo(104.13f, 59.76f)
                curveToRelative(0.49f, -0.25f, 2.11f, 2.39f, 2.11f, 2.39f)
                reflectiveCurveToRelative(-6.15f, 10.65f, -6.61f, 10.84f)
                curveToRelative(-0.46f, 0.18f, -1.75f, -2.94f, -1.75f, -2.94f)
                reflectiveCurveToRelative(5.14f, -9.74f, 6.25f, -10.29f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0A7E1D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(3.81f, 112.79f)
                reflectiveCurveToRelative(4.02f, 0.57f, 7.08f, -1.55f)
                curveToRelative(4.41f, -3.06f, 4.66f, -6.34f, 4.66f, -6.34f)
                reflectiveCurveToRelative(1.17f, 1.64f, 5.73f, 1.73f)
                curveToRelative(4.56f, 0.09f, 6.52f, -3.79f, 6.42f, -4.32f)
                curveToRelative(-0.05f, -0.26f, -4.54f, -5.73f, -4.54f, -5.73f)
                lineTo(17.9f, 85.99f)
                lineToRelative(0.61f, -2.57f)
                lineToRelative(-7.7f, -10.29f)
                lineToRelative(-3.04f, -17.9f)
                reflectiveCurveToRelative(-0.81f, -1.7f, -1.34f, -1.25f)
                reflectiveCurveToRelative(-2.6f, 4.48f, -2.6f, 4.48f)
                lineToRelative(-0.02f, 54.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00AA48)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.52f, 87.45f)
                curveToRelative(-1.78f, -0.18f, -3.49f, -2.69f, -3.85f, -8.95f)
                curveToRelative(-0.36f, -6.27f, 0.36f, -16.47f, -0.09f, -19.24f)
                curveToRelative(-0.45f, -2.77f, -1.16f, -5.28f, -1.16f, -5.28f)
                reflectiveCurveToRelative(0.36f, -0.9f, 1.07f, 0.09f)
                reflectiveCurveToRelative(4.48f, 7.52f, 6.27f, 11.99f)
                curveToRelative(1.79f, 4.48f, 7.84f, 15.23f, 8.77f, 16.83f)
                curveToRelative(1.62f, 2.78f, 2.87f, 5.13f, 0.72f, 5.22f)
                curveToRelative(-2.15f, 0.09f, -5.11f, -1.82f, -5.11f, -1.82f)
                reflectiveCurveToRelative(3.58f, 7.43f, 4.65f, 8.95f)
                reflectiveCurveToRelative(4.12f, 6.0f, 4.12f, 6.0f)
                reflectiveCurveToRelative(-0.54f, 1.79f, -3.4f, 0.98f)
                reflectiveCurveToRelative(-6.21f, -8.18f, -6.98f, -10.16f)
                curveToRelative(-0.75f, -1.93f, -0.85f, -3.65f, -0.75f, -4.47f)
                curveToRelative(0.18f, -1.52f, 1.47f, -1.84f, 1.47f, -1.84f)
                reflectiveCurveToRelative(-1.43f, -1.52f, -2.15f, -2.69f)
                curveToRelative(-0.72f, -1.16f, -1.52f, -2.77f, -1.52f, -2.77f)
                reflectiveCurveToRelative(0.63f, 7.43f, -2.06f, 7.16f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0A7E1D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(117.4f, 76.89f)
                reflectiveCurveToRelative(-18.44f, 5.46f, -19.15f, 6.27f)
                curveToRelative(-0.72f, 0.81f, -0.81f, 2.33f, 0.9f, 3.04f)
                reflectiveCurveToRelative(2.58f, 0.2f, 4.21f, 1.25f)
                curveToRelative(0.65f, 0.42f, -0.85f, 4.82f, -0.85f, 4.82f)
                reflectiveCurveToRelative(-7.75f, 2.53f, -7.88f, 3.65f)
                curveToRelative(-0.16f, 1.4f, 0.85f, 2.72f, 2.64f, 3.61f)
                curveToRelative(1.6f, 0.8f, 3.91f, 0.92f, 3.91f, 0.92f)
                reflectiveCurveToRelative(-9.01f, 6.84f, -8.57f, 8.54f)
                curveToRelative(0.45f, 1.7f, 2.38f, 4.53f, 6.71f, 4.42f)
                curveToRelative(3.94f, -0.1f, 4.56f, -1.52f, 6.71f, 0.09f)
                curveToRelative(2.15f, 1.61f, 2.07f, 5.31f, 7.88f, 4.92f)
                curveToRelative(4.03f, -0.27f, 5.02f, -2.82f, 7.07f, -3.85f)
                curveToRelative(1.65f, -0.82f, 3.19f, -0.93f, 3.19f, -0.93f)
                lineToRelative(-0.03f, -33.6f)
                lineToRelative(-6.74f, -3.15f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF02AB47)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(115.34f, 62.57f)
                curveToRelative(-2.06f, 0.12f, -3.13f, 5.28f, -7.25f, 10.29f)
                reflectiveCurveTo(98.21f, 83.2f, 98.21f, 83.2f)
                reflectiveCurveToRelative(0.48f, 1.22f, 1.92f, 1.21f)
                curveToRelative(2.06f, -0.02f, 3.67f, -1.88f, 5.28f, -1.25f)
                curveToRelative(1.61f, 0.63f, 1.34f, 3.4f, 3.85f, 3.58f)
                curveToRelative(2.51f, 0.18f, 5.91f, -3.13f, 8.14f, -3.22f)
                curveToRelative(2.24f, -0.09f, 2.24f, 2.24f, 4.39f, 2.15f)
                curveToRelative(2.15f, -0.09f, 2.36f, -1.08f, 2.36f, -1.08f)
                verticalLineToRelative(-6.82f)
                reflectiveCurveToRelative(-3.34f, -4.37f, -4.69f, -8.22f)
                curveToRelative(-1.34f, -3.85f, -2.59f, -7.07f, -4.12f, -6.98f)
                close()
                moveTo(109.35f, 90.41f)
                curveToRelative(3.04f, 0.08f, 5.82f, -2.77f, 9.58f, -2.42f)
                curveToRelative(3.76f, 0.36f, 3.94f, 4.56f, 3.49f, 6.27f)
                curveToRelative(-0.45f, 1.7f, -1.52f, 4.65f, -4.3f, 4.65f)
                curveToRelative(-2.77f, 0.0f, -2.86f, -1.61f, -4.56f, -1.43f)
                curveToRelative(-1.7f, 0.18f, -2.86f, 1.88f, -5.01f, 1.61f)
                reflectiveCurveToRelative(-3.13f, -2.33f, -4.74f, -2.6f)
                reflectiveCurveToRelative(-4.21f, 1.25f, -6.27f, 1.16f)
                curveToRelative(-2.06f, -0.09f, -2.93f, -1.81f, -2.93f, -1.81f)
                reflectiveCurveToRelative(0.42f, -1.5f, 1.85f, -2.3f)
                reflectiveCurveToRelative(2.6f, -1.34f, 4.48f, -3.13f)
                reflectiveCurveToRelative(2.41f, -2.95f, 2.41f, -2.95f)
                reflectiveCurveToRelative(2.69f, 2.86f, 6.0f, 2.95f)
                close()
                moveTo(105.05f, 108.66f)
                curveToRelative(3.11f, 0.0f, 2.51f, 4.48f, 6.35f, 5.19f)
                curveToRelative(3.85f, 0.72f, 9.04f, -7.07f, 6.27f, -11.1f)
                curveToRelative(-2.77f, -4.03f, -6.27f, 0.81f, -9.93f, -0.09f)
                curveToRelative(-3.67f, -0.9f, -3.67f, -2.6f, -6.44f, -2.51f)
                curveToRelative(-2.77f, 0.09f, -8.53f, 5.44f, -8.68f, 8.41f)
                curveToRelative(-0.09f, 1.79f, 2.77f, 2.66f, 5.55f, 2.24f)
                curveToRelative(2.76f, -0.41f, 5.45f, -2.14f, 6.88f, -2.14f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(20.34f, 16.11f)
                curveToRelative(0.07f, 3.53f, 5.69f, 4.39f, 14.98f, 4.47f)
                curveToRelative(9.29f, 0.07f, 15.12f, -0.72f, 14.98f, -4.18f)
                reflectiveCurveToRelative(-10.73f, -4.1f, -16.2f, -4.32f)
                curveToRelative(-5.47f, -0.22f, -13.83f, 0.43f, -13.76f, 4.03f)
                close()
                moveTo(42.44f, 29.83f)
                curveToRelative(0.46f, 2.71f, 4.32f, 2.36f, 9.87f, 2.46f)
                curveToRelative(6.19f, 0.12f, 11.0f, 0.62f, 11.39f, -2.68f)
                curveToRelative(0.39f, -3.31f, -7.69f, -2.97f, -10.93f, -2.93f)
                curveToRelative(-3.25f, 0.05f, -10.88f, -0.11f, -10.33f, 3.15f)
                close()
            }
        }
            .build()
        return _park!!
    }

private var _park: ImageVector? = null
