package ru.tech.imageresizershrinker.presentation.theme.emoji

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
import ru.tech.imageresizershrinker.presentation.theme.Emoji

val Emoji.Siren: ImageVector
    get() {
        if (_siren != null) {
            return _siren!!
        }
        _siren = Builder(
            name = "Siren", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(118.4f, 112.29f)
                curveToRelative(-0.54f, -2.88f, -2.62f, -18.81f, -2.62f, -18.81f)
                horizontalLineTo(12.22f)
                reflectiveCurveToRelative(-2.08f, 15.93f, -2.62f, 18.81f)
                curveTo(9.06f, 115.17f, 15.23f, 124.0f, 64.0f, 124.0f)
                reflectiveCurveToRelative(54.94f, -8.83f, 54.4f, -11.71f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFE0E0E0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.19f, 93.96f)
                curveToRelative(0.0f, -5.87f, 23.2f, -10.63f, 51.81f, -10.63f)
                reflectiveCurveToRelative(51.81f, 4.76f, 51.81f, 10.63f)
                reflectiveCurveToRelative(-23.2f, 11.6f, -51.81f, 11.6f)
                reflectiveCurveToRelative(-51.81f, -5.73f, -51.81f, -11.6f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(97.78f, 16.32f)
                curveTo(94.94f, 7.27f, 85.48f, 4.0f, 63.99f, 4.0f)
                reflectiveCurveTo(33.05f, 7.27f, 30.21f, 16.32f)
                curveToRelative(-5.72f, 18.23f, -10.86f, 75.71f, -10.86f, 75.71f)
                reflectiveCurveToRelative(4.47f, 7.49f, 44.65f, 7.49f)
                reflectiveCurveToRelative(44.66f, -7.48f, 44.66f, -7.48f)
                reflectiveCurveToRelative(-5.16f, -57.49f, -10.88f, -75.72f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC62828)), stroke = null, fillAlpha = 0.9f, strokeAlpha
                = 0.9f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(19.34f, 92.14f)
                curveToRelative(0.0f, -4.6f, 19.99f, -8.33f, 44.66f, -8.33f)
                reflectiveCurveToRelative(44.66f, 3.73f, 44.66f, 8.33f)
                reflectiveCurveToRelative(-19.99f, 9.09f, -44.66f, 9.09f)
                reflectiveCurveToRelative(-44.66f, -4.49f, -44.66f, -9.09f)
                close()
            }
            path(
                fill = radialGradient(
                    0.572f to Color(0xFFFF6E40), 0.768f to Color(0x89FF7046),
                    1.0f to Color(0x00FF7555), center = Offset(64.0012f, 53.000744f), radius =
                    43.590435f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(72.59f, 35.73f)
                lineTo(94.42f, 13.2f)
                curveToRelative(0.42f, -0.37f, 1.04f, 0.1f, 0.8f, 0.6f)
                lineTo(78.98f, 39.86f)
                curveToRelative(-1.21f, 2.52f, -0.14f, 4.15f, 2.66f, 4.08f)
                lineToRelative(20.65f, -0.21f)
                curveToRelative(0.56f, -0.01f, 0.73f, 0.75f, 0.22f, 0.98f)
                lineToRelative(-18.84f, 7.26f)
                curveToRelative(-2.55f, 1.15f, -3.09f, 3.88f, -0.91f, 5.62f)
                lineToRelative(23.49f, 16.94f)
                curveToRelative(0.44f, 0.35f, 0.08f, 1.04f, -0.46f, 0.89f)
                lineTo(79.06f, 64.35f)
                curveToRelative(-2.69f, -0.76f, -3.92f, 0.73f, -3.37f, 3.47f)
                lineToRelative(8.62f, 28.89f)
                curveToRelative(0.11f, 0.55f, -0.61f, 0.85f, -0.92f, 0.39f)
                lineTo(68.68f, 71.05f)
                curveToRelative(-1.57f, -2.31f, -3.47f, -2.75f, -4.8f, -0.3f)
                lineTo(49.77f, 97.43f)
                curveToRelative(-0.27f, 0.49f, -1.01f, 0.26f, -0.96f, -0.3f)
                lineToRelative(7.29f, -28.79f)
                curveToRelative(0.28f, -2.78f, -1.0f, -3.36f, -3.6f, -2.34f)
                lineTo(22.25f, 81.51f)
                curveToRelative(-0.52f, 0.2f, -0.94f, -0.45f, -0.54f, -0.84f)
                lineTo(47.09f, 59.6f)
                curveToRelative(2.0f, -1.95f, 1.91f, -4.08f, -0.74f, -4.97f)
                lineToRelative(-21.18f, -7.22f)
                curveToRelative(-0.53f, -0.18f, -0.43f, -0.95f, 0.12f, -0.99f)
                lineToRelative(20.84f, 0.4f)
                curveToRelative(2.79f, -0.21f, 4.2f, -2.99f, 2.74f, -5.37f)
                lineTo(32.12f, 17.3f)
                curveToRelative(-0.29f, -0.48f, 0.28f, -1.01f, 0.73f, -0.68f)
                lineToRelative(22.34f, 19.4f)
                curveToRelative(2.27f, 1.63f, 4.26f, 1.48f, 4.67f, -1.28f)
                lineToRelative(3.63f, -26.0f)
                curveToRelative(0.08f, -0.55f, 0.86f, -0.59f, 1.0f, -0.05f)
                lineToRelative(3.16f, 25.38f)
                curveToRelative(0.7f, 2.71f, 2.84f, 3.51f, 4.94f, 1.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD5CA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(65.85f, 34.06f)
                lineToRelative(9.36f, -16.89f)
                curveToRelative(0.28f, -0.48f, 1.02f, -0.23f, 0.95f, 0.32f)
                lineToRelative(-2.92f, 18.45f)
                curveToRelative(-0.34f, 2.77f, 1.2f, 3.98f, 3.82f, 3.02f)
                lineToRelative(18.33f, -6.16f)
                curveToRelative(0.52f, -0.19f, 0.93f, 0.47f, 0.52f, 0.85f)
                lineTo(81.55f, 45.91f)
                curveToRelative(-2.05f, 1.9f, -1.69f, 4.66f, 0.94f, 5.61f)
                lineToRelative(18.32f, 6.21f)
                curveToRelative(0.52f, 0.19f, 0.41f, 0.96f, -0.15f, 0.99f)
                lineToRelative(-19.52f, 0.39f)
                curveToRelative(-2.79f, 0.14f, -3.48f, 1.95f, -2.08f, 4.37f)
                lineToRelative(9.86f, 16.84f)
                curveToRelative(0.28f, 0.48f, -0.31f, 1.0f, -0.75f, 0.66f)
                lineToRelative(-14.73f, -12.2f)
                curveToRelative(-2.23f, -1.68f, -4.16f, -1.5f, -4.64f, 1.25f)
                lineToRelative(-3.5f, 19.94f)
                curveToRelative(-0.1f, 0.55f, -0.88f, 0.57f, -1.0f, 0.03f)
                lineToRelative(-3.64f, -19.77f)
                curveToRelative(-0.62f, -2.72f, -2.02f, -2.86f, -4.16f, -1.07f)
                lineTo(41.0f, 82.19f)
                curveToRelative(-0.43f, 0.36f, -1.04f, -0.13f, -0.78f, -0.62f)
                lineToRelative(9.11f, -16.74f)
                curveToRelative(1.27f, -2.49f, 0.51f, -4.47f, -2.29f, -4.47f)
                lineToRelative(-19.66f, 0.25f)
                curveToRelative(-0.56f, 0.0f, -0.71f, -0.76f, -0.2f, -0.98f)
                lineToRelative(17.16f, -6.61f)
                curveToRelative(2.57f, -1.09f, 3.02f, -4.17f, 0.88f, -5.97f)
                lineToRelative(-14.4f, -11.73f)
                curveToRelative(-0.43f, -0.36f, -0.05f, -1.04f, 0.48f, -0.88f)
                lineToRelative(18.17f, 5.44f)
                curveToRelative(2.67f, 0.82f, 4.51f, 0.04f, 4.02f, -2.71f)
                lineToRelative(-3.77f, -19.01f)
                curveToRelative(-0.1f, -0.55f, 0.63f, -0.83f, 0.93f, -0.37f)
                lineToRelative(10.01f, 16.26f)
                curveToRelative(1.51f, 2.35f, 3.8f, 2.43f, 5.19f, 0.01f)
                close()
            }
        }
            .build()
        return _siren!!
    }

private var _siren: ImageVector? = null
