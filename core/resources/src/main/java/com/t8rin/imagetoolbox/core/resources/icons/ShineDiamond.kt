package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.ShineDiamond: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.ShineDiamond",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(480f, 880f)
            lineTo(120f, 524f)
            lineToRelative(200f, -244f)
            horizontalLineToRelative(320f)
            lineToRelative(200f, 244f)
            lineTo(480f, 880f)
            close()
            moveTo(183f, 280f)
            lineToRelative(-85f, -85f)
            lineToRelative(57f, -56f)
            lineToRelative(85f, 85f)
            lineToRelative(-57f, 56f)
            close()
            moveTo(440f, 200f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(775f, 280f)
            lineTo(718f, 223f)
            lineTo(803f, 138f)
            lineTo(860f, 195f)
            lineTo(775f, 280f)
            close()
            moveTo(480f, 768f)
            lineToRelative(210f, -208f)
            lineTo(270f, 560f)
            lineToRelative(210f, 208f)
            close()
            moveTo(358f, 360f)
            lineToRelative(-99f, 120f)
            horizontalLineToRelative(442f)
            lineToRelative(-99f, -120f)
            lineTo(358f, 360f)
            close()
        }
    }.build()
}
