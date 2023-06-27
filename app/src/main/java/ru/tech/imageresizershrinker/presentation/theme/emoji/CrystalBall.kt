package ru.tech.imageresizershrinker.presentation.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.theme.Emoji

val Emoji.CrystalBall: ImageVector
    get() {
        if (_crystalball != null) {
            return _crystalball!!
        }
        _crystalball = Builder(
            name = "CrystalBall", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF8B5738)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(31.13f, 110.55f)
                curveToRelative(0.0f, -4.3f, 6.01f, -19.49f, 6.01f, -19.49f)
                curveToRelative(5.79f, -1.79f, 47.6f, -0.27f, 53.97f, -0.27f)
                curveToRelative(0.0f, 0.0f, 5.76f, 12.33f, 5.76f, 19.76f)
                reflectiveCurveTo(79.19f, 124.0f, 64.06f, 124.0f)
                reflectiveCurveToRelative(-32.93f, -6.02f, -32.93f, -13.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFB17A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(37.01f, 93.5f)
                arcToRelative(27.05f, 11.75f, 0.0f, true, false, 54.1f, 0.0f)
                arcToRelative(27.05f, 11.75f, 0.0f, true, false, -54.1f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCC8552)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(38.67f, 106.15f)
                curveToRelative(3.02f, 1.72f, 7.8f, 1.59f, 11.51f, 2.26f)
                curveToRelative(4.72f, 0.86f, 7.39f, 4.46f, 16.49f, 4.3f)
                curveToRelative(3.25f, -0.06f, 10.83f, -0.43f, 10.41f, -0.42f)
                curveToRelative(-0.26f, -0.53f, -1.74f, -1.04f, -4.62f, -1.49f)
                curveToRelative(-2.64f, -0.42f, -4.54f, -0.38f, -7.6f, -0.38f)
                curveToRelative(-2.34f, 0.0f, -5.55f, -0.7f, -8.79f, -2.48f)
                curveToRelative(-1.48f, -0.81f, -3.78f, -1.71f, -6.03f, -2.17f)
                curveToRelative(-3.46f, -0.71f, -5.87f, 0.04f, -10.7f, -2.25f)
                curveToRelative(-2.31f, -1.09f, -4.18f, -3.35f, -4.62f, -5.98f)
                lineToRelative(-1.14f, 3.28f)
                curveToRelative(1.5f, 2.75f, 2.87f, 4.07f, 5.09f, 5.33f)
                close()
                moveTo(88.44f, 103.46f)
                curveToRelative(-7.74f, 4.92f, -14.28f, 4.37f, -14.28f, 4.37f)
                curveToRelative(-0.55f, 0.36f, 3.96f, 1.33f, 7.31f, 0.77f)
                curveToRelative(6.58f, -1.1f, 10.58f, -4.4f, 13.18f, -8.82f)
                curveToRelative(-0.36f, -1.12f, -1.15f, -3.24f, -1.15f, -3.24f)
                curveToRelative(-0.75f, 1.81f, -1.62f, 4.73f, -5.06f, 6.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA86D44)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.51f, 117.42f)
                curveToRelative(-4.28f, 0.78f, -8.38f, -0.55f, -12.59f, -1.54f)
                curveToRelative(-5.77f, -1.35f, -16.61f, -1.16f, -16.61f, -1.16f)
                curveToRelative(-0.65f, 0.32f, 5.11f, 2.41f, 7.91f, 2.65f)
                curveToRelative(2.55f, 0.22f, 5.25f, 0.0f, 7.67f, 0.99f)
                curveToRelative(3.41f, 1.39f, 6.71f, 2.09f, 10.01f, 2.09f)
                curveToRelative(6.27f, 0.0f, 14.46f, -3.78f, 16.41f, -8.69f)
                curveToRelative(0.0f, 0.0f, -3.58f, 2.1f, -6.36f, 3.51f)
                curveToRelative(-2.02f, 1.03f, -4.2f, 1.75f, -6.44f, 2.15f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA86D44)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.82f, 111.48f)
                curveToRelative(0.85f, 1.36f, 1.72f, 2.62f, 2.86f, 3.75f)
                curveToRelative(1.93f, 1.92f, 4.37f, 3.03f, 5.94f, 2.75f)
                curveToRelative(0.0f, 0.0f, -5.56f, -4.49f, -4.97f, -5.66f)
                curveToRelative(0.3f, -0.6f, 11.84f, 0.35f, 11.84f, 0.35f)
                reflectiveCurveToRelative(-3.5f, -1.84f, -6.91f, -2.5f)
                curveToRelative(-2.21f, -0.43f, -6.25f, 0.43f, -9.93f, -2.16f)
                curveToRelative(-2.03f, -1.43f, -2.34f, -3.15f, -2.34f, -3.15f)
                reflectiveCurveToRelative(-0.73f, 2.49f, -0.88f, 3.44f)
                curveToRelative(2.13f, 2.23f, 4.11f, 2.73f, 4.39f, 3.18f)
                close()
                moveTo(94.3f, 106.54f)
                curveToRelative(-2.02f, 1.81f, -5.21f, 5.47f, -5.21f, 7.36f)
                curveToRelative(0.0f, 0.0f, 3.58f, -3.47f, 5.99f, -5.06f)
                curveToRelative(0.6f, -0.39f, 1.03f, -0.89f, 1.5f, -1.42f)
                curveToRelative(0.0f, 0.0f, -0.24f, -1.79f, -0.62f, -3.06f)
                curveToRelative(-0.15f, 0.85f, -0.76f, 1.38f, -1.66f, 2.18f)
                close()
            }
            path(
                fill = radialGradient(
                    0.104f to Color(0xFFCE93D8), 1.0f to Color(0xFFAB47BC),
                    center = Offset(79.82582f, 22.159212f), radius = 76.184f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.0f, 52.76f)
                moveToRelative(-48.76f, 0.0f)
                arcToRelative(48.76f, 48.76f, 0.0f, true, true, 97.52f, 0.0f)
                arcToRelative(48.76f, 48.76f, 0.0f, true, true, -97.52f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.28f to Color(0x0081D4FA), 0.964f to Color(0xE581D4FA),
                    center = Offset(53.349f, 46.135f), radius = 69.389f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.0f, 52.76f)
                moveToRelative(-48.76f, 0.0f)
                arcToRelative(48.76f, 48.76f, 0.0f, true, true, 97.52f, 0.0f)
                arcToRelative(48.76f, 48.76f, 0.0f, true, true, -97.52f, 0.0f)
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFF673AB7), 0.937f to Color(0x00673AB7), start
                    = Offset(74.69f, 10.651f), end = Offset(35.942f, 111.007f)
                ), stroke = null,
                fillAlpha = 0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap =
                Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(45.91f, 22.37f)
                curveToRelative(4.54f, 0.39f, 13.84f, 3.79f, 7.07f, 12.51f)
                curveTo(48.53f, 40.61f, 42.97f, 45.94f, 41.0f, 50.55f)
                curveToRelative(-5.69f, 13.33f, 8.57f, 18.77f, 13.0f, 20.16f)
                curveToRelative(3.84f, 1.21f, 11.24f, 3.91f, 13.32f, 7.36f)
                curveTo(75.35f, 91.37f, 50.93f, 95.0f, 50.93f, 95.0f)
                reflectiveCurveToRelative(17.97f, 6.55f, 32.77f, -3.46f)
                curveToRelative(5.6f, -3.79f, 10.53f, -12.61f, 5.71f, -20.55f)
                curveToRelative(-2.34f, -3.85f, -8.79f, -7.32f, -10.37f, -8.56f)
                curveToRelative(-6.76f, -5.34f, -10.93f, -10.86f, -7.57f, -17.05f)
                curveToRelative(1.36f, -2.5f, 4.2f, -3.8f, 6.95f, -4.56f)
                curveToRelative(7.7f, -2.13f, 17.31f, -1.75f, 19.82f, -11.26f)
                curveTo(100.41f, 21.36f, 84.57f, 3.04f, 60.0f, 6.02f)
                curveToRelative(-10.2f, 1.23f, -19.38f, 5.5f, -26.89f, 12.52f)
                curveToRelative(-13.86f, 12.97f, -12.66f, 23.7f, -12.66f, 23.7f)
                reflectiveCurveToRelative(9.03f, -21.3f, 25.46f, -19.87f)
                close()
            }
            path(
                fill = linearGradient(
                    0.235f to Color(0xFF1D44B3), 0.884f to Color(0x122044B3),
                    0.936f to Color(0x002144B3), start = Offset(80.878f, 24.934f), end =
                    Offset(80.878f, 108.077f)
                ), stroke = null, fillAlpha = 0.39f, strokeAlpha =
                0.39f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(99.77f, 24.22f)
                curveToRelative(-3.48f, -5.06f, -8.29f, -9.08f, -13.36f, -12.55f)
                curveToRelative(-8.09f, -5.53f, -18.55f, -5.98f, -18.55f, -5.98f)
                curveToRelative(12.84f, 6.14f, 11.4f, 14.28f, 7.21f, 20.1f)
                curveToRelative(-5.1f, 7.09f, -15.33f, 11.88f, -18.0f, 20.51f)
                curveToRelative(-1.56f, 5.04f, -0.6f, 10.34f, 4.31f, 13.56f)
                reflectiveCurveToRelative(22.58f, 11.77f, 23.19f, 21.89f)
                curveToRelative(0.46f, 7.62f, -5.6f, 12.42f, -5.6f, 12.42f)
                curveToRelative(7.09f, -1.53f, 22.01f, -11.97f, 17.59f, -22.71f)
                curveToRelative(-2.14f, -5.2f, -11.87f, -8.65f, -6.97f, -14.58f)
                curveToRelative(1.82f, -2.21f, 14.53f, -3.39f, 15.72f, -16.25f)
                curveToRelative(0.55f, -6.01f, -2.69f, -12.27f, -5.54f, -16.41f)
                close()
            }
            path(
                fill = linearGradient(
                    0.227f to Color(0xFFFFFFFF), 1.0f to Color(0x00FFFFFF), start
                    = Offset(32.907246f, 16.482409f), end = Offset(41.63574f, 49.018963f)
                ), stroke =
                null, fillAlpha = 0.85f, strokeAlpha = 0.85f, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(24.71f, 61.66f)
                reflectiveCurveToRelative(-7.3f, -19.36f, 7.69f, -37.29f)
                curveToRelative(12.35f, -14.78f, 23.5f, -8.8f, 21.78f, -2.5f)
                curveToRelative(-1.73f, 6.3f, -7.1f, 8.24f, -15.19f, 14.57f)
                curveTo(28.3f, 44.8f, 24.71f, 61.66f, 24.71f, 61.66f)
                close()
            }
            path(
                fill = linearGradient(
                    0.261f to Color(0xFFFFFFFF), 1.0f to Color(0x00FFFFFF), start
                    = Offset(85.871f, 28.96f), end = Offset(85.871f, 52.387f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(92.96f, 34.77f)
                curveToRelative(-1.95f, -0.97f, -3.51f, -2.19f, -4.19f, -4.26f)
                lineToRelative(-2.89f, -9.57f)
                lineToRelative(-2.89f, 9.57f)
                curveToRelative(-0.68f, 2.06f, -2.25f, 3.29f, -4.19f, 4.26f)
                lineToRelative(-4.83f, 2.09f)
                lineToRelative(4.97f, 2.06f)
                curveToRelative(1.95f, 0.97f, 3.37f, 2.3f, 4.06f, 4.36f)
                lineToRelative(2.89f, 9.49f)
                lineToRelative(2.89f, -9.49f)
                curveToRelative(0.68f, -2.06f, 2.11f, -3.39f, 4.06f, -4.36f)
                lineToRelative(4.97f, -2.06f)
                lineToRelative(-4.85f, -2.09f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFFFFFFF), 1.0f to Color(0x00FFFFFF), start =
                    Offset(76.221f, 58.161f), end = Offset(76.221f, 85.231f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(83.31f, 69.37f)
                curveToRelative(-1.95f, -0.97f, -3.51f, -2.19f, -4.19f, -4.26f)
                lineToRelative(-2.89f, -9.57f)
                lineToRelative(-2.89f, 9.57f)
                curveToRelative(-0.68f, 2.06f, -2.25f, 3.29f, -4.19f, 4.26f)
                lineToRelative(-4.83f, 2.09f)
                lineToRelative(4.97f, 2.06f)
                curveToRelative(1.95f, 0.97f, 3.37f, 2.3f, 4.06f, 4.36f)
                lineToRelative(2.89f, 9.49f)
                lineToRelative(2.89f, -9.49f)
                curveToRelative(0.68f, -2.06f, 2.11f, -3.39f, 4.06f, -4.36f)
                lineToRelative(4.97f, -2.06f)
                lineToRelative(-4.85f, -2.09f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(106.34f, 53.97f)
                curveToRelative(-1.47f, -0.73f, -2.65f, -1.66f, -3.17f, -3.21f)
                lineToRelative(-2.18f, -7.23f)
                lineToRelative(-2.18f, 7.23f)
                curveToRelative(-0.52f, 1.56f, -1.7f, 2.48f, -3.17f, 3.21f)
                lineTo(92.0f, 55.55f)
                lineToRelative(3.75f, 1.56f)
                curveToRelative(1.47f, 0.73f, 2.55f, 1.73f, 3.06f, 3.29f)
                lineToRelative(2.18f, 7.16f)
                lineToRelative(2.18f, -7.16f)
                curveToRelative(0.52f, -1.56f, 1.59f, -2.56f, 3.06f, -3.29f)
                lineToRelative(3.75f, -1.56f)
                lineToRelative(-3.64f, -1.58f)
                close()
            }
        }
            .build()
        return _crystalball!!
    }

private var _crystalball: ImageVector? = null
