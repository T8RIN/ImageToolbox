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

val Emoji.HotDog: ImageVector
    get() {
        if (_hotdog != null) {
            return _hotdog!!
        }
        _hotdog = Builder(
            name = "HotDog", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF8AF3C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(31.05f, 41.19f)
                reflectiveCurveToRelative(-2.96f, -22.81f, 9.71f, -21.4f)
                curveToRelative(12.52f, 1.39f, 14.98f, 16.93f, 30.97f, 29.0f)
                curveTo(94.68f, 66.11f, 104.82f, 66.81f, 109.04f, 73.0f)
                curveToRelative(4.22f, 6.19f, 3.1f, 11.54f, 3.1f, 11.54f)
                reflectiveCurveToRelative(-34.49f, 0.99f, -51.81f, -13.8f)
                reflectiveCurveToRelative(-29.28f, -29.55f, -29.28f, -29.55f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEC6B31)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.11f, 13.31f)
                curveToRelative(8.96f, -4.48f, 17.1f, 1.9f, 19.01f, 7.32f)
                curveTo(37.9f, 25.73f, 47.38f, 40.76f, 54.0f, 47.66f)
                curveTo(59.1f, 52.99f, 74.66f, 67.61f, 87.08f, 73.0f)
                curveToRelative(13.94f, 6.05f, 26.19f, 4.79f, 31.68f, 8.73f)
                curveToRelative(5.49f, 3.94f, 6.44f, 14.43f, 3.77f, 17.67f)
                curveToRelative(-2.67f, 3.24f, -9.26f, 6.83f, -19.96f, 2.89f)
                curveToRelative(-10.7f, -3.94f, -45.19f, -11.83f, -61.24f, -28.58f)
                reflectiveCurveTo(8.39f, 26.12f, 9.65f, 22.6f)
                reflectiveCurveToRelative(2.96f, -7.03f, 7.46f, -9.29f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFAF5832)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(15.99f, 45.55f)
                curveToRelative(-3.16f, -5.74f, -7.88f, -10.63f, -7.18f, -19.43f)
                curveToRelative(0.35f, -4.36f, 2.82f, -7.81f, 2.82f, -7.81f)
                reflectiveCurveToRelative(-0.56f, 6.26f, 3.1f, 8.94f)
                reflectiveCurveToRelative(9.01f, 4.5f, 13.09f, 10.28f)
                reflectiveCurveToRelative(9.85f, 20.27f, 28.16f, 34.35f)
                curveToRelative(18.64f, 14.34f, 33.65f, 12.95f, 40.4f, 17.46f)
                curveToRelative(6.76f, 4.5f, 8.31f, 11.83f, 16.33f, 11.68f)
                curveToRelative(8.02f, -0.14f, 10.7f, -3.24f, 10.7f, -3.24f)
                reflectiveCurveToRelative(-2.39f, 8.31f, -11.4f, 9.15f)
                reflectiveCurveToRelative(-16.05f, -3.1f, -19.99f, -4.5f)
                reflectiveCurveToRelative(-43.78f, -13.8f, -54.9f, -27.45f)
                reflectiveCurveToRelative(-19.58f, -26.61f, -21.13f, -29.43f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF5B03C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.5f, 95.11f)
                curveToRelative(18.76f, 14.93f, 41.53f, 22.38f, 55.75f, 18.86f)
                curveToRelative(16.71f, -4.14f, 14.94f, -14.5f, 13.66f, -17.18f)
                curveToRelative(-1.83f, -3.8f, -8.59f, -6.48f, -15.06f, -8.02f)
                curveToRelative(-6.48f, -1.55f, -21.72f, -5.34f, -37.03f, -19.43f)
                curveToRelative(-17.74f, -16.33f, -21.49f, -36.92f, -31.4f, -37.02f)
                curveToRelative(-6.54f, -0.07f, -10.2f, 3.31f, -11.12f, 13.23f)
                curveToRelative(-0.96f, 10.41f, 5.92f, 34.21f, 25.2f, 49.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFE361)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.87f, 14.3f)
                curveToRelative(-2.58f, -1.56f, -4.83f, 0.79f, -5.07f, 5.26f)
                curveToRelative(-0.19f, 3.57f, 1.22f, 6.96f, 5.26f, 8.92f)
                curveToRelative(3.38f, 1.64f, 8.59f, 3.0f, 10.37f, 6.52f)
                reflectiveCurveToRelative(0.19f, 15.49f, 5.26f, 20.51f)
                reflectiveCurveToRelative(16.64f, 3.93f, 21.09f, 8.53f)
                reflectiveCurveToRelative(5.37f, 13.48f, 11.85f, 16.81f)
                curveToRelative(6.48f, 3.33f, 12.39f, -1.36f, 18.35f, 0.47f)
                curveToRelative(5.96f, 1.83f, 10.84f, 10.18f, 11.5f, 11.4f)
                curveToRelative(0.66f, 1.22f, 2.79f, 3.66f, 4.93f, 2.25f)
                curveToRelative(2.34f, -1.55f, 0.19f, -4.27f, -0.56f, -5.63f)
                curveToRelative(-0.75f, -1.36f, -7.09f, -11.73f, -14.92f, -13.89f)
                reflectiveCurveToRelative(-11.83f, 1.74f, -16.42f, -0.19f)
                curveToRelative(-4.6f, -1.92f, -4.74f, -12.44f, -11.54f, -16.94f)
                reflectiveCurveToRelative(-15.77f, -3.0f, -19.8f, -7.18f)
                reflectiveCurveToRelative(-1.04f, -15.21f, -4.08f, -19.71f)
                curveTo(39.66f, 26.36f, 29.0f, 24.23f, 27.7f, 20.82f)
                curveToRelative(-1.08f, -2.76f, 1.27f, -5.25f, -0.83f, -6.52f)
                close()
            }
        }
            .build()
        return _hotdog!!
    }

private var _hotdog: ImageVector? = null
