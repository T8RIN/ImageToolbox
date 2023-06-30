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

val Emoji.Peach: ImageVector
    get() {
        if (_peach != null) {
            return _peach!!
        }
        _peach = Builder(
            name = "Peach", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF69A246)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(101.98f, 24.25f)
                reflectiveCurveToRelative(6.37f, -4.95f, 9.91f, -8.49f)
                curveToRelative(4.36f, -4.36f, 5.66f, -8.93f, 4.14f, -9.47f)
                curveToRelative(-1.52f, -0.54f, -7.41f, 0.54f, -7.41f, 0.54f)
                lineToRelative(-17.64f, 8.6f)
                lineToRelative(-4.47f, 6.64f)
                lineToRelative(15.47f, 2.18f)
                close()
                moveTo(28.36f, 21.96f)
                reflectiveCurveToRelative(-3.7f, -1.31f, -7.84f, -5.77f)
                curveToRelative(-4.04f, -4.36f, -5.23f, -7.3f, -4.68f, -8.93f)
                curveToRelative(0.54f, -1.63f, 20.69f, 4.36f, 20.69f, 4.36f)
                lineToRelative(13.4f, 5.99f)
                lineToRelative(3.48f, 7.19f)
                lineToRelative(-25.05f, -2.84f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF82B032)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(67.02f, 22.29f)
                reflectiveCurveToRelative(4.25f, -8.49f, 11.11f, -12.09f)
                reflectiveCurveToRelative(14.05f, -4.36f, 25.05f, -4.79f)
                reflectiveCurveToRelative(12.85f, 0.87f, 12.85f, 0.87f)
                reflectiveCurveToRelative(-8.28f, 3.92f, -16.23f, 7.62f)
                reflectiveCurveToRelative(-14.38f, 8.6f, -14.38f, 8.6f)
                lineTo(68.44f, 35.36f)
                lineTo(45.35f, 18.15f)
                reflectiveCurveToRelative(-8.06f, -4.03f, -14.7f, -6.53f)
                reflectiveCurveToRelative(-14.81f, -4.36f, -14.81f, -4.36f)
                reflectiveCurveToRelative(1.61f, -2.06f, 16.77f, -1.31f)
                curveToRelative(15.25f, 0.76f, 20.91f, 4.57f, 25.48f, 7.3f)
                reflectiveCurveToRelative(8.93f, 9.04f, 8.93f, 9.04f)
                close()
            }
            path(
                fill = radialGradient(
                    0.719f to Color(0xFFFE622B), 0.78f to Color(0xFFFB582C),
                    0.886f to Color(0xFFF23D2F), 1.0f to Color(0xFFE71932), center =
                    Offset(79.058f, 16.909f), radius = 88.809f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(68.22f, 24.69f)
                curveToRelative(2.5f, -0.22f, 7.98f, -7.82f, 22.95f, -6.61f)
                curveToRelative(14.97f, 1.2f, 37.81f, 18.2f, 31.66f, 50.08f)
                curveToRelative(-3.34f, 17.34f, -12.36f, 28.52f, -29.03f, 40.64f)
                curveToRelative(-11.78f, 8.56f, -23.77f, 14.0f, -29.31f, 14.08f)
                curveToRelative(-4.99f, 0.08f, -11.87f, -10.84f, -11.87f, -10.84f)
                lineToRelative(15.6f, -87.35f)
                close()
            }
            path(
                fill = radialGradient(
                    0.371f to Color(0xFFFF9759), 0.747f to Color(0x00FF9759),
                    center = Offset(102.96309f, 33.821556f), radius = 28.349752f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(68.22f, 24.69f)
                curveToRelative(2.5f, -0.22f, 7.98f, -7.82f, 22.95f, -6.61f)
                curveToRelative(14.04f, 1.13f, 34.99f, 16.14f, 32.49f, 44.28f)
                curveToRelative(-0.17f, 1.88f, -2.09f, 9.95f, -5.88f, 10.35f)
                curveToRelative(-18.48f, 1.98f, -27.32f, -20.64f, -32.08f, -26.6f)
                curveToRelative(-9.1f, -11.37f, -17.48f, -21.42f, -17.48f, -21.42f)
                close()
            }
            path(
                fill = radialGradient(
                    0.481f to Color(0xFFFF9759), 1.0f to Color(0x00FF9759),
                    center = Offset(53.531f, 15.482f), radius = 50.699f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(67.53f, 24.89f)
                curveToRelative(2.5f, -0.22f, 8.67f, -8.02f, 23.64f, -6.82f)
                curveToRelative(11.37f, 0.91f, 27.26f, 10.93f, 31.58f, 29.65f)
                curveToRelative(1.01f, 4.4f, -4.78f, 9.44f, -8.55f, 10.09f)
                curveToRelative(-11.52f, 1.99f, -25.3f, -14.74f, -30.07f, -20.7f)
                curveToRelative(-6.47f, -8.09f, -11.74f, -9.86f, -14.47f, -10.78f)
                curveToRelative(-1.1f, -0.38f, -2.13f, -1.44f, -2.13f, -1.44f)
                close()
            }
            path(
                fill = radialGradient(
                    0.701f to Color(0xFFFE622B), 0.766f to Color(0xFFFD5D2B),
                    0.84f to Color(0xFFF8502D), 0.917f to Color(0xFFF1392F), 0.998f to
                            Color(0xFFE71A32), 0.999f to Color(0xFFE71932), center =
                    Offset(55.211f, 29.084f), radius = 76.871f
                ), stroke = null, strokeLineWidth =
                0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(88.38f, 66.94f)
                lineToRelative(-2.19f, 23.04f)
                lineToRelative(-9.53f, 21.65f)
                lineToRelative(-7.95f, 9.73f)
                reflectiveCurveToRelative(-2.27f, 1.42f, -4.21f, 1.51f)
                curveToRelative(-1.95f, 0.08f, -16.45f, -4.09f, -29.36f, -12.83f)
                reflectiveCurveTo(11.98f, 90.83f, 7.53f, 79.06f)
                curveToRelative(-5.56f, -14.7f, -5.76f, -36.95f, 7.55f, -49.26f)
                reflectiveCurveTo(41.5f, 15.7f, 52.62f, 18.08f)
                reflectiveCurveToRelative(14.5f, 6.16f, 16.69f, 7.95f)
                reflectiveCurveToRelative(11.52f, 13.11f, 11.52f, 13.11f)
                lineToRelative(7.35f, 18.87f)
                lineToRelative(0.2f, 8.93f)
                close()
            }
            path(
                fill = radialGradient(
                    0.638f to Color(0xFFFF9759), 1.0f to Color(0x00FF9759),
                    center = Offset(44.68143f, 18.366055f), radius = 73.078835f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(56.83f, 95.2f)
                curveToRelative(-24.2f, 3.0f, -44.92f, -11.51f, -49.37f, -23.28f)
                curveToRelative(-3.08f, -8.13f, -4.44f, -11.44f, -2.86f, -21.24f)
                curveToRelative(1.28f, -7.9f, 4.25f, -14.99f, 10.47f, -20.88f)
                curveToRelative(12.41f, -11.75f, 26.57f, -14.39f, 38.15f, -11.71f)
                curveToRelative(11.08f, 2.56f, 13.89f, 6.15f, 16.07f, 7.94f)
                reflectiveCurveToRelative(11.52f, 13.11f, 11.52f, 13.11f)
                lineToRelative(7.35f, 18.87f)
                curveToRelative(0.02f, 0.0f, 1.52f, 33.12f, -31.33f, 37.19f)
                close()
            }
            path(
                fill = radialGradient(
                    0.499f to Color(0xFFF34124), 0.999f to Color(0xFFD10D22),
                    center = Offset(77.948f, 17.422f), radius = 85.745f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(67.91f, 121.45f)
                curveToRelative(0.43f, 0.12f, 4.58f, -1.88f, 7.19f, -5.13f)
                curveToRelative(7.01f, -8.75f, 15.91f, -20.91f, 17.49f, -43.44f)
                curveToRelative(2.37f, -33.8f, -19.57f, -46.25f, -20.9f, -46.85f)
                curveToRelative(-1.33f, -0.59f, -3.26f, -0.59f, -1.78f, 1.04f)
                curveToRelative(1.48f, 1.63f, 17.94f, 20.01f, 15.71f, 45.07f)
                curveToRelative(-1.99f, 22.42f, -10.82f, 39.58f, -13.94f, 43.44f)
                curveToRelative(-3.1f, 3.85f, -5.02f, 5.51f, -3.77f, 5.87f)
                close()
            }
            path(
                fill = radialGradient(
                    0.345f to Color(0xFFFEBD92), 1.0f to Color(0x00FEBD92),
                    center = Offset(33.433765f, 35.615208f), radius = 23.32952f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(28.31f, 50.94f)
                reflectiveCurveToRelative(-3.16f, 7.63f, -9.52f, 2.6f)
                curveToRelative(-3.46f, -2.74f, -3.23f, -9.81f, 0.14f, -15.71f)
                curveToRelative(4.12f, -7.24f, 11.89f, -12.22f, 22.26f, -13.63f)
                curveToRelative(9.26f, -1.26f, 15.55f, 1.37f, 16.7f, 6.83f)
                curveToRelative(1.4f, 6.6f, -4.47f, 6.33f, -4.47f, 6.33f)
                curveToRelative(-16.47f, -0.52f, -22.7f, 8.1f, -25.11f, 13.58f)
                close()
            }
            path(
                fill = radialGradient(
                    0.799f to Color(0xFFFF9759), 1.0f to Color(0x00FF9759),
                    center = Offset(43.61016f, 51.033573f), radius = 22.379402f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(23.62f, 57.97f)
                curveToRelative(-0.64f, -7.23f, 3.9f, -15.39f, 11.49f, -20.01f)
                curveToRelative(7.8f, -4.75f, 17.51f, -5.37f, 24.99f, -1.66f)
                lineTo(23.62f, 57.97f)
                close()
            }
            path(
                fill = radialGradient(
                    0.745f to Color(0x00FF9759), 0.974f to Color(0xFFFF9759),
                    center = Offset(35.596222f, 39.583282f), radius = 22.276033f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(28.31f, 50.94f)
                reflectiveCurveToRelative(-3.16f, 7.63f, -9.52f, 2.6f)
                curveToRelative(-3.46f, -2.74f, -3.23f, -9.81f, 0.14f, -15.71f)
                curveToRelative(4.12f, -7.24f, 11.89f, -12.22f, 22.26f, -13.63f)
                curveToRelative(9.26f, -1.26f, 15.55f, 1.37f, 16.7f, 6.83f)
                curveToRelative(1.4f, 6.6f, -4.47f, 6.33f, -4.47f, 6.33f)
                curveToRelative(-16.47f, -0.52f, -22.7f, 8.1f, -25.11f, 13.58f)
                close()
            }
        }
            .build()
        return _peach!!
    }

private var _peach: ImageVector? = null
