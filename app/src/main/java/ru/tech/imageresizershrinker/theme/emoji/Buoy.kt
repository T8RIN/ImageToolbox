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

val Emoji.Buoy: ImageVector
    get() {
        if (_buoy != null) {
            return _buoy!!
        }
        _buoy = Builder(
            name = "Buoy", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFDD2C00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.03f, 9.51f)
                curveToRelative(-33.19f, 0.0f, -58.0f, 27.24f, -58.0f, 58.71f)
                reflectiveCurveToRelative(24.99f, 55.26f, 58.0f, 55.26f)
                curveToRelative(34.01f, 0.0f, 58.09f, -23.64f, 58.09f, -55.11f)
                reflectiveCurveTo(104.26f, 9.51f, 64.03f, 9.51f)
                close()
                moveTo(63.87f, 90.86f)
                curveToRelative(-14.79f, 0.0f, -26.77f, -11.99f, -26.77f, -26.77f)
                reflectiveCurveToRelative(11.99f, -26.77f, 26.77f, -26.77f)
                reflectiveCurveToRelative(26.77f, 11.99f, 26.77f, 26.77f)
                reflectiveCurveToRelative(-11.99f, 26.77f, -26.77f, 26.77f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF3D00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.08f, 4.92f)
                curveTo(30.85f, 4.92f, 6.0f, 30.72f, 7.04f, 61.97f)
                curveToRelative(1.05f, 31.49f, 21.79f, 57.05f, 57.05f, 57.05f)
                curveToRelative(36.52f, 0.0f, 57.05f, -25.54f, 57.05f, -57.05f)
                reflectiveCurveTo(99.84f, 4.92f, 64.08f, 4.92f)
                close()
                moveTo(63.87f, 92.43f)
                curveToRelative(-16.74f, 0.0f, -30.31f, -13.57f, -30.31f, -30.31f)
                reflectiveCurveToRelative(13.57f, -30.31f, 30.31f, -30.31f)
                reflectiveCurveToRelative(30.31f, 13.57f, 30.31f, 30.31f)
                reflectiveCurveToRelative(-13.57f, 30.31f, -30.31f, 30.31f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF9E80)), stroke = null, fillAlpha = 0.88f, strokeAlpha
                = 0.88f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(19.13f, 39.97f)
                curveToRelative(-1.34f, -0.5f, -1.26f, -2.81f, -0.67f, -4.34f)
                curveToRelative(4.45f, -11.55f, 12.19f, -18.15f, 24.61f, -22.56f)
                curveToRelative(2.54f, -0.9f, 3.84f, -0.45f, 4.31f, 1.18f)
                curveToRelative(0.45f, 1.58f, -0.68f, 2.65f, -2.2f, 3.3f)
                curveToRelative(-9.46f, 4.06f, -17.58f, 11.64f, -22.3f, 20.79f)
                curveToRelative(-0.52f, 1.01f, -2.07f, 2.25f, -3.75f, 1.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.35f, 6.1f)
                curveToRelative(-0.2f, -0.69f, -0.47f, -1.12f, -1.16f, -1.3f)
                curveToRelative(-1.34f, -0.34f, -4.09f, -0.8f, -9.12f, -0.8f)
                curveToRelative(-5.03f, 0.0f, -7.78f, 0.46f, -9.12f, 0.8f)
                curveToRelative(-0.69f, 0.18f, -0.88f, 0.49f, -1.08f, 1.18f)
                curveToRelative(-0.54f, 1.84f, -1.55f, 4.91f, -1.34f, 12.93f)
                curveToRelative(0.2f, 7.74f, 2.71f, 15.7f, 2.71f, 15.7f)
                curveToRelative(0.17f, 0.36f, 4.34f, 0.51f, 8.83f, 0.51f)
                curveToRelative(4.49f, 0.01f, 8.66f, -0.15f, 8.83f, -0.51f)
                curveToRelative(0.0f, 0.0f, 2.21f, -7.78f, 2.21f, -15.63f)
                curveToRelative(0.0f, -6.92f, -0.22f, -11.04f, -0.76f, -12.88f)
                close()
                moveTo(73.17f, 91.19f)
                curveToRelative(-0.33f, -0.69f, -0.86f, -1.32f, -1.73f, -1.59f)
                curveToRelative(-0.39f, -0.12f, -0.8f, -0.11f, -1.19f, 0.01f)
                curveToRelative(-1.02f, 0.34f, -2.47f, 0.77f, -6.18f, 0.77f)
                curveToRelative(-3.71f, 0.0f, -5.15f, -0.43f, -6.18f, -0.77f)
                curveToRelative(-0.39f, -0.13f, -0.8f, -0.13f, -1.19f, -0.01f)
                curveToRelative(-0.87f, 0.26f, -1.4f, 0.9f, -1.73f, 1.59f)
                curveToRelative(0.0f, 0.0f, -2.02f, 2.93f, -2.02f, 14.49f)
                curveToRelative(0.0f, 5.8f, 0.44f, 10.33f, 0.75f, 12.74f)
                curveToRelative(0.16f, 1.25f, 0.73f, 2.25f, 0.73f, 2.25f)
                reflectiveCurveToRelative(4.9f, 0.76f, 9.65f, 0.7f)
                curveToRelative(4.75f, 0.06f, 9.37f, -0.87f, 9.37f, -0.87f)
                reflectiveCurveToRelative(0.7f, -0.79f, 0.87f, -1.96f)
                curveToRelative(0.35f, -2.38f, 0.88f, -6.96f, 0.88f, -12.86f)
                curveToRelative(-0.01f, -11.57f, -2.03f, -14.49f, -2.03f, -14.49f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.15f, 119.63f)
                curveToRelative(0.15f, -0.69f, -0.34f, -0.7f, -1.06f, -0.6f)
                curveToRelative(-1.55f, 0.21f, -4.62f, 0.79f, -9.28f, 0.79f)
                curveToRelative(-4.53f, 0.0f, -7.24f, -0.56f, -8.76f, -0.77f)
                curveToRelative(-0.75f, -0.1f, -1.25f, 0.12f, -1.04f, 0.83f)
                curveToRelative(0.35f, 1.19f, 1.02f, 3.17f, 2.04f, 3.51f)
                curveToRelative(0.0f, 0.0f, 3.44f, 0.62f, 7.95f, 0.62f)
                curveToRelative(6.0f, 0.0f, 8.33f, -0.63f, 8.33f, -0.63f)
                curveToRelative(0.79f, -0.26f, 1.54f, -2.41f, 1.82f, -3.75f)
                close()
                moveTo(55.08f, 34.79f)
                curveToRelative(0.41f, 1.44f, 1.8f, 3.29f, 1.8f, 3.29f)
                curveToRelative(0.38f, 0.48f, 0.77f, 0.51f, 1.56f, 0.35f)
                curveToRelative(1.26f, -0.25f, 3.13f, -0.74f, 5.65f, -0.74f)
                curveToRelative(2.53f, 0.0f, 4.32f, 0.47f, 5.62f, 0.73f)
                curveToRelative(0.8f, 0.16f, 1.1f, 0.03f, 1.44f, -0.3f)
                curveToRelative(0.0f, 0.0f, 1.52f, -2.32f, 1.88f, -3.28f)
                curveToRelative(0.18f, -0.49f, 0.1f, -0.89f, -1.15f, -1.36f)
                curveToRelative(-1.44f, -0.54f, -3.69f, -1.2f, -7.85f, -1.2f)
                curveToRelative(-4.12f, 0.0f, -6.29f, 0.62f, -7.71f, 1.09f)
                curveToRelative(-1.03f, 0.33f, -1.43f, 0.75f, -1.24f, 1.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.38f, 53.11f)
                curveToRelative(-0.7f, 0.16f, -1.24f, 0.63f, -1.43f, 1.25f)
                curveToRelative(-0.37f, 1.19f, -0.66f, 2.52f, -0.8f, 7.02f)
                curveToRelative(-0.13f, 4.51f, 0.28f, 8.42f, 0.58f, 9.63f)
                curveToRelative(0.08f, 0.31f, 0.1f, 1.04f, 0.61f, 0.86f)
                curveToRelative(0.83f, -0.29f, 2.42f, -1.35f, 13.16f, -1.89f)
                curveToRelative(5.97f, -0.3f, 12.63f, 0.35f, 14.55f, 0.47f)
                curveToRelative(1.16f, 0.07f, 2.32f, -0.3f, 2.32f, -0.3f)
                curveToRelative(0.36f, -0.14f, -0.9f, -3.85f, -0.79f, -7.87f)
                curveToRelative(0.13f, -4.02f, 0.81f, -7.34f, 0.81f, -7.34f)
                reflectiveCurveToRelative(-0.63f, -0.77f, -2.2f, -1.35f)
                curveToRelative(-1.24f, -0.46f, -6.21f, -1.99f, -12.97f, -2.15f)
                curveToRelative(-10.49f, -0.24f, -13.84f, 1.67f, -13.84f, 1.67f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.18f, 54.5f)
                curveToRelative(0.16f, -0.47f, 0.81f, -0.63f, 1.23f, -0.32f)
                curveToRelative(0.0f, 0.0f, 1.35f, 0.88f, 1.82f, 1.51f)
                curveToRelative(0.47f, 0.64f, 0.58f, 0.76f, 0.45f, 1.21f)
                curveToRelative(-0.25f, 0.88f, -1.0f, 3.33f, -1.0f, 6.36f)
                curveToRelative(0.0f, 2.93f, 0.59f, 5.11f, 0.67f, 6.29f)
                curveToRelative(0.03f, 0.44f, -0.23f, 0.59f, -1.34f, 0.75f)
                curveToRelative(-0.39f, 0.06f, -1.57f, 0.39f, -1.92f, -0.77f)
                curveToRelative(-0.37f, -1.26f, -1.0f, -3.9f, -1.01f, -7.31f)
                curveToRelative(-0.01f, -3.73f, 0.66f, -6.44f, 1.1f, -7.72f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(119.62f, 52.69f)
                curveToRelative(0.7f, 0.17f, 1.24f, 0.66f, 1.43f, 1.31f)
                curveToRelative(0.37f, 1.25f, 0.66f, 2.64f, 0.8f, 7.36f)
                curveToRelative(0.13f, 4.72f, -0.28f, 8.82f, -0.58f, 10.09f)
                curveToRelative(-0.08f, 0.33f, -0.28f, 0.73f, -0.82f, 0.71f)
                reflectiveCurveToRelative(-3.47f, -1.8f, -14.21f, -2.23f)
                curveToRelative(-10.01f, -0.41f, -14.87f, 1.18f, -14.87f, 1.18f)
                curveToRelative(-0.36f, -0.15f, 0.16f, -4.59f, 0.05f, -8.81f)
                curveToRelative(-0.13f, -4.21f, -0.81f, -7.69f, -0.81f, -7.69f)
                reflectiveCurveToRelative(0.63f, -0.8f, 2.2f, -1.41f)
                curveToRelative(1.24f, -0.48f, 6.21f, -2.09f, 12.97f, -2.25f)
                curveToRelative(10.49f, -0.26f, 13.84f, 1.74f, 13.84f, 1.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.82f, 54.15f)
                curveToRelative(-0.16f, -0.49f, -0.81f, -0.66f, -1.23f, -0.33f)
                curveToRelative(0.0f, 0.0f, -1.35f, 0.92f, -1.82f, 1.59f)
                reflectiveCurveToRelative(-0.58f, 0.8f, -0.45f, 1.27f)
                curveToRelative(0.25f, 0.92f, 1.0f, 3.49f, 1.0f, 6.67f)
                curveToRelative(0.0f, 3.07f, -0.79f, 6.43f, -0.86f, 7.67f)
                curveToRelative(-0.03f, 0.46f, 0.8f, 0.11f, 1.92f, 0.1f)
                curveToRelative(0.55f, 0.0f, 1.19f, 0.01f, 1.53f, -1.2f)
                curveToRelative(0.37f, -1.32f, 1.0f, -4.09f, 1.01f, -7.66f)
                curveToRelative(0.02f, -3.93f, -0.65f, -6.77f, -1.1f, -8.11f)
                close()
                moveTo(71.5f, 89.62f)
                curveToRelative(-0.36f, -0.12f, -0.96f, -0.1f, -1.32f, 0.02f)
                curveToRelative(-1.02f, 0.34f, -2.48f, 0.75f, -6.11f, 0.75f)
                curveToRelative(-3.61f, 0.0f, -5.08f, -0.41f, -6.1f, -0.75f)
                curveToRelative(-0.38f, -0.13f, -0.79f, -0.17f, -1.18f, -0.07f)
                curveToRelative(-0.65f, 0.18f, -1.31f, 0.59f, -1.79f, 1.39f)
                arcToRelative(0.946f, 0.946f, 0.0f, false, false, 0.57f, 1.38f)
                curveToRelative(1.77f, 0.48f, 4.51f, 0.94f, 8.45f, 0.87f)
                curveToRelative(3.74f, -0.07f, 6.56f, -0.49f, 8.5f, -0.91f)
                curveToRelative(0.61f, -0.13f, 0.93f, -0.85f, 0.6f, -1.39f)
                curveToRelative(-0.42f, -0.69f, -1.03f, -1.1f, -1.62f, -1.29f)
                close()
                moveTo(6.12f, 72.17f)
                curveToRelative(-1.39f, -5.94f, -1.01f, -11.82f, 0.24f, -16.98f)
                curveToRelative(0.26f, -1.07f, 0.83f, -1.76f, 1.29f, -1.76f)
                curveToRelative(0.0f, 0.0f, -0.02f, 0.25f, -0.14f, 0.75f)
                curveToRelative(-1.25f, 5.33f, -0.86f, 11.5f, 0.12f, 16.94f)
                curveToRelative(0.09f, 0.51f, -0.12f, 0.68f, -0.45f, 0.86f)
                curveToRelative(-0.17f, 0.1f, -0.35f, 0.21f, -0.49f, 0.3f)
                curveToRelative(-0.49f, 0.34f, -0.57f, -0.11f, -0.57f, -0.11f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(121.88f, 72.66f)
                curveToRelative(1.39f, -6.23f, 1.01f, -12.38f, -0.24f, -17.79f)
                curveToRelative(-0.26f, -1.12f, -0.83f, -1.84f, -1.29f, -1.84f)
                curveToRelative(0.0f, 0.0f, 0.02f, 0.26f, 0.14f, 0.78f)
                curveToRelative(1.25f, 5.59f, 0.86f, 12.05f, -0.12f, 17.75f)
                curveToRelative(-0.09f, 0.54f, 0.12f, 0.71f, 0.45f, 0.9f)
                curveToRelative(0.17f, 0.1f, 0.35f, 0.22f, 0.49f, 0.31f)
                curveToRelative(0.49f, 0.36f, 0.57f, -0.11f, 0.57f, -0.11f)
                close()
            }
        }
            .build()
        return _buoy!!
    }

private var _buoy: ImageVector? = null
