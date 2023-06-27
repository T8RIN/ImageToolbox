package ru.tech.imageresizershrinker.presentation.theme.emoji

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

val Emoji.Cocktail: ImageVector
    get() {
        if (_cocktail != null) {
            return _cocktail!!
        }
        _cocktail = Builder(
            name = "Cocktail", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(20.55f, 21.01f)
                lineToRelative(20.92f, 4.78f)
                lineToRelative(40.65f, 1.2f)
                lineToRelative(24.96f, -4.04f)
                lineToRelative(6.65f, -6.79f)
                lineToRelative(0.93f, 4.31f)
                lineToRelative(-43.07f, 43.08f)
                lineToRelative(-2.56f, 4.77f)
                lineToRelative(-0.11f, 27.48f)
                reflectiveCurveToRelative(0.93f, 6.52f, 3.61f, 7.8f)
                curveToRelative(2.68f, 1.28f, 12.23f, 2.45f, 16.07f, 5.36f)
                reflectiveCurveToRelative(2.1f, 5.71f, 2.1f, 5.71f)
                lineToRelative(-6.87f, 5.12f)
                lineToRelative(-19.44f, 3.03f)
                lineToRelative(-21.42f, -2.21f)
                reflectiveCurveToRelative(-8.01f, -4.94f, -8.01f, -6.69f)
                curveToRelative(0.0f, 0.0f, -0.32f, -3.37f, 3.82f, -5.77f)
                curveToRelative(3.65f, -2.12f, 12.97f, -2.55f, 15.14f, -4.19f)
                curveToRelative(4.31f, -3.26f, 4.54f, -6.75f, 4.54f, -6.75f)
                lineToRelative(0.35f, -28.87f)
                lineToRelative(-5.94f, -6.87f)
                reflectiveCurveTo(25.16f, 35.74f, 21.32f, 31.55f)
                curveToRelative(-3.84f, -4.19f, -5.59f, -6.52f, -7.22f, -8.97f)
                curveToRelative(-3.99f, -5.98f, -0.34f, -9.98f, -0.34f, -9.98f)
                lineToRelative(6.79f, 8.41f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(17.57f, 11.86f)
                lineTo(35.5f, 7.31f)
                lineTo(66.0f, 6.38f)
                lineToRelative(31.44f, 3.73f)
                lineToRelative(16.3f, 6.05f)
                lineToRelative(0.93f, 4.31f)
                lineToRelative(-43.08f, 43.08f)
                lineToRelative(-2.56f, 4.77f)
                lineToRelative(-0.11f, 27.48f)
                reflectiveCurveToRelative(0.93f, 6.52f, 3.61f, 7.8f)
                curveToRelative(2.68f, 1.28f, 12.23f, 2.45f, 16.07f, 5.36f)
                reflectiveCurveToRelative(2.1f, 5.71f, 2.1f, 5.71f)
                lineToRelative(-6.87f, 5.12f)
                lineToRelative(-19.44f, 3.03f)
                lineToRelative(-21.42f, -2.21f)
                reflectiveCurveToRelative(-8.01f, -4.94f, -8.01f, -6.69f)
                curveToRelative(0.0f, 0.0f, -0.32f, -3.37f, 3.82f, -5.77f)
                curveToRelative(3.65f, -2.12f, 12.97f, -2.55f, 15.14f, -4.19f)
                curveToRelative(4.31f, -3.26f, 4.54f, -6.75f, 4.54f, -6.75f)
                lineToRelative(0.35f, -28.87f)
                lineToRelative(-5.94f, -6.87f)
                reflectiveCurveTo(25.16f, 35.74f, 21.32f, 31.55f)
                curveToRelative(-3.84f, -4.19f, -5.59f, -6.52f, -7.22f, -8.97f)
                curveToRelative(-3.99f, -5.98f, -0.34f, -9.98f, -0.34f, -9.98f)
                lineToRelative(3.81f, -0.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEF9E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(24.89f, 26.32f)
                reflectiveCurveToRelative(11.78f, -6.06f, 39.31f, -5.7f)
                reflectiveCurveToRelative(38.34f, 7.98f, 38.34f, 7.98f)
                lineTo(87.0f, 39.35f)
                lineTo(45.23f, 37.4f)
                lineTo(24.89f, 26.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE5DDA0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.9f, 33.06f)
                curveToRelative(-25.58f, -0.61f, -40.43f, -6.7f, -40.43f, -6.7f)
                lineToRelative(30.45f, 30.08f)
                reflectiveCurveToRelative(3.61f, 4.54f, 8.24f, 4.54f)
                curveToRelative(4.63f, 0.0f, 6.37f, -1.98f, 7.96f, -3.2f)
                curveToRelative(1.58f, -1.22f, 31.46f, -29.17f, 31.46f, -29.17f)
                reflectiveCurveToRelative(-16.0f, 4.96f, -37.68f, 4.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(112.53f, 20.87f)
                lineToRelative(-1.59f, 2.22f)
                reflectiveCurveToRelative(-34.67f, 33.62f, -41.3f, 39.86f)
                curveToRelative(-2.72f, 2.56f, -3.65f, 4.5f, -3.54f, 7.53f)
                curveToRelative(0.1f, 2.78f, -1.79f, 16.9f, -1.79f, 16.9f)
                lineToRelative(1.86f, 15.59f)
                reflectiveCurveToRelative(0.89f, 2.54f, 2.93f, 4.07f)
                curveToRelative(2.04f, 1.53f, 7.0f, 2.16f, 7.0f, 2.16f)
                reflectiveCurveToRelative(-3.94f, -2.32f, -5.34f, -5.95f)
                reflectiveCurveToRelative(-1.21f, -5.12f, -1.21f, -7.28f)
                lineTo(69.55f, 69.45f)
                curveToRelative(0.0f, -1.78f, 2.16f, -4.13f, 3.69f, -5.6f)
                curveToRelative(1.53f, -1.46f, 37.39f, -36.69f, 38.79f, -38.22f)
                curveToRelative(2.71f, -2.96f, 3.56f, -4.92f, 3.68f, -6.45f)
                curveToRelative(0.13f, -1.53f, -0.33f, -3.6f, -0.91f, -2.58f)
                curveToRelative(-0.57f, 1.03f, -2.27f, 4.27f, -2.27f, 4.27f)
                close()
                moveTo(50.27f, 108.44f)
                reflectiveCurveToRelative(4.51f, -2.42f, 5.72f, -5.86f)
                reflectiveCurveToRelative(1.59f, -5.65f, 1.65f, -10.17f)
                reflectiveCurveToRelative(0.0f, -21.75f, 0.0f, -23.78f)
                curveToRelative(0.0f, -2.45f, -3.04f, -4.9f, -4.41f, -6.78f)
                curveToRelative(-0.24f, -0.33f, 1.81f, 0.34f, 3.82f, 1.56f)
                curveToRelative(2.45f, 1.49f, 4.89f, 2.89f, 4.66f, 6.51f)
                curveToRelative(-0.23f, 3.52f, 0.22f, 12.18f, 0.22f, 12.18f)
                lineToRelative(-0.16f, 15.66f)
                reflectiveCurveToRelative(0.07f, 6.31f, -3.82f, 8.48f)
                curveToRelative(-3.35f, 1.86f, -7.68f, 2.2f, -7.68f, 2.2f)
                close()
                moveTo(34.94f, 113.49f)
                curveToRelative(-0.12f, 0.33f, -0.19f, 3.6f, 2.29f, 5.19f)
                curveToRelative(1.78f, 1.14f, 7.3f, 5.54f, 25.63f, 5.6f)
                curveToRelative(21.24f, 0.06f, 27.13f, -6.51f, 27.85f, -8.33f)
                curveToRelative(0.83f, -2.1f, 0.35f, -4.17f, 0.17f, -4.12f)
                curveToRelative(-0.13f, 0.04f, -0.55f, 1.13f, -1.12f, 1.76f)
                curveToRelative(-0.57f, 0.64f, -4.64f, 5.47f, -15.71f, 6.74f)
                reflectiveCurveToRelative(-22.45f, 0.64f, -29.76f, -1.53f)
                reflectiveCurveToRelative(-9.29f, -5.47f, -9.35f, -5.31f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(37.8f, 111.81f)
                curveToRelative(-0.55f, 0.9f, -0.23f, 2.39f, 1.97f, 3.75f)
                curveToRelative(3.18f, 1.97f, 10.37f, 3.62f, 14.31f, 3.43f)
                curveToRelative(3.94f, -0.19f, 4.01f, -2.73f, 0.45f, -3.31f)
                curveToRelative(-3.56f, -0.57f, -8.52f, -1.02f, -12.85f, -3.37f)
                curveToRelative(-1.15f, -0.61f, -2.99f, -1.96f, -3.88f, -0.5f)
                close()
                moveTo(59.81f, 111.37f)
                curveToRelative(0.14f, 1.79f, 2.86f, 1.4f, 4.2f, 1.4f)
                curveToRelative(1.34f, 0.0f, 3.12f, -0.13f, 3.05f, -1.59f)
                curveToRelative(-0.06f, -1.46f, -2.48f, -1.33f, -3.5f, -1.27f)
                curveToRelative(-1.21f, 0.06f, -3.88f, -0.2f, -3.75f, 1.46f)
                close()
                moveTo(63.81f, 67.62f)
                curveToRelative(-2.08f, 0.03f, -2.95f, 0.89f, -2.97f, 7.75f)
                curveToRelative(-0.02f, 5.72f, -0.27f, 26.59f, -0.59f, 27.74f)
                reflectiveCurveToRelative(-1.21f, 3.43f, -1.02f, 4.52f)
                curveToRelative(0.19f, 1.08f, 1.46f, 1.53f, 4.07f, 1.53f)
                curveToRelative(2.61f, 0.0f, 3.94f, -1.21f, 4.01f, -2.73f)
                curveToRelative(0.06f, -1.53f, -0.51f, -2.16f, -0.7f, -3.37f)
                curveToRelative(-0.19f, -1.21f, -0.16f, -21.71f, -0.16f, -27.37f)
                curveToRelative(0.0f, -4.43f, 0.05f, -8.12f, -2.64f, -8.07f)
                close()
                moveTo(23.21f, 25.31f)
                curveToRelative(-1.26f, 1.26f, 12.15f, 14.21f, 17.3f, 19.36f)
                reflectiveCurveToRelative(17.34f, 16.14f, 18.46f, 14.94f)
                reflectiveCurveTo(46.42f, 45.89f, 41.8f, 41.32f)
                curveToRelative(-7.13f, -7.04f, -17.3f, -17.3f, -18.59f, -16.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBFBFBF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.78f, 15.1f)
                curveToRelative(-0.15f, -2.38f, 1.93f, -11.44f, 52.42f, -10.84f)
                curveToRelative(49.74f, 0.59f, 50.49f, 11.88f, 50.34f, 13.96f)
                curveToRelative(-0.15f, 2.08f, -4.45f, -0.89f, -4.45f, -0.89f)
                reflectiveCurveToRelative(-9.85f, -8.88f, -46.37f, -9.02f)
                curveToRelative(-36.53f, -0.15f, -46.28f, 4.12f, -46.58f, 6.35f)
                reflectiveCurveToRelative(-0.59f, 4.01f, -0.59f, 4.01f)
                lineToRelative(-4.77f, -3.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF8C977)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(76.74f, 32.82f)
                curveToRelative(9.75f, -7.8f, 30.02f, -23.97f, 31.61f, -25.22f)
                curveToRelative(2.19f, -1.72f, 8.54f, -5.72f, 9.09f, -5.17f)
                curveToRelative(0.55f, 0.55f, -3.76f, 6.03f, -6.58f, 8.7f)
                curveToRelative(-1.73f, 1.63f, -15.72f, 13.12f, -25.32f, 20.9f)
                curveToRelative(-6.08f, 4.93f, -12.5f, 3.75f, -8.8f, 0.79f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE3C08F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(70.04f, 38.47f)
                lineToRelative(6.7f, -5.66f)
                reflectiveCurveToRelative(2.21f, -0.09f, 4.3f, -0.26f)
                curveToRelative(1.8f, -0.15f, 4.48f, -0.5f, 4.48f, -0.5f)
                lineTo(73.1f, 42.38f)
                lineToRelative(-3.06f, -3.91f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF919F50)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.98f, 37.92f)
                curveToRelative(-2.99f, -3.64f, -9.65f, -3.29f, -13.63f, 0.31f)
                curveToRelative(-4.15f, 3.76f, -4.86f, 9.32f, -1.65f, 13.0f)
                reflectiveCurveToRelative(8.38f, 4.07f, 13.24f, -0.24f)
                curveToRelative(4.86f, -4.3f, 4.23f, -10.41f, 2.04f, -13.07f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE8DE9F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(61.27f, 43.48f)
                curveToRelative(1.1f, 1.33f, 2.66f, 0.78f, 3.92f, -0.24f)
                curveToRelative(1.23f, -1.0f, 2.0f, -2.49f, 1.1f, -3.6f)
                curveToRelative(-1.02f, -1.25f, -2.66f, -0.63f, -3.76f, 0.24f)
                reflectiveCurveToRelative(-2.19f, 2.48f, -1.26f, 3.6f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEF3E24)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(59.43f, 46.44f)
                curveToRelative(-1.4f, 0.89f, -0.11f, 3.29f, 0.67f, 4.3f)
                curveToRelative(0.56f, 0.72f, 2.18f, 2.57f, 3.46f, 1.51f)
                curveToRelative(1.28f, -1.06f, 0.0f, -3.07f, -0.89f, -4.41f)
                curveToRelative(-0.56f, -0.83f, -1.8f, -2.32f, -3.24f, -1.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBFBFBF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.1f, 29.37f)
                curveToRelative(20.93f, 0.34f, 43.51f, -1.39f, 49.34f, -7.18f)
                curveToRelative(5.15f, -5.11f, -0.44f, -8.94f, -0.44f, -8.94f)
                lineToRelative(-1.92f, 4.06f)
                lineToRelative(-3.0f, 3.46f)
                lineToRelative(-8.8f, 2.63f)
                lineToRelative(-9.0f, 1.31f)
                lineToRelative(-18.5f, 1.01f)
                lineToRelative(-19.41f, -0.51f)
                lineToRelative(-14.76f, -2.31f)
                lineToRelative(-11.43f, -2.33f)
                lineToRelative(-7.68f, -4.04f)
                lineToRelative(-1.92f, -3.54f)
                reflectiveCurveToRelative(-3.73f, 0.97f, -3.79f, 1.8f)
                curveToRelative(-0.1f, 1.34f, 0.58f, 2.74f, 1.56f, 3.96f)
                curveToRelative(1.87f, 2.33f, 12.03f, 10.02f, 49.75f, 10.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEFEFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(72.22f, 26.22f)
                curveToRelative(-2.39f, 0.0f, -4.92f, -0.03f, -7.59f, -0.09f)
                curveToRelative(-33.41f, -0.73f, -44.1f, -5.48f, -47.2f, -9.34f)
                curveToRelative(-1.22f, -1.52f, -1.06f, -2.69f, -1.02f, -2.91f)
                curveToRelative(0.11f, -0.54f, 0.64f, -0.89f, 1.18f, -0.78f)
                curveToRelative(0.53f, 0.1f, 0.87f, 0.6f, 0.79f, 1.13f)
                curveToRelative(0.0f, 0.06f, 0.01f, 0.66f, 0.8f, 1.54f)
                curveToRelative(2.54f, 2.81f, 11.83f, 7.63f, 45.48f, 8.36f)
                curveToRelative(34.79f, 0.76f, 43.16f, -3.87f, 45.14f, -5.9f)
                curveToRelative(0.72f, -0.73f, 0.7f, -1.22f, 0.7f, -1.22f)
                curveToRelative(-0.11f, -0.54f, 0.25f, -1.04f, 0.79f, -1.14f)
                curveToRelative(0.54f, -0.11f, 1.07f, 0.28f, 1.18f, 0.82f)
                curveToRelative(0.04f, 0.2f, 0.18f, 1.27f, -0.93f, 2.6f)
                curveToRelative(-2.7f, 3.24f, -11.85f, 6.93f, -39.32f, 6.93f)
                close()
            }
        }
            .build()
        return _cocktail!!
    }

private var _cocktail: ImageVector? = null
