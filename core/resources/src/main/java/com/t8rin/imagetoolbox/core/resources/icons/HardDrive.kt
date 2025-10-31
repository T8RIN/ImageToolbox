package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.HardDrive: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.HardDrive",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(680f, 640f)
            quadTo(705f, 640f, 722.5f, 623f)
            quadTo(740f, 606f, 740f, 580f)
            quadTo(740f, 555f, 722.5f, 537.5f)
            quadTo(705f, 520f, 680f, 520f)
            quadTo(654f, 520f, 637f, 537.5f)
            quadTo(620f, 555f, 620f, 580f)
            quadTo(620f, 606f, 637f, 623f)
            quadTo(654f, 640f, 680f, 640f)
            close()
            moveTo(80f, 360f)
            lineTo(216f, 224f)
            quadTo(227f, 213f, 241.5f, 206.5f)
            quadTo(256f, 200f, 273f, 200f)
            lineTo(686f, 200f)
            quadTo(703f, 200f, 717.5f, 206.5f)
            quadTo(732f, 213f, 743f, 224f)
            lineTo(880f, 360f)
            lineTo(80f, 360f)
            close()
            moveTo(160f, 760f)
            quadTo(126f, 760f, 103f, 737f)
            quadTo(80f, 714f, 80f, 680f)
            lineTo(80f, 440f)
            lineTo(880f, 440f)
            lineTo(880f, 680f)
            quadTo(880f, 714f, 856.5f, 737f)
            quadTo(833f, 760f, 800f, 760f)
            lineTo(160f, 760f)
            close()
        }
    }.build()
}
