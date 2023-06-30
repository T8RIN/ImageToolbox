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

val Emoji.Bathtub: ImageVector
    get() {
        if (_bathtub != null) {
            return _bathtub!!
        }
        _bathtub = Builder(
            name = "Bathtub", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(117.34f, 17.56f)
                curveToRelative(0.12f, -4.96f, -2.09f, -9.62f, -5.95f, -11.85f)
                curveToRelative(-5.8f, -3.35f, -12.44f, -1.66f, -15.71f, 4.0f)
                lineToRelative(-1.53f, 2.59f)
                lineToRelative(3.44f, 1.95f)
                lineToRelative(1.33f, -2.33f)
                curveToRelative(2.22f, -3.85f, 6.58f, -4.98f, 10.43f, -2.76f)
                curveToRelative(2.49f, 1.44f, 4.12f, 4.35f, 4.03f, 8.32f)
                verticalLineToRelative(61.58f)
                lineToRelative(3.97f, -0.02f)
                verticalLineTo(17.56f)
                close()
            }
            path(
                fill = radialGradient(
                    0.22f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA), center
                    = Offset(65.096436f, 72.39627f), radius = 5.5533376f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(64.96f, 72.32f)
                moveToRelative(-5.0f, 0.0f)
                arcToRelative(5.0f, 5.0f, 0.0f, true, true, 10.0f, 0.0f)
                arcToRelative(5.0f, 5.0f, 0.0f, true, true, -10.0f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.261f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(112.56893f, 73.67446f), radius = 6.278811f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(112.47f, 73.52f)
                moveToRelative(-5.66f, 0.0f)
                arcToRelative(5.66f, 5.66f, 0.0f, true, true, 11.32f, 0.0f)
                arcToRelative(5.66f, 5.66f, 0.0f, true, true, -11.32f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.304f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(75.23648f, 73.89179f), radius = 8.76039f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(75.14f, 73.25f)
                moveToRelative(-7.89f, 0.0f)
                arcToRelative(7.89f, 7.89f, 0.0f, true, true, 15.78f, 0.0f)
                arcToRelative(7.89f, 7.89f, 0.0f, true, true, -15.78f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.261f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(87.219666f, 70.27632f), radius = 10.498783f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(86.95f, 70.12f)
                moveToRelative(-9.46f, 0.0f)
                arcToRelative(9.46f, 9.46f, 0.0f, true, true, 18.92f, 0.0f)
                arcToRelative(9.46f, 9.46f, 0.0f, true, true, -18.92f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.261f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(101.394745f, 75.04163f), radius = 10.8091755f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(101.12f, 74.88f)
                moveToRelative(-9.74f, 0.0f)
                arcToRelative(9.74f, 9.74f, 0.0f, true, true, 19.48f, 0.0f)
                arcToRelative(9.74f, 9.74f, 0.0f, true, true, -19.48f, 0.0f)
            }
            path(
                fill = radialGradient(
                    0.376f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(98.10696f, 60.14189f), radius = 9.56424f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(97.94f, 59.69f)
                moveToRelative(-8.07f, 0.0f)
                arcToRelative(8.07f, 8.07f, 0.0f, true, true, 16.14f, 0.0f)
                arcToRelative(8.07f, 8.07f, 0.0f, true, true, -16.14f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFF78909C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(100.53f, 9.89f)
                curveToRelative(-0.6f, -1.22f, -2.31f, -2.16f, -3.44f, -2.16f)
                curveToRelative(-0.79f, 0.0f, -1.18f, 0.5f, -1.71f, 1.41f)
                curveToRelative(-0.52f, 0.9f, -0.21f, 2.06f, 0.69f, 2.58f)
                lineToRelative(1.64f, 0.94f)
                curveToRelative(0.9f, 0.52f, 2.06f, 0.21f, 2.58f, -0.69f)
                curveToRelative(0.52f, -0.9f, 0.57f, -1.41f, 0.24f, -2.08f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(98.06f, 10.6f)
                curveToRelative(-4.32f, -2.49f, -10.18f, -0.43f, -12.69f, 3.85f)
                curveToRelative(0.0f, 0.0f, 2.53f, 3.31f, 7.25f, 5.95f)
                reflectiveCurveToRelative(8.45f, 3.11f, 8.45f, 3.11f)
                curveToRelative(2.45f, -4.31f, 1.31f, -10.41f, -3.01f, -12.91f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF78909C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(99.05f, 25.62f)
                reflectiveCurveToRelative(-3.61f, -0.59f, -8.16f, -3.4f)
                reflectiveCurveToRelative(-6.71f, -5.18f, -6.71f, -5.18f)
                curveToRelative(-0.74f, -0.77f, -1.22f, -1.68f, -0.69f, -2.59f)
                curveToRelative(0.52f, -0.91f, 1.94f, -1.38f, 2.59f, -0.69f)
                curveToRelative(0.0f, 0.0f, 1.94f, 2.54f, 6.73f, 5.19f)
                curveToRelative(5.17f, 2.86f, 8.14f, 3.39f, 8.14f, 3.39f)
                curveToRelative(1.17f, 0.27f, 1.22f, 1.68f, 0.69f, 2.59f)
                curveToRelative(-0.52f, 0.91f, -1.9f, 0.93f, -2.59f, 0.69f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDFECF5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(88.97f, 13.18f)
                curveToRelative(0.43f, -1.51f, 2.8f, -2.56f, 4.43f, -2.76f)
                curveToRelative(1.58f, -0.2f, 2.54f, 0.29f, 2.53f, 1.12f)
                curveToRelative(-0.01f, 0.79f, -0.93f, 1.04f, -1.59f, 1.03f)
                curveToRelative(-2.33f, -0.05f, -3.23f, 0.46f, -3.63f, 0.69f)
                curveToRelative(-1.74f, 0.98f, -1.78f, 0.08f, -1.74f, -0.08f)
                close()
            }
            path(
                fill = radialGradient(
                    0.376f to Color(0x0080DEEA), 1.0f to Color(0xCC80DEEA),
                    center = Offset(84.09305f, 49.841164f), radius = 6.217755f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(83.27f, 48.95f)
                moveToRelative(-6.01f, 0.0f)
                arcToRelative(6.01f, 6.01f, 0.0f, true, true, 12.02f, 0.0f)
                arcToRelative(6.01f, 6.01f, 0.0f, true, true, -12.02f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.8f, strokeAlpha
                = 0.8f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(78.98f, 48.21f)
                curveToRelative(0.5f, -0.02f, 0.92f, -0.77f, 1.82f, -1.46f)
                curveToRelative(0.82f, -0.62f, 2.01f, -0.92f, 2.73f, -0.96f)
                curveToRelative(0.4f, -0.02f, 0.97f, -0.15f, 1.2f, -0.57f)
                curveToRelative(0.13f, -0.24f, -0.08f, -1.42f, -1.76f, -1.19f)
                curveToRelative(-1.98f, 0.27f, -3.72f, 1.57f, -4.16f, 2.75f)
                curveToRelative(-0.28f, 0.78f, -0.31f, 1.44f, 0.17f, 1.43f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.06f, 75.92f)
                curveToRelative(5.32f, 20.33f, 4.89f, 36.85f, 23.12f, 36.85f)
                lineToRelative(66.2f, 0.48f)
                curveToRelative(20.91f, -0.25f, 14.58f, -27.44f, 19.51f, -37.18f)
                curveToRelative(-16.68f, 1.77f, -31.8f, 2.05f, -50.2f, 0.83f)
                curveToRelative(-13.07f, -0.86f, -24.37f, -9.25f, -36.93f, -12.07f)
                curveToRelative(-8.67f, -1.31f, -18.27f, -3.35f, -26.74f, -0.5f)
                curveToRelative(2.12f, 3.66f, 3.81f, 7.55f, 5.04f, 11.59f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB7D5E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.23f, 82.29f)
                curveToRelative(-2.66f, 1.54f, -2.83f, 7.53f, -2.54f, 10.72f)
                curveToRelative(0.8f, 8.98f, -0.04f, 14.4f, -4.26f, 16.32f)
                curveToRelative(-0.01f, -0.33f, -0.07f, -0.7f, -0.24f, -1.12f)
                curveToRelative(-0.56f, -1.4f, -1.84f, -2.64f, -3.35f, -2.65f)
                curveToRelative(-2.92f, -0.02f, -3.89f, 1.13f, -5.56f, 2.39f)
                curveToRelative(-1.2f, 0.91f, -2.72f, 2.02f, -4.9f, 2.27f)
                lineToRelative(-48.63f, -0.35f)
                curveToRelative(-1.97f, -0.12f, -3.66f, -1.23f, -4.86f, -2.14f)
                curveToRelative(-1.88f, -1.43f, -2.51f, -1.91f, -4.95f, -2.41f)
                curveToRelative(-1.48f, -0.3f, -2.79f, 0.65f, -3.34f, 2.05f)
                curveToRelative(-0.31f, 0.77f, -0.35f, 1.4f, -0.19f, 1.91f)
                curveToRelative(-11.91f, -1.61f, -11.32f, -27.76f, -17.74f, -40.29f)
                curveToRelative(-2.08f, -4.06f, -5.03f, -3.56f, -4.82f, -3.19f)
                curveToRelative(1.74f, 3.23f, 3.16f, 6.63f, 4.23f, 10.14f)
                curveToRelative(5.32f, 20.33f, 4.89f, 36.85f, 23.12f, 36.85f)
                lineToRelative(66.2f, 0.48f)
                curveToRelative(14.95f, -0.18f, 16.86f, -14.27f, 17.89f, -31.59f)
                curveToRelative(0.07f, -1.45f, -4.82f, -0.11f, -6.06f, 0.61f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF69A1BA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(10.41f, 69.03f)
                curveToRelative(4.95f, -1.03f, 10.06f, -0.11f, 11.59f, 0.22f)
                curveToRelative(14.95f, 3.23f, 24.77f, 9.51f, 42.52f, 13.43f)
                curveToRelative(19.71f, 4.35f, 42.22f, 3.06f, 55.68f, 0.1f)
                curveToRelative(0.0f, 0.0f, 0.52f, -3.58f, 0.97f, -4.95f)
                curveToRelative(0.0f, 0.0f, -27.73f, 3.6f, -46.03f, 1.35f)
                curveToRelative(-19.12f, -2.35f, -41.06f, -13.02f, -53.83f, -14.58f)
                curveToRelative(-9.66f, -1.18f, -12.82f, 0.78f, -12.82f, 0.78f)
                lineToRelative(1.92f, 3.65f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(33.0f, 105.34f)
                curveToRelative(-2.41f, -0.3f, -2.84f, 2.8f, -2.84f, 2.8f)
                reflectiveCurveToRelative(-1.45f, -1.82f, -3.85f, -1.24f)
                curveToRelative(-1.47f, 0.36f, -1.99f, 2.2f, -1.35f, 2.68f)
                curveToRelative(1.14f, 0.86f, 4.95f, 2.78f, 3.81f, 5.5f)
                curveToRelative(-0.44f, 1.06f, -1.65f, 1.37f, -2.81f, 1.89f)
                curveToRelative(-2.66f, 1.2f, -3.7f, 3.18f, -3.96f, 4.45f)
                curveToRelative(-0.06f, 0.3f, -0.08f, 0.77f, 0.6f, 1.13f)
                reflectiveCurveToRelative(1.93f, 0.7f, 5.12f, 0.7f)
                reflectiveCurveToRelative(5.43f, -0.59f, 5.78f, -1.06f)
                curveToRelative(1.16f, -1.58f, -1.0f, -4.69f, 1.41f, -7.77f)
                curveToRelative(0.98f, -1.26f, 1.76f, -1.04f, 4.22f, -1.62f)
                curveToRelative(0.6f, -0.14f, 0.49f, -1.67f, -0.34f, -2.66f)
                curveToRelative(-0.64f, -0.77f, -2.63f, -1.15f, -3.9f, -0.46f)
                curveToRelative(0.0f, 0.0f, 1.09f, -3.97f, -1.89f, -4.34f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF78909C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(39.61f, 110.0f)
                curveToRelative(-1.54f, -1.68f, -3.73f, -0.82f, -4.12f, -0.61f)
                curveToRelative(0.29f, -0.16f, 0.14f, -1.65f, 0.1f, -1.9f)
                curveToRelative(-0.18f, -1.18f, -1.23f, -2.03f, -2.45f, -2.13f)
                curveToRelative(2.45f, 1.26f, 0.96f, 4.72f, 0.96f, 4.72f)
                reflectiveCurveToRelative(0.72f, -0.23f, 1.76f, -0.43f)
                curveToRelative(1.19f, -0.22f, 2.69f, 0.13f, 2.85f, 1.7f)
                curveToRelative(0.08f, 0.75f, -1.22f, 1.02f, -1.68f, 1.16f)
                curveToRelative(-3.38f, 1.0f, -3.66f, 2.73f, -4.01f, 4.02f)
                curveToRelative(-0.34f, 1.25f, 0.55f, 3.35f, -0.12f, 4.61f)
                curveToRelative(-0.88f, 1.66f, -4.34f, 1.73f, -6.9f, 1.38f)
                curveToRelative(-1.98f, -0.27f, -4.0f, -1.1f, -4.0f, -1.1f)
                reflectiveCurveToRelative(-0.12f, 1.0f, 0.6f, 1.41f)
                curveToRelative(3.39f, 1.92f, 9.51f, 1.14f, 11.33f, 0.08f)
                curveToRelative(0.27f, -0.16f, 0.45f, -0.38f, 0.51f, -0.69f)
                curveToRelative(0.09f, -0.52f, 0.16f, -1.46f, -0.21f, -2.95f)
                curveToRelative(-0.38f, -1.54f, -0.18f, -3.21f, 0.88f, -4.48f)
                curveToRelative(0.93f, -1.11f, 1.75f, -1.27f, 4.15f, -1.54f)
                curveToRelative(1.54f, -0.17f, 1.22f, -2.31f, 0.35f, -3.25f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDFECF5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.48f, 117.41f)
                curveToRelative(1.4f, -0.47f, 3.15f, -0.44f, 2.94f, 0.63f)
                curveToRelative(-0.12f, 0.61f, -0.85f, 0.93f, -1.64f, 1.17f)
                curveToRelative(-0.83f, 0.24f, -2.45f, 0.34f, -2.58f, -0.24f)
                curveToRelative(-0.16f, -0.73f, 0.42f, -1.28f, 1.28f, -1.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB7D5E5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(87.99f, 81.1f)
                curveToRelative(-7.04f, 0.0f, -21.7f, 0.0f, -39.0f, -6.67f)
                curveToRelative(-10.29f, -3.96f, -27.25f, -11.19f, -41.46f, -8.05f)
                curveToRelative(-1.61f, 0.35f, -3.19f, -0.66f, -3.55f, -2.27f)
                curveToRelative(-0.35f, -1.6f, 0.66f, -3.19f, 2.27f, -3.55f)
                curveToRelative(16.03f, -3.53f, 33.97f, 4.41f, 45.0f, 8.36f)
                curveToRelative(16.89f, 6.05f, 30.55f, 6.2f, 37.05f, 6.22f)
                curveToRelative(10.93f, -0.03f, 21.62f, -0.23f, 32.22f, -2.43f)
                curveToRelative(1.61f, -0.34f, 3.18f, 0.7f, 3.52f, 2.31f)
                curveToRelative(0.33f, 1.61f, -0.7f, 3.18f, -2.31f, 3.52f)
                curveToRelative(-11.14f, 2.31f, -22.16f, 2.53f, -33.41f, 2.55f)
                curveToRelative(-0.12f, 0.01f, -0.22f, 0.01f, -0.33f, 0.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(31.2f, 77.94f)
                curveToRelative(1.24f, 2.33f, 1.37f, 5.09f, 1.18f, 7.72f)
                reflectiveCurveToRelative(-0.66f, 5.26f, -0.49f, 7.9f)
                curveToRelative(0.15f, 2.34f, 2.16f, 6.24f, 2.3f, 7.65f)
                curveToRelative(0.1f, 0.95f, -0.77f, 1.81f, -1.73f, 1.9f)
                curveToRelative(-0.95f, 0.09f, -1.87f, -0.38f, -2.65f, -0.94f)
                curveToRelative(-3.24f, -2.35f, -4.95f, -6.29f, -5.88f, -10.19f)
                curveToRelative(-1.26f, -5.3f, -2.6f, -10.34f, -3.27f, -12.08f)
                curveToRelative(-1.94f, -5.02f, 1.27f, -7.27f, 5.09f, -6.29f)
                curveToRelative(2.3f, 0.6f, 4.33f, 2.24f, 5.45f, 4.33f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBDBDBD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(105.01f, 105.34f)
                curveToRelative(2.41f, -0.3f, 2.84f, 2.8f, 2.84f, 2.8f)
                reflectiveCurveToRelative(1.45f, -1.82f, 3.85f, -1.24f)
                curveToRelative(1.47f, 0.36f, 1.74f, 2.3f, 1.23f, 2.75f)
                curveToRelative(-1.21f, 1.04f, -4.83f, 2.71f, -3.69f, 5.43f)
                curveToRelative(0.44f, 1.06f, 1.65f, 1.37f, 2.81f, 1.89f)
                curveToRelative(2.66f, 1.2f, 3.7f, 3.18f, 3.96f, 4.45f)
                curveToRelative(0.06f, 0.3f, 0.08f, 0.77f, -0.6f, 1.13f)
                curveToRelative(-0.68f, 0.36f, -1.93f, 0.7f, -5.12f, 0.7f)
                reflectiveCurveToRelative(-5.43f, -0.59f, -5.78f, -1.06f)
                curveToRelative(-1.16f, -1.58f, 1.0f, -4.69f, -1.41f, -7.77f)
                curveToRelative(-0.98f, -1.26f, -1.76f, -1.04f, -4.22f, -1.62f)
                curveToRelative(-0.6f, -0.14f, -0.49f, -1.67f, 0.34f, -2.66f)
                curveToRelative(0.64f, -0.77f, 2.63f, -1.15f, 3.9f, -0.46f)
                curveToRelative(0.0f, 0.0f, -1.09f, -3.97f, 1.89f, -4.34f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF78909C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(98.4f, 110.0f)
                curveToRelative(1.54f, -1.68f, 3.73f, -0.82f, 4.12f, -0.61f)
                curveToRelative(-0.29f, -0.16f, -0.14f, -1.65f, -0.1f, -1.9f)
                curveToRelative(0.18f, -1.18f, 1.23f, -2.03f, 2.45f, -2.13f)
                curveToRelative(-2.45f, 1.26f, -0.96f, 4.72f, -0.96f, 4.72f)
                reflectiveCurveToRelative(-0.72f, -0.23f, -1.76f, -0.43f)
                curveToRelative(-1.19f, -0.22f, -2.69f, 0.13f, -2.85f, 1.7f)
                curveToRelative(-0.08f, 0.75f, 1.22f, 1.02f, 1.68f, 1.16f)
                curveToRelative(3.38f, 1.0f, 3.66f, 2.73f, 4.01f, 4.02f)
                curveToRelative(0.34f, 1.25f, -0.55f, 3.35f, 0.12f, 4.61f)
                curveToRelative(0.88f, 1.66f, 4.34f, 1.73f, 6.9f, 1.38f)
                curveToRelative(1.98f, -0.27f, 4.0f, -1.1f, 4.0f, -1.1f)
                reflectiveCurveToRelative(0.12f, 1.0f, -0.6f, 1.41f)
                curveToRelative(-3.39f, 1.92f, -9.51f, 1.14f, -11.33f, 0.08f)
                curveToRelative(-0.27f, -0.16f, -0.45f, -0.38f, -0.51f, -0.69f)
                curveToRelative(-0.09f, -0.52f, -0.16f, -1.46f, 0.21f, -2.95f)
                curveToRelative(0.38f, -1.54f, 0.18f, -3.21f, -0.88f, -4.48f)
                curveToRelative(-0.93f, -1.11f, -1.75f, -1.27f, -4.15f, -1.54f)
                curveToRelative(-1.54f, -0.17f, -1.22f, -2.31f, -0.35f, -3.25f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDFECF5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(107.83f, 117.35f)
                curveToRelative(-0.83f, 0.7f, -1.61f, 2.08f, -0.96f, 2.85f)
                curveToRelative(0.27f, 0.32f, 1.15f, -0.42f, 1.36f, -0.6f)
                curveToRelative(0.55f, -0.49f, 1.02f, -0.84f, 1.59f, -1.31f)
                curveToRelative(0.25f, -0.21f, 0.46f, -0.5f, 0.39f, -0.82f)
                curveToRelative(-0.16f, -0.72f, -1.69f, -0.7f, -2.38f, -0.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.8f, strokeAlpha
                = 0.8f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(82.14f, 68.06f)
                curveToRelative(-0.3f, 0.21f, -0.61f, 0.42f, -0.97f, 0.49f)
                reflectiveCurveToRelative(-0.77f, -0.01f, -1.0f, -0.29f)
                curveToRelative(-1.78f, -2.17f, 3.71f, -6.54f, 5.8f, -5.69f)
                curveToRelative(1.38f, 0.56f, 1.58f, 2.46f, 0.27f, 3.09f)
                curveToRelative(-1.52f, 0.71f, -2.7f, 1.4f, -4.1f, 2.4f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.55f, 59.15f)
                curveToRelative(-0.44f, -0.07f, -0.84f, -0.43f, -1.01f, -0.85f)
                curveToRelative(-0.21f, -0.54f, -0.12f, -1.15f, 0.07f, -1.69f)
                curveToRelative(0.46f, -1.31f, 1.54f, -2.41f, 2.86f, -2.84f)
                curveToRelative(0.36f, -0.12f, 0.74f, -0.19f, 1.12f, -0.13f)
                curveToRelative(0.61f, 0.1f, 1.13f, 0.58f, 1.29f, 1.18f)
                curveToRelative(0.42f, 1.59f, -1.24f, 1.72f, -2.12f, 2.48f)
                curveToRelative(-0.63f, 0.54f, -0.96f, 1.67f, -1.87f, 1.85f)
                arcToRelative(0.97f, 0.97f, 0.0f, false, true, -0.34f, 0.0f)
                close()
                moveTo(13.35f, 62.65f)
                curveToRelative(-0.33f, 1.36f, 1.1f, 1.59f, 5.85f, 1.57f)
                curveToRelative(5.72f, -0.02f, 9.73f, 1.83f, 9.18f, 0.59f)
                curveToRelative(-0.4f, -0.9f, -4.46f, -3.05f, -7.87f, -3.37f)
                curveToRelative(-2.08f, -0.2f, -6.74f, -0.46f, -7.16f, 1.21f)
                close()
            }
        }
            .build()
        return _bathtub!!
    }

private var _bathtub: ImageVector? = null
