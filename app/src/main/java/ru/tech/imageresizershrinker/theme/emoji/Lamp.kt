package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

val Emoji.Lamp: ImageVector
    get() {
        if (lamp != null) {
            return lamp!!
        }
        lamp = Builder(
            name = "Light-bulb", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF424242)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(51.91f, 116.87f)
                arcToRelative(12.09f, 7.13f, 0.0f, true, false, 24.18f, 0.0f)
                arcToRelative(12.09f, 7.13f, 0.0f, true, false, -24.18f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 4.0f)
                curveTo(42.92f, 4.0f, 25.82f, 19.67f, 25.82f, 38.99f)
                curveToRelative(0.0f, 5.04f, 1.52f, 10.43f, 3.75f, 15.18f)
                curveToRelative(3.13f, 6.68f, 6.54f, 11.62f, 7.54f, 13.44f)
                curveToRelative(2.78f, 5.06f, 2.38f, 10.39f, 3.15f, 13.73f)
                curveToRelative(1.45f, 6.24f, 5.79f, 8.5f, 23.73f, 8.5f)
                reflectiveCurveToRelative(21.8f, -2.15f, 23.41f, -7.9f)
                curveToRelative(1.1f, -3.91f, 0.03f, -8.18f, 2.8f, -13.23f)
                curveToRelative(1.0f, -1.82f, 5.07f, -7.85f, 8.21f, -14.54f)
                curveToRelative(2.23f, -4.75f, 3.75f, -10.14f, 3.75f, -15.18f)
                curveTo(102.18f, 19.67f, 85.08f, 4.0f, 64.0f, 4.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB26500)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(42.06f, 86.13f)
                arcToRelative(21.94f, 4.46f, 0.0f, true, false, 43.88f, 0.0f)
                arcToRelative(21.94f, 4.46f, 0.0f, true, false, -43.88f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB26500)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(42.06f, 86.13f)
                arcToRelative(21.94f, 4.46f, 0.0f, true, false, 43.88f, 0.0f)
                arcToRelative(21.94f, 4.46f, 0.0f, true, false, -43.88f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFA000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.01f, 86.13f)
                arcToRelative(15.99f, 2.06f, 0.0f, true, false, 31.98f, 0.0f)
                arcToRelative(15.99f, 2.06f, 0.0f, true, false, -31.98f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFB26500)),
                strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(53.3f, 56.77f)
                curveToRelative(-0.62f, 1.56f, -2.23f, 4.77f, -1.39f, 6.21f)
                curveToRelative(1.95f, 3.35f, 6.6f, 4.55f, 6.6f, 7.63f)
                curveToRelative(0.0f, 4.7f, -3.42f, 19.93f, -3.42f, 19.93f)
                moveToRelative(18.94f, -34.33f)
                reflectiveCurveToRelative(2.24f, 4.8f, 1.29f, 6.95f)
                curveToRelative(-0.71f, 1.6f, -4.98f, 4.18f, -5.53f, 4.61f)
                curveToRelative(-2.55f, 2.0f, 0.84f, 22.78f, 0.84f, 22.78f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFFFFFF)),
                strokeLineWidth = 2.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(53.3f, 56.77f)
                curveToRelative(3.44f, -6.8f, 5.21f, -22.32f, 0.84f, -21.53f)
                curveToRelative(-7.37f, 1.33f, 1.71f, 26.83f, 6.18f, 23.9f)
                reflectiveCurveToRelative(10.01f, -23.85f, 3.21f, -23.93f)
                curveToRelative(-6.8f, -0.08f, 0.46f, 26.66f, 5.08f, 23.69f)
                curveToRelative(3.65f, -2.35f, 12.56f, -23.66f, 5.24f, -23.66f)
                curveToRelative(-6.23f, 0.0f, 0.19f, 20.97f, 0.19f, 20.97f)
            }
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(85.89f, 87.06f)
                reflectiveCurveTo(80.13f, 89.84f, 64.0f, 89.84f)
                reflectiveCurveToRelative(-21.89f, -2.78f, -21.89f, -2.78f)
                reflectiveCurveToRelative(-0.36f, 5.14f, 0.83f, 7.47f)
                curveToRelative(1.43f, 2.8f, 2.53f, 3.77f, 2.53f, 3.77f)
                lineToRelative(0.6f, 2.85f)
                lineToRelative(-0.24f, 0.75f)
                curveToRelative(-0.31f, 0.98f, -0.09f, 2.06f, 0.6f, 2.83f)
                lineToRelative(0.52f, 0.58f)
                lineToRelative(0.58f, 2.74f)
                lineToRelative(-0.2f, 0.55f)
                curveToRelative(-0.38f, 1.05f, -0.12f, 2.22f, 0.66f, 3.02f)
                lineToRelative(0.38f, 0.39f)
                lineToRelative(0.47f, 2.24f)
                reflectiveCurveToRelative(2.38f, 5.08f, 15.16f, 5.08f)
                reflectiveCurveToRelative(15.16f, -5.08f, 15.16f, -5.08f)
                lineToRelative(0.04f, -0.19f)
                lineToRelative(0.26f, -0.26f)
                curveToRelative(0.52f, -0.51f, 0.69f, -1.27f, 0.44f, -1.95f)
                lineToRelative(-0.15f, -0.39f)
                lineToRelative(0.62f, -2.96f)
                lineToRelative(1.09f, -1.15f)
                curveToRelative(0.54f, -0.57f, 0.66f, -1.41f, 0.31f, -2.11f)
                lineToRelative(-0.5f, -0.99f)
                lineToRelative(0.63f, -2.97f)
                lineToRelative(0.4f, -0.31f)
                curveToRelative(0.59f, -0.65f, 0.6f, -1.63f, 0.34f, -2.3f)
                curveToRelative(-0.2f, -0.53f, -0.04f, -1.13f, 0.37f, -1.52f)
                curveToRelative(0.63f, -0.6f, 1.44f, -1.51f, 2.04f, -2.64f)
                curveToRelative(1.23f, -2.29f, 0.84f, -7.45f, 0.84f, -7.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F7889)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(45.47f, 98.3f)
                lineToRelative(0.54f, 2.87f)
                curveToRelative(5.82f, -0.03f, 13.59f, 0.26f, 28.5f, -2.11f)
                curveToRelative(2.69f, -0.61f, 5.92f, -1.82f, 2.35f, -1.32f)
                curveToRelative(0.0f, -0.01f, -13.69f, 1.3f, -31.39f, 0.56f)
                close()
                moveTo(47.47f, 108.07f)
                curveToRelative(6.44f, -0.11f, 19.6f, -0.75f, 33.74f, -3.82f)
                lineToRelative(0.63f, -2.97f)
                curveToRelative(-14.79f, 3.36f, -28.7f, 3.96f, -34.95f, 4.04f)
                lineToRelative(0.58f, 2.75f)
                close()
                moveTo(80.31f, 108.49f)
                curveToRelative(-13.09f, 2.84f, -25.34f, 3.57f, -31.97f, 3.73f)
                lineToRelative(0.43f, 2.04f)
                reflectiveCurveToRelative(0.21f, 6.33f, 15.16f, 6.33f)
                reflectiveCurveToRelative(15.16f, -6.33f, 15.16f, -6.33f)
                reflectiveCurveToRelative(-6.38f, 1.82f, -14.23f, 0.93f)
                arcToRelative(0.63f, 0.63f, 0.0f, false, true, -0.01f, -1.26f)
                curveToRelative(4.69f, -0.62f, 10.29f, -1.54f, 14.84f, -2.48f)
                lineToRelative(0.62f, -2.96f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF82AEC0)),
                strokeLineWidth = 3.997f, strokeLineCap = Round, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(42.18f, 87.06f)
                reflectiveCurveToRelative(6.46f, 2.78f, 21.76f, 2.78f)
                reflectiveCurveToRelative(21.88f, -2.78f, 21.88f, -2.78f)
            }
            path(
                fill = SolidColor(Color(0xFFFFFF8D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(49.88f, 10.32f)
                curveToRelative(3.91f, -0.96f, 8.0f, -0.48f, 10.8f, 2.92f)
                curveToRelative(0.79f, 0.96f, 1.4f, 2.1f, 1.54f, 3.34f)
                curveToRelative(0.28f, 2.39f, -1.2f, 4.65f, -2.96f, 6.31f)
                curveToRelative(-5.02f, 4.74f, -12.15f, 7.04f, -15.39f, 13.58f)
                curveToRelative(-0.76f, 1.53f, -1.36f, 3.18f, -2.52f, 4.43f)
                curveToRelative(-1.16f, 1.25f, -3.09f, 2.01f, -4.6f, 1.21f)
                curveToRelative(-0.8f, -0.42f, -1.35f, -1.21f, -1.8f, -2.0f)
                curveToRelative(-2.84f, -5.06f, -2.63f, -11.51f, -0.13f, -16.75f)
                curveToRelative(2.75f, -5.74f, 8.78f, -11.5f, 15.06f, -13.04f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(46.45f, 91.93f)
                curveToRelative(-0.88f, -0.4f, -0.53f, -1.72f, 0.43f, -1.65f)
                curveToRelative(3.22f, 0.25f, 8.7f, 0.56f, 15.95f, 0.56f)
                curveToRelative(7.64f, 0.0f, 14.36f, -0.57f, 18.28f, -0.99f)
                curveToRelative(0.97f, -0.1f, 1.34f, 1.23f, 0.45f, 1.64f)
                curveToRelative(-3.02f, 1.42f, -8.55f, 3.04f, -18.03f, 3.04f)
                curveToRelative(-9.25f, 0.0f, -14.35f, -1.37f, -17.08f, -2.6f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF94D1E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(51.94f, 102.03f)
                curveToRelative(-0.67f, 0.24f, -1.36f, 0.57f, -1.7f, 1.19f)
                curveToRelative(-0.12f, 0.23f, -0.19f, 0.49f, -0.14f, 0.75f)
                curveToRelative(0.08f, 0.38f, 0.43f, 0.65f, 0.78f, 0.82f)
                curveToRelative(0.7f, 0.34f, 1.49f, 0.43f, 2.26f, 0.44f)
                curveToRelative(1.59f, 0.02f, 3.17f, -0.28f, 4.74f, -0.58f)
                curveToRelative(0.47f, -0.09f, 0.95f, -0.18f, 1.37f, -0.41f)
                curveToRelative(0.42f, -0.23f, 0.78f, -0.62f, 0.85f, -1.09f)
                curveToRelative(0.1f, -0.63f, -0.35f, -1.24f, -0.9f, -1.54f)
                curveToRelative(-1.9f, -1.05f, -5.34f, -0.27f, -7.26f, 0.42f)
                close()
                moveTo(53.43f, 108.62f)
                curveToRelative(-0.67f, 0.24f, -1.36f, 0.57f, -1.7f, 1.19f)
                curveToRelative(-0.12f, 0.23f, -0.19f, 0.49f, -0.14f, 0.75f)
                curveToRelative(0.08f, 0.38f, 0.43f, 0.65f, 0.78f, 0.82f)
                curveToRelative(0.7f, 0.34f, 1.49f, 0.43f, 2.26f, 0.44f)
                curveToRelative(1.59f, 0.02f, 3.17f, -0.28f, 4.74f, -0.58f)
                curveToRelative(0.47f, -0.09f, 0.95f, -0.18f, 1.37f, -0.41f)
                curveToRelative(0.42f, -0.23f, 0.78f, -0.62f, 0.85f, -1.09f)
                curveToRelative(0.1f, -0.63f, -0.35f, -1.24f, -0.9f, -1.54f)
                curveToRelative(-1.9f, -1.04f, -5.35f, -0.26f, -7.26f, 0.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFF8D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(50.01f, 84.2f)
                curveToRelative(0.91f, 0.09f, 1.87f, 0.01f, 2.64f, -0.48f)
                reflectiveCurveToRelative(1.26f, -1.49f, 0.95f, -2.35f)
                curveToRelative(-0.16f, -0.45f, -0.51f, -0.81f, -0.85f, -1.15f)
                curveToRelative(-0.75f, -0.74f, -1.5f, -1.48f, -2.24f, -2.22f)
                curveToRelative(-0.83f, -0.83f, -1.66f, -1.65f, -2.56f, -2.4f)
                curveToRelative(-1.39f, -1.16f, -3.26f, -2.25f, -5.09f, -1.4f)
                curveToRelative(-1.56f, 0.72f, -1.93f, 2.14f, -1.24f, 3.63f)
                curveToRelative(1.47f, 3.13f, 4.89f, 6.01f, 8.39f, 6.37f)
                close()
            }
        }
            .build()
        return lamp!!
    }

private var lamp: ImageVector? = null
