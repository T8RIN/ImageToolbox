package ru.tech.imageresizershrinker.presentation.root.theme.emoji

import androidx.compose.ui.geometry.Offset
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

val Emoji.Sponge: ImageVector
    get() {
        if (_sponge != null) {
            return _sponge!!
        }
        _sponge = Builder(
            name = "Sponge", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(122.68f, 46.87f)
                lineToRelative(-41.8f, -14.44f)
                lineTo(4.03f, 66.57f)
                curveToRelative(-0.17f, 4.45f, 0.38f, 15.13f, 1.12f, 16.2f)
                curveToRelative(0.78f, 1.12f, 3.86f, 3.63f, 3.43f, 4.92f)
                curveToRelative(-0.33f, 0.99f, -1.4f, 1.51f, -2.1f, 2.29f)
                curveToRelative(-2.36f, 2.63f, 1.05f, 7.91f, 2.78f, 10.1f)
                curveToRelative(1.91f, 2.42f, 4.4f, 4.36f, 7.01f, 5.98f)
                curveToRelative(2.6f, 1.62f, 9.26f, 6.18f, 12.38f, 4.85f)
                curveToRelative(0.45f, -0.19f, 0.82f, -0.53f, 1.22f, -0.81f)
                curveToRelative(0.81f, -0.57f, 1.78f, -0.96f, 2.77f, -0.93f)
                curveToRelative(0.99f, 0.02f, 1.99f, 0.49f, 2.53f, 1.32f)
                curveToRelative(1.56f, 2.41f, 1.99f, 3.49f, 4.98f, 4.25f)
                curveToRelative(3.12f, 0.8f, 6.43f, 1.21f, 9.63f, 1.52f)
                curveToRelative(4.97f, 0.48f, 10.09f, 0.49f, 14.98f, -0.66f)
                curveToRelative(6.68f, -1.57f, 12.55f, -5.64f, 14.57f, -12.44f)
                curveToRelative(3.33f, -11.19f, 19.65f, -15.81f, 26.57f, -17.11f)
                curveToRelative(1.42f, -0.27f, 2.83f, -0.56f, 4.23f, -0.9f)
                curveToRelative(0.88f, -0.22f, 3.58f, -0.48f, 4.1f, -1.23f)
                curveToRelative(0.19f, -0.28f, 0.24f, -0.63f, 0.31f, -0.96f)
                curveToRelative(0.25f, -1.16f, 0.89f, -2.28f, 1.91f, -2.88f)
                curveToRelative(1.02f, -0.6f, 2.44f, -0.58f, 3.31f, 0.23f)
                curveToRelative(0.09f, 0.08f, 0.17f, 0.17f, 0.28f, 0.23f)
                curveToRelative(0.79f, 0.37f, 2.31f, -1.85f, 2.64f, -2.38f)
                arcToRelative(8.558f, 8.558f, 0.0f, false, false, 1.3f, -4.4f)
                curveToRelative(0.16f, -8.35f, -1.3f, -26.89f, -1.3f, -26.89f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFEB3B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(102.71f, 29.18f)
                curveToRelative(-16.67f, -6.8f, -28.58f, -7.83f, -37.05f, -6.06f)
                arcToRelative(0.66f, 0.66f, 0.0f, false, false, -0.49f, 0.84f)
                curveToRelative(0.06f, 0.19f, 0.1f, 0.41f, 0.11f, 0.66f)
                curveToRelative(0.01f, 1.21f, -2.09f, 2.62f, -4.92f, 2.84f)
                curveToRelative(-1.07f, 0.08f, -2.07f, -0.03f, -2.91f, -0.28f)
                curveToRelative(-0.75f, -0.22f, -1.57f, -0.04f, -2.21f, 0.42f)
                curveToRelative(-3.84f, 2.78f, -6.23f, 6.24f, -7.66f, 9.26f)
                curveToRelative(-1.19f, 2.51f, -3.5f, 4.2f, -5.91f, 5.46f)
                curveToRelative(-4.77f, 2.49f, -10.08f, 3.91f, -15.15f, 5.66f)
                curveToRelative(-13.6f, 4.69f, -15.94f, 7.55f, -16.42f, 8.87f)
                curveToRelative(-0.2f, 0.55f, -0.32f, 1.13f, -0.61f, 1.63f)
                curveToRelative(-0.77f, 1.31f, -2.45f, 1.7f, -3.74f, 2.5f)
                curveToRelative(-0.59f, 0.37f, -1.58f, 2.48f, -1.72f, 5.17f)
                curveToRelative(-0.48f, 9.75f, 9.69f, 15.87f, 17.23f, 19.05f)
                curveToRelative(21.96f, 9.29f, 36.02f, 12.57f, 47.86f, -0.62f)
                curveToRelative(4.97f, -5.53f, 8.51f, -10.61f, 11.72f, -12.19f)
                curveToRelative(-1.24f, -0.41f, -2.99f, -1.53f, -2.39f, -2.79f)
                curveToRelative(1.83f, -3.85f, 10.8f, -1.45f, 10.98f, -1.41f)
                curveToRelative(0.29f, 0.07f, 3.06f, -2.47f, 16.07f, -3.02f)
                curveToRelative(10.99f, -0.46f, 34.37f, -20.83f, -2.79f, -35.99f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD19600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.19f, 88.01f)
                curveToRelative(0.18f, 2.41f, -1.31f, 4.11f, -3.33f, 3.81f)
                curveToRelative(-2.02f, -0.31f, -3.81f, -2.51f, -3.99f, -4.91f)
                arcToRelative(4.4f, 4.4f, 0.0f, false, true, 0.16f, -1.65f)
                reflectiveCurveToRelative(5.18f, -2.56f, 8.72f, -0.82f)
                curveToRelative(3.54f, 1.74f, -1.56f, 3.57f, -1.56f, 3.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(28.19f, 87.98f)
                curveToRelative(-1.23f, 0.4f, -2.57f, 0.45f, -3.8f, 0.05f)
                curveToRelative(-0.69f, -0.22f, -1.33f, -0.57f, -1.91f, -1.01f)
                curveToRelative(-0.3f, -0.23f, -1.53f, -1.15f, -1.49f, -1.56f)
                curveToRelative(0.04f, -0.43f, 1.23f, -0.7f, 1.61f, -0.83f)
                curveToRelative(2.23f, -0.76f, 4.95f, -1.27f, 7.15f, -0.19f)
                curveToRelative(1.76f, 0.86f, 1.31f, 2.04f, -0.14f, 2.9f)
                curveToRelative(-0.45f, 0.27f, -0.93f, 0.48f, -1.42f, 0.64f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD19600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.23f, 99.91f)
                curveToRelative(0.16f, 2.19f, -1.19f, 3.72f, -3.02f, 3.45f)
                curveToRelative(-1.83f, -0.28f, -3.45f, -2.27f, -3.61f, -4.45f)
                curveToRelative(-0.16f, -2.18f, 1.19f, -3.72f, 3.02f, -3.45f)
                reflectiveCurveToRelative(3.45f, 2.28f, 3.61f, 4.45f)
                close()
                moveTo(42.84f, 101.6f)
                curveToRelative(0.19f, 2.49f, -1.18f, 4.29f, -3.07f, 4.01f)
                curveToRelative(-1.88f, -0.28f, -3.55f, -2.51f, -3.74f, -4.99f)
                curveToRelative(-0.19f, -2.48f, 1.18f, -4.29f, 3.07f, -4.01f)
                curveToRelative(1.88f, 0.27f, 3.55f, 2.5f, 3.74f, 4.99f)
                close()
                moveTo(53.33f, 109.71f)
                curveToRelative(0.11f, 1.51f, -0.73f, 2.61f, -1.87f, 2.45f)
                reflectiveCurveToRelative(-2.17f, -1.53f, -2.28f, -3.05f)
                curveToRelative(-0.11f, -1.51f, 0.73f, -2.61f, 1.87f, -2.45f)
                reflectiveCurveToRelative(2.17f, 1.52f, 2.28f, 3.05f)
                close()
                moveTo(69.85f, 93.43f)
                curveToRelative(0.04f, 2.62f, -1.31f, 5.34f, -3.01f, 6.08f)
                curveToRelative(-1.7f, 0.74f, -3.12f, -0.78f, -3.16f, -3.41f)
                curveToRelative(-0.04f, -2.62f, 1.31f, -5.34f, 3.01f, -6.08f)
                curveToRelative(1.7f, -0.73f, 3.11f, 0.8f, 3.16f, 3.41f)
                close()
                moveTo(110.7f, 79.26f)
                curveToRelative(-0.05f, 2.01f, -1.17f, 4.05f, -2.49f, 4.56f)
                curveToRelative(-1.33f, 0.51f, -2.37f, -0.69f, -2.32f, -2.71f)
                curveToRelative(0.05f, -2.01f, 1.17f, -4.05f, 2.49f, -4.56f)
                curveToRelative(1.33f, -0.51f, 2.37f, 0.71f, 2.32f, 2.71f)
                close()
                moveTo(120.22f, 68.23f)
                curveToRelative(-0.05f, 2.01f, -1.17f, 4.05f, -2.49f, 4.56f)
                reflectiveCurveToRelative(-2.37f, -0.69f, -2.32f, -2.71f)
                curveToRelative(0.05f, -2.01f, 1.17f, -4.05f, 2.49f, -4.56f)
                curveToRelative(1.33f, -0.51f, 2.37f, 0.71f, 2.32f, 2.71f)
                close()
                moveTo(100.22f, 74.1f)
                curveToRelative(-0.07f, 2.95f, -1.84f, 5.99f, -3.94f, 6.8f)
                reflectiveCurveToRelative(-3.75f, -0.9f, -3.68f, -3.85f)
                reflectiveCurveToRelative(1.84f, -5.99f, 3.94f, -6.8f)
                reflectiveCurveToRelative(3.76f, 0.91f, 3.68f, 3.85f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(16.861f, 60.12f)
                arcToRelative(3.8f, 6.92f, 85.682f, true, false, 13.801f, -1.042f)
                arcToRelative(3.8f, 6.92f, 85.682f, true, false, -13.801f, 1.042f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(40.902f, 62.851f)
                arcToRelative(5.16f, 10.53f, 85.682f, true, false, 21.0f, -1.586f)
                arcToRelative(5.16f, 10.53f, 85.682f, true, false, -21.0f, 1.586f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(55.172f, 36.636f)
                arcToRelative(4.07f, 7.41f, 85.682f, true, false, 14.778f, -1.116f)
                arcToRelative(4.07f, 7.41f, 85.682f, true, false, -14.778f, 1.116f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(38.013f, 51.691f)
                arcToRelative(2.28f, 4.15f, 85.682f, true, false, 8.276f, -0.625f)
                arcToRelative(2.28f, 4.15f, 85.682f, true, false, -8.276f, 0.625f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.484f, 73.341f)
                arcToRelative(2.28f, 4.15f, 85.682f, true, false, 8.276f, -0.625f)
                arcToRelative(2.28f, 4.15f, 85.682f, true, false, -8.276f, 0.625f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(45.069f, 80.71f)
                arcToRelative(3.22f, 5.87f, 85.682f, true, false, 11.707f, -0.884f)
                arcToRelative(3.22f, 5.87f, 85.682f, true, false, -11.707f, 0.884f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(59.032f, 48.395f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, 7.598f, -0.574f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, -7.598f, 0.574f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(56.953f, 71.795f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, 7.598f, -0.574f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, -7.598f, 0.574f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.192f, 30.954f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, 7.598f, -0.574f)
                arcToRelative(2.09f, 3.81f, 85.682f, true, false, -7.598f, 0.574f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(101.097f, 53.031f)
                arcToRelative(3.74f, 6.82f, 88.015f, true, false, 13.632f, -0.472f)
                arcToRelative(3.74f, 6.82f, 88.015f, true, false, -13.632f, 0.472f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(96.065f, 45.977f)
                arcToRelative(1.78f, 3.23f, 88.015f, true, false, 6.456f, -0.224f)
                arcToRelative(1.78f, 3.23f, 88.015f, true, false, -6.456f, 0.224f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(80.05f, 38.591f)
                arcToRelative(1.78f, 3.23f, 85.682f, true, false, 6.442f, -0.486f)
                arcToRelative(1.78f, 3.23f, 85.682f, true, false, -6.442f, 0.486f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEEBD1C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.502f, 53.935f)
                arcToRelative(4.07f, 7.41f, 85.682f, true, false, 14.778f, -1.116f)
                arcToRelative(4.07f, 7.41f, 85.682f, true, false, -14.778f, 1.116f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD19600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(83.94f, 69.59f)
                curveToRelative(1.66f, -0.03f, 3.28f, -0.53f, 4.8f, -1.19f)
                curveToRelative(0.11f, 0.95f, -0.3f, 1.87f, -0.7f, 2.74f)
                curveToRelative(-0.39f, 0.83f, -0.8f, 1.69f, -1.53f, 2.24f)
                curveToRelative(-0.88f, 0.66f, -2.09f, 0.74f, -3.16f, 0.47f)
                curveToRelative(-1.07f, -0.27f, -2.02f, -0.86f, -2.95f, -1.46f)
                curveToRelative(-0.89f, -0.57f, -3.16f, -2.69f, -1.33f, -3.7f)
                curveToRelative(0.58f, -0.32f, 1.22f, 0.1f, 1.76f, 0.3f)
                curveToRelative(1.02f, 0.38f, 2.01f, 0.62f, 3.11f, 0.6f)
                close()
            }
            path(
                fill = radialGradient(
                    0.376f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(34.000107f, 45.24951f), radius = 16.65914f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(33.74f, 43.62f)
                moveToRelative(-16.49f, 0.0f)
                arcToRelative(16.49f, 16.49f, 0.0f, true, true, 32.98f, 0.0f)
                arcToRelative(16.49f, 16.49f, 0.0f, true, true, -32.98f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.376f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(36.383553f, 15.234364f), radius = 6.956f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(35.47f, 14.24f)
                moveToRelative(-6.72f, 0.0f)
                arcToRelative(6.72f, 6.72f, 0.0f, true, true, 13.44f, 0.0f)
                arcToRelative(6.72f, 6.72f, 0.0f, true, true, -13.44f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.376f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(18.201242f, 31.914831f), radius = 8.834f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(17.98f, 31.79f)
                moveToRelative(-7.96f, 0.0f)
                arcToRelative(7.96f, 7.96f, 0.0f, true, true, 15.92f, 0.0f)
                arcToRelative(7.96f, 7.96f, 0.0f, true, true, -15.92f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.8f, strokeAlpha
                = 0.8f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(27.61f, 32.31f)
                curveToRelative(0.02f, 0.5f, 0.55f, 0.84f, 1.04f, 0.94f)
                curveToRelative(1.56f, 0.31f, 3.7f, -0.22f, 5.3f, -0.11f)
                curveToRelative(1.69f, 0.12f, 2.72f, 0.24f, 4.56f, 0.83f)
                curveToRelative(1.07f, 0.34f, 3.22f, 0.4f, 2.8f, -1.3f)
                curveToRelative(-0.36f, -1.47f, -2.91f, -2.56f, -4.17f, -2.87f)
                curveToRelative(-2.93f, -0.74f, -5.91f, -0.6f, -8.03f, 0.45f)
                curveToRelative(-1.4f, 0.69f, -1.51f, 1.79f, -1.5f, 2.06f)
                close()
            }
            path(
                fill = radialGradient(
                    0.2f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA), center
                    = Offset(89.53492f, 95.81554f), radius = 11.395f
                ), stroke = null, strokeLineWidth
                = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(87.81f, 92.88f)
                moveToRelative(-10.93f, 0.0f)
                arcToRelative(10.93f, 10.93f, 0.0f, true, true, 21.86f, 0.0f)
                arcToRelative(10.93f, 10.93f, 0.0f, true, true, -21.86f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.312f to Color(0x0080DEEA), 1.0f to Color(0xFF80DEEA),
                    center = Offset(106.7967f, 102.36458f), radius = 6.027f
                ), stroke = null,
                fillAlpha = 0.7f, strokeAlpha = 0.7f, strokeLineWidth = 0.0f, strokeLineCap =
                Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(106.38f, 102.66f)
                moveToRelative(-5.32f, 0.0f)
                arcToRelative(5.32f, 5.32f, 0.0f, true, true, 10.64f, 0.0f)
                arcToRelative(5.32f, 5.32f, 0.0f, true, true, -10.64f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.8f, strokeAlpha
                = 0.8f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(96.07f, 91.48f)
                curveToRelative(0.14f, 0.72f, 0.17f, 1.51f, -0.17f, 2.17f)
                curveToRelative(-0.34f, 0.66f, -1.12f, 1.13f, -1.82f, 0.9f)
                curveToRelative(-0.59f, -0.19f, -0.95f, -0.77f, -1.3f, -1.29f)
                curveToRelative(-1.16f, -1.67f, -2.84f, -2.9f, -4.75f, -3.57f)
                curveToRelative(-1.18f, -0.42f, -3.88f, -0.97f, -3.45f, -2.84f)
                curveToRelative(0.64f, -2.74f, 5.77f, -1.85f, 7.88f, -0.47f)
                curveToRelative(1.8f, 1.18f, 3.2f, 2.99f, 3.61f, 5.1f)
                close()
                moveTo(30.66f, 13.41f)
                curveToRelative(0.56f, -0.02f, 1.02f, -0.87f, 2.03f, -1.64f)
                curveToRelative(0.91f, -0.7f, 2.25f, -1.03f, 3.06f, -1.08f)
                curveToRelative(0.44f, -0.03f, 1.09f, -0.17f, 1.35f, -0.64f)
                curveToRelative(0.14f, -0.26f, -0.09f, -1.59f, -1.96f, -1.34f)
                curveToRelative(-2.22f, 0.3f, -4.16f, 1.76f, -4.65f, 3.08f)
                curveToRelative(-0.33f, 0.9f, -0.36f, 1.64f, 0.17f, 1.62f)
                close()
            }
        }
            .build()
        return _sponge!!
    }

private var _sponge: ImageVector? = null
