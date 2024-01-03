package ru.tech.imageresizershrinker.feature.image_stitch.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.container

@Composable
fun ImageFadingEdgesSelector(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int?) -> Unit
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
                Text(stringResource(id = R.string.fading_edges))
                Spacer(modifier = Modifier.height(8.dp))
            },
            items = listOf(
                stringResource(R.string.disabled),
                stringResource(R.string.start),
                stringResource(R.string.both)
            ),
            selectedIndex = when (value) {
                null -> 0
                0 -> 1
                else -> 2
            },
            indexChanged = {
                onValueChange(
                    when (it) {
                        0 -> null
                        1 -> 0
                        else -> 1
                    }
                )
            }
        )
    }
}