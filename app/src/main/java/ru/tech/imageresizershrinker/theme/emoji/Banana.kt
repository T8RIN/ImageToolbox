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

val Emoji.Banana: ImageVector
    get() {
        if (_banana != null) {
            return _banana!!
        }
        _banana = Builder(
            name = "Banana", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFF8E00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(119.22f, 95.12f)
                curveToRelative(-1.14f, 2.36f, -5.91f, 2.21f, -8.54f, 1.38f)
                curveToRelative(-6.72f, -2.12f, -10.9f, -7.9f, -10.9f, -7.9f)
                lineTo(65.14f, 73.9f)
                lineToRelative(-10.61f, -5.57f)
                reflectiveCurveToRelative(-2.06f, -3.5f, 2.06f, -6.17f)
                reflectiveCurveToRelative(7.01f, -2.87f, 7.01f, -2.87f)
                lineToRelative(11.94f, 7.6f)
                reflectiveCurveToRelative(7.71f, -6.0f, 15.54f, -6.91f)
                curveToRelative(7.48f, -0.86f, 16.79f, 5.22f, 19.24f, 16.7f)
                curveToRelative(1.99f, 9.31f, 5.14f, 12.59f, 6.09f, 13.8f)
                curveToRelative(0.95f, 1.22f, 3.79f, 2.62f, 2.81f, 4.64f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE4B4)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(77.72f, 32.15f)
                curveTo(74.65f, 21.99f, 70.77f, 10.1f, 64.91f, 6.03f)
                curveToRelative(-5.19f, -3.6f, -10.24f, -2.95f, -12.64f, -1.38f)
                curveToRelative(0.0f, 0.0f, -4.33f, 1.77f, -5.47f, 6.24f)
                curveToRelative(-1.88f, 7.34f, 0.59f, 16.15f, 2.2f, 25.25f)
                curveToRelative(1.61f, 9.1f, 2.45f, 15.36f, 3.18f, 22.67f)
                curveToRelative(0.07f, 0.66f, 12.16f, 22.18f, 12.16f, 22.18f)
                lineToRelative(9.65f, 5.17f)
                reflectiveCurveToRelative(7.37f, -15.94f, 7.26f, -18.19f)
                curveToRelative(-0.1f, -2.26f, -1.29f, -28.4f, -3.53f, -35.82f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE265)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(52.55f, 66.94f)
                curveToRelative(-0.13f, 2.43f, 5.73f, 21.8f, 5.73f, 21.8f)
                lineTo(74.0f, 86.14f)
                reflectiveCurveToRelative(11.15f, -6.6f, 11.46f, -18.57f)
                curveToRelative(0.48f, -18.05f, -2.61f, -26.55f, -4.85f, -33.97f)
                curveToRelative(-3.08f, -10.14f, -9.94f, -23.5f, -15.75f, -27.64f)
                curveToRelative(-3.41f, -2.43f, -7.19f, -2.79f, -8.74f, -2.49f)
                curveToRelative(0.0f, 0.0f, 10.32f, 8.08f, 15.54f, 21.9f)
                reflectiveCurveToRelative(6.45f, 29.79f, 6.45f, 29.79f)
                reflectiveCurveToRelative(-3.84f, 2.83f, -12.9f, 4.13f)
                curveToRelative(-7.0f, 1.01f, -13.04f, -0.7f, -13.04f, -0.7f)
                reflectiveCurveToRelative(0.55f, 5.27f, 0.38f, 8.35f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFA726)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(85.33f, 83.75f)
                lineToRelative(-6.29f, -13.14f)
                lineToRelative(0.34f, -4.68f)
                reflectiveCurveToRelative(1.34f, -3.93f, -7.03f, 2.42f)
                curveToRelative(-3.88f, 2.94f, -6.22f, 8.36f, -6.22f, 8.36f)
                reflectiveCurveToRelative(-1.18f, -2.92f, -5.32f, -6.78f)
                curveToRelative(-3.68f, -3.42f, -8.63f, -5.08f, -8.63f, -5.08f)
                reflectiveCurveToRelative(-10.28f, -2.0f, -11.4f, -1.13f)
                curveToRelative(-7.25f, 5.66f, -12.55f, 12.55f, -12.55f, 12.55f)
                lineTo(22.9f, 96.42f)
                lineToRelative(-8.86f, 5.43f)
                reflectiveCurveToRelative(2.42f, 3.54f, 5.92f, 2.22f)
                curveToRelative(2.73f, -1.03f, 6.11f, -4.12f, 7.93f, -8.01f)
                curveToRelative(2.5f, -5.35f, 7.75f, -15.47f, 10.42f, -18.67f)
                curveToRelative(5.16f, -6.16f, 10.22f, -8.41f, 13.99f, -6.62f)
                curveToRelative(4.29f, 2.03f, 0.86f, 22.29f, 0.86f, 22.29f)
                lineToRelative(-27.58f, 23.21f)
                lineToRelative(-0.47f, 3.16f)
                reflectiveCurveToRelative(4.01f, 3.85f, 15.06f, 3.49f)
                reflectiveCurveToRelative(25.83f, -4.45f, 35.18f, -17.2f)
                curveToRelative(8.25f, -11.24f, 9.98f, -21.97f, 9.98f, -21.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFB803)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.58f, 84.96f)
                curveToRelative(0.24f, 5.55f, -3.22f, 13.96f, -11.22f, 20.97f)
                curveToRelative(-7.99f, 7.01f, -16.43f, 9.33f, -21.67f, 9.73f)
                curveToRelative(-3.8f, 0.29f, -4.93f, -1.88f, -4.93f, -1.88f)
                reflectiveCurveToRelative(7.66f, -7.05f, 13.35f, -12.7f)
                curveToRelative(5.4f, -5.37f, 11.28f, -16.65f, 12.56f, -21.84f)
                curveToRelative(1.28f, -5.19f, 0.12f, -8.68f, 0.12f, -8.68f)
                reflectiveCurveToRelative(2.48f, 0.69f, 6.13f, 4.35f)
                curveToRelative(3.54f, 3.55f, 5.53f, 6.97f, 5.66f, 10.05f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF875B54)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.45f, 118.74f)
                curveToRelative(-0.83f, 1.93f, -3.68f, 2.01f, -4.92f, 0.94f)
                curveToRelative(-1.17f, -1.01f, -2.31f, -3.02f, -1.13f, -5.27f)
                curveToRelative(0.78f, -1.48f, 3.34f, -1.89f, 4.78f, -0.74f)
                reflectiveCurveToRelative(2.02f, 3.32f, 1.27f, 5.07f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEE4B4)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(23.76f, 97.84f)
                curveToRelative(-3.28f, 4.55f, -6.57f, 5.24f, -7.93f, 5.39f)
                curveToRelative(-1.19f, 0.14f, -2.49f, -1.38f, -2.34f, -2.78f)
                curveToRelative(0.15f, -1.4f, 2.45f, -3.37f, 2.73f, -7.67f)
                curveToRelative(0.28f, -4.3f, -0.06f, -20.07f, 8.69f, -29.13f)
                curveToRelative(6.17f, -6.38f, 13.73f, -4.18f, 18.61f, -2.49f)
                curveToRelative(5.5f, 1.9f, 8.67f, 3.7f, 8.67f, 3.7f)
                reflectiveCurveToRelative(-4.5f, -0.31f, -12.26f, 5.81f)
                curveToRelative(-4.51f, 3.56f, -7.16f, 8.45f, -9.66f, 14.06f)
                curveToRelative(-1.8f, 4.06f, -4.2f, 9.91f, -6.51f, 13.11f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEB804)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(111.0f, 109.58f)
                curveToRelative(-0.92f, 1.84f, -3.17f, 2.98f, -5.16f, 2.89f)
                curveToRelative(-4.21f, -0.18f, -8.05f, -2.35f, -12.3f, -7.83f)
                curveToRelative(-6.01f, -7.76f, -11.24f, -26.38f, -15.16f, -31.34f)
                curveToRelative(-2.65f, -3.35f, -5.95f, -5.01f, -5.95f, -5.01f)
                reflectiveCurveToRelative(1.82f, -1.54f, 3.64f, -2.49f)
                curveToRelative(1.82f, -0.95f, 6.22f, -0.5f, 6.22f, -0.5f)
                lineToRelative(18.78f, 30.18f)
                lineToRelative(9.93f, 14.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE4B4)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(100.39f, 68.46f)
                curveToRelative(4.12f, 5.89f, 4.66f, 11.06f, 5.28f, 16.34f)
                curveToRelative(0.56f, 4.77f, 1.71f, 14.8f, 3.44f, 18.07f)
                curveToRelative(1.74f, 3.27f, 3.43f, 5.4f, 1.56f, 7.39f)
                reflectiveCurveToRelative(-7.37f, 1.28f, -11.32f, -3.02f)
                curveToRelative(-3.18f, -3.47f, -4.94f, -7.25f, -7.33f, -12.8f)
                curveToRelative(-2.39f, -5.55f, -5.91f, -18.65f, -10.48f, -24.57f)
                curveToRelative(-2.92f, -3.78f, -5.83f, -3.89f, -5.83f, -3.89f)
                reflectiveCurveToRelative(3.85f, -3.14f, 11.2f, -3.22f)
                curveToRelative(6.59f, -0.07f, 10.05f, 0.8f, 13.48f, 5.7f)
                close()
            }
        }
            .build()
        return _banana!!
    }

private var _banana: ImageVector? = null
