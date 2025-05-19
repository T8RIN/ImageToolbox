package ru.tech.imageresizershrinker.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.filters.domain.model.MirrorSide
import ru.tech.imageresizershrinker.core.filters.presentation.utils.translatedName
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButtonGroup

@Composable
internal fun MirrorSideSelector(
    title: Int,
    value: MirrorSide,
    onValueChange: (MirrorSide) -> Unit,
) {
    Text(
        text = stringResource(title),
        modifier = Modifier.padding(
            top = 8.dp,
            start = 12.dp,
            end = 12.dp,
        )
    )
    val entries = remember {
        MirrorSide.entries
    }
    EnhancedButtonGroup(
        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        items = entries.map { it.translatedName },
        selectedIndex = entries.indexOf(value),
        onIndexChange = {
            onValueChange(entries[it])
        }
    )
}