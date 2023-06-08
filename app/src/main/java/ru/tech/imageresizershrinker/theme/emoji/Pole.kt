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

val Emoji.Pole: ImageVector
    get() {
        if (_pole != null) {
            return _pole!!
        }
        _pole = Builder(
            name = "Pole", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(43.22f, 32.72f)
                lineToRelative(0.8f, 62.85f)
                lineToRelative(41.77f, -0.27f)
                lineToRelative(-0.8f, -64.87f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD80D1A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(42.68f, 37.74f)
                lineToRelative(10.14f, -5.86f)
                lineToRelative(23.44f, 0.57f)
                lineToRelative(-33.71f, 23.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1D86FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(43.22f, 67.23f)
                lineToRelative(42.3f, -28.87f)
                lineToRelative(-0.27f, 19.34f)
                lineToRelative(-42.17f, 29.41f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD80D1A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(49.93f, 95.3f)
                lineToRelative(35.46f, -25.11f)
                lineToRelative(-0.54f, 18.53f)
                lineToRelative(-13.3f, 9.54f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA1D2D6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(40.53f, 32.45f)
                lineToRelative(0.81f, 64.06f)
                lineToRelative(3.89f, -1.48f)
                lineToRelative(-0.8f, -64.06f)
                close()
                moveTo(83.24f, 31.37f)
                lineToRelative(0.4f, 63.93f)
                lineToRelative(3.87f, -0.44f)
                lineTo(87.0f, 31.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC8C8C8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(40.05f, 33.09f)
                curveToRelative(0.64f, 0.71f, 45.97f, 0.16f, 46.76f, -0.24f)
                curveToRelative(0.79f, -0.4f, 4.81f, -3.73f, 4.5f, -9.05f)
                curveToRelative(-0.31f, -5.25f, -3.94f, -6.91f, -3.94f, -6.91f)
                reflectiveCurveToRelative(-0.7f, -5.26f, -6.75f, -9.29f)
                curveToRelative(-4.29f, -2.86f, -9.85f, -3.69f, -16.83f, -3.63f)
                curveToRelative(-7.7f, 0.07f, -14.93f, 0.54f, -19.53f, 5.14f)
                curveToRelative(-4.42f, 4.42f, -4.6f, 8.73f, -4.6f, 8.73f)
                reflectiveCurveToRelative(-3.91f, 1.2f, -3.91f, 7.08f)
                curveToRelative(-0.01f, 5.8f, 4.3f, 8.17f, 4.3f, 8.17f)
                close()
                moveTo(40.76f, 94.46f)
                curveToRelative(-0.66f, 0.55f, -0.79f, 3.97f, -0.79f, 3.97f)
                reflectiveCurveToRelative(-3.71f, 2.0f, -3.18f, 6.35f)
                curveToRelative(0.48f, 3.89f, 3.73f, 5.4f, 3.73f, 5.4f)
                reflectiveCurveToRelative(1.52f, 6.58f, 6.59f, 10.16f)
                curveToRelative(5.4f, 3.81f, 10.72f, 3.97f, 18.34f, 3.97f)
                curveToRelative(8.26f, 0.0f, 13.89f, -0.64f, 18.34f, -4.84f)
                curveToRelative(5.1f, -4.83f, 5.48f, -9.84f, 5.48f, -9.84f)
                reflectiveCurveToRelative(2.94f, -1.75f, 2.86f, -5.72f)
                curveToRelative(-0.08f, -3.97f, -3.33f, -5.48f, -3.33f, -5.48f)
                reflectiveCurveToRelative(-0.68f, -3.17f, -1.27f, -3.97f)
                curveToRelative(-0.3f, -0.41f, -12.16f, -0.3f, -23.82f, -0.24f)
                curveToRelative(-11.25f, 0.05f, -22.44f, -0.19f, -22.95f, 0.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF858585)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(61.57f, 120.42f)
                curveToRelative(-0.08f, 1.59f, 8.33f, 1.55f, 14.53f, 0.16f)
                curveToRelative(7.07f, -1.59f, 9.37f, -7.62f, 8.97f, -8.1f)
                curveToRelative(-0.4f, -0.48f, -7.54f, -0.32f, -7.94f, 0.08f)
                curveToRelative(-0.4f, 0.4f, -2.22f, 4.13f, -5.32f, 5.4f)
                curveToRelative(-3.1f, 1.27f, -10.18f, 1.25f, -10.24f, 2.46f)
                close()
                moveTo(37.04f, 105.83f)
                lineToRelative(54.95f, -0.25f)
                reflectiveCurveToRelative(-0.18f, 1.16f, -0.93f, 2.29f)
                curveToRelative(-0.73f, 1.09f, -1.78f, 1.75f, -1.78f, 1.75f)
                lineToRelative(-48.72f, 0.57f)
                reflectiveCurveToRelative(-1.46f, -0.79f, -2.09f, -1.57f)
                curveToRelative(-1.39f, -1.72f, -1.43f, -2.79f, -1.43f, -2.79f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE1E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(36.8f, 103.1f)
                lineToRelative(55.14f, -0.71f)
                reflectiveCurveToRelative(-0.18f, -1.45f, -1.16f, -2.54f)
                curveToRelative(-0.82f, -0.92f, -1.98f, -1.43f, -1.98f, -1.43f)
                lineToRelative(-49.54f, 0.48f)
                reflectiveCurveToRelative(-1.0f, 0.74f, -1.59f, 1.59f)
                curveToRelative(-0.71f, 1.03f, -0.87f, 2.61f, -0.87f, 2.61f)
                close()
                moveTo(35.94f, 22.86f)
                reflectiveCurveToRelative(11.02f, -2.16f, 27.14f, -2.4f)
                curveToRelative(16.12f, -0.24f, 27.73f, 0.71f, 27.73f, 0.71f)
                reflectiveCurveToRelative(-0.57f, -1.9f, -1.45f, -2.85f)
                reflectiveCurveToRelative(-1.98f, -1.43f, -1.98f, -1.43f)
                reflectiveCurveToRelative(-15.11f, -0.55f, -24.25f, -0.63f)
                curveToRelative(-11.04f, -0.09f, -23.47f, 1.58f, -23.47f, 1.58f)
                reflectiveCurveToRelative(-1.33f, 0.38f, -2.23f, 1.55f)
                curveToRelative(-1.28f, 1.65f, -1.49f, 3.47f, -1.49f, 3.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF858585)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.76f, 25.96f)
                reflectiveCurveToRelative(8.58f, -1.68f, 27.71f, -1.92f)
                reflectiveCurveToRelative(27.83f, 1.15f, 27.83f, 1.15f)
                reflectiveCurveToRelative(0.08f, 1.16f, -0.3f, 2.27f)
                curveToRelative(-0.42f, 1.24f, -1.08f, 2.06f, -1.08f, 2.06f)
                reflectiveCurveToRelative(-16.45f, -1.1f, -26.45f, -1.02f)
                reflectiveCurveToRelative(-26.32f, 1.66f, -26.32f, 1.66f)
                reflectiveCurveToRelative(-0.69f, -1.19f, -0.93f, -1.9f)
                curveToRelative(-0.23f, -0.74f, -0.46f, -2.3f, -0.46f, -2.3f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.56f, 19.45f)
                curveToRelative(0.0f, 0.85f, -5.63f, 1.35f, -11.61f, 1.71f)
                curveToRelative(-3.35f, 0.2f, -9.26f, 1.85f, -9.26f, -0.36f)
                reflectiveCurveToRelative(2.14f, -2.63f, 9.4f, -2.78f)
                reflectiveCurveToRelative(11.47f, 0.29f, 11.47f, 1.43f)
                close()
                moveTo(60.99f, 6.28f)
                curveToRelative(0.19f, 0.54f, -2.85f, 1.57f, -5.2f, 4.06f)
                curveToRelative(-1.66f, 1.76f, -2.21f, 3.42f, -3.35f, 3.85f)
                reflectiveCurveToRelative(-7.26f, 1.0f, -7.34f, -0.28f)
                curveToRelative(-0.1f, -1.78f, 1.18f, -4.6f, 4.2f, -6.27f)
                curveToRelative(4.78f, -2.65f, 11.33f, -2.36f, 11.69f, -1.36f)
                close()
            }
        }
            .build()
        return _pole!!
    }

private var _pole: ImageVector? = null
