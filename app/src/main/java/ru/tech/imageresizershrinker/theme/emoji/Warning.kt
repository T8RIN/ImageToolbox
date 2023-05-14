package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

public val Emoji.Warning: ImageVector
    get() {
        if (_warning != null) {
            return _warning!!
        }
        _warning = Builder(name = "Warning", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = SolidColor(Color(0xFFF2A600)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(57.16f, 8.42f)
                lineToRelative(-52.0f, 104.0f)
                curveToRelative(-1.94f, 4.02f, -0.26f, 8.85f, 3.75f, 10.79f)
                curveToRelative(1.08f, 0.52f, 2.25f, 0.8f, 3.45f, 0.81f)
                horizontalLineToRelative(104.0f)
                curveToRelative(4.46f, -0.04f, 8.05f, -3.69f, 8.01f, -8.15f)
                arcToRelative(8.123f, 8.123f, 0.0f, false, false, -0.81f, -3.45f)
                lineToRelative(-52.0f, -104.0f)
                arcToRelative(8.067f, 8.067f, 0.0f, false, false, -14.4f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFCC32)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(53.56f, 15.72f)
                lineToRelative(-48.8f, 97.4f)
                curveToRelative(-1.83f, 3.77f, -0.25f, 8.31f, 3.52f, 10.14f)
                curveToRelative(0.99f, 0.48f, 2.08f, 0.74f, 3.18f, 0.76f)
                horizontalLineToRelative(97.5f)
                arcToRelative(7.55f, 7.55f, 0.0f, false, false, 7.48f, -7.62f)
                arcToRelative(7.605f, 7.605f, 0.0f, false, false, -0.78f, -3.28f)
                lineToRelative(-48.7f, -97.4f)
                arcToRelative(7.443f, 7.443f, 0.0f, false, false, -9.93f, -3.47f)
                arcToRelative(7.484f, 7.484f, 0.0f, false, false, -3.47f, 3.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF424242)), stroke = null, fillAlpha = 0.2f, strokeAlpha
                    = 0.2f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(64.36f, 34.02f)
                curveToRelative(4.6f, 0.0f, 8.3f, 3.7f, 8.0f, 8.0f)
                lineToRelative(-3.4f, 48.0f)
                curveToRelative(-0.38f, 2.54f, -2.74f, 4.3f, -5.28f, 3.92f)
                arcToRelative(4.646f, 4.646f, 0.0f, false, true, -3.92f, -3.92f)
                lineToRelative(-3.4f, -48.0f)
                curveToRelative(-0.3f, -4.3f, 3.4f, -8.0f, 8.0f, -8.0f)
                moveToRelative(0.0f, 64.0f)
                curveToRelative(3.31f, 0.0f, 6.0f, 2.69f, 6.0f, 6.0f)
                reflectiveCurveToRelative(-2.69f, 6.0f, -6.0f, 6.0f)
                reflectiveCurveToRelative(-6.0f, -2.69f, -6.0f, -6.0f)
                reflectiveCurveToRelative(2.69f, -6.0f, 6.0f, -6.0f)
            }
            path(fill = linearGradient(0.0f to Color(0xFF424242), 1.0f to Color(0xFF212121), start =
                    Offset(64.36f,32.270035f), end = Offset(64.36f,110.96004f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(64.36f, 34.02f)
                curveToRelative(4.6f, 0.0f, 8.3f, 3.7f, 8.0f, 8.0f)
                lineToRelative(-3.4f, 48.0f)
                curveToRelative(-0.38f, 2.54f, -2.74f, 4.3f, -5.28f, 3.92f)
                arcToRelative(4.646f, 4.646f, 0.0f, false, true, -3.92f, -3.92f)
                lineToRelative(-3.4f, -48.0f)
                curveToRelative(-0.3f, -4.3f, 3.4f, -8.0f, 8.0f, -8.0f)
                close()
            }
            path(fill = linearGradient(0.0f to Color(0xFF424242), 1.0f to Color(0xFF212121), start =
                    Offset(64.36f,36.250015f), end = Offset(64.36f,114.94002f)), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(64.36f, 104.02f)
                moveToRelative(-6.0f, 0.0f)
                arcToRelative(6.0f, 6.0f, 0.0f, true, true, 12.0f, 0.0f)
                arcToRelative(6.0f, 6.0f, 0.0f, true, true, -12.0f, 0.0f)
            }
            path(fill = SolidColor(Color(0xFFFFF170)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(53.56f, 23.02f)
                curveToRelative(-1.2f, 1.5f, -21.4f, 41.0f, -21.4f, 41.0f)
                reflectiveCurveToRelative(-1.8f, 3.0f, 0.7f, 4.7f)
                curveToRelative(2.3f, 1.6f, 4.4f, -0.3f, 5.3f, -1.8f)
                reflectiveCurveToRelative(19.2f, -36.9f, 19.9f, -38.6f)
                curveToRelative(0.6f, -1.87f, 0.18f, -3.91f, -1.1f, -5.4f)
                curveToRelative(-1.3f, -1.2f, -2.6f, -1.0f, -3.4f, 0.1f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFF170)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(31.36f, 75.33f)
                moveToRelative(-3.3f, 0.0f)
                arcToRelative(3.3f, 3.3f, 0.0f, true, true, 6.6f, 0.0f)
                arcToRelative(3.3f, 3.3f, 0.0f, true, true, -6.6f, 0.0f)
            }
        }
        .build()
        return _warning!!
    }

private var _warning: ImageVector? = null
