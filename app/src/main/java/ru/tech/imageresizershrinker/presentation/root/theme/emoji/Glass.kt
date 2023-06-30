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

val Emoji.Glass: ImageVector
    get() {
        if (_glass != null) {
            return _glass!!
        }
        _glass = Builder(
            name = "Glass", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(20.08f, 25.95f)
                lineToRelative(9.19f, 76.28f)
                lineToRelative(12.01f, 7.22f)
                lineToRelative(27.45f, 3.03f)
                lineToRelative(28.58f, -7.95f)
                lineToRelative(1.41f, -1.82f)
                lineToRelative(10.5f, -74.79f)
                lineToRelative(-21.37f, 5.43f)
                lineToRelative(-35.59f, 3.03f)
                lineToRelative(-27.83f, -7.76f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(16.96f, 20.21f)
                curveToRelative(-0.12f, -2.87f, 13.99f, -14.49f, 49.2f, -13.99f)
                reflectiveCurveToRelative(45.33f, 10.28f, 45.5f, 15.33f)
                reflectiveCurveToRelative(-6.4f, 69.09f, -6.4f, 69.09f)
                lineToRelative(-1.52f, 10.28f)
                reflectiveCurveToRelative(0.0f, 8.76f, -0.84f, 10.45f)
                reflectiveCurveToRelative(-6.56f, 12.44f, -37.47f, 12.75f)
                curveToRelative(-33.67f, 0.33f, -40.05f, -13.25f, -40.55f, -14.77f)
                curveToRelative(-0.51f, -1.52f, -0.51f, -12.64f, -0.51f, -12.64f)
                lineToRelative(-7.25f, -66.9f)
                curveToRelative(0.01f, 0.0f, 0.01f, -5.73f, -0.16f, -9.6f)
                close()
            }
            path(
                fill = radialGradient(
                    0.671f to Color(0xFFAC5810), 0.767f to Color(0xFFA8560F),
                    0.864f to Color(0xFF9C4F0D), 0.96f to Color(0xFF884409), center =
                    Offset(65.34507f, 68.921715f), radius = 29.397963f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(27.25f, 67.61f)
                reflectiveCurveToRelative(1.9f, 17.37f, 2.22f, 19.84f)
                curveToRelative(0.48f, 3.71f, 1.48f, 5.51f, 2.43f, 6.36f)
                curveToRelative(0.95f, 0.85f, 12.25f, 11.49f, 32.28f, 11.96f)
                reflectiveCurveToRelative(31.69f, -10.1f, 33.23f, -12.15f)
                curveToRelative(0.85f, -1.14f, 1.23f, -4.75f, 1.52f, -6.27f)
                curveToRelative(0.28f, -1.52f, 2.18f, -17.94f, 2.18f, -17.94f)
                lineToRelative(-73.86f, -1.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCF701E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(38.62f, 90.03f)
                curveToRelative(-1.5f, 3.68f, 4.05f, 7.04f, 10.62f, 9.23f)
                curveToRelative(10.29f, 3.43f, 24.17f, 3.18f, 32.09f, 0.08f)
                curveToRelative(5.1f, -2.0f, 8.66f, -4.41f, 7.84f, -8.0f)
                reflectiveCurveToRelative(-6.72f, -0.24f, -10.86f, 0.24f)
                curveToRelative(-8.41f, 0.98f, -17.15f, 1.14f, -25.32f, 0.0f)
                reflectiveCurveToRelative(-13.3f, -4.17f, -14.37f, -1.55f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDD821D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(28.68f, 66.22f)
                lineToRelative(4.09f, 6.75f)
                lineToRelative(13.63f, 4.91f)
                lineToRelative(15.54f, 1.36f)
                lineToRelative(17.48f, -0.29f)
                lineToRelative(11.12f, -2.47f)
                lineToRelative(6.75f, -4.66f)
                lineToRelative(3.65f, -6.14f)
                reflectiveCurveToRelative(-2.7f, -6.6f, -18.75f, -9.16f)
                curveToRelative(-16.06f, -2.57f, -33.25f, 0.38f, -33.25f, 0.38f)
                lineToRelative(-14.85f, 4.0f)
                lineToRelative(-5.41f, 5.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF6AF3B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(36.02f, 71.5f)
                reflectiveCurveToRelative(2.12f, -3.25f, 6.45f, -5.14f)
                curveToRelative(5.23f, -2.29f, 13.47f, -2.78f, 17.15f, -1.47f)
                curveToRelative(3.67f, 1.31f, 3.02f, 4.74f, 1.55f, 6.21f)
                curveToRelative(-1.47f, 1.47f, -3.1f, 2.86f, -3.76f, 4.33f)
                reflectiveCurveToRelative(-1.14f, 3.51f, -1.14f, 3.51f)
                lineToRelative(-9.88f, -1.06f)
                lineToRelative(-7.76f, -2.86f)
                lineToRelative(-2.61f, -3.52f)
                close()
                moveTo(84.86f, 65.43f)
                curveToRelative(-0.05f, 1.54f, 1.64f, 1.97f, 2.65f, 2.21f)
                reflectiveCurveToRelative(1.92f, 0.82f, 1.92f, 1.49f)
                reflectiveCurveToRelative(-0.23f, 1.56f, -0.96f, 2.12f)
                curveToRelative(-0.82f, 0.63f, -1.16f, 1.65f, -0.72f, 2.31f)
                curveToRelative(0.58f, 0.87f, 2.31f, 1.49f, 4.33f, 0.05f)
                curveToRelative(2.19f, -1.56f, 3.11f, -3.57f, 2.6f, -6.16f)
                curveToRelative(-0.63f, -3.18f, -3.37f, -4.23f, -5.87f, -4.43f)
                curveToRelative(-2.26f, -0.17f, -3.9f, 0.77f, -3.95f, 2.41f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF6AF3B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(32.32f, 67.43f)
                reflectiveCurveToRelative(2.01f, -5.92f, 13.61f, -7.94f)
                curveToRelative(12.43f, -2.16f, 22.35f, -1.42f, 27.06f, -1.36f)
                curveToRelative(5.12f, 0.06f, 13.48f, 1.9f, 13.79f, 0.36f)
                curveToRelative(0.31f, -1.54f, -5.57f, -3.17f, -13.82f, -3.71f)
                curveToRelative(-8.26f, -0.54f, -26.26f, -1.54f, -37.76f, 4.25f)
                reflectiveCurveToRelative(-6.49f, 8.88f, -6.49f, 8.88f)
                lineToRelative(3.61f, -0.48f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF8C977)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(97.67f, 63.82f)
                curveToRelative(-1.72f, 0.2f, -0.76f, 2.01f, -1.16f, 4.17f)
                curveToRelative(-0.93f, 5.02f, -13.05f, 9.73f, -30.43f, 9.42f)
                curveToRelative(-17.22f, -0.31f, -30.81f, -4.4f, -33.36f, -9.65f)
                curveToRelative(-1.08f, -2.22f, -0.93f, -3.86f, -3.4f, -3.78f)
                reflectiveCurveToRelative(-2.49f, 2.9f, -1.85f, 4.32f)
                curveToRelative(1.31f, 2.93f, 3.6f, 7.95f, 20.0f, 11.66f)
                curveToRelative(18.77f, 4.25f, 36.76f, 1.54f, 45.49f, -2.32f)
                curveToRelative(8.23f, -3.64f, 8.26f, -7.65f, 8.42f, -9.58f)
                reflectiveCurveToRelative(-1.0f, -4.55f, -3.71f, -4.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(31.69f, 39.41f)
                reflectiveCurveToRelative(0.48f, 14.37f, 1.96f, 19.8f)
                curveToRelative(1.09f, 4.01f, 3.04f, 6.45f, 5.88f, 6.58f)
                curveToRelative(2.16f, 0.1f, 5.21f, -1.96f, 5.63f, -6.91f)
                curveToRelative(0.46f, -5.56f, 0.51f, -17.0f, 0.51f, -17.0f)
                reflectiveCurveToRelative(-3.04f, -0.13f, -7.15f, -0.7f)
                curveToRelative(-3.78f, -0.52f, -6.83f, -1.77f, -6.83f, -1.77f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(109.22f, 27.92f)
                reflectiveCurveToRelative(-7.92f, 58.43f, -8.18f, 62.31f)
                curveToRelative(-0.27f, 3.95f, -0.7f, 7.34f, 1.83f, 7.42f)
                curveToRelative(2.25f, 0.07f, 2.73f, -8.07f, 3.17f, -11.7f)
                curveToRelative(0.45f, -3.64f, 8.02f, -63.67f, 8.02f, -63.67f)
                lineToRelative(-2.2f, -3.17f)
                lineToRelative(-2.64f, 8.81f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD5D5D5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.17f, 21.94f)
                reflectiveCurveToRelative(8.29f, 67.6f, 8.5f, 69.7f)
                curveToRelative(0.21f, 2.1f, 0.19f, 6.34f, 3.26f, 6.09f)
                curveToRelative(2.52f, -0.2f, 1.47f, -4.83f, 1.15f, -6.93f)
                curveToRelative(-0.31f, -2.1f, -7.03f, -64.66f, -7.03f, -64.66f)
                lineToRelative(-5.88f, -4.2f)
                close()
                moveTo(28.92f, 102.45f)
                curveToRelative(-0.94f, 3.25f, 4.94f, 13.57f, 34.54f, 13.96f)
                curveToRelative(23.72f, 0.31f, 36.24f, -7.76f, 35.69f, -13.65f)
                curveToRelative(-0.52f, -5.67f, -13.96f, 7.62f, -34.85f, 6.93f)
                curveToRelative(-25.51f, -0.83f, -33.51f, -13.67f, -35.38f, -7.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEFEFE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(31.02f, 103.92f)
                curveToRelative(0.07f, 2.03f, 2.49f, 4.63f, 9.04f, 7.2f)
                curveToRelative(7.24f, 2.83f, 13.2f, 3.17f, 13.41f, 0.74f)
                curveToRelative(0.25f, -2.84f, -4.39f, -2.38f, -11.32f, -4.89f)
                curveToRelative(-6.74f, -2.45f, -11.25f, -6.4f, -11.13f, -3.05f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFc0c0c0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(65.12f, 4.22f)
                curveToRelative(-33.02f, -0.25f, -49.26f, 7.99f, -50.08f, 15.83f)
                curveToRelative(-0.78f, 7.42f, 11.87f, 18.97f, 49.81f, 18.83f)
                curveToRelative(36.57f, -0.13f, 49.14f, -9.83f, 49.26f, -17.19f)
                curveToRelative(0.13f, -8.05f, -13.51f, -17.2f, -48.99f, -17.47f)
                close()
                moveTo(65.25f, 33.15f)
                curveToRelative(-32.61f, -0.82f, -44.08f, -9.83f, -43.94f, -13.1f)
                curveToRelative(0.14f, -3.28f, 11.05f, -11.74f, 43.67f, -11.46f)
                curveToRelative(31.66f, 0.27f, 43.12f, 9.28f, 43.12f, 11.6f)
                curveToRelative(0.0f, 2.32f, -10.23f, 13.78f, -42.85f, 12.96f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.48f, 35.34f)
                curveToRelative(35.28f, 0.35f, 44.66f, -10.32f, 44.78f, -13.95f)
                reflectiveCurveToRelative(-3.4f, -4.1f, -3.4f, -4.1f)
                reflectiveCurveToRelative(0.45f, 1.87f, 0.0f, 3.05f)
                curveToRelative(-1.29f, 3.4f, -11.49f, 11.02f, -41.61f, 11.25f)
                curveToRelative(-29.67f, 0.23f, -40.9f, -7.65f, -41.5f, -11.02f)
                curveToRelative(-0.35f, -1.99f, 0.94f, -2.93f, 0.47f, -3.63f)
                curveToRelative(-0.47f, -0.7f, -2.93f, 0.82f, -3.63f, 3.63f)
                curveToRelative(-0.93f, 3.73f, 3.51f, 14.36f, 44.89f, 14.77f)
                close()
            }
        }
            .build()
        return _glass!!
    }

private var _glass: ImageVector? = null
