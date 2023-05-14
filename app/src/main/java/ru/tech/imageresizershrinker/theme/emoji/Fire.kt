package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

val Emoji.Fire: ImageVector
    get() {
        if (_fire != null) {
            return _fire!!
        }
        _fire = Builder(
            name = "Fire", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = radialGradient(
                    0.314f to Color(0xFFFF9800), 0.662f to Color(0xFFFF6D00),
                    0.972f to Color(0xFFF44336), center = Offset(62.215763f, 124.30092f), radius =
                    70.58766f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(35.56f, 40.73f)
                curveToRelative(-0.57f, 6.08f, -0.97f, 16.84f, 2.62f, 21.42f)
                curveToRelative(0.0f, 0.0f, -1.69f, -11.82f, 13.46f, -26.65f)
                curveToRelative(6.1f, -5.97f, 7.51f, -14.09f, 5.38f, -20.18f)
                curveToRelative(-1.21f, -3.45f, -3.42f, -6.3f, -5.34f, -8.29f)
                curveToRelative(-1.12f, -1.17f, -0.26f, -3.1f, 1.37f, -3.03f)
                curveToRelative(9.86f, 0.44f, 25.84f, 3.18f, 32.63f, 20.22f)
                curveToRelative(2.98f, 7.48f, 3.2f, 15.21f, 1.78f, 23.07f)
                curveToRelative(-0.9f, 5.02f, -4.1f, 16.18f, 3.2f, 17.55f)
                curveToRelative(5.21f, 0.98f, 7.73f, -3.16f, 8.86f, -6.14f)
                curveToRelative(0.47f, -1.24f, 2.1f, -1.55f, 2.98f, -0.56f)
                curveToRelative(8.8f, 10.01f, 9.55f, 21.8f, 7.73f, 31.95f)
                curveToRelative(-3.52f, 19.62f, -23.39f, 33.9f, -43.13f, 33.9f)
                curveToRelative(-24.66f, 0.0f, -44.29f, -14.11f, -49.38f, -39.65f)
                curveToRelative(-2.05f, -10.31f, -1.01f, -30.71f, 14.89f, -45.11f)
                curveToRelative(1.18f, -1.08f, 3.11f, -0.12f, 2.95f, 1.5f)
                close()
            }
            path(
                fill = radialGradient(
                    0.214f to Color(0xFFFFF176),
                    0.328f to Color(0xFFFFF27D),
                    0.487f to Color(0xFFFFF48F),
                    0.672f to Color(0xFFFFF7AD),
                    0.793f to
                            Color(0xFFFFF9C4),
                    0.822f to Color(0xCDFFF8BD),
                    0.863f to Color(0x86FFF6AB),
                    0.91f to Color(0x35FFF38D),
                    0.941f to Color(0x00FFF176),
                    center =
                    Offset(66.179955f, 54.058376f),
                    radius = 73.85638f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(76.11f, 77.42f)
                curveToRelative(-9.09f, -11.7f, -5.02f, -25.05f, -2.79f, -30.37f)
                curveToRelative(0.3f, -0.7f, -0.5f, -1.36f, -1.13f, -0.93f)
                curveToRelative(-3.91f, 2.66f, -11.92f, 8.92f, -15.65f, 17.73f)
                curveToRelative(-5.05f, 11.91f, -4.69f, 17.74f, -1.7f, 24.86f)
                curveToRelative(1.8f, 4.29f, -0.29f, 5.2f, -1.34f, 5.36f)
                curveToRelative(-1.02f, 0.16f, -1.96f, -0.52f, -2.71f, -1.23f)
                arcToRelative(16.09f, 16.09f, 0.0f, false, true, -4.44f, -7.6f)
                curveToRelative(-0.16f, -0.62f, -0.97f, -0.79f, -1.34f, -0.28f)
                curveToRelative(-2.8f, 3.87f, -4.25f, 10.08f, -4.32f, 14.47f)
                curveTo(40.47f, 113.0f, 51.68f, 124.0f, 65.24f, 124.0f)
                curveToRelative(17.09f, 0.0f, 29.54f, -18.9f, 19.72f, -34.7f)
                curveToRelative(-2.85f, -4.6f, -5.53f, -7.61f, -8.85f, -11.88f)
                close()
            }
        }
            .build()
        return _fire!!
    }

private var _fire: ImageVector? = null
