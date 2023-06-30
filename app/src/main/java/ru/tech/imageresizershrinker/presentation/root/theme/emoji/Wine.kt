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

val Emoji.Wine: ImageVector
    get() {
        if (_wine != null) {
            return _wine!!
        }
        _wine = Builder(
            name = "Wine", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(43.34f, 11.83f)
                lineToRelative(11.98f, 6.53f)
                lineToRelative(17.92f, 0.49f)
                lineToRelative(11.11f, -6.5f)
                lineToRelative(1.58f, 4.22f)
                reflectiveCurveToRelative(6.99f, 18.86f, 6.46f, 29.41f)
                curveToRelative(-0.49f, 9.88f, -1.7f, 18.62f, -10.81f, 26.51f)
                curveToRelative(-6.86f, 5.93f, -13.71f, 6.46f, -13.71f, 6.46f)
                lineTo(68.0f, 99.13f)
                reflectiveCurveToRelative(8.97f, 0.4f, 14.24f, 3.82f)
                curveToRelative(4.76f, 3.09f, 5.54f, 6.07f, 5.54f, 8.04f)
                curveToRelative(0.0f, 1.98f, -1.85f, 12.13f, -24.26f, 11.87f)
                reflectiveCurveToRelative(-23.74f, -11.21f, -23.74f, -13.19f)
                curveToRelative(0.0f, -1.98f, 2.12f, -5.79f, 8.44f, -8.18f)
                curveToRelative(5.93f, -2.24f, 11.08f, -1.98f, 11.08f, -1.98f)
                verticalLineTo(78.56f)
                reflectiveCurveToRelative(-8.31f, -2.11f, -14.64f, -8.18f)
                curveToRelative(-3.91f, -3.75f, -10.42f, -12.26f, -9.49f, -26.11f)
                curveToRelative(0.8f, -12.14f, 8.17f, -32.44f, 8.17f, -32.44f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(43.34f, 11.83f)
                lineToRelative(4.75f, -4.35f)
                lineToRelative(7.38f, -2.37f)
                lineToRelative(17.01f, 0.13f)
                lineToRelative(7.78f, 3.3f)
                lineToRelative(4.09f, 3.82f)
                lineToRelative(1.58f, 4.22f)
                reflectiveCurveToRelative(6.99f, 18.86f, 6.46f, 29.41f)
                curveToRelative(-0.49f, 9.88f, -1.7f, 18.62f, -10.81f, 26.51f)
                curveToRelative(-6.86f, 5.93f, -13.71f, 6.46f, -13.71f, 6.46f)
                lineToRelative(0.06f, 20.14f)
                reflectiveCurveToRelative(9.02f, 1.03f, 14.3f, 4.46f)
                curveToRelative(4.76f, 3.09f, 5.55f, 5.47f, 5.55f, 7.45f)
                curveToRelative(0.0f, 1.98f, -1.85f, 12.13f, -24.26f, 11.87f)
                reflectiveCurveToRelative(-23.74f, -11.21f, -23.74f, -13.19f)
                curveToRelative(0.0f, -1.98f, 2.12f, -5.79f, 8.44f, -8.18f)
                curveToRelative(5.93f, -2.24f, 11.01f, -2.4f, 11.01f, -2.4f)
                lineToRelative(0.07f, -20.54f)
                reflectiveCurveToRelative(-8.31f, -2.11f, -14.64f, -8.18f)
                curveToRelative(-3.91f, -3.75f, -10.42f, -12.26f, -9.49f, -26.11f)
                curveToRelative(0.8f, -12.15f, 8.17f, -32.45f, 8.17f, -32.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD4D4D4)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(58.59f, 79.03f)
                curveToRelative(-0.29f, -0.09f, -6.77f, -1.66f, -11.17f, -5.1f)
                curveToRelative(-8.56f, -6.7f, -12.62f, -14.09f, -13.14f, -23.95f)
                curveToRelative(-0.31f, -5.82f, 2.08f, -15.71f, 3.78f, -22.64f)
                curveToRelative(1.74f, -7.06f, 3.84f, -12.34f, 3.86f, -12.4f)
                curveToRelative(0.0f, 0.0f, 0.28f, -0.8f, 0.58f, -0.65f)
                curveToRelative(0.63f, 0.32f, 0.0f, 3.1f, 0.0f, 3.1f)
                curveToRelative(-0.48f, 1.78f, -0.94f, 3.46f, -2.64f, 10.42f)
                curveToRelative(-1.67f, 6.81f, -3.86f, 16.5f, -3.57f, 22.05f)
                curveToRelative(0.49f, 9.23f, 4.3f, 16.17f, 12.37f, 22.48f)
                curveToRelative(4.1f, 3.21f, 9.31f, 4.47f, 10.02f, 4.81f)
                reflectiveCurveToRelative(1.63f, 0.5f, 1.5f, 1.41f)
                curveToRelative(-0.14f, 0.94f, -1.59f, 0.47f, -1.59f, 0.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(68.62f, 79.28f)
                curveToRelative(-0.75f, 0.07f, -1.48f, -0.06f, -1.54f, -0.79f)
                curveToRelative(-0.08f, -0.9f, 1.04f, -0.96f, 1.04f, -0.96f)
                curveToRelative(0.74f, -0.27f, 4.53f, -0.58f, 8.28f, -2.83f)
                curveToRelative(10.57f, -6.34f, 15.69f, -16.29f, 15.03f, -29.19f)
                curveToRelative(-0.92f, -17.79f, -7.9f, -31.34f, -7.97f, -31.47f)
                lineToRelative(1.32f, -1.94f)
                curveToRelative(0.07f, 0.14f, 7.7f, 15.08f, 8.65f, 33.31f)
                curveToRelative(0.71f, 13.69f, -4.4f, 24.3f, -15.65f, 31.04f)
                curveToRelative(-4.1f, 2.46f, -8.77f, 2.79f, -9.16f, 2.83f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC8C8C8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(65.13f, 21.92f)
                curveToRelative(-0.25f, 0.0f, -0.5f, 0.0f, -0.75f, -0.01f)
                curveToRelative(-8.8f, -0.13f, -16.32f, -2.61f, -19.96f, -6.13f)
                curveToRelative(-1.3f, -1.25f, -1.48f, -2.12f, -1.55f, -3.5f)
                curveToRelative(-0.13f, -2.37f, 2.69f, -8.29f, 21.06f, -8.19f)
                curveToRelative(12.11f, 0.06f, 21.1f, 3.81f, 21.15f, 8.51f)
                curveToRelative(0.02f, 1.42f, -0.23f, 2.58f, -1.58f, 3.92f)
                curveToRelative(-3.45f, 3.39f, -10.29f, 5.4f, -18.37f, 5.4f)
                close()
                moveTo(63.03f, 6.58f)
                curveToRelative(-12.04f, 0.1f, -17.2f, 3.42f, -17.15f, 5.57f)
                curveToRelative(0.01f, 0.55f, 0.05f, 0.86f, 0.71f, 1.54f)
                curveToRelative(2.4f, 2.5f, 8.85f, 5.08f, 17.84f, 5.21f)
                curveToRelative(8.96f, 0.15f, 14.65f, -2.25f, 16.97f, -4.54f)
                curveToRelative(0.75f, -0.73f, 1.13f, -0.97f, 1.13f, -1.55f)
                curveToRelative(0.0f, -2.27f, -4.87f, -5.94f, -17.75f, -6.22f)
                curveToRelative(-0.59f, 0.0f, -1.18f, -0.01f, -1.75f, -0.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(65.32f, 19.74f)
                curveToRelative(-0.81f, 0.0f, -1.66f, -0.02f, -2.54f, -0.07f)
                curveToRelative(-14.0f, -0.74f, -16.72f, -6.35f, -16.85f, -6.61f)
                curveToRelative(-0.24f, -0.5f, -0.35f, -1.47f, 0.03f, -1.58f)
                curveToRelative(0.51f, -0.15f, 0.78f, 0.3f, 1.3f, 0.78f)
                curveToRelative(0.84f, 0.78f, 3.87f, 4.83f, 16.52f, 5.12f)
                curveToRelative(12.4f, 0.29f, 16.39f, -3.49f, 17.12f, -4.15f)
                curveToRelative(0.55f, -0.5f, 1.13f, -1.54f, 1.59f, -1.24f)
                curveToRelative(0.46f, 0.3f, 0.22f, 1.5f, -0.05f, 1.97f)
                curveToRelative(-0.42f, 0.72f, -3.55f, 5.78f, -17.12f, 5.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.79f, 79.74f)
                curveToRelative(-2.15f, 0.0f, -1.71f, 1.65f, -1.82f, 12.62f)
                curveToRelative(-0.08f, 8.75f, 0.83f, 10.31f, 1.73f, 10.4f)
                curveToRelative(0.91f, 0.08f, 1.73f, -0.58f, 1.73f, -9.82f)
                curveToRelative(0.01f, -10.72f, 0.34f, -13.2f, -1.64f, -13.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(67.11f, 100.05f)
                curveToRelative(-0.82f, 0.07f, 0.07f, 1.86f, -1.04f, 3.28f)
                curveToRelative(-0.88f, 1.12f, -2.53f, 1.79f, -2.46f, 3.05f)
                curveToRelative(0.07f, 1.27f, 6.55f, 0.45f, 6.55f, -1.94f)
                curveToRelative(0.0f, -0.93f, -0.68f, -1.58f, -1.34f, -2.46f)
                reflectiveCurveToRelative(-1.3f, -1.97f, -1.71f, -1.93f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(52.14f, 109.06f)
                reflectiveCurveToRelative(2.85f, 3.82f, 11.63f, 3.9f)
                curveToRelative(8.86f, 0.08f, 11.05f, -2.6f, 11.38f, -3.66f)
                curveToRelative(0.24f, -0.8f, -2.76f, -4.99f, -3.13f, -4.92f)
                curveToRelative(-0.37f, 0.07f, -1.34f, 3.96f, -8.19f, 3.71f)
                reflectiveCurveToRelative(-7.03f, -2.94f, -7.58f, -3.45f)
                curveToRelative(-0.44f, -0.41f, -4.11f, 4.42f, -4.11f, 4.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(43.95f, 107.2f)
                curveToRelative(-2.22f, 0.29f, -2.32f, 2.87f, -0.89f, 5.21f)
                curveToRelative(2.9f, 4.77f, 12.53f, 8.03f, 13.03f, 5.44f)
                curveToRelative(0.45f, -2.31f, -4.61f, -2.9f, -4.1f, -6.78f)
                curveToRelative(0.37f, -2.83f, 5.44f, -5.44f, 4.02f, -6.93f)
                curveToRelative(-1.41f, -1.49f, -3.57f, 2.01f, -6.48f, 2.68f)
                curveToRelative(-2.54f, 0.59f, -3.87f, 0.16f, -5.58f, 0.38f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCACACA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(76.12f, 103.4f)
                curveToRelative(-1.24f, 0.99f, 2.46f, 2.01f, 2.31f, 5.06f)
                curveToRelative(-0.15f, 3.05f, -3.2f, 4.37f, -2.61f, 5.21f)
                curveToRelative(0.89f, 1.27f, 6.14f, -1.42f, 5.96f, -5.59f)
                curveToRelative(-0.15f, -3.41f, -4.54f, -5.57f, -5.66f, -4.68f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.78f, 117.48f)
                curveToRelative(0.45f, 1.41f, 6.37f, 0.8f, 9.16f, -3.2f)
                curveToRelative(2.9f, -4.17f, 0.89f, -7.67f, -0.22f, -7.3f)
                curveToRelative(-1.12f, 0.37f, 0.45f, 2.98f, -2.38f, 5.73f)
                curveToRelative(-2.84f, 2.75f, -7.06f, 3.19f, -6.56f, 4.77f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(39.79f, 109.54f)
                curveToRelative(-0.26f, -0.24f, -0.73f, 2.12f, 0.06f, 4.06f)
                curveToRelative(0.67f, 1.64f, 4.1f, 10.2f, 24.65f, 10.35f)
                curveToRelative(22.86f, 0.17f, 24.03f, -9.63f, 23.74f, -12.37f)
                curveToRelative(-0.19f, -1.81f, -0.54f, -2.43f, -0.72f, -2.43f)
                curveToRelative(-0.17f, -0.01f, -0.35f, 1.6f, -0.5f, 2.47f)
                curveToRelative(-0.48f, 2.72f, -3.16f, 9.58f, -22.15f, 9.73f)
                curveToRelative(-17.28f, 0.14f, -22.34f, -6.93f, -23.23f, -8.19f)
                curveToRelative(-0.63f, -0.9f, -1.7f, -3.48f, -1.85f, -3.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCC1935)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(38.26f, 47.89f)
                lineToRelative(3.07f, 8.36f)
                lineToRelative(11.26f, 6.06f)
                lineTo(67.0f, 65.03f)
                lineToRelative(14.33f, -12.45f)
                lineToRelative(5.03f, -7.42f)
                reflectiveCurveToRelative(-4.94f, -2.1f, -10.52f, -2.92f)
                curveToRelative(-8.7f, -1.28f, -16.09f, -1.6f, -24.79f, 0.02f)
                curveToRelative(-10.69f, 1.99f, -12.79f, 5.63f, -12.79f, 5.63f)
                close()
            }
            path(
                fill = radialGradient(
                    0.404f to Color(0xFFAF0D1A), 0.535f to Color(0xFFAB0C19),
                    0.667f to Color(0xFF9F0B17), 0.801f to Color(0xFF8A0813), 0.935f to
                            Color(0xFF6E050D), 0.96f to Color(0xFF68040C), center =
                    Offset(63.951458f, 49.5767f), radius = 25.281094f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(57.96f, 60.51f)
                curveToRelative(10.06f, 0.34f, 11.43f, -5.54f, 19.62f, -11.34f)
                reflectiveCurveToRelative(11.51f, -4.09f, 11.51f, -4.09f)
                reflectiveCurveToRelative(2.75f, 12.47f, -6.74f, 22.34f)
                curveToRelative(-8.27f, 8.61f, -23.42f, 11.2f, -35.38f, 1.07f)
                curveToRelative(-8.87f, -7.51f, -8.71f, -20.6f, -8.71f, -20.6f)
                reflectiveCurveToRelative(6.99f, 12.19f, 19.7f, 12.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(45.09f, 20.08f)
                reflectiveCurveToRelative(-1.41f, 3.97f, -3.19f, 11.93f)
                curveToRelative(-1.53f, 6.84f, -2.16f, 11.19f, -2.16f, 11.19f)
                reflectiveCurveToRelative(3.22f, -1.6f, 6.81f, -2.54f)
                curveToRelative(3.25f, -0.85f, 6.06f, -0.68f, 6.06f, -0.68f)
                reflectiveCurveToRelative(-0.09f, -5.21f, 0.34f, -8.97f)
                curveToRelative(0.35f, -3.1f, 1.11f, -7.09f, 1.11f, -7.09f)
                reflectiveCurveToRelative(-2.82f, -0.68f, -4.53f, -1.45f)
                curveToRelative(-1.71f, -0.77f, -4.44f, -2.39f, -4.44f, -2.39f)
                close()
            }
        }
            .build()
        return _wine!!
    }

private var _wine: ImageVector? = null
