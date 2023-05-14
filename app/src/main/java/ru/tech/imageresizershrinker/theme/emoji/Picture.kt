package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
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
import ru.tech.imageresizershrinker.theme.Emoji

val Emoji.Picture: ImageVector
    get() {
        if (_picture != null) {
            return _picture!!
        }
        _picture = Builder(
            name = "Picture", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF2A600)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(122.66f, 124.0f)
                horizontalLineTo(5.34f)
                curveToRelative(-0.74f, 0.0f, -1.34f, -0.6f, -1.34f, -1.34f)
                verticalLineTo(5.34f)
                curveTo(4.0f, 4.6f, 4.6f, 4.0f, 5.34f, 4.0f)
                horizontalLineToRelative(117.32f)
                curveToRelative(0.74f, 0.0f, 1.34f, 0.6f, 1.34f, 1.34f)
                verticalLineToRelative(117.32f)
                curveToRelative(0.0f, 0.74f, -0.6f, 1.34f, -1.34f, 1.34f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.26f, 21.26f)
                horizontalLineToRelative(85.47f)
                verticalLineToRelative(85.47f)
                horizontalLineTo(21.26f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD1762C)), stroke = null, fillAlpha = 0.5f, strokeAlpha
                = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(122.66f, 4.0f)
                horizontalLineTo(5.34f)
                curveToRelative(-0.37f, 0.0f, -0.7f, 0.15f, -0.95f, 0.39f)
                lineToRelative(16.87f, 16.87f)
                horizontalLineToRelative(85.47f)
                lineTo(123.6f, 4.39f)
                curveToRelative(-0.24f, -0.24f, -0.57f, -0.39f, -0.94f, -0.39f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDE7340)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(21.26f, 21.26f)
                lineTo(4.39f, 4.39f)
                curveToRelative(-0.24f, 0.25f, -0.39f, 0.58f, -0.39f, 0.95f)
                verticalLineToRelative(117.32f)
                curveToRelative(0.0f, 0.37f, 0.15f, 0.71f, 0.39f, 0.95f)
                lineToRelative(16.87f, -16.87f)
                verticalLineTo(21.26f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFCD40)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(114.5f, 13.5f)
                verticalLineToRelative(101.0f)
                horizontalLineToRelative(-101.0f)
                verticalLineToRelative(-101.0f)
                horizontalLineToRelative(101.0f)
                close()
                moveTo(120.5f, 7.5f)
                lineTo(7.5f, 7.5f)
                verticalLineToRelative(113.0f)
                horizontalLineToRelative(113.0f)
                lineTo(120.5f, 7.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA65F3E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.5f, 114.5f)
                verticalLineToRelative(-101.0f)
                lineToRelative(-6.0f, -6.0f)
                verticalLineToRelative(113.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD1762C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(114.43f, 13.5f)
                lineToRelative(6.0f, -6.0f)
                horizontalLineTo(7.5f)
                lineToRelative(6.0f, 6.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFD1762C)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(116.5f, 116.5f)
                horizontalLineToRelative(-105.0f)
                verticalLineToRelative(-105.0f)
                horizontalLineToRelative(105.0f)
                verticalLineToRelative(105.0f)
                close()
                moveTo(13.5f, 114.5f)
                horizontalLineToRelative(101.0f)
                verticalLineToRelative(-101.0f)
                horizontalLineToRelative(-101.0f)
                verticalLineToRelative(101.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF824A34)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(13.5f, 114.5f)
                verticalLineToRelative(-101.0f)
                lineToRelative(-2.0f, -2.0f)
                verticalLineToRelative(105.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA65F3E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(114.5f, 13.5f)
                lineToRelative(2.0f, -2.0f)
                horizontalLineToRelative(-105.0f)
                lineToRelative(2.0f, 2.0f)
                close()
            }
            path(
                fill = linearGradient(
                    0.117f to Color(0xFFAFE4FE), 0.608f to Color(0xFF84C9ED),
                    1.0f to Color(0xFF5FB2DE), start = Offset(58.235f, 84.56f), end =
                    Offset(66.597f, 54.739f)
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap =
                Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(27.5f, 27.5f)
                horizontalLineToRelative(73.0f)
                verticalLineToRelative(73.0f)
                horizontalLineToRelative(-73.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1B5E20)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(100.5f, 62.62f)
                curveToRelative(-2.2f, 0.63f, -4.13f, 2.13f, -5.3f, 4.1f)
                curveToRelative(-0.18f, 0.31f, -0.34f, 0.66f, -0.66f, 0.85f)
                curveToRelative(-1.03f, 0.38f, -2.21f, 0.46f, -3.01f, 1.36f)
                curveToRelative(-0.34f, -2.12f, -2.91f, -3.3f, -4.88f, -2.64f)
                curveToRelative(-1.67f, 0.42f, -2.5f, 2.18f, -2.65f, 3.78f)
                curveToRelative(-1.76f, -0.52f, -3.7f, 0.83f, -3.82f, 2.66f)
                curveToRelative(-0.02f, 0.17f, -0.02f, 0.36f, -0.11f, 0.5f)
                curveToRelative(-0.44f, 0.45f, -1.33f, 0.4f, -1.87f, 0.94f)
                curveToRelative(-1.07f, 0.84f, -1.57f, 2.29f, -2.71f, 3.03f)
                curveToRelative(6.44f, -3.63f, 14.23f, -3.12f, 21.25f, -4.88f)
                curveToRelative(1.39f, -0.34f, 2.94f, -0.77f, 3.76f, -1.99f)
                verticalLineToRelative(-7.71f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF689F38)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(100.5f, 94.59f)
                reflectiveCurveToRelative(-16.59f, 0.86f, -34.7f, -4.72f)
                curveTo(47.69f, 84.3f, 39.38f, 78.6f, 39.38f, 78.6f)
                reflectiveCurveToRelative(12.11f, 2.35f, 28.28f, -0.29f)
                curveToRelative(10.59f, -1.73f, 18.44f, -7.72f, 32.82f, -8.0f)
                lineToRelative(0.02f, 24.28f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(57.5f, 79.7f)
                curveToRelative(0.14f, -0.19f, 0.31f, -0.37f, 0.37f, -0.6f)
                curveToRelative(0.07f, -0.28f, -0.04f, -0.57f, -0.12f, -0.84f)
                curveToRelative(-0.22f, -0.79f, -0.15f, -1.66f, 0.18f, -2.42f)
                curveToRelative(0.17f, -0.4f, 0.42f, -0.76f, 0.54f, -1.18f)
                curveToRelative(0.21f, -0.72f, 0.01f, -1.49f, -0.03f, -2.24f)
                curveToRelative(-0.06f, -1.02f, 0.17f, -2.05f, 0.65f, -2.95f)
                curveToRelative(0.07f, -0.13f, 0.15f, -0.26f, 0.29f, -0.33f)
                curveToRelative(0.38f, -0.17f, 0.7f, 0.36f, 0.74f, 0.77f)
                curveToRelative(0.12f, 1.04f, -0.1f, 2.11f, 0.12f, 3.14f)
                curveToRelative(0.31f, 1.44f, 1.47f, 2.69f, 1.39f, 4.17f)
                curveToRelative(-0.03f, 0.53f, -0.22f, 1.06f, -0.12f, 1.57f)
                curveToRelative(0.08f, 0.39f, 0.33f, 0.73f, 0.54f, 1.07f)
                curveToRelative(0.54f, 0.88f, 0.87f, 1.87f, 0.97f, 2.9f)
                curveToRelative(0.12f, 1.23f, -0.25f, 2.7f, -1.4f, 3.13f)
                curveToRelative(-1.25f, 0.48f, -2.53f, -0.54f, -3.51f, -1.21f)
                curveToRelative(-0.9f, -0.62f, -1.33f, -1.9f, -1.3f, -2.95f)
                curveToRelative(0.02f, -0.72f, 0.25f, -1.45f, 0.69f, -2.03f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFFFFFF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(86.82f, 56.92f)
                lineToRelative(-11.92f, 0.87f)
                curveToRelative(-1.97f, 0.14f, -3.97f, 0.29f, -5.91f, -0.09f)
                curveToRelative(-0.92f, -0.18f, -1.82f, -0.48f, -2.76f, -0.53f)
                curveToRelative(-1.71f, -0.09f, -3.34f, 0.64f, -5.0f, 1.06f)
                curveToRelative(-1.82f, 0.46f, -3.73f, 0.55f, -5.58f, 0.26f)
                curveToRelative(-1.29f, -0.2f, -2.61f, -0.62f, -3.55f, -1.53f)
                curveToRelative(-0.47f, -0.45f, -0.83f, -1.02f, -1.33f, -1.45f)
                curveToRelative(-0.61f, -0.54f, -1.4f, -0.84f, -2.08f, -1.28f)
                curveToRelative(-2.88f, -1.84f, -3.56f, -6.35f, -1.35f, -8.96f)
                curveToRelative(0.36f, -0.42f, 0.78f, -0.8f, 1.1f, -1.26f)
                curveToRelative(0.62f, -0.88f, 0.82f, -1.98f, 1.19f, -2.99f)
                curveToRelative(1.17f, -3.25f, 4.22f, -5.72f, 7.63f, -6.28f)
                curveToRelative(3.41f, -0.56f, 7.05f, 0.78f, 9.35f, 3.36f)
                curveToRelative(0.75f, 0.84f, 1.42f, 1.83f, 2.46f, 2.25f)
                curveToRelative(0.61f, 0.24f, 1.29f, 0.26f, 1.94f, 0.34f)
                curveToRelative(3.71f, 0.47f, 6.82f, 3.32f, 8.4f, 6.71f)
                curveToRelative(0.7f, 1.51f, 1.19f, 3.18f, 2.36f, 4.35f)
                curveToRelative(0.48f, 0.48f, 1.06f, 0.86f, 1.64f, 1.22f)
                curveToRelative(2.6f, 1.62f, 5.32f, 3.04f, 8.13f, 4.25f)
                curveToRelative(-0.72f, -0.05f, -1.43f, -0.09f, -2.15f, -0.14f)
            }
            path(
                fill = SolidColor(Color(0xFFC9E3E6)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(74.9f, 57.79f)
                curveToRelative(-1.97f, 0.14f, -3.97f, 0.29f, -5.91f, -0.09f)
                curveToRelative(-0.92f, -0.18f, -1.82f, -0.48f, -2.76f, -0.53f)
                curveToRelative(-1.71f, -0.09f, -3.34f, 0.64f, -5.0f, 1.06f)
                curveToRelative(-1.82f, 0.46f, -3.73f, 0.55f, -5.58f, 0.26f)
                curveToRelative(-1.29f, -0.2f, -2.61f, -0.62f, -3.55f, -1.53f)
                curveToRelative(-0.47f, -0.45f, -0.83f, -1.02f, -1.33f, -1.45f)
                curveToRelative(-0.61f, -0.54f, -1.4f, -0.84f, -2.08f, -1.28f)
                curveToRelative(-1.04f, -0.67f, -1.63f, -1.6f, -2.17f, -2.68f)
                curveToRelative(-0.13f, -0.25f, -0.13f, -0.48f, -0.15f, -0.76f)
                curveToRelative(0.87f, 1.34f, 3.22f, 2.78f, 4.16f, 1.73f)
                curveToRelative(0.31f, -0.92f, -0.79f, -2.52f, -1.16f, -3.79f)
                curveToRelative(-0.7f, -2.4f, -0.77f, -4.89f, 0.09f, -7.24f)
                curveToRelative(0.1f, -0.28f, 0.35f, -1.19f, 0.69f, -1.67f)
                curveToRelative(0.26f, 1.95f, 0.94f, 4.91f, 2.33f, 6.3f)
                curveToRelative(1.39f, 1.39f, 3.71f, 1.98f, 5.38f, 0.94f)
                curveToRelative(1.05f, -0.66f, 1.67f, -1.81f, 2.43f, -2.79f)
                curveToRelative(2.24f, -2.87f, 6.15f, -4.34f, 9.73f, -3.65f)
                curveToRelative(-1.85f, 1.08f, -3.65f, 2.39f, -4.74f, 4.23f)
                reflectiveCurveToRelative(-1.35f, 4.31f, -0.13f, 6.08f)
                curveToRelative(0.98f, 1.42f, 2.76f, 2.15f, 4.48f, 2.12f)
                curveToRelative(0.95f, -0.02f, 2.02f, -0.35f, 2.41f, -1.22f)
                curveToRelative(0.22f, -0.5f, 0.17f, -1.1f, 0.42f, -1.59f)
                curveToRelative(0.47f, -0.94f, 1.85f, -1.06f, 2.78f, -0.58f)
                curveToRelative(0.94f, 0.49f, 1.58f, 1.38f, 2.3f, 2.14f)
                curveToRelative(2.7f, 2.84f, 6.69f, 3.98f, 10.47f, 5.0f)
                curveToRelative(-0.11f, -0.03f, -0.34f, 0.06f, -0.46f, 0.08f)
                lineToRelative(-0.54f, 0.09f)
                curveToRelative(-0.28f, 0.05f, -0.56f, 0.12f, -0.85f, 0.08f)
                curveToRelative(-0.6f, -0.09f, -1.21f, 0.01f, -1.81f, 0.05f)
                curveToRelative(-1.41f, 0.1f, -2.83f, 0.21f, -4.24f, 0.31f)
                curveToRelative(-1.73f, 0.12f, -3.47f, 0.25f, -5.21f, 0.38f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF8BC34A)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(100.5f, 86.03f)
                curveToRelative(-35.07f, 8.17f, -51.74f, -11.9f, -73.0f, -14.16f)
                verticalLineToRelative(28.63f)
                horizontalLineToRelative(73.0f)
                verticalLineTo(86.03f)
                close()
            }
            path(
                fill = radialGradient(
                    0.0f to Color(0xFFD4E157), 1.0f to Color(0x00D4E157), center
                    = Offset(79.38561f, 86.79574f), radius = 35.94364f
                ), stroke = null,
                strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(100.5f, 86.03f)
                curveToRelative(-35.07f, 8.17f, -51.74f, -11.9f, -73.0f, -14.16f)
                verticalLineToRelative(28.63f)
                horizontalLineToRelative(73.0f)
                verticalLineTo(86.03f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(36.32f, 77.15f)
                curveToRelative(-1.04f, 0.31f, -1.9f, -0.49f, -2.77f, -0.91f)
                curveToRelative(-2.31f, -1.71f, -2.31f, -4.83f, -2.13f, -7.44f)
                curveToRelative(-0.05f, -0.62f, 0.39f, -1.64f, 1.12f, -1.2f)
                curveToRelative(-0.3f, -1.5f, -0.33f, -3.05f, -0.11f, -4.56f)
                curveToRelative(0.15f, -1.17f, 0.56f, -1.98f, 1.51f, -2.58f)
                curveToRelative(0.06f, -0.12f, 0.02f, -0.26f, -0.01f, -0.39f)
                curveToRelative(-0.44f, -1.72f, 0.36f, -3.5f, 0.44f, -5.28f)
                curveToRelative(0.07f, -1.39f, -0.3f, -2.83f, 0.18f, -4.13f)
                curveToRelative(0.2f, -0.58f, 0.93f, -0.9f, 1.06f, -0.17f)
                curveToRelative(0.7f, 2.74f, 1.91f, 5.47f, 1.68f, 8.35f)
                curveToRelative(-0.08f, 0.39f, 0.07f, 0.71f, 0.39f, 0.94f)
                curveToRelative(2.04f, 1.82f, 1.13f, 4.99f, 1.46f, 7.46f)
                curveToRelative(0.96f, 2.27f, 0.94f, 4.81f, -0.2f, 7.02f)
                curveToRelative(-0.52f, 1.22f, -1.36f, 2.35f, -2.62f, 2.89f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF689F38)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(50.15f, 78.67f)
                curveToRelative(-4.42f, -1.06f, -4.6f, 0.11f, -6.06f, -0.23f)
                curveToRelative(-5.02f, -1.51f, -1.93f, -6.4f, -0.48f, -7.59f)
                curveToRelative(-0.61f, -2.33f, 0.98f, -3.28f, 2.86f, -5.58f)
                curveToRelative(0.3f, -4.71f, 4.82f, -3.34f, 4.24f, 0.79f)
                curveToRelative(1.2f, 1.56f, 1.64f, 3.27f, 1.25f, 5.22f)
                curveToRelative(0.12f, 1.16f, 1.25f, 1.92f, 1.78f, 2.96f)
                curveToRelative(1.03f, 2.03f, -0.7f, 5.12f, -3.59f, 4.43f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF2E7D32)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.47f, 71.01f)
                curveToRelative(-0.53f, 1.1f, -2.05f, 1.33f, -3.24f, 1.03f)
                curveToRelative(-0.55f, -0.14f, -1.13f, -0.44f, -1.66f, -0.24f)
                curveToRelative(-1.24f, 0.46f, -1.63f, 1.46f, -1.67f, 2.46f)
                curveToRelative(0.0f, 1.34f, 1.34f, 2.39f, 2.68f, 2.45f)
                curveToRelative(1.34f, 0.06f, 2.6f, -0.64f, 3.61f, -1.5f)
                curveToRelative(0.3f, 1.8f, 3.09f, 1.75f, 4.31f, 0.72f)
                curveToRelative(1.16f, -0.98f, 0.66f, -2.55f, 0.66f, -2.55f)
                curveToRelative(1.39f, 1.55f, 1.32f, 2.77f, 1.16f, 4.06f)
                curveToRelative(-0.15f, 1.24f, -1.22f, 2.62f, -0.58f, 3.58f)
                curveToRelative(-2.22f, 0.57f, -3.32f, -1.34f, -5.09f, -2.13f)
                curveToRelative(-0.65f, -0.18f, -1.33f, 0.01f, -1.99f, 0.14f)
                curveToRelative(-2.72f, 0.71f, -5.77f, -0.89f, -5.79f, -3.9f)
                curveToRelative(-0.02f, -1.45f, 0.78f, -2.88f, 2.0f, -3.67f)
                curveToRelative(0.14f, -0.09f, 0.29f, -0.18f, 0.35f, -0.34f)
                curveToRelative(0.06f, -0.17f, 0.0f, -0.35f, -0.06f, -0.52f)
                curveToRelative(-1.24f, -3.32f, 2.0f, -3.29f, 3.4f, -5.44f)
                curveToRelative(0.56f, 0.62f, 1.48f, 0.88f, 2.28f, 0.65f)
                curveToRelative(-0.63f, 1.46f, -2.4f, 0.76f, -3.52f, 1.35f)
                curveToRelative(-2.08f, 1.71f, 1.34f, 4.11f, 3.15f, 3.85f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF1B5E20)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(33.5f, 64.3f)
                curveToRelative(0.72f, 1.31f, 2.62f, 2.37f, 2.19f, 4.08f)
                curveToRelative(-0.65f, -0.43f, -1.44f, -0.98f, -2.23f, -0.95f)
                curveToRelative(-0.66f, 0.88f, -0.4f, 2.42f, 0.12f, 3.33f)
                curveToRelative(2.42f, 3.24f, 5.03f, -1.65f, 5.23f, -3.94f)
                curveToRelative(2.94f, 3.44f, 0.25f, 12.98f, -5.07f, 9.66f)
                curveToRelative(-0.71f, -0.57f, -2.78f, -2.86f, -2.66f, -6.68f)
                curveToRelative(0.07f, -2.32f, 1.0f, -2.47f, 1.54f, -2.4f)
                curveToRelative(-0.79f, -1.22f, -0.94f, -2.95f, -0.63f, -4.2f)
                curveToRelative(0.27f, -1.07f, 0.74f, -1.93f, 1.46f, -2.63f)
                curveToRelative(0.73f, -0.72f, 0.2f, -1.36f, 0.22f, -2.46f)
                curveToRelative(0.02f, -1.6f, 0.44f, -2.52f, 0.77f, -3.43f)
                curveToRelative(0.12f, 1.7f, 0.75f, 3.45f, 0.48f, 5.14f)
                curveToRelative(-0.16f, 0.72f, -0.96f, 0.71f, -1.32f, 1.23f)
                curveToRelative(-0.59f, 0.97f, -0.64f, 2.25f, -0.1f, 3.25f)
                close()
            }
            path(
                fill = radialGradient(
                    0.441f to Color(0xFFA06841), 0.923f to Color(0x23A06841),
                    1.0f to Color(0x00A06841), center = Offset(37.615337f, 76.84977f), radius =
                    4.758194f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(37.06f, 82.18f)
                curveToRelative(0.38f, 1.23f, -4.12f, 0.33f, -3.49f, -1.25f)
                curveToRelative(0.44f, -1.1f, 0.64f, -2.25f, 0.86f, -3.38f)
                curveToRelative(0.22f, -1.79f, -0.31f, -3.39f, -1.14f, -4.92f)
                curveToRelative(0.19f, -0.17f, 0.41f, -0.32f, 0.64f, -0.43f)
                curveToRelative(1.8f, 3.34f, 1.86f, 2.16f, 3.34f, -0.45f)
                curveToRelative(0.28f, -0.03f, 0.56f, -0.01f, 0.83f, 0.06f)
                curveToRelative(-1.79f, 2.84f, -2.22f, 7.09f, -1.04f, 10.37f)
                close()
            }
        }
            .build()
        return _picture!!
    }

private var _picture: ImageVector? = null
