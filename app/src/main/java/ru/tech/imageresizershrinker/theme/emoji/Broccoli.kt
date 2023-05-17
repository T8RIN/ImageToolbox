package ru.tech.imageresizershrinker.theme.emoji

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

val Emoji.Broccoli: ImageVector
    get() {
        if (_broccoli != null) {
            return _broccoli!!
        }
        _broccoli = Builder(
            name = "Broccoli", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF2F7C31)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.74f, 24.77f)
                lineTo(8.57f, 41.04f)
                reflectiveCurveToRelative(-3.95f, 4.95f, -2.93f, 9.84f)
                curveToRelative(1.15f, 5.46f, 4.05f, 5.79f, 4.05f, 5.79f)
                reflectiveCurveTo(9.5f, 60.91f, 12.0f, 64.52f)
                reflectiveCurveToRelative(3.69f, 5.32f, 12.77f, 8.07f)
                reflectiveCurveToRelative(25.46f, 5.84f, 25.46f, 5.84f)
                lineToRelative(4.22f, 16.38f)
                reflectiveCurveToRelative(1.13f, 4.05f, 6.16f, 6.16f)
                reflectiveCurveToRelative(8.96f, 1.56f, 8.96f, 1.56f)
                reflectiveCurveToRelative(2.08f, 5.75f, 8.99f, 7.35f)
                curveToRelative(5.98f, 1.39f, 10.75f, -1.62f, 10.75f, -1.62f)
                reflectiveCurveToRelative(5.03f, 3.08f, 11.51f, 1.62f)
                curveToRelative(7.52f, -1.69f, 9.24f, -6.32f, 9.24f, -6.32f)
                reflectiveCurveToRelative(6.16f, -0.65f, 10.21f, -6.49f)
                reflectiveCurveToRelative(1.13f, -12.48f, 1.13f, -12.48f)
                reflectiveCurveToRelative(3.59f, -5.32f, 2.59f, -11.35f)
                curveToRelative(-0.9f, -5.46f, -3.07f, -7.32f, -3.07f, -7.32f)
                lineToRelative(-12.98f, -9.06f)
                reflectiveCurveToRelative(5.51f, -2.27f, 7.46f, -12.0f)
                reflectiveCurveToRelative(-2.92f, -14.59f, -2.92f, -14.59f)
                reflectiveCurveToRelative(1.91f, -5.49f, -1.3f, -10.38f)
                curveToRelative(-3.08f, -4.7f, -7.46f, -2.43f, -9.57f, -4.54f)
                curveToRelative(-2.6f, -2.6f, -17.35f, -4.22f, -17.35f, -4.22f)
                lineTo(67.25f, 25.26f)
                lineToRelative(-45.51f, -0.49f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF709921)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.0f, 8.45f)
                reflectiveCurveToRelative(5.09f, -5.58f, 11.92f, -5.09f)
                reflectiveCurveToRelative(8.07f, 4.61f, 8.07f, 4.61f)
                reflectiveCurveToRelative(4.19f, -0.69f, 6.82f, 0.48f)
                curveToRelative(5.38f, 2.4f, 4.81f, 5.19f, 6.44f, 5.0f)
                reflectiveCurveToRelative(1.63f, -7.11f, 9.23f, -9.52f)
                reflectiveCurveToRelative(11.73f, 1.25f, 13.36f, 1.15f)
                reflectiveCurveToRelative(3.65f, -2.03f, 9.42f, -0.19f)
                curveToRelative(5.19f, 1.66f, 6.3f, 5.1f, 6.54f, 6.15f)
                curveToRelative(0.28f, 1.27f, 0.28f, 2.69f, 1.82f, 4.32f)
                reflectiveCurveToRelative(-0.11f, 7.54f, -2.32f, 9.94f)
                reflectiveCurveToRelative(-6.52f, 2.65f, -6.52f, 2.65f)
                reflectiveCurveToRelative(1.01f, 8.3f, -5.89f, 12.86f)
                curveToRelative(-4.85f, 3.2f, -10.07f, 1.94f, -10.07f, 1.94f)
                reflectiveCurveToRelative(-5.57f, 4.82f, -11.82f, 2.8f)
                curveToRelative(-6.25f, -2.02f, -6.92f, -4.61f, -3.46f, -7.5f)
                curveToRelative(3.46f, -2.88f, 5.0f, -8.46f, 0.87f, -9.52f)
                curveToRelative(-4.13f, -1.06f, -8.36f, 0.96f, -9.8f, 5.48f)
                curveToRelative(-1.44f, 4.52f, -1.54f, 8.46f, -6.25f, 11.82f)
                curveToRelative(-4.71f, 3.36f, -8.17f, 2.31f, -12.88f, 3.56f)
                curveToRelative(-4.71f, 1.25f, -7.88f, 5.29f, -11.82f, 5.38f)
                curveToRelative(-3.94f, 0.1f, -7.02f, -2.79f, -9.04f, -2.5f)
                curveToRelative(-2.02f, 0.29f, -6.15f, 1.25f, -6.15f, 1.25f)
                reflectiveCurveToRelative(-1.83f, -3.46f, -0.67f, -7.69f)
                reflectiveCurveToRelative(2.78f, -4.8f, 2.78f, -4.8f)
                reflectiveCurveToRelative(-9.37f, -9.06f, -0.76f, -22.88f)
                curveTo(15.6f, 5.66f, 28.0f, 8.45f, 28.0f, 8.45f)
                close()
                moveTo(79.71f, 73.23f)
                reflectiveCurveToRelative(3.65f, -1.54f, 6.06f, -5.67f)
                curveToRelative(2.4f, -4.13f, 3.65f, -7.21f, 7.5f, -9.23f)
                curveToRelative(3.84f, -2.02f, 8.17f, -1.83f, 13.75f, -1.54f)
                curveToRelative(5.58f, 0.29f, 6.99f, 1.99f, 8.36f, 3.36f)
                curveToRelative(2.69f, 2.69f, 5.58f, 5.77f, 5.58f, 5.77f)
                reflectiveCurveToRelative(-4.71f, 0.38f, -7.21f, 4.23f)
                reflectiveCurveToRelative(-4.04f, 9.13f, -4.04f, 9.13f)
                reflectiveCurveToRelative(-3.36f, 1.25f, -6.92f, 3.17f)
                reflectiveCurveToRelative(-11.44f, 7.02f, -16.63f, 5.67f)
                curveToRelative(-5.19f, -1.35f, -7.88f, -7.31f, -7.88f, -7.98f)
                curveToRelative(-0.01f, -0.66f, 1.43f, -6.91f, 1.43f, -6.91f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9FB525)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.34f, 81.83f)
                curveToRelative(-0.24f, 0.72f, -25.28f, 28.89f, -25.28f, 28.89f)
                reflectiveCurveToRelative(3.01f, 4.57f, 5.66f, 6.74f)
                reflectiveCurveToRelative(6.95f, 5.41f, 9.68f, 6.15f)
                curveToRelative(2.96f, 0.8f, 5.24f, 1.68f, 7.89f, -1.81f)
                curveToRelative(2.65f, -3.49f, 4.69f, -8.06f, 11.92f, -15.77f)
                reflectiveCurveToRelative(12.4f, -12.4f, 18.17f, -15.17f)
                reflectiveCurveToRelative(14.08f, -4.69f, 16.25f, -4.93f)
                curveToRelative(2.17f, -0.24f, 4.93f, -0.48f, 4.93f, -0.48f)
                reflectiveCurveToRelative(1.63f, -3.29f, 1.08f, -5.9f)
                curveToRelative(-0.61f, -2.91f, -4.69f, -4.21f, -4.69f, -4.21f)
                lineToRelative(-20.7f, 3.01f)
                lineToRelative(2.45f, -0.82f)
                reflectiveCurveToRelative(-0.97f, -1.43f, 0.32f, -3.28f)
                curveToRelative(1.12f, -1.61f, 6.26f, -7.83f, 7.95f, -10.12f)
                curveToRelative(1.69f, -2.29f, 5.12f, -6.73f, 5.05f, -7.45f)
                curveToRelative(-0.12f, -1.32f, -3.25f, -3.01f, -4.21f, -3.61f)
                curveToRelative(-0.96f, -0.6f, -3.01f, -1.44f, -3.01f, -1.44f)
                lineTo(46.65f, 74.6f)
                lineToRelative(-1.2f, -1.08f)
                reflectiveCurveToRelative(-1.44f, -0.07f, -2.29f, -2.29f)
                curveToRelative(-0.6f, -1.56f, -0.84f, -6.26f, -1.32f, -10.35f)
                curveToRelative(-0.3f, -2.56f, -0.48f, -5.54f, -0.48f, -5.54f)
                reflectiveCurveToRelative(-3.49f, -1.56f, -5.78f, -1.32f)
                reflectiveCurveToRelative(-4.93f, 2.77f, -4.93f, 2.77f)
                lineToRelative(4.69f, 25.04f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB7D118)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.91f, 57.51f)
                curveToRelative(-0.36f, 0.96f, 0.24f, 5.3f, 0.24f, 11.07f)
                reflectiveCurveToRelative(-0.33f, 11.12f, -3.13f, 14.56f)
                curveToRelative(-3.61f, 4.45f, -9.75f, 10.71f, -14.44f, 14.92f)
                curveToRelative(-2.03f, 1.82f, -3.37f, 2.53f, -3.49f, 3.01f)
                reflectiveCurveToRelative(0.87f, 4.86f, 1.81f, 6.74f)
                curveToRelative(1.7f, 3.4f, 4.82f, 6.49f, 4.82f, 6.49f)
                reflectiveCurveToRelative(12.51f, -14.07f, 17.81f, -19.49f)
                curveToRelative(5.3f, -5.42f, 8.18f, -9.03f, 9.39f, -7.82f)
                curveToRelative(1.2f, 1.2f, -2.79f, 5.89f, -5.66f, 9.15f)
                curveToRelative(-4.33f, 4.93f, -16.47f, 22.92f, -16.47f, 22.92f)
                reflectiveCurveToRelative(0.67f, 0.7f, 2.03f, 1.63f)
                curveToRelative(1.2f, 0.83f, 2.14f, 1.26f, 2.14f, 1.26f)
                reflectiveCurveToRelative(6.4f, -10.41f, 15.31f, -19.8f)
                reflectiveCurveToRelative(13.96f, -13.72f, 20.1f, -15.77f)
                reflectiveCurveToRelative(28.28f, -6.86f, 28.28f, -6.86f)
                reflectiveCurveToRelative(-0.96f, -3.37f, -1.56f, -4.81f)
                reflectiveCurveToRelative(-1.93f, -3.13f, -1.93f, -3.13f)
                reflectiveCurveToRelative(-11.07f, 2.89f, -13.0f, 3.49f)
                curveToRelative(-1.93f, 0.6f, -12.88f, 1.93f, -13.36f, 0.72f)
                curveToRelative(-0.48f, -1.2f, 7.34f, -10.95f, 9.63f, -13.96f)
                curveToRelative(2.29f, -3.01f, 6.43f, -8.79f, 6.43f, -8.79f)
                reflectiveCurveToRelative(-2.45f, -1.68f, -5.46f, -2.4f)
                reflectiveCurveToRelative(-4.09f, -0.12f, -4.09f, -0.12f)
                reflectiveCurveToRelative(-7.34f, 9.87f, -9.51f, 12.76f)
                curveToRelative(-2.17f, 2.89f, -6.38f, 9.15f, -10.23f, 10.23f)
                curveToRelative(-3.85f, 1.08f, -4.81f, 0.12f, -5.3f, -2.77f)
                curveToRelative(-0.48f, -2.89f, -1.69f, -16.73f, -1.69f, -16.73f)
                reflectiveCurveToRelative(-3.85f, 0.24f, -5.3f, 0.96f)
                reflectiveCurveToRelative(-3.01f, 1.58f, -3.37f, 2.54f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F7C31)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(102.96f, 75.13f)
                reflectiveCurveToRelative(2.24f, -4.05f, 4.17f, -2.36f)
                curveToRelative(1.94f, 1.69f, -1.75f, 7.02f, -2.54f, 7.2f)
                curveToRelative(-0.79f, 0.18f, -6.95f, -1.87f, -6.05f, -4.17f)
                curveToRelative(0.92f, -2.3f, 4.42f, -0.67f, 4.42f, -0.67f)
                close()
                moveTo(44.78f, 34.49f)
                curveToRelative(2.3f, -0.08f, 7.13f, -4.8f, 5.09f, -7.25f)
                curveToRelative(-1.65f, -1.98f, -5.09f, 1.86f, -5.09f, 1.86f)
                reflectiveCurveToRelative(-3.64f, -3.75f, -5.08f, -1.94f)
                curveToRelative(-2.05f, 2.62f, 3.39f, 7.39f, 5.08f, 7.33f)
                close()
                moveTo(31.96f, 39.21f)
                reflectiveCurveToRelative(2.0f, -4.11f, 4.29f, -2.36f)
                curveToRelative(2.3f, 1.75f, -1.69f, 7.68f, -2.9f, 8.04f)
                curveToRelative(-1.21f, 0.36f, -7.71f, -3.36f, -5.99f, -5.99f)
                curveToRelative(1.4f, -2.11f, 4.6f, 0.31f, 4.6f, 0.31f)
                close()
                moveTo(14.36f, 35.46f)
                curveToRelative(0.72f, 0.12f, 5.87f, -2.78f, 4.41f, -5.32f)
                reflectiveCurveToRelative(-4.41f, 0.67f, -4.41f, 0.67f)
                reflectiveCurveToRelative(-2.79f, -2.47f, -3.99f, -0.97f)
                curveToRelative(-1.93f, 2.42f, 2.91f, 5.44f, 3.99f, 5.62f)
                close()
                moveTo(15.27f, 43.81f)
                reflectiveCurveToRelative(1.94f, -3.99f, 3.99f, -2.48f)
                curveToRelative(2.06f, 1.51f, -1.51f, 7.56f, -2.06f, 7.8f)
                curveToRelative(-0.54f, 0.24f, -7.81f, -1.81f, -6.23f, -4.72f)
                curveToRelative(1.1f, -1.99f, 4.3f, -0.6f, 4.3f, -0.6f)
                close()
                moveTo(79.21f, 39.31f)
                curveToRelative(1.59f, -0.69f, 4.25f, -6.07f, 1.76f, -7.16f)
                curveToRelative(-2.01f, -0.88f, -3.06f, 2.4f, -3.06f, 2.4f)
                reflectiveCurveToRelative(-2.23f, -1.96f, -3.76f, -0.35f)
                curveToRelative(-2.35f, 2.47f, 4.1f, 5.53f, 5.06f, 5.11f)
                close()
                moveTo(90.51f, 18.38f)
                reflectiveCurveToRelative(0.13f, -2.34f, 1.93f, -2.22f)
                curveToRelative(1.98f, 0.14f, 1.72f, 5.02f, 0.21f, 5.94f)
                curveToRelative(-1.51f, 0.92f, -6.49f, -1.72f, -5.36f, -3.56f)
                curveToRelative(0.72f, -1.17f, 3.35f, -0.37f, 3.22f, -0.16f)
                close()
            }
        }
            .build()
        return _broccoli!!
    }

private var _broccoli: ImageVector? = null
