package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.net.Uri
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter

data class BasicFilterState(
    val uris: List<Uri>? = null,
    val filters: List<UiFilter<*>> = emptyList(),
    val selectedUri: Uri? = null
)