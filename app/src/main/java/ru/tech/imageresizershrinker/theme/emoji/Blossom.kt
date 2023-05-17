package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
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

public val Emoji.Blossom: ImageVector
    get() {
        if (_blossom != null) {
            return _blossom!!
        }
        _blossom = Builder(name = "Blossom", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = radialGradient(0.393f to Color(0xFFFFD655), 0.452f to Color(0xFFFFD450),
                    0.521f to Color(0xFFFFD042), 0.596f to Color(0xFFFEC82B), 0.673f to
                    Color(0xFFFEBE0A), 0.692f to Color(0xFFFEBB01), center =
                    Offset(63.917f,63.865f), radius = 58.902f), stroke = null, strokeLineWidth =
                    0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(88.49f, 37.39f)
                reflectiveCurveToRelative(8.02f, -8.17f, 8.31f, -15.63f)
                curveToRelative(0.28f, -7.46f, -2.46f, -9.85f, -6.12f, -11.4f)
                reflectiveCurveTo(83.0f, 12.05f, 83.0f, 12.05f)
                reflectiveCurveToRelative(-0.14f, -4.5f, -4.93f, -6.19f)
                reflectiveCurveToRelative(-10.7f, 2.25f, -12.25f, 8.17f)
                reflectiveCurveToRelative(-1.88f, 16.8f, -1.88f, 16.8f)
                reflectiveCurveTo(63.0f, 19.1f, 59.2f, 12.9f)
                reflectiveCurveToRelative(-7.46f, -7.46f, -12.53f, -5.91f)
                reflectiveCurveToRelative(-4.79f, 7.74f, -4.79f, 7.74f)
                reflectiveCurveToRelative(-4.79f, -4.93f, -10.42f, -2.53f)
                reflectiveCurveToRelative(-3.87f, 11.19f, -3.03f, 13.94f)
                curveToRelative(0.56f, 2.25f, 6.41f, 13.94f, 6.41f, 13.94f)
                reflectiveCurveToRelative(-8.92f, -6.03f, -16.12f, -7.39f)
                curveToRelative(-6.34f, -1.2f, -10.77f, 2.18f, -10.77f, 6.83f)
                reflectiveCurveToRelative(3.52f, 6.9f, 3.52f, 6.9f)
                reflectiveCurveToRelative(-5.66f, 1.52f, -6.76f, 6.9f)
                curveToRelative(-0.77f, 3.8f, 0.75f, 6.68f, 3.45f, 8.73f)
                curveToRelative(4.65f, 3.52f, 19.36f, 4.5f, 19.36f, 4.5f)
                reflectiveCurveToRelative(-10.7f, 0.7f, -16.19f, 4.08f)
                curveToRelative(-5.62f, 3.46f, -6.34f, 8.73f, -5.07f, 11.54f)
                reflectiveCurveToRelative(7.32f, 4.22f, 7.32f, 4.22f)
                reflectiveCurveToRelative(-5.07f, 6.34f, -0.99f, 11.4f)
                reflectiveCurveToRelative(10.14f, 4.93f, 13.94f, 3.1f)
                curveToRelative(3.8f, -1.83f, 12.81f, -9.71f, 12.81f, -9.71f)
                reflectiveCurveToRelative(-7.4f, 11.7f, -7.88f, 18.02f)
                curveToRelative(-0.47f, 6.19f, 2.64f, 8.51f, 6.48f, 8.87f)
                curveToRelative(3.47f, 0.33f, 6.05f, -2.67f, 6.05f, -2.67f)
                reflectiveCurveToRelative(1.41f, 6.05f, 5.91f, 6.76f)
                reflectiveCurveToRelative(9.15f, -1.27f, 11.97f, -7.46f)
                curveToRelative(2.71f, -5.96f, 3.94f, -16.66f, 3.94f, -16.66f)
                reflectiveCurveToRelative(2.67f, 16.94f, 8.59f, 21.3f)
                curveToRelative(5.91f, 4.36f, 10.18f, 2.53f, 12.53f, 0.14f)
                curveToRelative(2.25f, -2.67f, 1.83f, -8.59f, 1.83f, -8.59f)
                reflectiveCurveToRelative(5.07f, 4.08f, 9.01f, 1.55f)
                curveToRelative(3.94f, -2.53f, 3.1f, -7.74f, 1.83f, -12.39f)
                reflectiveCurveTo(92.0f, 87.66f, 92.0f, 87.66f)
                reflectiveCurveToRelative(9.43f, 9.43f, 17.03f, 9.15f)
                curveToRelative(7.6f, -0.28f, 9.44f, -3.48f, 9.85f, -7.32f)
                curveToRelative(0.42f, -3.94f, -2.96f, -7.18f, -2.96f, -7.18f)
                reflectiveCurveToRelative(6.62f, -0.7f, 7.32f, -7.46f)
                curveToRelative(0.7f, -6.76f, -6.34f, -9.01f, -9.29f, -9.85f)
                reflectiveCurveToRelative(-11.4f, -1.13f, -11.4f, -1.13f)
                reflectiveCurveToRelative(12.81f, -3.52f, 16.33f, -7.46f)
                curveToRelative(3.52f, -3.94f, 3.73f, -7.81f, 2.18f, -10.63f)
                curveToRelative(-1.48f, -2.69f, -7.11f, -3.73f, -7.11f, -3.73f)
                reflectiveCurveToRelative(2.53f, -3.38f, 0.7f, -8.31f)
                curveToRelative(-1.83f, -4.93f, -9.43f, -5.07f, -13.37f, -3.8f)
                curveToRelative(-3.92f, 1.25f, -12.79f, 7.45f, -12.79f, 7.45f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF2A05B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(53.15f, 48.6f)
                reflectiveCurveToRelative(-3.1f, -6.19f, -5.35f, -13.84f)
                curveToRelative(-0.84f, -2.86f, -3.45f, -11.35f, -2.82f, -11.87f)
                curveToRelative(0.73f, -0.61f, 6.38f, 9.38f, 7.84f, 12.95f)
                curveToRelative(1.26f, 3.09f, 5.54f, 10.98f, 5.54f, 10.98f)
                lineToRelative(10.7f, -1.83f)
                reflectiveCurveToRelative(3.71f, -8.31f, 4.83f, -10.56f)
                curveToRelative(1.13f, -2.25f, 5.44f, -10.79f, 6.19f, -10.42f)
                curveToRelative(0.89f, 0.44f, -1.13f, 8.21f, -2.82f, 13.0f)
                curveToRelative(-1.69f, 4.79f, -3.85f, 10.79f, -3.85f, 10.79f)
                lineToRelative(9.01f, 6.71f)
                reflectiveCurveToRelative(6.34f, -2.35f, 10.0f, -3.75f)
                curveToRelative(3.66f, -1.41f, 10.28f, -3.38f, 10.56f, -2.53f)
                curveToRelative(0.28f, 0.84f, -3.85f, 3.75f, -9.2f, 6.29f)
                reflectiveCurveToRelative(-10.14f, 5.3f, -10.14f, 5.3f)
                lineToRelative(0.75f, 8.4f)
                reflectiveCurveToRelative(7.7f, 3.89f, 11.22f, 5.73f)
                curveToRelative(3.52f, 1.83f, 10.32f, 5.54f, 10.18f, 6.52f)
                curveToRelative(-0.14f, 0.99f, -9.85f, -1.92f, -12.39f, -2.63f)
                curveToRelative(-2.53f, -0.7f, -12.11f, -3.99f, -12.11f, -3.99f)
                lineToRelative(-5.35f, 6.9f)
                reflectiveCurveToRelative(3.43f, 7.93f, 4.55f, 10.46f)
                curveToRelative(1.13f, 2.53f, 5.16f, 13.19f, 4.46f, 13.61f)
                reflectiveCurveToRelative(-6.15f, -7.98f, -7.88f, -11.12f)
                curveToRelative(-1.74f, -3.14f, -7.04f, -11.83f, -7.04f, -11.83f)
                lineToRelative(-9.85f, 3.24f)
                reflectiveCurveToRelative(-3.24f, 7.18f, -5.21f, 11.26f)
                reflectiveCurveToRelative(-6.48f, 11.31f, -7.18f, 10.89f)
                curveToRelative(-0.7f, -0.42f, 2.58f, -11.45f, 3.71f, -14.83f)
                reflectiveCurveToRelative(4.04f, -10.84f, 4.04f, -10.84f)
                lineTo(47.8f, 75.4f)
                reflectiveCurveToRelative(-8.17f, 3.38f, -12.95f, 4.93f)
                curveToRelative(-4.79f, 1.55f, -11.26f, 3.94f, -11.26f, 2.82f)
                curveToRelative(0.0f, -1.13f, 4.19f, -3.49f, 10.98f, -6.95f)
                curveToRelative(7.84f, -3.99f, 11.4f, -5.58f, 11.4f, -5.58f)
                lineToRelative(-1.41f, -9.57f)
                reflectiveCurveToRelative(-6.29f, -2.16f, -10.79f, -3.85f)
                reflectiveCurveToRelative(-12.86f, -6.15f, -12.58f, -7.41f)
                curveToRelative(0.28f, -1.27f, 7.6f, 1.41f, 12.25f, 2.82f)
                reflectiveCurveToRelative(13.66f, 4.08f, 13.66f, 4.08f)
                lineToRelative(6.05f, -8.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFED6C31)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(47.8f, 52.73f)
                reflectiveCurveToRelative(-5.21f, -5.07f, -6.62f, -6.48f)
                curveToRelative(-1.41f, -1.41f, -4.36f, -4.36f, -3.66f, -5.35f)
                reflectiveCurveToRelative(6.48f, 3.8f, 7.88f, 5.07f)
                curveToRelative(1.41f, 1.27f, 6.48f, 4.93f, 6.48f, 4.93f)
                lineToRelative(10.7f, -5.35f)
                reflectiveCurveToRelative(0.14f, -4.79f, 0.28f, -7.18f)
                curveToRelative(0.14f, -2.39f, 0.0f, -6.76f, 1.13f, -6.76f)
                reflectiveCurveToRelative(1.55f, 6.05f, 1.69f, 8.17f)
                curveToRelative(0.14f, 2.11f, 0.56f, 6.19f, 0.56f, 6.19f)
                lineToRelative(10.84f, 3.52f)
                reflectiveCurveToRelative(3.52f, -4.08f, 4.65f, -5.35f)
                curveToRelative(1.13f, -1.27f, 3.66f, -5.07f, 4.79f, -4.22f)
                curveToRelative(1.13f, 0.84f, -3.24f, 6.76f, -4.08f, 8.02f)
                curveToRelative(-0.84f, 1.27f, -3.66f, 4.93f, -3.66f, 4.93f)
                lineToRelative(4.65f, 9.57f)
                reflectiveCurveToRelative(5.21f, -0.14f, 8.17f, -0.28f)
                reflectiveCurveToRelative(8.02f, 0.0f, 8.02f, 0.84f)
                reflectiveCurveToRelative(-4.32f, 2.02f, -8.26f, 2.44f)
                reflectiveCurveToRelative(-8.07f, 0.52f, -8.07f, 0.52f)
                lineToRelative(-1.64f, 9.57f)
                reflectiveCurveToRelative(1.92f, 2.53f, 4.69f, 5.02f)
                curveToRelative(2.47f, 2.22f, 4.46f, 4.88f, 3.89f, 5.73f)
                curveToRelative(-0.56f, 0.84f, -4.55f, -2.02f, -6.38f, -3.71f)
                reflectiveCurveToRelative(-5.21f, -4.65f, -5.21f, -4.65f)
                lineTo(67.8f, 84.11f)
                verticalLineToRelative(6.48f)
                curveToRelative(0.0f, 1.41f, -0.56f, 7.18f, -1.97f, 6.76f)
                reflectiveCurveToRelative(-1.27f, -5.77f, -1.27f, -7.46f)
                curveToRelative(0.0f, -2.92f, -0.01f, -6.61f, 0.05f, -6.15f)
                lineToRelative(-12.29f, -3.0f)
                reflectiveCurveToRelative(-3.05f, 2.67f, -4.46f, 4.22f)
                curveToRelative(-1.41f, 1.55f, -5.26f, 5.35f, -6.24f, 4.22f)
                reflectiveCurveToRelative(2.25f, -4.5f, 3.66f, -6.05f)
                curveToRelative(1.41f, -1.55f, 4.79f, -4.93f, 4.79f, -4.93f)
                lineToRelative(-5.49f, -9.15f)
                reflectiveCurveToRelative(-5.35f, 0.14f, -7.04f, 0.14f)
                reflectiveCurveToRelative(-7.74f, 0.56f, -7.74f, -1.13f)
                reflectiveCurveToRelative(5.35f, -1.69f, 7.88f, -1.83f)
                curveToRelative(2.53f, -0.14f, 8.63f, -0.52f, 8.63f, -0.52f)
                lineToRelative(1.49f, -12.98f)
                close()
            }
            path(fill = SolidColor(Color(0xFFED6C31)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(44.99f, 54.0f)
                curveToRelative(-2.92f, 5.29f, -5.02f, 23.37f, 9.06f, 30.27f)
                curveToRelative(14.08f, 6.9f, 27.26f, -1.55f, 31.07f, -12.67f)
                reflectiveCurveToRelative(-2.67f, -22.52f, -13.37f, -26.89f)
                reflectiveCurveTo(49.49f, 45.83f, 44.99f, 54.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF2A05B)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(58.08f, 49.21f)
                curveTo(51.1f, 51.37f, 44.8f, 61.27f, 49.96f, 72.2f)
                curveToRelative(4.45f, 9.43f, 15.2f, 11.45f, 23.46f, 6.01f)
                curveToRelative(8.37f, -5.52f, 9.53f, -15.3f, 4.6f, -22.62f)
                curveToRelative(-4.92f, -7.32f, -12.2f, -8.77f, -19.94f, -6.38f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCCD87)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(54.83f, 52.6f)
                curveToRelative(-1.89f, 2.01f, -2.36f, 4.73f, -1.04f, 6.23f)
                reflectiveCurveToRelative(4.9f, 0.8f, 6.5f, -1.41f)
                reflectiveCurveToRelative(1.6f, -4.63f, 0.38f, -5.83f)
                curveToRelative(-1.57f, -1.55f, -4.33f, -0.59f, -5.84f, 1.01f)
                close()
                moveTo(62.91f, 50.99f)
                curveToRelative(-0.51f, 1.27f, -0.94f, 4.41f, 1.6f, 5.26f)
                curveToRelative(2.4f, 0.8f, 3.43f, -0.66f, 4.22f, -2.11f)
                curveToRelative(0.84f, -1.53f, 0.66f, -4.08f, -1.13f, -4.83f)
                curveToRelative(-2.32f, -0.99f, -4.12f, 0.28f, -4.69f, 1.68f)
                close()
                moveTo(69.34f, 54.05f)
                curveToRelative(-0.67f, 0.99f, -1.31f, 4.08f, 1.78f, 4.69f)
                curveToRelative(3.25f, 0.64f, 4.13f, -3.47f, 2.67f, -4.97f)
                curveToRelative(-1.18f, -1.23f, -3.37f, -1.32f, -4.45f, 0.28f)
                close()
                moveTo(74.74f, 57.99f)
                curveToRelative(-0.62f, 0.55f, -1.31f, 2.39f, 0.19f, 3.47f)
                reflectiveCurveToRelative(2.77f, 0.23f, 3.24f, -0.7f)
                curveToRelative(0.27f, -0.55f, 0.47f, -1.69f, -0.38f, -2.53f)
                curveToRelative(-0.84f, -0.85f, -2.21f, -0.99f, -3.05f, -0.24f)
                close()
                moveTo(60.18f, 58.61f)
                curveToRelative(-0.86f, 1.97f, -0.43f, 4.44f, 1.67f, 5.08f)
                curveToRelative(2.43f, 0.74f, 4.4f, -0.69f, 4.45f, -4.0f)
                curveToRelative(0.02f, -1.25f, -0.76f, -2.57f, -2.63f, -2.91f)
                curveToRelative(-1.88f, -0.34f, -3.05f, 0.81f, -3.49f, 1.83f)
                close()
                moveTo(57.94f, 62.83f)
                curveToRelative(-1.61f, -0.14f, -2.86f, 1.59f, -2.86f, 3.14f)
                curveToRelative(0.0f, 1.54f, 1.2f, 3.17f, 2.65f, 3.14f)
                curveToRelative(2.08f, -0.05f, 3.12f, -1.29f, 3.07f, -3.34f)
                curveToRelative(-0.05f, -1.85f, -1.09f, -2.79f, -2.86f, -2.94f)
                close()
                moveTo(52.26f, 59.16f)
                curveToRelative(-1.69f, -0.05f, -3.38f, 1.36f, -3.33f, 4.18f)
                curveToRelative(0.05f, 2.82f, 1.27f, 4.18f, 2.82f, 4.08f)
                curveToRelative(1.55f, -0.09f, 3.28f, -1.31f, 3.47f, -4.32f)
                curveToRelative(0.19f, -3.0f, -1.41f, -3.89f, -2.96f, -3.94f)
                close()
                moveTo(51.26f, 68.72f)
                curveToRelative(-1.75f, 1.42f, -0.59f, 3.9f, 0.39f, 5.24f)
                curveToRelative(0.98f, 1.33f, 3.0f, 2.62f, 4.58f, 0.74f)
                curveToRelative(1.57f, -1.88f, 0.39f, -4.2f, -0.44f, -5.24f)
                curveToRelative(-0.84f, -1.04f, -2.47f, -2.42f, -4.53f, -0.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCCD87)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(56.44f, 76.66f)
                curveToRelative(0.35f, 1.71f, 1.5f, 2.39f, 3.1f, 2.35f)
                reflectiveCurveToRelative(2.49f, -1.74f, 2.44f, -2.91f)
                curveToRelative(-0.05f, -1.17f, -1.03f, -2.55f, -2.72f, -2.67f)
                curveToRelative(-1.98f, -0.14f, -3.2f, 1.4f, -2.82f, 3.23f)
                close()
            }
        }
        .build()
        return _blossom!!
    }

private var _blossom: ImageVector? = null
