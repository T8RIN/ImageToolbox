package ru.tech.imageresizershrinker.feature.image_stitch.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.StitchMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import kotlin.math.roundToInt

@Composable
fun StitchModeSelector(
    modifier: Modifier = Modifier,
    value: StitchMode,
    onValueChange: (StitchMode) -> Unit
) {
    Column(
        modifier = modifier
            .container(shape = RoundedCornerShape(24.dp))
    ) {
        ToggleGroupButton(
            modifier = Modifier.padding(start = 3.dp, end = 2.dp),
            enabled = true,
            title = {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(id = R.string.stitch_mode))
                }
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(
                stringResource(R.string.horizontal),
                stringResource(R.string.vertical),
                stringResource(R.string.horizontal_grid),
                stringResource(R.string.vertical_grid)
            ),
            selectedIndex = when (value) {
                StitchMode.Horizontal -> 0
                StitchMode.Vertical -> 1
                is StitchMode.Grid.Horizontal -> 2
                is StitchMode.Grid.Vertical -> 3
            },
            indexChanged = {
                onValueChange(
                    when (it) {
                        0 -> StitchMode.Horizontal
                        1 -> StitchMode.Vertical
                        2 -> StitchMode.Grid.Horizontal()
                        3 -> StitchMode.Grid.Vertical()
                        else -> StitchMode.Horizontal
                    }
                )
            }
        )
        AnimatedVisibility(
            visible = value is StitchMode.Grid.Horizontal,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EnhancedSliderItem(
                modifier = Modifier.padding(8.dp),
                value = value.gridCellsCount(),
                title = stringResource(R.string.rows_count),
                sliderModifier = Modifier
                    .padding(
                        top = 14.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                icon = Icons.Rounded.TableRows,
                valueRange = 2f..6f,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChangeFinished = {
                    onValueChange(
                        StitchMode.Grid.Horizontal(it.roundToInt())
                    )
                },
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
        }
        AnimatedVisibility(
            visible = value is StitchMode.Grid.Vertical,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EnhancedSliderItem(
                modifier = Modifier.padding(8.dp),
                value = value.gridCellsCount(),
                title = stringResource(R.string.columns_count),
                sliderModifier = Modifier
                    .padding(
                        top = 14.dp,
                        start = 12.dp,
                        end = 12.dp,
                        bottom = 10.dp
                    ),
                icon = Icons.Rounded.ViewColumn,
                valueRange = 2f..6f,
                internalStateTransformation = {
                    it.roundToInt()
                },
                onValueChangeFinished = {
                    onValueChange(
                        StitchMode.Grid.Vertical(it.roundToInt())
                    )
                },
                onValueChange = {},
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceContainer
            )
        }
    }
}