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

public val Emoji.Locked: ImageVector
    get() {
        if (_locked != null) {
            return _locked!!
        }
        _locked = Builder(
            name = "Locked", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFE2A610)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(103.65f, 62.7f)
                horizontalLineToRelative(-0.01f)
                curveToRelative(-0.12f, -2.45f, -2.72f, -4.74f, -7.21f, -6.09f)
                curveToRelative(-9.61f, -2.89f, -20.27f, -4.58f, -32.43f, -4.68f)
                curveToRelative(-12.15f, 0.1f, -22.81f, 1.79f, -32.43f, 4.68f)
                curveToRelative(-4.49f, 1.35f, -7.07f, 3.64f, -7.2f, 6.09f)
                horizontalLineToRelative(-0.02f)
                verticalLineToRelative(50.08f)
                curveToRelative(-0.11f, 2.8f, 3.02f, 5.8f, 10.26f, 7.85f)
                curveToRelative(7.48f, 2.12f, 17.6f, 3.49f, 29.38f, 3.49f)
                reflectiveCurveToRelative(21.9f, -1.37f, 29.38f, -3.49f)
                curveToRelative(7.59f, -2.15f, 10.66f, -5.34f, 10.22f, -8.25f)
                horizontalLineToRelative(0.04f)
                lineTo(103.63f, 62.7f)
                close()
                moveTo(24.35f, 63.43f)
                horizontalLineToRelative(79.3f)
            }
            path(
                fill = SolidColor(Color(0xFF4E342E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveToRelative(71.9f, 108.24f)
                lineToRelative(-4.49f, -10.58f)
                arcToRelative(7.662f, 7.662f, 0.0f, false, false, 4.33f, -6.9f)
                curveToRelative(0.0f, -4.24f, -3.44f, -7.67f, -7.67f, -7.67f)
                reflectiveCurveToRelative(-7.67f, 3.44f, -7.67f, 7.67f)
                curveToRelative(0.0f, 2.85f, 1.56f, 5.34f, 3.87f, 6.66f)
                lineToRelative(-4.18f, 10.89f)
                curveToRelative(-0.53f, 1.38f, 0.49f, 2.85f, 1.96f, 2.85f)
                horizontalLineToRelative(11.92f)
                curveToRelative(1.5f, 0.0f, 2.51f, -1.54f, 1.93f, -2.92f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF9E740B)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(71.47f, 107.26f)
                lineTo(56.48f, 107.26f)
                lineToRelative(-0.4f, 1.05f)
                curveToRelative(-0.53f, 1.38f, 0.49f, 2.85f, 1.96f, 2.85f)
                horizontalLineToRelative(11.92f)
                curveToRelative(1.51f, 0.0f, 2.52f, -1.54f, 1.93f, -2.92f)
                lineToRelative(-0.42f, -0.98f)
                close()
                moveTo(60.95f, 95.4f)
                curveToRelative(0.18f, -0.52f, -0.01f, -1.08f, -0.45f, -1.4f)
                curveToRelative(-0.6f, -0.44f, -1.41f, -1.22f, -1.92f, -2.46f)
                curveToRelative(-1.86f, -4.48f, 0.35f, -6.47f, 0.35f, -6.47f)
                arcToRelative(7.622f, 7.622f, 0.0f, false, false, -2.54f, 5.69f)
                curveToRelative(0.0f, 2.85f, 1.56f, 5.34f, 3.87f, 6.66f)
                lineToRelative(0.69f, -2.02f)
                close()
                moveTo(69.46f, 85.3f)
                reflectiveCurveToRelative(2.2f, 4.42f, -0.32f, 7.25f)
                curveToRelative(-0.86f, 0.96f, -1.59f, 1.47f, -2.13f, 1.72f)
                curveToRelative(-0.53f, 0.25f, -0.77f, 0.88f, -0.52f, 1.41f)
                lineToRelative(0.93f, 1.96f)
                arcToRelative(7.662f, 7.662f, 0.0f, false, false, 4.33f, -6.9f)
                curveToRelative(-0.02f, -3.25f, -2.29f, -5.44f, -2.29f, -5.44f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFDD835)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(94.74f, 55.95f)
                curveTo(85.63f, 53.3f, 75.52f, 51.76f, 64.0f, 51.66f)
                curveToRelative(-11.52f, 0.09f, -21.63f, 1.64f, -30.74f, 4.28f)
                curveToRelative(-9.13f, 2.65f, -10.04f, 9.26f, 2.89f, 12.8f)
                curveToRelative(7.09f, 1.94f, 16.69f, 3.19f, 27.85f, 3.19f)
                reflectiveCurveToRelative(20.76f, -1.25f, 27.85f, -3.19f)
                curveToRelative(12.93f, -3.53f, 12.02f, -10.15f, 2.89f, -12.79f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF84B0C1)), stroke = SolidColor(Color(0xFF84B0C1)),
                strokeLineWidth = 1.958f, strokeLineCap = Butt, strokeLineJoin = Miter,
                strokeLineMiter = 4.0f, pathFillType = NonZero
            ) {
                moveTo(92.15f, 62.21f)
                reflectiveCurveToRelative(-0.37f, 1.72f, -5.22f, 1.72f)
                reflectiveCurveToRelative(-5.88f, -1.72f, -5.88f, -1.72f)
                verticalLineTo(40.16f)
                curveTo(81.05f, 30.14f, 73.4f, 22.0f, 63.99f, 22.0f)
                reflectiveCurveToRelative(-17.06f, 8.15f, -17.06f, 18.16f)
                verticalLineToRelative(22.06f)
                reflectiveCurveToRelative(-2.11f, 1.72f, -5.95f, 1.72f)
                reflectiveCurveToRelative(-5.14f, -1.72f, -5.14f, -1.72f)
                verticalLineTo(40.16f)
                curveToRelative(0.0f, -16.14f, 12.63f, -29.26f, 28.15f, -29.26f)
                reflectiveCurveToRelative(28.15f, 13.13f, 28.15f, 29.26f)
                verticalLineToRelative(22.05f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFB9E4EA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(51.31f, 21.56f)
                curveToRelative(-5.14f, 3.06f, -5.93f, 5.13f, -7.36f, 6.08f)
                curveToRelative(-1.06f, 0.7f, -2.47f, 0.14f, -1.55f, -2.29f)
                curveToRelative(0.79f, -2.08f, 2.69f, -6.02f, 7.43f, -9.05f)
                curveToRelative(8.16f, -5.22f, 15.2f, -4.98f, 14.82f, -0.99f)
                curveToRelative(-0.32f, 3.53f, -7.86f, 2.99f, -13.34f, 6.25f)
                close()
            }
        }
            .build()
        return _locked!!
    }

private var _locked: ImageVector? = null
