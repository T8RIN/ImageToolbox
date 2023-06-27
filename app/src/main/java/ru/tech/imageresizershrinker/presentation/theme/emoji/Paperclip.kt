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

val Emoji.Paperclip: ImageVector
    get() {
        if (_paperclip != null) {
            return _paperclip!!
        }
        _paperclip = Builder(
            name = "Paperclip", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(112.03f, 116.44f)
                curveToRelative(13.51f, -11.74f, 14.42f, -29.75f, 2.22f, -43.78f)
                lineTo(72.16f, 24.24f)
                curveToRelative(-0.74f, -0.86f, -6.94f, 4.53f, -6.19f, 5.39f)
                lineToRelative(42.09f, 48.42f)
                curveToRelative(7.63f, 8.78f, 10.16f, 22.15f, -1.41f, 32.2f)
                curveToRelative(-7.72f, 6.71f, -20.0f, 9.25f, -31.31f, -3.76f)
                lineTo(21.17f, 44.18f)
                curveToRelative(-6.39f, -7.35f, -11.73f, -20.28f, -2.25f, -28.53f)
                curveToRelative(10.27f, -8.93f, 21.53f, 1.78f, 25.89f, 6.8f)
                lineTo(77.3f, 59.84f)
                curveToRelative(4.19f, 4.82f, 8.24f, 14.99f, 4.77f, 18.46f)
                reflectiveCurveToRelative(-10.56f, 6.59f, -21.16f, -4.0f)
                lineTo(41.02f, 51.69f)
                curveToRelative(-0.66f, -0.75f, -6.99f, 4.48f, -6.16f, 5.42f)
                lineToRelative(19.89f, 22.61f)
                curveToRelative(6.2f, 7.03f, 18.4f, 17.24f, 30.47f, 6.75f)
                curveToRelative(9.17f, -7.97f, 8.51f, -20.24f, -1.72f, -32.01f)
                lineTo(51.01f, 17.07f)
                curveTo(38.73f, 2.96f, 24.37f, 0.04f, 13.53f, 9.46f)
                curveToRelative(-11.21f, 9.74f, -11.13f, 25.65f, 0.2f, 38.68f)
                lineToRelative(55.41f, 63.74f)
                curveToRelative(12.39f, 14.26f, 29.63f, 16.09f, 42.89f, 4.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F7889)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(84.81f, 70.48f)
                curveToRelative(0.0f, -5.18f, -3.2f, -10.08f, -5.73f, -12.99f)
                lineToRelative(-32.5f, -37.38f)
                curveToRelative(-3.57f, -4.09f, -8.63f, -8.45f, -15.12f, -8.83f)
                curveToRelative(-8.61f, -0.5f, -12.98f, 4.58f, -12.03f, 3.86f)
                curveToRelative(10.53f, -7.95f, 21.02f, 2.29f, 25.38f, 7.31f)
                lineTo(77.3f, 59.84f)
                curveToRelative(5.21f, 6.58f, 6.99f, 13.7f, 5.32f, 17.76f)
                curveToRelative(0.0f, -0.01f, 2.19f, -1.67f, 2.19f, -7.12f)
                close()
                moveTo(111.76f, 104.23f)
                curveToRelative(7.2f, -9.42f, 4.75f, -20.84f, -1.94f, -28.53f)
                curveToRelative(0.0f, 0.0f, -42.24f, -48.46f, -42.24f, -48.55f)
                curveToRelative(-0.88f, 0.93f, -1.94f, 2.09f, -1.68f, 2.38f)
                lineToRelative(42.14f, 48.52f)
                curveToRelative(5.65f, 6.49f, 9.29f, 17.17f, 3.72f, 26.18f)
                close()
                moveTo(13.49f, 47.86f)
                lineToRelative(55.65f, 64.02f)
                curveToRelative(13.7f, 15.21f, 30.14f, 15.58f, 43.4f, 4.05f)
                curveToRelative(2.58f, -2.24f, 0.14f, -0.21f, -0.67f, 0.38f)
                curveToRelative(-12.89f, 9.37f, -29.3f, 6.64f, -40.96f, -6.78f)
                lineTo(15.5f, 45.79f)
                curveTo(6.17f, 35.06f, 4.48f, 22.37f, 10.45f, 12.78f)
                curveToRelative(-7.88f, 9.41f, -6.91f, 23.64f, 3.04f, 35.08f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F7889)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(85.73f, 85.96f)
                curveToRelative(1.65f, -1.49f, 2.8f, -3.05f, 3.82f, -4.77f)
                curveToRelative(-0.8f, 1.03f, -1.5f, 2.0f, -2.56f, 2.93f)
                curveToRelative(-12.07f, 10.5f, -24.27f, 0.28f, -30.47f, -6.75f)
                lineTo(36.41f, 54.64f)
                reflectiveCurveToRelative(-2.15f, 1.9f, -1.5f, 2.54f)
                lineToRelative(22.28f, 25.13f)
                curveToRelative(6.63f, 6.63f, 17.54f, 13.6f, 28.54f, 3.65f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB9E4EA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(10.41f, 32.56f)
                curveTo(8.47f, 29.33f, 8.4f, 24.81f, 8.97f, 21.9f)
                curveToRelative(0.92f, -4.64f, 2.92f, -8.61f, 6.59f, -11.57f)
                curveToRelative(4.63f, -3.72f, 11.7f, -5.7f, 18.11f, -4.08f)
                curveToRelative(0.0f, 0.0f, 2.44f, 0.57f, 2.06f, 2.2f)
                reflectiveCurveToRelative(-4.24f, 1.0f, -4.24f, 1.0f)
                curveToRelative(-3.75f, -0.81f, -10.09f, 0.14f, -13.99f, 3.28f)
                curveToRelative(-2.56f, 2.06f, -4.54f, 4.93f, -5.46f, 8.11f)
                curveToRelative(-2.5f, 8.67f, 0.88f, 15.91f, -1.63f, 11.72f)
                close()
                moveTo(113.69f, 111.44f)
                curveToRelative(-2.15f, 2.12f, -4.55f, 3.59f, -5.91f, 2.76f)
                curveToRelative(-0.68f, -0.41f, -0.48f, -1.65f, 0.61f, -2.7f)
                curveToRelative(2.62f, -2.5f, 4.7f, -5.17f, 5.2f, -5.94f)
                curveToRelative(5.35f, -8.17f, 5.19f, -13.37f, 3.44f, -20.77f)
                curveToRelative(-0.47f, -2.01f, 0.48f, -2.19f, 1.21f, -1.27f)
                curveToRelative(1.08f, 1.36f, 1.7f, 4.52f, 1.93f, 5.95f)
                curveToRelative(2.18f, 13.88f, -5.5f, 21.01f, -6.48f, 21.97f)
                close()
                moveTo(85.09f, 82.39f)
                curveToRelative(-3.64f, 3.04f, -5.84f, 0.16f, -3.91f, -1.9f)
                curveToRelative(2.49f, -2.65f, 7.22f, -5.92f, 4.1f, -15.37f)
                curveToRelative(-0.52f, -1.57f, 0.17f, -3.14f, 1.61f, -0.54f)
                curveToRelative(4.77f, 8.62f, 1.84f, 14.77f, -1.8f, 17.81f)
                close()
            }
        }
            .build()
        return _paperclip!!
    }

private var _paperclip: ImageVector? = null
