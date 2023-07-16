package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Calendar: ImageVector
    get() {
        if (_calendar != null) {
            return _calendar!!
        }
        _calendar = Builder(
            name = "Calendar", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(6.81f, 45.78f)
                verticalLineToRelative(64.74f)
                curveToRelative(0.0f, 3.12f, 2.9f, 5.21f, 6.32f, 7.61f)
                curveToRelative(3.9f, 2.74f, 8.48f, 5.25f, 10.17f, 5.25f)
                lineToRelative(93.55f, 0.62f)
                curveToRelative(2.4f, 0.0f, 4.34f, -2.94f, 4.34f, -5.34f)
                verticalLineTo(45.78f)
                horizontalLineTo(6.81f)
                close()
            }
            path(
                fill = linearGradient(
                    0.337f to Color(0xFF616161), 1.0f to Color(0x00616161), start
                    = Offset(117.05f, 74.704f), end = Offset(117.05f, 114.633f)
                ), stroke = null,
                fillAlpha = 0.29f, strokeAlpha = 0.29f, strokeLineWidth = 0.0f, strokeLineCap =
                Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(121.19f, 118.66f)
                lineToRelative(-8.28f, -8.51f)
                verticalLineTo(43.92f)
                lineToRelative(8.28f, -0.19f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC62828)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(121.19f, 51.32f)
                lineToRelative(-6.46f, -4.05f)
                lineTo(104.62f, 4.0f)
                horizontalLineToRelative(5.44f)
                curveToRelative(9.65f, 0.0f, 11.13f, 5.57f, 11.13f, 7.47f)
                verticalLineToRelative(39.85f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFAFAFA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(9.75f, 45.78f)
                verticalLineToRelative(62.68f)
                curveToRelative(0.0f, 2.7f, 2.19f, 4.88f, 4.88f, 4.88f)
                horizontalLineToRelative(94.85f)
                curveToRelative(2.7f, 0.0f, 5.22f, -2.01f, 5.22f, -4.71f)
                verticalLineTo(45.78f)
                horizontalLineTo(9.75f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.73f, 47.27f)
                horizontalLineTo(6.81f)
                verticalLineTo(9.75f)
                curveTo(6.81f, 6.57f, 9.38f, 4.0f, 12.56f, 4.0f)
                horizontalLineToRelative(96.59f)
                curveToRelative(3.19f, 0.0f, 5.77f, 2.59f, 5.75f, 5.78f)
                lineToRelative(-0.17f, 37.49f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(41.95f, 15.46f)
                horizontalLineToRelative(4.12f)
                lineTo(46.07f, 29.3f)
                curveToRelative(0.0f, 1.27f, -0.28f, 2.4f, -0.84f, 3.37f)
                curveToRelative(-0.56f, 0.97f, -1.36f, 1.73f, -2.38f, 2.26f)
                curveToRelative(-1.03f, 0.53f, -2.18f, 0.8f, -3.47f, 0.8f)
                curveToRelative(-2.11f, 0.0f, -3.76f, -0.54f, -4.94f, -1.61f)
                curveToRelative(-1.18f, -1.07f, -1.77f, -2.6f, -1.77f, -4.56f)
                horizontalLineToRelative(4.15f)
                curveToRelative(0.0f, 0.98f, 0.21f, 1.7f, 0.62f, 2.17f)
                curveToRelative(0.41f, 0.47f, 1.06f, 0.7f, 1.95f, 0.7f)
                curveToRelative(0.79f, 0.0f, 1.41f, -0.27f, 1.88f, -0.81f)
                reflectiveCurveToRelative(0.7f, -1.31f, 0.7f, -2.31f)
                lineTo(41.97f, 15.46f)
                close()
                moveTo(58.23f, 33.94f)
                curveToRelative(-0.98f, 1.19f, -2.33f, 1.78f, -4.06f, 1.78f)
                curveToRelative(-1.59f, 0.0f, -2.81f, -0.46f, -3.64f, -1.37f)
                curveToRelative(-0.84f, -0.91f, -1.27f, -2.26f, -1.28f, -4.02f)
                lineTo(49.25f, 20.6f)
                horizontalLineToRelative(3.97f)
                verticalLineToRelative(9.61f)
                curveToRelative(0.0f, 1.55f, 0.7f, 2.32f, 2.11f, 2.32f)
                curveToRelative(1.35f, 0.0f, 2.27f, -0.47f, 2.77f, -1.4f)
                lineTo(58.1f, 20.6f)
                horizontalLineToRelative(3.98f)
                verticalLineToRelative(14.85f)
                horizontalLineToRelative(-3.73f)
                lineToRelative(-0.12f, -1.51f)
                close()
                moveTo(69.25f, 35.45f)
                horizontalLineToRelative(-3.98f)
                lineTo(65.27f, 14.37f)
                horizontalLineToRelative(3.98f)
                verticalLineToRelative(21.08f)
                close()
                moveTo(78.06f, 29.83f)
                lineToRelative(2.75f, -9.24f)
                horizontalLineToRelative(4.26f)
                lineTo(79.1f, 37.75f)
                lineToRelative(-0.33f, 0.78f)
                curveToRelative(-0.89f, 1.94f, -2.35f, 2.91f, -4.39f, 2.91f)
                curveToRelative(-0.58f, 0.0f, -1.16f, -0.09f, -1.76f, -0.26f)
                verticalLineToRelative(-3.01f)
                lineToRelative(0.6f, 0.01f)
                curveToRelative(0.75f, 0.0f, 1.31f, -0.11f, 1.68f, -0.34f)
                curveToRelative(0.37f, -0.23f, 0.66f, -0.61f, 0.87f, -1.14f)
                lineToRelative(0.47f, -1.22f)
                lineToRelative(-5.2f, -14.89f)
                horizontalLineToRelative(4.27f)
                lineToRelative(2.75f, 9.24f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(51.58f, 102.31f)
                horizontalLineTo(43.0f)
                verticalLineTo(69.26f)
                lineToRelative(-10.24f, 3.17f)
                verticalLineToRelative(-6.97f)
                lineToRelative(17.89f, -6.41f)
                horizontalLineToRelative(0.92f)
                verticalLineToRelative(43.26f)
                close()
                moveTo(91.95f, 63.9f)
                lineToRelative(-16.7f, 38.41f)
                horizontalLineTo(66.2f)
                lineToRelative(16.73f, -36.28f)
                horizontalLineTo(61.45f)
                verticalLineToRelative(-6.91f)
                horizontalLineToRelative(30.5f)
                verticalLineToRelative(4.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF616161)), stroke = null, fillAlpha = 0.29f, strokeAlpha
                = 0.29f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(114.7f, 52.24f)
                lineToRelative(-104.95f, 0.11f)
                verticalLineToRelative(-5.08f)
                horizontalLineTo(114.7f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF606060)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(22.8f, 17.74f)
                moveToRelative(-7.1f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, 14.2f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, -14.2f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF94D1E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(20.44f, 15.39f)
                moveToRelative(-7.1f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, 14.2f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, -14.2f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFC9EFF2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.05f, 12.45f)
                curveToRelative(-0.16f, 0.85f, -0.7f, 1.57f, -1.32f, 2.18f)
                curveToRelative(-0.74f, 0.72f, -1.61f, 1.32f, -2.59f, 1.65f)
                curveToRelative(-0.58f, 0.2f, -1.25f, 0.28f, -1.76f, -0.06f)
                curveToRelative(-1.41f, -0.95f, -0.28f, -4.52f, 0.79f, -5.47f)
                curveToRelative(1.63f, -1.44f, 5.44f, -1.17f, 4.88f, 1.7f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF606060)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(101.3f, 17.74f)
                moveToRelative(-7.1f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, 14.2f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, -14.2f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF94D1E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(98.95f, 15.39f)
                moveToRelative(-7.1f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, 14.2f, 0.0f)
                arcToRelative(7.1f, 7.1f, 0.0f, true, true, -14.2f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFC9EFF2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(99.56f, 12.45f)
                curveToRelative(-0.16f, 0.85f, -0.7f, 1.57f, -1.32f, 2.18f)
                curveToRelative(-0.74f, 0.72f, -1.61f, 1.32f, -2.59f, 1.65f)
                curveToRelative(-0.58f, 0.2f, -1.25f, 0.28f, -1.76f, -0.06f)
                curveToRelative(-1.41f, -0.95f, -0.28f, -4.52f, 0.79f, -5.47f)
                curveToRelative(1.63f, -1.44f, 5.43f, -1.17f, 4.88f, 1.7f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF757575)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(103.71f, 118.74f)
                curveToRelative(-3.18f, 0.39f, -6.36f, 0.56f, -9.54f, 0.84f)
                curveToRelative(-3.18f, 0.2f, -6.36f, 0.38f, -9.54f, 0.5f)
                curveToRelative(-3.18f, 0.16f, -6.36f, 0.19f, -9.54f, 0.29f)
                lineToRelative(-9.54f, 0.09f)
                lineToRelative(-9.54f, -0.1f)
                curveToRelative(-3.18f, -0.09f, -6.36f, -0.13f, -9.54f, -0.29f)
                curveToRelative(-3.18f, -0.12f, -6.36f, -0.3f, -9.54f, -0.5f)
                curveToRelative(-3.18f, -0.28f, -6.36f, -0.45f, -9.54f, -0.84f)
                verticalLineToRelative(-0.2f)
                lineToRelative(38.15f, -0.1f)
                lineToRelative(38.15f, 0.1f)
                verticalLineToRelative(0.21f)
                close()
                moveTo(84.5f, 113.34f)
                horizontalLineToRelative(25.32f)
                curveToRelative(2.7f, 0.0f, 4.88f, -2.19f, 4.88f, -4.88f)
                lineTo(114.7f, 90.03f)
                reflectiveCurveToRelative(-5.5f, 7.64f, -13.83f, 13.92f)
                reflectiveCurveToRelative(-16.37f, 9.39f, -16.37f, 9.39f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.17f, 104.31f)
                curveToRelative(7.72f, -9.09f, 7.53f, -14.27f, 7.53f, -14.27f)
                reflectiveCurveToRelative(-2.23f, 3.14f, -9.24f, 3.85f)
                curveToRelative(-2.47f, 0.25f, -6.01f, -0.7f, -8.62f, -1.57f)
                curveToRelative(-1.57f, -0.52f, -3.2f, 0.6f, -3.25f, 2.24f)
                curveToRelative(-0.07f, 2.11f, -0.42f, 5.07f, -1.55f, 8.59f)
                curveToRelative(-1.88f, 5.88f, -7.55f, 10.2f, -7.55f, 10.2f)
                reflectiveCurveToRelative(14.05f, 1.11f, 22.68f, -9.04f)
                close()
            }
        }
            .build()
        return _calendar!!
    }

private var _calendar: ImageVector? = null
