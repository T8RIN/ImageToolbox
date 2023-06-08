package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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

val Emoji.Couch: ImageVector
    get() {
        if (_couch != null) {
            return _couch!!
        }
        _couch = Builder(
            name = "Couch", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFD600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(110.93f, 8.32f)
                curveToRelative(0.0f, -2.36f, -5.89f, -4.28f, -13.16f, -4.28f)
                curveToRelative(-7.27f, 0.0f, -13.17f, 1.91f, -13.17f, 4.28f)
                curveToRelative(0.0f, 2.01f, -3.42f, 18.4f, -4.33f, 23.28f)
                curveToRelative(-0.33f, 1.77f, 0.21f, 2.49f, 1.09f, 3.0f)
                curveToRelative(2.68f, 1.54f, 9.02f, 2.63f, 16.4f, 2.63f)
                curveToRelative(7.74f, 0.0f, 14.33f, -1.19f, 16.76f, -2.85f)
                curveToRelative(0.65f, -0.45f, 0.95f, -1.56f, 0.6f, -3.55f)
                curveToRelative(-1.03f, -5.87f, -4.19f, -21.74f, -4.19f, -22.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFF8D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(82.86f, 31.79f)
                arcToRelative(14.91f, 3.59f, 0.0f, true, false, 29.82f, 0.0f)
                arcToRelative(14.91f, 3.59f, 0.0f, true, false, -29.82f, 0.0f)
                close()
            }
            path(
                fill = linearGradient(
                    0.041f to Color(0xFFFFFF8D), 0.567f to Color(0xFFC87D5E),
                    0.676f to Color(0xFFBA7151), 0.799f to Color(0xFFA65F3E), start =
                    Offset(97.768f, 14.655f), end = Offset(97.768f, 73.951f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(95.78f, 28.2f)
                horizontalLineToRelative(3.98f)
                verticalLineToRelative(50.42f)
                horizontalLineToRelative(-3.98f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC87D5E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(112.27f, 117.0f)
                lineToRelative(-0.77f, 5.95f)
                curveToRelative(-0.02f, 0.51f, -0.57f, 1.01f, -2.2f, 1.01f)
                curveToRelative(-1.45f, 0.0f, -1.8f, -0.62f, -1.84f, -1.03f)
                lineToRelative(-0.61f, -5.92f)
                horizontalLineToRelative(5.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA65F3E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.0f, 118.53f)
                curveToRelative(0.17f, 0.8f, 1.26f, 1.56f, 2.61f, 1.56f)
                reflectiveCurveToRelative(2.44f, -0.69f, 2.44f, -1.55f)
                lineToRelative(0.19f, -1.55f)
                curveToRelative(-0.15f, 0.0f, -5.25f, 0.01f, -5.4f, 0.03f)
                lineToRelative(0.16f, 1.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC87D5E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(15.73f, 117.0f)
                lineToRelative(0.77f, 5.95f)
                curveToRelative(0.02f, 0.51f, 0.57f, 1.01f, 2.2f, 1.01f)
                curveToRelative(1.45f, 0.0f, 1.8f, -0.62f, 1.84f, -1.03f)
                lineToRelative(0.61f, -5.92f)
                horizontalLineToRelative(-5.42f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA65F3E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.0f, 118.53f)
                curveToRelative(-0.17f, 0.8f, -1.26f, 1.56f, -2.61f, 1.56f)
                curveToRelative(-1.35f, 0.0f, -2.44f, -0.69f, -2.44f, -1.55f)
                lineToRelative(-0.19f, -1.54f)
                curveToRelative(0.15f, 0.0f, 5.25f, 0.01f, 5.4f, 0.03f)
                lineToRelative(-0.16f, 1.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0B52A0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(115.07f, 82.41f)
                lineToRelative(-0.4f, 15.8f)
                lineToRelative(-9.27f, -6.89f)
                lineToRelative(1.83f, -11.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF48CDED)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(113.04f, 82.31f)
                curveToRelative(1.04f, 0.54f, 1.9f, 0.48f, 2.52f, -0.52f)
                curveToRelative(1.54f, -2.44f, 3.6f, -2.32f, 5.07f, -2.21f)
                curveToRelative(1.4f, 0.1f, 2.33f, 0.74f, 3.2f, 2.51f)
                curveToRelative(-0.24f, -1.02f, -0.48f, -2.74f, -3.59f, -3.38f)
                curveToRelative(-3.54f, -0.74f, -12.78f, -0.82f, -12.78f, -0.82f)
                curveToRelative(-0.18f, -0.09f, -0.23f, 2.22f, -0.23f, 2.22f)
                lineToRelative(5.81f, 2.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0B52A0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(13.05f, 82.41f)
                lineToRelative(0.4f, 15.8f)
                lineToRelative(9.27f, -6.89f)
                lineToRelative(-1.83f, -11.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF48CDED)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.08f, 82.31f)
                curveToRelative(-1.04f, 0.54f, -1.9f, 0.48f, -2.52f, -0.52f)
                curveToRelative(-1.54f, -2.44f, -3.6f, -2.32f, -5.07f, -2.21f)
                curveToRelative(-1.4f, 0.1f, -2.33f, 0.74f, -3.2f, 2.51f)
                curveToRelative(0.24f, -1.02f, 0.48f, -2.74f, 3.59f, -3.38f)
                curveToRelative(3.54f, -0.74f, 12.78f, -0.82f, 12.78f, -0.82f)
                curveToRelative(0.18f, -0.09f, 0.23f, 2.22f, 0.23f, 2.22f)
                lineToRelative(-5.81f, 2.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF3DAAE0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.28f, 65.77f)
                curveToRelative(-0.04f, -0.28f, -0.09f, -0.56f, -0.14f, -0.82f)
                curveToRelative(-0.37f, -1.87f, -1.73f, -3.29f, -3.42f, -3.55f)
                curveToRelative(-3.43f, -0.53f, -10.4f, -1.43f, -18.7f, -1.43f)
                curveToRelative(-7.37f, 0.0f, -13.69f, 0.71f, -17.41f, 1.24f)
                curveToRelative(-2.04f, 0.29f, -3.56f, 2.25f, -3.56f, 4.56f)
                curveToRelative(0.0f, -2.32f, -1.52f, -4.27f, -3.56f, -4.56f)
                arcToRelative(126.07f, 126.07f, 0.0f, false, false, -17.41f, -1.24f)
                curveToRelative(-8.3f, 0.0f, -15.28f, 0.9f, -18.7f, 1.43f)
                curveToRelative(-1.69f, 0.26f, -3.03f, 1.68f, -3.42f, 3.55f)
                curveToRelative(-1.99f, 9.52f, 1.15f, 26.56f, 1.15f, 26.56f)
                horizontalLineToRelative(83.9f)
                curveToRelative(-0.02f, 0.0f, 2.77f, -16.3f, 1.27f, -25.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF188AD6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(65.09f, 91.51f)
                curveToRelative(-0.55f, 1.3f, -1.8f, 1.31f, -2.11f, 0.0f)
                curveToRelative(-0.31f, -1.31f, 0.0f, -18.09f, 0.0f, -20.65f)
                curveToRelative(0.0f, -2.56f, 1.06f, -5.33f, 1.06f, -5.33f)
                reflectiveCurveToRelative(1.06f, 3.29f, 1.06f, 5.33f)
                reflectiveCurveToRelative(0.54f, 19.35f, -0.01f, 20.65f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF48CDED)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.42f, 97.58f)
                curveToRelative(0.16f, -0.01f, -5.43f, -6.07f, -8.98f, -6.68f)
                curveToRelative(0.0f, 0.0f, -6.46f, -1.21f, -17.75f, -1.21f)
                reflectiveCurveToRelative(-23.66f, 1.82f, -23.66f, 1.82f)
                lineToRelative(-0.03f, 0.46f)
                lineToRelative(-0.03f, -0.47f)
                reflectiveCurveToRelative(-12.38f, -1.82f, -23.66f, -1.82f)
                curveToRelative(-11.29f, 0.0f, -17.75f, 1.21f, -17.75f, 1.21f)
                curveToRelative(-3.55f, 0.61f, -9.14f, 6.67f, -8.98f, 6.68f)
                lineToRelative(45.18f, -0.12f)
                lineToRelative(5.24f, 3.56f)
                lineToRelative(4.22f, -3.56f)
                lineToRelative(46.2f, 0.13f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF188AD6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(120.21f, 79.37f)
                curveToRelative(-3.29f, -0.28f, -5.37f, 1.37f, -5.68f, 4.86f)
                curveToRelative(-0.32f, 3.55f, -0.26f, 9.23f, -0.16f, 13.32f)
                curveToRelative(-0.41f, -0.5f, -0.97f, -0.84f, -1.61f, -0.93f)
                curveToRelative(-3.89f, -0.53f, -12.36f, -1.47f, -23.21f, -1.47f)
                reflectiveCurveToRelative(-19.37f, 0.61f, -23.21f, 1.47f)
                curveToRelative(-0.88f, 0.2f, -1.63f, 1.18f, -2.03f, 2.26f)
                curveToRelative(-0.1f, 0.28f, -0.51f, 0.29f, -0.61f, 0.0f)
                curveToRelative(-0.4f, -1.08f, -1.15f, -2.06f, -2.03f, -2.27f)
                curveToRelative(-3.77f, -0.86f, -12.36f, -1.47f, -23.21f, -1.47f)
                curveToRelative(-10.85f, 0.0f, -19.32f, 0.94f, -23.21f, 1.47f)
                curveToRelative(-0.64f, 0.09f, -1.2f, 0.43f, -1.61f, 0.93f)
                curveToRelative(0.1f, -4.09f, 0.15f, -9.76f, -0.16f, -13.32f)
                curveToRelative(-0.31f, -3.49f, -2.01f, -5.09f, -5.68f, -4.86f)
                curveToRelative(-2.13f, 0.13f, -3.91f, 1.76f, -3.79f, 4.16f)
                lineToRelative(0.86f, 17.64f)
                curveToRelative(0.0f, 11.53f, 6.71f, 16.86f, 14.95f, 16.86f)
                horizontalLineToRelative(88.36f)
                curveToRelative(8.24f, 0.0f, 14.95f, -5.33f, 14.95f, -16.86f)
                lineToRelative(0.86f, -17.64f)
                curveToRelative(0.13f, -2.39f, -1.66f, -3.97f, -3.78f, -4.15f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0B52A0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(112.67f, 108.75f)
                curveToRelative(-3.82f, 0.95f, -11.68f, 0.95f, -23.13f, 0.95f)
                curveToRelative(-11.44f, 0.0f, -19.39f, -0.6f, -23.13f, -0.95f)
                curveToRelative(-0.84f, -0.08f, -1.57f, -0.91f, -2.0f, -1.9f)
                arcToRelative(0.46f, 0.46f, 0.0f, false, false, -0.85f, 0.0f)
                curveToRelative(-0.43f, 0.99f, -1.15f, 1.82f, -2.0f, 1.9f)
                curveToRelative(-3.73f, 0.36f, -11.68f, 0.95f, -23.13f, 0.95f)
                curveToRelative(-11.44f, 0.0f, -19.64f, 0.0f, -23.13f, -0.95f)
                curveToRelative(-0.73f, -0.2f, -1.38f, -0.62f, -1.82f, -1.21f)
                curveToRelative(0.62f, 2.66f, 2.9f, 4.68f, 5.72f, 4.84f)
                curveToRelative(7.47f, 0.44f, 82.08f, 0.44f, 89.56f, 0.0f)
                curveToRelative(2.81f, -0.16f, 5.08f, -2.18f, 5.71f, -4.82f)
                curveToRelative(-0.42f, 0.59f, -1.07f, 1.01f, -1.8f, 1.19f)
                close()
            }
        }
            .build()
        return _couch!!
    }

private var _couch: ImageVector? = null
