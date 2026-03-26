package com.smarttoolfactory.image.beforeafter

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntRect
import com.smarttoolfactory.image.ImageScope

/**
 * Scope for before-after images that returns touch position on Composable
 */
interface BeforeAfterImageScope : ImageScope {
    /**
     * Touch position on screen
     */
    var position: Offset
}

class BeforeAfterImageScopeImpl(
    private val density: Density,
    override val constraints: Constraints,
    override val imageWidth: Dp,
    override val imageHeight: Dp,
    override val rect: IntRect,
    override var position: Offset,
) : BeforeAfterImageScope {

    override val minWidth: Dp get() = with(density) { constraints.minWidth.toDp() }

    override val maxWidth: Dp
        get() = with(density) {
            if (constraints.hasBoundedWidth) constraints.maxWidth.toDp() else Dp.Infinity
        }

    override val minHeight: Dp get() = with(density) { constraints.minHeight.toDp() }

    override val maxHeight: Dp
        get() = with(density) {
            if (constraints.hasBoundedHeight) constraints.maxHeight.toDp() else Dp.Infinity
        }
}
