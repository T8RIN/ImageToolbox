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

val Emoji.Sunglasses: ImageVector
    get() {
        if (_sunglasses != null) {
            return _sunglasses!!
        }
        _sunglasses = Builder(
            name = "Sunglasses", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(52.44f, 53.27f)
                curveToRelative(-1.03f, -1.71f, -3.17f, -5.28f, -18.55f, -5.69f)
                curveToRelative(-10.54f, -0.36f, -16.68f, 0.93f, -18.81f, 3.97f)
                curveToRelative(-0.93f, 1.46f, -1.85f, 5.0f, -0.95f, 13.57f)
                curveToRelative(1.56f, 14.33f, 8.76f, 14.71f, 17.89f, 15.18f)
                lineToRelative(0.69f, 0.04f)
                curveToRelative(0.99f, 0.05f, 1.92f, 0.08f, 2.8f, 0.08f)
                curveToRelative(12.94f, 0.0f, 15.2f, -5.65f, 16.86f, -13.48f)
                curveToRelative(1.48f, -6.67f, 1.51f, -11.26f, 0.07f, -13.67f)
                close()
                moveTo(112.7f, 51.51f)
                curveToRelative(-2.1f, -3.01f, -8.24f, -4.29f, -18.78f, -3.93f)
                curveToRelative(-15.39f, 0.42f, -17.53f, 3.98f, -18.56f, 5.69f)
                curveToRelative(-1.44f, 2.41f, -1.42f, 7.0f, 0.08f, 13.64f)
                curveToRelative(1.67f, 7.85f, 3.92f, 13.49f, 16.86f, 13.49f)
                curveToRelative(0.88f, 0.0f, 1.82f, -0.03f, 2.8f, -0.08f)
                lineToRelative(0.69f, -0.04f)
                curveToRelative(9.12f, -0.47f, 16.33f, -0.85f, 17.89f, -15.18f)
                curveToRelative(0.89f, -8.55f, -0.03f, -12.09f, -0.98f, -13.59f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF616161)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(123.8f, 47.84f)
                reflectiveCurveToRelative(-1.0f, -1.7f, -19.03f, -3.41f)
                curveToRelative(-18.93f, -1.8f, -28.35f, 2.0f, -32.25f, 4.41f)
                curveToRelative(-0.7f, 0.3f, -1.9f, 0.6f, -3.31f, 0.5f)
                horizontalLineToRelative(-0.3f)
                curveToRelative(-1.9f, -0.1f, -3.61f, -0.2f, -5.61f, -0.2f)
                horizontalLineToRelative(-0.1f)
                curveToRelative(-1.1f, 0.0f, -3.51f, 0.1f, -5.21f, 0.2f)
                curveToRelative(-0.8f, 0.1f, -1.5f, 0.0f, -2.1f, -0.2f)
                curveToRelative(-3.61f, -2.4f, -13.02f, -6.61f, -32.75f, -4.71f)
                curveTo(5.1f, 46.14f, 4.1f, 47.84f, 4.1f, 47.84f)
                lineTo(4.0f, 54.65f)
                reflectiveCurveToRelative(2.7f, 0.8f, 4.41f, 2.9f)
                curveToRelative(1.7f, 2.1f, 2.5f, 14.12f, 5.41f, 19.73f)
                curveToRelative(4.4f, 8.72f, 26.14f, 6.52f, 26.14f, 6.52f)
                reflectiveCurveToRelative(8.91f, 0.7f, 13.22f, -6.81f)
                curveToRelative(3.71f, -6.41f, 5.21f, -15.43f, 5.61f, -17.63f)
                curveToRelative(0.9f, -0.9f, 2.6f, -2.0f, 5.11f, -2.1f)
                curveToRelative(2.8f, 0.0f, 4.61f, 1.4f, 5.41f, 2.4f)
                curveToRelative(0.4f, 2.7f, 1.9f, 11.22f, 5.51f, 17.33f)
                curveToRelative(4.31f, 7.51f, 13.22f, 6.81f, 13.22f, 6.81f)
                reflectiveCurveToRelative(21.74f, 2.2f, 26.14f, -6.51f)
                curveToRelative(2.8f, -5.61f, 3.61f, -17.63f, 5.41f, -19.73f)
                curveToRelative(1.7f, -2.2f, 4.41f, -2.9f, 4.41f, -2.9f)
                lineToRelative(-0.2f, -6.82f)
                close()
                moveTo(51.38f, 66.71f)
                curveToRelative(-1.7f, 8.01f, -3.91f, 13.42f, -18.63f, 12.62f)
                curveToRelative(-9.52f, -0.5f, -16.13f, -0.5f, -17.63f, -14.32f)
                curveToRelative(-0.9f, -8.61f, 0.1f, -11.82f, 0.8f, -12.92f)
                curveToRelative(0.7f, -1.0f, 3.21f, -4.01f, 17.93f, -3.51f)
                curveToRelative(14.72f, 0.4f, 16.83f, 3.71f, 17.73f, 5.21f)
                curveToRelative(0.9f, 1.5f, 1.6f, 4.91f, -0.2f, 12.92f)
                close()
                moveTo(112.68f, 65.01f)
                curveToRelative(-1.5f, 13.82f, -8.11f, 13.82f, -17.63f, 14.32f)
                curveToRelative(-14.72f, 0.8f, -16.93f, -4.61f, -18.63f, -12.62f)
                curveToRelative(-1.8f, -8.01f, -1.1f, -11.42f, -0.2f, -12.92f)
                curveToRelative(0.9f, -1.5f, 3.01f, -4.81f, 17.73f, -5.21f)
                curveToRelative(14.72f, -0.5f, 17.23f, 2.5f, 17.93f, 3.51f)
                curveToRelative(0.7f, 1.1f, 1.7f, 4.3f, 0.8f, 12.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, fillAlpha = 0.75f, strokeAlpha
                = 0.75f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(26.82f, 52.22f)
                curveToRelative(4.33f, -0.84f, 5.79f, 1.26f, 5.37f, 3.14f)
                curveToRelative(-0.77f, 3.47f, -4.11f, 1.62f, -9.0f, 4.6f)
                curveToRelative(-2.06f, 1.26f, -2.9f, 2.16f, -3.66f, 2.13f)
                curveToRelative(-0.73f, -0.03f, -1.36f, -0.78f, -1.01f, -2.2f)
                curveToRelative(0.63f, -2.58f, 1.68f, -6.39f, 8.3f, -7.67f)
                close()
                moveTo(83.66f, 53.21f)
                curveToRelative(1.77f, -0.99f, 6.74f, -1.88f, 7.27f, 0.73f)
                curveToRelative(0.54f, 2.69f, -1.99f, 3.43f, -3.78f, 4.04f)
                curveToRelative(-4.23f, 1.44f, -4.93f, 3.49f, -5.81f, 4.05f)
                curveToRelative(-0.59f, 0.37f, -2.33f, 1.21f, -2.14f, -1.34f)
                curveToRelative(0.22f, -3.24f, 1.28f, -5.69f, 4.46f, -7.48f)
                close()
            }
        }
            .build()
        return _sunglasses!!
    }

private var _sunglasses: ImageVector? = null
