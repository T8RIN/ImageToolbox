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

val Emoji.Medal: ImageVector
    get() {
        if (_medal != null) {
            return _medal!!
        }
        _medal = Builder(
            name = "Medal", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF176CC7)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(69.09f, 4.24f)
                curveToRelative(-1.08f, 0.96f, -9.48f, 17.63f, -9.48f, 17.63f)
                lineToRelative(-6.25f, 25.21f)
                lineToRelative(24.32f, -2.23f)
                reflectiveCurveTo(97.91f, 7.23f, 98.32f, 6.36f)
                curveToRelative(0.73f, -1.58f, 1.12f, -2.23f, -1.67f, -2.23f)
                curveToRelative(-2.79f, -0.01f, -26.55f, -0.79f, -27.56f, 0.11f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFCC417)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(81.68f, 43.29f)
                curveToRelative(-1.21f, -0.65f, -36.85f, -1.21f, -37.69f, 0.0f)
                curveToRelative(-0.76f, 1.1f, -0.65f, 6.13f, -0.28f, 6.78f)
                curveToRelative(0.37f, 0.65f, 12.35f, 6.22f, 12.35f, 6.22f)
                lineToRelative(-0.01f, 2.03f)
                reflectiveCurveToRelative(0.66f, 1.59f, 7.34f, 1.59f)
                reflectiveCurveToRelative(7.37f, -1.35f, 7.37f, -1.35f)
                lineToRelative(0.06f, -2.05f)
                reflectiveCurveToRelative(10.49f, -5.24f, 11.04f, -5.7f)
                curveToRelative(0.56f, -0.47f, 1.03f, -6.87f, -0.18f, -7.52f)
                close()
                moveTo(70.7f, 51.62f)
                reflectiveCurveToRelative(-0.03f, -1.4f, -0.72f, -1.75f)
                curveToRelative(-0.69f, -0.35f, -11.8f, -0.29f, -12.74f, -0.24f)
                curveToRelative(-0.94f, 0.05f, -0.94f, 1.73f, -0.94f, 1.73f)
                lineToRelative(-7.6f, -3.7f)
                verticalLineToRelative(-0.74f)
                lineToRelative(28.3f, 0.2f)
                lineToRelative(0.05f, 0.84f)
                lineToRelative(-6.35f, 3.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFDFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(59.26f, 51.17f)
                curveToRelative(-0.94f, 0.0f, -1.48f, 0.98f, -1.48f, 2.67f)
                curveToRelative(0.0f, 1.58f, 0.54f, 2.91f, 1.73f, 2.81f)
                curveToRelative(0.98f, -0.08f, 1.32f, -1.58f, 1.23f, -2.91f)
                curveToRelative(-0.09f, -1.58f, -0.29f, -2.57f, -1.48f, -2.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFCC417)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.98f, 90.72f)
                curveToRelative(0.0f, 23.96f, 21.66f, 34.63f, 36.06f, 34.12f)
                curveToRelative(15.88f, -0.57f, 34.9f, -12.95f, 33.75f, -35.81f)
                curveTo(97.7f, 67.37f, 79.48f, 57.1f, 63.7f, 57.21f)
                curveToRelative(-18.34f, 0.13f, -34.72f, 12.58f, -34.72f, 33.51f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFA912C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.53f, 120.67f)
                curveToRelative(-0.25f, 0.0f, -0.51f, 0.0f, -0.76f, -0.01f)
                curveToRelative(-7.5f, -0.25f, -14.91f, -3.41f, -20.33f, -8.66f)
                curveToRelative(-5.8f, -5.62f, -8.98f, -13.22f, -8.94f, -21.39f)
                curveToRelative(0.09f, -19.95f, 17.53f, -29.2f, 29.36f, -29.2f)
                horizontalLineToRelative(0.1f)
                curveToRelative(16.03f, 0.07f, 29.19f, 12.53f, 29.56f, 29.42f)
                curveToRelative(0.16f, 7.52f, -2.92f, 15.41f, -8.96f, 21.35f)
                curveToRelative(-5.64f, 5.53f, -13.12f, 8.49f, -20.03f, 8.49f)
                close()
                moveTo(63.84f, 64.73f)
                curveToRelative(-10.61f, 0.0f, -26.3f, 8.68f, -26.34f, 25.88f)
                curveToRelative(-0.03f, 12.86f, 9.93f, 26.08f, 26.52f, 26.64f)
                curveToRelative(6.32f, 0.2f, 12.83f, -2.22f, 18.09f, -7.39f)
                curveToRelative(5.46f, -5.37f, 8.53f, -12.29f, 8.42f, -18.99f)
                curveToRelative(-0.24f, -14.53f, -12.12f, -26.09f, -26.54f, -26.15f)
                curveToRelative(-0.04f, 0.0f, -0.12f, 0.01f, -0.15f, 0.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEFFFA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(57.82f, 60.61f)
                curveToRelative(-0.69f, -0.95f, -8.51f, -0.77f, -15.9f, 6.45f)
                curveToRelative(-7.13f, 6.97f, -7.9f, 13.54f, -6.53f, 13.92f)
                curveToRelative(1.55f, 0.43f, 3.44f, -6.53f, 9.97f, -12.38f)
                curveToRelative(6.0f, -5.36f, 13.84f, -6.1f, 12.46f, -7.99f)
                close()
                moveTo(88.07f, 86.48f)
                curveToRelative(-2.41f, 0.34f, 0.09f, 7.56f, -5.5f, 15.64f)
                curveToRelative(-4.85f, 7.01f, -10.35f, 9.55f, -9.71f, 11.09f)
                curveToRelative(0.86f, 2.06f, 9.67f, -3.07f, 13.75f, -11.43f)
                curveToRelative(3.7f, -7.57f, 3.26f, -15.56f, 1.46f, -15.3f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFA912C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(55.85f, 77.02f)
                curveToRelative(-0.52f, 0.77f, -0.05f, 7.52f, 0.26f, 7.82f)
                curveToRelative(0.6f, 0.6f, 5.16f, -1.55f, 5.16f, -1.55f)
                lineToRelative(-0.17f, 18.05f)
                reflectiveCurveToRelative(-3.35f, -0.04f, -3.7f, 0.09f)
                curveToRelative(-0.69f, 0.26f, -0.6f, 7.22f, -0.09f, 7.56f)
                reflectiveCurveToRelative(14.18f, 0.52f, 14.7f, -0.17f)
                curveToRelative(0.52f, -0.69f, 0.39f, -6.78f, 0.15f, -7.06f)
                curveToRelative(-0.43f, -0.52f, -3.7f, -0.31f, -3.7f, -0.31f)
                reflectiveCurveToRelative(0.28f, -26.58f, 0.19f, -27.43f)
                reflectiveCurveToRelative(-1.03f, -1.38f, -2.15f, -1.12f)
                reflectiveCurveToRelative(-10.32f, 3.62f, -10.65f, 4.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E9DF4)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(25.51f, 3.72f)
                curveToRelative(-0.63f, 0.58f, 23.46f, 43.48f, 23.46f, 43.48f)
                reflectiveCurveToRelative(4.04f, 0.52f, 13.06f, 0.6f)
                reflectiveCurveToRelative(13.49f, -0.52f, 13.49f, -0.52f)
                reflectiveCurveTo(56.79f, 4.15f, 55.67f, 3.72f)
                curveToRelative(-0.55f, -0.22f, -7.97f, -0.3f, -15.22f, -0.38f)
                curveToRelative(-7.26f, -0.09f, -14.34f, -0.18f, -14.94f, 0.38f)
                close()
            }
        }
            .build()
        return _medal!!
    }

private var _medal: ImageVector? = null
