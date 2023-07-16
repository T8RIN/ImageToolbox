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

val Emoji.Vest: ImageVector
    get() {
        if (_vest != null) {
            return _vest!!
        }
        _vest = Builder(
            name = "Vest", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE64A19)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(43.86f, 11.94f)
                reflectiveCurveTo(57.2f, 5.25f, 64.0f, 5.25f)
                reflectiveCurveToRelative(20.14f, 6.69f, 20.14f, 6.69f)
                verticalLineToRelative(103.52f)
                reflectiveCurveToRelative(-7.87f, -3.22f, -20.14f, -3.22f)
                reflectiveCurveToRelative(-20.14f, 3.22f, -20.14f, 3.22f)
                verticalLineTo(11.94f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(57.92f, 56.45f)
                curveToRelative(0.0f, -7.67f, -3.33f, -12.66f, -4.72f, -15.91f)
                curveToRelative(-2.26f, -5.31f, -3.22f, -10.81f, -3.41f, -16.38f)
                curveToRelative(-0.18f, -5.39f, 2.15f, -13.38f, 5.23f, -17.86f)
                curveToRelative(-8.39f, 3.55f, -29.34f, 14.3f, -29.55f, 14.56f)
                curveToRelative(0.0f, 0.0f, 0.61f, 19.0f, -1.68f, 27.84f)
                curveTo(20.12f, 62.88f, 12.2f, 68.86f, 12.2f, 68.86f)
                verticalLineToRelative(48.4f)
                curveToRelative(6.71f, 2.22f, 24.96f, 4.35f, 36.26f, 5.52f)
                curveToRelative(2.04f, 0.21f, 6.47f, -0.54f, 8.05f, -1.84f)
                curveToRelative(1.67f, -1.37f, 2.71f, -3.77f, 2.74f, -5.93f)
                curveToRelative(0.23f, -19.42f, -1.33f, -50.88f, -1.33f, -58.56f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.23f, 105.94f)
                curveToRelative(13.87f, 4.17f, 41.09f, 5.89f, 46.95f, 4.86f)
                lineToRelative(-0.19f, -8.12f)
                curveToRelative(-5.86f, -0.02f, -32.74f, -0.43f, -46.77f, -5.28f)
                lineToRelative(0.01f, 8.54f)
                close()
                moveTo(58.99f, 84.36f)
                reflectiveCurveToRelative(-7.17f, 0.35f, -17.81f, -0.71f)
                lineToRelative(1.31f, -71.46f)
                curveToRelative(-3.21f, 1.48f, -8.33f, 4.16f, -8.33f, 4.16f)
                lineToRelative(-0.99f, 66.59f)
                curveToRelative(-7.47f, -0.8f, -15.36f, -2.05f, -20.95f, -3.98f)
                verticalLineToRelative(8.54f)
                curveToRelative(13.87f, 4.17f, 41.11f, 6.51f, 46.96f, 5.48f)
                lineToRelative(-0.19f, -8.62f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF616161)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.63f, 68.91f)
                curveToRelative(-0.08f, -1.19f, -0.05f, -1.66f, 0.37f, -2.13f)
                curveToRelative(0.36f, -0.41f, 0.77f, -0.9f, 1.11f, -1.33f)
                curveToRelative(0.98f, -1.22f, 1.93f, -2.46f, 2.83f, -3.74f)
                curveToRelative(2.08f, -2.94f, 4.06f, -6.1f, 5.23f, -9.53f)
                curveToRelative(2.24f, -6.6f, 3.15f, -13.6f, 3.37f, -20.56f)
                curveToRelative(0.17f, -5.2f, -0.45f, -10.04f, -0.45f, -10.04f)
                reflectiveCurveToRelative(-0.21f, -0.44f, 1.03f, -1.09f)
                curveToRelative(1.24f, -0.65f, 1.76f, -0.4f, 1.76f, -0.4f)
                curveToRelative(0.32f, 1.86f, 0.58f, 5.18f, 0.66f, 6.9f)
                curveToRelative(0.78f, 15.38f, -2.62f, 23.58f, -3.5f, 26.17f)
                curveToRelative(-2.66f, 7.83f, -10.09f, 16.49f, -11.8f, 17.25f)
                curveToRelative(0.02f, -0.01f, -0.52f, -0.31f, -0.61f, -1.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF9100)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(70.08f, 56.45f)
                curveToRelative(0.0f, -7.67f, 3.33f, -12.66f, 4.72f, -15.91f)
                curveToRelative(2.26f, -5.31f, 3.22f, -10.81f, 3.41f, -16.38f)
                curveToRelative(0.18f, -5.39f, -2.15f, -13.38f, -5.23f, -17.86f)
                curveToRelative(8.39f, 3.55f, 29.34f, 14.3f, 29.55f, 14.56f)
                curveToRelative(0.0f, 0.0f, -0.61f, 19.0f, 1.68f, 27.84f)
                curveToRelative(3.67f, 14.18f, 11.59f, 20.16f, 11.59f, 20.16f)
                lineToRelative(-0.05f, 48.1f)
                curveToRelative(-12.84f, 3.32f, -25.32f, 5.5f, -36.06f, 5.79f)
                curveToRelative(-2.05f, 0.05f, -6.05f, 0.23f, -8.21f, -1.81f)
                curveToRelative(-2.52f, -2.38f, -2.63f, -3.32f, -2.65f, -5.48f)
                curveToRelative(-0.22f, -19.42f, 1.25f, -51.33f, 1.25f, -59.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(115.73f, 105.94f)
                curveToRelative(-13.87f, 4.17f, -41.05f, 5.89f, -46.9f, 4.86f)
                lineToRelative(0.19f, -8.12f)
                curveToRelative(5.86f, -0.02f, 32.7f, -0.43f, 46.73f, -5.28f)
                lineToRelative(-0.02f, 8.54f)
                close()
                moveTo(94.84f, 82.94f)
                lineToRelative(-0.99f, -66.59f)
                reflectiveCurveToRelative(-5.12f, -2.68f, -8.33f, -4.16f)
                lineToRelative(1.31f, 71.46f)
                curveToRelative(-8.4f, 1.1f, -17.81f, 1.22f, -17.81f, 1.22f)
                lineToRelative(-0.19f, 8.12f)
                curveToRelative(5.37f, 0.65f, 33.05f, -1.31f, 46.92f, -5.48f)
                verticalLineToRelative(-8.54f)
                curveToRelative(-5.6f, 1.92f, -13.44f, 3.17f, -20.91f, 3.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF616161)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(116.37f, 68.91f)
                curveToRelative(0.08f, -1.19f, 0.05f, -1.66f, -0.37f, -2.13f)
                curveToRelative(-0.36f, -0.41f, -0.77f, -0.9f, -1.11f, -1.33f)
                curveToRelative(-0.98f, -1.22f, -1.93f, -2.46f, -2.83f, -3.74f)
                curveToRelative(-2.08f, -2.94f, -4.06f, -6.1f, -5.23f, -9.53f)
                curveToRelative(-2.24f, -6.6f, -3.15f, -13.6f, -3.37f, -20.56f)
                curveToRelative(-0.17f, -5.2f, 0.45f, -10.04f, 0.45f, -10.04f)
                reflectiveCurveToRelative(0.21f, -0.44f, -1.03f, -1.09f)
                curveToRelative(-1.24f, -0.65f, -1.76f, -0.4f, -1.76f, -0.4f)
                curveToRelative(-0.32f, 1.86f, -0.58f, 5.18f, -0.66f, 6.9f)
                curveToRelative(-0.78f, 15.38f, 2.62f, 23.58f, 3.5f, 26.17f)
                curveToRelative(2.66f, 7.83f, 10.09f, 16.49f, 11.8f, 17.25f)
                curveToRelative(-0.02f, -0.01f, 0.52f, -0.31f, 0.61f, -1.5f)
                close()
                moveTo(115.75f, 114.94f)
                curveToRelative(-0.36f, 0.16f, -1.43f, 0.4f, -1.94f, 0.48f)
                curveToRelative(-1.47f, 0.22f, -18.33f, 4.14f, -34.49f, 5.31f)
                curveToRelative(-1.62f, 0.07f, -4.78f, 0.15f, -7.32f, -1.75f)
                curveToRelative(-1.07f, -0.8f, -1.49f, -2.61f, -1.48f, -3.56f)
                curveToRelative(0.07f, -6.28f, 1.27f, -55.9f, 1.29f, -56.41f)
                curveToRelative(0.0f, -0.01f, 0.45f, -6.97f, 2.13f, -12.1f)
                curveToRelative(2.72f, -8.33f, 5.98f, -19.77f, 6.04f, -26.96f)
                curveToRelative(0.04f, -5.23f, -1.29f, -9.24f, -3.94f, -11.91f)
                curveTo(73.35f, 5.33f, 68.14f, 4.0f, 64.0f, 4.0f)
                reflectiveCurveToRelative(-9.35f, 1.33f, -12.03f, 4.03f)
                curveToRelative(-2.66f, 2.68f, -3.98f, 6.69f, -3.94f, 11.91f)
                curveToRelative(0.06f, 7.19f, 3.31f, 18.63f, 6.04f, 26.96f)
                curveToRelative(1.68f, 5.13f, 2.12f, 12.09f, 2.13f, 12.1f)
                curveToRelative(0.01f, 0.51f, 1.22f, 50.13f, 1.29f, 56.41f)
                curveToRelative(0.01f, 0.94f, -0.41f, 2.75f, -1.48f, 3.56f)
                curveToRelative(-2.53f, 1.91f, -5.7f, 1.83f, -7.32f, 1.75f)
                curveToRelative(-16.17f, -1.17f, -33.0f, -4.58f, -34.49f, -5.0f)
                curveToRelative(-0.5f, -0.14f, -1.58f, -0.31f, -1.94f, -0.48f)
                curveToRelative(0.0f, 0.0f, -0.28f, -0.03f, -0.22f, 1.29f)
                curveToRelative(0.03f, 0.79f, 0.1f, 1.25f, 0.31f, 1.51f)
                curveToRelative(0.3f, 0.38f, 0.82f, 0.44f, 1.6f, 0.72f)
                curveToRelative(1.73f, 0.61f, 8.43f, 1.57f, 17.09f, 3.11f)
                curveToRelative(5.46f, 0.97f, 13.68f, 2.04f, 16.6f, 2.09f)
                curveToRelative(3.86f, 0.07f, 7.71f, 0.33f, 10.95f, -2.76f)
                curveToRelative(1.42f, -1.35f, 1.96f, -3.02f, 1.94f, -4.78f)
                curveToRelative(-0.07f, -6.3f, -1.27f, -56.97f, -1.29f, -57.54f)
                curveToRelative(-0.02f, -0.3f, -0.47f, -7.41f, -2.27f, -12.92f)
                curveToRelative(-3.81f, -11.65f, -5.85f, -20.66f, -5.89f, -26.04f)
                curveToRelative(-0.03f, -4.39f, 0.99f, -7.67f, 3.06f, -9.75f)
                curveToRelative(1.99f, -2.01f, 5.4f, -3.03f, 9.87f, -3.11f)
                curveToRelative(4.47f, 0.08f, 7.87f, 1.1f, 9.87f, 3.11f)
                curveToRelative(2.07f, 2.08f, 3.09f, 5.36f, 3.06f, 9.75f)
                curveToRelative(-0.04f, 5.38f, -2.08f, 14.39f, -5.89f, 26.04f)
                curveToRelative(-1.8f, 5.51f, -2.25f, 12.62f, -2.27f, 12.92f)
                curveToRelative(-0.01f, 0.57f, -1.22f, 51.24f, -1.29f, 57.54f)
                curveToRelative(-0.02f, 1.77f, 0.52f, 3.44f, 1.94f, 4.78f)
                curveToRelative(3.25f, 3.09f, 7.09f, 2.83f, 10.95f, 2.76f)
                curveToRelative(2.92f, -0.05f, 11.14f, -1.12f, 16.6f, -2.09f)
                curveToRelative(8.66f, -1.53f, 15.35f, -2.83f, 17.09f, -3.39f)
                curveToRelative(0.9f, -0.29f, 1.3f, -0.34f, 1.6f, -0.72f)
                curveToRelative(0.2f, -0.26f, 0.27f, -0.72f, 0.31f, -1.51f)
                curveToRelative(0.05f, -1.31f, -0.23f, -1.31f, -0.23f, -1.31f)
                close()
            }
        }
            .build()
        return _vest!!
    }

private var _vest: ImageVector? = null
