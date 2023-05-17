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

public val Emoji.Ball: ImageVector
    get() {
        if (_ball != null) {
            return _ball!!
        }
        _ball = Builder(
            name = "Ball", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFC8C8C8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.95f, 4.06f)
                curveToRelative(-35.52f, -0.5f, -61.9f, 29.92f, -60.13f, 62.45f)
                curveToRelative(2.09f, 38.31f, 29.48f, 57.56f, 61.52f, 57.34f)
                curveToRelative(33.66f, -0.23f, 59.2f, -24.09f, 58.5f, -60.13f)
                curveToRelative(-0.69f, -35.98f, -27.15f, -59.19f, -59.89f, -59.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.86f, 15.21f)
                curveTo(11.0f, 29.46f, -4.78f, 69.85f, 21.7f, 100.41f)
                curveToRelative(3.24f, 3.74f, 8.39f, 6.35f, 12.3f, 8.13f)
                curveToRelative(26.7f, 12.07f, 59.01f, -6.49f, 71.51f, -20.64f)
                curveToRelative(9.92f, -11.23f, 13.57f, -18.68f, 12.39f, -34.01f)
                curveToRelative(-0.91f, -11.92f, -6.01f, -23.43f, -16.58f, -31.94f)
                curveToRelative(-23.9f, -19.28f, -49.67f, -15.8f, -65.46f, -6.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF171717)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 39.79f)
                curveToRelative(-0.09f, -0.05f, -9.34f, -5.36f, -16.52f, -8.02f)
                curveToRelative(-7.48f, -2.77f, -13.0f, -4.26f, -13.06f, -4.27f)
                lineToRelative(1.03f, -3.86f)
                curveToRelative(0.23f, 0.06f, 5.75f, 1.54f, 13.42f, 4.39f)
                curveToRelative(7.5f, 2.78f, 16.74f, 8.08f, 17.13f, 8.31f)
                lineToRelative(-2.0f, 3.45f)
                close()
                moveTo(77.167f, 29.354f)
                lineTo(82.61f, 9.932f)
                lineToRelative(3.852f, 1.079f)
                lineToRelative(-5.442f, 19.422f)
                close()
                moveTo(117.6f, 54.52f)
                curveToRelative(-0.05f, -0.02f, -5.37f, -2.04f, -11.31f, -3.65f)
                curveToRelative(-4.47f, -1.21f, -10.23f, -2.44f, -10.29f, -2.45f)
                lineToRelative(0.83f, -3.91f)
                curveToRelative(0.24f, 0.05f, 5.92f, 1.26f, 10.5f, 2.5f)
                curveToRelative(6.15f, 1.66f, 11.47f, 3.69f, 11.7f, 3.77f)
                lineToRelative(-1.43f, 3.74f)
                close()
                moveTo(86.13f, 93.76f)
                curveToRelative(0.0f, -0.06f, -0.5f, -6.16f, -1.7f, -12.4f)
                curveToRelative(-1.19f, -6.16f, -3.46f, -11.96f, -3.48f, -12.01f)
                lineToRelative(3.72f, -1.47f)
                curveToRelative(0.1f, 0.25f, 2.43f, 6.19f, 3.69f, 12.73f)
                curveToRelative(1.24f, 6.47f, 1.74f, 12.58f, 1.76f, 12.84f)
                lineToRelative(-3.99f, 0.31f)
                close()
                moveTo(106.02f, 99.32f)
                lineToRelative(-2.64f, -3.01f)
                curveToRelative(0.04f, -0.03f, 3.88f, -3.43f, 8.62f, -9.83f)
                curveToRelative(4.38f, -5.92f, 6.4f, -11.1f, 6.42f, -11.16f)
                lineToRelative(3.73f, 1.43f)
                curveToRelative(-0.09f, 0.23f, -2.24f, 5.75f, -6.94f, 12.1f)
                curveToRelative(-5.01f, 6.78f, -9.02f, 10.32f, -9.19f, 10.47f)
                close()
                moveTo(32.847f, 78.88f)
                lineTo(55.99f, 58.223f)
                lineToRelative(2.664f, 2.984f)
                lineTo(35.51f, 81.864f)
                close()
                moveTo(12.91f, 73.35f)
                curveToRelative(-0.06f, -0.39f, -1.38f, -9.64f, -1.38f, -18.1f)
                curveToRelative(0.0f, -8.44f, 0.96f, -16.25f, 1.01f, -16.58f)
                lineToRelative(3.97f, 0.5f)
                curveToRelative(-0.01f, 0.08f, -0.97f, 7.9f, -0.97f, 16.08f)
                curveToRelative(0.0f, 8.16f, 1.33f, 17.44f, 1.34f, 17.53f)
                lineToRelative(-3.97f, 0.57f)
                close()
                moveTo(71.3f, 113.65f)
                curveToRelative(-0.25f, -0.03f, -6.35f, -0.8f, -16.9f, -3.9f)
                curveToRelative(-6.72f, -1.98f, -14.4f, -6.04f, -14.73f, -6.21f)
                lineToRelative(1.88f, -3.53f)
                curveToRelative(0.08f, 0.04f, 7.65f, 4.05f, 13.98f, 5.91f)
                curveToRelative(10.2f, 3.0f, 16.2f, 3.76f, 16.26f, 3.77f)
                lineToRelative(-0.49f, 3.96f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF474C4F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(53.37f, 32.72f)
                curveToRelative(-0.31f, 0.78f, 0.1f, 28.63f, 0.73f, 29.46f)
                reflectiveCurveToRelative(28.63f, 10.34f, 29.88f, 10.03f)
                curveToRelative(1.25f, -0.31f, 16.19f, -23.82f, 16.19f, -24.76f)
                reflectiveCurveTo(81.79f, 23.84f, 80.85f, 23.63f)
                curveToRelative(-0.94f, -0.21f, -27.06f, 8.04f, -27.48f, 9.09f)
                close()
                moveTo(117.36f, 36.14f)
                reflectiveCurveToRelative(-4.12f, 13.71f, -4.12f, 14.76f)
                curveToRelative(0.0f, 1.04f, 2.51f, 6.79f, 3.66f, 13.89f)
                curveToRelative(1.15f, 7.1f, -0.52f, 17.76f, 0.21f, 18.18f)
                curveToRelative(0.73f, 0.42f, 4.35f, 0.21f, 4.35f, 0.21f)
                reflectiveCurveToRelative(3.49f, -10.97f, 2.02f, -25.07f)
                curveToRelative(-1.47f, -14.11f, -6.12f, -21.97f, -6.12f, -21.97f)
                close()
                moveTo(87.64f, 90.7f)
                curveToRelative(-0.84f, 0.08f, -22.88f, 19.01f, -22.78f, 19.54f)
                curveToRelative(0.1f, 0.52f, 8.63f, 13.09f, 8.63f, 13.09f)
                reflectiveCurveToRelative(10.72f, -0.85f, 20.54f, -6.17f)
                reflectiveCurveToRelative(13.55f, -10.05f, 13.55f, -10.05f)
                reflectiveCurveToRelative(1.36f, -12.64f, 0.84f, -12.74f)
                curveToRelative(-0.51f, -0.12f, -19.73f, -3.77f, -20.78f, -3.67f)
                close()
                moveTo(14.09f, 67.09f)
                curveToRelative(-0.57f, 0.47f, -3.87f, 7.73f, -4.81f, 11.81f)
                curveToRelative(-0.94f, 4.07f, -1.46f, 7.94f, -1.46f, 7.94f)
                reflectiveCurveToRelative(2.51f, 7.26f, 6.27f, 12.64f)
                curveToRelative(4.66f, 6.66f, 10.72f, 11.36f, 10.72f, 11.36f)
                reflectiveCurveToRelative(19.78f, -6.03f, 19.89f, -6.87f)
                reflectiveCurveToRelative(-2.3f, -8.15f, -3.97f, -14.21f)
                curveToRelative(-1.67f, -6.06f, -1.98f, -12.85f, -2.61f, -13.27f)
                curveToRelative(-0.63f, -0.42f, -7.0f, -2.4f, -12.01f, -4.39f)
                reflectiveCurveToRelative(-11.39f, -5.53f, -12.02f, -5.01f)
                close()
                moveTo(7.89f, 41.88f)
                reflectiveCurveToRelative(5.47f, 4.73f, 6.31f, 4.73f)
                curveToRelative(0.84f, 0.0f, 6.14f, -7.02f, 11.49f, -11.28f)
                curveToRelative(7.21f, -5.75f, 10.76f, -7.63f, 10.87f, -8.36f)
                curveToRelative(0.1f, -0.73f, 3.24f, -18.18f, 3.24f, -18.18f)
                reflectiveCurveToRelative(-11.6f, 4.39f, -20.06f, 14.1f)
                reflectiveCurveTo(7.89f, 41.88f, 7.89f, 41.88f)
                close()
                moveTo(67.05f, 4.13f)
                reflectiveCurveToRelative(6.67f, 3.43f, 9.1f, 5.12f)
                curveToRelative(2.43f, 1.69f, 6.01f, 5.27f, 7.07f, 5.38f)
                curveToRelative(1.05f, 0.11f, 15.56f, 0.12f, 15.56f, 0.12f)
                reflectiveCurveToRelative(-6.58f, -4.5f, -13.87f, -7.08f)
                curveToRelative(-8.77f, -3.11f, -17.86f, -3.54f, -17.86f, -3.54f)
                close()
            }
        }
            .build()
        return _ball!!
    }

private var _ball: ImageVector? = null
