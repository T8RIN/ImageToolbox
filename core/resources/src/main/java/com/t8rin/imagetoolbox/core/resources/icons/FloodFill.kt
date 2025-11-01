package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.FloodFill: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.FloodFill",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(346f, 820f)
            lineTo(100f, 574f)
            quadTo(90f, 564f, 85f, 552f)
            quadTo(80f, 540f, 80f, 527f)
            quadTo(80f, 514f, 85f, 502f)
            quadTo(90f, 490f, 100f, 480f)
            lineTo(330f, 251f)
            lineTo(224f, 145f)
            lineTo(286f, 80f)
            lineTo(686f, 480f)
            quadTo(696f, 490f, 700.5f, 502f)
            quadTo(705f, 514f, 705f, 527f)
            quadTo(705f, 540f, 700.5f, 552f)
            quadTo(696f, 564f, 686f, 574f)
            lineTo(440f, 820f)
            quadTo(430f, 830f, 418f, 835f)
            quadTo(406f, 840f, 393f, 840f)
            quadTo(380f, 840f, 368f, 835f)
            quadTo(356f, 830f, 346f, 820f)
            close()
            moveTo(393f, 314f)
            lineTo(179f, 528f)
            quadTo(179f, 528f, 179f, 528f)
            quadTo(179f, 528f, 179f, 528f)
            lineTo(607f, 528f)
            quadTo(607f, 528f, 607f, 528f)
            quadTo(607f, 528f, 607f, 528f)
            lineTo(393f, 314f)
            close()
            moveTo(792f, 840f)
            quadTo(756f, 840f, 731f, 814.5f)
            quadTo(706f, 789f, 706f, 752f)
            quadTo(706f, 725f, 719.5f, 701f)
            quadTo(733f, 677f, 750f, 654f)
            lineTo(792f, 600f)
            lineTo(836f, 654f)
            quadTo(852f, 677f, 866f, 701f)
            quadTo(880f, 725f, 880f, 752f)
            quadTo(880f, 789f, 854f, 814.5f)
            quadTo(828f, 840f, 792f, 840f)
            close()
        }
    }.build()
}
