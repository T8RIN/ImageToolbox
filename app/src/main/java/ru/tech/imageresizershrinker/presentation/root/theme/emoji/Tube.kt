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

val Emoji.Tube: ImageVector
    get() {
        if (_tube != null) {
            return _tube!!
        }
        _tube = Builder(
            name = "Tube", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF00EDCD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(109.77f, 11.02f)
                moveToRelative(-4.66f, 0.0f)
                arcToRelative(
                    4.66f, 4.66f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 9.32f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    4.66f, 4.66f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -9.32f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, fillAlpha = 0.75f, strokeAlpha
                = 0.75f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(11.43f, 91.09f)
                lineToRelative(72.66f, -72.66f)
                curveToRelative(0.11f, -0.11f, 0.21f, -0.28f, 0.31f, -0.45f)
                curveToRelative(0.77f, -1.32f, 0.85f, -2.58f, 0.73f, -4.25f)
                curveToRelative(-0.21f, -2.83f, 0.46f, -4.36f, 1.83f, -5.38f)
                curveToRelative(1.81f, -1.36f, 8.08f, -1.08f, 19.81f, 10.65f)
                reflectiveCurveToRelative(12.0f, 17.99f, 10.64f, 19.8f)
                curveToRelative(-1.02f, 1.37f, -2.55f, 2.03f, -5.38f, 1.83f)
                curveToRelative(-1.67f, -0.12f, -2.93f, -0.05f, -4.25f, 0.73f)
                curveToRelative(-0.18f, 0.1f, -0.34f, 0.2f, -0.45f, 0.31f)
                lineToRelative(-72.66f, 72.66f)
                curveToRelative(-9.98f, 9.98f, -19.51f, 8.46f, -25.56f, 2.41f)
                lineToRelative(-0.04f, -0.04f)
                lineToRelative(-0.04f, -0.04f)
                curveToRelative(-6.07f, -6.06f, -7.59f, -15.59f, 2.4f, -25.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1D44B3)), stroke = null, fillAlpha = 0.39f, strokeAlpha
                = 0.39f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(99.79f, 23.22f)
                curveToRelative(-6.36f, -6.36f, -11.2f, -9.55f, -14.69f, -10.98f)
                curveToRelative(-0.02f, 0.45f, -0.02f, 0.95f, 0.02f, 1.49f)
                curveToRelative(0.09f, 1.19f, 0.06f, 2.17f, -0.23f, 3.11f)
                curveToRelative(3.17f, 1.71f, 7.14f, 4.55f, 12.01f, 9.11f)
                curveToRelative(7.0f, 6.56f, 10.34f, 11.51f, 11.73f, 15.0f)
                curveToRelative(0.92f, -0.34f, 1.86f, -0.4f, 2.98f, -0.35f)
                curveToRelative(-0.67f, -3.5f, -3.62f, -9.19f, -11.82f, -17.38f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(54.15f, 48.51f)
                lineTo(11.67f, 90.94f)
                curveToRelative(-10.36f, 10.35f, -9.05f, 20.03f, -3.03f, 26.05f)
                curveToRelative(6.02f, 6.01f, 15.68f, 7.35f, 26.06f, -3.03f)
                lineTo(102.0f, 47.04f)
                lineToRelative(-47.85f, 1.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00937A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(45.06f, 76.96f)
                moveToRelative(-5.06f, 0.0f)
                arcToRelative(
                    5.06f, 5.06f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 10.12f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    5.06f, 5.06f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -10.12f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(96.58f, 37.04f)
                moveToRelative(-4.76f, 0.0f)
                arcToRelative(
                    4.76f, 4.76f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 9.52f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    4.76f, 4.76f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -9.52f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.69f, strokeAlpha
                = 0.69f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(73.27f, 57.3f)
                moveToRelative(-2.93f, 0.0f)
                arcToRelative(
                    2.93f, 2.93f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 5.86f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    2.93f, 2.93f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -5.86f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(121.84f, 12.01f)
                moveToRelative(-2.17f, 0.0f)
                arcToRelative(
                    2.17f, 2.17f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 4.34f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    2.17f, 2.17f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -4.34f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFF00EDCD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(59.15f, 72.54f)
                moveToRelative(-4.0f, 0.0f)
                arcToRelative(
                    4.0f, 4.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 8.0f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    4.0f, 4.0f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -8.0f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0xFF00EDCD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.75f, 61.12f)
                moveToRelative(-1.54f, 0.0f)
                arcToRelative(
                    1.54f, 1.54f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = 3.08f,
                    dy1 = 0.0f
                )
                arcToRelative(
                    1.54f, 1.54f, 0.0f,
                    isMoreThanHalf = true,
                    isPositiveArc = true,
                    dx1 = -3.08f,
                    dy1 = 0.0f
                )
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFFFFFFFF)),
                fillAlpha = 0.6f, strokeAlpha = 0.6f, strokeLineWidth = 4.195f, strokeLineCap =
                Round, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(93.2f, 14.66f)
                curveToRelative(3.21f, 2.08f, 6.13f, 4.6f, 8.65f, 7.46f)
            }
            path(
                fill = SolidColor(Color(0xFF00EDCD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(102.02f, 46.96f)
                curveToRelative(-0.84f, 0.74f, -3.57f, 1.23f, -10.75f, 2.35f)
                curveToRelative(-3.81f, 0.6f, -8.47f, 1.2f, -13.82f, 1.64f)
                curveToRelative(-16.77f, 1.39f, -28.08f, -0.9f, -21.37f, -3.64f)
                curveToRelative(5.39f, -2.2f, 24.53f, -1.72f, 36.51f, -1.34f)
                curveToRelative(5.78f, 0.19f, 10.09f, 0.42f, 9.43f, 0.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.6f, strokeAlpha
                = 0.6f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(11.63f, 103.5f)
                curveToRelative(-1.72f, -0.71f, -1.02f, -2.68f, 0.0f, -3.71f)
                lineToRelative(67.5f, -69.29f)
                curveToRelative(4.1f, -4.24f, 7.85f, -7.12f, 9.95f, -5.12f)
                curveToRelative(2.35f, 2.23f, 1.6f, 6.23f, -7.61f, 14.49f)
                lineToRelative(-64.68f, 61.17f)
                curveToRelative(-0.51f, 0.51f, -3.44f, 3.17f, -5.16f, 2.46f)
                close()
            }
        }
            .build()
        return _tube!!
    }

private var _tube: ImageVector? = null
