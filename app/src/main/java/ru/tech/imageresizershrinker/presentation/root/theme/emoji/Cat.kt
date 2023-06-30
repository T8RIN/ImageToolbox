package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Cat: ImageVector
    get() {
        if (_cat != null) {
            return _cat!!
        }
        _cat = Builder(
            name = "Cat", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth =
            128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFC022)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(110.47f, 59.02f)
                curveToRelative(9.51f, -24.83f, 3.65f, -43.83f, 0.29f, -49.28f)
                curveToRelative(-1.33f, -2.16f, -3.89f, -2.76f, -6.25f, -2.02f)
                curveTo(98.29f, 9.68f, 81.5f, 23.4f, 74.08f, 42.6f)
                moveTo(17.53f, 59.02f)
                curveToRelative(-9.51f, -24.83f, -3.65f, -43.83f, -0.29f, -49.28f)
                curveToRelative(1.33f, -2.16f, 3.89f, -2.76f, 6.25f, -2.02f)
                curveTo(29.71f, 9.68f, 46.5f, 23.4f, 53.92f, 42.6f)
            }
            path(
                fill = SolidColor(Color(0xFFFFC022)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.11f, 70.76f)
                curveTo(112.31f, 44.78f, 94.44f, 26.3f, 64.0f, 26.3f)
                reflectiveCurveTo(15.69f, 44.78f, 13.89f, 70.76f)
                curveToRelative(-1.05f, 15.14f, 5.05f, 28.01f, 17.09f, 36.21f)
                curveToRelative(0.0f, 0.0f, 12.21f, 9.88f, 33.02f, 10.14f)
                curveToRelative(20.81f, -0.26f, 33.02f, -10.14f, 33.02f, -10.14f)
                curveToRelative(12.03f, -8.2f, 18.14f, -21.07f, 17.09f, -36.21f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF7043)), stroke = null, fillAlpha = 0.47f, strokeAlpha
                = 0.47f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(54.12f, 45.02f)
                curveToRelative(1.13f, 0.96f, 3.42f, 0.82f, 4.75f, -0.72f)
                curveToRelative(1.61f, -1.87f, 3.29f, -8.17f, 2.24f, -17.91f)
                curveToRelative(-4.67f, 0.17f, -9.09f, 0.84f, -13.21f, 1.97f)
                curveToRelative(3.33f, 5.46f, 4.13f, 14.88f, 6.22f, 16.66f)
                close()
                moveTo(73.88f, 45.02f)
                curveToRelative(-1.13f, 0.96f, -3.42f, 0.82f, -4.75f, -0.72f)
                curveToRelative(-1.61f, -1.87f, -3.29f, -8.17f, -2.24f, -17.91f)
                curveToRelative(4.67f, 0.17f, 9.09f, 0.84f, 13.21f, 1.97f)
                curveToRelative(-3.33f, 5.46f, -4.13f, 14.88f, -6.22f, 16.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(55.96f, 76.97f)
                curveToRelative(-0.05f, -2.86f, 4.06f, -4.24f, 7.95f, -4.3f)
                reflectiveCurveToRelative(8.07f, 1.2f, 8.12f, 4.06f)
                curveToRelative(0.05f, 2.86f, -4.86f, 6.64f, -7.95f, 6.64f)
                curveToRelative(-3.08f, 0.0f, -8.07f, -3.54f, -8.12f, -6.4f)
                close()
                moveTo(66.47f, 88.31f)
                curveToRelative(2.44f, 2.37f, 6.18f, 3.1f, 11.86f, 1.16f)
                curveToRelative(3.4f, -1.16f, 6.21f, -4.1f, 8.06f, -6.56f)
                curveToRelative(0.69f, -0.91f, 2.16f, -0.29f, 1.99f, 0.84f)
                curveToRelative(-0.31f, 2.02f, -0.79f, 4.26f, -1.54f, 5.72f)
                curveToRelative(-1.97f, 3.83f, -6.05f, 7.38f, -15.27f, 6.44f)
                curveToRelative(-2.53f, -0.26f, -5.08f, -0.25f, -7.6f, 0.11f)
                curveToRelative(-9.06f, 1.31f, -13.74f, 0.94f, -16.31f, 0.07f)
                curveToRelative(-1.16f, -0.4f, -0.92f, -2.09f, 0.3f, -2.14f)
                curveToRelative(2.57f, -0.1f, 6.22f, -0.38f, 9.31f, -1.25f)
                curveToRelative(2.84f, -0.8f, 5.36f, -1.86f, 6.05f, -3.37f)
                reflectiveCurveToRelative(2.58f, -1.58f, 3.15f, -1.02f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF000000)),
                strokeLineWidth = 4.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(65.35f, 89.47f)
                curveToRelative(-0.77f, -2.07f, -1.19f, -4.28f, -1.23f, -6.49f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFF0592B)),
                strokeLineWidth = 3.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(60.35f, 101.17f)
                curveToRelative(3.73f, 2.19f, 8.08f, 3.28f, 12.4f, 3.12f)
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF9E9E9E)),
                strokeLineWidth = 3.0f, strokeLineCap = Round, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(2.4f, 70.42f)
                reflectiveCurveToRelative(9.31f, -1.69f, 20.03f, 3.95f)
                moveTo(2.31f, 83.15f)
                reflectiveCurveToRelative(8.21f, -3.72f, 19.24f, -1.76f)
                moveTo(5.5f, 94.37f)
                reflectiveCurveToRelative(7.82f, -5.18f, 17.75f, -6.06f)
                moveToRelative(101.92f, -19.97f)
                reflectiveCurveToRelative(-9.36f, -1.38f, -19.88f, 4.62f)
                moveToRelative(20.4f, 8.1f)
                reflectiveCurveToRelative(-8.33f, -3.44f, -19.29f, -1.11f)
                moveToRelative(16.48f, 12.44f)
                reflectiveCurveToRelative(-7.99f, -4.92f, -17.94f, -5.45f)
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(33.85f, 63.78f)
                curveToRelative(0.0f, -2.98f, 0.95f, -7.41f, 2.42f, -7.85f)
                curveToRelative(0.45f, -0.13f, 0.99f, -0.06f, 1.36f, 0.24f)
                curveToRelative(5.16f, 4.18f, 8.79f, 6.2f, 9.21f, 6.52f)
                curveToRelative(3.53f, 2.65f, -1.65f, 9.96f, -5.72f, 10.04f)
                curveToRelative(-3.93f, 0.06f, -7.27f, -3.94f, -7.27f, -8.95f)
                close()
                moveTo(94.14f, 63.78f)
                curveToRelative(0.0f, -3.27f, -0.52f, -6.7f, -1.99f, -7.14f)
                curveToRelative(-0.45f, -0.13f, -0.99f, -0.06f, -1.36f, 0.24f)
                curveToRelative(-5.16f, 4.18f, -9.22f, 5.49f, -9.64f, 5.81f)
                curveToRelative(-3.53f, 2.65f, 1.65f, 9.96f, 5.72f, 10.04f)
                curveToRelative(3.93f, 0.06f, 7.27f, -3.94f, 7.27f, -8.95f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD1D1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.16f, 36.23f)
                curveToRelative(-1.54f, -1.29f, -1.5f, -3.37f, -0.6f, -5.16f)
                curveToRelative(2.16f, -4.31f, 7.33f, -8.78f, 9.16f, -10.23f)
                curveToRelative(3.0f, -2.38f, 5.32f, -3.18f, 6.21f, 0.65f)
                curveToRelative(1.65f, 7.08f, 1.52f, 16.69f, -0.25f, 21.99f)
                curveToRelative(-0.62f, 1.87f, -2.54f, 2.86f, -4.02f, 1.57f)
                lineToRelative(-10.5f, -8.82f)
                close()
                moveTo(35.84f, 36.23f)
                curveToRelative(1.54f, -1.29f, 1.5f, -3.37f, 0.6f, -5.16f)
                curveToRelative(-2.16f, -4.31f, -7.33f, -8.78f, -9.16f, -10.23f)
                curveToRelative(-3.0f, -2.38f, -5.32f, -3.18f, -6.21f, 0.65f)
                curveToRelative(-1.65f, 7.08f, -1.52f, 16.69f, 0.25f, 21.99f)
                curveToRelative(0.62f, 1.87f, 2.54f, 2.86f, 4.02f, 1.57f)
                lineToRelative(10.5f, -8.82f)
                close()
            }
        }
            .build()
        return _cat!!
    }

private var _cat: ImageVector? = null
