package ru.tech.imageresizershrinker.coreui.icons.material

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

val Icons.Rounded.Ton: ImageVector by lazy {
    Builder(
        name = "Ton", defaultWidth = 56.0.dp, defaultHeight = 56.0.dp, viewportWidth
        = 56.0f, viewportHeight = 56.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFFffffff)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(37.5603f, 15.6277f)
            horizontalLineTo(18.4386f)
            curveTo(14.9228f, 15.6277f, 12.6944f, 19.4202f, 14.4632f, 22.4861f)
            lineTo(26.2644f, 42.9409f)
            curveTo(27.0345f, 44.2765f, 28.9644f, 44.2765f, 29.7345f, 42.9409f)
            lineTo(41.5381f, 22.4861f)
            curveTo(43.3045f, 19.4251f, 41.0761f, 15.6277f, 37.5627f, 15.6277f)
            horizontalLineTo(37.5603f)
            close()
            moveTo(26.2548f, 36.8068f)
            lineTo(23.6847f, 31.8327f)
            lineTo(17.4833f, 20.7414f)
            curveTo(17.0742f, 20.0315f, 17.5795f, 19.1218f, 18.4362f, 19.1218f)
            horizontalLineTo(26.2524f)
            verticalLineTo(36.8092f)
            lineTo(26.2548f, 36.8068f)
            close()
            moveTo(38.5108f, 20.739f)
            lineTo(32.3118f, 31.8351f)
            lineTo(29.7417f, 36.8068f)
            verticalLineTo(19.1194f)
            horizontalLineTo(37.5579f)
            curveTo(38.4146f, 19.1194f, 38.9199f, 20.0291f, 38.5108f, 20.739f)
            close()
        }
    }.build()
}