package ru.tech.imageresizershrinker.theme.emoji

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
import ru.tech.imageresizershrinker.theme.Emoji

val Emoji.Blowfish: ImageVector
    get() {
        if (_blowfish != null) {
            return _blowfish!!
        }
        _blowfish = Builder(
            name = "Blowfish", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF8A5A54)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.32f, 41.05f)
                reflectiveCurveToRelative(8.92f, -7.88f, 9.1f, -7.7f)
                reflectiveCurveToRelative(3.66f, 4.97f, 3.66f, 7.41f)
                curveToRelative(0.0f, 3.05f, -2.72f, 6.38f, -2.72f, 6.38f)
                reflectiveCurveToRelative(11.54f, -6.38f, 12.2f, -6.19f)
                reflectiveCurveToRelative(7.98f, 13.61f, 7.98f, 14.55f)
                reflectiveCurveToRelative(-6.85f, 15.11f, -7.51f, 15.3f)
                curveToRelative(-0.66f, 0.19f, -11.07f, -4.79f, -11.07f, -4.79f)
                reflectiveCurveToRelative(3.57f, 4.32f, 3.1f, 7.88f)
                curveToRelative(-0.47f, 3.57f, -2.72f, 9.01f, -3.28f, 9.29f)
                reflectiveCurveToRelative(-11.36f, -6.76f, -11.36f, -6.76f)
                lineToRelative(-0.1f, -35.37f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCD7955)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.46f, 44.8f)
                reflectiveCurveToRelative(-6.1f, -13.7f, -5.73f, -14.08f)
                curveToRelative(0.38f, -0.38f, 13.33f, 6.95f, 13.33f, 6.95f)
                lineToRelative(-7.6f, 7.13f)
                close()
                moveTo(32.41f, 33.63f)
                reflectiveCurveToRelative(0.09f, -16.61f, 0.66f, -16.89f)
                curveToRelative(0.56f, -0.28f, 11.17f, 12.86f, 11.17f, 12.86f)
                lineToRelative(-11.83f, 4.03f)
                close()
                moveTo(49.87f, 28.38f)
                reflectiveCurveToRelative(5.82f, -15.39f, 6.38f, -15.39f)
                curveToRelative(0.56f, 0.0f, 7.32f, 17.55f, 7.32f, 17.55f)
                lineToRelative(-13.7f, -2.16f)
                close()
                moveTo(65.92f, 28.85f)
                reflectiveCurveToRelative(8.82f, -13.05f, 9.67f, -12.86f)
                reflectiveCurveToRelative(2.35f, 16.99f, 2.35f, 16.99f)
                lineToRelative(-12.02f, -4.13f)
                close()
                moveTo(78.78f, 32.69f)
                reflectiveCurveToRelative(12.95f, -9.1f, 13.51f, -8.82f)
                curveToRelative(0.56f, 0.28f, -0.94f, 17.74f, -0.94f, 17.74f)
                lineToRelative(-12.57f, -8.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBA8D72)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.14f, 66.29f)
                reflectiveCurveTo(3.78f, 74.46f, 3.88f, 75.77f)
                reflectiveCurveToRelative(15.2f, 2.53f, 15.2f, 2.53f)
                lineToRelative(-3.94f, -12.01f)
                close()
                moveTo(21.9f, 83.66f)
                reflectiveCurveToRelative(-8.35f, 11.07f, -7.88f, 11.83f)
                curveToRelative(0.47f, 0.75f, 15.49f, -3.28f, 15.49f, -3.28f)
                lineToRelative(-7.61f, -8.55f)
                close()
                moveTo(32.98f, 95.58f)
                reflectiveCurveToRelative(-2.06f, 16.33f, -1.13f, 16.71f)
                reflectiveCurveToRelative(12.39f, -11.17f, 12.39f, -11.17f)
                lineToRelative(-11.26f, -5.54f)
                close()
                moveTo(50.06f, 101.77f)
                reflectiveCurveToRelative(7.51f, 15.02f, 8.07f, 15.02f)
                curveToRelative(0.56f, 0.0f, 5.16f, -16.05f, 5.16f, -16.05f)
                lineToRelative(-13.23f, 1.03f)
                close()
                moveTo(70.05f, 99.33f)
                reflectiveCurveToRelative(10.79f, 9.39f, 11.83f, 9.01f)
                curveToRelative(1.03f, -0.38f, -0.84f, -15.39f, -0.84f, -15.39f)
                lineToRelative(-10.99f, 6.38f)
                close()
                moveTo(85.06f, 91.26f)
                reflectiveCurveToRelative(14.83f, 5.16f, 15.3f, 4.6f)
                curveToRelative(0.47f, -0.56f, -7.7f, -15.02f, -7.7f, -15.02f)
                lineToRelative(-7.6f, 10.42f)
                close()
                moveTo(97.59f, 54.66f)
                curveToRelative(0.42f, 1.9f, 8.17f, -2.11f, 10.28f, -3.1f)
                curveToRelative(2.11f, -0.99f, 8.66f, -3.94f, 8.02f, -5.07f)
                curveToRelative(-0.63f, -1.13f, -7.53f, 2.04f, -9.29f, 2.82f)
                curveToRelative(-1.76f, 0.77f, -9.22f, 4.38f, -9.01f, 5.35f)
                close()
                moveTo(97.5f, 57.19f)
                curveToRelative(-0.05f, 1.12f, 2.11f, 1.81f, 10.14f, 1.38f)
                reflectiveCurveToRelative(11.76f, -1.81f, 11.68f, -2.93f)
                curveToRelative(-0.07f, -1.13f, -9.08f, -0.66f, -12.04f, -0.45f)
                reflectiveCurveToRelative(-9.71f, 0.52f, -9.78f, 2.0f)
                close()
                moveTo(99.33f, 60.29f)
                curveToRelative(-0.28f, 0.61f, 5.63f, 3.07f, 9.78f, 4.27f)
                curveToRelative(4.15f, 1.2f, 7.41f, 2.37f, 7.77f, 1.31f)
                curveToRelative(0.35f, -1.06f, -2.98f, -2.44f, -7.06f, -3.78f)
                curveToRelative(-3.39f, -1.11f, -10.0f, -2.86f, -10.49f, -1.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF3A25C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.99f, 67.3f)
                reflectiveCurveToRelative(-1.55f, -0.77f, -1.76f, -2.18f)
                curveToRelative(-0.21f, -1.41f, -0.07f, -6.97f, 1.48f, -12.32f)
                reflectiveCurveToRelative(5.0f, -10.49f, 5.0f, -10.49f)
                lineToRelative(-2.6f, -6.48f)
                lineToRelative(5.84f, 2.53f)
                reflectiveCurveToRelative(3.45f, -3.03f, 5.84f, -4.36f)
                curveToRelative(2.39f, -1.34f, 5.28f, -2.67f, 5.28f, -2.67f)
                lineToRelative(0.35f, -7.93f)
                lineToRelative(4.79f, 5.96f)
                reflectiveCurveToRelative(3.03f, -0.84f, 6.83f, -1.13f)
                curveToRelative(3.8f, -0.28f, 6.62f, -0.56f, 6.62f, -0.56f)
                lineToRelative(2.79f, -7.18f)
                lineToRelative(2.35f, 7.32f)
                reflectiveCurveToRelative(2.53f, -0.07f, 5.63f, 0.14f)
                curveToRelative(3.1f, 0.21f, 5.44f, 0.52f, 5.44f, 0.52f)
                lineToRelative(3.75f, -5.82f)
                lineToRelative(1.08f, 7.13f)
                reflectiveCurveToRelative(2.35f, 0.84f, 4.32f, 1.69f)
                reflectiveCurveToRelative(4.62f, 2.32f, 4.62f, 2.32f)
                lineToRelative(5.56f, -4.36f)
                lineToRelative(-0.77f, 7.39f)
                reflectiveCurveToRelative(10.63f, 5.35f, 13.02f, 19.36f)
                reflectiveCurveToRelative(-2.77f, 18.65f, -2.77f, 18.65f)
                lineTo(15.99f, 67.3f)
                close()
            }
            path(
                fill = radialGradient(
                    0.622f to Color(0xFFF4D6B5), 0.744f to Color(0xFFF1D2B2),
                    0.873f to Color(0xFFE7C5AA), 1.0f to Color(0xFFD8B19C), center =
                    Offset(57.186897f, 49.68204f), radius = 52.185352f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(37.72f, 54.45f)
                curveToRelative(-12.87f, 3.98f, -23.37f, 11.19f, -23.37f, 11.19f)
                lineToRelative(0.77f, 3.45f)
                lineToRelative(-5.77f, 5.35f)
                lineToRelative(7.32f, 0.99f)
                reflectiveCurveToRelative(0.58f, 1.87f, 2.7f, 5.74f)
                reflectiveCurveToRelative(3.64f, 5.38f, 3.64f, 5.38f)
                lineToRelative(-3.78f, 5.39f)
                lineToRelative(7.02f, -1.45f)
                reflectiveCurveToRelative(2.56f, 2.92f, 4.39f, 4.33f)
                curveToRelative(1.83f, 1.41f, 4.77f, 3.76f, 4.77f, 3.76f)
                lineToRelative(-0.92f, 7.46f)
                lineToRelative(6.05f, -5.14f)
                reflectiveCurveToRelative(2.75f, 0.99f, 6.12f, 1.62f)
                curveToRelative(3.38f, 0.63f, 6.97f, 0.7f, 6.97f, 0.7f)
                lineToRelative(3.87f, 7.32f)
                lineToRelative(2.67f, -7.53f)
                reflectiveCurveToRelative(4.29f, -0.49f, 6.83f, -0.99f)
                curveToRelative(2.53f, -0.49f, 6.55f, -2.53f, 6.55f, -2.53f)
                lineToRelative(6.76f, 5.0f)
                lineToRelative(-1.06f, -8.02f)
                reflectiveCurveToRelative(3.8f, -2.39f, 5.42f, -3.59f)
                curveToRelative(1.62f, -1.2f, 3.8f, -3.38f, 3.8f, -3.38f)
                lineToRelative(6.97f, 2.25f)
                lineToRelative(-3.17f, -6.83f)
                reflectiveCurveToRelative(3.17f, -3.87f, 4.72f, -6.05f)
                curveToRelative(1.55f, -2.18f, 3.73f, -7.39f, 3.73f, -7.39f)
                reflectiveCurveToRelative(-7.84f, -12.38f, -25.77f, -17.8f)
                curveToRelative(-15.13f, -4.58f, -29.49f, -1.62f, -37.23f, 0.77f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFBFFFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.09f, 78.59f)
                curveToRelative(0.91f, 0.61f, 9.85f, -9.57f, 9.85f, -9.57f)
                reflectiveCurveToRelative(-0.47f, -2.53f, -3.0f, -3.75f)
                curveToRelative(-2.53f, -1.22f, -4.79f, -0.19f, -4.79f, -0.19f)
                reflectiveCurveToRelative(-2.9f, 12.94f, -2.06f, 13.51f)
                close()
                moveTo(50.99f, 63.76f)
                reflectiveCurveToRelative(1.97f, -2.72f, 6.01f, -2.44f)
                reflectiveCurveToRelative(5.73f, 2.25f, 5.73f, 2.25f)
                reflectiveCurveToRelative(-4.13f, 14.45f, -5.07f, 14.45f)
                curveToRelative(-0.94f, 0.0f, -6.67f, -14.26f, -6.67f, -14.26f)
                close()
                moveTo(39.45f, 92.95f)
                curveToRelative(0.8f, 0.8f, 11.17f, -8.92f, 11.17f, -8.92f)
                reflectiveCurveToRelative(-1.22f, -2.63f, -3.85f, -4.41f)
                curveToRelative(-2.63f, -1.78f, -6.76f, -0.66f, -6.76f, -0.66f)
                reflectiveCurveToRelative(-1.31f, 13.24f, -0.56f, 13.99f)
                close()
                moveTo(64.13f, 80.65f)
                reflectiveCurveToRelative(0.7f, -2.96f, 3.94f, -4.04f)
                curveToRelative(3.66f, -1.22f, 5.91f, 0.47f, 5.91f, 0.47f)
                reflectiveCurveToRelative(0.56f, 12.67f, -0.47f, 13.23f)
                reflectiveCurveToRelative(-9.38f, -9.66f, -9.38f, -9.66f)
                close()
                moveTo(74.93f, 67.61f)
                reflectiveCurveToRelative(-0.47f, -3.0f, 1.88f, -4.79f)
                reflectiveCurveToRelative(4.88f, -1.22f, 4.88f, -1.22f)
                reflectiveCurveToRelative(5.16f, 11.92f, 4.5f, 12.48f)
                curveToRelative(-0.66f, 0.57f, -11.26f, -6.47f, -11.26f, -6.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCD7955)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.28f, 40.01f)
                curveToRelative(0.9f, 0.9f, 2.53f, -0.84f, 3.19f, -1.6f)
                curveToRelative(0.66f, -0.75f, 2.02f, -2.16f, 2.02f, -2.16f)
                lineToRelative(2.4f, 1.75f)
                reflectiveCurveToRelative(1.92f, 1.92f, 3.1f, 0.75f)
                curveToRelative(0.73f, -0.73f, -1.74f, -3.71f, -2.77f, -4.83f)
                reflectiveCurveToRelative(-2.3f, -2.39f, -3.1f, -2.25f)
                curveToRelative(-0.93f, 0.16f, -2.27f, 2.13f, -3.05f, 3.38f)
                curveToRelative(-1.41f, 2.24f, -2.54f, 4.21f, -1.79f, 4.96f)
                close()
                moveTo(72.96f, 42.69f)
                curveToRelative(0.51f, 1.69f, 3.44f, 0.23f, 4.22f, -0.05f)
                curveToRelative(1.08f, -0.38f, 2.49f, -0.89f, 2.49f, -0.89f)
                reflectiveCurveToRelative(0.19f, 1.36f, 0.33f, 2.96f)
                curveToRelative(0.09f, 1.0f, 0.28f, 3.05f, 1.64f, 3.0f)
                curveToRelative(1.41f, -0.05f, 1.42f, -2.54f, 1.36f, -3.8f)
                curveToRelative(-0.19f, -3.75f, 0.0f, -5.87f, -0.89f, -6.43f)
                curveToRelative(-0.89f, -0.56f, -5.68f, 2.06f, -6.66f, 2.63f)
                reflectiveCurveToRelative(-2.78f, 1.64f, -2.49f, 2.58f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFDC1E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(30.86f, 38.75f)
                curveToRelative(-4.65f, 1.74f, -5.82f, 5.82f, -5.21f, 8.4f)
                curveToRelative(0.61f, 2.58f, 3.14f, 6.52f, 10.18f, 5.3f)
                curveToRelative(6.35f, -1.1f, 6.72f, -7.23f, 5.3f, -10.23f)
                curveToRelative(-1.59f, -3.38f, -5.57f, -5.23f, -10.27f, -3.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF282F2F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.89f, 45.22f)
                curveToRelative(-0.14f, 2.91f, 2.54f, 4.06f, 5.07f, 3.89f)
                curveToRelative(1.45f, -0.09f, 4.5f, -1.13f, 4.32f, -4.18f)
                curveToRelative(-0.18f, -2.86f, -2.63f, -3.47f, -5.16f, -3.43f)
                curveToRelative(-1.84f, 0.05f, -4.11f, 1.38f, -4.23f, 3.72f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB34B4B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(23.17f, 53.01f)
                curveToRelative(0.17f, -4.31f, -3.0f, -7.09f, -7.46f, -6.34f)
                curveToRelative(-4.32f, 0.73f, -3.46f, 5.94f, -3.33f, 6.52f)
                curveToRelative(0.19f, 0.84f, 0.66f, 1.97f, 0.66f, 1.97f)
                reflectiveCurveTo(12.0f, 55.83f, 12.0f, 57.89f)
                reflectiveCurveToRelative(0.78f, 4.05f, 4.5f, 3.52f)
                curveToRelative(3.95f, -0.56f, 6.53f, -4.92f, 6.67f, -8.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA82612)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(14.06f, 54.77f)
                curveToRelative(0.14f, 0.42f, -0.24f, 1.9f, 1.78f, 1.88f)
                curveToRelative(4.32f, -0.05f, 5.19f, -2.63f, 5.23f, -3.19f)
                curveToRelative(0.05f, -0.56f, -0.56f, -2.06f, -4.93f, -1.97f)
                curveToRelative(-3.29f, 0.07f, -2.27f, 2.71f, -2.08f, 3.28f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF885B52)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(49.02f, 52.78f)
                curveToRelative(0.24f, 2.38f, 3.97f, 3.53f, 8.35f, 4.13f)
                curveToRelative(4.79f, 0.66f, 8.21f, 0.47f, 8.21f, 0.47f)
                reflectiveCurveToRelative(2.35f, -1.13f, 2.3f, -7.79f)
                curveToRelative(-0.05f, -6.48f, -2.58f, -7.32f, -2.58f, -7.32f)
                reflectiveCurveToRelative(-6.43f, 0.47f, -11.5f, 4.22f)
                curveToRelative(-2.8f, 2.08f, -4.96f, 4.41f, -4.78f, 6.29f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBA8D72)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.15f, 46.44f)
                curveToRelative(-3.51f, 0.92f, -11.31f, 3.99f, -11.12f, 6.34f)
                curveToRelative(0.14f, 1.74f, 3.89f, 1.31f, 9.1f, 1.27f)
                curveToRelative(5.5f, -0.05f, 9.16f, 0.53f, 9.16f, 0.53f)
                reflectiveCurveToRelative(0.17f, -0.47f, 0.26f, -0.87f)
                curveToRelative(0.11f, -0.48f, 0.13f, -1.02f, 0.13f, -1.02f)
                reflectiveCurveToRelative(-4.39f, -0.78f, -8.56f, -0.66f)
                curveToRelative(-3.28f, 0.09f, -7.79f, 0.7f, -6.8f, -0.42f)
                curveToRelative(0.35f, -0.4f, 5.58f, -2.58f, 8.45f, -3.1f)
                curveToRelative(2.86f, -0.52f, 7.04f, -0.79f, 7.04f, -0.79f)
                reflectiveCurveToRelative(-0.05f, -0.6f, -0.14f, -1.2f)
                curveToRelative(-0.06f, -0.4f, -0.21f, -1.04f, -0.21f, -1.04f)
                reflectiveCurveToRelative(-4.26f, 0.17f, -7.31f, 0.96f)
                close()
            }
        }
            .build()
        return _blowfish!!
    }

private var _blowfish: ImageVector? = null
