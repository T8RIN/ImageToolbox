package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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

public val Emoji.Package: ImageVector
    get() {
        if (_package != null) {
            return _package!!
        }
        _package = Builder(name = "Package", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-183.97f, 248.8f)
                horizontalLineToRelative(302.33f)
                verticalLineToRelative(169.12f)
                horizontalLineToRelative(-302.33f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDEA66C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(62.54f, 4.84f)
                lineTo(10.38f, 31.73f)
                curveToRelative(-1.14f, 0.67f, -1.47f, 1.67f, -1.46f, 3.12f)
                lineToRelative(0.1f, 1.81f)
                lineTo(64.09f, 64.9f)
                lineToRelative(54.94f, -27.79f)
                reflectiveCurveToRelative(0.06f, -0.28f, 0.06f, -1.26f)
                curveToRelative(0.0f, 0.0f, -0.05f, -1.29f, -0.34f, -1.89f)
                curveToRelative(-0.32f, -0.68f, -1.0f, -1.29f, -1.44f, -1.66f)
                curveToRelative(-1.17f, -0.97f, -2.34f, -1.83f, -4.33f, -2.95f)
                curveTo(84.45f, 13.22f, 65.69f, 4.91f, 65.69f, 4.91f)
                curveToRelative(-1.21f, -0.45f, -2.21f, -0.61f, -3.15f, -0.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFFB38251)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.74f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(1.11f, 0.46f, 1.77f, 0.68f, 2.54f, 0.72f)
                curveToRelative(0.57f, 0.03f, 1.51f, -0.35f, 1.51f, -0.35f)
                reflectiveCurveToRelative(-1.53f, -55.19f, -1.62f, -58.34f)
                curveToRelative(-0.09f, -3.08f, -1.38f, -4.1f, -2.52f, -4.84f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.07f, -0.51f, -2.33f, 0.13f, -2.33f, 1.33f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.57f, 0.35f, 1.05f, 0.83f, 1.36f)
                close()
            }
            path(fill = SolidColor(Color(0xFF966239)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(117.58f, 96.51f)
                lineTo(65.82f, 123.2f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.17f, -1.76f, -0.34f, -2.24f)
                curveToRelative(-0.47f, -1.4f, 0.55f, -1.96f, 1.01f, -2.19f)
                lineToRelative(51.34f, -26.44f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                curveToRelative(0.0f, 1.03f, -0.58f, 1.98f, -1.51f, 2.46f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(94.46f, 47.59f)
                reflectiveCurveTo(66.09f, 62.1f, 65.63f, 63.33f)
                curveToRelative(-0.46f, 1.22f, 22.26f, -9.7f, 24.92f, -11.07f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.01f, -1.14f, -22.74f, 10.91f, -22.74f, 10.91f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.81f, 92.85f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(20.58f, 93.66f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.02f, 3.1f, -0.03f, 4.64f, -0.04f, 7.74f)
                close()
                moveTo(14.12f, 90.34f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.65f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.95f, 85.99f)
                curveToRelative(2.37f, 1.21f, 3.63f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(12.81f, 82.84f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.6f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(31.81f, 101.4f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.03f, 3.66f, 0.04f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(28.76f, 100.75f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.12f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(34.55f, 90.36f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                curveToRelative(-3.52f, -1.83f, -5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE0B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(100.74f, 41.49f)
                curveToRelative(-0.5f, -0.37f, -54.19f, -28.42f, -54.19f, -28.42f)
                lineToRelative(-16.66f, 8.61f)
                lineToRelative(55.05f, 28.39f)
                lineToRelative(0.03f, 0.03f)
                lineToRelative(16.11f, -8.25f)
                reflectiveCurveToRelative(-0.03f, -0.08f, -0.12f, -0.18f)
                curveToRelative(-0.07f, -0.06f, -0.19f, -0.15f, -0.22f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF9F85)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(101.0f, 41.73f)
                curveToRelative(-9.12f, 4.49f, -16.06f, 8.34f, -16.06f, 8.34f)
                curveToRelative(0.23f, 0.23f, 0.24f, 0.53f, 0.24f, 0.68f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.08f, -0.08f, 3.23f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.19f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.02f, -1.08f, -0.25f, -1.19f, -0.25f, -1.19f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDEA66C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(-98.73f, 4.84f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveTo(-76.82f, 13.22f, -95.58f, 4.91f, -95.58f, 4.91f)
                curveToRelative(-1.21f, -0.45f, -2.2f, -0.61f, -3.15f, -0.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF8049)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-151.53f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.58f, 0.36f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF9E673C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(-43.69f, 96.51f)
                lineToRelative(-51.76f, 26.69f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                curveToRelative(0.0f, 1.04f, -0.58f, 1.99f, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-70.71f, 52.26f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveTo(-95.18f, 62.1f, -95.64f, 63.33f)
                curveToRelative(-0.45f, 1.22f, 22.26f, -9.7f, 24.93f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-148.46f, 92.85f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(-140.69f, 93.66f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.64f, -0.04f, 7.74f)
                close()
                moveTo(-147.15f, 90.34f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.65f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-142.32f, 85.99f)
                curveToRelative(2.37f, 1.21f, 3.63f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(-148.46f, 82.84f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.6f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-129.46f, 101.4f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.03f, 3.66f, 0.04f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF212121)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                    = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-132.51f, 100.75f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.12f, -0.57f, -1.94f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(-126.72f, 90.36f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                curveToRelative(-3.52f, -1.83f, -5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.63f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE0B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-61.86f, 42.33f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                arcToRelative(1.15f, 1.15f, 0.0f, false, false, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF9F85)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-61.63f, 42.5f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.73f, 0.74f, 0.73f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.02f, -0.87f, -0.3f, -1.26f, -0.3f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDEA66C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(223.81f, 4.84f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveToRelative(-28.53f, -16.12f, -47.29f, -24.42f, -47.29f, -24.42f)
                curveToRelative(-1.2f, -0.47f, -2.2f, -0.63f, -3.14f, -0.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF8049)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(171.01f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.58f, 0.35f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF9E673C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(278.85f, 96.51f)
                lineToRelative(-51.76f, 26.69f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                curveToRelative(0.0f, 1.04f, -0.59f, 1.99f, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(251.82f, 52.26f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveTo(227.36f, 62.1f, 226.9f, 63.33f)
                curveToRelative(-0.46f, 1.22f, 22.26f, -9.7f, 24.92f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(173.36f, 93.22f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(181.13f, 94.02f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.02f, 3.1f, -0.03f, 4.65f, -0.04f, 7.74f)
                close()
                moveTo(174.67f, 90.71f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.09f, -0.02f, 4.64f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(179.5f, 86.36f)
                curveToRelative(2.37f, 1.21f, 3.63f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(173.36f, 83.2f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.6f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(192.35f, 101.77f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.04f, 3.66f, 0.05f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(189.31f, 101.12f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.12f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(195.1f, 90.73f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                curveToRelative(-3.52f, -1.83f, -5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFE0B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(260.68f, 42.33f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                curveToRelative(-0.07f, -0.06f, -0.14f, -0.12f, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF9F85)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(260.91f, 42.5f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.73f, 0.74f, 0.73f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.02f, -0.87f, -0.3f, -1.26f, -0.3f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDEA66C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(566.51f, 4.84f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveToRelative(-28.53f, -16.12f, -47.29f, -24.42f, -47.29f, -24.42f)
                curveToRelative(-1.2f, -0.47f, -2.2f, -0.63f, -3.14f, -0.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBF8049)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(513.71f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.58f, 0.35f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF9E673C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(621.55f, 96.51f)
                lineToRelative(-51.76f, 26.69f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                arcToRelative(2.815f, 2.815f, 0.0f, false, true, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(594.52f, 52.26f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveTo(570.06f, 62.1f, 569.6f, 63.33f)
                curveToRelative(-0.46f, 1.22f, 22.26f, -9.7f, 24.92f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(516.06f, 93.22f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(523.82f, 94.02f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.65f, -0.04f, 7.74f)
                close()
                moveTo(517.37f, 90.71f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.09f, -0.02f, 4.64f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(522.2f, 86.36f)
                curveToRelative(2.37f, 1.21f, 3.64f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(516.06f, 83.2f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.61f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(535.05f, 101.77f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.04f, 3.66f, 0.05f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.85f, strokeAlpha
                    = 0.85f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(532.01f, 101.12f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.13f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(537.8f, 90.73f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                reflectiveCurveToRelative(-5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCE8B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(603.38f, 42.33f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                curveToRelative(-0.07f, -0.06f, -0.14f, -0.12f, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFECB354)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(603.61f, 42.5f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.72f, 0.74f, 0.72f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.01f, -0.87f, -0.29f, -1.26f, -0.29f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFDEA66C)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(62.54f, 216.07f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveToRelative(-28.53f, -16.12f, -47.29f, -24.42f, -47.29f, -24.42f)
                curveToRelative(-1.2f, -0.47f, -2.2f, -0.63f, -3.14f, -0.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBD8955)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(9.74f, 307.39f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.59f, 0.35f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA06841)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(117.58f, 307.74f)
                lineToRelative(-51.76f, 26.69f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                curveToRelative(0.0f, 1.04f, -0.58f, 1.99f, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(90.55f, 263.49f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveToRelative(-28.37f, 14.51f, -28.83f, 15.74f)
                curveToRelative(-0.44f, 1.22f, 22.27f, -9.7f, 24.93f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(12.09f, 304.45f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(19.86f, 305.25f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.02f, 3.1f, -0.03f, 4.65f, -0.04f, 7.74f)
                close()
                moveTo(13.4f, 301.94f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.09f, -0.02f, 4.64f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(18.23f, 297.59f)
                curveToRelative(2.37f, 1.21f, 3.63f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(12.09f, 294.43f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.6f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(31.09f, 313.0f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.03f, 3.66f, 0.04f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(28.04f, 312.35f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.33f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.12f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(33.83f, 301.96f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                curveToRelative(-3.52f, -1.83f, -5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCE8B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(99.41f, 253.56f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                curveToRelative(-0.06f, -0.06f, -0.14f, -0.12f, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFECB354)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(99.64f, 253.73f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.73f, 0.74f, 0.73f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.02f, -0.87f, -0.3f, -1.26f, -0.3f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE6A467)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(401.82f, 4.84f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveToRelative(-28.53f, -16.12f, -47.29f, -24.42f, -47.29f, -24.42f)
                curveToRelative(-1.2f, -0.47f, -2.2f, -0.63f, -3.14f, -0.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFD68B52)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(349.02f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.58f, 0.35f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA06841)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(456.86f, 96.51f)
                lineTo(405.1f, 123.2f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                arcToRelative(2.815f, 2.815f, 0.0f, false, true, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(429.83f, 52.26f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveTo(405.36f, 62.1f, 404.9f, 63.33f)
                curveToRelative(-0.45f, 1.22f, 22.27f, -9.7f, 24.93f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(351.37f, 93.22f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(359.13f, 94.02f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.65f, -0.04f, 7.74f)
                close()
                moveTo(352.68f, 90.71f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.35f, -1.21f)
                curveToRelative(0.0f, 3.09f, -0.01f, 4.64f, -0.02f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(357.51f, 86.36f)
                curveToRelative(2.37f, 1.21f, 3.64f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.17f, 0.76f, -1.78f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(351.37f, 83.2f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.61f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(370.36f, 101.77f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.04f, 3.66f, 0.05f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(367.32f, 101.12f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.13f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(373.11f, 90.73f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                curveToRelative(-3.52f, -1.83f, -5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCE8B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(438.69f, 42.33f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                curveToRelative(-0.07f, -0.06f, -0.14f, -0.12f, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFECB354)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(438.92f, 42.5f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.73f, 0.74f, 0.73f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.02f, -0.87f, -0.3f, -1.26f, -0.3f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE6A475)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(-240.63f, 4.84f)
                lineToRelative(-51.83f, 26.68f)
                curveToRelative(-2.52f, 1.48f, -1.8f, 5.09f, -1.8f, 5.09f)
                lineToRelative(55.17f, 28.3f)
                lineToRelative(55.07f, -27.86f)
                curveToRelative(0.05f, -0.17f, 0.11f, -1.21f, -0.15f, -3.19f)
                curveToRelative(-0.14f, -1.04f, -3.25f, -2.94f, -6.03f, -4.51f)
                curveToRelative(-28.53f, -16.12f, -47.29f, -24.42f, -47.29f, -24.42f)
                curveToRelative(-1.2f, -0.47f, -2.2f, -0.63f, -3.14f, -0.09f)
                close()
            }
            path(fill = SolidColor(Color(0xFFC78C61)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-293.43f, 96.16f)
                curveToRelative(3.04f, 2.01f, 8.59f, 4.65f, 14.2f, 7.69f)
                curveToRelative(13.04f, 7.06f, 29.52f, 15.64f, 37.72f, 19.01f)
                curveToRelative(2.45f, 1.01f, 3.75f, 0.51f, 3.75f, 0.51f)
                reflectiveCurveToRelative(-1.15f, -56.3f, -1.11f, -59.45f)
                curveToRelative(0.02f, -1.32f, -1.59f, -3.12f, -2.74f, -3.86f)
                curveToRelative(-1.78f, -1.14f, -5.64f, -2.92f, -5.64f, -2.92f)
                reflectiveCurveToRelative(-13.52f, -6.72f, -27.02f, -13.8f)
                curveToRelative(-6.42f, -3.37f, -11.84f, -6.82f, -17.67f, -9.66f)
                curveToRelative(-1.06f, -0.52f, -2.33f, 0.12f, -2.32f, 1.31f)
                verticalLineToRelative(59.8f)
                curveToRelative(0.0f, 0.58f, 0.35f, 1.06f, 0.83f, 1.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA06841)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(-185.59f, 96.51f)
                lineToRelative(-51.76f, 26.69f)
                curveToRelative(-0.83f, 0.43f, -1.82f, -0.17f, -1.82f, -1.11f)
                lineToRelative(0.09f, -57.18f)
                curveToRelative(0.0f, -0.55f, -0.41f, -1.98f, -0.56f, -2.48f)
                curveToRelative(-0.14f, -0.5f, 0.35f, -1.51f, 0.81f, -1.74f)
                lineToRelative(51.76f, -26.66f)
                curveToRelative(1.36f, -0.7f, 2.99f, 0.29f, 2.99f, 1.82f)
                verticalLineToRelative(58.19f)
                arcToRelative(2.815f, 2.815f, 0.0f, false, true, -1.51f, 2.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-212.62f, 52.26f)
                curveToRelative(3.01f, -1.55f, 26.65f, -14.19f, 26.65f, -15.58f)
                curveToRelative(0.0f, -1.14f, -22.75f, 10.91f, -22.75f, 10.91f)
                reflectiveCurveToRelative(-28.37f, 14.51f, -28.83f, 15.74f)
                curveToRelative(-0.45f, 1.22f, 22.26f, -9.7f, 24.93f, -11.07f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-291.08f, 93.22f)
                curveToRelative(3.69f, 1.86f, 7.35f, 3.78f, 11.91f, 6.11f)
                verticalLineToRelative(-1.78f)
                curveToRelative(-4.54f, -2.32f, -8.21f, -4.23f, -11.91f, -6.11f)
                verticalLineToRelative(1.78f)
                close()
                moveTo(-283.32f, 94.02f)
                curveToRelative(1.04f, 0.53f, 1.56f, 0.8f, 2.54f, 1.3f)
                curveToRelative(0.01f, -3.1f, 0.01f, -4.65f, 0.02f, -7.75f)
                curveToRelative(-0.98f, -0.5f, -1.49f, -0.76f, -2.52f, -1.29f)
                curveToRelative(-0.01f, 3.1f, -0.02f, 4.65f, -0.04f, 7.74f)
                close()
                moveTo(-289.77f, 90.71f)
                curveToRelative(0.86f, 0.44f, 1.34f, 0.69f, 2.32f, 1.19f)
                lineToRelative(0.05f, -7.72f)
                curveToRelative(-0.99f, -0.51f, -1.47f, -0.75f, -2.34f, -1.21f)
                curveToRelative(-0.01f, 3.09f, -0.02f, 4.64f, -0.03f, 7.74f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-284.95f, 86.36f)
                curveToRelative(2.37f, 1.21f, 3.63f, 1.86f, 5.72f, 2.92f)
                curveToRelative(-1.04f, -1.89f, -1.61f, -2.85f, -2.75f, -4.79f)
                curveToRelative(-1.16f, 0.76f, -1.77f, 1.13f, -2.97f, 1.87f)
                close()
                moveTo(-291.08f, 83.2f)
                curveToRelative(1.77f, 0.92f, 2.96f, 1.53f, 5.3f, 2.73f)
                curveToRelative(-1.16f, -1.95f, -1.74f, -2.92f, -2.81f, -4.83f)
                curveToRelative(-1.09f, 0.79f, -1.61f, 1.2f, -2.49f, 2.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-272.09f, 101.77f)
                curveToRelative(0.83f, 0.43f, 1.26f, 0.66f, 2.09f, 1.09f)
                curveToRelative(-0.06f, -3.68f, -0.09f, -5.52f, -0.16f, -9.2f)
                curveToRelative(-0.8f, -0.42f, -1.2f, -0.63f, -2.0f, -1.04f)
                curveToRelative(0.04f, 3.66f, 0.05f, 5.49f, 0.07f, 9.15f)
                close()
            }
            path(fill = SolidColor(Color(0xFF784D30)), stroke = null, fillAlpha = 0.73f, strokeAlpha
                    = 0.73f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(-275.13f, 101.12f)
                curveToRelative(3.04f, 1.6f, 5.4f, 2.8f, 8.32f, 4.36f)
                curveToRelative(-0.02f, -1.05f, -0.83f, -2.32f, -1.93f, -2.91f)
                curveToRelative(-1.72f, -0.91f, -2.7f, -1.41f, -4.44f, -2.32f)
                curveToRelative(-1.13f, -0.57f, -1.95f, -0.16f, -1.95f, 0.87f)
                close()
                moveTo(-269.34f, 90.73f)
                lineToRelative(1.28f, 2.71f)
                curveToRelative(-0.4f, 0.85f, -0.61f, 1.28f, -1.03f, 2.12f)
                curveToRelative(0.36f, -0.03f, 0.54f, -0.04f, 0.9f, -0.07f)
                curveToRelative(0.48f, -0.64f, 0.72f, -0.96f, 1.18f, -1.6f)
                curveToRelative(-0.42f, -0.99f, -0.64f, -1.49f, -1.08f, -2.48f)
                curveToRelative(1.22f, 0.67f, 1.82f, 1.01f, 2.93f, 1.66f)
                curveToRelative(0.0f, 3.58f, -2.34f, 5.16f, -5.86f, 3.32f)
                reflectiveCurveToRelative(-5.85f, -5.92f, -5.85f, -9.51f)
                curveToRelative(2.62f, 1.31f, 4.38f, 2.17f, 7.53f, 3.85f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFCE8B2)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-203.76f, 42.33f)
                curveToRelative(-0.5f, -0.37f, -54.32f, -28.49f, -54.32f, -28.49f)
                lineToRelative(-16.68f, 8.6f)
                lineToRelative(55.11f, 28.37f)
                curveToRelative(4.76f, -2.45f, 12.32f, -6.35f, 16.12f, -8.3f)
                curveToRelative(-0.07f, -0.06f, -0.14f, -0.12f, -0.23f, -0.18f)
                close()
            }
            path(fill = SolidColor(Color(0xFFECB354)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(-203.54f, 42.5f)
                curveToRelative(-8.34f, 3.77f, -16.5f, 8.11f, -16.5f, 8.11f)
                curveToRelative(0.21f, 0.09f, 0.73f, 0.74f, 0.73f, 0.74f)
                curveToRelative(-0.03f, 1.16f, -0.05f, 2.31f, -0.08f, 3.47f)
                curveToRelative(-0.14f, 5.95f, -0.22f, 11.91f, -0.42f, 17.86f)
                curveToRelative(0.67f, -0.4f, 1.48f, 0.48f, 2.09f, 0.0f)
                curveToRelative(0.94f, -0.74f, 2.94f, -4.0f, 3.24f, -4.1f)
                curveToRelative(0.4f, -0.13f, 0.83f, -0.03f, 1.24f, -0.05f)
                curveToRelative(1.8f, -0.1f, 5.83f, -3.77f, 6.69f, -3.94f)
                curveToRelative(0.83f, -0.16f, 3.57f, 0.37f, 3.57f, 0.37f)
                reflectiveCurveToRelative(-0.21f, -18.15f, -0.26f, -21.2f)
                curveToRelative(-0.01f, -0.87f, -0.3f, -1.26f, -0.3f, -1.26f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE6A475)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(90.29f, -146.79f)
                lineToRelative(-10.47f, 5.4f)
                lineToRelative(-21.48f, 11.07f)
                curveToRelative(-3.76f, 1.94f, -7.53f, 3.87f, -11.29f, 5.81f)
                curveToRelative(-2.52f, 1.3f, -7.89f, 2.93f, -8.71f, 6.08f)
                curveToRelative(-0.18f, 0.71f, -0.11f, 1.45f, -0.03f, 2.18f)
                curveToRelative(0.36f, 3.32f, 0.27f, 6.27f, 2.43f, 8.9f)
                curveToRelative(2.04f, 2.48f, 5.47f, 5.8f, 9.01f, 5.8f)
                curveToRelative(8.11f, 2.65f, 15.85f, 6.28f, 23.82f, 9.3f)
                curveToRelative(7.12f, 2.7f, 16.09f, 6.9f, 23.36f, 2.82f)
                curveToRelative(0.88f, -0.49f, 1.6f, -1.16f, 2.49f, -1.6f)
                curveToRelative(1.19f, -0.59f, 2.43f, -1.05f, 3.61f, -1.68f)
                curveToRelative(3.35f, -1.77f, 6.3f, -4.2f, 9.47f, -6.26f)
                curveToRelative(5.34f, -3.48f, 11.24f, -5.92f, 16.99f, -8.61f)
                curveToRelative(2.89f, -1.35f, 5.76f, -2.77f, 8.49f, -4.42f)
                arcToRelative(47.9f, 47.9f, 0.0f, false, false, 3.96f, -2.65f)
                curveToRelative(0.98f, -0.73f, 2.71f, -1.74f, 3.05f, -3.01f)
                curveToRelative(0.05f, -0.17f, 0.07f, -0.34f, 0.07f, -0.52f)
                curveToRelative(-0.01f, -0.7f, -0.41f, -1.34f, -0.94f, -1.79f)
                curveToRelative(-1.76f, -1.5f, -4.26f, -2.28f, -6.33f, -3.19f)
                curveToRelative(-2.7f, -1.18f, -5.24f, -2.87f, -7.81f, -4.32f)
                curveToRelative(-5.42f, -3.06f, -10.88f, -6.04f, -16.38f, -8.93f)
                arcToRelative(675.49f, 675.49f, 0.0f, false, false, -12.28f, -6.3f)
                curveToRelative(-2.11f, -1.05f, -4.22f, -2.09f, -6.34f, -3.13f)
                curveToRelative(-1.36f, -0.66f, -3.13f, -1.75f, -4.69f, -0.95f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA06841)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(38.78f, -57.74f)
                curveToRelative(0.77f, 0.88f, 1.81f, 1.52f, 2.83f, 2.1f)
                curveToRelative(5.09f, 2.89f, 10.31f, 5.52f, 15.53f, 8.2f)
                curveToRelative(2.61f, 1.35f, 5.23f, 2.69f, 7.84f, 4.04f)
                curveToRelative(2.57f, 1.33f, 5.15f, 2.65f, 7.72f, 3.98f)
                lineToRelative(6.93f, 3.57f)
                curveToRelative(1.82f, 0.94f, 3.64f, 1.88f, 5.46f, 2.81f)
                curveToRelative(1.1f, 0.57f, 2.2f, 1.14f, 3.31f, 1.7f)
                curveToRelative(1.41f, 0.73f, 2.39f, 1.44f, 4.02f, 0.85f)
                curveToRelative(1.36f, -0.49f, 1.44f, -2.15f, 1.75f, -3.43f)
                curveToRelative(0.86f, -3.51f, 1.22f, -7.13f, 1.37f, -10.74f)
                curveToRelative(0.21f, -4.78f, 0.05f, -9.58f, -0.1f, -14.36f)
                curveToRelative(-0.18f, -5.74f, 0.18f, -11.49f, -0.06f, -17.26f)
                curveToRelative(-0.07f, -1.58f, -0.11f, -3.17f, -0.16f, -4.74f)
                curveToRelative(-0.04f, -1.39f, -0.89f, -2.66f, -1.7f, -3.74f)
                curveToRelative(-0.99f, -1.31f, -2.11f, -2.69f, -2.18f, -4.33f)
                curveToRelative(-0.13f, -2.69f, -2.81f, -3.47f, -4.83f, -4.36f)
                curveToRelative(-3.05f, -1.35f, -5.99f, -3.01f, -8.96f, -4.53f)
                arcToRelative(1074.144f, 1074.144f, 0.0f, false, true, -31.34f, -16.7f)
                curveToRelative(-1.25f, -0.69f, -8.13f, -6.01f, -8.16f, -2.05f)
                curveToRelative(-0.16f, 17.81f, -0.33f, 35.61f, -0.62f, 53.42f)
                curveToRelative(-0.03f, 1.6f, -0.03f, 3.29f, 0.75f, 4.68f)
                curveToRelative(0.17f, 0.35f, 0.37f, 0.63f, 0.6f, 0.89f)
                close()
            }
            path(fill = SolidColor(Color(0xFFC78C61)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(144.15f, -57.63f)
                curveToRelative(-1.05f, 1.05f, -2.57f, 1.8f, -4.5f, 2.79f)
                curveToRelative(-4.13f, 2.12f, -8.27f, 4.25f, -12.4f, 6.37f)
                curveToRelative(-8.41f, 4.32f, -16.81f, 8.64f, -25.22f, 12.95f)
                curveToRelative(-1.83f, 0.94f, -8.1f, 5.04f, -10.16f, 4.15f)
                curveToRelative(-1.58f, -0.68f, -0.62f, -8.5f, -0.62f, -10.22f)
                verticalLineToRelative(-33.5f)
                curveToRelative(0.0f, -4.14f, 0.39f, -8.53f, -0.27f, -12.61f)
                curveToRelative(-0.2f, -1.24f, -0.42f, -2.39f, 0.33f, -3.49f)
                curveToRelative(1.2f, -1.74f, 4.09f, -2.67f, 5.93f, -3.62f)
                curveToRelative(2.23f, -1.15f, 4.46f, -2.3f, 6.7f, -3.45f)
                curveToRelative(5.66f, -2.92f, 11.31f, -5.83f, 16.97f, -8.75f)
                lineToRelative(13.5f, -6.96f)
                curveToRelative(2.41f, -1.24f, 5.07f, -3.18f, 7.66f, -3.95f)
                curveToRelative(2.3f, -0.68f, 2.74f, 1.36f, 2.88f, 3.28f)
                curveToRelative(0.4f, 5.75f, 0.33f, 11.58f, 0.46f, 17.34f)
                curveToRelative(0.26f, 11.46f, 0.42f, 22.91f, 0.5f, 34.37f)
                curveToRelative(0.02f, 2.61f, -0.58f, 4.13f, -1.76f, 5.3f)
                close()
            }
            path(fill = SolidColor(Color(0xFFA06841)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(117.23f, -100.16f)
                curveToRelative(2.95f, -1.52f, 26.07f, -13.88f, 26.08f, -15.24f)
                curveToRelative(0.0f, -1.12f, -22.26f, 10.67f, -22.26f, 10.67f)
                reflectiveCurveToRelative(-27.76f, 14.2f, -28.2f, 15.4f)
                curveToRelative(-0.45f, 1.2f, 21.77f, -9.48f, 24.38f, -10.83f)
                close()
            }
            path(fill = linearGradient(0.31f to Color(0xFFFCE8B2), 0.399f to Color(0xFFFFCC80),
                    start = Offset(96.68f,-108.368f), end = Offset(110.772995f,-79.066f)), stroke =
                    null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(110.19f, -80.18f)
                curveToRelative(0.2f, -5.82f, 0.28f, -11.65f, 0.41f, -17.47f)
                curveToRelative(0.03f, -1.13f, 0.05f, -2.26f, 0.08f, -3.39f)
                curveToRelative(0.0f, 0.0f, -0.01f, -0.38f, -0.21f, -0.47f)
                lineToRelative(-54.04f, -27.82f)
                lineToRelative(16.32f, -8.41f)
                reflectiveCurveToRelative(52.65f, 27.51f, 53.14f, 27.87f)
                curveToRelative(0.49f, 0.36f, 0.49f, 0.36f, 0.51f, 1.41f)
                curveToRelative(0.04f, 2.98f, 0.09f, 5.96f, 0.13f, 8.94f)
                lineToRelative(0.09f, 6.16f)
                curveToRelative(0.01f, 0.88f, 0.48f, 3.14f, 0.03f, 3.94f)
                curveToRelative(-0.44f, 0.78f, -2.68f, 1.18f, -3.49f, 1.34f)
                curveToRelative(-0.84f, 0.16f, -1.73f, 0.23f, -2.48f, 0.66f)
                curveToRelative(-1.54f, 0.87f, -2.31f, 3.09f, -4.07f, 3.2f)
                curveToRelative(-0.41f, 0.02f, -0.83f, -0.08f, -1.22f, 0.05f)
                curveToRelative(-0.29f, 0.1f, -0.52f, 0.31f, -0.74f, 0.52f)
                curveToRelative(-0.85f, 0.81f, -1.67f, 1.73f, -2.59f, 2.46f)
                curveToRelative(-0.59f, 0.45f, -1.21f, 0.61f, -1.87f, 1.01f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFCC80)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(127.72f, -48.65f)
                curveToRelative(0.0f, -2.64f, -0.18f, -8.15f, -0.18f, -8.15f)
                reflectiveCurveToRelative(-2.07f, 1.76f, -3.37f, 2.25f)
                curveToRelative(-1.02f, 0.38f, -2.13f, 0.58f, -3.02f, 1.21f)
                curveToRelative(-0.92f, 0.66f, -1.52f, 1.71f, -2.46f, 2.35f)
                curveToRelative(-2.32f, 1.59f, -5.48f, 0.96f, -5.48f, 0.96f)
                verticalLineToRelative(8.86f)
                lineToRelative(14.51f, -7.48f)
                close()
            }
            path(fill = SolidColor(Color(0xFF7D5133)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(40.47f, -60.08f)
                curveToRelative(3.61f, 1.82f, 7.19f, 3.7f, 11.66f, 5.98f)
                verticalLineToRelative(-1.74f)
                curveToRelative(-4.44f, -2.27f, -8.03f, -4.14f, -11.66f, -5.98f)
                verticalLineToRelative(1.74f)
                close()
                moveTo(48.07f, -59.3f)
                curveToRelative(1.01f, 0.52f, 1.52f, 0.78f, 2.49f, 1.28f)
                curveToRelative(0.01f, -3.03f, 0.01f, -4.55f, 0.02f, -7.58f)
                curveToRelative(-0.95f, -0.49f, -1.46f, -0.74f, -2.47f, -1.26f)
                curveToRelative(-0.02f, 3.02f, -0.03f, 4.54f, -0.04f, 7.56f)
                close()
                moveTo(41.75f, -62.54f)
                curveToRelative(0.85f, 0.43f, 1.31f, 0.67f, 2.27f, 1.16f)
                lineToRelative(0.05f, -7.56f)
                curveToRelative(-0.97f, -0.5f, -1.43f, -0.74f, -2.29f, -1.18f)
                curveToRelative(-0.01f, 3.04f, -0.02f, 4.55f, -0.03f, 7.58f)
                close()
            }
            path(fill = SolidColor(Color(0xFF7D5133)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(46.47f, -66.8f)
                curveToRelative(2.31f, 1.19f, 3.56f, 1.82f, 5.59f, 2.86f)
                curveToRelative(-1.02f, -1.84f, -1.57f, -2.79f, -2.7f, -4.69f)
                curveToRelative(-1.12f, 0.75f, -1.72f, 1.11f, -2.89f, 1.83f)
                close()
                moveTo(40.47f, -69.88f)
                curveToRelative(1.73f, 0.9f, 2.89f, 1.49f, 5.19f, 2.67f)
                curveToRelative(-1.14f, -1.91f, -1.7f, -2.86f, -2.75f, -4.72f)
                curveToRelative(-1.07f, 0.77f, -1.57f, 1.17f, -2.44f, 2.05f)
                close()
                moveTo(59.01f, -52.15f)
                curveToRelative(0.82f, 0.42f, 1.23f, 0.64f, 2.05f, 1.07f)
                lineToRelative(-0.15f, -9.0f)
                curveToRelative(-0.78f, -0.41f, -1.18f, -0.62f, -1.95f, -1.02f)
                curveToRelative(0.01f, 3.58f, 0.02f, 5.37f, 0.05f, 8.95f)
                close()
            }
            path(fill = SolidColor(Color(0xFF7D5133)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(56.03f, -52.78f)
                curveToRelative(2.97f, 1.57f, 5.29f, 2.74f, 8.14f, 4.27f)
                curveToRelative(-0.02f, -1.03f, -0.81f, -2.27f, -1.89f, -2.84f)
                curveToRelative(-1.68f, -0.89f, -2.64f, -1.38f, -4.35f, -2.27f)
                curveToRelative(-1.09f, -0.58f, -1.9f, -0.17f, -1.9f, 0.84f)
                close()
                moveTo(61.69f, -62.95f)
                curveToRelative(0.5f, 1.06f, 0.76f, 1.59f, 1.25f, 2.65f)
                curveToRelative(-0.4f, 0.83f, -0.6f, 1.25f, -1.01f, 2.08f)
                curveToRelative(0.36f, -0.03f, 0.53f, -0.04f, 0.88f, -0.07f)
                curveToRelative(0.47f, -0.63f, 0.7f, -0.94f, 1.15f, -1.57f)
                curveToRelative(-0.41f, -0.97f, -0.63f, -1.45f, -1.05f, -2.43f)
                curveToRelative(1.2f, 0.65f, 1.78f, 0.99f, 2.87f, 1.63f)
                curveToRelative(0.0f, 3.51f, -2.29f, 5.04f, -5.73f, 3.25f)
                curveToRelative(-3.44f, -1.79f, -5.72f, -5.8f, -5.72f, -9.3f)
                curveToRelative(2.56f, 1.28f, 4.28f, 2.11f, 7.36f, 3.76f)
                close()
            }
        }
        .build()
        return _package!!
    }

private var _package: ImageVector? = null
