package ru.tech.imageresizershrinker.theme.emoji

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Brush.Companion.radialGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.theme.Emoji

public val Emoji.Sunrise: ImageVector
    get() {
        if (_sunrise != null) {
            return _sunrise!!
        }
        _sunrise = Builder(name = "Sunrise", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
                viewportWidth = 128.0f, viewportHeight = 128.0f).apply {
            path(fill = radialGradient(0.275f to Color(0xFFFFD0AF), 0.372f to Color(0xFFFFC7AB),
                    0.529f to Color(0xFFFFAFA0), 0.631f to Color(0xFFFF9C97), 0.702f to
                    Color(0xFFFF947A), 0.79f to Color(0xFFFF8C5A), 0.865f to Color(0xFFFC895C),
                    0.928f to Color(0xFFF47E62), 0.987f to Color(0xFFE56D6D), 1.0f to
                    Color(0xFFE16870), center = Offset(63.542f,78.723f), radius = 88.912f), stroke =
                    null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(116.62f, 124.26f)
                horizontalLineTo(11.32f)
                curveToRelative(-4.15f, 0.0f, -7.52f, -3.37f, -7.52f, -7.52f)
                verticalLineTo(11.44f)
                curveToRelative(0.0f, -4.15f, 3.37f, -7.52f, 7.52f, -7.52f)
                horizontalLineToRelative(105.3f)
                curveToRelative(4.15f, 0.0f, 7.52f, 3.37f, 7.52f, 7.52f)
                verticalLineToRelative(105.3f)
                curveToRelative(0.01f, 4.15f, -3.36f, 7.52f, -7.52f, 7.52f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFF7B3)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(64.48f, 47.56f)
                curveToRelative(-14.65f, 0.06f, -27.84f, 12.2f, -27.66f, 28.21f)
                reflectiveCurveToRelative(11.47f, 27.83f, 28.16f, 27.64f)
                curveToRelative(17.13f, -0.2f, 27.09f, -13.06f, 26.97f, -27.49f)
                curveToRelative(-0.12f, -14.44f, -10.3f, -28.42f, -27.47f, -28.36f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFDFA)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(55.64f, 57.24f)
                curveToRelative(-2.68f, -2.16f, -6.97f, 0.85f, -9.21f, 4.61f)
                curveToRelative(-2.02f, 3.39f, -2.95f, 7.77f, -0.4f, 9.06f)
                curveToRelative(3.29f, 1.67f, 5.26f, -2.83f, 7.04f, -5.72f)
                curveToRelative(1.76f, -2.89f, 5.21f, -5.82f, 2.57f, -7.95f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.975f to Color(0x0DFFD525), 1.0f to Color(0x00FFD525), center =
                    Offset(35.001f,77.736f), radius = 32.437f), stroke = null, strokeLineWidth =
                    0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveToRelative(3.85f, 74.01f)
                lineToRelative(-0.08f, 6.88f)
                lineToRelative(30.82f, 2.55f)
                reflectiveCurveToRelative(-0.97f, -3.21f, -1.09f, -7.67f)
                lineTo(3.85f, 74.01f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.906f to Color(0x00FFD525), center = Offset(34.733f,70.686f), radius =
                    36.905f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.02f, 56.26f)
                curveToRelative(-0.03f, 3.98f, -0.06f, 7.8f, -0.1f, 11.19f)
                lineToRelative(29.65f, 5.34f)
                curveToRelative(0.05f, -0.8f, 0.14f, -1.62f, 0.27f, -2.46f)
                curveToRelative(0.13f, -0.87f, 0.3f, -1.74f, 0.5f, -2.59f)
                lineTo(4.02f, 56.26f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.897f to Color(0x00FFD525), center = Offset(38.928f,59.148f), radius =
                    44.193f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(4.1f, 34.38f)
                curveToRelative(0.01f, 4.07f, 0.0f, 8.5f, -0.03f, 12.97f)
                lineToRelative(31.07f, 17.49f)
                curveToRelative(0.63f, -1.92f, 1.45f, -3.74f, 2.42f, -5.47f)
                lineTo(4.1f, 34.38f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.033f to Color(0xFFFFF29E),
                    0.111f to Color(0xFFFFE873), 0.186f to Color(0xFFFFE051), 0.258f to
                    Color(0xFFFFDA39), 0.324f to Color(0xFFFFD62A), 0.378f to Color(0xFFFFD525),
                    0.757f to Color(0x00FFD525), center = Offset(41.779f,49.682f), radius =
                    51.324f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(7.53f, 7.42f)
                curveTo(5.2f, 9.61f, 3.67f, 13.15f, 3.9f, 18.87f)
                curveToRelative(0.02f, 0.61f, 0.05f, 1.32f, 0.07f, 2.1f)
                lineTo(39.2f, 56.74f)
                curveToRelative(1.17f, -1.68f, 2.53f, -3.22f, 4.03f, -4.62f)
                lineTo(7.53f, 7.42f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.854f to Color(0x00FFD525), center = Offset(44.695f,50.914f), radius =
                    50.513f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(29.96f, 3.89f)
                curveToRelative(-6.29f, 0.0f, -10.78f, 0.02f, -12.17f, 0.08f)
                curveToRelative(-0.52f, 0.02f, -1.05f, 0.05f, -1.58f, 0.1f)
                lineToRelative(29.12f, 46.27f)
                arcToRelative(29.04f, 29.04f, 0.0f, false, true, 4.94f, -3.03f)
                lineTo(29.96f, 3.89f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.976f to Color(0x00FFD525), center = Offset(56.16f,45.789f), radius = 42.264f),
                    stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                    Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(51.45f, 3.95f)
                curveToRelative(-3.9f, -0.02f, -7.69f, -0.03f, -11.23f, -0.04f)
                lineToRelative(12.63f, 42.3f)
                curveToRelative(1.92f, -0.71f, 3.96f, -1.24f, 6.12f, -1.57f)
                lineTo(51.45f, 3.95f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.975f to Color(0x0DFFD525), 1.0f to Color(0x00FFD525), center =
                    Offset(64.665f,45.167f), radius = 41.283f), stroke = null, strokeLineWidth =
                    0.0f, strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(70.26f, 4.01f)
                curveToRelative(-3.82f, -0.03f, -6.31f, -0.09f, -10.16f, -0.11f)
                lineToRelative(1.56f, 40.42f)
                curveToRelative(1.1f, -0.08f, 2.23f, -0.12f, 3.38f, -0.1f)
                curveToRelative(1.1f, 0.02f, 2.19f, 0.09f, 3.25f, 0.22f)
                lineToRelative(1.97f, -40.43f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.04f to Color(0xFFFFF29E), 0.133f
                    to Color(0xFFFFE873), 0.224f to Color(0xFFFFE051), 0.309f to Color(0xFFFFDA39),
                    0.388f to Color(0xFFFFD62A), 0.453f to Color(0xFFFFD525), 0.959f to
                    Color(0x00FFD525), center = Offset(73.378f,46.619f), radius = 44.476f), stroke =
                    null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(90.45f, 4.21f)
                curveToRelative(-3.52f, -0.03f, -7.37f, -0.06f, -11.41f, -0.09f)
                lineToRelative(-8.11f, 40.76f)
                curveToRelative(2.09f, 0.44f, 4.06f, 1.1f, 5.95f, 1.92f)
                lineTo(90.45f, 4.21f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.926f to Color(0x00FFD525), center = Offset(81.468f,49.523f), radius =
                    47.227f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(111.37f, 4.59f)
                curveToRelative(-1.28f, -0.09f, -2.67f, -0.15f, -4.17f, -0.18f)
                curveToRelative(-1.68f, -0.04f, -4.49f, -0.07f, -8.1f, -0.11f)
                lineTo(79.34f, 48.01f)
                curveToRelative(1.67f, 0.91f, 3.22f, 1.98f, 4.67f, 3.16f)
                lineToRelative(27.36f, -46.58f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.037f to Color(0xFFFFF29E),
                    0.126f to Color(0xFFFFE873), 0.212f to Color(0xFFFFE051), 0.293f to
                    Color(0xFFFFDA39), 0.368f to Color(0xFFFFD62A), 0.43f to Color(0xFFFFD525),
                    0.832f to Color(0x00FFD525), center = Offset(85.311f,52.263f), radius =
                    49.025f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(124.13f, 23.3f)
                curveToRelative(0.01f, -6.26f, 0.02f, -10.77f, 0.02f, -11.86f)
                curveToRelative(0.0f, -1.39f, -0.45f, -2.56f, -1.41f, -3.52f)
                lineToRelative(-36.75f, 45.0f)
                arcToRelative(32.43f, 32.43f, 0.0f, false, true, 3.97f, 4.65f)
                lineToRelative(34.17f, -34.27f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    1.0f to Color(0x00FFD525), center = Offset(92.896f,62.144f), radius = 35.226f),
                    stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                    Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(124.08f, 48.54f)
                lineToRelative(0.03f, -13.37f)
                lineToRelative(-32.79f, 24.52f)
                curveToRelative(1.05f, 1.79f, 1.89f, 3.68f, 2.55f, 5.62f)
                lineToRelative(30.21f, -16.77f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    1.0f to Color(0x00FFD525), center = Offset(94.17f,70.686f), radius = 35.972f),
                    stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin =
                    Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveToRelative(95.36f, 73.6f)
                lineToRelative(28.68f, -4.23f)
                curveToRelative(0.01f, -3.27f, 0.01f, -6.98f, 0.02f, -10.93f)
                lineTo(94.6f, 67.87f)
                curveToRelative(0.44f, 1.85f, 0.7f, 3.74f, 0.75f, 5.64f)
                curveToRelative(0.0f, 0.03f, 0.0f, 0.06f, 0.01f, 0.09f)
                close()
            }
            path(fill = radialGradient(0.0f to Color(0xFFFFF7B3), 0.046f to Color(0xFFFFF29E),
                    0.155f to Color(0xFFFFE873), 0.261f to Color(0xFFFFE051), 0.36f to
                    Color(0xFFFFDA39), 0.452f to Color(0xFFFFD62A), 0.528f to Color(0xFFFFD525),
                    0.942f to Color(0x00FFD525), center = Offset(94.537f,82.092f), radius =
                    31.836f), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                    strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(95.35f, 76.7f)
                curveToRelative(-0.18f, 6.48f, -1.27f, 11.96f, -1.27f, 11.96f)
                lineToRelative(29.93f, -6.75f)
                reflectiveCurveToRelative(0.0f, -2.34f, 0.01f, -6.17f)
                lineToRelative(-28.67f, 0.96f)
                close()
            }
            path(fill = radialGradient(0.246f to Color(0xFFCAD7A8), 0.34f to Color(0xFFC5D6AA),
                    0.457f to Color(0xFFB5D4B0), 0.587f to Color(0xFF9CD1B9), 0.724f to
                    Color(0xFF79CDC6), 0.867f to Color(0xFF4CC7D6), 0.997f to Color(0xFF1DC1E8),
                    center = Offset(65.22744f,68.51817f), radius = 54.924046f), stroke = null,
                    strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f, pathFillType = NonZero) {
                moveTo(124.15f, 79.02f)
                verticalLineToRelative(37.72f)
                curveToRelative(0.0f, 4.15f, -3.37f, 7.52f, -7.52f, 7.52f)
                horizontalLineTo(11.32f)
                curveToRelative(-4.15f, 0.0f, -7.52f, -3.37f, -7.52f, -7.52f)
                verticalLineTo(78.55f)
                lineToRelative(120.35f, 0.47f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(35.72f, 85.04f)
                curveToRelative(-0.12f, 2.0f, 1.5f, 3.43f, 4.5f, 3.5f)
                curveToRelative(3.0f, 0.07f, 46.6f, -0.12f, 48.1f, -0.07f)
                curveToRelative(2.36f, 0.07f, 4.29f, -1.07f, 4.15f, -3.43f)
                curveToRelative(-0.14f, -2.36f, -2.43f, -2.64f, -3.93f, -2.64f)
                curveToRelative(-1.01f, 0.0f, -18.44f, -0.07f, -26.87f, -0.07f)
                reflectiveCurveToRelative(-21.16f, 0.07f, -22.58f, 0.14f)
                reflectiveCurveToRelative(-3.23f, 0.21f, -3.37f, 2.57f)
                close()
                moveTo(40.23f, 94.54f)
                curveToRelative(-0.09f, 2.43f, 1.93f, 2.86f, 4.5f, 2.93f)
                curveToRelative(2.57f, 0.07f, 12.79f, 0.07f, 19.8f, 0.07f)
                reflectiveCurveToRelative(18.3f, -0.14f, 20.08f, -0.14f)
                curveToRelative(1.5f, 0.0f, 3.14f, -1.0f, 3.07f, -2.93f)
                curveToRelative(-0.06f, -1.51f, -0.93f, -2.57f, -3.5f, -2.64f)
                curveToRelative(-2.57f, -0.07f, -22.37f, 0.07f, -29.02f, 0.0f)
                reflectiveCurveToRelative(-9.93f, 0.0f, -11.44f, 0.0f)
                curveToRelative(-1.49f, 0.0f, -3.42f, 0.85f, -3.49f, 2.71f)
                close()
                moveTo(48.67f, 102.87f)
                curveToRelative(0.07f, 1.79f, 1.64f, 2.5f, 3.5f, 2.5f)
                reflectiveCurveToRelative(23.66f, 0.21f, 25.44f, 0.14f)
                curveToRelative(1.79f, -0.07f, 2.93f, -1.29f, 3.0f, -2.72f)
                curveToRelative(0.07f, -1.43f, -1.22f, -2.22f, -2.93f, -2.22f)
                reflectiveCurveToRelative(-24.66f, -0.14f, -26.09f, -0.07f)
                curveToRelative(-1.42f, 0.09f, -3.0f, 0.66f, -2.92f, 2.37f)
                close()
            }
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(56.24f, 106.95f)
                curveToRelative(0.16f, 1.43f, 0.71f, 2.5f, 3.29f, 2.57f)
                curveToRelative(2.57f, 0.07f, 10.29f, -0.01f, 11.72f, -0.07f)
                curveToRelative(1.57f, -0.07f, 2.32f, -1.35f, 2.36f, -2.5f)
                curveToRelative(0.07f, -1.93f, -1.72f, -2.36f, -1.72f, -2.36f)
                lineToRelative(-14.22f, 0.14f)
                reflectiveCurveToRelative(-1.57f, 0.93f, -1.43f, 2.22f)
                close()
            }
            path(fill = SolidColor(Color(0xFFBAF3FA)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(93.34f, 92.25f)
                curveToRelative(0.09f, 1.86f, 1.0f, 2.93f, 4.15f, 3.0f)
                reflectiveCurveToRelative(6.15f, 0.07f, 7.5f, 0.0f)
                reflectiveCurveToRelative(2.84f, -0.99f, 2.72f, -3.0f)
                curveToRelative(-0.14f, -2.36f, -2.14f, -2.36f, -3.43f, -2.5f)
                curveToRelative(-1.29f, -0.14f, -6.86f, -0.07f, -8.22f, -0.07f)
                reflectiveCurveToRelative(-2.8f, 1.14f, -2.72f, 2.57f)
                close()
                moveTo(16.86f, 90.61f)
                curveToRelative(0.0f, 2.0f, 1.64f, 2.43f, 3.57f, 2.5f)
                curveToRelative(1.93f, 0.07f, 5.07f, 0.07f, 6.36f, -0.07f)
                reflectiveCurveToRelative(2.29f, -1.07f, 2.36f, -2.43f)
                reflectiveCurveToRelative(-1.29f, -2.22f, -3.07f, -2.22f)
                reflectiveCurveToRelative(-5.43f, 0.07f, -6.65f, 0.07f)
                curveToRelative(-1.21f, 0.01f, -2.57f, 0.94f, -2.57f, 2.15f)
                close()
                moveTo(29.08f, 94.61f)
                curveToRelative(-2.13f, 0.25f, -2.57f, 1.72f, -2.5f, 2.72f)
                reflectiveCurveToRelative(0.86f, 2.22f, 2.64f, 2.22f)
                reflectiveCurveToRelative(6.22f, 0.0f, 7.5f, -0.14f)
                curveToRelative(1.29f, -0.14f, 1.72f, -1.36f, 1.79f, -2.57f)
                curveToRelative(0.07f, -1.21f, -1.0f, -2.14f, -2.29f, -2.22f)
                curveToRelative(-1.28f, -0.08f, -5.28f, -0.22f, -7.14f, -0.01f)
                close()
            }
        }
        .build()
        return _sunrise!!
    }

private var _sunrise: ImageVector? = null
