package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Beer: ImageVector
    get() {
        if (_beer != null) {
            return _beer!!
        }
        _beer = Builder(
            name = "Beer", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(108.69f, 45.31f)
                curveToRelative(-5.59f, -3.45f, -12.96f, -2.82f, -16.36f, -2.24f)
                verticalLineToRelative(-3.96f)
                reflectiveCurveToRelative(2.36f, -0.87f, 2.11f, -3.72f)
                reflectiveCurveToRelative(-6.82f, -3.1f, -6.82f, -3.1f)
                lineToRelative(-59.75f, -0.12f)
                reflectiveCurveToRelative(-3.1f, 0.5f, -3.1f, 3.35f)
                reflectiveCurveTo(27.0f, 38.86f, 27.0f, 38.86f)
                reflectiveCurveToRelative(-0.74f, 58.51f, -0.99f, 60.12f)
                curveToRelative(-0.25f, 1.61f, -1.98f, 8.06f, -2.48f, 10.16f)
                curveToRelative(-0.5f, 2.11f, 0.0f, 3.47f, 1.61f, 4.83f)
                curveToRelative(1.61f, 1.36f, 11.78f, 9.67f, 33.72f, 10.29f)
                curveToRelative(21.44f, 0.61f, 31.36f, -7.56f, 33.34f, -9.3f)
                curveToRelative(1.22f, -1.06f, 1.98f, -3.1f, 1.61f, -4.59f)
                reflectiveCurveToRelative(-1.36f, -9.05f, -1.36f, -9.05f)
                lineToRelative(-0.1f, -4.32f)
                curveToRelative(1.64f, -0.13f, 3.69f, -0.41f, 5.55f, -1.01f)
                curveToRelative(6.2f, -1.98f, 13.55f, -7.5f, 15.99f, -14.01f)
                curveToRelative(3.36f, -8.9f, 5.72f, -29.92f, -5.2f, -36.67f)
                close()
                moveTo(101.13f, 81.63f)
                curveToRelative(-3.0f, 3.06f, -6.59f, 3.6f, -8.8f, 3.7f)
                lineTo(92.33f, 54.54f)
                curveToRelative(2.26f, -0.43f, 6.58f, -1.02f, 9.17f, 0.07f)
                curveToRelative(2.36f, 0.99f, 3.72f, 2.6f, 4.46f, 6.2f)
                curveToRelative(0.44f, 2.07f, 1.25f, 14.62f, -4.83f, 20.82f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(108.69f, 45.31f)
                curveToRelative(-5.59f, -3.45f, -12.96f, -2.82f, -16.36f, -2.24f)
                verticalLineToRelative(-3.96f)
                reflectiveCurveToRelative(2.36f, -0.87f, 2.11f, -3.72f)
                reflectiveCurveToRelative(-6.82f, -3.1f, -6.82f, -3.1f)
                lineToRelative(-59.75f, -0.12f)
                reflectiveCurveToRelative(-3.1f, 0.5f, -3.1f, 3.35f)
                reflectiveCurveTo(27.0f, 38.86f, 27.0f, 38.86f)
                reflectiveCurveToRelative(-0.74f, 58.51f, -0.99f, 60.12f)
                curveToRelative(-0.25f, 1.61f, -1.98f, 8.06f, -2.48f, 10.16f)
                curveToRelative(-0.5f, 2.11f, 0.0f, 3.47f, 1.61f, 4.83f)
                curveToRelative(1.61f, 1.36f, 11.78f, 9.67f, 33.72f, 10.29f)
                curveToRelative(21.44f, 0.61f, 31.36f, -7.56f, 33.34f, -9.3f)
                curveToRelative(1.22f, -1.06f, 1.98f, -3.1f, 1.61f, -4.59f)
                reflectiveCurveToRelative(-1.36f, -9.05f, -1.36f, -9.05f)
                lineToRelative(-0.1f, -4.32f)
                curveToRelative(1.64f, -0.13f, 3.69f, -0.41f, 5.55f, -1.01f)
                curveToRelative(6.2f, -1.98f, 13.55f, -7.5f, 15.99f, -14.01f)
                curveToRelative(3.36f, -8.9f, 5.72f, -29.92f, -5.2f, -36.67f)
                close()
                moveTo(101.13f, 81.63f)
                curveToRelative(-3.0f, 3.06f, -6.59f, 3.6f, -8.8f, 3.7f)
                lineTo(92.33f, 54.54f)
                curveToRelative(2.26f, -0.43f, 6.58f, -1.02f, 9.17f, 0.07f)
                curveToRelative(2.36f, 0.99f, 3.72f, 2.6f, 4.46f, 6.2f)
                curveToRelative(0.44f, 2.07f, 1.25f, 14.62f, -4.83f, 20.82f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(28.84f, 94.82f)
                lineToRelative(-0.46f, 12.95f)
                lineToRelative(6.36f, 6.36f)
                lineToRelative(2.05f, 1.36f)
                lineToRelative(0.08f, -11.51f)
                lineToRelative(-4.25f, -3.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD5D5D5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.49f, 109.36f)
                curveToRelative(0.08f, 0.53f, 5.15f, 5.91f, 15.15f, 9.24f)
                reflectiveCurveToRelative(22.27f, 3.48f, 33.7f, 0.83f)
                reflectiveCurveToRelative(15.75f, -9.47f, 15.75f, -9.47f)
                reflectiveCurveToRelative(0.23f, -1.44f, -1.06f, -2.04f)
                reflectiveCurveToRelative(-4.39f, -0.68f, -6.44f, 1.44f)
                curveToRelative(-2.01f, 2.08f, -2.2f, 5.38f, -2.2f, 5.38f)
                reflectiveCurveToRelative(-2.2f, -4.24f, -7.5f, -3.18f)
                curveToRelative(-5.51f, 1.1f, -7.27f, 6.67f, -7.27f, 6.67f)
                reflectiveCurveToRelative(-2.27f, -4.77f, -7.73f, -4.85f)
                curveToRelative(-5.45f, -0.08f, -8.48f, 5.6f, -8.48f, 5.6f)
                reflectiveCurveToRelative(0.91f, -6.51f, -5.83f, -8.33f)
                curveToRelative(-6.38f, -1.72f, -8.94f, 3.56f, -8.94f, 3.56f)
                reflectiveCurveToRelative(-0.27f, -5.2f, -3.26f, -6.97f)
                curveToRelative(-4.07f, -2.43f, -5.89f, 2.12f, -5.89f, 2.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEBC990)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(29.63f, 34.02f)
                lineToRelative(-0.1f, 9.86f)
                lineToRelative(-0.03f, 3.77f)
                lineTo(66.27f, 69.9f)
                lineToRelative(22.61f, -24.86f)
                lineToRelative(0.02f, -10.61f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9D9D9C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.95f, 92.34f)
                reflectiveCurveToRelative(-1.93f, 0.11f, -1.48f, 2.63f)
                curveToRelative(0.27f, 1.46f, 5.84f, 16.94f, 31.02f, 16.71f)
                curveToRelative(24.26f, -0.22f, 29.69f, -15.34f, 29.87f, -16.71f)
                curveToRelative(0.34f, -2.63f, -2.98f, -1.72f, -2.98f, -1.72f)
                lineToRelative(-56.43f, -0.91f)
                close()
            }
            path(
                fill = radialGradient(
                    0.594f to Color(0xFFDB8116), 0.896f to Color(0xFFFDB408),
                    center = Offset(60.329365f, 13.463319f), radius = 96.22961f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(58.92f, 50.57f)
                curveToRelative(-18.2f, 0.57f, -29.41f, -6.18f, -29.41f, -6.18f)
                reflectiveCurveToRelative(-0.31f, 43.57f, -0.23f, 46.24f)
                curveToRelative(0.11f, 3.89f, 4.92f, 19.46f, 29.76f, 19.23f)
                curveToRelative(24.84f, -0.23f, 29.99f, -15.68f, 29.99f, -19.69f)
                curveToRelative(0.0f, -2.18f, -0.11f, -45.78f, -0.11f, -45.78f)
                reflectiveCurveToRelative(-7.92f, 5.48f, -30.0f, 6.18f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(91.73f, 47.06f)
                curveToRelative(0.24f, 2.87f, 4.54f, 0.98f, 8.79f, 1.07f)
                curveToRelative(4.65f, 0.1f, 7.45f, 2.04f, 7.69f, -0.64f)
                curveToRelative(0.24f, -2.72f, -4.55f, -3.41f, -8.39f, -3.61f)
                curveToRelative(-2.64f, -0.14f, -8.32f, 0.41f, -8.09f, 3.18f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.1f, 52.42f)
                curveToRelative(-0.62f, 0.67f, -0.11f, 4.21f, 0.21f, 4.45f)
                curveToRelative(0.59f, 0.46f, 4.22f, -1.69f, 8.04f, -0.42f)
                curveToRelative(6.22f, 2.06f, 5.46f, 6.18f, 5.83f, 6.1f)
                curveToRelative(0.39f, -0.08f, 1.37f, -6.94f, -3.85f, -9.77f)
                curveToRelative(-3.93f, -2.12f, -9.61f, -1.03f, -10.23f, -0.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.66f, 87.07f)
                curveToRelative(-0.73f, 0.64f, -0.41f, 2.99f, 2.0f, 2.95f)
                curveToRelative(2.72f, -0.05f, 6.53f, -0.91f, 9.66f, -4.62f)
                curveToRelative(2.23f, -2.64f, 2.72f, -7.44f, 1.86f, -7.44f)
                reflectiveCurveToRelative(-2.45f, 4.9f, -6.85f, 7.21f)
                curveToRelative(-4.04f, 2.12f, -5.9f, 1.22f, -6.67f, 1.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(92.17f, 93.56f)
                lineToRelative(0.2f, 4.66f)
                reflectiveCurveToRelative(7.47f, 0.42f, 14.98f, -5.62f)
                curveToRelative(9.2f, -7.39f, 9.07f, -21.6f, 9.03f, -21.62f)
                curveToRelative(-0.1f, -0.05f, -2.95f, 12.44f, -10.98f, 18.22f)
                curveToRelative(-6.48f, 4.67f, -13.23f, 4.36f, -13.23f, 4.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE265)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(62.69f, 61.89f)
                curveToRelative(-1.03f, 1.64f, -0.59f, 3.25f, 0.23f, 3.72f)
                curveToRelative(1.01f, 0.58f, 2.55f, -0.09f, 3.29f, -1.62f)
                curveToRelative(0.65f, -1.34f, 0.65f, -2.81f, -0.57f, -3.38f)
                curveToRelative(-0.96f, -0.45f, -2.05f, -0.16f, -2.95f, 1.28f)
                close()
                moveTo(54.35f, 71.93f)
                curveToRelative(-1.07f, 0.85f, -0.65f, 2.16f, 0.06f, 3.09f)
                curveToRelative(0.76f, 1.0f, 2.21f, 1.42f, 3.01f, 0.71f)
                curveToRelative(0.77f, -0.69f, 0.71f, -2.13f, -0.11f, -3.18f)
                curveToRelative(-0.69f, -0.87f, -1.99f, -1.39f, -2.96f, -0.62f)
                close()
                moveTo(53.11f, 81.31f)
                curveToRelative(-1.99f, 0.06f, -2.21f, 2.01f, -2.07f, 2.84f)
                curveToRelative(0.1f, 0.57f, 0.62f, 1.81f, 2.5f, 1.76f)
                reflectiveCurveToRelative(2.1f, -2.04f, 1.99f, -2.75f)
                curveToRelative(-0.13f, -0.71f, -0.66f, -1.9f, -2.42f, -1.85f)
                close()
                moveTo(59.4f, 82.79f)
                curveToRelative(-0.97f, 0.03f, -1.88f, 0.92f, -1.62f, 2.35f)
                curveToRelative(0.26f, 1.39f, 1.19f, 1.73f, 1.99f, 1.62f)
                curveToRelative(0.76f, -0.11f, 1.56f, -0.85f, 1.45f, -2.13f)
                curveToRelative(-0.12f, -1.27f, -0.97f, -1.87f, -1.82f, -1.84f)
                close()
                moveTo(63.5f, 89.76f)
                curveToRelative(-1.18f, 0.0f, -1.95f, 1.26f, -1.91f, 2.68f)
                curveToRelative(0.04f, 1.42f, 0.92f, 3.16f, 2.64f, 2.96f)
                curveToRelative(1.22f, -0.14f, 1.87f, -2.15f, 1.58f, -3.33f)
                curveToRelative(-0.28f, -1.18f, -1.09f, -2.31f, -2.31f, -2.31f)
                close()
                moveTo(56.59f, 96.33f)
                curveToRelative(-1.34f, 0.4f, -0.91f, 2.7f, -0.73f, 3.25f)
                curveToRelative(0.24f, 0.73f, 0.89f, 2.11f, 2.36f, 1.79f)
                reflectiveCurveToRelative(1.18f, -2.27f, 0.65f, -3.49f)
                curveToRelative(-0.42f, -0.97f, -1.06f, -1.91f, -2.28f, -1.55f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFADFB1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.79f, 22.76f)
                curveToRelative(-0.32f, 0.32f, -6.26f, 1.11f, -6.1f, 6.5f)
                curveToRelative(0.16f, 5.39f, 5.95f, 7.21f, 8.24f, 8.01f)
                curveToRelative(2.3f, 0.79f, 7.37f, 1.66f, 8.88f, 6.66f)
                curveToRelative(1.51f, 4.99f, 1.75f, 7.35f, 4.12f, 8.01f)
                curveToRelative(2.85f, 0.79f, 4.77f, -3.32f, 6.18f, -5.63f)
                curveToRelative(1.51f, -2.46f, 2.93f, -4.6f, 5.31f, -4.91f)
                reflectiveCurveToRelative(16.09f, 0.0f, 24.02f, -1.98f)
                reflectiveCurveToRelative(14.37f, -6.49f, 14.03f, -13.08f)
                curveTo(94.0f, 17.21f, 81.4f, 18.16f, 81.4f, 18.16f)
                reflectiveCurveTo(76.44f, 5.73f, 63.16f, 4.13f)
                curveToRelative(-16.41f, -1.98f, -20.13f, 8.32f, -20.13f, 8.32f)
                reflectiveCurveToRelative(-6.06f, -1.62f, -10.54f, 2.46f)
                curveToRelative(-3.67f, 3.34f, -2.7f, 7.85f, -2.7f, 7.85f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.69f, 17.13f)
                curveToRelative(-0.77f, 0.5f, -5.49f, -2.81f, -9.08f, 0.92f)
                curveToRelative(-3.03f, 3.16f, -0.75f, 6.13f, -1.7f, 7.32f)
                curveToRelative(-1.3f, 1.62f, -5.93f, 0.99f, -5.61f, 4.39f)
                curveToRelative(0.32f, 3.41f, 4.97f, 3.76f, 7.38f, 4.47f)
                curveToRelative(2.27f, 0.66f, 4.55f, 1.65f, 6.73f, 3.56f)
                curveToRelative(2.27f, 1.99f, 1.49f, 7.16f, 3.82f, 7.64f)
                curveToRelative(2.3f, 0.48f, 2.76f, -4.64f, 5.7f, -6.86f)
                curveToRelative(2.93f, -2.22f, 4.98f, -2.01f, 12.43f, -2.42f)
                curveToRelative(7.44f, -0.41f, 25.98f, -0.8f, 25.29f, -9.2f)
                curveToRelative(-0.55f, -6.74f, -8.48f, -4.2f, -10.86f, -5.39f)
                curveToRelative(-2.38f, -1.19f, -5.2f, -11.64f, -16.73f, -13.24f)
                curveToRelative(-11.98f, -1.65f, -16.09f, 7.99f, -17.37f, 8.81f)
                close()
                moveTo(32.5f, 42.64f)
                curveToRelative(-0.34f, 1.91f, -0.54f, 46.26f, -0.54f, 46.26f)
                reflectiveCurveToRelative(-0.2f, 3.46f, 2.79f, 3.87f)
                curveToRelative(2.79f, 0.39f, 3.8f, -2.25f, 3.87f, -4.03f)
                curveToRelative(0.08f, -1.78f, 0.46f, -41.53f, 0.39f, -43.31f)
                reflectiveCurveToRelative(-1.78f, -3.8f, -3.49f, -4.34f)
                reflectiveCurveToRelative(-2.79f, 0.24f, -3.02f, 1.55f)
                close()
            }
        }
            .build()
        return _beer!!
    }

private var _beer: ImageVector? = null
