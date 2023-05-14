package ru.tech.imageresizershrinker.theme.emoji

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

val Emoji.Alien: ImageVector
    get() {
        if (_alien != null) {
            return _alien!!
        }
        _alien = Builder(
            name = "Alien", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFADBCC3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 4.5f)
                curveTo(36.2f, 4.5f, 8.0f, 28.1f, 8.0f, 55.8f)
                reflectiveCurveToRelative(44.3f, 67.7f, 56.2f, 67.7f)
                curveToRelative(13.2f, 0.0f, 55.8f, -39.9f, 55.8f, -67.7f)
                reflectiveCurveTo(91.8f, 4.5f, 64.0f, 4.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF422B0D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 102.35f)
                curveToRelative(-9.37f, 0.0f, -16.06f, -5.52f, -18.07f, -8.58f)
                curveToRelative(-0.76f, -0.98f, -0.92f, -2.31f, -0.41f, -3.44f)
                curveToRelative(0.39f, -0.74f, 1.17f, -1.18f, 2.0f, -1.15f)
                curveToRelative(0.3f, 0.0f, 0.6f, 0.05f, 0.88f, 0.15f)
                curveTo(53.1f, 90.8f, 57.23f, 93.0f, 64.0f, 93.0f)
                reflectiveCurveToRelative(10.92f, -2.16f, 15.63f, -3.63f)
                curveToRelative(0.28f, -0.1f, 0.58f, -0.15f, 0.88f, -0.15f)
                curveToRelative(0.83f, -0.03f, 1.61f, 0.41f, 2.0f, 1.15f)
                curveToRelative(0.51f, 1.13f, 0.35f, 2.46f, -0.41f, 3.44f)
                curveToRelative(-2.04f, 3.02f, -8.73f, 8.54f, -18.1f, 8.54f)
                close()
                moveTo(54.05f, 81.56f)
                curveToRelative(-3.0f, 3.88f, -17.71f, 3.89f, -28.85f, -6.36f)
                curveToRelative(-10.09f, -9.28f, -9.46f, -25.64f, -6.13f, -28.94f)
                reflectiveCurveToRelative(19.19f, -3.38f, 28.85f, 6.36f)
                reflectiveCurveToRelative(9.8f, 25.3f, 6.13f, 28.94f)
                close()
                moveTo(80.08f, 52.58f)
                curveToRelative(9.66f, -9.7f, 25.52f, -9.62f, 28.85f, -6.33f)
                reflectiveCurveToRelative(3.53f, 19.11f, -6.13f, 28.8f)
                reflectiveCurveTo(77.64f, 85.0f, 73.94f, 81.38f)
                reflectiveCurveToRelative(-3.52f, -19.1f, 6.14f, -28.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFADBCC3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.0f, 4.5f)
                curveToRelative(14.52f, 0.0f, 29.13f, 6.42f, 39.8f, 16.5f)
                curveTo(93.0f, 9.46f, 77.17f, 2.0f, 61.47f, 2.0f)
                curveToRelative(-27.8f, 0.0f, -56.0f, 23.6f, -56.0f, 51.3f)
                curveToRelative(0.0f, 15.6f, 14.06f, 35.11f, 28.47f, 49.18f)
                curveTo(20.46f, 88.64f, 8.0f, 70.49f, 8.0f, 55.8f)
                curveTo(8.0f, 28.1f, 36.2f, 4.5f, 64.0f, 4.5f)
                close()
            }
        }
            .build()
        return _alien!!
    }

private var _alien: ImageVector? = null
