package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.ToggleGroupButton


@Composable
fun ResizeGroup(
    enabled: Boolean,
    resizeType: Int,
    onResizeChange: (Int) -> Unit
) {
    ToggleGroupButton(
        modifier = Modifier
            .block(shape = RoundedCornerShape(24.dp))
            .padding(start = 3.dp, end = 2.dp),
        enabled = enabled,
        title = stringResource(R.string.resize_type),
        items = listOf(
            stringResource(R.string.explicit),
            stringResource(R.string.flexible),
            stringResource(R.string.ratio)
        ),
        selectedIndex = resizeType,
        indexChanged = onResizeChange
    )
}