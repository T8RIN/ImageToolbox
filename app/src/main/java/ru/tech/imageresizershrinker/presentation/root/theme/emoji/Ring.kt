package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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

val Emoji.Ring: ImageVector
    get() {
        if (_ring != null) {
            return _ring!!
        }
        _ring = Builder(
            name = "Ring", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF4B8A99)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(95.27f, 61.54f)
                curveToRelative(2.16f, 4.51f, 3.37f, 9.57f, 3.37f, 14.9f)
                curveToRelative(0.0f, 19.13f, -15.5f, 34.63f, -34.63f, 34.63f)
                reflectiveCurveToRelative(-34.63f, -15.5f, -34.63f, -34.63f)
                curveToRelative(0.0f, -5.34f, 1.21f, -10.39f, 3.37f, -14.9f)
                arcToRelative(38.026f, 38.026f, 0.0f, false, false, -6.96f, 21.98f)
                curveToRelative(0.0f, 21.11f, 17.11f, 38.22f, 38.22f, 38.22f)
                reflectiveCurveToRelative(38.22f, -17.11f, 38.22f, -38.22f)
                arcToRelative(38.195f, 38.195f, 0.0f, false, false, -6.96f, -21.98f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(32.44f, 47.8f)
                curveTo(15.0f, 65.23f, 15.0f, 93.49f, 32.44f, 110.93f)
                curveToRelative(17.43f, 17.43f, 45.69f, 17.43f, 63.13f, 0.0f)
                reflectiveCurveToRelative(17.43f, -45.69f, 0.0f, -63.13f)
                curveToRelative(-17.44f, -17.43f, -45.7f, -17.43f, -63.13f, 0.0f)
                close()
                moveTo(87.99f, 107.14f)
                curveToRelative(-13.6f, 13.6f, -35.64f, 13.6f, -49.24f, 0.0f)
                curveToRelative(-13.6f, -13.6f, -13.6f, -35.64f, 0.0f, -49.24f)
                reflectiveCurveToRelative(35.64f, -13.6f, 49.24f, 0.0f)
                curveToRelative(13.6f, 13.6f, 13.6f, 35.64f, 0.0f, 49.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF64B5F6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(37.94f, 20.79f)
                lineToRelative(7.75f, -11.75f)
                lineToRelative(6.0f, 6.86f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(55.71f, 29.8f)
                lineToRelative(-17.77f, -9.01f)
                lineToRelative(13.75f, -4.89f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.5f, 18.63f)
                lineTo(51.69f, 15.9f)
                lineToRelative(4.02f, 13.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1E88E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(91.07f, 20.79f)
                lineTo(83.32f, 9.04f)
                lineToRelative(-6.0f, 6.86f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB3E5FC)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(73.29f, 29.8f)
                lineToRelative(17.78f, -9.01f)
                lineToRelative(-13.75f, -4.89f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(64.5f, 18.63f)
                lineToRelative(12.82f, -2.73f)
                lineToRelative(-4.03f, 13.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB2EBF2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.29f, 29.8f)
                lineTo(64.5f, 18.63f)
                lineTo(55.71f, 29.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB3E5FC)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(55.22f, 4.0f)
                horizontalLineToRelative(18.57f)
                lineToRelative(9.53f, 5.04f)
                lineToRelative(-6.0f, 6.86f)
                lineToRelative(-12.82f, 2.73f)
                lineToRelative(-12.81f, -2.73f)
                lineToRelative(-6.0f, -6.86f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1E88E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(64.5f, 47.09f)
                lineToRelative(-26.56f, -26.3f)
                lineToRelative(17.77f, 9.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(64.5f, 47.09f)
                lineToRelative(26.57f, -26.3f)
                lineToRelative(-17.78f, 9.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE1F5FE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.29f, 29.8f)
                lineTo(64.5f, 47.09f)
                lineTo(55.71f, 29.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.05f, 51.24f)
                curveToRelative(2.91f, 0.95f, 1.18f, 3.67f, 0.57f, 4.27f)
                curveToRelative(-2.39f, 2.36f, -3.03f, 3.89f, -4.52f, 6.36f)
                curveToRelative(-1.55f, 2.56f, -3.36f, 2.69f, -4.1f, 1.16f)
                curveToRelative(-2.02f, -4.19f, 4.65f, -12.91f, 8.05f, -11.79f)
                close()
                moveTo(87.15f, 111.38f)
                curveToRelative(-5.16f, 3.14f, -7.01f, 1.88f, -5.69f, 1.0f)
                curveToRelative(4.97f, -3.32f, 11.64f, -7.94f, 15.78f, -20.76f)
                curveToRelative(0.29f, -0.91f, 1.27f, -1.56f, 2.17f, -1.24f)
                curveToRelative(0.82f, 0.29f, 1.3f, 1.16f, 1.07f, 1.99f)
                curveToRelative(-3.43f, 12.59f, -9.18f, 16.48f, -13.33f, 19.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(82.16f, 29.7f)
                lineToRelative(-13.4f, 13.14f)
                lineToRelative(-1.97f, -0.19f)
                lineToRelative(0.4f, -8.7f)
                curveToRelative(0.0f, -1.7f, -1.06f, -2.96f, -2.68f, -2.96f)
                curveToRelative(-1.63f, 0.0f, -2.6f, 1.26f, -2.6f, 2.96f)
                lineToRelative(0.4f, 8.7f)
                lineToRelative(-1.97f, 0.19f)
                lineTo(46.93f, 29.7f)
                reflectiveCurveToRelative(-3.49f, 1.51f, -0.14f, 4.83f)
                curveToRelative(3.79f, 3.75f, 13.1f, 12.64f, 13.1f, 12.64f)
                horizontalLineToRelative(9.3f)
                reflectiveCurveToRelative(9.32f, -8.89f, 13.1f, -12.64f)
                curveToRelative(3.36f, -3.32f, -0.13f, -4.83f, -0.13f, -4.83f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF8E8E8E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(82.13f, 29.65f)
                lineTo(71.21f, 40.44f)
                curveToRelative(-0.93f, 0.91f, -0.5f, 2.5f, 0.77f, 2.8f)
                curveToRelative(2.85f, 0.66f, 7.53f, 2.37f, 13.85f, 6.8f)
                curveToRelative(4.26f, 2.99f, 6.68f, 2.46f, 7.6f, 0.76f)
                curveToRelative(1.16f, -2.14f, 0.23f, -4.89f, -1.84f, -6.53f)
                curveToRelative(-6.59f, -5.22f, -12.27f, -6.84f, -12.27f, -6.84f)
                curveToRelative(1.07f, -1.03f, 2.11f, -2.03f, 2.98f, -2.9f)
                curveToRelative(3.35f, -3.32f, -0.17f, -4.88f, -0.17f, -4.88f)
                close()
            }
        }
            .build()
        return _ring!!
    }

private var _ring: ImageVector? = null
