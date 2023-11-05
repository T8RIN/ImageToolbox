package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.net.Uri
import ru.tech.imageresizershrinker.presentation.root.transformation.filter.UiFilter

data class BasicFilterState(
    val uris: List<Uri>? = null,
    val filters: List<UiFilter<*>> = emptyList(),
    val selectedUri: Uri? = null
)