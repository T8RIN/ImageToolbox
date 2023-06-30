package ru.tech.imageresizershrinker.presentation.root.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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

val Emoji.Money: ImageVector
    get() {
        if (_money != null) {
            return _money!!
        }
        _money = Builder(
            name = "Money", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(56.48f, 35.92f)
                curveToRelative(0.6f, -2.69f, 0.14f, -1.41f, -3.73f, -7.07f)
                curveToRelative(-3.48f, -5.08f, -2.76f, -12.3f, -6.88f, -15.89f)
                curveToRelative(-2.53f, -2.2f, -5.7f, -4.67f, -17.51f, 1.27f)
                curveToRelative(-4.1f, 2.06f, -12.29f, 8.85f, -21.57f, 11.96f)
                curveToRelative(-7.47f, 2.5f, -3.33f, 5.52f, -1.74f, 6.08f)
                curveToRelative(5.66f, 1.99f, 13.15f, -2.31f, 13.15f, -2.31f)
                reflectiveCurveToRelative(-10.74f, 7.02f, -4.43f, 8.68f)
                curveToRelative(5.12f, 1.35f, 11.57f, -4.8f, 11.57f, -4.8f)
                reflectiveCurveToRelative(-6.32f, 6.7f, -1.15f, 8.42f)
                curveToRelative(3.55f, 1.18f, 10.6f, -5.56f, 10.6f, -5.56f)
                reflectiveCurveToRelative(-4.93f, 6.85f, -2.55f, 8.3f)
                curveToRelative(3.62f, 2.2f, 9.55f, -3.47f, 9.55f, -3.47f)
                reflectiveCurveToRelative(-4.71f, 6.88f, -2.25f, 8.0f)
                curveToRelative(3.68f, 1.68f, 9.14f, -3.04f, 9.14f, -3.04f)
                reflectiveCurveToRelative(6.72f, -5.76f, 7.8f, -10.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.68f, 13.76f)
                reflectiveCurveToRelative(-6.44f, 3.78f, -8.79f, 6.82f)
                curveToRelative(-0.31f, 0.4f, -2.24f, 2.33f, -0.54f, 3.44f)
                curveToRelative(0.43f, 0.28f, 1.71f, 0.1f, 2.47f, -0.36f)
                curveToRelative(3.46f, -2.1f, 9.58f, -7.08f, 10.25f, -6.82f)
                curveToRelative(0.67f, 0.26f, -5.47f, 4.3f, -7.45f, 9.19f)
                curveToRelative(-0.74f, 1.84f, 0.7f, 3.16f, 4.45f, 0.95f)
                curveToRelative(4.08f, -2.4f, 7.59f, -6.63f, 8.18f, -6.61f)
                curveToRelative(0.0f, 0.0f, -1.64f, 2.86f, -3.74f, 5.12f)
                curveToRelative(-2.14f, 2.31f, -3.65f, 4.66f, -2.07f, 5.4f)
                curveToRelative(1.99f, 0.94f, 6.29f, -1.61f, 6.29f, -1.61f)
                reflectiveCurveToRelative(-2.74f, 3.32f, -6.29f, 4.07f)
                curveToRelative(-1.25f, 0.27f, -2.23f, 0.1f, -2.83f, -0.58f)
                curveToRelative(-0.91f, -1.04f, -0.56f, -2.64f, -0.56f, -2.64f)
                curveToRelative(-1.89f, 0.75f, -5.15f, 1.97f, -7.09f, 0.33f)
                curveToRelative(-1.51f, -1.28f, 0.55f, -5.03f, 0.55f, -5.03f)
                reflectiveCurveToRelative(-3.65f, 2.02f, -5.73f, 1.41f)
                curveToRelative(-1.08f, -0.32f, -2.73f, -1.88f, -0.32f, -5.19f)
                curveToRelative(3.8f, -5.17f, 13.22f, -7.89f, 13.22f, -7.89f)
                close()
                moveTo(43.38f, 50.13f)
                curveToRelative(4.17f, -5.42f, 8.34f, -10.83f, 12.51f, -16.25f)
                lineToRelative(-0.36f, -1.56f)
                curveToRelative(-0.18f, -0.55f, -1.82f, -0.62f, -4.09f, 0.69f)
                curveToRelative(-1.52f, 0.88f, -4.31f, 4.04f, -6.1f, 2.3f)
                curveToRelative(-1.24f, -1.2f, 2.32f, -5.49f, 2.32f, -5.49f)
                curveToRelative(-0.49f, 0.01f, -4.44f, 2.65f, -5.07f, 5.3f)
                curveToRelative(-0.54f, 2.25f, -0.02f, 4.89f, 5.06f, 2.32f)
                curveToRelative(0.15f, -0.08f, 0.31f, -0.17f, 0.46f, -0.26f)
                curveToRelative(-0.64f, 1.01f, -1.22f, 2.07f, -1.71f, 3.15f)
                curveToRelative(-1.42f, 3.12f, -2.26f, 6.46f, -3.02f, 9.8f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFF43A047), 0.999f to Color(0xFF66BB6A), start
                    = Offset(81.426506f, 11.156713f), end = Offset(81.426506f, 11.156713f)
                ), stroke =
                null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(81.39f, 11.13f)
                lineToRelative(0.07f, 0.05f)
            }
            path(
                fill = SolidColor(Color(0xFF66BB6A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(87.13f, 70.83f)
                curveToRelative(-10.05f, 15.06f, -18.5f, 31.65f, -37.72f, 36.73f)
                curveToRelative(-0.2f, 0.05f, -0.41f, -0.01f, -0.55f, -0.16f)
                lineToRelative(-31.4f, -34.65f)
                curveToRelative(-0.93f, -1.03f, -0.47f, -2.66f, 0.85f, -3.09f)
                curveToRelative(12.91f, -4.24f, 20.97f, -14.34f, 28.15f, -25.11f)
                curveToRelative(9.9f, -14.83f, 18.38f, -31.15f, 36.99f, -36.5f)
                curveToRelative(0.71f, -0.27f, 1.54f, -0.1f, 2.08f, 0.49f)
                curveToRelative(4.17f, 4.6f, 35.62f, 34.54f, 35.67f, 34.6f)
                curveToRelative(0.02f, 0.01f, -26.36f, 16.13f, -34.07f, 27.69f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(58.11f, 77.71f)
                curveToRelative(-0.68f, -0.68f, -1.49f, -1.02f, -2.43f, -1.01f)
                reflectiveCurveToRelative(-2.16f, 0.39f, -3.67f, 1.14f)
                curveToRelative(-2.05f, 1.09f, -3.83f, 1.6f, -5.32f, 1.55f)
                curveToRelative(-1.49f, -0.06f, -2.83f, -0.68f, -4.01f, -1.87f)
                curveToRelative(-1.21f, -1.21f, -1.85f, -2.56f, -1.91f, -4.04f)
                curveToRelative(-0.07f, -1.48f, 0.44f, -2.91f, 1.53f, -4.29f)
                lineToRelative(-1.91f, -1.92f)
                arcToRelative(0.736f, 0.736f, 0.0f, false, true, 0.0f, -1.03f)
                lineToRelative(0.62f, -0.62f)
                curveToRelative(0.28f, -0.28f, 0.74f, -0.28f, 1.03f, 0.0f)
                lineToRelative(1.92f, 1.93f)
                curveToRelative(1.41f, -1.07f, 2.9f, -1.51f, 4.46f, -1.33f)
                curveToRelative(1.4f, 0.17f, 2.78f, 0.86f, 4.13f, 2.1f)
                curveToRelative(0.26f, 0.24f, 0.26f, 0.66f, 0.01f, 0.91f)
                lineToRelative(-1.14f, 1.14f)
                curveToRelative(-0.24f, 0.24f, -0.63f, 0.24f, -0.88f, 0.01f)
                curveToRelative(-0.9f, -0.78f, -1.83f, -1.23f, -2.78f, -1.33f)
                curveToRelative(-1.11f, -0.12f, -2.09f, 0.24f, -2.93f, 1.08f)
                curveToRelative(-0.88f, 0.88f, -1.33f, 1.78f, -1.34f, 2.69f)
                curveToRelative(-0.01f, 0.92f, 0.39f, 1.79f, 1.2f, 2.6f)
                curveToRelative(0.75f, 0.76f, 1.6f, 1.11f, 2.54f, 1.07f)
                curveToRelative(0.94f, -0.04f, 2.15f, -0.44f, 3.65f, -1.2f)
                reflectiveCurveToRelative(2.77f, -1.23f, 3.85f, -1.42f)
                curveToRelative(1.07f, -0.19f, 2.05f, -0.14f, 2.93f, 0.14f)
                curveToRelative(0.88f, 0.29f, 1.71f, 0.82f, 2.49f, 1.6f)
                curveToRelative(1.24f, 1.25f, 1.87f, 2.63f, 1.88f, 4.14f)
                curveToRelative(0.01f, 1.51f, -0.59f, 3.01f, -1.81f, 4.5f)
                lineToRelative(1.6f, 1.6f)
                curveToRelative(0.28f, 0.28f, 0.28f, 0.74f, 0.0f, 1.03f)
                lineToRelative(-0.61f, 0.61f)
                curveToRelative(-0.28f, 0.28f, -0.74f, 0.28f, -1.03f, 0.0f)
                lineToRelative(-1.6f, -1.6f)
                curveToRelative(-1.5f, 1.24f, -3.07f, 1.83f, -4.7f, 1.75f)
                curveToRelative(-1.45f, -0.07f, -2.84f, -0.69f, -4.16f, -1.86f)
                arcToRelative(0.62f, 0.62f, 0.0f, false, true, -0.01f, -0.91f)
                lineToRelative(1.16f, -1.15f)
                curveToRelative(0.23f, -0.23f, 0.6f, -0.25f, 0.85f, -0.04f)
                curveToRelative(0.89f, 0.77f, 1.81f, 1.16f, 2.76f, 1.18f)
                curveToRelative(1.09f, 0.02f, 2.13f, -0.47f, 3.12f, -1.46f)
                curveToRelative(0.97f, -0.96f, 1.5f, -1.96f, 1.59f, -2.97f)
                curveToRelative(0.07f, -1.01f, -0.28f, -1.92f, -1.08f, -2.72f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(60.67f, 84.74f)
                lineToRelative(-2.1f, 1.17f)
                lineToRelative(-16.23f, -16.66f)
                lineToRelative(-0.55f, -0.56f)
                lineToRelative(2.17f, -1.13f)
                lineToRelative(16.23f, 16.7f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(75.88f, 45.55f)
                moveToRelative(-14.42f, 0.0f)
                arcToRelative(14.42f, 14.42f, 0.0f, true, true, 28.84f, 0.0f)
                arcToRelative(14.42f, 14.42f, 0.0f, true, true, -28.84f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(92.16f, 78.42f)
                curveToRelative(-5.15f, 7.72f, -12.57f, 22.45f, -22.79f, 30.55f)
                curveToRelative(-9.38f, 7.44f, -21.21f, 11.11f, -21.21f, 11.11f)
                lineTo(15.61f, 81.96f)
                lineToRelative(1.35f, -10.46f)
                lineToRelative(31.92f, 35.91f)
                curveToRelative(0.13f, 0.14f, 0.33f, 0.21f, 0.52f, 0.16f)
                curveToRelative(19.23f, -5.08f, 27.68f, -21.67f, 37.73f, -36.73f)
                curveToRelative(7.71f, -11.56f, 19.24f, -23.88f, 34.08f, -27.68f)
                curveToRelative(1.18f, 1.3f, 0.66f, 12.2f, -0.63f, 12.63f)
                curveToRelative(-13.15f, 4.36f, -21.22f, 11.82f, -28.42f, 22.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFC06C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(78.85f, 83.52f)
                lineToRelative(3.02f, 11.15f)
                reflectiveCurveToRelative(9.2f, -13.46f, 10.69f, -16.72f)
                lineTo(89.22f, 67.9f)
                curveToRelative(-4.46f, 5.55f, -10.37f, 15.62f, -10.37f, 15.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(78.07f, 17.01f)
                curveToRelative(0.77f, 1.11f, 2.12f, 2.26f, 4.29f, 2.93f)
                curveToRelative(0.77f, 0.24f, 1.58f, 0.36f, 2.39f, 0.36f)
                curveToRelative(1.72f, 0.0f, 3.16f, -0.53f, 4.13f, -1.02f)
                curveToRelative(2.43f, 2.44f, 5.47f, 5.41f, 8.41f, 8.29f)
                curveToRelative(2.88f, 2.82f, 5.84f, 5.72f, 8.25f, 8.13f)
                curveToRelative(-0.78f, 1.19f, -1.4f, 2.99f, -1.14f, 5.42f)
                curveToRelative(0.12f, 1.12f, 0.91f, 2.26f, 1.75f, 3.19f)
                curveToRelative(-5.32f, 3.42f, -15.53f, 11.02f, -26.0f, 24.42f)
                curveToRelative(-2.21f, 2.83f, -4.37f, 5.97f, -6.45f, 9.0f)
                curveToRelative(-5.18f, 7.54f, -10.53f, 15.32f, -17.65f, 20.08f)
                curveToRelative(-0.84f, -1.37f, -2.39f, -3.03f, -5.16f, -3.72f)
                curveToRelative(-0.67f, -0.17f, -1.33f, -0.25f, -1.96f, -0.25f)
                curveToRelative(-1.78f, 0.0f, -3.18f, 0.66f, -4.18f, 1.44f)
                lineToRelative(-15.1f, -17.36f)
                curveToRelative(1.27f, -0.99f, 2.46f, -2.68f, 2.58f, -5.29f)
                curveToRelative(0.06f, -1.22f, -0.59f, -2.33f, -1.33f, -3.17f)
                curveToRelative(7.36f, -5.45f, 15.13f, -14.14f, 19.31f, -20.38f)
                curveToRelative(0.61f, -0.91f, 1.24f, -1.86f, 1.89f, -2.84f)
                curveToRelative(6.54f, -9.87f, 15.4f, -23.19f, 25.97f, -29.23f)
                moveToRelative(0.79f, -2.34f)
                curveToRelative(-12.72f, 6.3f, -22.85f, 22.71f, -30.06f, 33.45f)
                curveToRelative(-4.72f, 7.03f, -13.25f, 16.2f, -20.6f, 21.15f)
                curveToRelative(0.0f, 0.0f, 2.41f, 1.71f, 2.33f, 3.26f)
                curveToRelative(-0.2f, 4.16f, -3.58f, 4.86f, -3.58f, 4.86f)
                lineToRelative(17.75f, 20.4f)
                reflectiveCurveToRelative(1.3f, -2.28f, 4.22f, -2.28f)
                curveToRelative(0.47f, 0.0f, 0.99f, 0.06f, 1.55f, 0.2f)
                curveToRelative(4.02f, 1.0f, 4.83f, 4.54f, 4.83f, 4.54f)
                curveToRelative(11.34f, -6.6f, 18.19f, -20.27f, 26.17f, -30.5f)
                curveToRelative(10.83f, -13.87f, 21.6f, -21.73f, 27.43f, -25.18f)
                curveToRelative(0.0f, 0.0f, -2.68f, -2.17f, -2.83f, -3.65f)
                curveToRelative(-0.41f, -3.89f, 1.79f, -5.29f, 1.79f, -5.29f)
                curveToRelative(-5.15f, -5.21f, -13.61f, -13.32f, -18.73f, -18.51f)
                curveToRelative(0.0f, 0.0f, -1.86f, 1.46f, -4.39f, 1.46f)
                curveToRelative(-0.6f, 0.0f, -1.24f, -0.08f, -1.89f, -0.28f)
                curveToRelative(-3.68f, -1.11f, -3.99f, -3.63f, -3.99f, -3.63f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFECB3)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(53.82f, 33.32f)
                lineToRelative(-9.73f, 14.71f)
                lineToRelative(34.76f, 35.49f)
                reflectiveCurveToRelative(5.68f, -8.98f, 10.36f, -15.62f)
                lineTo(53.82f, 33.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD69136)), stroke = null, fillAlpha = 0.68f, strokeAlpha
                = 0.68f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(90.62f, 72.12f)
                reflectiveCurveToRelative(-6.86f, 3.49f, -8.73f, 14.13f)
                curveToRelative(-0.71f, 4.02f, -0.01f, 8.42f, -0.01f, 8.42f)
                lineToRelative(5.66f, -8.71f)
                lineToRelative(4.05f, -12.11f)
                lineToRelative(-0.97f, -1.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(88.65f, 74.43f)
                curveToRelative(1.99f, -1.91f, 2.65f, -2.5f, 8.87f, -2.95f)
                curveToRelative(6.14f, -0.45f, 8.96f, -3.14f, 16.72f, -1.53f)
                curveToRelative(3.28f, 0.68f, 8.48f, 3.68f, 7.16f, 15.64f)
                curveToRelative(-0.5f, 4.56f, -0.38f, 15.1f, 1.77f, 24.65f)
                curveToRelative(1.73f, 7.68f, -3.0f, 5.71f, -4.3f, 4.64f)
                curveToRelative(-4.64f, -3.81f, -4.84f, -12.44f, -4.84f, -12.44f)
                reflectiveCurveToRelative(-0.44f, 12.82f, -5.13f, 8.29f)
                curveToRelative(-3.8f, -3.68f, -1.89f, -12.38f, -1.89f, -12.38f)
                reflectiveCurveToRelative(-2.46f, 8.88f, -6.61f, 5.34f)
                curveToRelative(-2.85f, -2.43f, -0.73f, -11.95f, -0.73f, -11.95f)
                reflectiveCurveToRelative(-3.31f, 7.77f, -5.77f, 6.48f)
                curveToRelative(-3.76f, -1.96f, -1.98f, -9.97f, -1.98f, -9.97f)
                reflectiveCurveToRelative(-3.44f, 7.59f, -5.68f, 6.06f)
                curveToRelative(-3.34f, -2.28f, -2.13f, -9.39f, -2.13f, -9.39f)
                reflectiveCurveToRelative(0.98f, -7.08f, 4.54f, -10.49f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(118.34f, 79.28f)
                reflectiveCurveToRelative(0.48f, 6.18f, 0.05f, 9.87f)
                curveToRelative(-0.27f, 2.29f, -0.39f, 4.39f, -2.49f, 4.08f)
                curveToRelative(-0.51f, -0.08f, -1.7f, -1.07f, -1.91f, -4.88f)
                curveToRelative(-0.22f, -4.04f, 0.62f, -9.38f, 0.03f, -9.78f)
                curveToRelative(-0.59f, -0.41f, -0.62f, 6.6f, -3.54f, 10.99f)
                curveToRelative(-1.1f, 1.65f, -2.95f, 1.83f, -3.24f, -2.52f)
                curveToRelative(-0.32f, -4.73f, 2.13f, -10.33f, 1.78f, -10.81f)
                curveToRelative(0.0f, 0.0f, -2.07f, 2.55f, -3.27f, 6.16f)
                curveToRelative(-1.2f, 3.61f, -1.73f, 5.31f, -3.14f, 4.27f)
                curveToRelative(-2.53f, -1.86f, 1.18f, -9.27f, 1.18f, -9.27f)
                reflectiveCurveToRelative(-2.72f, 2.43f, -3.9f, 5.83f)
                curveToRelative(-0.75f, 2.16f, -0.34f, 5.71f, 1.58f, 6.61f)
                curveToRelative(2.26f, 1.06f, 3.58f, -0.83f, 3.58f, -0.83f)
                reflectiveCurveToRelative(0.48f, 6.05f, 3.43f, 5.92f)
                curveToRelative(2.83f, -0.13f, 3.55f, -3.32f, 3.55f, -3.32f)
                reflectiveCurveToRelative(-0.12f, 3.08f, 2.74f, 5.19f)
                curveToRelative(1.39f, 1.03f, 4.62f, 1.2f, 5.02f, -2.88f)
                curveToRelative(0.65f, -6.62f, -1.45f, -14.63f, -1.45f, -14.63f)
                close()
                moveTo(95.07f, 77.11f)
                curveToRelative(-0.61f, -0.15f, -1.49f, 2.6f, -2.59f, 4.15f)
                curveToRelative(-0.42f, 0.58f, -1.32f, 1.77f, -2.7f, 1.67f)
                curveToRelative(-1.38f, -0.1f, -0.29f, -4.16f, -0.61f, -3.94f)
                curveToRelative(-0.32f, 0.22f, -1.61f, 2.39f, -1.1f, 4.91f)
                curveToRelative(0.14f, 0.7f, 0.85f, 1.53f, 1.55f, 1.64f)
                curveToRelative(2.62f, 0.42f, 3.77f, -2.8f, 3.77f, -2.8f)
                reflectiveCurveToRelative(-0.39f, 3.85f, 2.2f, 4.25f)
                curveToRelative(3.13f, 0.48f, 3.77f, -4.45f, 3.49f, -4.63f)
                curveToRelative(-0.28f, -0.18f, -1.29f, 3.1f, -3.52f, 1.7f)
                curveToRelative(-1.83f, -1.16f, 0.12f, -6.79f, -0.49f, -6.95f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF206022)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(15.61f, 81.95f)
                lineToRelative(32.43f, 37.99f)
                reflectiveCurveToRelative(1.45f, -2.15f, 1.78f, -5.5f)
                curveToRelative(0.29f, -2.92f, -0.73f, -6.8f, -0.73f, -6.8f)
                lineTo(16.96f, 71.49f)
                lineToRelative(-1.35f, 10.46f)
                close()
            }
        }
            .build()
        return _money!!
    }

private var _money: ImageVector? = null
