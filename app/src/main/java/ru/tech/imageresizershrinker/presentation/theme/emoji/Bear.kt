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

val Emoji.Bear: ImageVector
    get() {
        if (_bear != null) {
            return _bear!!
        }
        _bear = Builder(
            name = "Bear", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF855B52)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.29f, 9.93f)
                curveToRelative(14.92f, 0.22f, 16.4f, 3.61f, 20.77f, 3.73f)
                curveToRelative(6.32f, 0.18f, 15.98f, -11.97f, 29.07f, -0.99f)
                curveToRelative(12.9f, 10.82f, 2.39f, 27.8f, 2.39f, 27.8f)
                reflectiveCurveToRelative(7.18f, 12.72f, 8.02f, 32.1f)
                curveToRelative(0.61f, 14.03f, -4.79f, 31.49f, -29.0f, 41.39f)
                curveToRelative(-22.29f, 9.12f, -55.55f, 7.3f, -75.88f, -8.31f)
                curveTo(2.9f, 93.56f, 4.01f, 75.92f, 4.44f, 69.91f)
                curveToRelative(1.13f, -15.77f, 8.02f, -28.44f, 8.02f, -28.44f)
                reflectiveCurveToRelative(-10.19f, -20.15f, 2.25f, -29.7f)
                curveToRelative(13.66f, -10.49f, 23.38f, 1.86f, 29.0f, 1.41f)
                curveToRelative(4.09f, -0.34f, 5.43f, -3.46f, 19.58f, -3.25f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB99277)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.98f, 20.77f)
                curveToRelative(-0.19f, -1.11f, -7.43f, -8.29f, -14.92f, -0.99f)
                curveToRelative(-5.77f, 5.63f, 0.0f, 15.42f, 0.99f, 14.64f)
                curveToRelative(0.76f, -0.6f, 3.56f, -4.21f, 6.83f, -7.32f)
                curveToRelative(3.35f, -3.19f, 7.17f, -5.9f, 7.1f, -6.33f)
                close()
                moveTo(92.5f, 20.84f)
                curveToRelative(-0.05f, 0.62f, 3.81f, 2.95f, 7.32f, 6.26f)
                curveToRelative(3.55f, 3.35f, 6.75f, 7.67f, 7.32f, 7.6f)
                curveToRelative(1.13f, -0.14f, 5.98f, -9.64f, -0.35f, -14.99f)
                reflectiveCurveToRelative(-14.15f, -0.7f, -14.29f, 1.13f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF2A258)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.51f, 60.38f)
                curveToRelative(14.64f, 0.0f, 23.42f, 16.52f, 22.9f, 26.84f)
                curveToRelative(-0.47f, 9.39f, -5.82f, 17.18f, -23.84f, 17.08f)
                curveToRelative(-18.02f, -0.09f, -22.59f, -8.36f, -22.9f, -17.18f)
                curveToRelative(-0.28f, -7.88f, 5.35f, -26.74f, 23.84f, -26.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF171716)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.92f, 59.7f)
                curveToRelative(0.33f, 4.16f, -1.9f, 7.95f, -6.17f, 7.63f)
                curveToRelative(-4.12f, -0.32f, -7.13f, -3.66f, -7.13f, -7.84f)
                reflectiveCurveToRelative(2.22f, -7.14f, 5.56f, -7.6f)
                curveToRelative(4.08f, -0.57f, 7.27f, 1.9f, 7.74f, 7.81f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F3030)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.55f, 76.99f)
                curveToRelative(-0.34f, 4.16f, -3.1f, 8.61f, -11.12f, 8.33f)
                curveToRelative(-6.73f, -0.24f, -9.9f, -4.72f, -10.0f, -8.89f)
                curveToRelative(-0.14f, -5.89f, 3.0f, -8.06f, 10.61f, -8.17f)
                curveToRelative(8.68f, -0.11f, 10.98f, 3.01f, 10.51f, 8.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF171716)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.37f, 61.25f)
                curveToRelative(-0.43f, 4.15f, -4.33f, 7.06f, -7.98f, 6.17f)
                curveToRelative(-4.01f, -0.98f, -5.81f, -5.2f, -4.97f, -9.29f)
                curveToRelative(0.75f, -3.66f, 3.96f, -6.51f, 7.6f, -6.1f)
                curveToRelative(3.8f, 0.42f, 5.89f, 3.99f, 5.35f, 9.22f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F3030)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(63.99f, 95.04f)
                curveToRelative(5.49f, 0.0f, 8.49f, -3.03f, 9.34f, -3.87f)
                reflectiveCurveToRelative(1.99f, -2.28f, 3.75f, -0.84f)
                curveToRelative(1.5f, 1.22f, 0.02f, 3.12f, -0.66f, 3.85f)
                curveToRelative(-1.22f, 1.31f, -4.9f, 5.31f, -12.93f, 4.95f)
                curveToRelative(-7.88f, -0.35f, -10.91f, -3.92f, -11.76f, -4.76f)
                curveToRelative(-0.84f, -0.84f, -2.25f, -3.0f, -0.66f, -4.22f)
                curveToRelative(1.91f, -1.46f, 2.53f, 0.19f, 4.5f, 1.78f)
                reflectiveCurveToRelative(3.21f, 3.11f, 8.42f, 3.11f)
                close()
            }
        }
            .build()
        return _bear!!
    }

private var _bear: ImageVector? = null
