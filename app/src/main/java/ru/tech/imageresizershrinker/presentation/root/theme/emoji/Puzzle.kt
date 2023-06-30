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

val Emoji.Puzzle: ImageVector
    get() {
        if (_puzzle != null) {
            return _puzzle!!
        }
        _puzzle = Builder(
            name = "Puzzle", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFB0B0AF)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(32.74f, 59.8f)
                curveToRelative(-0.42f, -0.42f, -25.99f, -6.05f, -25.99f, -6.05f)
                reflectiveCurveToRelative(-1.49f, 2.39f, -1.06f, 3.8f)
                curveToRelative(0.24f, 0.79f, 5.82f, 7.28f, 11.56f, 13.25f)
                curveToRelative(4.53f, 4.72f, 9.68f, 9.52f, 10.43f, 12.26f)
                curveToRelative(1.15f, 4.22f, 1.27f, 10.99f, 1.27f, 10.99f)
                lineToRelative(-16.67f, 10.18f)
                reflectiveCurveToRelative(2.04f, 10.38f, 15.26f, 9.97f)
                curveToRelative(12.35f, -0.38f, 12.99f, -13.46f, 16.91f, -13.53f)
                curveToRelative(0.66f, -0.01f, 1.57f, -0.24f, 3.2f, 1.51f)
                curveToRelative(4.04f, 4.34f, 11.6f, 12.72f, 14.13f, 15.26f)
                curveToRelative(3.38f, 3.38f, 5.78f, 6.76f, 7.05f, 6.62f)
                curveToRelative(1.27f, -0.14f, 11.98f, -9.72f, 15.08f, -12.68f)
                curveToRelative(3.1f, -2.96f, 5.92f, -6.48f, 5.92f, -9.44f)
                curveToRelative(0.0f, -2.96f, -4.37f, -5.21f, -4.37f, -5.21f)
                lineToRelative(-24.1f, 2.82f)
                lineTo(32.74f, 59.8f)
                close()
                moveTo(103.62f, 21.62f)
                curveToRelative(0.42f, 0.0f, 11.13f, -5.35f, 11.13f, -5.35f)
                reflectiveCurveToRelative(5.5f, 11.13f, -1.55f, 17.47f)
                curveToRelative(-7.05f, 6.34f, -8.6f, 2.75f, -11.98f, 6.48f)
                curveToRelative(-2.58f, 2.85f, -1.27f, 8.74f, -1.27f, 8.74f)
                lineTo(120.9f, 69.1f)
                reflectiveCurveToRelative(1.17f, 2.13f, 0.33f, 3.68f)
                curveToRelative(-0.6f, 1.1f, -7.65f, 8.99f, -13.11f, 14.66f)
                curveToRelative(-2.46f, 2.55f, -5.89f, 3.41f, -7.75f, 2.96f)
                curveToRelative(-4.65f, -1.13f, -5.35f, -4.79f, -6.2f, -11.27f)
                curveToRelative(-0.85f, -6.48f, -6.43f, -6.38f, -8.46f, -6.06f)
                curveToRelative(-4.28f, 0.68f, -7.07f, 3.18f, -7.07f, 3.18f)
                reflectiveCurveToRelative(-25.2f, -30.37f, -24.92f, -31.22f)
                curveToRelative(0.28f, -0.85f, 1.27f, -7.19f, -1.69f, -9.86f)
                curveToRelative(-2.96f, -2.68f, -8.87f, -4.04f, -11.13f, -5.59f)
                curveToRelative(-2.25f, -1.55f, -2.55f, -4.5f, -2.14f, -7.36f)
                reflectiveCurveToRelative(18.63f, -2.83f, 18.63f, -2.83f)
                reflectiveCurveToRelative(31.42f, 17.33f, 31.57f, 16.63f)
                curveToRelative(0.14f, -0.74f, 14.66f, -14.4f, 14.66f, -14.4f)
                close()
            }
            path(
                fill = radialGradient(
                    0.508f to Color(0xFFB7D118),
                    0.572f to Color(0xFFB2D019),
                    0.643f to Color(0xFFA5CD1D),
                    0.717f to Color(0xFF8FC922),
                    0.793f to
                            Color(0xFF70C22A),
                    0.871f to Color(0xFF48BA34),
                    0.949f to Color(0xFF18B040),
                    0.981f to Color(0xFF02AB46),
                    center = Offset(62.172f, -28.3f),
                    radius =
                    119.225f
                ), stroke = null, strokeLineWidth = 0.0f, strokeLineCap = Butt,
                strokeLineJoin = Miter, strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(88.12f, 24.99f)
                curveToRelative(0.76f, -2.78f, -0.11f, -9.61f, 5.2f, -14.13f)
                reflectiveCurveToRelative(13.57f, -3.96f, 18.54f, 0.79f)
                reflectiveCurveToRelative(5.77f, 13.68f, -0.11f, 19.56f)
                curveToRelative(-5.88f, 5.88f, -8.59f, 2.6f, -11.87f, 5.77f)
                curveToRelative(-3.28f, 3.17f, -2.71f, 5.77f, -0.68f, 7.69f)
                curveToRelative(2.04f, 1.92f, 22.28f, 23.07f, 21.71f, 24.42f)
                reflectiveCurveToRelative(-14.36f, 16.96f, -16.62f, 18.09f)
                curveToRelative(-2.26f, 1.13f, -6.22f, 1.02f, -7.01f, -2.71f)
                reflectiveCurveToRelative(0.57f, -11.19f, -5.09f, -13.12f)
                curveToRelative(-5.65f, -1.92f, -8.14f, -0.34f, -13.68f, 5.54f)
                curveToRelative(-5.54f, 5.88f, -8.03f, 12.32f, -3.51f, 15.6f)
                curveToRelative(4.52f, 3.28f, 10.18f, 1.92f, 12.44f, 5.77f)
                reflectiveCurveToRelative(-1.13f, 7.35f, -7.12f, 12.89f)
                curveToRelative(-5.99f, 5.54f, -10.63f, 10.06f, -11.42f, 9.5f)
                curveToRelative(-0.79f, -0.57f, -17.98f, -18.77f, -19.67f, -20.69f)
                curveToRelative(-1.7f, -1.92f, -4.98f, -3.62f, -8.14f, -0.68f)
                curveToRelative(-3.17f, 2.94f, -3.73f, 12.55f, -17.07f, 12.55f)
                reflectiveCurveTo(9.08f, 97.36f, 13.83f, 91.37f)
                reflectiveCurveToRelative(15.26f, -6.56f, 15.49f, -10.63f)
                curveToRelative(0.1f, -1.87f, -5.67f, -6.91f, -10.85f, -12.32f)
                curveTo(12.38f, 62.05f, 6.72f, 55.3f, 6.48f, 54.51f)
                curveToRelative(-0.45f, -1.47f, 12.66f, -14.36f, 15.15f, -16.17f)
                reflectiveCurveToRelative(5.31f, -0.68f, 6.67f, 0.79f)
                curveToRelative(1.36f, 1.47f, 1.7f, 5.99f, 2.83f, 8.59f)
                curveToRelative(1.13f, 2.6f, 6.44f, 13.91f, 19.22f, 1.02f)
                curveToRelative(12.66f, -12.78f, 0.68f, -18.32f, -3.39f, -19.45f)
                curveToRelative(-4.07f, -1.13f, -7.0f, -1.5f, -8.14f, -5.65f)
                curveToRelative(-0.9f, -3.28f, 4.07f, -7.12f, 9.27f, -12.1f)
                reflectiveCurveToRelative(8.25f, -7.58f, 9.61f, -7.58f)
                curveToRelative(1.36f, 0.0f, 19.56f, 19.56f, 21.94f, 21.82f)
                reflectiveCurveToRelative(3.84f, 2.83f, 5.54f, 2.37f)
                curveToRelative(1.69f, -0.44f, 2.6f, -1.91f, 2.94f, -3.16f)
                close()
            }
        }
            .build()
        return _puzzle!!
    }

private var _puzzle: ImageVector? = null
