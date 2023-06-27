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

val Emoji.Pushpin: ImageVector
    get() {
        if (_pushpin != null) {
            return _pushpin!!
        }
        _pushpin = Builder(
            name = "Pushpin", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF82AEC0)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(44.27f, 77.03f)
                curveToRelative(0.62f, -0.64f, 1.26f, -1.26f, 1.93f, -1.83f)
                curveToRelative(1.97f, -1.65f, 7.09f, -5.48f, 9.85f, -3.89f)
                curveToRelative(1.64f, 0.95f, 1.4f, 4.1f, 0.8f, 5.41f)
                curveToRelative(-1.49f, 3.24f, -5.31f, 5.97f, -7.64f, 8.67f)
                curveToRelative(-1.84f, 2.14f, -4.04f, 4.05f, -6.05f, 6.05f)
                curveToRelative(-3.2f, 3.21f, -6.27f, 6.81f, -9.74f, 9.74f)
                curveToRelative(-0.01f, 0.0f, -21.31f, 17.99f, -26.56f, 22.43f)
                curveToRelative(-0.66f, 0.56f, -1.63f, 0.51f, -2.24f, -0.09f)
                curveToRelative(-0.61f, -0.61f, -0.65f, -1.58f, -0.1f, -2.24f)
                lineTo(26.95f, 94.7f)
                curveToRelative(4.39f, -4.39f, 8.79f, -8.79f, 13.18f, -13.19f)
                curveToRelative(1.41f, -1.41f, 2.74f, -3.02f, 4.14f, -4.48f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2F7889)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(56.88f, 76.61f)
                curveToRelative(-0.71f, -0.89f, -1.48f, -1.77f, -2.32f, -2.62f)
                curveToRelative(-0.88f, -0.88f, -1.8f, -1.69f, -2.73f, -2.42f)
                curveToRelative(-2.22f, 0.93f, -4.47f, 2.65f, -5.63f, 3.63f)
                curveToRelative(-0.67f, 0.56f, -1.31f, 1.18f, -1.93f, 1.83f)
                curveToRelative(-1.4f, 1.47f, -2.72f, 3.07f, -4.14f, 4.48f)
                curveToRelative(-1.33f, 1.33f, -2.67f, 2.67f, -4.0f, 4.01f)
                curveToRelative(0.0f, 0.0f, -0.24f, 2.06f, 2.11f, 4.41f)
                curveToRelative(2.35f, 2.35f, 4.3f, 2.14f, 4.3f, 2.14f)
                curveToRelative(0.21f, -0.21f, 0.41f, -0.42f, 0.62f, -0.63f)
                curveToRelative(2.0f, -2.0f, 4.21f, -3.91f, 6.05f, -6.05f)
                curveToRelative(2.33f, -2.7f, 6.14f, -5.43f, 7.64f, -8.67f)
                curveToRelative(0.01f, -0.03f, 0.02f, -0.08f, 0.03f, -0.11f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(40.81f, 81.59f)
                curveToRelative(-4.79f, -5.16f, -9.05f, -10.75f, -12.51f, -16.54f)
                curveToRelative(-1.46f, -2.44f, -3.36f, -7.29f, -3.94f, -11.96f)
                curveToRelative(-0.67f, -5.32f, 3.26f, -10.16f, 8.6f, -10.64f)
                curveToRelative(6.54f, -0.59f, 16.67f, -0.37f, 24.02f, 5.06f)
                lineToRelative(23.65f, -23.65f)
                lineToRelative(23.65f, 23.64f)
                lineToRelative(-23.65f, 23.64f)
                curveToRelative(5.43f, 7.34f, 5.65f, 17.46f, 5.07f, 24.0f)
                curveToRelative(-0.49f, 5.41f, -5.44f, 9.32f, -10.82f, 8.58f)
                curveToRelative(-4.88f, -0.68f, -9.97f, -2.77f, -12.48f, -4.31f)
                curveToRelative(-7.74f, -4.74f, -15.15f, -10.89f, -21.59f, -17.82f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(52.77f, 50.86f)
                lineToRelative(27.86f, -27.01f)
                reflectiveCurveToRelative(5.76f, 7.62f, 10.14f, 12.09f)
                curveToRelative(4.38f, 4.46f, 13.52f, 11.56f, 13.52f, 11.56f)
                lineTo(77.32f, 74.46f)
                reflectiveCurveToRelative(-9.94f, 0.92f, -17.34f, -6.48f)
                curveToRelative(-8.62f, -8.63f, -7.21f, -17.12f, -7.21f, -17.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFC62828)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(65.96f, 56.15f)
                curveToRelative(1.2f, 8.92f, 4.49f, 13.92f, 6.22f, 15.43f)
                curveToRelative(2.79f, 2.41f, 6.75f, 1.26f, 8.45f, -0.43f)
                lineToRelative(23.65f, -23.65f)
                reflectiveCurveToRelative(-9.14f, -7.09f, -13.52f, -11.56f)
                reflectiveCurveToRelative(-10.13f, -12.09f, -10.13f, -12.09f)
                lineTo(69.99f, 34.09f)
                curveToRelative(-1.87f, 1.8f, -3.13f, 4.15f, -3.53f, 6.71f)
                curveToRelative(-0.52f, 3.39f, -1.51f, 7.88f, -0.5f, 15.35f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(78.87f, 5.52f)
                lineTo(68.38f, 16.01f)
                curveToRelative(-1.96f, 1.53f, -2.02f, 3.47f, -2.02f, 3.47f)
                curveToRelative(-1.08f, 6.02f, 4.8f, 17.13f, 14.9f, 27.24f)
                curveToRelative(10.11f, 10.11f, 21.22f, 15.98f, 27.24f, 14.9f)
                curveToRelative(0.0f, 0.0f, 1.6f, 0.13f, 3.14f, -1.69f)
                lineToRelative(10.82f, -10.82f)
                lineTo(78.87f, 5.52f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(122.46f, 49.11f)
                curveToRelative(-4.28f, 4.86f, -18.05f, -1.46f, -30.09f, -13.5f)
                reflectiveCurveTo(74.29f, 10.1f, 78.87f, 5.52f)
                curveToRelative(4.58f, -4.58f, 17.93f, 1.59f, 30.09f, 13.5f)
                curveToRelative(11.76f, 11.51f, 17.88f, 25.12f, 13.5f, 30.09f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF7555)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(53.34f, 51.32f)
                curveToRelative(-0.08f, -3.85f, -7.76f, -8.46f, -12.51f, -4.53f)
                curveToRelative(-4.01f, 3.32f, -2.39f, 10.39f, -0.34f, 14.1f)
                curveToRelative(2.27f, 4.11f, 5.49f, 7.92f, 9.71f, 9.98f)
                curveToRelative(4.23f, 2.07f, 10.59f, 0.93f, 8.35f, -3.2f)
                curveToRelative(-2.97f, -5.48f, -5.03f, -8.08f, -5.21f, -16.35f)
                close()
                moveTo(92.44f, 33.36f)
                curveToRelative(19.12f, 18.79f, 26.11f, 15.19f, 27.49f, 14.12f)
                curveToRelative(0.98f, -0.75f, 1.44f, -2.07f, 1.56f, -3.34f)
                curveToRelative(0.32f, -3.34f, -1.89f, -9.3f, -3.53f, -12.17f)
                curveToRelative(-4.27f, -7.47f, -8.36f, -11.92f, -20.4f, -20.98f)
                curveToRelative(-5.32f, -4.01f, -13.16f, -6.34f, -15.81f, -4.01f)
                curveToRelative(-3.12f, 2.74f, -3.45f, 12.48f, 10.69f, 26.38f)
                close()
            }
        }
            .build()
        return _pushpin!!
    }

private var _pushpin: ImageVector? = null
