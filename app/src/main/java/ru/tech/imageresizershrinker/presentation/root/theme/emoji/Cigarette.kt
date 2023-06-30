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

val Emoji.Cigarette: ImageVector
    get() {
        if (_cigarette != null) {
            return _cigarette!!
        }
        _cigarette = Builder(
            name = "Cigarette", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(38.42f, 78.77f)
                curveToRelative(5.97f, -2.61f, 28.73f, -6.41f, 29.91f, -13.89f)
                curveToRelative(1.91f, -12.04f, -17.87f, -12.89f, -26.62f, -13.39f)
                curveToRelative(-10.32f, -0.58f, -18.8f, -4.0f, -25.62f, -10.34f)
                curveToRelative(-9.8f, -8.6f, -13.95f, -29.48f, -1.61f, -37.32f)
                curveToRelative(0.0f, 0.0f, -0.63f, 10.39f, 6.98f, 14.37f)
                curveToRelative(18.82f, 9.84f, 42.26f, -0.56f, 61.93f, 6.9f)
                curveToRelative(10.99f, 4.04f, 17.66f, 15.67f, 13.88f, 26.95f)
                curveToRelative(-2.26f, 10.74f, -16.98f, 20.54f, -31.06f, 22.71f)
                curveToRelative(-18.68f, 2.87f, -27.79f, 4.01f, -27.79f, 4.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(118.59f, 119.9f)
                horizontalLineTo(18.16f)
                verticalLineTo(96.78f)
                horizontalLineToRelative(100.43f)
                reflectiveCurveToRelative(5.41f, 1.95f, 5.41f, 11.56f)
                reflectiveCurveToRelative(-5.41f, 11.56f, -5.41f, 11.56f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFBDBDBD), 1.0f to Color(0x00BDBDBD), start =
                    Offset(71.08f, 126.585f), end = Offset(71.08f, 112.254f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(118.59f, 119.9f)
                horizontalLineTo(18.16f)
                verticalLineTo(96.78f)
                horizontalLineToRelative(100.43f)
                reflectiveCurveToRelative(5.41f, 1.95f, 5.41f, 11.56f)
                reflectiveCurveToRelative(-5.41f, 11.56f, -5.41f, 11.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF19534)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(118.59f, 96.78f)
                horizontalLineTo(92.77f)
                reflectiveCurveToRelative(5.41f, 1.95f, 5.41f, 11.56f)
                reflectiveCurveToRelative(-5.41f, 11.56f, -5.41f, 11.56f)
                horizontalLineToRelative(25.82f)
                reflectiveCurveToRelative(5.41f, -1.95f, 5.41f, -11.56f)
                reflectiveCurveToRelative(-5.41f, -11.56f, -5.41f, -11.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFCC80)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(123.57f, 112.62f)
                curveToRelative(-0.01f, -0.06f, -0.09f, -0.35f, -0.58f, -0.77f)
                curveToRelative(-0.49f, -0.42f, -1.08f, -0.67f, -1.73f, -0.6f)
                curveToRelative(-0.2f, 0.02f, -0.4f, 0.07f, -0.55f, 0.2f)
                curveToRelative(-0.26f, 0.21f, -0.33f, 0.57f, -0.28f, 0.9f)
                curveToRelative(0.05f, 0.32f, 0.94f, 1.86f, 1.07f, 5.12f)
                curveToRelative(0.81f, -1.11f, 1.6f, -2.67f, 2.07f, -4.85f)
                close()
                moveTo(106.16f, 109.54f)
                curveToRelative(0.41f, 0.4f, 0.82f, 0.85f, 0.9f, 1.42f)
                curveToRelative(0.09f, 0.64f, -0.26f, 1.25f, -0.65f, 1.76f)
                curveToRelative(-0.44f, 0.56f, -0.94f, 1.07f, -1.51f, 1.5f)
                curveToRelative(-0.21f, 0.16f, -0.45f, 0.32f, -0.71f, 0.31f)
                curveToRelative(-0.51f, -0.02f, -0.76f, -0.63f, -0.87f, -1.13f)
                curveToRelative(-0.43f, -1.82f, -0.76f, -3.66f, -1.0f, -5.52f)
                curveToRelative(-0.02f, -0.17f, -0.04f, -0.36f, 0.07f, -0.49f)
                curveToRelative(0.73f, -0.93f, 3.36f, 1.76f, 3.77f, 2.15f)
                close()
                moveTo(114.26f, 107.26f)
                curveToRelative(0.58f, -0.34f, 1.13f, -0.74f, 1.55f, -1.26f)
                curveToRelative(0.42f, -0.53f, 0.68f, -1.19f, 0.61f, -1.86f)
                curveToRelative(-0.02f, -0.19f, -0.08f, -0.4f, -0.24f, -0.5f)
                curveToRelative(-0.17f, -0.1f, -0.38f, -0.05f, -0.57f, 0.0f)
                curveToRelative(-1.27f, 0.35f, -2.52f, 0.77f, -3.74f, 1.27f)
                curveToRelative(-0.17f, 0.07f, -0.35f, 0.15f, -0.48f, 0.29f)
                curveToRelative(-0.13f, 0.14f, -0.19f, 0.34f, -0.25f, 0.52f)
                curveToRelative(-0.14f, 0.51f, -0.21f, 1.03f, -0.23f, 1.55f)
                curveToRelative(-0.07f, 2.13f, 2.43f, 0.54f, 3.35f, -0.01f)
                close()
                moveTo(107.21f, 100.66f)
                curveToRelative(-0.05f, 0.35f, -0.41f, 0.57f, -0.73f, 0.72f)
                curveToRelative(-0.84f, 0.37f, -1.76f, 0.61f, -2.67f, 0.55f)
                curveToRelative(-1.21f, -0.07f, -4.43f, -2.36f, -1.96f, -2.63f)
                curveToRelative(0.9f, -0.1f, 5.5f, 0.33f, 5.36f, 1.36f)
                close()
                moveTo(110.69f, 113.6f)
                curveToRelative(0.45f, -0.32f, 1.2f, -0.06f, 1.65f, 0.28f)
                curveToRelative(1.76f, 1.34f, 2.19f, 0.84f, 3.4f, 0.42f)
                curveToRelative(1.34f, -0.47f, 1.24f, 0.48f, 0.72f, 1.25f)
                curveToRelative(-0.59f, 0.88f, -3.01f, 2.49f, -4.35f, 2.18f)
                curveToRelative(-0.83f, -0.18f, -1.9f, -3.78f, -1.42f, -4.13f)
                close()
                moveTo(124.0f, 107.9f)
                curveToRelative(-0.06f, -3.73f, -1.0f, -6.42f, -2.02f, -7.96f)
                curveToRelative(-0.2f, 0.03f, -0.4f, 0.11f, -0.6f, 0.24f)
                curveToRelative(-0.51f, 0.35f, -1.46f, 2.66f, -1.35f, 3.13f)
                curveToRelative(0.21f, 0.93f, 3.0f, 3.59f, 3.97f, 4.59f)
                close()
                moveTo(112.05f, 98.72f)
                curveToRelative(1.06f, 1.21f, 1.85f, 0.19f, 2.06f, -0.27f)
                curveToRelative(0.24f, -0.53f, 0.27f, -1.11f, 0.18f, -1.67f)
                horizontalLineToRelative(-4.49f)
                curveToRelative(0.66f, 0.5f, 1.33f, 0.88f, 2.25f, 1.94f)
                close()
                moveTo(99.53f, 117.67f)
                curveToRelative(0.87f, -0.61f, 2.15f, -0.8f, 2.86f, 0.01f)
                curveToRelative(0.67f, 0.76f, 1.27f, 1.65f, 2.03f, 2.22f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(0.54f, -1.06f, 1.08f, -1.96f, 1.47f, -2.23f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF607D8B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(24.56f, 104.89f)
                curveToRelative(-0.6f, -0.82f, -1.71f, -1.21f, -2.15f, -2.12f)
                curveToRelative(-0.57f, -1.17f, 0.23f, -2.62f, -0.21f, -3.85f)
                curveToRelative(-0.45f, -1.28f, -1.97f, -1.75f, -3.29f, -2.05f)
                curveToRelative(-0.82f, -0.19f, -1.81f, -0.18f, -2.61f, 0.08f)
                curveToRelative(-0.31f, 0.1f, -0.61f, 0.24f, -0.93f, 0.34f)
                curveToRelative(-1.28f, 0.41f, -2.6f, -0.01f, -3.91f, 0.15f)
                curveToRelative(-0.81f, 0.1f, -1.64f, 0.39f, -2.14f, 1.03f)
                curveToRelative(-0.27f, 0.35f, -0.43f, 0.78f, -0.7f, 1.13f)
                curveToRelative(-0.51f, 0.63f, -1.36f, 0.72f, -2.0f, 1.18f)
                curveToRelative(-0.61f, 0.44f, -1.29f, 0.87f, -1.6f, 1.59f)
                curveToRelative(-0.5f, 1.16f, 0.18f, 2.5f, -0.05f, 3.74f)
                curveToRelative(-0.13f, 0.69f, -0.52f, 1.29f, -0.75f, 1.95f)
                curveToRelative(-0.03f, 0.09f, -0.06f, 0.18f, -0.08f, 0.27f)
                curveToRelative(-0.29f, 1.09f, -0.12f, 2.3f, 0.47f, 3.26f)
                curveToRelative(0.54f, 0.86f, 1.39f, 1.53f, 1.73f, 2.49f)
                curveToRelative(0.3f, 0.85f, -0.02f, 1.78f, 0.29f, 2.62f)
                curveToRelative(0.28f, 0.76f, 1.09f, 1.17f, 1.85f, 1.3f)
                curveToRelative(1.52f, 0.25f, 3.01f, 0.47f, 4.51f, 0.9f)
                curveToRelative(0.86f, 0.25f, 1.67f, 0.66f, 2.51f, 0.95f)
                curveToRelative(1.78f, 0.62f, 4.07f, 0.29f, 5.74f, -0.57f)
                curveToRelative(0.67f, -0.35f, 1.23f, -1.0f, 1.25f, -1.75f)
                curveToRelative(0.01f, -0.22f, -0.03f, -0.45f, 0.04f, -0.66f)
                curveToRelative(0.08f, -0.25f, 0.29f, -0.42f, 0.46f, -0.62f)
                curveToRelative(0.59f, -0.73f, 0.5f, -1.8f, 0.29f, -2.72f)
                curveToRelative(-0.21f, -0.92f, -0.5f, -1.89f, -0.2f, -2.78f)
                curveToRelative(0.18f, -0.55f, 0.56f, -1.0f, 0.89f, -1.48f)
                curveToRelative(0.46f, -0.64f, 0.84f, -1.35f, 1.0f, -2.12f)
                curveToRelative(0.02f, -0.09f, 0.03f, -0.18f, 0.04f, -0.28f)
                curveToRelative(0.09f, -0.69f, -0.04f, -1.42f, -0.45f, -1.98f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(7.74f, 115.15f)
                curveToRelative(0.23f, 1.79f, 3.96f, 2.86f, 3.61f, 0.36f)
                curveToRelative(-0.22f, -1.51f, -1.13f, -1.79f, -1.57f, -2.66f)
                curveToRelative(-0.37f, -0.53f, -0.49f, -0.95f, -0.12f, -1.5f)
                curveToRelative(0.47f, -0.97f, 0.8f, -2.41f, 0.04f, -3.17f)
                curveToRelative(-1.48f, -1.36f, -3.78f, 0.26f, -2.9f, 2.11f)
                curveToRelative(0.37f, 0.79f, 1.66f, 0.95f, 1.96f, 1.78f)
                curveToRelative(0.41f, 1.2f, -1.33f, 1.8f, -1.02f, 3.08f)
                close()
                moveTo(14.32f, 100.44f)
                curveToRelative(0.33f, 0.8f, 0.07f, 2.02f, 0.31f, 2.76f)
                curveToRelative(0.46f, 1.44f, 2.02f, 1.2f, 2.75f, 3.29f)
                curveToRelative(0.45f, 1.29f, 0.65f, 3.28f, -0.79f, 4.73f)
                curveToRelative(-0.45f, 0.45f, -1.21f, 0.63f, -1.53f, 0.37f)
                curveToRelative(-0.93f, -0.69f, -0.94f, -1.76f, -0.61f, -3.25f)
                curveToRelative(0.07f, -0.31f, -0.15f, -0.33f, -0.24f, -0.45f)
                curveToRelative(-0.68f, -0.39f, -1.22f, -0.97f, -1.08f, -1.81f)
                curveToRelative(0.21f, -1.26f, 1.41f, -1.8f, 1.05f, -2.87f)
                curveToRelative(-0.23f, -0.7f, -0.7f, -1.19f, -1.54f, -1.16f)
                curveToRelative(-0.62f, 0.02f, -0.52f, 0.57f, -1.35f, 0.94f)
                curveToRelative(-0.58f, 0.26f, -1.36f, -0.64f, -1.97f, -0.4f)
                reflectiveCurveToRelative(0.07f, 2.25f, -1.24f, 2.79f)
                curveToRelative(-0.5f, 0.21f, -1.0f, -0.29f, -1.22f, -0.74f)
                curveToRelative(-0.37f, -0.7f, -0.13f, -1.93f, 0.62f, -2.33f)
                curveToRelative(0.63f, -0.35f, 1.16f, 0.25f, 1.77f, -0.15f)
                curveToRelative(0.61f, -0.4f, 0.47f, -2.11f, 1.24f, -2.82f)
                curveToRelative(1.53f, -1.24f, 3.32f, -0.54f, 3.83f, 1.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(16.32f, 79.77f)
                curveToRelative(7.03f, -9.07f, 18.67f, -11.58f, 28.99f, -15.12f)
                curveToRelative(3.79f, -1.3f, 10.04f, -3.62f, 9.08f, -7.71f)
                curveToRelative(-1.41f, -6.02f, -13.08f, -5.23f, -17.65f, -6.04f)
                curveToRelative(-10.75f, -1.9f, -20.2f, -6.76f, -25.75f, -16.16f)
                curveToRelative(-4.61f, -7.23f, -7.59f, -23.62f, 3.49f, -30.9f)
                curveToRelative(0.0f, 0.0f, -6.94f, 9.97f, 6.78f, 22.71f)
                curveTo(36.6f, 40.8f, 62.11f, 30.32f, 79.45f, 41.52f)
                curveToRelative(20.72f, 14.59f, 2.35f, 30.82f, -16.39f, 33.72f)
                curveToRelative(-8.43f, 1.31f, -16.89f, 2.42f, -25.3f, 3.63f)
                curveToRelative(-10.92f, 1.56f, -19.36f, 3.92f, -25.49f, 13.68f)
                curveToRelative(-0.58f, -4.63f, 1.15f, -9.08f, 4.05f, -12.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF424242)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.87f, 97.96f)
                curveToRelative(0.82f, 0.44f, 1.61f, 1.26f, 1.45f, 2.17f)
                curveToRelative(-0.07f, 0.42f, -0.33f, 0.77f, -0.49f, 1.17f)
                curveToRelative(-0.4f, 0.99f, -0.09f, 2.22f, 0.72f, 2.91f)
                curveToRelative(0.71f, 0.59f, 1.76f, 0.84f, 2.14f, 1.68f)
                curveToRelative(0.37f, 0.81f, -0.1f, 1.76f, -0.7f, 2.43f)
                curveToRelative(-0.79f, 0.89f, -2.08f, 1.91f, -2.0f, 3.23f)
                curveToRelative(0.04f, 0.65f, 0.33f, 1.25f, 0.67f, 1.8f)
                curveToRelative(0.16f, 0.25f, 0.33f, 0.5f, 0.41f, 0.79f)
                curveToRelative(0.08f, 0.29f, 0.06f, 0.62f, -0.14f, 0.84f)
                curveToRelative(-0.16f, 0.17f, -0.4f, 0.24f, -0.6f, 0.37f)
                curveToRelative(-0.49f, 0.33f, -0.62f, 0.99f, -0.64f, 1.58f)
                curveToRelative(-0.02f, 0.55f, 0.09f, 1.6f, -0.3f, 2.05f)
                curveToRelative(-0.33f, 0.39f, -2.1f, 0.92f, -2.6f, 0.92f)
                curveToRelative(1.3f, 0.0f, 3.77f, 0.53f, 4.42f, -0.99f)
                curveToRelative(0.13f, -0.3f, 0.16f, -0.63f, 0.29f, -0.92f)
                curveToRelative(0.34f, -0.8f, 1.24f, -1.18f, 1.88f, -1.76f)
                curveToRelative(0.6f, -0.55f, 0.99f, -1.33f, 1.08f, -2.14f)
                curveToRelative(0.06f, -0.58f, -0.03f, -1.17f, -0.01f, -1.76f)
                curveToRelative(0.04f, -1.63f, 1.31f, -1.81f, 2.25f, -2.89f)
                curveToRelative(0.45f, -0.51f, 0.71f, -1.17f, 0.78f, -1.85f)
                curveToRelative(0.06f, -0.51f, 0.02f, -1.03f, -0.12f, -1.53f)
                curveToRelative(-0.32f, -1.15f, -1.11f, -2.13f, -2.05f, -2.86f)
                curveToRelative(-0.9f, -0.7f, -2.07f, -1.36f, -2.16f, -2.5f)
                curveToRelative(-0.03f, -0.36f, 0.07f, -0.72f, 0.09f, -1.09f)
                curveToRelative(0.08f, -1.7f, -1.66f, -2.03f, -2.7f, -2.46f)
                curveToRelative(-1.04f, -0.43f, -3.85f, -0.38f, -3.85f, -0.38f)
                curveToRelative(0.63f, 0.53f, 1.45f, 0.8f, 2.18f, 1.19f)
                close()
            }
            path(
                fill = linearGradient(
                    0.0f to Color(0xFFA15D38), 1.0f to Color(0x00A15D38), start =
                    Offset(108.387f, 126.585f), end = Offset(108.387f, 112.254f)
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(118.59f, 96.78f)
                horizontalLineTo(92.77f)
                reflectiveCurveToRelative(5.41f, 1.95f, 5.41f, 11.56f)
                reflectiveCurveToRelative(-5.41f, 11.56f, -5.41f, 11.56f)
                horizontalLineToRelative(25.82f)
                reflectiveCurveToRelative(5.41f, -1.95f, 5.41f, -11.56f)
                reflectiveCurveToRelative(-5.41f, -11.56f, -5.41f, -11.56f)
                close()
            }
        }
            .build()
        return _cigarette!!
    }

private var _cigarette: ImageVector? = null
