package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Speed: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Speed",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(418f, 620f)
            quadTo(443f, 645f, 481f, 643.5f)
            quadTo(519f, 642f, 536f, 616f)
            lineTo(705f, 363f)
            quadTo(714f, 349f, 702.5f, 337.5f)
            quadTo(691f, 326f, 677f, 335f)
            lineTo(424f, 504f)
            quadTo(398f, 522f, 395.5f, 558.5f)
            quadTo(393f, 595f, 418f, 620f)
            close()
            moveTo(204f, 800f)
            quadTo(182f, 800f, 163.5f, 790.5f)
            quadTo(145f, 781f, 134f, 762f)
            quadTo(108f, 715f, 94f, 664.5f)
            quadTo(80f, 614f, 80f, 560f)
            quadTo(80f, 477f, 111.5f, 404f)
            quadTo(143f, 331f, 197f, 277f)
            quadTo(251f, 223f, 324f, 191.5f)
            quadTo(397f, 160f, 480f, 160f)
            quadTo(562f, 160f, 634f, 191f)
            quadTo(706f, 222f, 760f, 275.5f)
            quadTo(814f, 329f, 846f, 400.5f)
            quadTo(878f, 472f, 879f, 554f)
            quadTo(880f, 609f, 866.5f, 661.5f)
            quadTo(853f, 714f, 825f, 762f)
            quadTo(814f, 781f, 795.5f, 790.5f)
            quadTo(777f, 800f, 755f, 800f)
            lineTo(204f, 800f)
            close()
        }
    }.build()
}
