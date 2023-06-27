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

val Emoji.Slots: ImageVector
    get() {
        if (_slots != null) {
            return _slots!!
        }
        _slots = Builder(
            name = "Slots", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF9A9A9A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(105.77f, 90.9f)
                lineToRelative(8.13f, -0.05f)
                lineToRelative(0.16f, -17.62f)
                lineToRelative(5.14f, 0.08f)
                reflectiveCurveToRelative(-0.16f, 18.85f, -0.16f, 20.15f)
                curveToRelative(0.0f, 1.31f, -0.82f, 2.61f, -1.96f, 2.77f)
                curveToRelative(-1.14f, 0.16f, -10.12f, 0.16f, -10.12f, 0.16f)
                lineToRelative(-1.19f, -5.49f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDB0D2B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(109.29f, 67.63f)
                curveToRelative(-0.12f, 3.96f, 2.25f, 7.82f, 7.56f, 7.77f)
                curveToRelative(5.31f, -0.05f, 7.15f, -3.26f, 7.26f, -7.21f)
                curveToRelative(0.1f, -3.56f, -2.15f, -7.97f, -7.51f, -7.82f)
                curveToRelative(-4.85f, 0.14f, -7.21f, 3.96f, -7.31f, 7.26f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(111.89f, 68.69f)
                curveToRelative(1.85f, 0.8f, 1.93f, -1.33f, 2.94f, -2.74f)
                curveToRelative(1.02f, -1.42f, 2.72f, -1.97f, 1.77f, -3.12f)
                curveToRelative(-0.59f, -0.71f, -2.53f, -1.38f, -4.43f, 1.08f)
                curveToRelative(-1.3f, 1.67f, -1.84f, 4.1f, -0.28f, 4.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFCACACA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(6.38f, 100.31f)
                reflectiveCurveToRelative(0.0f, -41.59f, 0.41f, -48.68f)
                curveToRelative(0.41f, -7.09f, 6.68f, -26.32f, 30.27f, -34.09f)
                reflectiveCurveToRelative(45.77f, 0.89f, 54.53f, 8.46f)
                curveToRelative(16.09f, 13.91f, 15.82f, 26.86f, 15.82f, 26.86f)
                lineToRelative(0.14f, 46.36f)
                reflectiveCurveToRelative(3.15f, 4.07f, 3.15f, 5.16f)
                curveToRelative(0.0f, 1.09f, -0.02f, 3.56f, -0.02f, 3.56f)
                lineToRelative(-107.58f, 0.28f)
                reflectiveCurveToRelative(-0.1f, -2.9f, 0.44f, -4.13f)
                reflectiveCurveToRelative(2.84f, -3.78f, 2.84f, -3.78f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDB0A27)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(55.88f, 4.67f)
                curveToRelative(-4.87f, 0.0f, -7.98f, 3.23f, -8.95f, 5.66f)
                curveToRelative(-0.84f, 2.12f, -0.91f, 4.93f, -0.91f, 4.93f)
                reflectiveCurveToRelative(2.57f, 0.54f, 10.04f, 0.54f)
                reflectiveCurveToRelative(9.44f, -0.71f, 9.44f, -0.71f)
                reflectiveCurveToRelative(0.35f, -3.02f, -0.79f, -5.1f)
                curveToRelative(-1.3f, -2.38f, -3.73f, -5.32f, -8.83f, -5.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF4E23)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(47.85f, 12.81f)
                curveToRelative(1.28f, 1.54f, 3.93f, 0.67f, 6.31f, -1.23f)
                curveToRelative(1.97f, -1.58f, 4.17f, -4.56f, 3.01f, -5.56f)
                curveToRelative(-1.67f, -1.43f, -4.52f, -0.47f, -7.03f, 1.48f)
                curveToRelative(-1.56f, 1.22f, -3.43f, 3.95f, -2.29f, 5.31f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFDFD)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(49.19f, 11.62f)
                curveToRelative(0.79f, 0.63f, 1.89f, -0.51f, 3.38f, -1.76f)
                curveToRelative(1.26f, -1.05f, 3.14f, -2.14f, 2.41f, -3.03f)
                curveToRelative(-0.74f, -0.9f, -2.71f, -0.08f, -4.13f, 1.06f)
                curveToRelative(-1.26f, 0.99f, -2.89f, 2.75f, -1.66f, 3.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9A9A9A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(3.35f, 105.01f)
                curveToRelative(-0.49f, 0.86f, -0.74f, 15.1f, 0.44f, 15.99f)
                curveToRelative(1.18f, 0.89f, 23.69f, 0.15f, 53.44f, 0.15f)
                reflectiveCurveToRelative(51.51f, -0.15f, 52.55f, -1.48f)
                curveToRelative(1.49f, -1.92f, 1.42f, -14.76f, 0.97f, -15.5f)
                curveToRelative(-0.86f, -1.44f, -27.77f, -0.19f, -53.67f, -0.04f)
                curveToRelative(-26.65f, 0.14f, -52.96f, -0.48f, -53.73f, 0.88f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF858585)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(8.97f, 108.27f)
                curveToRelative(-1.04f, 0.44f, -1.18f, 8.59f, 0.0f, 8.88f)
                curveToRelative(1.44f, 0.36f, 33.92f, 0.09f, 47.54f, 0.09f)
                reflectiveCurveToRelative(47.35f, 0.5f, 48.68f, -0.68f)
                curveToRelative(1.33f, -1.18f, 1.21f, -7.63f, 0.32f, -8.67f)
                curveToRelative(-0.89f, -1.04f, -35.7f, -0.21f, -48.73f, -0.21f)
                reflectiveCurveToRelative(-45.68f, -0.33f, -47.81f, 0.59f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF6F7178)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.64f, 51.42f)
                curveToRelative(-0.3f, 1.04f, -0.44f, 45.59f, 0.0f, 45.89f)
                curveToRelative(0.44f, 0.3f, 90.0f, 0.59f, 90.89f, -0.3f)
                curveToRelative(0.89f, -0.89f, 0.3f, -44.85f, 0.15f, -45.74f)
                curveToRelative(-0.15f, -0.89f, -46.04f, -1.92f, -46.04f, -1.92f)
                lineToRelative(-45.0f, 2.07f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF3A3839)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(14.73f, 54.74f)
                curveToRelative(-0.17f, 0.46f, 0.09f, 39.48f, 0.51f, 39.91f)
                curveToRelative(0.43f, 0.43f, 84.02f, -0.17f, 84.45f, -0.51f)
                curveToRelative(0.43f, -0.34f, 0.34f, -39.35f, 0.0f, -39.65f)
                curveToRelative(-0.34f, -0.31f, -84.79f, -0.23f, -84.96f, 0.25f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFDB8B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(57.34f, 18.42f)
                curveToRelative(-20.06f, -0.18f, -32.33f, 8.99f, -37.92f, 16.41f)
                reflectiveCurveToRelative(-8.65f, 14.27f, -7.79f, 16.59f)
                curveToRelative(0.23f, 0.63f, 14.72f, 0.06f, 44.01f, 0.18f)
                reflectiveCurveToRelative(46.55f, 0.56f, 47.03f, -0.33f)
                curveToRelative(0.49f, -0.89f, -1.51f, -10.96f, -12.4f, -20.33f)
                curveToRelative(-8.74f, -7.54f, -19.07f, -12.4f, -32.93f, -12.52f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFAF29)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(14.88f, 48.54f)
                reflectiveCurveToRelative(9.93f, -27.86f, 41.53f, -27.59f)
                curveToRelative(34.61f, 0.3f, 41.63f, 27.39f, 41.63f, 27.39f)
                lineToRelative(-83.16f, 0.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD61C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(29.89f, 34.82f)
                curveToRelative(-5.97f, 5.42f, -8.58f, 11.02f, -8.58f, 11.02f)
                reflectiveCurveToRelative(22.26f, 0.46f, 22.32f, 0.12f)
                curveToRelative(0.07f, -0.43f, -13.74f, -11.14f, -13.74f, -11.14f)
                close()
                moveTo(43.69f, 26.61f)
                curveToRelative(-4.02f, 1.3f, -7.45f, 3.15f, -10.32f, 5.2f)
                curveToRelative(3.78f, 3.44f, 14.23f, 11.42f, 15.65f, 12.43f)
                curveToRelative(1.69f, 1.2f, 2.25f, 1.85f, 2.63f, 1.62f)
                curveToRelative(0.31f, -0.18f, -0.53f, -1.76f, -0.8f, -2.41f)
                curveToRelative(-0.47f, -1.15f, -5.67f, -12.74f, -7.16f, -16.84f)
                close()
                moveTo(62.48f, 25.24f)
                curveToRelative(-2.06f, -0.32f, -4.63f, -0.62f, -6.91f, -0.6f)
                curveToRelative(-2.78f, 0.02f, -4.67f, 0.3f, -7.07f, 0.78f)
                curveToRelative(0.0f, 0.0f, 7.88f, 19.49f, 8.05f, 19.96f)
                curveToRelative(0.18f, 0.47f, 0.58f, 0.95f, 0.8f, 0.0f)
                curveToRelative(0.21f, -0.94f, 5.13f, -20.14f, 5.13f, -20.14f)
                close()
                moveTo(70.67f, 46.13f)
                curveToRelative(0.14f, 0.31f, 21.56f, 0.13f, 21.56f, 0.13f)
                reflectiveCurveToRelative(-3.06f, -5.83f, -9.48f, -11.41f)
                curveToRelative(0.0f, -0.01f, -12.34f, 10.69f, -12.08f, 11.28f)
                close()
                moveTo(78.96f, 31.72f)
                curveToRelative(-3.26f, -2.28f, -7.13f, -4.32f, -11.67f, -5.64f)
                curveToRelative(-1.27f, 4.8f, -4.05f, 16.43f, -4.33f, 17.9f)
                curveToRelative(-0.11f, 0.55f, -0.5f, 1.72f, -0.23f, 1.94f)
                curveToRelative(0.28f, 0.23f, 1.29f, -0.42f, 2.16f, -1.26f)
                curveToRelative(0.99f, -0.96f, 14.07f, -12.94f, 14.07f, -12.94f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(41.57f, 74.52f)
                curveToRelative(-0.18f, -9.43f, 2.24f, -14.88f, 3.48f, -17.03f)
                lineToRelative(-21.64f, 0.03f)
                reflectiveCurveToRelative(-5.34f, 6.65f, -5.43f, 16.3f)
                curveToRelative(-0.09f, 9.65f, 5.06f, 17.99f, 5.06f, 17.99f)
                lineToRelative(21.91f, -0.09f)
                curveToRelative(-1.05f, -2.2f, -3.2f, -7.84f, -3.38f, -17.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(70.17f, 74.52f)
                curveToRelative(0.3f, -8.71f, -1.83f, -14.6f, -2.95f, -17.06f)
                lineToRelative(-19.89f, 0.03f)
                curveToRelative(-1.22f, 2.91f, -3.4f, 9.21f, -3.22f, 16.87f)
                curveToRelative(0.19f, 8.35f, 2.38f, 14.95f, 3.29f, 17.36f)
                lineToRelative(20.51f, -0.08f)
                curveToRelative(0.69f, -2.68f, 1.96f, -8.65f, 2.26f, -17.12f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(91.71f, 57.43f)
                lineToRelative(-21.75f, 0.03f)
                curveToRelative(1.29f, 3.21f, 3.4f, 9.78f, 3.0f, 17.64f)
                curveToRelative(-0.41f, 7.91f, -1.83f, 14.01f, -2.51f, 16.52f)
                lineToRelative(20.61f, -0.09f)
                reflectiveCurveToRelative(5.25f, -6.37f, 5.72f, -16.3f)
                curveToRelative(0.46f, -9.93f, -5.07f, -17.8f, -5.07f, -17.8f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFAB037)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(27.65f, 91.81f)
                lineToRelative(9.89f, -0.05f)
                reflectiveCurveToRelative(-0.76f, -2.78f, -4.52f, -2.68f)
                curveToRelative(-3.84f, 0.12f, -5.37f, 2.73f, -5.37f, 2.73f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFD429)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(54.08f, 91.7f)
                lineToRelative(7.58f, -0.04f)
                reflectiveCurveToRelative(-0.07f, -2.29f, -3.78f, -2.24f)
                curveToRelative(-3.2f, 0.04f, -3.67f, 2.02f, -3.8f, 2.28f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA75FB7)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(78.53f, 91.6f)
                lineToRelative(8.77f, -0.05f)
                reflectiveCurveToRelative(0.45f, -2.5f, -1.81f, -2.7f)
                curveToRelative(-1.92f, -0.17f, -2.39f, 1.62f, -2.39f, 1.62f)
                reflectiveCurveToRelative(-0.42f, -1.29f, -2.09f, -1.32f)
                curveToRelative(-2.01f, -0.04f, -2.57f, 2.15f, -2.48f, 2.45f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFEBF21)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(76.73f, 57.45f)
                lineToRelative(11.51f, -0.02f)
                reflectiveCurveToRelative(0.07f, 3.58f, -4.42f, 3.67f)
                curveToRelative(-6.4f, 0.13f, -7.09f, -3.65f, -7.09f, -3.65f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF5B6467)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(53.31f, 57.47f)
                reflectiveCurveToRelative(-0.73f, 2.47f, -0.22f, 2.95f)
                curveToRelative(0.6f, 0.55f, 8.66f, 0.66f, 9.22f, -0.02f)
                curveToRelative(0.56f, -0.69f, -0.14f, -2.94f, -0.14f, -2.94f)
                lineToRelative(-8.86f, 0.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF4E9226)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(27.29f, 57.5f)
                lineToRelative(11.5f, -0.02f)
                reflectiveCurveToRelative(0.11f, 2.93f, -5.06f, 2.99f)
                curveToRelative(-5.86f, 0.08f, -6.44f, -2.97f, -6.44f, -2.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF2B26)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(51.64f, 66.61f)
                curveToRelative(-0.43f, 0.03f, -1.07f, -1.79f, -1.84f, -1.62f)
                curveToRelative(-0.77f, 0.17f, -1.84f, 0.38f, -1.62f, 5.34f)
                reflectiveCurveToRelative(0.6f, 5.38f, 1.58f, 5.34f)
                curveToRelative(0.98f, -0.04f, 2.48f, -2.9f, 2.48f, -2.9f)
                reflectiveCurveToRelative(1.05f, -0.45f, 1.78f, -0.5f)
                curveToRelative(0.73f, -0.04f, 1.25f, 0.62f, 2.15f, 0.92f)
                curveToRelative(0.9f, 0.3f, 2.18f, 0.34f, 2.18f, 0.34f)
                reflectiveCurveToRelative(-3.03f, 2.56f, -4.06f, 5.85f)
                reflectiveCurveToRelative(-1.07f, 6.53f, -0.81f, 6.74f)
                curveToRelative(0.26f, 0.21f, 8.07f, 0.3f, 8.37f, 0.09f)
                curveToRelative(0.3f, -0.21f, -0.13f, -9.05f, 1.45f, -11.82f)
                curveToRelative(1.58f, -2.77f, 3.71f, -6.1f, 3.71f, -6.1f)
                reflectiveCurveToRelative(-0.81f, -2.18f, -1.49f, -2.43f)
                curveToRelative(-0.68f, -0.26f, -2.13f, 0.81f, -3.93f, 0.73f)
                curveToRelative(-1.79f, -0.09f, -3.89f, -1.87f, -4.95f, -2.18f)
                curveToRelative(-1.8f, -0.53f, -3.72f, 2.11f, -5.0f, 2.2f)
                close()
                moveTo(24.66f, 66.4f)
                curveToRelative(-0.43f, 0.03f, -1.03f, -1.42f, -1.82f, -1.47f)
                curveToRelative(-0.91f, -0.06f, -1.58f, 0.08f, -1.64f, 5.03f)
                curveToRelative(-0.05f, 4.86f, 0.62f, 5.86f, 1.6f, 5.82f)
                reflectiveCurveToRelative(2.47f, -3.08f, 2.47f, -3.08f)
                reflectiveCurveToRelative(0.8f, -0.5f, 1.55f, -0.25f)
                curveToRelative(0.69f, 0.23f, 1.23f, 0.68f, 2.28f, 0.83f)
                curveToRelative(0.94f, 0.13f, 2.0f, 0.29f, 2.0f, 0.29f)
                reflectiveCurveToRelative(-2.75f, 2.47f, -3.78f, 5.75f)
                curveToRelative(-1.02f, 3.29f, -1.07f, 6.53f, -0.81f, 6.74f)
                curveToRelative(0.26f, 0.21f, 8.07f, 0.3f, 8.37f, 0.09f)
                curveToRelative(0.3f, -0.21f, 0.06f, -8.75f, 1.64f, -11.53f)
                reflectiveCurveToRelative(3.53f, -6.4f, 3.53f, -6.4f)
                reflectiveCurveToRelative(-0.81f, -2.18f, -1.49f, -2.43f)
                curveToRelative(-0.68f, -0.26f, -2.46f, 0.59f, -4.25f, 0.5f)
                curveToRelative(-1.79f, -0.09f, -3.74f, -1.73f, -4.84f, -1.89f)
                curveToRelative(-2.04f, -0.3f, -3.53f, 1.92f, -4.81f, 2.0f)
                close()
                moveTo(78.86f, 65.9f)
                curveToRelative(-0.43f, 0.03f, -1.19f, -1.5f, -1.95f, -1.33f)
                curveToRelative(-0.77f, 0.17f, -1.61f, 0.38f, -1.62f, 5.34f)
                curveToRelative(-0.01f, 4.15f, 0.6f, 5.29f, 1.58f, 5.34f)
                curveToRelative(1.56f, 0.07f, 2.39f, -2.68f, 2.39f, -2.68f)
                reflectiveCurveToRelative(1.09f, -0.39f, 1.81f, -0.43f)
                curveToRelative(0.73f, -0.04f, 1.3f, 0.34f, 2.2f, 0.64f)
                curveToRelative(0.9f, 0.3f, 2.01f, 0.42f, 2.01f, 0.42f)
                reflectiveCurveToRelative(-2.87f, 2.48f, -3.89f, 5.77f)
                reflectiveCurveToRelative(-1.07f, 6.53f, -0.81f, 6.74f)
                curveToRelative(0.26f, 0.21f, 8.07f, 0.3f, 8.37f, 0.09f)
                curveToRelative(0.3f, -0.21f, -0.08f, -8.63f, 1.5f, -11.4f)
                reflectiveCurveToRelative(3.7f, -5.49f, 3.7f, -6.09f)
                curveToRelative(0.0f, -0.6f, -1.0f, -2.84f, -1.69f, -3.1f)
                curveToRelative(-0.68f, -0.26f, -1.96f, 0.79f, -3.75f, 0.71f)
                curveToRelative(-1.79f, -0.09f, -3.94f, -1.76f, -5.0f, -2.07f)
                curveToRelative(-1.8f, -0.53f, -3.56f, 1.96f, -4.85f, 2.05f)
                close()
            }
        }
            .build()
        return _slots!!
    }

private var _slots: ImageVector? = null
