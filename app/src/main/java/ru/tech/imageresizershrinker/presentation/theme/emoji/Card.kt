package ru.tech.imageresizershrinker.presentation.theme.emoji

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.presentation.theme.Emoji

val Emoji.Card: ImageVector
    get() {
        if (_card != null) {
            return _card!!
        }
        _card = Builder(
            name = "Card", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFFC107)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(116.34f, 101.95f)
                horizontalLineTo(11.67f)
                curveToRelative(-4.2f, 0.0f, -7.63f, -3.43f, -7.63f, -7.63f)
                verticalLineTo(33.68f)
                curveToRelative(0.0f, -4.2f, 3.43f, -7.63f, 7.63f, -7.63f)
                horizontalLineToRelative(104.67f)
                curveToRelative(4.2f, 0.0f, 7.63f, 3.43f, 7.63f, 7.63f)
                verticalLineToRelative(60.64f)
                curveToRelative(0.0f, 4.2f, -3.43f, 7.63f, -7.63f, 7.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF424242)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(4.03f, 38.88f)
                horizontalLineToRelative(119.95f)
                verticalLineToRelative(16.07f)
                horizontalLineTo(4.03f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.2f, 74.14f)
                horizontalLineTo(13.87f)
                curveToRelative(-0.98f, 0.0f, -1.79f, -0.8f, -1.79f, -1.79f)
                verticalLineToRelative(-8.41f)
                curveToRelative(0.0f, -0.98f, 0.8f, -1.79f, 1.79f, -1.79f)
                horizontalLineTo(114.2f)
                curveToRelative(0.98f, 0.0f, 1.79f, 0.8f, 1.79f, 1.79f)
                verticalLineToRelative(8.41f)
                curveToRelative(-0.01f, 0.98f, -0.81f, 1.79f, -1.79f, 1.79f)
                close()
            }
            path(
                fill = SolidColor(Color(0x00000000)), stroke = SolidColor(Color(0xFF424242)),
                strokeLineWidth = 2.0f, strokeLineCap = Round, strokeLineJoin =
                StrokeJoin.Companion.Round, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(23.98f, 70.49f)
                curveToRelative(0.56f, -1.08f, 0.71f, -2.34f, 1.21f, -3.45f)
                curveToRelative(0.5f, -1.11f, 1.59f, -2.14f, 2.79f, -1.95f)
                curveToRelative(1.11f, 0.18f, 1.8f, 1.29f, 2.21f, 2.33f)
                curveToRelative(0.57f, 1.45f, 0.88f, 3.0f, 0.92f, 4.56f)
                curveToRelative(0.01f, 0.32f, -0.01f, 0.67f, -0.22f, 0.92f)
                curveToRelative(-0.37f, 0.42f, -1.13f, 0.21f, -1.42f, -0.27f)
                curveToRelative(-0.29f, -0.48f, -0.22f, -1.09f, -0.09f, -1.64f)
                curveToRelative(0.62f, -2.55f, 2.62f, -4.72f, 5.11f, -5.54f)
                curveToRelative(0.26f, -0.09f, 0.53f, -0.16f, 0.8f, -0.11f)
                curveToRelative(0.58f, 0.11f, 0.9f, 0.71f, 1.16f, 1.23f)
                curveToRelative(0.61f, 1.19f, 1.35f, 2.32f, 2.2f, 3.35f)
                curveToRelative(0.34f, 0.42f, 0.73f, 0.83f, 1.25f, 0.99f)
                curveToRelative(1.71f, 0.5f, 2.7f, -2.02f, 4.35f, -2.69f)
                curveToRelative(1.98f, -0.8f, 3.91f, 1.29f, 6.01f, 1.68f)
                curveToRelative(3.07f, 0.57f, 4.7f, -1.82f, 7.39f, -2.43f)
                curveToRelative(0.36f, -0.08f, 0.75f, -0.13f, 1.11f, -0.03f)
                curveToRelative(0.66f, 0.19f, 1.07f, 0.82f, 1.46f, 1.39f)
                curveToRelative(0.91f, 1.34f, 2.21f, 2.66f, 3.83f, 2.67f)
                curveToRelative(1.03f, 0.01f, 1.98f, -0.52f, 2.92f, -0.97f)
                curveToRelative(3.33f, -1.59f, 7.26f, -2.25f, 10.74f, -1.03f)
            }
        }
            .build()
        return _card!!
    }

private var _card: ImageVector? = null
