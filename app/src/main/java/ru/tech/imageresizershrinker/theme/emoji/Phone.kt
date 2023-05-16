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

val Emoji.Phone: ImageVector
    get() {
        if (_phone != null) {
            return _phone!!
        }
        _phone = Builder(
            name = "Phone", defaultWidth = 1.0.dp, defaultHeight = 1.0.dp,
            viewportWidth = 128.0f, viewportHeight = 128.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF424242)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(87.4f, 124.0f)
                horizontalLineTo(40.6f)
                curveToRelative(-4.7f, 0.0f, -8.6f, -3.8f, -8.6f, -8.6f)
                verticalLineTo(12.6f)
                curveTo(32.0f, 7.9f, 35.9f, 4.0f, 40.6f, 4.0f)
                horizontalLineToRelative(46.8f)
                curveToRelative(4.7f, 0.0f, 8.6f, 3.8f, 8.6f, 8.6f)
                verticalLineToRelative(102.9f)
                curveToRelative(0.0f, 4.7f, -3.9f, 8.5f, -8.6f, 8.5f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(86.77f, 120.11f)
                horizontalLineTo(39.93f)
                curveToRelative(-2.15f, 0.0f, -3.89f, -1.74f, -3.89f, -3.89f)
                verticalLineTo(19.65f)
                curveToRelative(0.0f, -2.14f, 1.74f, -3.88f, 3.88f, -3.88f)
                horizontalLineToRelative(48.15f)
                curveToRelative(2.15f, 0.0f, 3.89f, 1.74f, 3.89f, 3.89f)
                verticalLineToRelative(95.93f)
                curveToRelative(0.0f, 0.93f, -1.4f, 4.52f, -5.19f, 4.52f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF212121)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.65f, 11.59f)
                horizontalLineToRelative(-19.2f)
                curveToRelative(-0.7f, 0.0f, -1.2f, -0.6f, -1.2f, -1.2f)
                reflectiveCurveToRelative(0.6f, -1.2f, 1.2f, -1.2f)
                horizontalLineToRelative(19.1f)
                curveToRelative(0.7f, 0.0f, 1.2f, 0.6f, 1.2f, 1.2f)
                reflectiveCurveToRelative(-0.5f, 1.2f, -1.1f, 1.2f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF757575)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(87.4f, 6.0f)
                curveToRelative(3.64f, 0.0f, 6.6f, 2.96f, 6.6f, 6.6f)
                verticalLineToRelative(102.9f)
                curveToRelative(0.0f, 3.58f, -2.96f, 6.5f, -6.6f, 6.5f)
                horizontalLineTo(40.6f)
                curveToRelative(-3.64f, 0.0f, -6.6f, -2.96f, -6.6f, -6.6f)
                verticalLineTo(12.6f)
                curveTo(34.0f, 9.02f, 37.02f, 6.0f, 40.6f, 6.0f)
                horizontalLineToRelative(46.8f)
                moveToRelative(0.0f, -2.0f)
                horizontalLineTo(40.6f)
                curveTo(35.9f, 4.0f, 32.0f, 7.9f, 32.0f, 12.6f)
                verticalLineToRelative(102.8f)
                curveToRelative(0.0f, 4.8f, 3.9f, 8.6f, 8.6f, 8.6f)
                horizontalLineToRelative(46.8f)
                curveToRelative(4.7f, 0.0f, 8.6f, -3.8f, 8.6f, -8.5f)
                verticalLineTo(12.6f)
                curveTo(96.0f, 7.8f, 92.1f, 4.0f, 87.4f, 4.0f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEAB56E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.35f, 29.96f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 29.96f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF80AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.45f, 29.96f)
                horizontalLineTo(67.1f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.33f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0288D1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(86.01f, 29.96f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.35f, 44.12f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 44.12f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.45f, 44.12f)
                horizontalLineTo(67.1f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.33f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFEAB56E)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(86.01f, 44.12f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFB8C00)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(48.35f, 58.28f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineTo(50.6f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF80AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 58.28f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineTo(50.6f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.45f, 58.28f)
                horizontalLineTo(67.1f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineTo(50.6f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.33f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0288D1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(86.01f, 58.28f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineTo(50.6f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
                moveTo(48.35f, 72.44f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF81D4FA)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 72.44f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.73f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
                moveTo(48.35f, 114.92f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.72f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF0288D1)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(60.9f, 114.92f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.72f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFF00BFA5)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(73.45f, 114.92f)
                horizontalLineTo(67.1f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.72f, -0.59f, 1.32f, -1.33f, 1.32f)
                close()
            }
            path(
                fill = SolidColor(Color(0xFFFF80AB)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = NonZero
            ) {
                moveTo(86.01f, 114.92f)
                horizontalLineToRelative(-6.36f)
                curveToRelative(-0.73f, 0.0f, -1.32f, -0.59f, -1.32f, -1.32f)
                verticalLineToRelative(-6.36f)
                curveToRelative(0.0f, -0.73f, 0.59f, -1.32f, 1.32f, -1.32f)
                horizontalLineToRelative(6.36f)
                curveToRelative(0.73f, 0.0f, 1.32f, 0.59f, 1.32f, 1.32f)
                verticalLineToRelative(6.36f)
                curveToRelative(0.0f, 0.72f, -0.59f, 1.32f, -1.32f, 1.32f)
                close()
            }
        }
            .build()
        return _phone!!
    }

private var _phone: ImageVector? = null
