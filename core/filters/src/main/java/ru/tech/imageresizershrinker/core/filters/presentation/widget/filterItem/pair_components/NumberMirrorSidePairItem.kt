package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.pair_components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.utils.roundTo
import ru.tech.imageresizershrinker.core.filters.domain.model.MirrorSide
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem.MirrorSideSelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem

@Composable
internal fun NumberMirrorSidePairItem(
    value: Pair<Number, MirrorSide>,
    filter: UiFilter<Pair<*, *>>,
    onFilterChange: (value: Pair<Number, MirrorSide>) -> Unit,
    previewOnly: Boolean
) {
    var sliderState1 by remember(value) { mutableFloatStateOf(value.first.toFloat()) }
    var mirrorSide by remember(value) { mutableStateOf(value.second) }

    EnhancedSliderItem(
        modifier = Modifier
            .padding(
                top = 8.dp,
                start = 8.dp,
                end = 8.dp
            ),
        enabled = !previewOnly,
        value = sliderState1,
        title = filter.paramsInfo[0].title?.let {
            stringResource(it)
        } ?: "",
        onValueChange = {
            sliderState1 = it
            onFilterChange(sliderState1 to mirrorSide)
        },
        internalStateTransformation = {
            it.roundTo(filter.paramsInfo[0].roundTo)
        },
        valueRange = filter.paramsInfo[0].valueRange,
        behaveAsContainer = false
    )
    filter.paramsInfo[1].title?.let { title ->
        MirrorSideSelector(
            title = title,
            value = mirrorSide,
            onValueChange = {
                mirrorSide = it
                onFilterChange(sliderState1 to mirrorSide)
            }
        )
    }
}