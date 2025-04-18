package ru.tech.imageresizershrinker.core.filters.domain.model

import com.t8rin.curves.ImageCurvesEditorState

data class ToneCurvesParams(
    val controlPoints: List<List<Float>>
) {
    companion object {
        val Default by lazy {
            ToneCurvesParams(ImageCurvesEditorState.Default.controlPoints)
        }
    }
}