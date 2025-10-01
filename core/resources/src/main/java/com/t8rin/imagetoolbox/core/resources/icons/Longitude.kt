package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.Longitude: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Longitude",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 2f)
            arcTo(10f, 10f, 0f, isMoreThanHalf = true, isPositiveArc = false, 22f, 12f)
            arcTo(10.03f, 10.03f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12f, 2f)
            moveTo(9.4f, 19.6f)
            arcTo(8.05f, 8.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.4f, 4.4f)
            arcTo(16.45f, 16.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.5f, 12f)
            arcTo(16.45f, 16.45f, 0f, isMoreThanHalf = false, isPositiveArc = false, 9.4f, 19.6f)
            moveTo(12f, 20f)
            arcTo(13.81f, 13.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9.5f, 12f)
            arcTo(13.81f, 13.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 4f)
            arcTo(13.81f, 13.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14.5f, 12f)
            arcTo(13.81f, 13.81f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 20f)
            moveTo(14.6f, 19.6f)
            arcTo(16.15f, 16.15f, 0f, isMoreThanHalf = false, isPositiveArc = false, 14.6f, 4.4f)
            arcTo(8.03f, 8.03f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 12f)
            arcTo(7.9f, 7.9f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14.6f, 19.6f)
            close()
        }
    }.build()
}
