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

val Emoji.Heart: ImageVector
    get() {
        if (_heart != null) {
            return _heart!!
        }
        _heart = Builder(
            name = "Heart", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFFF44336)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(93.99f, 8.97f)
                curveToRelative(-21.91f, 0.0f, -29.96f, 22.39f, -29.96f, 22.39f)
                reflectiveCurveToRelative(-7.94f, -22.39f, -30.0f, -22.39f)
                curveToRelative(-16.58f, 0.0f, -35.48f, 13.14f, -28.5f, 43.01f)
                curveToRelative(6.98f, 29.87f, 58.56f, 67.08f, 58.56f, 67.08f)
                reflectiveCurveToRelative(51.39f, -37.21f, 58.38f, -67.08f)
                curveToRelative(6.98f, -29.87f, -10.56f, -43.01f, -28.48f, -43.01f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFA73229)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(30.65f, 11.2f)
                curveToRelative(17.2f, 0.0f, 25.74f, 18.49f, 28.5f, 25.98f)
                curveToRelative(0.39f, 1.07f, 1.88f, 1.1f, 2.33f, 0.06f)
                lineTo(64.0f, 31.35f)
                curveTo(60.45f, 20.01f, 50.69f, 8.97f, 34.03f, 8.97f)
                curveToRelative(-6.9f, 0.0f, -14.19f, 2.28f, -19.86f, 7.09f)
                curveToRelative(5.01f, -3.29f, 10.88f, -4.86f, 16.48f, -4.86f)
                close()
                moveTo(93.99f, 8.97f)
                curveToRelative(-5.29f, 0.0f, -10.11f, 1.15f, -13.87f, 3.47f)
                curveToRelative(2.64f, -1.02f, 5.91f, -1.24f, 9.15f, -1.24f)
                curveToRelative(16.21f, 0.0f, 30.72f, 12.29f, 24.17f, 40.7f)
                curveToRelative(-5.62f, 24.39f, -38.46f, 53.98f, -48.49f, 65.27f)
                curveToRelative(-0.64f, 0.72f, -0.86f, 1.88f, -0.86f, 1.88f)
                reflectiveCurveToRelative(51.39f, -37.21f, 58.38f, -67.08f)
                curveToRelative(6.98f, -29.86f, -10.53f, -43.0f, -28.48f, -43.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF8A80)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(17.04f, 24.82f)
                curveToRelative(3.75f, -4.68f, 10.45f, -8.55f, 16.13f, -4.09f)
                curveToRelative(3.07f, 2.41f, 1.73f, 7.35f, -1.02f, 9.43f)
                curveToRelative(-4.0f, 3.04f, -7.48f, 4.87f, -9.92f, 9.63f)
                curveToRelative(-1.46f, 2.86f, -2.34f, 5.99f, -2.79f, 9.18f)
                curveToRelative(-0.18f, 1.26f, -1.83f, 1.57f, -2.45f, 0.46f)
                curveToRelative(-4.22f, -7.48f, -5.42f, -17.78f, 0.05f, -24.61f)
                close()
                moveTo(77.16f, 34.66f)
                curveToRelative(-1.76f, 0.0f, -3.0f, -1.7f, -2.36f, -3.34f)
                curveToRelative(1.19f, -3.02f, 2.73f, -5.94f, 4.58f, -8.54f)
                curveToRelative(2.74f, -3.84f, 7.95f, -6.08f, 11.25f, -3.75f)
                curveToRelative(3.38f, 2.38f, 2.94f, 7.14f, 0.57f, 9.44f)
                curveToRelative(-5.09f, 4.93f, -11.51f, 6.19f, -14.04f, 6.19f)
                close()
            }
        }
            .build()
        return _heart!!
    }

private var _heart: ImageVector? = null
