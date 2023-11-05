package ru.tech.imageresizershrinker.presentation.filters_screen.components

import android.net.Uri

data class MaskingFilterState(
    val uri: Uri? = null,
    val masks: List<UiFilterMask> = emptyList()
)