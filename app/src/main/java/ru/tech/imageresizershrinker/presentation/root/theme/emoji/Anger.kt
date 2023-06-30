package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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

val Emoji.Anger: ImageVector
    get() {
        if (_anger != null) {
            return _anger!!
        }
        _anger = Builder(
            name = "Anger", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.7f, 40.03f)
                curveToRelative(-27.77f, 0.0f, -38.93f, -27.23f, -38.93f, -27.23f)
                curveToRelative(-1.22f, -2.34f, -1.79f, -5.33f, -0.03f, -7.38f)
                curveToRelative(0.13f, -0.15f, 0.26f, -0.28f, 0.39f, -0.41f)
                curveToRelative(2.19f, -2.02f, 5.49f, -0.86f, 6.61f, 2.11f)
                curveToRelative(2.27f, 6.02f, 11.25f, 18.63f, 31.79f, 18.63f)
                curveToRelative(21.24f, 0.0f, 30.81f, -13.83f, 32.87f, -19.01f)
                curveToRelative(0.43f, -1.09f, 1.18f, -2.01f, 2.17f, -2.4f)
                curveToRelative(1.58f, -0.62f, 3.4f, -0.2f, 4.66f, 1.26f)
                curveToRelative(1.76f, 2.04f, 1.3f, 4.28f, -0.02f, 7.38f)
                curveToRelative(0.0f, 0.0f, -11.74f, 27.05f, -39.51f, 27.05f)
                close()
                moveTo(64.71f, 87.97f)
                curveToRelative(27.77f, 0.0f, 38.93f, 27.23f, 38.93f, 27.23f)
                curveToRelative(1.22f, 2.34f, 1.79f, 5.33f, 0.03f, 7.38f)
                curveToRelative(-0.13f, 0.15f, -0.26f, 0.28f, -0.39f, 0.41f)
                curveToRelative(-2.19f, 2.02f, -5.49f, 0.86f, -6.61f, -2.11f)
                curveToRelative(-2.27f, -6.02f, -11.25f, -18.63f, -31.79f, -18.63f)
                curveToRelative(-21.24f, 0.0f, -30.81f, 13.83f, -32.87f, 19.01f)
                curveToRelative(-0.43f, 1.09f, -1.18f, 2.01f, -2.17f, 2.4f)
                curveToRelative(-1.58f, 0.62f, -3.4f, 0.2f, -4.66f, -1.26f)
                curveToRelative(-1.76f, -2.04f, -1.3f, -4.28f, 0.02f, -7.38f)
                curveToRelative(0.0f, 0.0f, 11.73f, -27.05f, 39.51f, -27.05f)
                close()
                moveTo(87.97f, 63.62f)
                curveToRelative(0.0f, -27.33f, 27.23f, -38.31f, 27.23f, -38.31f)
                curveToRelative(2.34f, -1.2f, 5.33f, -1.76f, 7.38f, -0.03f)
                curveToRelative(0.15f, 0.12f, 0.28f, 0.25f, 0.41f, 0.39f)
                curveToRelative(2.02f, 2.15f, 0.86f, 5.4f, -2.11f, 6.5f)
                curveToRelative(-6.02f, 2.24f, -18.63f, 11.07f, -18.63f, 31.28f)
                curveToRelative(0.0f, 20.9f, 13.83f, 30.32f, 19.01f, 32.34f)
                curveToRelative(1.09f, 0.42f, 2.01f, 1.16f, 2.4f, 2.13f)
                curveToRelative(0.62f, 1.56f, 0.2f, 3.35f, -1.26f, 4.59f)
                curveToRelative(-2.04f, 1.73f, -4.28f, 1.28f, -7.38f, -0.01f)
                curveToRelative(0.0f, -0.01f, -27.05f, -11.56f, -27.05f, -38.88f)
                close()
                moveTo(40.03f, 64.21f)
                curveToRelative(0.0f, 27.33f, -27.23f, 38.31f, -27.23f, 38.31f)
                curveToRelative(-2.34f, 1.2f, -5.33f, 1.76f, -7.38f, 0.03f)
                arcToRelative(3.78f, 3.78f, 0.0f, false, true, -0.41f, -0.39f)
                curveToRelative(-2.02f, -2.15f, -0.86f, -5.4f, 2.11f, -6.5f)
                curveToRelative(6.02f, -2.24f, 18.63f, -11.07f, 18.63f, -31.28f)
                curveToRelative(0.0f, -20.9f, -13.83f, -30.32f, -19.01f, -32.34f)
                curveToRelative(-1.09f, -0.42f, -2.01f, -1.16f, -2.4f, -2.13f)
                curveToRelative(-0.62f, -1.56f, -0.2f, -3.35f, 1.26f, -4.59f)
                curveToRelative(2.04f, -1.73f, 4.28f, -1.28f, 7.38f, 0.01f)
                curveToRelative(0.0f, 0.0f, 27.05f, 11.55f, 27.05f, 38.88f)
                close()
            }
        }
            .build()
        return _anger!!
    }

private var _anger: ImageVector? = null
