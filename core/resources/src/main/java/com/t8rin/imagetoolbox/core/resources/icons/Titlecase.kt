package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Titlecase: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Titlecase",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(344f, 710f)
            lineTo(344f, 344f)
            lineTo(224f, 344f)
            lineTo(224f, 280f)
            lineTo(532f, 280f)
            lineTo(532f, 344f)
            lineTo(412f, 344f)
            lineTo(412f, 710f)
            lineTo(344f, 710f)
            close()
            moveTo(688f, 720f)
            quadTo(644f, 720f, 619f, 694.5f)
            quadTo(594f, 669f, 594f, 624f)
            lineTo(594f, 462f)
            lineTo(540f, 462f)
            lineTo(540f, 404f)
            lineTo(594f, 404f)
            lineTo(594f, 317f)
            lineTo(660f, 317f)
            lineTo(660f, 404f)
            lineTo(734f, 404f)
            lineTo(734f, 462f)
            lineTo(660f, 462f)
            lineTo(660f, 610f)
            quadTo(660f, 633f, 670.5f, 646f)
            quadTo(681f, 659f, 699f, 659f)
            quadTo(708f, 659f, 717.5f, 655.5f)
            quadTo(727f, 652f, 736f, 646f)
            lineTo(736f, 711f)
            quadTo(726f, 716f, 714f, 718f)
            quadTo(702f, 720f, 688f, 720f)
            close()
        }
    }.build()
}
