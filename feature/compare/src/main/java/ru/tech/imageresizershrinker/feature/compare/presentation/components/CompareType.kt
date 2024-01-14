package ru.tech.imageresizershrinker.feature.compare.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material.icons.rounded.TouchApp
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Transparency

sealed class CompareType(
    val icon: ImageVector,
    @StringRes val title: Int
) {
    data object Slide : CompareType(
        icon = Icons.Rounded.Compare,
        title = R.string.slide
    )

    data object SideBySide : CompareType(
        icon = Icons.Rounded.ZoomIn,
        title = R.string.side_by_side
    )

    data object Tap : CompareType(
        icon = Icons.Rounded.TouchApp,
        title = R.string.toggle_tap
    )

    data object Transparency : CompareType(
        icon = Icons.Filled.Transparency,
        title = R.string.transparency
    )

    companion object {
        val entries by lazy {
            listOf(Slide, SideBySide, Tap, Transparency)
        }
    }
}