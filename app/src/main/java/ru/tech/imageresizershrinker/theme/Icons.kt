package ru.tech.imageresizershrinker.theme

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Github: ImageVector
    get() {
        if (_github != null) {
            return _github!!
        }
        _github = Builder(
            name = "Github", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(12.0f, 2.0f)
                arcTo(10.0f, 10.0f, 0.0f, false, false, 2.0f, 12.0f)
                curveTo(2.0f, 16.42f, 4.87f, 20.17f, 8.84f, 21.5f)
                curveTo(9.34f, 21.58f, 9.5f, 21.27f, 9.5f, 21.0f)
                curveTo(9.5f, 20.77f, 9.5f, 20.14f, 9.5f, 19.31f)
                curveTo(6.73f, 19.91f, 6.14f, 17.97f, 6.14f, 17.97f)
                curveTo(5.68f, 16.81f, 5.03f, 16.5f, 5.03f, 16.5f)
                curveTo(4.12f, 15.88f, 5.1f, 15.9f, 5.1f, 15.9f)
                curveTo(6.1f, 15.97f, 6.63f, 16.93f, 6.63f, 16.93f)
                curveTo(7.5f, 18.45f, 8.97f, 18.0f, 9.54f, 17.76f)
                curveTo(9.63f, 17.11f, 9.89f, 16.67f, 10.17f, 16.42f)
                curveTo(7.95f, 16.17f, 5.62f, 15.31f, 5.62f, 11.5f)
                curveTo(5.62f, 10.39f, 6.0f, 9.5f, 6.65f, 8.79f)
                curveTo(6.55f, 8.54f, 6.2f, 7.5f, 6.75f, 6.15f)
                curveTo(6.75f, 6.15f, 7.59f, 5.88f, 9.5f, 7.17f)
                curveTo(10.29f, 6.95f, 11.15f, 6.84f, 12.0f, 6.84f)
                curveTo(12.85f, 6.84f, 13.71f, 6.95f, 14.5f, 7.17f)
                curveTo(16.41f, 5.88f, 17.25f, 6.15f, 17.25f, 6.15f)
                curveTo(17.8f, 7.5f, 17.45f, 8.54f, 17.35f, 8.79f)
                curveTo(18.0f, 9.5f, 18.38f, 10.39f, 18.38f, 11.5f)
                curveTo(18.38f, 15.32f, 16.04f, 16.16f, 13.81f, 16.41f)
                curveTo(14.17f, 16.72f, 14.5f, 17.33f, 14.5f, 18.26f)
                curveTo(14.5f, 19.6f, 14.5f, 20.68f, 14.5f, 21.0f)
                curveTo(14.5f, 21.27f, 14.66f, 21.59f, 15.17f, 21.5f)
                curveTo(19.14f, 20.16f, 22.0f, 16.42f, 22.0f, 12.0f)
                arcTo(10.0f, 10.0f, 0.0f, false, false, 12.0f, 2.0f)
                close()
            }
        }
            .build()
        return _github!!
    }

private var _github: ImageVector? = null

val Icons.Rounded.Telegram: ImageVector
    get() {
        if (_telegram != null) {
            return _telegram!!
        }
        _telegram = Builder(
            name = "Telegram", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(11.944f, 0.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, false, 0.0f, 12.0f)
                arcToRelative(12.0f, 12.0f, 0.0f, false, false, 12.0f, 12.0f)
                arcToRelative(12.0f, 12.0f, 0.0f, false, false, 12.0f, -12.0f)
                arcTo(12.0f, 12.0f, 0.0f, false, false, 12.0f, 0.0f)
                arcToRelative(12.0f, 12.0f, 0.0f, false, false, -0.056f, 0.0f)
                close()
                moveTo(16.906f, 7.224f)
                curveToRelative(0.1f, -0.002f, 0.321f, 0.023f, 0.465f, 0.14f)
                arcToRelative(0.506f, 0.506f, 0.0f, false, true, 0.171f, 0.325f)
                curveToRelative(0.016f, 0.093f, 0.036f, 0.306f, 0.02f, 0.472f)
                curveToRelative(-0.18f, 1.898f, -0.962f, 6.502f, -1.36f, 8.627f)
                curveToRelative(-0.168f, 0.9f, -0.499f, 1.201f, -0.82f, 1.23f)
                curveToRelative(-0.696f, 0.065f, -1.225f, -0.46f, -1.9f, -0.902f)
                curveToRelative(-1.056f, -0.693f, -1.653f, -1.124f, -2.678f, -1.8f)
                curveToRelative(-1.185f, -0.78f, -0.417f, -1.21f, 0.258f, -1.91f)
                curveToRelative(0.177f, -0.184f, 3.247f, -2.977f, 3.307f, -3.23f)
                curveToRelative(0.007f, -0.032f, 0.014f, -0.15f, -0.056f, -0.212f)
                reflectiveCurveToRelative(-0.174f, -0.041f, -0.249f, -0.024f)
                curveToRelative(-0.106f, 0.024f, -1.793f, 1.14f, -5.061f, 3.345f)
                curveToRelative(-0.48f, 0.33f, -0.913f, 0.49f, -1.302f, 0.48f)
                curveToRelative(-0.428f, -0.008f, -1.252f, -0.241f, -1.865f, -0.44f)
                curveToRelative(-0.752f, -0.245f, -1.349f, -0.374f, -1.297f, -0.789f)
                curveToRelative(0.027f, -0.216f, 0.325f, -0.437f, 0.893f, -0.663f)
                curveToRelative(3.498f, -1.524f, 5.83f, -2.529f, 6.998f, -3.014f)
                curveToRelative(3.332f, -1.386f, 4.025f, -1.627f, 4.476f, -1.635f)
                close()
            }
        }
            .build()
        return _telegram!!
    }

private var _telegram: ImageVector? = null

