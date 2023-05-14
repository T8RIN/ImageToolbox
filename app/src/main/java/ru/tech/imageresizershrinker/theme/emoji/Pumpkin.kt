package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

val Emoji.Pumpkin: ImageVector
    get() {
        if (_pumpkin != null) {
            return _pumpkin!!
        }
        _pumpkin = Builder(
            name = "Pumpkin", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF57C00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(109.44f, 34.58f)
                curveToRelative(-10.76f, -8.62f, -25.91f, -10.36f, -45.25f, -10.36f)
                curveToRelative(-19.21f, 0.0f, -34.27f, 1.67f, -45.03f, 10.18f)
                curveTo(2.9f, 47.29f, -1.48f, 76.26f, 9.05f, 94.81f)
                curveToRelative(10.19f, 17.95f, 23.7f, 25.64f, 55.14f, 25.64f)
                curveToRelative(31.45f, 0.0f, 46.58f, -8.9f, 55.15f, -25.64f)
                curveToRelative(9.67f, -18.88f, 6.16f, -47.38f, -9.9f, -60.23f)
                close()
                moveTo(57.28f, 77.72f)
                lineToRelative(5.09f, -6.33f)
                curveToRelative(0.89f, -1.1f, 2.42f, -1.16f, 3.38f, -0.12f)
                lineToRelative(6.08f, 6.57f)
                curveToRelative(0.97f, 1.04f, 0.6f, 1.89f, -0.83f, 1.89f)
                lineTo(58.25f, 79.73f)
                curveToRelative(-1.43f, -0.01f, -1.86f, -0.92f, -0.97f, -2.01f)
                close()
                moveTo(28.83f, 69.87f)
                lineToRelative(11.08f, -12.48f)
                curveToRelative(0.64f, -0.72f, 1.49f, -0.6f, 1.89f, 0.27f)
                lineToRelative(6.72f, 14.36f)
                curveToRelative(0.41f, 0.86f, -0.04f, 1.49f, -0.99f, 1.38f)
                lineTo(29.4f, 71.36f)
                curveToRelative(-0.94f, -0.11f, -1.2f, -0.78f, -0.57f, -1.49f)
                close()
                moveTo(96.22f, 85.77f)
                lineToRelative(-4.83f, 15.88f)
                curveToRelative(-0.34f, 1.1f, -1.37f, 1.55f, -2.29f, 1.0f)
                lineToRelative(-5.01f, -2.97f)
                curveToRelative(-0.91f, -0.55f, -2.4f, -0.51f, -3.29f, 0.09f)
                lineToRelative(-6.49f, 4.34f)
                curveToRelative(-0.9f, 0.6f, -2.32f, 0.53f, -3.16f, -0.16f)
                lineToRelative(-5.36f, -4.35f)
                curveToRelative(-0.84f, -0.68f, -2.2f, -0.66f, -3.02f, 0.06f)
                lineToRelative(-5.29f, 4.62f)
                curveToRelative(-0.83f, 0.71f, -2.24f, 0.84f, -3.15f, 0.28f)
                lineToRelative(-6.75f, -4.16f)
                curveToRelative(-0.91f, -0.56f, -2.43f, -0.61f, -3.38f, -0.13f)
                lineToRelative(-5.43f, 2.79f)
                curveToRelative(-0.94f, 0.48f, -2.03f, 0.0f, -2.4f, -1.08f)
                lineTo(30.7f, 85.74f)
                curveToRelative(-0.37f, -1.08f, 0.09f, -1.57f, 1.04f, -1.08f)
                lineToRelative(8.05f, 4.15f)
                curveToRelative(0.95f, 0.48f, 2.36f, 0.25f, 3.15f, -0.51f)
                lineToRelative(3.26f, -3.15f)
                curveToRelative(0.78f, -0.76f, 2.17f, -0.91f, 3.08f, -0.34f)
                lineToRelative(6.31f, 3.99f)
                curveToRelative(0.9f, 0.57f, 2.32f, 0.46f, 3.14f, -0.25f)
                lineToRelative(4.03f, -3.48f)
                curveToRelative(0.81f, -0.71f, 2.21f, -0.77f, 3.08f, -0.13f)
                lineToRelative(5.12f, 3.74f)
                curveToRelative(0.87f, 0.65f, 2.31f, 0.67f, 3.2f, 0.07f)
                lineToRelative(4.79f, -3.28f)
                curveToRelative(0.89f, -0.61f, 2.31f, -0.56f, 3.16f, 0.11f)
                lineToRelative(3.91f, 3.06f)
                curveToRelative(0.85f, 0.67f, 2.28f, 0.74f, 3.19f, 0.14f)
                lineToRelative(5.98f, -3.93f)
                curveToRelative(0.91f, -0.6f, 1.37f, -0.18f, 1.03f, 0.92f)
                close()
                moveTo(99.13f, 71.12f)
                lineToRelative(-18.07f, 2.47f)
                curveToRelative(-0.95f, 0.13f, -1.41f, -0.49f, -1.02f, -1.36f)
                lineToRelative(6.37f, -14.51f)
                curveToRelative(0.38f, -0.88f, 1.23f, -1.03f, 1.88f, -0.32f)
                lineToRelative(11.38f, 12.22f)
                curveToRelative(0.64f, 0.69f, 0.4f, 1.36f, -0.54f, 1.5f)
                close()
            }
            group {
                path(
                    fill = SolidColor(Color(0xFFFF9800)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(98.33f, 38.11f)
                    curveToRelative(-6.66f, -7.19f, -15.42f, -9.75f, -26.28f, -10.46f)
                    curveToRelative(0.73f, -0.23f, 1.46f, -0.46f, 2.26f, -0.69f)
                    curveToRelative(2.19f, -0.64f, 4.44f, -1.12f, 6.71f, -1.33f)
                    curveToRelative(0.96f, -0.09f, 1.93f, -0.02f, 2.88f, -0.09f)
                    curveToRelative(0.46f, -0.03f, 0.88f, -0.19f, 1.35f, -0.2f)
                    curveToRelative(0.52f, -0.01f, 1.03f, 0.05f, 1.55f, 0.07f)
                    curveToRelative(0.43f, 0.01f, 0.78f, 0.2f, 1.18f, 0.25f)
                    reflectiveCurveToRelative(0.81f, -0.06f, 1.21f, -0.01f)
                    curveToRelative(0.64f, 0.08f, 1.64f, 0.44f, 1.82f, -0.47f)
                    curveToRelative(0.18f, -0.88f, -0.61f, -1.32f, -1.34f, -1.51f)
                    curveToRelative(-0.91f, -0.24f, -1.95f, -0.3f, -2.88f, -0.43f)
                    curveToRelative(-3.85f, -0.53f, -8.26f, -0.09f, -13.12f, 1.32f)
                    curveToRelative(-2.31f, 0.67f, -4.34f, 1.33f, -6.22f, 2.02f)
                    curveToRelative(-0.27f, 0.1f, -0.56f, 0.24f, -0.86f, 0.37f)
                    curveToRelative(-0.25f, 0.11f, -0.53f, 0.24f, -0.8f, 0.33f)
                    curveToRelative(-0.31f, 0.07f, -0.62f, 0.14f, -0.95f, 0.16f)
                    curveToRelative(-0.23f, 0.0f, -0.46f, -0.01f, -0.78f, -0.01f)
                    curveToRelative(-0.02f, 0.0f, -0.05f, 0.01f, -0.08f, 0.01f)
                    horizontalLineToRelative(-0.75f)
                    curveToRelative(-0.75f, -0.05f, -1.49f, -0.22f, -2.31f, -0.43f)
                    lineToRelative(-0.56f, -0.14f)
                    curveToRelative(-0.62f, -0.15f, -1.15f, -0.29f, -1.66f, -0.57f)
                    curveToRelative(-1.13f, -0.66f, -2.18f, -1.25f, -3.36f, -1.69f)
                    curveToRelative(-1.6f, -0.58f, -3.36f, -1.05f, -5.21f, -1.39f)
                    curveToRelative(-2.12f, -0.38f, -4.27f, -0.57f, -6.42f, -0.54f)
                    curveToRelative(-1.01f, 0.01f, -2.01f, 0.17f, -3.02f, 0.21f)
                    curveToRelative(-0.68f, 0.02f, -1.69f, 0.01f, -1.97f, 0.84f)
                    curveToRelative(-0.13f, 0.37f, 0.16f, 0.61f, 0.37f, 0.84f)
                    curveToRelative(1.12f, 1.28f, 3.31f, 0.63f, 4.77f, 0.61f)
                    curveToRelative(1.96f, -0.02f, 3.93f, 0.17f, 5.87f, 0.51f)
                    curveToRelative(1.73f, 0.31f, 3.35f, 0.73f, 4.82f, 1.28f)
                    curveToRelative(0.53f, 0.2f, 1.03f, 0.44f, 1.55f, 0.7f)
                    curveToRelative(-10.72f, 0.71f, -19.39f, 3.23f, -25.96f, 10.26f)
                    curveTo(16.06f, 52.97f, 11.8f, 79.4f, 20.21f, 99.41f)
                    curveToRelative(7.82f, 18.61f, 20.52f, 26.22f, 43.93f, 26.26f)
                    curveToRelative(22.86f, 0.0f, 37.23f, -8.61f, 43.94f, -26.31f)
                    curveToRelative(7.91f, -20.84f, 3.8f, -46.6f, -9.75f, -61.25f)
                    close()
                    moveTo(22.42f, 98.4f)
                    curveToRelative(-8.05f, -19.13f, -4.0f, -44.38f, 9.42f, -58.73f)
                    curveToRelative(5.69f, -6.09f, 13.22f, -8.53f, 22.53f, -9.37f)
                    curveToRelative(-2.03f, 1.63f, -3.87f, 4.14f, -5.4f, 8.01f)
                    curveToRelative(-5.88f, 14.89f, -7.65f, 41.04f, -4.15f, 60.81f)
                    curveToRelative(2.17f, 12.23f, 5.28f, 19.74f, 10.02f, 23.52f)
                    curveToRelative(-16.43f, -1.88f, -26.14f, -9.28f, -32.42f, -24.24f)
                    close()
                    moveTo(64.08f, 123.17f)
                    lineTo(64.0f, 123.17f)
                    curveToRelative(-8.87f, -0.03f, -13.76f, -7.13f, -16.84f, -24.48f)
                    curveToRelative(-3.43f, -19.37f, -1.71f, -44.91f, 4.0f, -59.41f)
                    curveToRelative(2.94f, -7.41f, 6.83f, -9.18f, 12.09f, -9.33f)
                    curveToRelative(0.1f, 0.0f, 0.19f, 0.03f, 0.29f, 0.03f)
                    horizontalLineToRelative(0.04f)
                    curveToRelative(0.37f, 0.0f, 0.77f, -0.01f, 1.17f, -0.03f)
                    horizontalLineToRelative(0.28f)
                    verticalLineToRelative(0.02f)
                    curveToRelative(0.04f, 0.0f, 0.07f, -0.02f, 0.11f, -0.02f)
                    curveToRelative(5.14f, 0.22f, 8.97f, 2.09f, 11.86f, 9.5f)
                    curveToRelative(5.51f, 14.12f, 7.17f, 39.04f, 3.93f, 59.25f)
                    curveToRelative(-2.61f, 16.45f, -8.14f, 24.45f, -16.85f, 24.47f)
                    close()
                    moveTo(105.85f, 98.44f)
                    curveToRelative(-5.49f, 14.48f, -16.06f, 22.29f, -32.88f, 24.22f)
                    curveToRelative(5.03f, -3.92f, 8.44f, -11.7f, 10.34f, -23.56f)
                    curveToRelative(3.29f, -20.61f, 1.58f, -46.1f, -4.08f, -60.6f)
                    curveToRelative(-1.56f, -3.99f, -3.44f, -6.56f, -5.51f, -8.21f)
                    curveToRelative(9.45f, 0.83f, 17.11f, 3.31f, 22.88f, 9.56f)
                    curveToRelative(12.94f, 13.97f, 16.82f, 38.62f, 9.25f, 58.59f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveTo(58.61f, 26.89f)
                    curveToRelative(-0.06f, -1.05f, 0.06f, -2.15f, 0.06f, -3.0f)
                    curveToRelative(0.0f, -2.69f, -1.41f, -4.92f, -3.27f, -6.75f)
                    curveToRelative(-1.56f, -1.54f, -4.22f, -2.48f, -5.58f, -4.06f)
                    curveToRelative(-0.39f, -0.46f, -0.83f, -1.89f, -0.75f, -2.45f)
                    curveToRelative(0.34f, -2.4f, 2.39f, -5.03f, 4.87f, -5.45f)
                    curveToRelative(1.31f, -0.21f, 2.44f, 0.23f, 3.45f, 0.99f)
                    curveToRelative(5.66f, 4.27f, 10.35f, 7.84f, 11.28f, 15.31f)
                    curveToRelative(0.18f, 1.47f, 0.54f, 3.0f, 0.53f, 4.52f)
                    curveToRelative(-0.01f, 2.43f, -0.74f, 3.29f, -3.16f, 3.92f)
                    curveToRelative(-1.63f, 0.43f, -4.69f, 0.63f, -6.2f, -0.3f)
                    curveToRelative(-0.87f, -0.55f, -1.16f, -1.59f, -1.23f, -2.73f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0xFF422B0D)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero
                ) {
                    moveToRelative(57.28f, 77.72f)
                    lineToRelative(5.09f, -6.33f)
                    curveToRelative(0.89f, -1.1f, 2.42f, -1.16f, 3.38f, -0.12f)
                    lineToRelative(6.08f, 6.57f)
                    curveToRelative(0.97f, 1.04f, 0.6f, 1.89f, -0.83f, 1.89f)
                    lineTo(58.25f, 79.73f)
                    curveToRelative(-1.43f, -0.01f, -1.86f, -0.92f, -0.97f, -2.01f)
                    close()
                    moveTo(28.83f, 69.87f)
                    lineToRelative(11.08f, -12.48f)
                    curveToRelative(0.64f, -0.72f, 1.49f, -0.6f, 1.89f, 0.27f)
                    lineToRelative(6.72f, 14.36f)
                    curveToRelative(0.41f, 0.86f, -0.04f, 1.49f, -0.99f, 1.38f)
                    lineTo(29.4f, 71.36f)
                    curveToRelative(-0.94f, -0.11f, -1.2f, -0.78f, -0.57f, -1.49f)
                    close()
                    moveTo(96.22f, 85.77f)
                    lineToRelative(-4.83f, 15.88f)
                    curveToRelative(-0.34f, 1.1f, -1.37f, 1.55f, -2.29f, 1.0f)
                    lineToRelative(-5.01f, -2.97f)
                    curveToRelative(-0.91f, -0.55f, -2.4f, -0.51f, -3.29f, 0.09f)
                    lineToRelative(-6.49f, 4.34f)
                    curveToRelative(-0.9f, 0.6f, -2.32f, 0.53f, -3.16f, -0.16f)
                    lineToRelative(-5.36f, -4.35f)
                    curveToRelative(-0.84f, -0.68f, -2.2f, -0.66f, -3.02f, 0.06f)
                    lineToRelative(-5.29f, 4.62f)
                    curveToRelative(-0.83f, 0.71f, -2.24f, 0.84f, -3.15f, 0.28f)
                    lineToRelative(-6.75f, -4.16f)
                    curveToRelative(-0.91f, -0.56f, -2.43f, -0.61f, -3.38f, -0.13f)
                    lineToRelative(-5.43f, 2.79f)
                    curveToRelative(-0.94f, 0.48f, -2.03f, 0.0f, -2.4f, -1.08f)
                    lineTo(30.7f, 85.74f)
                    curveToRelative(-0.37f, -1.08f, 0.09f, -1.57f, 1.04f, -1.08f)
                    lineToRelative(8.05f, 4.15f)
                    curveToRelative(0.95f, 0.48f, 2.36f, 0.25f, 3.15f, -0.51f)
                    lineToRelative(3.26f, -3.15f)
                    curveToRelative(0.78f, -0.76f, 2.17f, -0.91f, 3.08f, -0.34f)
                    lineToRelative(6.31f, 3.99f)
                    curveToRelative(0.9f, 0.57f, 2.32f, 0.46f, 3.14f, -0.25f)
                    lineToRelative(4.03f, -3.48f)
                    curveToRelative(0.81f, -0.71f, 2.21f, -0.77f, 3.08f, -0.13f)
                    lineToRelative(5.12f, 3.74f)
                    curveToRelative(0.87f, 0.65f, 2.31f, 0.67f, 3.2f, 0.07f)
                    lineToRelative(4.79f, -3.28f)
                    curveToRelative(0.89f, -0.61f, 2.31f, -0.56f, 3.16f, 0.11f)
                    lineToRelative(3.91f, 3.06f)
                    curveToRelative(0.85f, 0.67f, 2.28f, 0.74f, 3.19f, 0.14f)
                    lineToRelative(5.98f, -3.93f)
                    curveToRelative(0.91f, -0.6f, 1.37f, -0.18f, 1.03f, 0.92f)
                    close()
                    moveTo(99.13f, 71.12f)
                    lineToRelative(-18.07f, 2.47f)
                    curveToRelative(-0.95f, 0.13f, -1.41f, -0.49f, -1.02f, -1.36f)
                    lineToRelative(6.37f, -14.51f)
                    curveToRelative(0.38f, -0.88f, 1.23f, -1.03f, 1.88f, -0.32f)
                    lineToRelative(11.38f, 12.22f)
                    curveToRelative(0.64f, 0.69f, 0.4f, 1.36f, -0.54f, 1.5f)
                    close()
                }
            }
        }
            .build()
        return _pumpkin!!
    }

private var _pumpkin: ImageVector? = null
