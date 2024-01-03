package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import ru.tech.imageresizershrinker.coredomain.image.draw.PathPaint
import ru.tech.imageresizershrinker.coredomain.image.filters.Filter
import ru.tech.imageresizershrinker.coredomain.model.FilterMask

data class UiFilterMask(
    override val filters: List<Filter<Bitmap, *>>,
    override val maskPaints: List<PathPaint<Path, Color>>,
    override val isInverseFillType: Boolean
) : FilterMask<Bitmap, Path, Color>