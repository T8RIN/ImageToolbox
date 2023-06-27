package ru.tech.imageresizershrinker.presentation.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.theme.Emoji

val Emoji.MoonA: ImageVector
    get() {
        if (_moona != null) {
            return _moona!!
        }
        _moona = Builder(
            name = "MoonA", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = radialGradient(
                    0.306f to Color(0xFF6092AB), 0.472f to Color(0xFF5989A1),
                    0.749f to Color(0xFF457287), 0.843f to Color(0xFF3D687D), center =
                    Offset(63.55f, 56.074f), radius = 68.672f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 64.0f)
                moveToRelative(-60.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, 120.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, -120.0f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.762f to Color(0x00B2EBF2), 1.0f to Color(0xFFE0F7FA),
                    center = Offset(63.702f, 50.155f), radius = 85.604f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.0f, 64.0f)
                moveToRelative(-60.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, 120.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, -120.0f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF212121)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(85.49f, 59.81f)
                curveToRelative(1.53f, 1.25f, 4.18f, 1.39f, 6.42f, 0.44f)
                curveToRelative(2.66f, -1.14f, 3.87f, -2.9f, 3.91f, -5.65f)
                curveToRelative(0.04f, -3.41f, -2.8f, -7.05f, -8.42f, -7.41f)
                curveToRelative(-9.26f, -0.59f, -12.82f, 7.71f, -12.92f, 8.02f)
                curveToRelative(-0.22f, 0.65f, 0.1f, 1.58f, 1.95f, 0.12f)
                curveToRelative(5.63f, -4.45f, 7.65f, -3.71f, 7.65f, -3.71f)
                reflectiveCurveToRelative(-3.24f, 4.37f, 1.41f, 8.19f)
                close()
                moveTo(43.26f, 59.81f)
                curveToRelative(1.53f, 1.25f, 4.18f, 1.39f, 6.42f, 0.44f)
                curveToRelative(2.66f, -1.14f, 3.87f, -2.9f, 3.91f, -5.65f)
                curveToRelative(0.04f, -3.41f, -2.8f, -7.05f, -8.42f, -7.41f)
                curveToRelative(-9.26f, -0.59f, -12.82f, 7.71f, -12.92f, 8.02f)
                curveToRelative(-0.22f, 0.65f, 0.1f, 1.58f, 1.95f, 0.12f)
                curveToRelative(5.63f, -4.45f, 7.65f, -3.71f, 7.65f, -3.71f)
                reflectiveCurveToRelative(-3.24f, 4.37f, 1.41f, 8.19f)
                close()
                moveTo(80.04f, 77.94f)
                lineToRelative(-0.12f, -0.07f)
                curveToRelative(-0.6f, -0.36f, -1.39f, -0.29f, -1.92f, 0.16f)
                curveToRelative(-1.57f, 1.34f, -8.08f, 3.82f, -14.01f, 3.94f)
                curveToRelative(-5.93f, -0.12f, -12.44f, -2.6f, -14.01f, -3.94f)
                curveToRelative(-0.53f, -0.45f, -1.31f, -0.53f, -1.92f, -0.16f)
                lineToRelative(-0.12f, 0.07f)
                curveToRelative(-0.76f, 0.46f, -0.9f, 1.4f, -0.37f, 2.08f)
                curveToRelative(2.06f, 2.58f, 7.52f, 6.46f, 16.41f, 6.59f)
                curveToRelative(8.89f, -0.12f, 14.34f, -4.01f, 16.41f, -6.59f)
                curveToRelative(0.56f, -0.68f, 0.41f, -1.62f, -0.35f, -2.08f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF274C5E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(61.8f, 93.24f)
                curveToRelative(-0.72f, 0.21f, -1.57f, 0.75f, -1.39f, 1.48f)
                curveToRelative(0.06f, 0.24f, 0.22f, 0.43f, 0.4f, 0.6f)
                curveToRelative(0.57f, 0.52f, 1.31f, 0.82f, 2.05f, 1.01f)
                curveToRelative(2.09f, 0.53f, 4.36f, 0.17f, 6.25f, -0.85f)
                curveToRelative(1.09f, -0.59f, 4.57f, -3.19f, 3.8f, -4.83f)
                curveToRelative(-0.62f, -1.33f, -3.01f, 0.52f, -3.94f, 0.91f)
                curveToRelative(-2.31f, 0.97f, -4.77f, 0.99f, -7.17f, 1.68f)
                close()
                moveTo(58.68f, 72.26f)
                curveToRelative(-1.44f, -1.62f, -0.11f, -2.46f, 1.47f, -1.98f)
                curveToRelative(2.0f, 0.61f, 5.2f, 0.32f, 6.02f, -1.48f)
                curveToRelative(1.58f, -3.46f, -0.39f, -5.41f, -2.84f, -11.39f)
                curveToRelative(-2.56f, -6.25f, 0.65f, -13.06f, 4.35f, -13.41f)
                curveToRelative(2.97f, -0.28f, 1.98f, 1.27f, 1.05f, 1.93f)
                curveToRelative(-1.82f, 1.3f, -3.73f, 5.6f, -0.8f, 10.42f)
                curveToRelative(4.99f, 8.19f, 5.4f, 12.12f, 2.42f, 15.98f)
                curveToRelative(-1.85f, 2.4f, -7.62f, 4.49f, -11.67f, -0.07f)
                close()
            }
            path(
                fill = radialGradient(
                    0.0f to Color(0xFFFFFFFF), 1.0f to Color(0x00FFF9B9), center
                    = Offset(91.892654f, 62.838722f), radius = 16.370747f
                ), stroke = null, fillAlpha
                = 0.3f, strokeAlpha = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(82.57f, 72.92f)
                curveToRelative(1.55f, 3.75f, 7.63f, 4.79f, 13.58f, 2.32f)
                curveToRelative(5.95f, -2.46f, 9.52f, -7.5f, 7.97f, -11.24f)
                curveToRelative(-1.55f, -3.75f, -7.63f, -4.79f, -13.58f, -2.32f)
                curveToRelative(-5.95f, 2.46f, -9.52f, 7.49f, -7.97f, 11.24f)
                close()
            }
            path(
                fill = radialGradient(
                    0.0f to Color(0xFFFFFFFF), 1.0f to Color(0x00FFF9B9), center
                    = Offset(40.55805f, 63.186386f), radius = 16.135736f
                ), stroke = null, fillAlpha =
                0.3f, strokeAlpha = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(49.43f, 72.92f)
                curveToRelative(-1.55f, 3.75f, -7.63f, 4.79f, -13.58f, 2.32f)
                curveToRelative(-5.95f, -2.46f, -9.52f, -7.5f, -7.97f, -11.24f)
                curveToRelative(1.55f, -3.75f, 7.63f, -4.79f, 13.58f, -2.32f)
                curveToRelative(5.95f, 2.46f, 9.52f, 7.49f, 7.97f, 11.24f)
                close()
            }
        }
            .build()
        return _moona!!
    }

private var _moona: ImageVector? = null
