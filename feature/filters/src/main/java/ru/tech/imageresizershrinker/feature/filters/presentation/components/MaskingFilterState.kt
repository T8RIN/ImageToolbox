package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.net.Uri

data class MaskingFilterState(
    val uri: Uri? = null,
    val masks: List<UiFilterMask> = emptyList()
)