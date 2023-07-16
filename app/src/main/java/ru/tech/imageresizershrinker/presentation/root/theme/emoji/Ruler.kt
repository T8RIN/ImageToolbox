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

val Emoji.Ruler: ImageVector
    get() {
        if (_ruler != null) {
            return _ruler!!
        }
        _ruler = Builder(
            name = "Ruler", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFEAA700)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(31.22f, 4.78f)
                lineToRelative(92.16f, 92.16f)
                curveToRelative(0.87f, 0.87f, 0.87f, 2.29f, 0.0f, 3.17f)
                lineToRelative(-21.84f, 21.84f)
                curveToRelative(-0.87f, 0.87f, -2.29f, 0.87f, -3.17f, 0.0f)
                lineTo(6.21f, 29.79f)
                curveToRelative(-0.87f, -0.87f, -0.87f, -2.29f, 0.0f, -3.17f)
                lineTo(28.05f, 4.78f)
                curveToRelative(0.87f, -0.88f, 2.29f, -0.88f, 3.17f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBF8400)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(31.22f, 4.78f)
                lineToRelative(88.04f, 88.04f)
                curveToRelative(0.87f, 0.87f, -19.07f, 19.47f, -19.95f, 20.34f)
                lineToRelative(1.01f, 9.41f)
                reflectiveCurveToRelative(-1.08f, 0.25f, -1.95f, -0.63f)
                lineTo(6.21f, 29.79f)
                curveToRelative(-0.87f, -0.87f, -0.87f, -2.29f, 0.0f, -3.17f)
                lineTo(28.05f, 4.78f)
                curveToRelative(0.87f, -0.88f, 2.29f, -0.88f, 3.17f, 0.0f)
                close()
            }
            path(
                fill = linearGradient(
                    0.546f to Color(0xFFFDD835), 0.893f to Color(0xFFF9A825),
                    start = Offset(60.506275f, 66.464584f), end = Offset(83.343704f, 43.627155f)
                ),
                stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(31.22f, 4.78f)
                lineToRelative(89.69f, 89.69f)
                lineToRelative(2.86f, 2.86f)
                reflectiveCurveToRelative(-2.7f, 0.14f, -5.99f, 3.42f)
                reflectiveCurveToRelative(-16.4f, 16.4f, -16.4f, 16.4f)
                curveToRelative(-0.87f, 0.87f, -2.29f, 0.87f, -3.17f, 0.0f)
                lineTo(8.52f, 27.47f)
                curveToRelative(-0.87f, -0.87f, -0.87f, -2.29f, 0.0f, -3.17f)
                lineTo(28.05f, 4.78f)
                curveToRelative(0.87f, -0.88f, 2.29f, -0.88f, 3.17f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(72.17f, 56.85f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineToRelative(-5.14f, 5.14f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(74.444f, 51.812f), end = Offset(75.653f, 50.509f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(77.73f, 51.29f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-2.83f, -2.83f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(53.34f, 38.02f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineToRelative(-5.14f, 5.14f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(55.618f, 32.986f), end = Offset(56.827f, 31.683f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(58.9f, 32.46f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-2.83f, -2.82f)
                lineToRelative(1.99f, -2.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(41.32f, 31.92f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-2.42f, -2.42f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(8.52f, -8.52f)
                lineToRelative(3.54f, 3.54f)
                lineToRelative(-8.52f, 8.52f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(46.193f, 23.584f), end = Offset(47.418f, 22.265f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(49.84f, 23.4f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-3.54f, -3.53f)
                lineToRelative(2.0f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(91.0f, 75.67f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineTo(91.0f, 75.67f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(93.27f, 70.638f), end = Offset(94.479f, 69.335f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(96.55f, 70.11f)
                lineToRelative(-1.99f, 2.0f)
                lineToRelative(-2.83f, -2.83f)
                lineToRelative(2.0f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(109.82f, 94.5f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineToRelative(-5.14f, 5.14f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(112.095f, 89.463f), end = Offset(113.304f, 88.161f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(115.38f, 88.94f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-2.83f, -2.83f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.15f, 50.75f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-2.42f, -2.42f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(8.52f, -8.52f)
                lineToRelative(3.54f, 3.54f)
                lineToRelative(-8.52f, 8.52f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(65.019f, 42.41f), end = Offset(66.244f, 41.092f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(68.67f, 42.23f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-3.54f, -3.53f)
                lineToRelative(1.99f, -2.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(78.98f, 69.57f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-2.42f, -2.42f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(8.52f, -8.52f)
                lineToRelative(3.54f, 3.54f)
                lineToRelative(-8.52f, 8.52f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(83.847f, 61.238f), end = Offset(85.072f, 59.919f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(87.5f, 61.06f)
                lineToRelative(-2.0f, 1.99f)
                lineToRelative(-3.53f, -3.54f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(97.8f, 88.4f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-2.42f, -2.42f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(8.52f, -8.52f)
                lineToRelative(3.54f, 3.54f)
                lineToRelative(-8.52f, 8.52f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(102.671f, 80.062f), end = Offset(103.896f, 78.744f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(106.32f, 79.88f)
                lineToRelative(-1.99f, 1.99f)
                lineToRelative(-3.54f, -3.53f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.52f, 19.19f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineToRelative(-5.14f, 5.14f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(36.792f, 14.16f), end = Offset(38.001f, 12.857f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(40.08f, 13.64f)
                lineToRelative(-2.0f, 1.99f)
                lineToRelative(-2.82f, -2.83f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF795548)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.52f, 19.19f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, -1.12f, 0.0f)
                lineToRelative(-1.71f, -1.71f)
                arcToRelative(0.79f, 0.79f, 0.0f, false, true, 0.0f, -1.12f)
                lineToRelative(5.14f, -5.14f)
                lineToRelative(2.83f, 2.83f)
                lineToRelative(-5.14f, 5.14f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0x005D4037), 1.0f to Color(0xFF5D4037), start =
                    Offset(36.792f, 14.16f), end = Offset(38.001f, 12.857f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(40.08f, 13.64f)
                lineToRelative(-2.0f, 1.99f)
                lineToRelative(-2.82f, -2.83f)
                lineToRelative(1.99f, -1.99f)
                close()
            }
        }
            .build()
        return _ruler!!
    }

private var _ruler: ImageVector? = null
