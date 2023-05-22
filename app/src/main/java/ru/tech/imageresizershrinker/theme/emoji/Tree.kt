package ru.tech.imageresizershrinker.theme.emoji

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

public val Emoji.Tree: ImageVector
    get() {
        if (_tree != null) {
            return _tree!!
        }
        _tree = Builder(
            name = "Tree", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF5B9821)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(32.0f, 24.66f)
                reflectiveCurveToRelative(2.43f, -7.3f, 8.16f, -11.88f)
                reflectiveCurveToRelative(58.85f, 15.04f, 58.85f, 15.04f)
                reflectiveCurveToRelative(9.28f, 3.31f, 14.77f, 12.08f)
                curveToRelative(3.69f, 5.9f, 5.75f, 14.62f, 1.84f, 25.15f)
                curveToRelative(-9.32f, 25.08f, -33.5f, 22.18f, -39.8f, 20.75f)
                curveToRelative(-2.96f, -0.67f, -10.17f, -2.58f, -10.17f, -2.58f)
                reflectiveCurveToRelative(-16.47f, 13.6f, -36.29f, 3.17f)
                curveToRelative(-15.09f, -7.94f, -13.22f, -19.81f, -13.22f, -19.81f)
                reflectiveCurveTo(5.98f, 56.42f, 10.38f, 42.27f)
                curveTo(15.68f, 25.23f, 32.0f, 24.66f, 32.0f, 24.66f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF8BC02B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(66.37f, 55.01f)
                curveToRelative(13.75f, -2.29f, 18.04f, -8.16f, 21.77f, -13.6f)
                curveToRelative(4.11f, -6.0f, 3.01f, -11.03f, 5.87f, -11.17f)
                reflectiveCurveToRelative(5.14f, 5.02f, 2.0f, 14.03f)
                curveToRelative(-4.44f, 12.74f, -13.57f, 14.61f, -13.75f, 18.04f)
                curveToRelative(-0.14f, 2.72f, 9.72f, 2.91f, 18.62f, -1.43f)
                curveToRelative(12.89f, -6.3f, 12.89f, -21.05f, 12.89f, -21.05f)
                reflectiveCurveToRelative(-2.29f, -4.73f, -6.73f, -8.02f)
                reflectiveCurveToRelative(-7.74f, -5.01f, -7.74f, -5.01f)
                reflectiveCurveTo(94.0f, 6.9f, 69.95f, 3.6f)
                reflectiveCurveTo(36.01f, 17.07f, 36.01f, 17.07f)
                reflectiveCurveToRelative(0.86f, 5.44f, 5.01f, 11.74f)
                curveToRelative(3.31f, 5.03f, 6.73f, 4.87f, 6.44f, 7.73f)
                curveToRelative(-0.19f, 1.86f, -6.16f, 2.43f, -12.46f, -0.29f)
                curveToRelative(-5.74f, -2.48f, -7.16f, -5.87f, -8.73f, -5.87f)
                reflectiveCurveTo(11.68f, 37.63f, 17.11f, 53.0f)
                curveToRelative(4.3f, 12.17f, 18.51f, 14.45f, 21.05f, 14.89f)
                curveToRelative(7.45f, 1.29f, 11.74f, -3.58f, 6.44f, -7.45f)
                reflectiveCurveToRelative(-11.17f, -6.16f, -9.88f, -10.02f)
                curveToRelative(1.39f, -4.18f, 6.35f, 0.18f, 12.31f, 2.76f)
                curveToRelative(5.89f, 2.55f, 13.02f, 2.89f, 19.34f, 1.83f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF6D4B41)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(39.03f, 70.97f)
                reflectiveCurveToRelative(3.3f, -0.19f, 4.66f, -0.58f)
                curveToRelative(1.36f, -0.39f, 3.2f, -1.55f, 3.2f, -1.55f)
                reflectiveCurveTo(59.64f, 82.18f, 60.96f, 83.3f)
                curveToRelative(1.29f, 1.09f, 2.81f, -15.53f, 2.81f, -15.53f)
                reflectiveCurveToRelative(2.91f, 0.45f, 5.63f, 0.02f)
                curveToRelative(3.58f, -0.57f, 6.02f, -1.71f, 6.02f, -1.71f)
                reflectiveCurveTo(73.68f, 76.31f, 73.0f, 80.19f)
                curveToRelative(-0.43f, 2.49f, -0.58f, 9.41f, -0.58f, 9.41f)
                lineToRelative(-9.32f, 7.76f)
                lineToRelative(-7.09f, -4.76f)
                reflectiveCurveToRelative(-0.02f, -1.87f, -1.07f, -3.14f)
                curveToRelative(-3.26f, -3.94f, -15.91f, -18.49f, -15.91f, -18.49f)
                close()
                moveTo(71.54f, 93.78f)
                lineToRelative(7.09f, -8.54f)
                reflectiveCurveToRelative(2.52f, 0.19f, 4.37f, 0.19f)
                curveToRelative(1.84f, 0.0f, 5.05f, -0.39f, 5.05f, -0.39f)
                reflectiveCurveToRelative(-5.82f, 7.86f, -8.93f, 11.74f)
                curveToRelative(-3.11f, 3.88f, -7.18f, 8.74f, -7.18f, 8.74f)
                lineToRelative(-3.88f, -11.26f)
                lineToRelative(3.48f, -0.48f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF865B50)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(66.5f, 86.98f)
                curveToRelative(-2.9f, 0.26f, -10.48f, 5.63f, -10.48f, 5.63f)
                reflectiveCurveToRelative(0.39f, 9.12f, 0.1f, 14.27f)
                reflectiveCurveToRelative(-2.32f, 13.56f, -0.45f, 15.69f)
                curveToRelative(1.62f, 1.84f, 15.24f, 2.19f, 17.27f, 0.05f)
                curveToRelative(2.04f, -2.14f, 0.06f, -10.98f, 0.06f, -16.71f)
                reflectiveCurveToRelative(-0.58f, -16.31f, -0.58f, -16.31f)
                reflectiveCurveToRelative(-2.62f, -2.91f, -5.92f, -2.62f)
                close()
            }
        }
            .build()
        return _tree!!
    }

private var _tree: ImageVector? = null
