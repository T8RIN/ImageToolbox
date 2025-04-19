package ru.tech.imageresizershrinker.core.ui.utils.helper

import androidx.compose.runtime.compositionLocalOf
import ru.tech.imageresizershrinker.core.domain.model.ImageModel

val LocalFilterPreviewModel =
    compositionLocalOf<ImageModel> { error("FilterPreviewModel not present") }