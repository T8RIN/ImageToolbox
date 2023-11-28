package ru.tech.imageresizershrinker.presentation.root.icons.material

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

val Icons.Rounded.TopAppBar: ImageVector by lazy {
    Builder(
        name = "Top App Bar", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp, viewportWidth
        = 24.0f, viewportHeight = 24.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, fillAlpha = 0.5f, strokeAlpha
            = 0.5f, strokeLineWidth = 0.0f, strokeLineCap = Butt, strokeLineJoin = Miter,
            strokeLineMiter = 4.0f, pathFillType = NonZero
        ) {
            moveTo(17.0f, 20.8f)
            horizontalLineTo(7.0f)
            curveToRelative(-0.6f, 0.0f, -1.0f, -0.5f, -1.0f, -1.0f)
            verticalLineTo(7.7f)
            curveToRelative(0.0f, -0.6f, 0.5f, -1.0f, 1.0f, -1.0f)
            horizontalLineToRelative(10.0f)
            curveToRelative(0.6f, 0.0f, 1.0f, 0.5f, 1.0f, 1.0f)
            verticalLineToRelative(12.1f)
            curveTo(18.0f, 20.4f, 17.5f, 20.8f, 17.0f, 20.8f)
            close()
        }
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(17.0f, 3.2f)
            horizontalLineTo(7.0f)
            curveToRelative(-0.5f, 0.0f, -1.0f, 0.4f, -1.0f, 1.0f)
            verticalLineTo(8.0f)
            curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            horizontalLineTo(18.0f)
            curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
            verticalLineTo(4.2f)
            curveTo(18.0f, 3.6f, 17.6f, 3.2f, 17.0f, 3.2f)
            close()
            moveTo(16.2f, 5.7f)
            curveToRelative(-0.4f, 0.0f, -0.8f, -0.3f, -0.8f, -0.8f)
            curveToRelative(0.0f, -0.4f, 0.3f, -0.8f, 0.8f, -0.8f)
            curveToRelative(0.4f, 0.0f, 0.8f, 0.3f, 0.8f, 0.8f)
            curveTo(16.9f, 5.4f, 16.6f, 5.7f, 16.2f, 5.7f)
            close()
        }
    }.build()
}