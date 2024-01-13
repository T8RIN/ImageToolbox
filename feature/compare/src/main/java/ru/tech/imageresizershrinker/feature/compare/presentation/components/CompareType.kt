package ru.tech.imageresizershrinker.feature.compare.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.core.ui.icons.material.Transparency

sealed class CompareType(
    val icon: ImageVector
) {
    data object Slide : CompareType(
        icon = Icons.Rounded.Compare
    )

    data object SideBySide : CompareType(
        icon = Icons.Rounded.ZoomIn
    )

    data object Tap : CompareType(
        icon = Icons.Rounded.TouchApp
    )

    data object Transparency : CompareType(
        icon = Icons.Filled.Transparency
    )

    companion object {
        val entries by lazy {
            listOf(Slide, SideBySide, Tap, Transparency)
        }
    }
}