package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
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

public val Emoji.Star: ImageVector
    get() {
        if (_star != null) {
            return _star!!
        }
        _star = Builder(name = "Star", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
                = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = linearGradient(0.0f to Color(0xFF3367D6), 0.521f to Color(0xFF352893), 1.0f
                    to Color(0xFF1A237E), start = Offset(64.0f,155.558f), end =
                    Offset(64.0f,-0.244f)), stroke = null, strokeLineWidth = 0.0f, strokeLineCap =
                    Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(116.0f, 124.0f)
                horizontalLineTo(12.0f)
                curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
                verticalLineTo(12.0f)
                curveToRelative(0.0f, -4.42f, 3.58f, -8.0f, 8.0f, -8.0f)
                horizontalLineToRelative(104.0f)
                curveToRelative(4.42f, 0.0f, 8.0f, 3.58f, 8.0f, 8.0f)
                verticalLineToRelative(104.0f)
                curveToRelative(0.0f, 4.42f, -3.58f, 8.0f, -8.0f, 8.0f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFFFFF7AF), 1.0f to Color(0x19FFF7AF), start =
                    Offset(46.624f,85.54f), end = Offset(124.494f,-3.705f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(34.15f, 69.14f)
                lineToRelative(40.47f, 22.92f)
                curveTo(87.88f, 57.62f, 107.55f, 34.57f, 124.0f, 20.08f)
                verticalLineTo(12.0f)
                curveToRelative(0.0f, -3.0f, -1.65f, -5.61f, -4.1f, -6.98f)
                curveToRelative(-29.9f, 7.41f, -70.83f, 24.6f, -85.75f, 64.12f)
                close()
            }
            path(fill = linearGradient(0.254f to Color(0xFFFFFDE7), 1.0f to Color(0x00FFFDE7), start
                    = Offset(47.136f,81.18f), end = Offset(120.894f,13.38f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(114.43f, 12.88f)
                curveTo(62.41f, 30.29f, 41.0f, 73.56f, 41.0f, 73.56f)
                lineTo(61.23f, 90.2f)
                reflectiveCurveToRelative(4.5f, -20.25f, 18.05f, -41.77f)
                curveToRelative(10.98f, -17.43f, 28.3f, -28.56f, 35.99f, -33.86f)
                curveToRelative(0.91f, -0.62f, 0.21f, -2.04f, -0.84f, -1.69f)
                close()
            }
            path(fill = linearGradient(0.005f to Color(0x004453AB), 1.0f to Color(0xFF3949AB), start
                    = Offset(85.658f,12.325f), end = Offset(68.685f,95.571f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(75.24f, 67.39f)
                curveToRelative(0.3f, -5.04f, 3.11f, -17.44f, 3.11f, -17.44f)
                curveToRelative(0.73f, -4.1f, -3.0f, -5.1f, -5.53f, -3.82f)
                curveToRelative(-7.19f, 3.64f, -14.83f, 8.76f, -19.53f, 9.16f)
                curveToRelative(-5.6f, 0.48f, -6.26f, -5.54f, -6.18f, -8.76f)
                curveToRelative(-5.28f, 6.58f, -9.73f, 14.07f, -12.96f, 22.61f)
                lineToRelative(40.47f, 22.92f)
                curveToRelative(12.84f, -33.36f, 31.71f, -56.03f, 47.83f, -70.59f)
                lineToRelative(1.55f, -1.38f)
                verticalLineToRelative(-2.36f)
                curveToRelative(-23.11f, 16.34f, -34.65f, 35.0f, -41.37f, 44.77f)
                curveToRelative(-4.25f, 6.17f, -7.63f, 8.9f, -7.39f, 4.89f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFDD835)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(29.58f, 49.88f)
                lineTo(46.36f, 60.7f)
                curveToRelative(1.29f, 0.83f, 2.92f, 0.89f, 4.27f, 0.17f)
                lineToRelative(17.25f, -9.32f)
                curveToRelative(2.02f, -0.95f, 4.25f, 0.82f, 3.78f, 3.0f)
                lineTo(66.2f, 72.98f)
                curveToRelative(-0.44f, 1.49f, -0.02f, 3.1f, 1.09f, 4.19f)
                lineTo(81.9f, 91.5f)
                curveToRelative(1.53f, 1.63f, 0.53f, 4.3f, -1.69f, 4.52f)
                lineToRelative(-19.72f, 1.09f)
                curveToRelative(-1.51f, 0.08f, -2.87f, 0.98f, -3.54f, 2.34f)
                lineToRelative(-8.71f, 17.73f)
                curveToRelative(-1.07f, 1.96f, -3.92f, 1.83f, -4.82f, -0.21f)
                lineToRelative(-7.48f, -19.05f)
                arcToRelative(4.207f, 4.207f, 0.0f, false, false, -3.43f, -2.64f)
                lineTo(13.42f, 93.1f)
                curveToRelative(-2.19f, -0.42f, -2.95f, -3.16f, -1.29f, -4.65f)
                lineToRelative(15.32f, -12.24f)
                curveToRelative(1.2f, -0.96f, 1.78f, -2.49f, 1.52f, -4.0f)
                lineToRelative(-3.41f, -19.67f)
                curveToRelative(-0.29f, -2.21f, 2.09f, -3.78f, 4.02f, -2.66f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFF8D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(39.79f, 66.36f)
                lineToRelative(-8.57f, -10.49f)
                curveToRelative(-0.46f, -0.6f, -1.3f, -1.58f, -0.29f, -2.25f)
                curveToRelative(0.8f, -0.53f, 2.32f, 0.84f, 2.32f, 0.84f)
                lineToRelative(9.36f, 6.77f)
                curveToRelative(3.56f, 2.58f, 3.8f, 4.11f, 3.03f, 5.62f)
                curveToRelative(-0.9f, 1.75f, -3.4f, 2.51f, -5.85f, -0.49f)
                close()
            }
            path(fill = SolidColor(Color(0xFFF4B400)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(64.23f, 72.85f)
                lineToRelative(4.71f, -14.05f)
                curveToRelative(0.22f, -0.72f, 0.66f, -1.94f, -0.52f, -2.21f)
                curveToRelative(-0.93f, -0.21f, -1.88f, 1.6f, -1.88f, 1.6f)
                lineToRelative(-6.32f, 8.98f)
                curveToRelative(-1.96f, 2.56f, -2.79f, 4.98f, -1.85f, 6.72f)
                curveToRelative(1.26f, 2.31f, 4.33f, 2.7f, 5.86f, -1.04f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFF176)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(118.09f, 63.75f)
                curveToRelative(-3.13f, -1.04f, -5.56f, -3.6f, -6.45f, -6.78f)
                arcToRelative(0.44f, 0.44f, 0.0f, false, false, -0.85f, -0.02f)
                arcToRelative(9.97f, 9.97f, 0.0f, false, true, -6.76f, 6.49f)
                curveToRelative(-0.41f, 0.12f, -0.42f, 0.68f, -0.02f, 0.81f)
                arcToRelative(9.953f, 9.953f, 0.0f, false, true, 6.45f, 6.76f)
                curveToRelative(0.12f, 0.44f, 0.71f, 0.45f, 0.85f, 0.02f)
                arcToRelative(9.947f, 9.947f, 0.0f, false, true, 6.75f, -6.47f)
                arcToRelative(0.42f, 0.42f, 0.0f, false, false, 0.03f, -0.81f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFF9C4)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(42.71f, 30.83f)
                arcToRelative(7.16f, 7.16f, 0.0f, false, true, -4.64f, -4.88f)
                arcToRelative(0.316f, 0.316f, 0.0f, false, false, -0.61f, -0.01f)
                arcToRelative(7.17f, 7.17f, 0.0f, false, true, -4.87f, 4.67f)
                curveToRelative(-0.29f, 0.08f, -0.3f, 0.49f, -0.01f, 0.59f)
                arcToRelative(7.2f, 7.2f, 0.0f, false, true, 4.65f, 4.87f)
                curveToRelative(0.09f, 0.32f, 0.51f, 0.33f, 0.61f, 0.01f)
                arcToRelative(7.16f, 7.16f, 0.0f, false, true, 4.86f, -4.66f)
                curveToRelative(0.29f, -0.09f, 0.3f, -0.5f, 0.01f, -0.59f)
                close()
            }
            path(fill = SolidColor(Color(0xFF7BAAF7)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(12.36f, 41.85f)
                moveToRelative(-2.41f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, 4.82f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, -4.82f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFF5C6BC0)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(59.23f, 15.07f)
                moveToRelative(-4.77f, 0.0f)
                arcToRelative(4.77f, 4.77f, 0.0f, true, true, 9.54f, 0.0f)
                arcToRelative(4.77f, 4.77f, 0.0f, true, true, -9.54f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFF7986CB)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(109.42f, 94.45f)
                moveToRelative(-3.19f, 0.0f)
                arcToRelative(3.19f, 3.19f, 0.0f, true, true, 6.38f, 0.0f)
                arcToRelative(3.19f, 3.19f, 0.0f, true, true, -6.38f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFFFFF176)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(98.9f, 80.52f)
                moveToRelative(-2.41f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, 4.82f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, -4.82f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFFFFF176)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(19.67f, 17.48f)
                moveToRelative(-2.41f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, 4.82f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, -4.82f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFF7BAAF7)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = EvenOdd) {
                moveTo(69.65f, 113.47f)
                moveToRelative(-2.41f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, 4.82f, 0.0f)
                arcToRelative(2.41f, 2.41f, 0.0f, true, true, -4.82f, 0.0f)
            }
        }
        .build()
        return _star!!
    }

private var _star: ImageVector? = null
