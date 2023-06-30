package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.root.theme.Emoji

val Emoji.Alienmonster: ImageVector
    get() {
        if (_alienmonster != null) {
            return _alienmonster!!
        }
        _alienmonster = Builder(
            name = "Alienmonster", defaultWidth = 1.0.dp, defaultHeight =
            1.0.dp, viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF995AA8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(30.47f, 104.24f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.39f)
                lineTo(30.47f, 117.63f)
                close()
                moveTo(84.04f, 104.24f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.39f)
                lineTo(84.04f, 117.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB574C3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(30.48f, 10.51f)
                horizontalLineToRelative(13.39f)
                lineTo(43.87f, 23.9f)
                lineTo(30.48f, 23.9f)
                close()
                moveTo(84.04f, 10.51f)
                horizontalLineToRelative(13.39f)
                lineTo(97.43f, 23.9f)
                lineTo(84.04f, 23.9f)
                close()
            }
            path(
                fill = radialGradient(
                    0.508f to Color(0xFFB574C3), 0.684f to Color(0xFFB070BF),
                    0.878f to Color(0xFFA363B2), 0.981f to Color(0xFF995AA8), center =
                    Offset(64.344f, 9.403f), radius = 83.056f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(97.46f, 64.08f)
                lineTo(97.46f, 37.3f)
                lineTo(84.04f, 37.3f)
                lineTo(84.04f, 23.9f)
                lineTo(70.65f, 23.9f)
                verticalLineToRelative(13.4f)
                lineTo(57.26f, 37.3f)
                lineTo(57.26f, 23.9f)
                lineTo(43.87f, 23.9f)
                verticalLineToRelative(13.4f)
                lineTo(30.48f, 37.3f)
                verticalLineToRelative(26.78f)
                lineTo(17.09f, 64.08f)
                verticalLineToRelative(13.39f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.4f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.38f)
                horizontalLineToRelative(13.39f)
                lineTo(57.26f, 90.87f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.38f)
                horizontalLineToRelative(13.39f)
                lineTo(84.04f, 90.87f)
                horizontalLineToRelative(13.42f)
                verticalLineToRelative(-13.4f)
                horizontalLineToRelative(13.37f)
                lineTo(110.83f, 64.08f)
                lineTo(97.46f, 64.08f)
                close()
                moveTo(57.25f, 64.08f)
                lineTo(43.86f, 64.08f)
                lineTo(43.86f, 50.69f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.39f)
                close()
                moveTo(84.03f, 64.08f)
                lineTo(70.64f, 64.08f)
                lineTo(70.64f, 50.69f)
                horizontalLineToRelative(13.39f)
                verticalLineToRelative(13.39f)
                close()
            }
            path(
                fill = radialGradient(
                    0.508f to Color(0xFFB574C3), 0.684f to Color(0xFFB070BF),
                    0.878f to Color(0xFFA363B2), 0.981f to Color(0xFF995AA8), center =
                    Offset(63.118f, 24.114f), radius = 65.281f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(110.82f, 37.29f)
                horizontalLineToRelative(13.4f)
                verticalLineToRelative(26.8f)
                horizontalLineToRelative(-13.4f)
                close()
            }
            path(
                fill = radialGradient(
                    0.508f to Color(0xFFB574C3), 0.684f to Color(0xFFB070BF),
                    0.878f to Color(0xFFA363B2), 0.981f to Color(0xFF995AA8), center =
                    Offset(62.811f, 13.081f), radius = 75.09f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(3.7f, 37.28f)
                horizontalLineToRelative(13.4f)
                verticalLineToRelative(26.8f)
                horizontalLineTo(3.7f)
                close()
            }
        }
            .build()
        return _alienmonster!!
    }

private var _alienmonster: ImageVector? = null
