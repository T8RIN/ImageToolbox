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

val Emoji.Die: ImageVector
    get() {
        if (_die != null) {
            return _die!!
        }
        _die = Builder(
            name = "Die", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth =
            128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFCECECE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(33.09f, 16.55f)
                reflectiveCurveToRelative(-11.5f, 4.75f, -14.2f, 6.7f)
                curveToRelative(-3.29f, 2.38f, -7.29f, 5.41f, -9.41f, 9.46f)
                curveToRelative(-2.03f, 3.87f, -2.84f, 6.83f, -2.99f, 11.79f)
                curveToRelative(-0.15f, 4.96f, 5.15f, 46.15f, 5.15f, 46.15f)
                reflectiveCurveToRelative(28.72f, 27.06f, 33.25f, 29.11f)
                reflectiveCurveToRelative(11.21f, 4.56f, 19.23f, 4.41f)
                curveToRelative(8.02f, -0.15f, 13.29f, -2.62f, 16.36f, -4.08f)
                curveToRelative(3.06f, -1.46f, 39.4f, -55.26f, 39.7f, -59.64f)
                reflectiveCurveToRelative(0.59f, -12.73f, 0.41f, -16.26f)
                curveToRelative(-0.19f, -3.62f, -0.93f, -6.51f, -2.83f, -10.16f)
                reflectiveCurveToRelative(-5.02f, -7.21f, -6.91f, -8.82f)
                curveToRelative(-1.9f, -1.6f, -25.75f, -12.31f, -32.97f, -13.93f)
                curveToRelative(-20.86f, -4.65f, -44.79f, 5.27f, -44.79f, 5.27f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEAEAEA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.44f, 29.1f)
                curveToRelative(0.14f, 4.89f, 4.08f, 7.15f, 12.11f, 11.23f)
                reflectiveCurveTo(56.0f, 56.38f, 64.46f, 56.67f)
                curveToRelative(8.46f, 0.29f, 39.82f, -18.77f, 44.35f, -21.7f)
                curveToRelative(4.48f, -2.9f, 5.23f, -7.53f, 1.02f, -10.69f)
                curveToRelative(-5.84f, -4.37f, -31.95f, -16.48f, -37.49f, -18.52f)
                curveToRelative(-2.83f, -1.04f, -10.04f, -2.66f, -15.76f, -0.44f)
                curveToRelative(-4.52f, 1.75f, -33.19f, 15.02f, -35.96f, 16.77f)
                curveToRelative(-2.77f, 1.75f, -5.25f, 4.28f, -5.18f, 7.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.34f, 19.12f)
                curveToRelative(-1.78f, -2.01f, -14.31f, 4.55f, -14.31f, 10.14f)
                curveToRelative(0.0f, 5.42f, 11.37f, 12.32f, 14.22f, 9.76f)
                curveToRelative(2.05f, -1.84f, -5.21f, -7.58f, -4.26f, -10.9f)
                curveToRelative(0.94f, -3.32f, 5.87f, -7.3f, 4.35f, -9.0f)
                close()
                moveTo(46.74f, 48.08f)
                curveToRelative(-1.97f, 2.85f, 10.62f, 8.98f, 18.3f, 8.98f)
                curveToRelative(7.23f, 0.0f, 19.04f, -7.06f, 17.52f, -9.11f)
                curveToRelative(-2.46f, -3.32f, -8.43f, -0.2f, -17.72f, 0.0f)
                curveToRelative(-5.12f, 0.11f, -16.05f, -2.84f, -18.1f, 0.13f)
                close()
                moveTo(92.41f, 39.04f)
                curveToRelative(2.46f, 1.61f, 16.49f, -5.5f, 16.77f, -9.76f)
                reflectiveCurveToRelative(-8.72f, -9.38f, -11.28f, -7.58f)
                curveToRelative(-2.56f, 1.8f, 0.76f, 3.13f, -0.09f, 6.25f)
                reflectiveCurveToRelative(-8.35f, 9.16f, -5.4f, 11.09f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDFDFDF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(6.48f, 45.09f)
                curveToRelative(-0.15f, 5.17f, 0.16f, 36.73f, 0.23f, 41.01f)
                curveToRelative(0.07f, 4.72f, 1.23f, 9.97f, 4.37f, 12.66f)
                reflectiveCurveToRelative(28.57f, 18.82f, 34.29f, 21.29f)
                curveToRelative(8.01f, 3.46f, 15.8f, -0.34f, 16.36f, -8.96f)
                curveToRelative(0.56f, -8.63f, 0.22f, -34.96f, 0.22f, -38.54f)
                reflectiveCurveToRelative(-1.8f, -8.86f, -5.72f, -11.55f)
                curveToRelative(-3.92f, -2.69f, -34.92f, -20.47f, -38.1f, -22.3f)
                curveToRelative(-4.48f, -2.57f, -11.42f, -1.34f, -11.65f, 6.39f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB8B8B8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(70.68f, 61.34f)
                curveToRelative(-3.36f, 2.66f, -4.71f, 6.5f, -4.71f, 10.64f)
                reflectiveCurveToRelative(-0.22f, 36.08f, -0.11f, 39.33f)
                reflectiveCurveToRelative(4.71f, 11.88f, 11.88f, 9.75f)
                curveToRelative(7.41f, -2.2f, 16.81f, -8.85f, 23.53f, -12.89f)
                curveToRelative(6.72f, -4.03f, 11.96f, -7.45f, 14.3f, -9.37f)
                curveToRelative(3.0f, -2.45f, 5.03f, -8.38f, 5.14f, -13.42f)
                curveToRelative(0.11f, -4.93f, -0.03f, -35.79f, -0.12f, -38.21f)
                curveToRelative(-0.22f, -5.68f, -2.52f, -10.37f, -8.68f, -9.03f)
                reflectiveCurveTo(75.5f, 57.53f, 70.68f, 61.34f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFE4E27)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(50.48f, 29.27f)
                arcToRelative(13.81f, 9.93f, 0.0f, true, false, 27.62f, 0.0f)
                arcToRelative(13.81f, 9.93f, 0.0f, true, false, -27.62f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F2F2F)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.49f, 55.55f)
                curveToRelative(1.8f, 5.02f, 1.26f, 10.22f, -2.59f, 11.6f)
                reflectiveCurveToRelative(-8.46f, -2.05f, -10.23f, -6.96f)
                curveToRelative(-1.81f, -5.02f, -1.36f, -10.23f, 2.59f, -11.6f)
                curveToRelative(3.87f, -1.33f, 8.43f, 1.94f, 10.23f, 6.96f)
                close()
                moveTo(51.82f, 99.6f)
                curveToRelative(2.14f, 4.88f, 2.02f, 10.66f, -1.72f, 12.3f)
                curveToRelative(-3.74f, 1.64f, -8.81f, -1.77f, -10.92f, -6.55f)
                curveToRelative(-2.15f, -4.88f, -1.83f, -10.23f, 1.91f, -11.87f)
                curveToRelative(3.75f, -1.64f, 8.59f, 1.24f, 10.73f, 6.12f)
                close()
                moveTo(101.96f, 66.96f)
                curveToRelative(3.27f, 1.86f, 8.53f, -0.45f, 11.24f, -5.19f)
                curveToRelative(2.71f, -4.75f, 2.25f, -10.1f, -1.01f, -11.97f)
                curveToRelative(-3.27f, -1.86f, -8.44f, 0.44f, -11.15f, 5.19f)
                curveToRelative(-2.71f, 4.74f, -2.35f, 10.11f, 0.92f, 11.97f)
                close()
                moveTo(89.04f, 88.7f)
                curveToRelative(3.32f, 1.94f, 8.72f, -0.37f, 11.54f, -5.2f)
                curveToRelative(2.82f, -4.82f, 2.42f, -10.31f, -0.9f, -12.25f)
                reflectiveCurveToRelative(-8.83f, 0.21f, -11.65f, 5.03f)
                curveToRelative(-2.82f, 4.84f, -2.3f, 10.48f, 1.01f, 12.42f)
                close()
                moveTo(76.21f, 110.92f)
                curveToRelative(3.21f, 1.96f, 8.54f, -0.2f, 11.39f, -4.86f)
                reflectiveCurveToRelative(2.55f, -10.03f, -0.66f, -11.99f)
                curveToRelative(-3.21f, -1.96f, -8.18f, -0.58f, -11.46f, 4.61f)
                curveToRelative(-2.92f, 4.61f, -2.48f, 10.28f, 0.73f, 12.24f)
                close()
            }
        }
            .build()
        return _die!!
    }

private var _die: ImageVector? = null
