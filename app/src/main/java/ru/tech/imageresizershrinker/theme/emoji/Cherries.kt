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

val Emoji.Cherries: ImageVector
    get() {
        if (_cherries != null) {
            return _cherries!!
        }
        _cherries = Builder(
            name = "Cherries", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFAF0C1A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(79.05f, 78.15f)
                lineToRelative(-13.17f, -2.32f)
                reflectiveCurveToRelative(-0.57f, 0.46f, -2.15f, 2.38f)
                reflectiveCurveToRelative(-3.16f, 5.46f, -3.16f, 5.46f)
                lineToRelative(-31.52f, -6.3f)
                reflectiveCurveToRelative(-14.21f, 9.19f, -10.62f, 27.77f)
                curveToRelative(3.2f, 16.53f, 17.55f, 20.18f, 25.44f, 20.34f)
                curveToRelative(16.68f, 0.34f, 24.36f, -12.12f, 24.36f, -12.12f)
                reflectiveCurveToRelative(16.24f, 12.16f, 31.89f, 1.69f)
                curveToRelative(16.0f, -10.7f, 10.18f, -31.63f, 6.46f, -34.45f)
                curveToRelative(-3.25f, -2.45f, -27.53f, -2.45f, -27.53f, -2.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDC0D27)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.45f, 73.13f)
                curveToRelative(-13.97f, -0.11f, -21.48f, 7.22f, -22.08f, 16.56f)
                curveToRelative(-0.56f, 8.68f, 5.3f, 21.07f, 20.96f, 22.08f)
                curveToRelative(15.66f, 1.01f, 25.8f, -10.7f, 24.34f, -20.39f)
                curveToRelative(-1.31f, -8.6f, -8.91f, -18.14f, -23.22f, -18.25f)
                close()
                moveTo(78.59f, 99.27f)
                reflectiveCurveToRelative(-1.69f, -7.1f, -4.96f, -12.84f)
                curveToRelative(-3.27f, -5.75f, -7.77f, -10.59f, -7.77f, -10.59f)
                reflectiveCurveToRelative(9.16f, -9.16f, 22.79f, -7.13f)
                curveToRelative(13.63f, 2.03f, 19.8f, 14.57f, 19.8f, 14.57f)
                reflectiveCurveToRelative(-1.33f, 11.43f, -9.92f, 16.0f)
                curveToRelative(-12.05f, 6.41f, -19.94f, -0.01f, -19.94f, -0.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF605F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(71.61f, 75.61f)
                curveToRelative(-1.42f, 1.32f, 0.03f, 6.21f, 4.42f, 9.14f)
                curveToRelative(4.39f, 2.93f, 8.87f, 3.7f, 10.68f, 0.55f)
                curveToRelative(1.8f, -3.15f, -0.56f, -5.44f, -3.38f, -6.54f)
                curveToRelative(-1.84f, -0.72f, -3.53f, -0.55f, -5.67f, -2.24f)
                reflectiveCurveToRelative(-4.46f, -2.4f, -6.05f, -0.91f)
                close()
                moveTo(89.32f, 79.09f)
                curveToRelative(-0.78f, 1.88f, 1.97f, 4.03f, 4.03f, 3.0f)
                curveToRelative(2.06f, -1.03f, 2.06f, -3.6f, -0.6f, -4.37f)
                curveToRelative(-2.28f, -0.66f, -3.01f, 0.35f, -3.43f, 1.37f)
                close()
                moveTo(41.4f, 95.89f)
                curveToRelative(1.22f, 3.14f, 5.66f, 1.29f, 8.14f, -0.17f)
                curveToRelative(2.49f, -1.46f, 3.34f, -3.43f, 2.57f, -5.31f)
                reflectiveCurveToRelative(-4.2f, -2.23f, -7.11f, -0.77f)
                curveToRelative(-2.36f, 1.18f, -4.8f, 3.17f, -3.6f, 6.25f)
                close()
                moveTo(30.77f, 78.24f)
                curveToRelative(-3.87f, 2.29f, -4.11f, 6.54f, -3.51f, 7.89f)
                curveToRelative(1.54f, 3.51f, 3.34f, 3.26f, 4.46f, 5.06f)
                curveToRelative(1.11f, 1.8f, 2.82f, 5.33f, 5.23f, 4.97f)
                curveToRelative(3.43f, -0.51f, 3.17f, -4.97f, 2.06f, -7.8f)
                curveToRelative(-0.83f, -2.11f, -1.8f, -4.2f, -1.97f, -6.86f)
                curveToRelative(-0.18f, -2.66f, -0.18f, -6.86f, -6.27f, -3.26f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF759937)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.17f, 19.6f)
                curveToRelative(-1.29f, 3.35f, 1.03f, 5.79f, 3.51f, 7.03f)
                curveToRelative(2.71f, 1.35f, 5.67f, 1.35f, 5.67f, 1.35f)
                reflectiveCurveToRelative(8.51f, 10.13f, 12.43f, 22.56f)
                curveToRelative(3.66f, 11.6f, 4.96f, 25.24f, 4.73f, 27.56f)
                curveToRelative(-0.27f, 2.7f, -0.41f, 5.27f, 0.27f, 6.22f)
                curveToRelative(1.34f, 1.88f, 4.73f, 1.49f, 5.4f, 0.14f)
                curveToRelative(0.95f, -1.89f, -0.14f, -6.22f, -0.14f, -6.22f)
                reflectiveCurveToRelative(-0.41f, -15.67f, -4.32f, -28.1f)
                reflectiveCurveToRelative(-8.92f, -19.59f, -8.92f, -19.59f)
                reflectiveCurveToRelative(16.97f, 4.62f, 27.97f, 14.73f)
                curveTo(78.4f, 60.54f, 80.16f, 69.56f, 80.16f, 69.56f)
                reflectiveCurveToRelative(0.09f, 4.49f, 1.17f, 5.84f)
                curveToRelative(1.08f, 1.35f, 6.02f, 0.54f, 6.16f, -1.49f)
                curveToRelative(0.14f, -2.03f, -1.27f, -5.73f, -1.27f, -5.73f)
                reflectiveCurveToRelative(-4.16f, -14.83f, -21.99f, -28.31f)
                curveTo(48.19f, 27.75f, 28.55f, 23.11f, 28.55f, 23.11f)
                reflectiveCurveToRelative(-2.01f, -3.03f, -5.27f, -4.73f)
                curveToRelative(-2.85f, -1.48f, -6.9f, -1.93f, -8.11f, 1.22f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF366918)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.81f, 23.32f)
                curveToRelative(-0.54f, -1.22f, 1.47f, -7.82f, 11.47f, -13.09f)
                reflectiveCurveToRelative(21.39f, -6.88f, 36.09f, -6.64f)
                curveToRelative(16.51f, 0.27f, 24.18f, 2.8f, 24.49f, 3.79f)
                curveToRelative(0.32f, 1.02f, -7.67f, 6.52f, -7.67f, 6.52f)
                lineToRelative(-26.42f, 7.8f)
                lineToRelative(-25.0f, 3.24f)
                lineToRelative(-12.96f, -1.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF518E30)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(66.65f, 28.92f)
                curveToRelative(21.01f, -3.99f, 32.13f, -20.1f, 32.13f, -21.09f)
                curveToRelative(0.0f, -0.12f, -1.33f, 0.14f, -3.08f, 0.55f)
                curveToRelative(-1.76f, 0.41f, -17.17f, 4.76f, -25.0f, 6.89f)
                curveToRelative(-8.92f, 2.43f, -20.55f, 4.58f, -24.59f, 5.13f)
                curveToRelative(-5.95f, 0.81f, -19.05f, 2.97f, -19.05f, 2.97f)
                reflectiveCurveToRelative(15.9f, 10.05f, 39.59f, 5.55f)
                close()
            }
        }
            .build()
        return _cherries!!
    }

private var _cherries: ImageVector? = null
