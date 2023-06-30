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

val Emoji.Cloud: ImageVector
    get() {
        if (_cloud != null) {
            return _cloud!!
        }
        _cloud = Builder(
            name = "Cloud", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE4EAEE)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(23.45f, 62.3f)
                curveToRelative(0.72f, -0.72f, -1.27f, -9.29f, 7.6f, -15.91f)
                reflectiveCurveToRelative(14.92f, -2.67f, 15.77f, -2.96f)
                curveToRelative(0.84f, -0.28f, 4.79f, -17.6f, 21.4f, -22.1f)
                reflectiveCurveToRelative(33.93f, 3.94f, 38.01f, 18.02f)
                curveToRelative(3.73f, 12.87f, 0.84f, 21.54f, 1.27f, 22.1f)
                curveToRelative(0.42f, 0.56f, 8.45f, 0.28f, 13.09f, 7.74f)
                reflectiveCurveToRelative(2.96f, 12.11f, 2.96f, 12.11f)
                lineToRelative(-29.56f, 9.15f)
                horizontalLineToRelative(-47.3f)
                reflectiveCurveTo(5.02f, 79.47f, 4.6f, 77.5f)
                curveToRelative(-0.42f, -1.97f, 0.53f, -8.37f, 7.32f, -12.25f)
                curveToRelative(5.9f, -3.37f, 10.26f, -1.68f, 11.53f, -2.95f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFBACDD2)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(35.16f, 92.84f)
                reflectiveCurveToRelative(-15.78f, 3.3f, -26.45f, -4.96f)
                curveTo(2.29f, 82.9f, 4.63f, 74.83f, 4.63f, 74.83f)
                reflectiveCurveToRelative(4.6f, 4.65f, 13.89f, 5.91f)
                curveToRelative(9.29f, 1.27f, 19.71f, 0.84f, 19.71f, 0.84f)
                reflectiveCurveToRelative(2.6f, 4.44f, 12.39f, 6.48f)
                curveToRelative(12.27f, 2.55f, 18.74f, -3.73f, 18.74f, -3.73f)
                reflectiveCurveToRelative(3.36f, 4.02f, 15.19f, 4.3f)
                curveToRelative(11.83f, 0.28f, 18.46f, -7.98f, 19.57f, -8.17f)
                curveToRelative(0.56f, -0.09f, 3.82f, 2.87f, 10.28f, 1.83f)
                curveToRelative(6.15f, -0.99f, 9.39f, -3.66f, 9.39f, -3.66f)
                reflectiveCurveToRelative(0.89f, 6.62f, -5.3f, 10.7f)
                curveToRelative(-4.83f, 3.18f, -13.23f, 3.52f, -13.23f, 3.52f)
                reflectiveCurveToRelative(-1.28f, 4.91f, -7.05f, 8.48f)
                curveToRelative(-5.36f, 3.33f, -14.6f, 4.44f, -21.44f, 2.4f)
                curveToRelative(-8.59f, -2.56f, -10.72f, -6.47f, -10.72f, -6.47f)
                reflectiveCurveToRelative(-6.4f, 3.75f, -16.4f, 2.48f)
                curveToRelative(-9.45f, -1.18f, -14.49f, -6.9f, -14.49f, -6.9f)
                close()
            }
        }
            .build()
        return _cloud!!
    }

private var _cloud: ImageVector? = null
