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

val Emoji.Tick: ImageVector
    get() {
        if (_tick != null) {
            return _tick!!
        }
        _tick = Builder(
            name = "Tick", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF689F38)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(116.46f, 3.96f)
                horizontalLineToRelative(-104.0f)
                curveToRelative(-4.42f, 0.0f, -8.0f, 3.58f, -8.0f, 8.0f)
                verticalLineToRelative(104.0f)
                curveToRelative(0.0f, 4.42f, 3.58f, 8.0f, 8.0f, 8.0f)
                horizontalLineToRelative(104.0f)
                curveToRelative(4.42f, 0.0f, 8.0f, -3.58f, 8.0f, -8.0f)
                verticalLineToRelative(-104.0f)
                curveToRelative(0.0f, -4.42f, -3.58f, -8.0f, -8.0f, -8.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF7CB342)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(110.16f, 3.96f)
                horizontalLineToRelative(-98.2f)
                arcToRelative(7.555f, 7.555f, 0.0f, false, false, -7.5f, 7.5f)
                verticalLineToRelative(97.9f)
                curveToRelative(-0.01f, 4.14f, 3.34f, 7.49f, 7.48f, 7.5f)
                horizontalLineToRelative(98.12f)
                curveToRelative(4.14f, 0.01f, 7.49f, -3.34f, 7.5f, -7.48f)
                verticalLineTo(11.46f)
                curveToRelative(0.09f, -4.05f, -3.13f, -7.41f, -7.18f, -7.5f)
                horizontalLineToRelative(-0.22f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(40.16f, 12.86f)
                curveToRelative(0.0f, -2.3f, -1.6f, -3.0f, -10.8f, -2.7f)
                curveToRelative(-7.7f, 0.3f, -11.5f, 1.2f, -13.8f, 4.0f)
                reflectiveCurveToRelative(-2.9f, 8.5f, -3.0f, 15.3f)
                curveToRelative(0.0f, 4.8f, 0.0f, 9.3f, 2.5f, 9.3f)
                curveToRelative(3.4f, 0.0f, 3.4f, -7.9f, 6.2f, -12.3f)
                curveToRelative(5.4f, -8.7f, 18.9f, -10.6f, 18.9f, -13.6f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFBF9F9)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(43.26f, 109.46f)
                arcToRelative(8.862f, 8.862f, 0.0f, false, true, -8.7f, -6.2f)
                lineToRelative(-15.1f, -45.5f)
                curveToRelative(-1.46f, -4.81f, 1.26f, -9.9f, 6.07f, -11.36f)
                curveToRelative(4.65f, -1.41f, 9.59f, 1.08f, 11.23f, 5.66f)
                lineToRelative(9.8f, 29.5f)
                lineToRelative(47.1f, -59.6f)
                curveToRelative(3.12f, -3.95f, 8.85f, -4.62f, 12.8f, -1.5f)
                reflectiveCurveToRelative(4.62f, 8.85f, 1.5f, 12.8f)
                lineToRelative(-57.6f, 72.7f)
                arcToRelative(9.086f, 9.086f, 0.0f, false, true, -7.1f, 3.5f)
                close()
            }
        }
            .build()
        return _tick!!
    }

private var _tick: ImageVector? = null
