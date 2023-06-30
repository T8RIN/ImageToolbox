package ru.tech.imageresizershrinker.presentation.root.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
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

val Emoji.Wood: ImageVector
    get() {
        if (_wood != null) {
            return _wood!!
        }
        _wood = Builder(
            name = "Wood", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF633D35)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(37.29f, 113.26f)
                lineToRelative(-8.42f, 4.48f)
                verticalLineTo(97.58f)
                lineToRelative(10.52f, 11.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFAD7156)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(99.99f, 19.95f)
                curveToRelative(0.0f, 3.02f, 0.51f, 21.49f, 0.51f, 21.49f)
                reflectiveCurveToRelative(0.26f, 1.98f, 1.75f, 2.48f)
                curveToRelative(0.98f, 0.33f, 3.12f, -0.37f, 5.73f, -2.41f)
                curveToRelative(0.35f, -0.27f, 0.82f, -0.31f, 1.2f, -0.09f)
                curveToRelative(1.0f, 0.6f, 3.15f, 1.77f, 4.5f, 5.32f)
                curveToRelative(1.49f, 3.92f, 1.21f, 7.22f, 0.98f, 8.62f)
                curveToRelative(-0.08f, 0.47f, -0.3f, 0.9f, -0.57f, 1.24f)
                curveToRelative(-2.43f, 3.0f, -14.59f, 18.58f, -14.59f, 28.48f)
                curveToRelative(0.0f, 0.0f, 0.2f, 14.45f, 0.2f, 22.95f)
                curveToRelative(0.0f, 7.75f, -13.09f, 14.22f, -30.49f, 15.72f)
                lineToRelative(-9.71f, -2.67f)
                lineToRelative(-11.2f, 2.01f)
                curveToRelative(-3.56f, -0.55f, -6.88f, -1.31f, -9.86f, -2.25f)
                lineToRelative(-7.47f, -21.02f)
                lineToRelative(-2.1f, 17.92f)
                reflectiveCurveToRelative(-5.61f, -5.75f, -5.61f, -9.71f)
                curveToRelative(0.0f, -8.57f, -2.1f, -88.03f, -2.1f, -88.03f)
                lineToRelative(78.83f, -0.05f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFCE8963), 1.0f to Color(0x00D08A61), start =
                    Offset(102.035f, 43.768f), end = Offset(102.017f, 43.832f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(102.25f, 43.91f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCE8963)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(47.0f, 35.58f)
                curveToRelative(-0.33f, 5.56f, -0.91f, 11.07f, -1.53f, 16.57f)
                lineToRelative(-0.93f, 8.23f)
                curveToRelative(-0.29f, 2.74f, -0.67f, 5.45f, -0.59f, 8.08f)
                curveToRelative(0.0f, 2.7f, 0.13f, 5.46f, 0.27f, 8.21f)
                lineToRelative(0.37f, 8.28f)
                curveToRelative(0.24f, 5.53f, 0.45f, 11.06f, 0.41f, 16.63f)
                curveToRelative(-1.2f, -5.44f, -2.05f, -10.93f, -2.74f, -16.44f)
                curveToRelative(-0.68f, -5.52f, -1.21f, -11.02f, -1.31f, -16.67f)
                curveToRelative(-0.02f, -2.91f, 0.4f, -5.71f, 0.78f, -8.46f)
                curveToRelative(0.44f, -2.75f, 0.88f, -5.49f, 1.43f, -8.21f)
                curveToRelative(1.07f, -5.45f, 2.29f, -10.86f, 3.84f, -16.22f)
                close()
                moveTo(34.0f, 63.58f)
                curveToRelative(0.27f, 0.7f, 0.3f, 1.46f, 0.38f, 2.19f)
                curveToRelative(0.05f, 0.74f, 0.1f, 1.48f, 0.12f, 2.22f)
                curveToRelative(0.04f, 1.48f, 0.06f, 2.96f, 0.04f, 4.44f)
                curveToRelative(-0.02f, 2.96f, -0.13f, 5.91f, -0.29f, 8.86f)
                curveToRelative(-0.15f, 2.95f, -0.35f, 5.89f, -0.59f, 8.83f)
                curveToRelative(-0.21f, 2.94f, -0.52f, 5.88f, -0.81f, 8.82f)
                lineToRelative(-0.03f, -0.36f)
                lineToRelative(1.42f, 5.21f)
                lineToRelative(1.35f, 5.23f)
                curveToRelative(0.87f, 3.5f, 1.7f, 7.0f, 2.41f, 10.55f)
                curveToRelative(-1.32f, -3.36f, -2.52f, -6.77f, -3.68f, -10.18f)
                lineToRelative(-1.7f, -5.13f)
                lineTo(31.0f, 99.13f)
                lineToRelative(-0.05f, -0.16f)
                lineToRelative(0.02f, -0.19f)
                curveToRelative(0.25f, -2.93f, 0.46f, -5.87f, 0.74f, -8.81f)
                curveToRelative(0.25f, -2.93f, 0.53f, -5.87f, 0.82f, -8.8f)
                lineToRelative(0.89f, -8.79f)
                lineToRelative(0.43f, -4.4f)
                curveToRelative(0.07f, -0.73f, 0.13f, -1.47f, 0.18f, -2.2f)
                curveToRelative(0.02f, -0.73f, 0.12f, -1.47f, -0.03f, -2.2f)
                close()
                moveTo(58.5f, 96.08f)
                verticalLineToRelative(25.0f)
                lineToRelative(8.0f, 2.0f)
                reflectiveCurveToRelative(-5.0f, -15.0f, -8.0f, -27.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF965B44)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(88.5f, 79.08f)
                curveToRelative(5.68f, -12.95f, 25.68f, -26.93f, 25.68f, -26.93f)
                lineToRelative(0.59f, -0.43f)
                curveToRelative(0.11f, 1.76f, 0.02f, 3.04f, -0.14f, 3.74f)
                curveToRelative(-0.07f, 0.31f, -0.3f, 0.82f, -0.46f, 1.01f)
                curveToRelative(-1.92f, 2.36f, -14.67f, 18.42f, -14.67f, 28.6f)
                curveToRelative(0.0f, 0.0f, 0.2f, 14.45f, 0.2f, 22.95f)
                curveToRelative(0.0f, 3.81f, -4.2f, 9.05f, -13.2f, 12.05f)
                lineToRelative(4.0f, -29.0f)
                lineToRelative(-9.0f, 9.0f)
                curveToRelative(0.0f, 0.01f, 3.01f, -11.88f, 7.0f, -20.99f)
                close()
                moveTo(100.5f, 40.79f)
                curveToRelative(0.26f, 4.05f, 3.08f, 3.13f, 3.08f, 3.13f)
                curveToRelative(-3.24f, 1.52f, -7.94f, 2.37f, -10.66f, 0.56f)
                curveToRelative(-3.0f, -2.0f, -4.42f, -12.39f, -4.42f, -12.39f)
                lineToRelative(2.0f, -6.0f)
                lineToRelative(9.44f, -5.53f)
                lineToRelative(0.56f, 20.23f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFCC80)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(58.48f, 4.27f)
                curveTo(38.43f, 4.73f, 22.5f, 11.73f, 22.5f, 20.3f)
                curveToRelative(0.0f, 8.87f, 17.05f, 16.05f, 38.08f, 16.05f)
                reflectiveCurveToRelative(38.08f, -7.19f, 38.08f, -16.05f)
                curveToRelative(0.0f, -8.53f, -15.78f, -15.51f, -35.71f, -16.02f)
            }
            path(
                fill = SolidColor(Color(0xFFCE8963)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(58.48f, 4.27f)
                curveToRelative(-7.17f, 0.57f, -14.32f, 1.7f, -20.99f, 4.16f)
                curveToRelative(-3.32f, 1.21f, -6.56f, 2.71f, -9.27f, 4.8f)
                curveToRelative(-1.33f, 1.05f, -2.56f, 2.23f, -3.37f, 3.59f)
                curveToRelative(-0.84f, 1.34f, -1.18f, 2.84f, -0.94f, 4.28f)
                curveToRelative(0.24f, 1.43f, 1.05f, 2.79f, 2.16f, 3.95f)
                curveToRelative(1.11f, 1.16f, 2.45f, 2.19f, 3.91f, 3.05f)
                curveToRelative(2.93f, 1.73f, 6.22f, 2.96f, 9.58f, 3.89f)
                curveToRelative(6.75f, 1.86f, 13.83f, 2.64f, 20.89f, 2.63f)
                curveToRelative(7.05f, 0.02f, 14.14f, -0.73f, 20.9f, -2.57f)
                curveToRelative(3.36f, -0.92f, 6.67f, -2.13f, 9.61f, -3.83f)
                curveToRelative(1.47f, -0.85f, 2.84f, -1.84f, 3.96f, -3.0f)
                curveToRelative(1.13f, -1.14f, 2.0f, -2.48f, 2.28f, -3.91f)
                curveToRelative(0.33f, -1.43f, -0.01f, -2.93f, -0.77f, -4.29f)
                curveToRelative(-0.78f, -1.36f, -1.97f, -2.57f, -3.29f, -3.63f)
                curveToRelative(-2.67f, -2.13f, -5.91f, -3.64f, -9.22f, -4.87f)
                curveToRelative(-6.65f, -2.49f, -13.8f, -3.64f, -20.97f, -4.24f)
                curveToRelative(1.8f, -0.03f, 3.6f, -0.02f, 5.41f, 0.04f)
                curveToRelative(1.8f, 0.09f, 3.61f, 0.24f, 5.4f, 0.45f)
                curveToRelative(3.59f, 0.44f, 7.18f, 1.09f, 10.69f, 2.13f)
                curveToRelative(3.49f, 1.08f, 6.95f, 2.47f, 10.05f, 4.66f)
                curveToRelative(1.55f, 1.1f, 2.99f, 2.44f, 4.07f, 4.16f)
                curveToRelative(1.07f, 1.71f, 1.68f, 3.93f, 1.29f, 6.06f)
                curveToRelative(-0.34f, 2.14f, -1.5f, 3.97f, -2.82f, 5.42f)
                curveToRelative(-1.34f, 1.47f, -2.9f, 2.62f, -4.52f, 3.62f)
                curveToRelative(-3.24f, 1.98f, -6.74f, 3.33f, -10.28f, 4.39f)
                curveToRelative(-7.12f, 2.04f, -14.47f, 2.84f, -21.8f, 2.87f)
                curveToRelative(-7.32f, -0.06f, -14.68f, -0.87f, -21.79f, -2.95f)
                curveToRelative(-3.54f, -1.07f, -7.03f, -2.45f, -10.26f, -4.45f)
                curveToRelative(-1.61f, -1.01f, -3.15f, -2.2f, -4.48f, -3.69f)
                curveToRelative(-1.3f, -1.48f, -2.4f, -3.37f, -2.68f, -5.51f)
                curveToRelative(-0.13f, -1.06f, -0.06f, -2.15f, 0.21f, -3.18f)
                curveToRelative(0.26f, -1.03f, 0.74f, -1.96f, 1.29f, -2.81f)
                curveToRelative(1.11f, -1.7f, 2.59f, -2.99f, 4.14f, -4.08f)
                curveTo(30.0f, 9.27f, 33.45f, 7.89f, 36.95f, 6.83f)
                curveToRelative(3.51f, -1.02f, 7.1f, -1.66f, 10.7f, -2.09f)
                curveToRelative(1.8f, -0.2f, 3.6f, -0.35f, 5.41f, -0.43f)
                curveToRelative(1.81f, -0.08f, 3.62f, -0.08f, 5.42f, -0.04f)
                close()
            }
            path(
                fill = radialGradient(
                    0.056f to Color(0xFFFFA726), 0.135f to Color(0xFFFFA726),
                    1.0f to Color(0x19FFA726), center = Offset(60.109764f, 14.419851f), radius =
                    25.54106f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(21.91f, 17.65f)
                curveToRelative(0.0f, 7.03f, 14.33f, 12.73f, 32.02f, 12.73f)
                reflectiveCurveToRelative(32.02f, -5.7f, 32.02f, -12.73f)
                reflectiveCurveTo(79.7f, 2.51f, 53.93f, 4.91f)
                curveToRelative(-17.61f, 1.65f, -32.02f, 5.7f, -32.02f, 12.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF7F441C)), stroke = null, fillAlpha = 0.66f, strokeAlpha
                = 0.66f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(31.08f, 18.62f)
                curveToRelative(-0.01f, 1.39f, 0.81f, 2.64f, 1.79f, 3.61f)
                curveToRelative(1.01f, 0.96f, 2.21f, 1.71f, 3.46f, 2.34f)
                curveToRelative(2.51f, 1.24f, 5.22f, 2.04f, 7.95f, 2.66f)
                curveToRelative(5.48f, 1.15f, 11.1f, 1.56f, 16.68f, 1.49f)
                curveToRelative(5.58f, -0.07f, 11.18f, -0.63f, 16.57f, -1.95f)
                curveToRelative(2.68f, -0.68f, 5.33f, -1.55f, 7.75f, -2.8f)
                curveToRelative(1.2f, -0.62f, 2.33f, -1.38f, 3.26f, -2.26f)
                curveToRelative(0.9f, -0.9f, 1.57f, -1.98f, 1.54f, -3.08f)
                curveToRelative(-0.01f, -1.09f, -0.69f, -2.14f, -1.61f, -3.01f)
                curveToRelative(-0.94f, -0.86f, -2.07f, -1.58f, -3.28f, -2.17f)
                curveToRelative(-2.42f, -1.19f, -5.07f, -1.99f, -7.75f, -2.61f)
                curveToRelative(-2.69f, -0.62f, -5.43f, -1.03f, -8.18f, -1.31f)
                curveToRelative(-2.75f, -0.3f, -5.53f, -0.44f, -8.3f, -0.46f)
                curveToRelative(-5.55f, -0.04f, -11.13f, 0.24f, -16.6f, 1.29f)
                curveToRelative(-2.72f, 0.56f, -5.43f, 1.28f, -7.95f, 2.46f)
                curveToRelative(-1.25f, 0.6f, -2.45f, 1.32f, -3.48f, 2.25f)
                curveToRelative(-0.99f, 0.93f, -1.83f, 2.15f, -1.85f, 3.55f)
                close()
                moveTo(30.86f, 18.62f)
                curveToRelative(-0.02f, -1.48f, 0.83f, -2.79f, 1.83f, -3.79f)
                curveToRelative(1.03f, -1.0f, 2.21f, -1.82f, 3.47f, -2.49f)
                curveToRelative(2.5f, -1.37f, 5.19f, -2.34f, 7.93f, -3.11f)
                curveToRelative(5.48f, -1.52f, 11.18f, -2.27f, 16.87f, -2.33f)
                curveToRelative(2.85f, -0.01f, 5.7f, 0.11f, 8.54f, 0.39f)
                curveToRelative(2.84f, 0.3f, 5.66f, 0.78f, 8.45f, 1.46f)
                curveToRelative(2.78f, 0.71f, 5.53f, 1.61f, 8.14f, 2.97f)
                curveToRelative(1.3f, 0.68f, 2.55f, 1.5f, 3.67f, 2.57f)
                curveToRelative(0.54f, 0.55f, 1.05f, 1.17f, 1.44f, 1.89f)
                curveToRelative(0.39f, 0.72f, 0.64f, 1.57f, 0.63f, 2.44f)
                curveToRelative(-0.01f, 0.87f, -0.27f, 1.71f, -0.67f, 2.42f)
                reflectiveCurveToRelative(-0.91f, 1.32f, -1.46f, 1.85f)
                curveToRelative(-1.12f, 1.04f, -2.38f, 1.83f, -3.69f, 2.47f)
                curveToRelative(-2.61f, 1.29f, -5.36f, 2.13f, -8.14f, 2.77f)
                curveToRelative(-5.56f, 1.25f, -11.24f, 1.71f, -16.9f, 1.67f)
                curveToRelative(-5.65f, -0.1f, -11.32f, -0.71f, -16.79f, -2.13f)
                curveToRelative(-2.73f, -0.71f, -5.43f, -1.61f, -7.94f, -2.91f)
                curveToRelative(-1.26f, -0.64f, -2.45f, -1.43f, -3.49f, -2.4f)
                curveToRelative(-1.01f, -0.98f, -1.88f, -2.26f, -1.89f, -3.74f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB5772C)), stroke = null, fillAlpha = 0.82f, strokeAlpha
                = 0.82f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(37.81f, 17.5f)
                curveToRelative(0.0f, 3.71f, 9.66f, 6.72f, 21.57f, 6.72f)
                reflectiveCurveToRelative(21.57f, -3.01f, 21.57f, -6.72f)
                reflectiveCurveToRelative(-9.66f, -6.72f, -21.57f, -6.72f)
                reflectiveCurveToRelative(-21.57f, 3.01f, -21.57f, 6.72f)
                close()
                moveTo(41.49f, 17.5f)
                curveToRelative(0.0f, -2.78f, 7.62f, -5.04f, 17.03f, -5.04f)
                reflectiveCurveToRelative(17.03f, 2.26f, 17.03f, 5.04f)
                reflectiveCurveToRelative(-7.62f, 5.04f, -17.03f, 5.04f)
                reflectiveCurveToRelative(-17.03f, -2.26f, -17.03f, -5.04f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFF8A5140), 1.0f to Color(0xFF8A5140), start =
                    Offset(69.9f, 16.939f), end = Offset(52.911f, 16.939f)
                ), stroke = null, fillAlpha
                = 0.74f, strokeAlpha = 0.74f, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(52.92f, 16.94f)
                arcToRelative(8.49f, 2.24f, 0.0f, true, false, 16.98f, 0.0f)
                arcToRelative(8.49f, 2.24f, 0.0f, true, false, -16.98f, 0.0f)
                close()
            }
        }
            .build()
        return _wood!!
    }

private var _wood: ImageVector? = null
