package ru.tech.imageresizershrinker.presentation.theme.emoji

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

val Emoji.Diamond: ImageVector
    get() {
        if (_diamond != null) {
            return _diamond!!
        }
        _diamond = Builder(
            name = "Diamond", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE1F5FE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(4.01f, 47.94f)
                lineToRelative(17.48f, -26.51f)
                lineTo(35.03f, 36.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.11f, 68.26f)
                lineTo(4.01f, 47.94f)
                lineTo(35.03f, 36.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF64B5F6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.94f, 43.06f)
                lineTo(35.03f, 36.9f)
                lineToRelative(9.08f, 31.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0288D1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(123.87f, 47.94f)
                lineToRelative(-17.48f, -26.51f)
                lineTo(92.85f, 36.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(83.77f, 68.26f)
                lineToRelative(40.1f, -20.32f)
                lineTo(92.85f, 36.9f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE1F5FE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(63.94f, 43.06f)
                lineToRelative(28.91f, -6.16f)
                lineToRelative(-9.08f, 31.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB2EBF2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(83.77f, 68.26f)
                lineToRelative(-19.83f, -25.2f)
                lineToRelative(-19.83f, 25.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB3E5FC)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(43.0f, 10.06f)
                horizontalLineToRelative(41.88f)
                lineToRelative(21.51f, 11.37f)
                lineTo(92.85f, 36.9f)
                lineToRelative(-28.91f, 6.16f)
                lineToRelative(-28.91f, -6.16f)
                lineToRelative(-13.54f, -15.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1E88E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.94f, 117.27f)
                lineTo(4.01f, 47.94f)
                lineToRelative(40.1f, 20.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB3E5FC)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(63.94f, 117.27f)
                lineToRelative(59.93f, -69.33f)
                lineToRelative(-40.1f, 20.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE1F5FE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(83.77f, 68.26f)
                lineToRelative(-19.83f, 49.01f)
                lineToRelative(-19.83f, -49.01f)
                close()
            }
        }
            .build()
        return _diamond!!
    }

private var _diamond: ImageVector? = null
