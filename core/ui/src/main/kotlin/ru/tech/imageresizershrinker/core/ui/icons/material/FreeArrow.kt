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

val Icons.Rounded.FreeArrow: ImageVector by lazy {
    Builder(
        name = "FreeArrow", defaultWidth = 24.0.dp, defaultHeight = 24.0.dp,
        viewportWidth = 960.0f, viewportHeight = 960.0f
    ).apply {
        path(
            fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
            strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
            pathFillType = NonZero
        ) {
            moveTo(800.0f, 377.0f)
            lineTo(621.0f, 555.0f)
            quadToRelative(-35.0f, 35.0f, -85.0f, 35.0f)
            reflectiveQuadToRelative(-85.0f, -35.0f)
            lineToRelative(-47.0f, -47.0f)
            quadToRelative(-11.0f, -11.0f, -28.0f, -11.0f)
            reflectiveQuadToRelative(-28.0f, 11.0f)
            lineTo(164.0f, 692.0f)
            quadToRelative(-11.0f, 11.0f, -28.0f, 11.0f)
            reflectiveQuadToRelative(-28.0f, -11.0f)
            quadToRelative(-11.0f, -11.0f, -11.0f, -28.0f)
            reflectiveQuadToRelative(11.0f, -28.0f)
            lineToRelative(184.0f, -184.0f)
            quadToRelative(35.0f, -35.0f, 85.0f, -35.0f)
            reflectiveQuadToRelative(85.0f, 35.0f)
            lineToRelative(46.0f, 46.0f)
            quadToRelative(12.0f, 12.0f, 28.5f, 12.0f)
            reflectiveQuadToRelative(28.5f, -12.0f)
            lineToRelative(178.0f, -178.0f)
            horizontalLineToRelative(-63.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(640.0f, 280.0f)
            quadToRelative(0.0f, -17.0f, 11.5f, -28.5f)
            reflectiveQuadTo(680.0f, 240.0f)
            horizontalLineToRelative(160.0f)
            quadToRelative(17.0f, 0.0f, 28.5f, 11.5f)
            reflectiveQuadTo(880.0f, 280.0f)
            verticalLineToRelative(160.0f)
            quadToRelative(0.0f, 17.0f, -11.5f, 28.5f)
            reflectiveQuadTo(840.0f, 480.0f)
            quadToRelative(-17.0f, 0.0f, -28.5f, -11.5f)
            reflectiveQuadTo(800.0f, 440.0f)
            verticalLineToRelative(-63.0f)
            close()
        }
    }.build()
}