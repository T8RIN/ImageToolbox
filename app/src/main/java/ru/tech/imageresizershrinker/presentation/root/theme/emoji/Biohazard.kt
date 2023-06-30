package ru.tech.imageresizershrinker.presentation.root.theme.emoji

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

val Emoji.Biohazard: ImageVector
    get() {
        if (_biohazard != null) {
            return _biohazard!!
        }
        _biohazard = Builder(
            name = "Biohazard", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF2A600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(64.46f, 63.96f)
                moveToRelative(-60.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, 120.0f, 0.0f)
                arcToRelative(60.0f, 60.0f, 0.0f, true, true, -120.0f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFFFCC32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.56f, 63.06f)
                moveToRelative(-56.1f, 0.0f)
                arcToRelative(56.1f, 56.1f, 0.0f, true, true, 112.2f, 0.0f)
                arcToRelative(56.1f, 56.1f, 0.0f, true, true, -112.2f, 0.0f)
            }
            path(
                fill = SolidColor(Color(0xFFFFF170)), stroke = null, fillAlpha = 0.65f, strokeAlpha
                = 0.65f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(24.46f, 29.66f)
                curveToRelative(4.5f, -7.1f, 14.1f, -13.0f, 24.1f, -14.8f)
                curveToRelative(2.5f, -0.4f, 5.0f, -0.6f, 7.1f, 0.2f)
                curveToRelative(1.6f, 0.6f, 2.9f, 2.1f, 2.0f, 3.8f)
                curveToRelative(-0.7f, 1.4f, -2.6f, 2.0f, -4.1f, 2.5f)
                arcToRelative(44.64f, 44.64f, 0.0f, false, false, -23.0f, 17.4f)
                curveToRelative(-2.0f, 3.0f, -5.0f, 11.3f, -8.7f, 9.2f)
                curveToRelative(-3.9f, -2.3f, -3.1f, -9.5f, 2.6f, -18.3f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(52.86f, 45.96f)
                curveToRelative(6.41f, -4.09f, 14.5f, -4.51f, 21.3f, -1.1f)
                curveToRelative(1.0f, 0.5f, 3.2f, 1.5f, 1.8f, 2.8f)
                curveToRelative(-0.59f, 0.57f, -1.31f, 0.98f, -2.1f, 1.2f)
                curveToRelative(-0.6f, 0.0f, -1.2f, -0.6f, -1.8f, -0.8f)
                curveToRelative(-3.47f, -1.61f, -7.34f, -2.1f, -11.1f, -1.4f)
                curveToRelative(-1.03f, 0.21f, -2.03f, 0.51f, -3.0f, 0.9f)
                curveToRelative(-0.9f, 0.3f, -1.8f, 1.1f, -2.7f, 1.3f)
                reflectiveCurveToRelative(-4.0f, -1.9f, -2.4f, -2.9f)
                close()
                moveTo(37.96f, 62.26f)
                curveToRelative(-7.14f, 0.3f, -13.71f, 3.97f, -17.7f, 9.9f)
                curveToRelative(-0.92f, 1.41f, -1.69f, 2.92f, -2.3f, 4.5f)
                curveToRelative(-0.28f, 0.72f, -0.51f, 1.45f, -0.7f, 2.2f)
                curveToRelative(-0.1f, 0.4f, -0.1f, 1.2f, -0.7f, 1.1f)
                curveToRelative(-1.4f, -0.1f, 1.0f, -7.4f, 1.3f, -8.3f)
                arcToRelative(26.312f, 26.312f, 0.0f, false, true, 14.0f, -14.4f)
                curveToRelative(2.96f, -1.3f, 6.16f, -1.98f, 9.4f, -2.0f)
                horizontalLineToRelative(2.4f)
                curveToRelative(0.5f, 0.0f, 1.1f, 0.0f, 1.4f, -0.6f)
                reflectiveCurveToRelative(-0.4f, -1.3f, -0.8f, -1.8f)
                curveToRelative(-0.5f, -0.7f, -1.0f, -1.3f, -1.5f, -2.0f)
                arcToRelative(25.805f, 25.805f, 0.0f, false, true, -3.5f, -20.6f)
                curveToRelative(0.89f, -3.52f, 2.49f, -6.82f, 4.7f, -9.7f)
                curveToRelative(0.3f, -0.3f, 0.8f, -1.3f, 1.3f, -1.3f)
                reflectiveCurveToRelative(0.5f, 0.3f, 0.4f, 0.6f)
                curveToRelative(-0.3f, 0.75f, -0.71f, 1.46f, -1.2f, 2.1f)
                curveToRelative(-0.4f, 0.9f, -0.8f, 1.7f, -1.1f, 2.6f)
                curveToRelative(-2.28f, 6.81f, -1.2f, 14.3f, 2.9f, 20.2f)
                curveToRelative(2.1f, 2.91f, 4.83f, 5.31f, 8.0f, 7.0f)
                curveToRelative(1.55f, 0.81f, 3.2f, 1.41f, 4.9f, 1.8f)
                curveToRelative(0.82f, 0.21f, 1.66f, 0.38f, 2.5f, 0.5f)
                curveToRelative(0.6f, 0.1f, 1.2f, 0.2f, 1.4f, 0.8f)
                curveToRelative(0.3f, 1.1f, -0.2f, 1.7f, -1.2f, 2.1f)
                arcToRelative(6.775f, 6.775f, 0.0f, false, false, -3.3f, 2.5f)
                arcToRelative(7.626f, 7.626f, 0.0f, false, false, -1.5f, 4.1f)
                curveToRelative(-0.05f, 0.84f, 0.05f, 1.69f, 0.3f, 2.5f)
                curveToRelative(0.2f, 0.7f, 0.9f, 1.6f, 0.9f, 2.3f)
                curveToRelative(-0.22f, 0.91f, -0.91f, 1.63f, -1.8f, 1.9f)
                curveToRelative(-0.8f, 0.1f, -1.3f, -0.9f, -1.8f, -1.4f)
                curveToRelative(-0.7f, -0.7f, -1.4f, -1.3f, -2.1f, -1.9f)
                arcToRelative(22.294f, 22.294f, 0.0f, false, false, -9.4f, -4.3f)
                curveToRelative(-1.71f, -0.33f, -3.46f, -0.46f, -5.2f, -0.4f)
                close()
                moveTo(57.06f, 83.96f)
                curveToRelative(-0.82f, -0.15f, -1.6f, -0.46f, -2.3f, -0.9f)
                curveToRelative(-0.8f, -0.4f, -1.6f, -0.9f, -2.4f, -1.4f)
                curveToRelative(-1.35f, -0.93f, -2.59f, -2.0f, -3.7f, -3.2f)
                curveToRelative(-2.31f, -2.5f, -3.99f, -5.52f, -4.9f, -8.8f)
                curveToRelative(-0.3f, -0.9f, -1.2f, -3.2f, 0.1f, -3.7f)
                curveToRelative(0.92f, -0.07f, 1.84f, 0.17f, 2.6f, 0.7f)
                curveToRelative(0.7f, 0.5f, 0.7f, 1.4f, 0.9f, 2.2f)
                curveToRelative(0.9f, 3.18f, 2.71f, 6.03f, 5.2f, 8.2f)
                curveToRelative(1.41f, 1.09f, 2.92f, 2.06f, 4.5f, 2.9f)
                curveToRelative(1.1f, 0.6f, 1.7f, 4.0f, 0.0f, 4.0f)
                close()
                moveTo(74.36f, 100.26f)
                curveToRelative(1.93f, 1.93f, 4.2f, 3.49f, 6.7f, 4.6f)
                curveToRelative(1.17f, 0.49f, 2.37f, 0.89f, 3.6f, 1.2f)
                curveToRelative(0.3f, 0.1f, 0.9f, 0.1f, 1.0f, 0.5f)
                curveToRelative(0.1f, 0.6f, -0.5f, 0.6f, -0.8f, 0.5f)
                curveToRelative(-5.1f, -0.1f, -10.4f, -2.5f, -14.3f, -5.7f)
                curveToRelative(-1.04f, -0.86f, -2.01f, -1.79f, -2.9f, -2.8f)
                curveToRelative(-0.4f, -0.5f, -0.8f, -0.9f, -1.2f, -1.4f)
                reflectiveCurveToRelative(-0.9f, -1.5f, -1.5f, -1.8f)
                curveToRelative(-1.1f, -0.6f, -1.9f, 1.3f, -2.5f, 2.0f)
                curveToRelative(-0.92f, 1.14f, -1.92f, 2.21f, -3.0f, 3.2f)
                arcToRelative(25.647f, 25.647f, 0.0f, false, true, -13.5f, 6.3f)
                curveToRelative(-0.5f, 0.1f, -2.2f, 0.6f, -2.5f, 0.1f)
                curveToRelative(-0.6f, -0.9f, 2.2f, -1.3f, 2.7f, -1.4f)
                curveToRelative(2.43f, -0.83f, 4.7f, -2.08f, 6.7f, -3.7f)
                curveToRelative(4.45f, -3.63f, 7.26f, -8.89f, 7.8f, -14.6f)
                curveToRelative(0.24f, -2.86f, -0.04f, -5.74f, -0.8f, -8.5f)
                curveToRelative(-0.3f, -1.3f, -0.7f, -2.5f, -1.1f, -3.8f)
                curveToRelative(-0.3f, -0.9f, -0.9f, -1.7f, -0.3f, -2.5f)
                curveToRelative(0.41f, -0.46f, 0.84f, -0.89f, 1.3f, -1.3f)
                curveToRelative(0.8f, -0.8f, 1.3f, -0.5f, 2.3f, -0.1f)
                curveToRelative(1.33f, 0.42f, 2.74f, 0.49f, 4.1f, 0.2f)
                curveToRelative(1.0f, -0.2f, 1.8f, -1.0f, 2.7f, -0.3f)
                curveToRelative(0.55f, 0.45f, 1.05f, 0.95f, 1.5f, 1.5f)
                curveToRelative(0.7f, 0.8f, 0.0f, 1.7f, -0.3f, 2.5f)
                curveToRelative(-0.9f, 2.81f, -1.6f, 5.69f, -2.1f, 8.6f)
                curveToRelative(-0.8f, 6.0f, 2.2f, 12.5f, 6.4f, 16.7f)
                close()
                moveTo(85.36f, 68.56f)
                curveToRelative(-0.73f, 3.39f, -2.28f, 6.54f, -4.5f, 9.2f)
                arcToRelative(23.394f, 23.394f, 0.0f, false, true, -4.0f, 3.7f)
                curveToRelative(-0.68f, 0.47f, -1.38f, 0.9f, -2.1f, 1.3f)
                curveToRelative(-0.83f, 0.54f, -1.74f, 0.95f, -2.7f, 1.2f)
                curveToRelative(-1.4f, 0.2f, -1.2f, -1.5f, -1.1f, -2.4f)
                reflectiveCurveToRelative(0.1f, -1.2f, 0.9f, -1.5f)
                curveToRelative(0.8f, -0.37f, 1.57f, -0.81f, 2.3f, -1.3f)
                curveToRelative(2.66f, -1.7f, 4.77f, -4.13f, 6.1f, -7.0f)
                curveToRelative(0.47f, -0.92f, 0.84f, -1.9f, 1.1f, -2.9f)
                curveToRelative(0.2f, -0.8f, 0.3f, -1.8f, 1.1f, -2.3f)
                curveToRelative(0.9f, -0.56f, 2.08f, -0.29f, 2.65f, 0.61f)
                curveToRelative(0.25f, 0.42f, 0.34f, 0.91f, 0.25f, 1.39f)
                close()
                moveTo(112.66f, 79.76f)
                curveToRelative(-0.8f, 0.9f, -1.5f, -2.7f, -1.7f, -3.2f)
                arcToRelative(18.83f, 18.83f, 0.0f, false, false, -2.3f, -4.4f)
                curveToRelative(-1.91f, -2.9f, -4.48f, -5.29f, -7.5f, -7.0f)
                arcToRelative(22.704f, 22.704f, 0.0f, false, false, -20.9f, -0.8f)
                curveToRelative(-1.72f, 0.78f, -3.3f, 1.83f, -4.7f, 3.1f)
                curveToRelative(-0.6f, 0.5f, -1.2f, 1.1f, -1.8f, 1.7f)
                curveToRelative(-0.4f, 0.4f, -0.7f, 0.9f, -1.3f, 1.0f)
                reflectiveCurveToRelative(-1.5f, -0.9f, -1.8f, -1.4f)
                reflectiveCurveToRelative(0.0f, -0.9f, 0.2f, -1.4f)
                curveToRelative(0.42f, -0.81f, 0.69f, -1.69f, 0.8f, -2.6f)
                arcToRelative(7.331f, 7.331f, 0.0f, false, false, -3.9f, -7.4f)
                curveToRelative(-0.5f, -0.3f, -1.4f, -0.4f, -1.8f, -0.8f)
                curveToRelative(-0.36f, -0.53f, -0.44f, -1.2f, -0.2f, -1.8f)
                arcToRelative(0.99f, 0.99f, 0.0f, false, true, 0.7f, -0.7f)
                curveToRelative(0.92f, -0.22f, 1.86f, -0.39f, 2.8f, -0.5f)
                curveToRelative(7.17f, -1.6f, 13.11f, -6.6f, 15.9f, -13.4f)
                curveToRelative(1.41f, -3.41f, 1.89f, -7.14f, 1.4f, -10.8f)
                curveToRelative(-0.2f, -1.77f, -0.6f, -3.52f, -1.2f, -5.2f)
                curveToRelative(-0.27f, -0.76f, -0.61f, -1.5f, -1.0f, -2.2f)
                curveToRelative(-0.49f, -0.64f, -0.9f, -1.35f, -1.2f, -2.1f)
                curveToRelative(0.0f, -0.3f, 0.1f, -0.6f, 0.4f, -0.6f)
                curveToRelative(0.5f, 0.0f, 1.1f, 1.0f, 1.3f, 1.4f)
                curveToRelative(1.02f, 1.36f, 1.92f, 2.8f, 2.7f, 4.3f)
                curveToRelative(1.59f, 3.11f, 2.51f, 6.51f, 2.7f, 10.0f)
                curveToRelative(0.23f, 3.46f, -0.24f, 6.93f, -1.4f, 10.2f)
                curveToRelative(-0.6f, 1.58f, -1.3f, 3.11f, -2.1f, 4.6f)
                curveToRelative(-0.8f, 1.4f, -2.0f, 2.6f, -2.8f, 3.9f)
                curveToRelative(-0.34f, 0.47f, -0.25f, 1.13f, 0.2f, 1.5f)
                curveToRelative(0.5f, 0.3f, 1.3f, 0.1f, 1.8f, 0.1f)
                curveToRelative(1.68f, -0.02f, 3.35f, 0.11f, 5.0f, 0.4f)
                arcToRelative(26.044f, 26.044f, 0.0f, false, true, 21.0f, 19.5f)
                curveToRelative(0.22f, 0.82f, 0.39f, 1.66f, 0.5f, 2.5f)
                curveToRelative(0.2f, 0.3f, 0.6f, 1.7f, 0.2f, 2.1f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, fillAlpha = 0.3f, strokeAlpha
                = 0.3f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(24.46f, 29.66f)
                curveToRelative(4.5f, -7.1f, 14.1f, -13.0f, 24.1f, -14.8f)
                curveToRelative(2.5f, -0.4f, 5.0f, -0.6f, 7.1f, 0.2f)
                curveToRelative(1.6f, 0.6f, 2.9f, 2.1f, 2.0f, 3.8f)
                curveToRelative(-0.7f, 1.4f, -2.6f, 2.0f, -4.1f, 2.5f)
                arcToRelative(44.64f, 44.64f, 0.0f, false, false, -23.0f, 17.4f)
                curveToRelative(-2.0f, 3.0f, -5.0f, 11.3f, -8.7f, 9.2f)
                curveToRelative(-3.9f, -2.3f, -3.1f, -9.5f, 2.6f, -18.3f)
                close()
            }
        }
            .build()
        return _biohazard!!
    }

private var _biohazard: ImageVector? = null
