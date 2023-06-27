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

val Emoji.Battery: ImageVector
    get() {
        if (_battery != null) {
            return _battery!!
        }
        _battery = Builder(
            name = "Battery", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF8DCC47)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(34.02f, 20.35f)
                verticalLineToRelative(88.57f)
                curveToRelative(0.0f, 7.92f, 13.42f, 14.34f, 29.98f, 14.34f)
                curveToRelative(16.56f, 0.0f, 29.98f, -6.42f, 29.98f, -14.34f)
                verticalLineTo(20.35f)
                horizontalLineTo(34.02f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9EE350)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(94.0f, 20.36f)
                curveToRelative(0.0f, 9.44f, -15.79f, 15.37f, -29.98f, 15.37f)
                curveToRelative(-13.82f, 0.0f, -29.02f, -5.96f, -29.98f, -15.37f)
                curveToRelative(-1.03f, -10.14f, 13.42f, -17.1f, 29.98f, -17.1f)
                reflectiveCurveTo(94.0f, 10.92f, 94.0f, 20.36f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD9D9D9)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(40.56f, 17.55f)
                arcToRelative(23.64f, 11.43f, 0.0f, true, false, 47.28f, 0.0f)
                arcToRelative(23.64f, 11.43f, 0.0f, true, false, -47.28f, 0.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(76.56f, 6.44f)
                verticalLineToRelative(0.66f)
                curveToRelative(0.03f, -0.41f, 0.0f, -0.66f, 0.0f, -0.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(76.99f, 51.54f)
                curveToRelative(0.0f, -0.66f, -0.25f, -1.06f, -0.86f, -1.3f)
                curveToRelative(-0.57f, -0.23f, -1.4f, 0.01f, -2.25f, 0.23f)
                curveToRelative(-1.68f, 0.42f, -3.41f, 0.66f, -4.97f, 0.64f)
                curveToRelative(-0.63f, -0.01f, -0.57f, -0.61f, -0.57f, -1.16f)
                verticalLineToRelative(-5.48f)
                curveToRelative(0.0f, -0.6f, -0.07f, -1.36f, -1.18f, -1.36f)
                curveToRelative(-0.67f, 0.0f, -1.48f, 0.18f, -3.17f, 0.2f)
                curveToRelative(-1.69f, -0.01f, -2.5f, -0.2f, -3.17f, -0.2f)
                curveToRelative(-1.11f, 0.0f, -1.18f, 0.76f, -1.18f, 1.36f)
                verticalLineToRelative(5.48f)
                curveToRelative(0.0f, 0.55f, 0.06f, 1.15f, -0.57f, 1.16f)
                curveToRelative(-1.56f, 0.02f, -3.29f, -0.22f, -4.97f, -0.64f)
                curveToRelative(-0.86f, -0.21f, -1.68f, -0.45f, -2.25f, -0.23f)
                curveToRelative(-0.61f, 0.25f, -0.86f, 0.64f, -0.86f, 1.3f)
                lineToRelative(-0.03f, 5.09f)
                curveToRelative(0.28f, 0.79f, 0.57f, 1.36f, 0.89f, 1.53f)
                curveToRelative(0.94f, 0.49f, 2.99f, 1.24f, 6.27f, 1.4f)
                curveToRelative(0.65f, 0.03f, 1.38f, -0.19f, 1.53f, 0.52f)
                curveToRelative(0.07f, 0.35f, 0.0f, 4.67f, 0.0f, 6.42f)
                curveToRelative(0.0f, 1.59f, 2.3f, 1.76f, 3.54f, 1.86f)
                curveToRelative(0.27f, 0.02f, 1.35f, 0.02f, 1.61f, 0.0f)
                curveToRelative(1.23f, -0.11f, 3.54f, -0.27f, 3.54f, -1.86f)
                curveToRelative(0.0f, -1.75f, -0.07f, -6.07f, 0.0f, -6.42f)
                curveToRelative(0.15f, -0.71f, 0.87f, -0.49f, 1.53f, -0.52f)
                curveToRelative(3.28f, -0.16f, 5.33f, -0.92f, 6.27f, -1.4f)
                curveToRelative(0.31f, -0.16f, 0.61f, -0.73f, 0.89f, -1.53f)
                lineToRelative(-0.04f, -5.09f)
                close()
                moveTo(75.07f, 102.56f)
                reflectiveCurveToRelative(-3.85f, 1.17f, -11.07f, 1.17f)
                reflectiveCurveToRelative(-11.07f, -1.17f, -11.07f, -1.17f)
                curveToRelative(-1.37f, -0.2f, -1.95f, 0.87f, -1.95f, 1.95f)
                verticalLineToRelative(4.22f)
                curveToRelative(0.0f, 1.0f, 0.65f, 1.93f, 1.81f, 2.5f)
                curveToRelative(2.39f, 1.18f, 6.25f, 1.98f, 10.87f, 1.93f)
                curveToRelative(0.12f, 0.0f, 0.23f, -0.01f, 0.35f, -0.01f)
                curveToRelative(0.12f, 0.0f, 0.23f, 0.01f, 0.35f, 0.01f)
                curveToRelative(4.63f, 0.05f, 8.48f, -0.75f, 10.87f, -1.93f)
                curveToRelative(1.16f, -0.57f, 1.81f, -1.5f, 1.81f, -2.5f)
                verticalLineToRelative(-4.22f)
                curveToRelative(-0.01f, -1.08f, -0.6f, -2.15f, -1.97f, -1.95f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFF5F5F5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(58.59f, 27.81f)
                curveToRelative(1.6f, -1.54f, -4.16f, -2.87f, -6.88f, -3.8f)
                curveToRelative(-3.42f, -1.17f, -7.0f, -3.23f, -8.09f, -7.26f)
                curveToRelative(-0.82f, -3.04f, -2.25f, -2.09f, -2.51f, -1.73f)
                curveToRelative(-0.37f, 0.51f, -1.8f, 3.49f, 0.95f, 6.8f)
                curveToRelative(1.89f, 2.28f, 4.95f, 4.01f, 7.56f, 4.89f)
                curveToRelative(3.57f, 1.21f, 7.91f, 2.12f, 8.97f, 1.1f)
                close()
                moveTo(56.0f, 13.18f)
                curveToRelative(0.33f, -1.12f, 3.23f, -1.81f, 4.99f, -1.83f)
                curveToRelative(3.01f, -0.04f, 6.11f, 2.27f, 6.57f, 3.06f)
                curveToRelative(1.29f, 2.2f, -1.65f, 2.98f, -6.79f, 2.53f)
                curveToRelative(-3.46f, -0.3f, -5.1f, -2.63f, -4.77f, -3.76f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E9E9E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.11f, 15.01f)
                curveToRelative(-0.35f, -0.62f, -0.71f, -1.27f, -1.05f, -1.9f)
                curveToRelative(-0.36f, -0.65f, -0.35f, 0.05f, -0.43f, 0.32f)
                curveToRelative(-0.41f, 1.42f, -1.22f, 2.05f, -1.79f, 2.36f)
                curveToRelative(-1.72f, 0.92f, -3.78f, 1.36f, -5.83f, 1.33f)
                curveToRelative(-2.06f, 0.02f, -4.11f, -0.41f, -5.83f, -1.33f)
                curveToRelative(-0.58f, -0.31f, -1.38f, -0.94f, -1.79f, -2.36f)
                curveToRelative(-0.08f, -0.28f, -0.07f, -0.97f, -0.43f, -0.32f)
                curveToRelative(-0.35f, 0.63f, -0.7f, 1.28f, -1.05f, 1.9f)
                curveToRelative(-0.9f, 1.58f, -0.12f, 3.33f, 1.03f, 4.45f)
                curveToRelative(2.01f, 1.95f, 5.13f, 2.85f, 8.08f, 2.85f)
                reflectiveCurveToRelative(6.07f, -0.9f, 8.08f, -2.85f)
                curveToRelative(1.14f, -1.13f, 1.91f, -2.88f, 1.01f, -4.45f)
                close()
            }
        }
            .build()
        return _battery!!
    }

private var _battery: ImageVector? = null
