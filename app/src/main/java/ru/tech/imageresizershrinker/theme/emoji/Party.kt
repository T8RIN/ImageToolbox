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

public val Emoji.Party: ImageVector
    get() {
        if (_party != null) {
            return _party!!
        }
        _party = Builder(name = "Party", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = SolidColor(Color(0xFFFFC107)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(7.45f, 123.27f)
                curveToRelative(2.27f, 2.46f, 11.62f, -1.83f, 19.0f, -5.27f)
                curveToRelative(5.53f, -2.57f, 27.66f, -11.65f, 38.66f, -16.36f)
                curveToRelative(2.97f, -1.27f, 7.29f, -2.93f, 10.4f, -7.02f)
                curveToRelative(2.76f, -3.64f, 10.08f, -19.1f, -4.66f, -34.76f)
                curveToRelative(-14.96f, -15.9f, -30.37f, -11.51f, -36.13f, -7.43f)
                curveToRelative(-3.39f, 2.4f, -6.15f, 7.81f, -7.39f, 10.56f)
                curveToRelative(-5.24f, 11.62f, -12.71f, 32.91f, -15.75f, 41.28f)
                curveToRelative(-2.23f, 6.17f, -6.38f, 16.56f, -4.13f, 19.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFF8F00)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(25.85f, 66.49f)
                curveToRelative(0.14f, 1.74f, 0.49f, 4.57f, 1.69f, 10.02f)
                curveToRelative(0.82f, 3.74f, 2.16f, 7.66f, 3.25f, 10.25f)
                curveToRelative(3.27f, 7.79f, 7.86f, 10.93f, 12.51f, 13.45f)
                curveToRelative(7.9f, 4.28f, 13.27f, 5.08f, 13.27f, 5.08f)
                lineToRelative(-6.44f, 2.63f)
                reflectiveCurveToRelative(-3.9f, -0.81f, -9.22f, -3.43f)
                curveToRelative(-5.07f, -2.5f, -10.35f, -6.73f, -14.21f, -15.01f)
                curveToRelative(-1.67f, -3.59f, -2.64f, -7.07f, -3.2f, -9.83f)
                curveToRelative(-0.69f, -3.42f, -0.8f, -5.36f, -0.8f, -5.36f)
                lineToRelative(3.15f, -7.8f)
                close()
                moveTo(17.94f, 86.77f)
                reflectiveCurveToRelative(0.8f, 6.49f, 6.16f, 14.68f)
                curveToRelative(6.28f, 9.58f, 15.05f, 11.15f, 15.05f, 11.15f)
                lineToRelative(-5.83f, 2.4f)
                reflectiveCurveToRelative(-6.51f, -1.99f, -12.7f, -10.44f)
                curveToRelative(-3.86f, -5.27f, -4.94f, -11.57f, -4.94f, -11.57f)
                lineToRelative(2.26f, -6.22f)
                close()
                moveTo(12.39f, 102.15f)
                reflectiveCurveToRelative(1.46f, 5.6f, 4.66f, 9.78f)
                curveToRelative(3.81f, 4.99f, 8.66f, 6.44f, 8.66f, 6.44f)
                lineToRelative(-4.47f, 1.98f)
                reflectiveCurveToRelative(-3.39f, -0.71f, -7.1f, -5.41f)
                curveToRelative(-2.82f, -3.57f, -3.62f, -7.67f, -3.62f, -7.67f)
                lineToRelative(1.87f, -5.12f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFDE7)), stroke = null, fillAlpha = 0.44f, strokeAlpha
                    = 0.44f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(9.96f, 116.37f)
                curveToRelative(-0.2f, -0.45f, -0.2f, -0.96f, 0.01f, -1.4f)
                lineToRelative(25.47f, -52.82f)
                lineToRelative(4.19f, 15.75f)
                lineToRelative(-26.8f, 38.71f)
                curveToRelative(-0.72f, 1.08f, -2.34f, 0.94f, -2.87f, -0.24f)
                close()
            }
            path(fill = linearGradient(0.024f to Color(0xFF8F4700), 1.0f to Color(0xFF703E2D), start
                    = Offset(74.384f,61.839f), end = Offset(44.617f,79.699f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(41.65f, 83.19f)
                curveToRelative(11.9f, 13.92f, 25.45f, 12.18f, 29.96f, 8.66f)
                curveToRelative(4.52f, -3.53f, 8.09f, -15.66f, -3.76f, -29.35f)
                curveToRelative(-12.42f, -14.34f, -26.48f, -10.25f, -29.73f, -7.15f)
                reflectiveCurveToRelative(-7.39f, 15.07f, 3.53f, 27.84f)
                close()
            }
            path(fill = SolidColor(Color(0xFF03A9F4)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(82.52f, 88.92f)
                curveToRelative(-4.34f, -3.64f, -6.65f, -2.99f, -9.75f, -1.7f)
                curveToRelative(-4.0f, 1.66f, -10.29f, 2.89f, -18.83f, 0.0f)
                lineToRelative(2.57f, -6.19f)
                curveToRelative(5.07f, 1.71f, 8.74f, 0.88f, 11.91f, -0.99f)
                curveToRelative(4.08f, -2.4f, 9.66f, -5.69f, 18.34f, 1.6f)
                curveToRelative(3.62f, 3.04f, 7.33f, 5.06f, 10.05f, 4.14f)
                curveToRelative(1.98f, -0.66f, 3.03f, -3.61f, 3.56f, -5.96f)
                curveToRelative(0.05f, -0.21f, 0.13f, -0.81f, 0.19f, -1.34f)
                curveToRelative(0.48f, -3.67f, 1.28f, -11.59f, 7.18f, -15.64f)
                curveToRelative(6.31f, -4.33f, 12.94f, -4.33f, 12.94f, -4.33f)
                lineToRelative(1.2f, 11.92f)
                curveToRelative(-3.05f, -0.45f, -5.17f, 0.17f, -6.96f, 1.16f)
                curveToRelative(-6.74f, 3.75f, -0.87f, 18.15f, -11.36f, 22.99f)
                curveToRelative(-10.09f, 4.69f, -18.34f, -3.4f, -21.04f, -5.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(45.4f, 73.72f)
                lineToRelative(-4.34f, -3.89f)
                curveToRelative(7.97f, -8.9f, 5.87f, -15.44f, 4.34f, -20.2f)
                curveToRelative(-0.31f, -0.96f, -0.6f, -1.87f, -0.79f, -2.74f)
                curveToRelative(-0.68f, -3.08f, -0.82f, -5.76f, -0.61f, -8.1f)
                curveToRelative(-3.06f, -3.81f, -4.41f, -7.8f, -4.5f, -8.07f)
                curveToRelative(-1.86f, -5.63f, -0.46f, -11.12f, 2.75f, -16.27f)
                curveTo(48.74f, 4.0f, 60.49f, 4.0f, 60.49f, 4.0f)
                lineToRelative(3.92f, 10.49f)
                curveToRelative(-2.98f, -0.12f, -12.75f, 0.03f, -15.75f, 4.76f)
                curveToRelative(-3.79f, 5.96f, -1.3f, 9.64f, -1.12f, 10.06f)
                curveToRelative(0.73f, -0.95f, 1.47f, -1.71f, 2.13f, -2.3f)
                curveToRelative(4.79f, -4.25f, 8.95f, -4.86f, 11.6f, -4.62f)
                curveToRelative(2.98f, 0.27f, 5.68f, 1.77f, 7.61f, 4.23f)
                curveToRelative(2.11f, 2.7f, 2.98f, 6.21f, 2.31f, 9.4f)
                curveToRelative(-0.65f, 3.11f, -2.72f, 5.74f, -5.83f, 7.41f)
                curveToRelative(-5.43f, 2.92f, -9.95f, 2.52f, -12.98f, 1.51f)
                curveToRelative(0.02f, 0.07f, 0.03f, 0.15f, 0.05f, 0.22f)
                curveToRelative(0.11f, 0.5f, 0.33f, 1.2f, 0.59f, 2.01f)
                curveToRelative(1.77f, 5.48f, 5.06f, 14.18f, -7.62f, 26.55f)
                close()
                moveTo(52.75f, 36.19f)
                curveToRelative(0.58f, 0.42f, 1.19f, 0.77f, 1.82f, 1.02f)
                curveToRelative(2.1f, 0.84f, 4.39f, 0.56f, 6.99f, -0.84f)
                curveToRelative(1.53f, -0.82f, 1.71f, -1.7f, 1.77f, -1.99f)
                curveToRelative(0.18f, -0.87f, -0.12f, -1.98f, -0.77f, -2.81f)
                curveToRelative(-0.57f, -0.73f, -1.23f, -1.11f, -2.02f, -1.19f)
                curveToRelative(-1.5f, -0.13f, -3.53f, 0.82f, -5.56f, 2.63f)
                curveToRelative(-0.97f, 0.87f, -1.71f, 1.94f, -2.23f, 3.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF48FB1)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(62.77f, 75.35f)
                lineToRelative(-6.21f, -0.17f)
                reflectiveCurveToRelative(2.95f, -16.66f, 12.5f, -19.46f)
                curveToRelative(1.79f, -0.52f, 3.75f, -1.05f, 5.72f, -1.34f)
                curveToRelative(1.17f, -0.18f, 3.02f, -0.45f, 3.93f, -0.79f)
                curveToRelative(0.21f, -1.57f, -0.45f, -3.57f, -1.19f, -5.84f)
                curveToRelative(-0.58f, -1.76f, -1.18f, -3.57f, -1.5f, -5.55f)
                curveToRelative(-0.62f, -3.86f, 0.41f, -7.27f, 2.9f, -9.62f)
                curveToRelative(3.04f, -2.85f, 7.95f, -3.76f, 13.49f, -2.5f)
                curveToRelative(3.16f, 0.72f, 5.49f, 2.27f, 7.54f, 3.63f)
                curveToRelative(2.93f, 1.95f, 4.64f, 2.94f, 8.22f, 0.53f)
                curveToRelative(4.33f, -2.92f, -1.33f, -14.35f, -4.34f, -20.95f)
                lineToRelative(11.23f, -4.68f)
                curveToRelative(1.51f, 3.3f, 8.8f, 20.28f, 3.99f, 29.97f)
                curveToRelative(-1.62f, 3.26f, -4.41f, 5.42f, -8.07f, 6.23f)
                curveToRelative(-7.96f, 1.78f, -12.62f, -1.32f, -16.02f, -3.58f)
                curveToRelative(-1.61f, -1.07f, -3.02f, -1.91f, -4.55f, -2.35f)
                curveToRelative(-10.63f, -3.03f, 4.21f, 12.61f, -2.74f, 19.64f)
                curveToRelative(-4.17f, 4.21f, -14.36f, 5.32f, -15.02f, 5.48f)
                curveToRelative(-6.56f, 1.58f, -9.88f, 11.35f, -9.88f, 11.35f)
                close()
            }
            path(fill = SolidColor(Color(0xFFC92B27)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(43.99f, 38.79f)
                curveToRelative(-0.19f, 2.2f, -0.28f, 3.51f, 0.29f, 6.37f)
                curveToRelative(2.75f, 2.02f, 8.74f, 2.02f, 8.74f, 2.02f)
                curveToRelative(-0.26f, -0.81f, -0.49f, -1.51f, -0.59f, -2.01f)
                curveToRelative(-0.02f, -0.07f, -0.03f, -0.15f, -0.05f, -0.22f)
                curveToRelative(-6.09f, -3.04f, -8.39f, -6.16f, -8.39f, -6.16f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFC107)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(31.53f, 48.64f)
                lineToRelative(-10.34f, -5.07f)
                lineToRelative(5.15f, -7.44f)
                lineToRelative(8.11f, 5.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(16.29f, 34.6f)
                curveToRelative(-5.28f, -0.71f, -10.66f, -5.19f, -11.25f, -5.7f)
                lineToRelative(5.19f, -6.09f)
                curveToRelative(1.57f, 1.33f, 4.9f, 3.56f, 7.13f, 3.86f)
                lineToRelative(-1.07f, 7.93f)
                close()
            }
            path(fill = SolidColor(Color(0xFF03A9F4)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(25.61f, 21.27f)
                lineToRelative(-7.6f, -2.49f)
                curveToRelative(0.87f, -2.66f, 1.1f, -5.53f, 0.65f, -8.3f)
                lineToRelative(7.9f, -1.27f)
                curveToRelative(0.65f, 4.02f, 0.32f, 8.19f, -0.95f, 12.06f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(73.073f, 15.325f)
                lineToRelative(7.815f, -1.71f)
                lineToRelative(2.257f, 10.316f)
                lineToRelative(-7.815f, 1.71f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFC107)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(92.46f, 17.77f)
                lineToRelative(-5.5f, -5.81f)
                curveToRelative(2.88f, -2.73f, 3.54f, -6.3f, 3.54f, -6.34f)
                lineToRelative(7.9f, 1.29f)
                curveToRelative(-0.1f, 0.63f, -1.11f, 6.29f, -5.94f, 10.86f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(95.514f, 48.58f)
                lineToRelative(6.987f, -2.184f)
                lineToRelative(2.386f, 7.636f)
                lineToRelative(-6.987f, 2.184f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(97.55f, 113.03f)
                lineToRelative(-7.95f, -0.94f)
                curveToRelative(0.34f, -2.83f, -1.77f, -6.3f, -2.35f, -7.07f)
                lineToRelative(6.4f, -4.8f)
                curveToRelative(0.48f, 0.63f, 4.65f, 6.4f, 3.9f, 12.81f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(120.37f, 102.89f)
                curveToRelative(-2.99f, -0.45f, -6.05f, -0.63f, -9.07f, -0.52f)
                lineToRelative(-0.27f, -8.0f)
                curveToRelative(3.51f, -0.12f, 7.06f, 0.08f, 10.53f, 0.61f)
                lineToRelative(-1.19f, 7.91f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF48FB1)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(109.614f, 113.902f)
                lineToRelative(5.62f, -5.693f)
                lineToRelative(7.735f, 7.638f)
                lineToRelative(-5.62f, 5.692f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(93.103f, 63.334f)
                lineToRelative(5.78f, 6.609f)
                lineToRelative(-6.609f, 5.78f)
                lineToRelative(-5.78f, -6.609f)
                close()
            }
        }
        .build()
        return _party!!
    }

private var _party: ImageVector? = null
