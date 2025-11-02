package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.SquareFoot: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.SquareFoot",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(208f, 840f)
            quadTo(171f, 840f, 145.5f, 814.5f)
            quadTo(120f, 789f, 120f, 752f)
            lineTo(120f, 204f)
            quadTo(120f, 175f, 147f, 163.5f)
            quadTo(174f, 152f, 194f, 172f)
            lineTo(284f, 262f)
            lineTo(230f, 316f)
            lineTo(258f, 344f)
            lineTo(312f, 290f)
            lineTo(416f, 394f)
            lineTo(362f, 448f)
            lineTo(390f, 476f)
            lineTo(444f, 422f)
            lineTo(548f, 526f)
            lineTo(494f, 580f)
            lineTo(522f, 608f)
            lineTo(576f, 554f)
            lineTo(680f, 658f)
            lineTo(626f, 712f)
            lineTo(654f, 740f)
            lineTo(708f, 686f)
            lineTo(788f, 766f)
            quadTo(808f, 786f, 796.5f, 813f)
            quadTo(785f, 840f, 756f, 840f)
            lineTo(208f, 840f)
            close()
            moveTo(240f, 720f)
            lineTo(572f, 720f)
            lineTo(240f, 388f)
            lineTo(240f, 720f)
            quadTo(240f, 720f, 240f, 720f)
            quadTo(240f, 720f, 240f, 720f)
            close()
        }
    }.build()
}
