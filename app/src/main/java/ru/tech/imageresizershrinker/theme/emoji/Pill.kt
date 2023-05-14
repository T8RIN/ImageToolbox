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

val Emoji.Pill: ImageVector
    get() {
        if (_pill != null) {
            return _pill!!
        }
        _pill = Builder(
            name = "Pill", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.06f, 115.46f)
                curveToRelative(11.38f, 11.38f, 30.01f, 11.38f, 41.4f, 0.0f)
                curveToRelative(11.38f, -11.38f, 11.38f, -30.01f, 0.0f, -41.4f)
                lineTo(84.7f, 43.3f)
                lineTo(43.3f, 84.7f)
                lineToRelative(30.76f, 30.76f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFDD835)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(84.7f, 43.3f)
                lineTo(53.94f, 12.54f)
                curveToRelative(-11.38f, -11.38f, -30.01f, -11.38f, -41.4f, 0.0f)
                curveToRelative(-11.38f, 11.38f, -11.38f, 30.01f, 0.0f, 41.4f)
                lineTo(43.3f, 84.7f)
                lineToRelative(41.4f, -41.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC62828)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(86.819f, 45.423f)
                lineToRelative(-41.4f, 41.401f)
                lineToRelative(-2.122f, -2.12f)
                lineToRelative(41.4f, -41.403f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.65f, strokeAlpha
                = 0.65f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(69.43f, 36.25f)
                curveTo(81.59f, 49.5f, 103.65f, 74.5f, 103.65f, 74.5f)
                curveToRelative(2.86f, 2.61f, 6.79f, 9.34f, 4.22f, 11.66f)
                curveToRelative(-2.74f, 2.48f, -9.12f, -2.75f, -11.98f, -5.36f)
                lineTo(49.22f, 36.89f)
                curveToRelative(-2.85f, -2.61f, -12.39f, -11.06f, -10.53f, -17.01f)
                curveToRelative(3.51f, -11.23f, 17.2f, 1.61f, 30.74f, 16.37f)
                close()
            }
        }
            .build()
        return _pill!!
    }

private var _pill: ImageVector? = null
