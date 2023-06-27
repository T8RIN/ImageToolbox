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

val Emoji.Key: ImageVector
    get() {
        if (_key != null) {
            return _key!!
        }
        _key = Builder(
            name = "Key", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth =
            128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF9E740B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.77f, 15.16f)
                curveToRelative(-14.5f, -11.48f, -35.93f, -6.24f, -46.72f, 3.2f)
                curveToRelative(-12.08f, 10.56f, -12.42f, 27.25f, -6.13f, 40.88f)
                lineToRelative(-43.58f, 42.77f)
                curveToRelative(-1.77f, 1.74f, -3.59f, 14.19f, -3.5f, 16.64f)
                lineToRelative(5.9f, 4.12f)
                reflectiveCurveToRelative(9.99f, 2.99f, 14.34f, -0.41f)
                reflectiveCurveToRelative(8.17f, -9.56f, 7.72f, -12.33f)
                curveToRelative(-0.26f, -1.59f, 0.08f, -3.07f, 1.11f, -4.08f)
                curveToRelative(1.28f, -1.26f, 3.32f, -1.47f, 5.39f, -0.78f)
                curveToRelative(1.59f, 0.53f, 3.36f, 0.08f, 4.55f, -1.09f)
                lineToRelative(0.42f, -0.41f)
                curveToRelative(1.69f, -1.58f, 1.88f, -3.42f, 1.55f, -4.96f)
                curveToRelative(-0.51f, -2.39f, 0.36f, -4.37f, 1.7f, -6.12f)
                curveToRelative(1.16f, -1.52f, 3.36f, -2.16f, 5.28f, -2.19f)
                curveToRelative(3.46f, -0.05f, 5.7f, -0.79f, 8.15f, -3.19f)
                lineToRelative(6.38f, -6.24f)
                curveToRelative(14.36f, 6.4f, 31.97f, 3.51f, 43.4f, -8.68f)
                curveToRelative(14.66f, -15.64f, 10.91f, -43.77f, -5.96f, -57.13f)
                close()
                moveTo(99.13f, 48.51f)
                curveToRelative(-4.0f, 3.92f, -10.48f, 3.92f, -14.48f, 0.0f)
                reflectiveCurveToRelative(-4.0f, -10.29f, 0.0f, -14.21f)
                curveToRelative(4.0f, -3.92f, 10.48f, -3.92f, 14.48f, 0.0f)
                reflectiveCurveToRelative(4.0f, 10.29f, 0.0f, 14.21f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFCA28)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.77f, 15.16f)
                curveToRelative(-14.88f, -14.88f, -39.0f, -14.88f, -53.88f, 0.0f)
                curveToRelative(-11.39f, 11.39f, -14.05f, 28.18f, -8.01f, 42.11f)
                lineTo(7.83f, 95.08f)
                arcToRelative(9.11f, 9.11f, 0.0f, false, false, -2.67f, 6.76f)
                lineToRelative(0.44f, 11.75f)
                curveToRelative(2.17f, 1.22f, 2.24f, 5.07f, 2.24f, 5.07f)
                lineToRelative(9.9f, 0.76f)
                curveToRelative(2.53f, 0.08f, 4.98f, -0.89f, 6.76f, -2.67f)
                lineToRelative(3.14f, -3.14f)
                curveToRelative(1.96f, -1.96f, 3.07f, -4.73f, 2.58f, -7.45f)
                curveToRelative(-0.31f, -1.69f, 0.03f, -3.27f, 1.1f, -4.34f)
                curveToRelative(1.26f, -1.26f, 3.23f, -1.5f, 5.26f, -0.87f)
                curveToRelative(1.69f, 0.53f, 3.53f, 0.19f, 4.79f, -1.06f)
                lineToRelative(0.43f, -0.43f)
                curveToRelative(1.71f, -1.63f, 1.9f, -3.52f, 1.57f, -5.1f)
                curveToRelative(-0.51f, -2.46f, 0.37f, -4.49f, 1.71f, -6.29f)
                curveToRelative(1.17f, -1.57f, 3.4f, -2.22f, 5.33f, -2.25f)
                curveToRelative(3.49f, -0.05f, 5.76f, -0.81f, 8.23f, -3.28f)
                lineToRelative(5.98f, -5.95f)
                curveToRelative(14.13f, 6.65f, 31.49f, 4.14f, 43.17f, -7.54f)
                curveToRelative(14.86f, -14.89f, 14.86f, -39.01f, -0.02f, -53.89f)
                close()
                moveTo(94.17f, 43.38f)
                curveToRelative(-4.04f, 4.04f, -10.59f, 4.04f, -14.62f, 0.0f)
                curveToRelative(-4.04f, -4.04f, -4.04f, -10.59f, 0.0f, -14.62f)
                curveToRelative(4.04f, -4.04f, 10.59f, -4.04f, 14.62f, 0.0f)
                curveToRelative(4.04f, 4.03f, 4.04f, 10.58f, 0.0f, 14.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDBA010)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(5.6f, 113.59f)
                reflectiveCurveToRelative(1.24f, 0.84f, 2.24f, 5.07f)
                lineToRelative(47.48f, -48.27f)
                curveToRelative(1.55f, -1.7f, -5.4f, -0.82f, -7.48f, 1.25f)
                lineTo(5.6f, 113.59f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFF59D)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.43f, 19.92f)
                curveToRelative(-3.9f, 2.12f, -8.3f, 7.8f, -9.51f, 16.0f)
                curveToRelative(-1.1f, 7.44f, -0.63f, 12.61f, 1.87f, 16.96f)
                curveToRelative(2.83f, 4.91f, 6.68f, 2.31f, 5.13f, -2.47f)
                curveToRelative(-1.02f, -3.15f, -1.28f, -4.34f, -1.42f, -7.68f)
                curveToRelative(-0.11f, -2.61f, 0.16f, -5.24f, 0.9f, -7.74f)
                curveToRelative(1.84f, -6.2f, 6.11f, -10.27f, 7.36f, -12.28f)
                curveToRelative(1.59f, -2.57f, -0.92f, -4.66f, -4.33f, -2.79f)
                close()
                moveTo(33.05f, 79.72f)
                curveToRelative(-2.84f, 2.42f, 0.1f, -5.11f, 2.28f, -7.65f)
                curveTo(38.2f, 68.71f, 45.0f, 61.58f, 46.2f, 60.97f)
                curveToRelative(1.64f, -0.83f, 1.54f, 3.75f, 0.87f, 4.59f)
                curveTo(44.69f, 68.5f, 35.9f, 77.3f, 33.05f, 79.72f)
                close()
            }
        }
            .build()
        return _key!!
    }

private var _key: ImageVector? = null
