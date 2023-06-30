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

val Emoji.Bone: ImageVector
    get() {
        if (_bone != null) {
            return _bone!!
        }
        _bone = Builder(
            name = "Bone", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp, viewportWidth
            = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFFCEACC)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(122.0f, 81.14f)
                curveToRelative(-4.21f, -5.9f, -14.22f, -5.49f, -17.03f, -5.38f)
                lineToRelative(-0.37f, 0.01f)
                curveToRelative(-4.94f, 0.2f, -9.74f, -1.7f, -13.2f, -5.22f)
                lineToRelative(-34.1f, -34.1f)
                arcToRelative(17.495f, 17.495f, 0.0f, false, true, -4.99f, -13.29f)
                lineToRelative(0.02f, -0.37f)
                curveToRelative(0.16f, -2.8f, 0.35f, -6.57f, -0.05f, -9.52f)
                curveToRelative(-0.62f, -4.57f, -5.23f, -11.57f, -17.29f, -9.12f)
                curveToRelative(-6.22f, 1.26f, -8.57f, 3.72f, -9.04f, 9.56f)
                curveToRelative(-0.1f, 1.24f, 0.53f, 7.65f, -1.94f, 10.22f)
                curveToRelative(-1.22f, 1.27f, -8.11f, 0.62f, -9.35f, 0.7f)
                curveToRelative(-5.85f, 0.38f, -10.76f, 4.56f, -10.76f, 12.94f)
                curveToRelative(-0.01f, 9.07f, 5.68f, 14.25f, 8.95f, 14.45f)
                curveToRelative(2.97f, 0.18f, 6.71f, 0.33f, 9.52f, 0.22f)
                lineToRelative(8.59f, -0.43f)
                reflectiveCurveToRelative(1.15f, 1.14f, 4.61f, 4.66f)
                lineToRelative(34.48f, 35.08f)
                curveToRelative(3.47f, 3.53f, 5.28f, 8.36f, 4.99f, 13.29f)
                lineToRelative(-0.02f, 0.37f)
                curveToRelative(-0.16f, 2.8f, -1.72f, 13.28f, 5.07f, 17.12f)
                curveToRelative(0.94f, 0.53f, 5.6f, 2.84f, 12.27f, 1.53f)
                curveToRelative(6.23f, -1.23f, 8.57f, -3.72f, 9.04f, -9.56f)
                curveToRelative(0.1f, -1.24f, 0.11f, -8.87f, 2.65f, -11.36f)
                reflectiveCurveToRelative(8.43f, -1.52f, 9.67f, -1.6f)
                curveToRelative(5.85f, -0.37f, 9.32f, -1.99f, 10.49f, -8.23f)
                curveToRelative(1.03f, -5.5f, -1.58f, -11.09f, -2.21f, -11.97f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFDEBCA8)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(26.26f, 11.1f)
                curveToRelative(0.8f, -3.07f, 4.92f, -2.66f, 5.94f, -1.67f)
                curveToRelative(1.05f, 1.01f, 1.63f, 2.42f, 1.82f, 3.84f)
                curveToRelative(0.75f, 5.81f, -1.2f, 9.15f, -3.23f, 10.24f)
                curveToRelative(-2.06f, 1.09f, -3.43f, 1.28f, -6.48f, 1.59f)
                curveToRelative(-1.85f, 0.19f, -3.38f, -0.11f, -2.73f, -0.46f)
                curveToRelative(1.03f, -0.55f, 3.14f, -1.99f, 3.6f, -2.83f)
                curveToRelative(0.53f, -0.98f, 0.51f, -1.86f, 0.61f, -2.9f)
                curveToRelative(0.24f, -2.6f, -0.2f, -5.25f, 0.47f, -7.81f)
                close()
                moveTo(124.21f, 93.1f)
                curveToRelative(0.0f, -0.02f, 0.01f, -0.04f, 0.01f, -0.06f)
                curveToRelative(0.32f, -1.75f, -1.72f, -3.0f, -3.15f, -1.95f)
                curveToRelative(-0.33f, 0.24f, -0.7f, 0.48f, -1.09f, 0.7f)
                curveToRelative(-4.09f, 2.29f, -9.73f, 0.46f, -13.55f, 3.17f)
                curveToRelative(-6.79f, 4.82f, -2.82f, 17.85f, -7.43f, 20.95f)
                curveToRelative(-2.63f, 1.77f, -7.73f, 2.3f, -10.55f, 0.87f)
                curveToRelative(-4.55f, -2.3f, -5.24f, -5.19f, -5.48f, -8.35f)
                curveToRelative(-0.36f, -4.73f, -0.41f, -10.04f, -1.14f, -13.72f)
                curveToRelative(-0.99f, -4.98f, -7.27f, -11.72f, -7.27f, -11.72f)
                reflectiveCurveTo(48.8f, 54.75f, 39.12f, 45.96f)
                curveToRelative(-3.72f, -3.37f, -9.51f, -2.21f, -13.93f, -1.67f)
                curveToRelative(-5.17f, 0.62f, -8.6f, 0.19f, -11.63f, -2.4f)
                reflectiveCurveToRelative(-3.85f, -8.54f, -2.59f, -12.32f)
                reflectiveCurveToRelative(7.01f, -4.86f, 7.01f, -4.86f)
                curveToRelative(-2.02f, -0.09f, -2.7f, -0.12f, -3.33f, -0.08f)
                curveToRelative(-5.84f, 0.38f, -10.75f, 4.56f, -10.75f, 12.94f)
                curveToRelative(-0.01f, 9.07f, 5.42f, 13.76f, 8.6f, 14.55f)
                curveToRelative(3.58f, 0.9f, 12.96f, -0.04f, 12.96f, -0.04f)
                reflectiveCurveToRelative(5.07f, -0.7f, 7.15f, 1.38f)
                curveToRelative(0.71f, 0.71f, 1.68f, 1.7f, 2.96f, 3.01f)
                lineToRelative(34.48f, 35.08f)
                curveToRelative(3.47f, 3.53f, 5.28f, 8.36f, 4.99f, 13.29f)
                lineToRelative(-0.02f, 0.37f)
                curveToRelative(-0.16f, 2.8f, -1.72f, 13.28f, 5.07f, 17.12f)
                curveToRelative(0.94f, 0.53f, 5.6f, 2.84f, 12.27f, 1.53f)
                curveToRelative(6.23f, -1.23f, 8.57f, -3.72f, 9.04f, -9.56f)
                curveToRelative(0.1f, -1.24f, 0.11f, -8.87f, 2.65f, -11.36f)
                reflectiveCurveToRelative(8.43f, -1.52f, 9.67f, -1.6f)
                curveToRelative(5.85f, -0.38f, 9.33f, -2.0f, 10.49f, -8.24f)
                close()
            }
        }
            .build()
        return _bone!!
    }

private var _bone: ImageVector? = null
