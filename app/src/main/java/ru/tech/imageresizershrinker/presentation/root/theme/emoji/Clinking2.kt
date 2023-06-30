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

val Emoji.Clinking2: ImageVector
    get() {
        if (_clinking2 != null) {
            return _clinking2!!
        }
        _clinking2 = Builder(
            name = "Clinking2", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, fillAlpha = 0.7f, strokeAlpha
                = 0.7f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(63.46f, 15.98f)
                lineToRelative(1.21f, 6.44f)
                lineToRelative(-9.83f, -5.06f)
                lineToRelative(-6.89f, -1.29f)
                reflectiveCurveTo(36.86f, 35.1f, 33.12f, 43.52f)
                curveToRelative(-7.72f, 17.37f, -4.82f, 28.19f, -4.61f, 34.95f)
                curveToRelative(0.15f, 4.61f, -1.39f, 7.4f, -2.36f, 9.43f)
                curveToRelative(-0.96f, 2.04f, -6.67f, 14.05f, -6.67f, 14.05f)
                reflectiveCurveToRelative(-5.42f, -2.99f, -10.92f, -2.77f)
                curveToRelative(-4.71f, 0.18f, -5.18f, 3.13f, -5.08f, 3.88f)
                curveToRelative(0.11f, 0.75f, 6.69f, 9.4f, 16.98f, 14.12f)
                reflectiveCurveToRelative(15.32f, 2.53f, 15.32f, 2.53f)
                reflectiveCurveToRelative(3.03f, -2.19f, -0.51f, -6.59f)
                reflectiveCurveToRelative(-8.36f, -7.11f, -8.36f, -7.11f)
                lineToRelative(8.68f, -18.97f)
                lineToRelative(5.68f, -6.0f)
                lineToRelative(6.43f, -4.72f)
                lineToRelative(6.43f, -7.08f)
                lineToRelative(5.9f, -9.11f)
                lineToRelative(7.18f, -13.72f)
                lineToRelative(1.61f, -3.0f)
                reflectiveCurveToRelative(3.86f, 13.61f, 8.68f, 21.33f)
                curveToRelative(4.82f, 7.72f, 7.5f, 8.36f, 10.29f, 14.04f)
                reflectiveCurveToRelative(6.57f, 22.06f, 6.57f, 22.06f)
                reflectiveCurveToRelative(-4.16f, 1.35f, -8.05f, 3.61f)
                curveToRelative(-5.63f, 3.27f, -3.9f, 5.9f, -3.73f, 7.22f)
                curveToRelative(0.34f, 2.66f, 8.42f, 4.74f, 21.61f, 0.13f)
                reflectiveCurveToRelative(14.34f, -8.45f, 14.01f, -10.38f)
                curveToRelative(-0.32f, -1.93f, -4.22f, -2.64f, -7.61f, -2.86f)
                curveToRelative(-3.08f, -0.2f, -7.91f, 0.62f, -7.91f, 0.62f)
                lineToRelative(-3.43f, -10.96f)
                lineToRelative(-1.93f, -10.08f)
                lineToRelative(0.75f, -7.4f)
                lineToRelative(1.93f, -7.72f)
                lineToRelative(0.75f, -10.61f)
                lineToRelative(-1.72f, -15.01f)
                lineToRelative(-4.82f, -21.65f)
                lineToRelative(-2.11f, -7.48f)
                reflectiveCurveToRelative(-11.7f, 0.95f, -16.42f, 2.12f)
                curveToRelative(-9.54f, 2.36f, -12.22f, 5.59f, -12.22f, 5.59f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveToRelative(63.46f, 15.98f)
                lineToRelative(1.21f, 6.44f)
                lineToRelative(10.4f, 6.48f)
                lineToRelative(-11.21f, -1.78f)
                lineToRelative(-7.98f, -4.35f)
                lineToRelative(-7.93f, -6.7f)
                reflectiveCurveTo(36.86f, 35.1f, 33.12f, 43.52f)
                curveToRelative(-7.72f, 17.37f, -4.82f, 28.19f, -4.61f, 34.95f)
                curveToRelative(0.15f, 4.61f, -1.39f, 7.4f, -2.36f, 9.43f)
                curveToRelative(-0.96f, 2.04f, -6.67f, 14.05f, -6.67f, 14.05f)
                reflectiveCurveToRelative(-5.42f, -2.99f, -10.92f, -2.77f)
                curveToRelative(-4.71f, 0.18f, -5.18f, 3.13f, -5.08f, 3.88f)
                curveToRelative(0.11f, 0.75f, 6.69f, 9.4f, 16.98f, 14.12f)
                reflectiveCurveToRelative(15.43f, 2.02f, 15.43f, 2.02f)
                reflectiveCurveToRelative(2.91f, -1.67f, -0.63f, -6.07f)
                reflectiveCurveToRelative(-8.36f, -7.11f, -8.36f, -7.11f)
                lineToRelative(8.68f, -18.97f)
                lineToRelative(5.68f, -6.0f)
                lineToRelative(6.43f, -4.72f)
                lineToRelative(6.43f, -7.08f)
                lineToRelative(5.9f, -9.11f)
                lineToRelative(7.18f, -13.72f)
                lineToRelative(1.61f, -3.0f)
                reflectiveCurveToRelative(3.86f, 13.61f, 8.68f, 21.33f)
                curveToRelative(4.82f, 7.72f, 7.5f, 8.36f, 10.29f, 14.04f)
                reflectiveCurveToRelative(6.57f, 22.06f, 6.57f, 22.06f)
                reflectiveCurveToRelative(-4.16f, 1.35f, -8.05f, 3.61f)
                curveToRelative(-5.63f, 3.27f, -3.9f, 5.9f, -3.73f, 7.22f)
                curveToRelative(0.34f, 2.66f, 8.42f, 4.74f, 21.61f, 0.13f)
                reflectiveCurveToRelative(14.34f, -8.45f, 14.01f, -10.38f)
                curveToRelative(-0.32f, -1.93f, -4.22f, -2.64f, -7.61f, -2.86f)
                curveToRelative(-3.08f, -0.2f, -7.91f, 0.62f, -7.91f, 0.62f)
                lineToRelative(-3.43f, -10.96f)
                lineToRelative(-1.93f, -10.08f)
                lineToRelative(0.75f, -7.4f)
                lineToRelative(1.93f, -7.72f)
                lineToRelative(0.75f, -10.61f)
                lineToRelative(-1.72f, -15.01f)
                lineToRelative(-4.82f, -21.65f)
                lineToRelative(-1.94f, -6.25f)
                reflectiveCurveToRelative(-4.42f, 4.16f, -12.99f, 5.8f)
                curveToRelative(-9.63f, 1.86f, -15.81f, 0.67f, -15.81f, 0.67f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEF3C0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(41.77f, 36.48f)
                reflectiveCurveToRelative(0.39f, -0.57f, 1.75f, -0.78f)
                curveToRelative(0.75f, -0.12f, 7.88f, -1.13f, 12.01f, -2.47f)
                curveToRelative(3.66f, -1.18f, 10.4f, -4.93f, 12.33f, -4.4f)
                reflectiveCurveToRelative(1.72f, 1.82f, 1.07f, 3.97f)
                reflectiveCurveToRelative(-3.66f, 7.9f, -3.66f, 7.9f)
                reflectiveCurveToRelative(-19.75f, 10.81f, -19.32f, 10.49f)
                curveToRelative(0.43f, -0.32f, -4.18f, -14.71f, -4.18f, -14.71f)
                close()
            }
            path(
                fill = radialGradient(
                    0.404f to Color(0xFFFFD61B),
                    0.531f to Color(0xFFFFD71F),
                    0.66f to Color(0xFFFFD92B),
                    0.79f to Color(0xFFFFDC40),
                    0.921f to
                            Color(0xFFFFE05C),
                    0.96f to Color(0xFFFFE266),
                    center = Offset(48.517f, 56.866f),
                    radius = 18.683f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(35.48f, 76.75f)
                curveToRelative(5.47f, 2.57f, 12.54f, -4.5f, 17.9f, -13.4f)
                curveToRelative(4.6f, -7.64f, 11.9f, -22.73f, 11.9f, -22.73f)
                reflectiveCurveToRelative(-9.11f, 3.32f, -13.72f, 1.93f)
                reflectiveCurveToRelative(-9.76f, -6.11f, -9.76f, -6.11f)
                reflectiveCurveToRelative(-5.21f, 9.41f, -7.83f, 17.58f)
                curveToRelative(-2.67f, 8.37f, -3.74f, 20.26f, 1.51f, 22.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFEFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.97f, 49.66f)
                curveToRelative(-1.24f, 0.57f, -0.84f, 2.27f, -0.69f, 2.88f)
                curveToRelative(0.21f, 0.91f, 0.91f, 2.45f, 2.61f, 1.71f)
                curveToRelative(1.49f, -0.65f, 0.91f, -2.56f, 0.59f, -3.31f)
                curveToRelative(-0.29f, -0.65f, -1.23f, -1.86f, -2.51f, -1.28f)
                close()
                moveTo(49.5f, 47.9f)
                curveToRelative(-0.05f, 1.12f, 0.8f, 2.08f, 2.24f, 2.03f)
                curveToRelative(1.44f, -0.05f, 2.19f, -1.07f, 2.24f, -2.19f)
                curveToRelative(0.05f, -1.12f, -1.2f, -1.98f, -2.45f, -1.87f)
                curveToRelative(-1.23f, 0.11f, -1.98f, 0.75f, -2.03f, 2.03f)
                close()
                moveTo(40.75f, 62.57f)
                curveToRelative(-0.96f, -1.02f, -2.72f, -0.53f, -3.79f, 0.53f)
                curveToRelative(-0.72f, 0.72f, -1.44f, 2.45f, -0.48f, 3.47f)
                curveToRelative(0.96f, 1.01f, 2.83f, 0.37f, 3.79f, -0.53f)
                curveToRelative(0.96f, -0.91f, 1.23f, -2.67f, 0.48f, -3.47f)
                close()
                moveTo(43.85f, 62.14f)
                curveToRelative(0.57f, 0.5f, 1.55f, 0.32f, 2.03f, -0.27f)
                curveToRelative(0.48f, -0.59f, 0.48f, -1.71f, -0.21f, -2.13f)
                curveToRelative(-0.69f, -0.43f, -1.6f, -0.08f, -1.97f, 0.53f)
                curveToRelative(-0.44f, 0.7f, -0.33f, 1.44f, 0.15f, 1.87f)
                close()
                moveTo(37.71f, 70.57f)
                curveToRelative(-1.38f, 0.24f, -1.71f, 1.76f, -1.55f, 2.61f)
                curveToRelative(0.16f, 0.85f, 0.91f, 1.65f, 1.97f, 1.44f)
                curveToRelative(1.07f, -0.21f, 1.81f, -1.12f, 1.71f, -2.4f)
                curveToRelative(-0.1f, -1.28f, -1.22f, -1.81f, -2.13f, -1.65f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEF3C0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(68.76f, 24.08f)
                reflectiveCurveToRelative(-0.94f, -1.49f, 0.39f, -1.99f)
                reflectiveCurveToRelative(6.74f, 1.22f, 11.05f, 2.21f)
                curveToRelative(4.31f, 0.99f, 12.54f, 3.15f, 12.54f, 3.15f)
                lineToRelative(-0.28f, 1.99f)
                reflectiveCurveToRelative(-15.58f, 3.04f, -15.69f, 2.87f)
                curveToRelative(-0.11f, -0.17f, -1.77f, -2.1f, -1.88f, -2.43f)
                curveToRelative(-0.11f, -0.33f, -3.59f, -4.69f, -3.76f, -4.69f)
                curveToRelative(-0.16f, 0.0f, -2.37f, -1.11f, -2.37f, -1.11f)
                close()
            }
            path(
                fill = radialGradient(
                    0.404f to Color(0xFFFFD61B),
                    0.531f to Color(0xFFFFD71F),
                    0.66f to Color(0xFFFFD92B),
                    0.79f to Color(0xFFFFDC40),
                    0.921f to
                            Color(0xFFFFE05C),
                    0.96f to Color(0xFFFFE266),
                    center = Offset(84.066f, 49.413f),
                    radius = 17.862f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(75.39f, 28.89f)
                reflectiveCurveToRelative(5.41f, 0.0f, 8.95f, -0.11f)
                curveToRelative(3.54f, -0.11f, 8.4f, -1.33f, 8.4f, -1.33f)
                reflectiveCurveToRelative(2.52f, 11.9f, 3.49f, 20.63f)
                curveToRelative(0.84f, 7.55f, 0.34f, 21.95f, -4.14f, 23.14f)
                curveToRelative(-5.87f, 1.56f, -11.41f, -9.29f, -13.79f, -14.63f)
                curveToRelative(-3.48f, -7.79f, -6.71f, -19.42f, -6.71f, -19.42f)
                lineToRelative(3.8f, -8.28f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(80.04f, 37.09f)
                curveToRelative(0.7f, 0.7f, 1.89f, 0.41f, 2.78f, -0.41f)
                curveToRelative(0.89f, -0.81f, 0.81f, -2.22f, 0.07f, -2.7f)
                reflectiveCurveToRelative(-1.81f, -0.33f, -2.48f, 0.41f)
                curveToRelative(-0.7f, 0.77f, -1.11f, 1.95f, -0.37f, 2.7f)
                close()
                moveTo(88.29f, 39.86f)
                curveToRelative(-1.59f, -0.26f, -2.52f, 0.59f, -2.85f, 2.63f)
                curveToRelative(-0.32f, 1.96f, 1.55f, 2.3f, 2.3f, 2.3f)
                curveToRelative(1.04f, 0.0f, 1.81f, -1.07f, 1.96f, -2.59f)
                reflectiveCurveToRelative(-0.34f, -2.16f, -1.41f, -2.34f)
                close()
                moveTo(85.92f, 48.6f)
                curveToRelative(-0.09f, 1.3f, 1.48f, 2.0f, 2.78f, 1.96f)
                curveToRelative(1.3f, -0.04f, 1.93f, -0.7f, 1.93f, -1.55f)
                curveToRelative(0.0f, -0.85f, -1.0f, -1.74f, -2.44f, -1.81f)
                reflectiveCurveToRelative(-2.19f, 0.29f, -2.27f, 1.4f)
                close()
                moveTo(86.66f, 54.93f)
                curveToRelative(-1.52f, -0.96f, -3.7f, 0.56f, -3.33f, 2.52f)
                curveToRelative(0.37f, 1.96f, 1.89f, 2.41f, 3.22f, 1.74f)
                curveToRelative(1.23f, -0.62f, 1.65f, -3.29f, 0.11f, -4.26f)
                close()
                moveTo(90.81f, 61.93f)
                curveToRelative(-1.12f, -0.44f, -2.31f, 0.11f, -2.81f, 1.18f)
                curveToRelative(-0.33f, 0.7f, -0.33f, 2.7f, 1.22f, 2.96f)
                curveToRelative(1.55f, 0.26f, 2.37f, -0.7f, 2.59f, -1.67f)
                reflectiveCurveToRelative(0.04f, -2.07f, -1.0f, -2.47f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF2A23)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(104.91f, 28.08f)
                arcToRelative(1.998f, 1.998f, 0.0f, false, true, -1.66f, -3.12f)
                lineToRelative(7.27f, -10.78f)
                curveToRelative(0.62f, -0.92f, 1.86f, -1.16f, 2.78f, -0.54f)
                curveToRelative(0.92f, 0.62f, 1.16f, 1.86f, 0.54f, 2.78f)
                lineToRelative(-7.27f, 10.78f)
                curveToRelative(-0.39f, 0.57f, -1.02f, 0.88f, -1.66f, 0.88f)
                close()
                moveTo(107.49f, 34.18f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, -0.56f, -3.92f)
                lineToRelative(15.24f, -4.45f)
                arcToRelative(2.0f, 2.0f, 0.0f, false, true, 1.12f, 3.84f)
                lineToRelative(-15.24f, 4.45f)
                curveToRelative(-0.18f, 0.05f, -0.37f, 0.08f, -0.56f, 0.08f)
                close()
                moveTo(120.97f, 45.78f)
                curveToRelative(-0.25f, 0.0f, -0.5f, -0.05f, -0.75f, -0.15f)
                lineToRelative(-11.84f, -4.81f)
                arcToRelative(2.003f, 2.003f, 0.0f, false, true, 1.51f, -3.71f)
                lineToRelative(11.84f, 4.81f)
                arcToRelative(2.003f, 2.003f, 0.0f, false, true, -0.76f, 3.86f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFAFAFAF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(102.26f, 102.23f)
                curveToRelative(-0.22f, -0.76f, -5.38f, -18.67f, -5.95f, -21.78f)
                curveToRelative(-0.55f, -2.98f, 0.37f, -6.9f, 1.35f, -11.04f)
                curveToRelative(0.5f, -2.14f, 1.02f, -4.35f, 1.37f, -6.53f)
                curveToRelative(0.93f, -5.88f, 0.64f, -17.36f, -1.7f, -27.93f)
                curveToRelative(-2.2f, -9.92f, -5.77f, -24.91f, -5.81f, -25.06f)
                lineToRelative(2.0f, -0.71f)
                curveToRelative(0.04f, 0.15f, 3.56f, 15.38f, 5.77f, 25.33f)
                curveToRelative(2.12f, 9.54f, 2.85f, 21.6f, 1.73f, 28.67f)
                curveToRelative(-0.36f, 2.26f, -0.89f, 4.5f, -1.4f, 6.68f)
                curveToRelative(-0.93f, 3.94f, -1.8f, 7.65f, -1.33f, 10.22f)
                curveToRelative(0.55f, 3.0f, 5.85f, 21.4f, 5.91f, 21.58f)
                lineToRelative(-1.94f, 0.57f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC9C9C9)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.35f, 16.97f)
                curveToRelative(-0.53f, 0.0f, -0.84f, -0.46f, -0.92f, -1.0f)
                curveToRelative(-0.21f, -1.34f, 1.09f, -3.92f, 13.22f, -6.59f)
                curveToRelative(8.9f, -1.96f, 14.42f, -2.19f, 16.15f, -1.04f)
                curveToRelative(0.51f, 0.34f, 0.69f, 0.7f, 0.73f, 0.91f)
                curveToRelative(0.11f, 0.6f, -0.46f, 1.04f, -1.06f, 1.15f)
                curveToRelative(-0.43f, 0.08f, -0.85f, -0.11f, -1.09f, -0.44f)
                curveToRelative(-0.42f, -0.18f, -3.01f, -0.91f, -14.26f, 1.57f)
                curveToRelative(-9.72f, 2.14f, -11.69f, 4.16f, -11.69f, 4.17f)
                curveToRelative(0.09f, 0.6f, -0.32f, 1.16f, -0.92f, 1.26f)
                curveToRelative(-0.04f, 0.01f, -0.1f, 0.01f, -0.16f, 0.01f)
                close()
                moveTo(91.2f, 9.52f)
                close()
                moveTo(91.2f, 9.52f)
                close()
                moveTo(91.2f, 9.52f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(69.26f, 17.6f)
                curveToRelative(-3.26f, 0.0f, -4.84f, -0.47f, -4.95f, -0.51f)
                arcToRelative(1.101f, 1.101f, 0.0f, false, true, 0.66f, -2.1f)
                curveToRelative(0.04f, 0.01f, 4.33f, 1.24f, 13.76f, -0.65f)
                curveToRelative(10.32f, -2.08f, 12.86f, -5.35f, 12.89f, -5.38f)
                curveToRelative(0.36f, -0.48f, 1.04f, -0.59f, 1.53f, -0.24f)
                reflectiveCurveToRelative(0.61f, 1.02f, 0.26f, 1.51f)
                curveToRelative(-0.28f, 0.4f, -3.07f, 4.01f, -14.25f, 6.26f)
                curveToRelative(-4.25f, 0.86f, -7.52f, 1.11f, -9.9f, 1.11f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFAFAFAF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(26.31f, 108.49f)
                lineToRelative(-2.58f, -0.86f)
                curveToRelative(0.07f, -0.16f, 8.08f, -16.09f, 10.83f, -21.26f)
                curveToRelative(1.51f, -2.84f, 4.62f, -4.75f, 7.76f, -6.97f)
                curveToRelative(2.95f, -2.09f, 6.3f, -4.47f, 8.86f, -7.78f)
                curveTo(56.09f, 65.28f, 61.47f, 56.2f, 64.9f, 48.5f)
                curveToRelative(2.64f, -5.93f, 9.55f, -19.66f, 9.62f, -19.8f)
                lineToRelative(1.66f, 1.14f)
                curveToRelative(-0.07f, 0.14f, -6.84f, 13.58f, -9.45f, 19.47f)
                curveToRelative(-3.49f, 7.84f, -8.97f, 17.08f, -13.97f, 23.54f)
                curveToRelative(-2.74f, 3.54f, -6.22f, 6.01f, -9.29f, 8.19f)
                curveToRelative(-3.07f, 2.18f, -5.72f, 4.06f, -7.01f, 6.48f)
                curveToRelative(-2.72f, 5.12f, -10.07f, 20.81f, -10.15f, 20.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC9C9C9)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(75.18f, 30.54f)
                curveToRelative(-0.52f, 0.0f, -0.95f, -0.36f, -1.07f, -0.83f)
                curveToRelative(-0.17f, -0.29f, -1.68f, -2.41f, -11.97f, -7.6f)
                curveToRelative(-9.88f, -4.98f, -12.44f, -4.61f, -12.74f, -4.53f)
                curveToRelative(-0.45f, 0.26f, -1.02f, 0.18f, -1.38f, -0.23f)
                curveToRelative(-0.4f, -0.46f, -0.36f, -1.15f, 0.1f, -1.55f)
                curveToRelative(0.87f, -0.76f, 3.98f, -1.22f, 15.01f, 4.35f)
                curveToRelative(6.0f, 3.02f, 13.14f, 7.09f, 13.14f, 9.3f)
                curveToRelative(0.01f, 0.6f, -0.48f, 1.09f, -1.09f, 1.09f)
                close()
                moveTo(74.08f, 29.44f)
                close()
                moveTo(74.08f, 29.44f)
                close()
                moveTo(49.37f, 17.58f)
                close()
                moveTo(49.57f, 17.45f)
                close()
                moveTo(49.58f, 17.44f)
                curveToRelative(0.0f, 0.01f, -0.01f, 0.01f, 0.0f, 0.0f)
                curveToRelative(-0.01f, 0.01f, 0.0f, 0.0f, 0.0f, 0.0f)
                close()
                moveTo(49.58f, 17.44f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.33f, 30.68f)
                curveToRelative(-1.92f, 0.0f, -7.11f, -0.44f, -14.54f, -4.43f)
                curveToRelative(-8.32f, -4.47f, -11.71f, -8.6f, -11.85f, -8.77f)
                curveToRelative(-0.38f, -0.47f, -0.31f, -1.17f, 0.16f, -1.55f)
                curveToRelative(0.47f, -0.38f, 1.16f, -0.31f, 1.55f, 0.16f)
                curveToRelative(0.03f, 0.04f, 3.3f, 3.98f, 11.18f, 8.21f)
                curveToRelative(8.64f, 4.64f, 14.01f, 4.16f, 14.07f, 4.15f)
                curveToRelative(0.61f, -0.07f, 1.14f, 0.37f, 1.21f, 0.98f)
                curveToRelative(0.07f, 0.6f, -0.37f, 1.14f, -0.97f, 1.21f)
                curveToRelative(-0.05f, 0.01f, -0.33f, 0.04f, -0.81f, 0.04f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0B0B0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(94.81f, 102.13f)
                curveToRelative(-0.65f, 0.33f, -0.3f, 2.54f, 0.52f, 3.09f)
                curveToRelative(1.01f, 0.68f, 2.27f, 0.85f, 4.8f, 0.17f)
                curveToRelative(2.7f, -0.72f, 4.1f, -1.66f, 4.34f, -2.26f)
                curveToRelative(0.24f, -0.6f, -0.7f, -2.89f, -0.7f, -2.89f)
                reflectiveCurveToRelative(-1.87f, 1.21f, -3.97f, 1.81f)
                curveToRelative(-2.1f, 0.61f, -4.27f, -0.28f, -4.99f, 0.08f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.73f, 78.94f)
                curveToRelative(-1.46f, 0.48f, -1.42f, 2.32f, -0.46f, 5.98f)
                reflectiveCurveToRelative(3.96f, 14.98f, 4.2f, 15.76f)
                curveToRelative(0.24f, 0.78f, 0.9f, 2.1f, 3.3f, 1.32f)
                reflectiveCurveToRelative(1.0f, -1.67f, 0.4f, -3.83f)
                curveToRelative(-0.45f, -1.61f, -4.62f, -16.94f, -4.84f, -17.72f)
                curveToRelative(-0.36f, -1.21f, -1.51f, -1.87f, -2.6f, -1.51f)
                close()
                moveTo(86.31f, 106.94f)
                curveToRelative(-2.03f, -0.3f, -2.22f, 2.04f, -1.26f, 3.36f)
                curveToRelative(0.64f, 0.87f, 1.67f, 1.36f, 3.06f, 1.5f)
                curveToRelative(1.92f, 0.19f, 3.66f, -0.29f, 3.66f, -1.68f)
                curveToRelative(0.0f, -1.14f, -1.74f, -0.72f, -3.0f, -1.26f)
                curveToRelative(-0.77f, -0.33f, -1.27f, -1.74f, -2.46f, -1.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0B0B0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(101.41f, 111.41f)
                curveToRelative(5.78f, -1.41f, 9.43f, -2.92f, 11.76f, -4.54f)
                curveToRelative(2.98f, -2.08f, 3.74f, -4.19f, 3.71f, -4.87f)
                curveToRelative(-0.06f, -1.2f, -0.62f, -2.05f, -0.54f, -2.22f)
                curveToRelative(0.18f, -0.36f, 2.3f, 1.08f, 2.4f, 2.4f)
                curveToRelative(0.08f, 1.02f, 0.44f, 3.52f, -4.25f, 6.51f)
                curveToRelative(-2.47f, 1.58f, -6.09f, 3.44f, -12.41f, 5.0f)
                curveToRelative(-6.21f, 1.53f, -11.21f, 1.85f, -14.69f, 1.21f)
                curveToRelative(-3.7f, -0.68f, -5.68f, -2.41f, -5.8f, -4.36f)
                curveToRelative(-0.11f, -1.8f, 1.03f, -3.03f, 1.2f, -2.99f)
                curveToRelative(0.24f, 0.06f, -0.43f, 1.32f, 0.24f, 2.58f)
                curveToRelative(0.53f, 0.98f, 1.85f, 1.93f, 4.44f, 2.44f)
                curveToRelative(2.71f, 0.54f, 6.83f, 0.58f, 13.94f, -1.16f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(47.56f, 21.05f)
                curveToRelative(-1.23f, 2.15f, -5.53f, 9.54f, -5.99f, 10.49f)
                curveToRelative(-1.2f, 2.5f, 2.75f, 3.8f, 4.23f, 1.76f)
                curveToRelative(1.76f, -2.43f, 5.0f, -7.82f, 5.11f, -8.94f)
                curveToRelative(0.15f, -1.61f, -2.73f, -4.38f, -3.35f, -3.31f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFAFAFAF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(19.17f, 103.63f)
                curveToRelative(-0.76f, 0.16f, -1.55f, 1.48f, -1.48f, 2.04f)
                curveToRelative(0.14f, 1.12f, 2.09f, 2.43f, 3.61f, 3.24f)
                curveToRelative(2.16f, 1.15f, 3.9f, 1.42f, 4.78f, 0.08f)
                curveToRelative(0.88f, -1.34f, -0.63f, -2.84f, -0.63f, -2.84f)
                reflectiveCurveToRelative(-2.08f, 0.75f, -3.28f, -0.06f)
                curveToRelative(-1.19f, -0.81f, -2.01f, -2.67f, -3.0f, -2.46f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(7.41f, 102.44f)
                curveToRelative(-0.61f, 0.49f, -1.34f, 1.75f, 0.7f, 3.77f)
                curveToRelative(2.07f, 2.05f, 3.39f, 2.56f, 4.36f, 1.54f)
                reflectiveCurveToRelative(-0.16f, -2.29f, -1.61f, -3.8f)
                curveToRelative(-1.11f, -1.15f, -2.2f, -2.49f, -3.45f, -1.51f)
                close()
                moveTo(30.98f, 83.54f)
                curveToRelative(-0.6f, 0.93f, -9.4f, 19.22f, -9.71f, 19.85f)
                curveToRelative(-0.43f, 0.85f, -0.27f, 2.07f, 1.01f, 2.72f)
                curveToRelative(1.42f, 0.73f, 2.4f, 0.32f, 2.93f, -0.48f)
                curveToRelative(0.36f, -0.54f, 7.96f, -16.5f, 8.67f, -17.94f)
                curveToRelative(0.96f, -1.97f, 2.24f, -4.05f, 0.3f, -5.21f)
                curveToRelative(-1.6f, -0.97f, -2.72f, 0.31f, -3.2f, 1.06f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB0B0B0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(18.17f, 114.57f)
                curveToRelative(5.28f, 2.75f, 9.02f, 4.02f, 11.83f, 4.34f)
                curveToRelative(3.62f, 0.4f, 5.02f, 0.06f, 5.81f, -0.63f)
                curveToRelative(0.9f, -0.79f, 0.88f, -1.95f, 1.06f, -2.02f)
                curveToRelative(0.37f, -0.15f, 1.01f, 2.33f, 0.22f, 3.38f)
                curveToRelative(-0.61f, 0.82f, -1.81f, 2.35f, -7.31f, 1.5f)
                curveToRelative(-2.9f, -0.44f, -6.85f, -1.43f, -12.63f, -4.42f)
                curveToRelative(-5.68f, -2.94f, -9.65f, -6.0f, -11.84f, -8.77f)
                curveToRelative(-2.33f, -2.95f, -2.68f, -5.56f, -1.49f, -7.1f)
                curveToRelative(1.1f, -1.43f, 2.05f, -1.24f, 2.16f, -1.09f)
                curveToRelative(0.14f, 0.2f, -0.48f, 0.35f, -0.8f, 1.73f)
                curveToRelative(-0.25f, 1.09f, 0.12f, 2.67f, 1.73f, 4.76f)
                curveToRelative(1.69f, 2.18f, 4.77f, 4.93f, 11.26f, 8.32f)
                close()
            }
        }
            .build()
        return _clinking2!!
    }

private var _clinking2: ImageVector? = null
