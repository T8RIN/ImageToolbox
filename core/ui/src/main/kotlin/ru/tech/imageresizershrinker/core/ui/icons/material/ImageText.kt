package ru.tech.imageresizershrinker.core.ui.icons.material

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

val Icons.Rounded.ImageTextAlt: ImageVector by lazy {
    Builder(
        name = "Image Text", defaultWidth = 24.0.dp, defaultHeight =
        24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 13.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(11.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(13.0f)
            moveTo(22.0f, 7.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(9.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(7.0f)
            moveTo(14.0f, 17.0f)
            horizontalLineTo(22.0f)
            verticalLineTo(15.0f)
            horizontalLineTo(14.0f)
            verticalLineTo(17.0f)
            moveTo(12.0f, 9.0f)
            verticalLineTo(15.0f)
            curveTo(12.0f, 16.1f, 11.1f, 17.0f, 10.0f, 17.0f)
            horizontalLineTo(4.0f)
            curveTo(2.9f, 17.0f, 2.0f, 16.1f, 2.0f, 15.0f)
            verticalLineTo(9.0f)
            curveTo(2.0f, 7.9f, 2.9f, 7.0f, 4.0f, 7.0f)
            horizontalLineTo(10.0f)
            curveTo(11.1f, 7.0f, 12.0f, 7.9f, 12.0f, 9.0f)
            moveTo(10.5f, 15.0f)
            lineTo(8.3f, 12.0f)
            lineTo(6.5f, 14.3f)
            lineTo(5.3f, 12.8f)
            lineTo(3.5f, 15.0f)
            horizontalLineTo(10.5f)
            close()
        }
    }.build()
}

val Icons.Outlined.ImageText: ImageVector by lazy {
    Builder(
        name = "ImageText", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 13.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 7.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(14.0f, 17.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(10.953f, 15.953f)
            horizontalLineTo(3.047f)
            verticalLineTo(8.047f)
            horizontalLineToRelative(7.906f)
            moveTo(10.953f, 6.9176f)
            horizontalLineTo(3.047f)
            curveToRelative(-0.6238f, 0.0f, -1.1294f, 0.5057f, -1.1294f, 1.1294f)
            verticalLineToRelative(7.906f)
            curveToRelative(0.0f, 0.6238f, 0.5057f, 1.1294f, 1.1294f, 1.1294f)
            horizontalLineToRelative(7.906f)
            curveToRelative(0.6238f, 0.0f, 1.1294f, -0.5057f, 1.1294f, -1.1294f)
            verticalLineTo(8.047f)
            curveTo(12.0824f, 7.4232f, 11.5768f, 6.9176f, 10.953f, 6.9176f)
            moveTo(8.1068f, 12.1638f)
            lineTo(6.5539f, 14.1629f)
            lineToRelative(-1.1068f, -1.3327f)
            lineToRelative(-1.553f, 1.9934f)
            horizontalLineToRelative(6.2119f)
            lineTo(8.1068f, 12.1638f)
            close()
        }
    }.build()
}

val Icons.Rounded.ImageText: ImageVector by lazy {
    Builder(
        name = "ImageTextRounded", defaultWidth = 24.0.dp, defaultHeight
        = 24.0.dp, viewportWidth = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 13.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(22.0f, 7.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(14.0f, 17.0f)
            lineToRelative(8.0f, 0.0f)
            lineToRelative(0.0f, -2.0f)
            lineToRelative(-8.0f, 0.0f)
            lineToRelative(0.0f, 2.0f)
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(10.953f, 6.9175f)
            horizontalLineTo(3.047f)
            curveToRelative(-0.6238f, 0.0f, -1.1295f, 0.5057f, -1.1295f, 1.1295f)
            verticalLineToRelative(7.906f)
            curveToRelative(0.0f, 0.6238f, 0.5057f, 1.1295f, 1.1295f, 1.1295f)
            horizontalLineToRelative(7.906f)
            curveToRelative(0.6238f, 0.0f, 1.1295f, -0.5057f, 1.1295f, -1.1295f)
            verticalLineTo(8.047f)
            curveTo(12.0825f, 7.4232f, 11.5768f, 6.9175f, 10.953f, 6.9175f)
            close()
            moveTo(3.894f, 14.8236f)
            lineToRelative(1.553f, -1.9935f)
            lineToRelative(1.1069f, 1.3327f)
            lineToRelative(1.553f, -1.9991f)
            lineToRelative(1.9991f, 2.6599f)
            horizontalLineTo(3.894f)
            close()
        }
    }.build()
}