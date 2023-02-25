package ru.tech.imageresizershrinker.resize_screen.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.block

@Composable
fun ExtensionGroup(
    enabled: Boolean,
    mime: Int,
    onMimeChange: (Int) -> Unit
) {
    ToggleGroupButton(
        modifier = Modifier
            .block()
            .padding(start = 3.dp, end = 2.dp),
        title = stringResource(R.string.extension),
        enabled = enabled,
        items = listOf("JPEG", "WEBP", "PNG"),
        selectedIndex = mime,
        indexChanged = onMimeChange
    )
}