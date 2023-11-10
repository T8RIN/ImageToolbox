package ru.tech.imageresizershrinker.presentation.filters_screen.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.mixedContainer
import ru.tech.imageresizershrinker.presentation.root.widget.controls.EnhancedButton

@Composable
fun AddFilterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    EnhancedButton(
        containerColor = MaterialTheme.colorScheme.mixedContainer,
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Rounded.PhotoFilter,
            contentDescription = null
        )
        Spacer(Modifier.width(8.dp))
        Text(stringResource(id = R.string.add_filter))
    }
}