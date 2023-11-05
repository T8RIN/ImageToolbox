package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import ru.tech.imageresizershrinker.domain.image.filters.Filter
import ru.tech.imageresizershrinker.domain.model.FilterMask

data class UiFilterMask(
    override val path: Path,
    override val paint: Paint,
    override val filters: List<Filter<Bitmap, *>>
) : FilterMask<Bitmap, Path, Paint>