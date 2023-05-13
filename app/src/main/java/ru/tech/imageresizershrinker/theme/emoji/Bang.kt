package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
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

public val Emoji.Bang: ImageVector
    get() {
        if (_bang != null) {
            return _bang!!
        }
        _bang = Builder(name = "Bang", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
                = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = SolidColor(Color(0xFFFFA000)), stroke = SolidColor(Color(0xFFF44336)),
                    strokeLineWidth = 3.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(68.27f, 8.51f)
                lineToRelative(15.84f, 33.16f)
                lineToRelative(35.33f, -31.88f)
                lineToRelative(-18.06f, 45.64f)
                lineToRelative(19.67f, -5.31f)
                lineToRelative(-18.65f, 26.51f)
                lineToRelative(17.03f, 5.87f)
                lineToRelative(-17.4f, 8.27f)
                lineToRelative(16.93f, 29.03f)
                lineToRelative(-32.19f, -19.91f)
                lineToRelative(-6.9f, 21.54f)
                lineTo(69.65f, 98.4f)
                lineToRelative(-29.0f, 21.78f)
                lineToRelative(8.82f, -24.85f)
                lineToRelative(-39.97f, 8.04f)
                lineTo(39.42f, 81.1f)
                lineTo(7.89f, 58.4f)
                lineToRelative(35.96f, 3.11f)
                lineTo(10.6f, 10.52f)
                lineTo(64.0f, 45.51f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFFDE7), 1.0f to Color(0xFFFFF176), center
                    = Offset(77.587f,75.735f), radius = 26.365f), stroke =
                    SolidColor(Color(0xFFFFEB3B)), strokeLineWidth = 2.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(72.63f, 34.77f)
                lineToRelative(9.54f, 24.71f)
                lineToRelative(18.88f, -21.31f)
                lineToRelative(-14.19f, 29.74f)
                lineToRelative(17.21f, -5.17f)
                lineToRelative(-15.95f, 15.85f)
                lineToRelative(13.36f, 4.39f)
                lineToRelative(-12.8f, 1.89f)
                lineToRelative(10.19f, 14.44f)
                lineToRelative(-16.56f, -10.58f)
                lineToRelative(-3.81f, 12.32f)
                lineToRelative(-5.87f, -14.09f)
                lineToRelative(-14.65f, 11.16f)
                lineTo(64.0f, 84.45f)
                lineToRelative(-23.0f, 5.02f)
                lineToRelative(16.95f, -10.28f)
                lineToRelative(-22.11f, -10.63f)
                lineToRelative(24.49f, 2.28f)
                lineToRelative(-20.11f, -31.55f)
                lineTo(71.1f, 60.17f)
                close()
            }
        }
        .build()
        return _bang!!
    }

private var _bang: ImageVector? = null
