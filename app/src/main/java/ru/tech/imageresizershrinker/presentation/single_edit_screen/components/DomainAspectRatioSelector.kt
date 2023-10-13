package ru.tech.imageresizershrinker.presentation.single_edit_screen.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.domain.model.AspectRatio
import ru.tech.imageresizershrinker.presentation.crop_screen.components.AspectRatioSelection
import ru.tech.imageresizershrinker.presentation.crop_screen.components.toDomainAspect
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun DomainAspectRatioSelector(
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .container(shape = RoundedCornerShape(24.dp)),
    value: AspectRatio,
    onValueChange: (AspectRatio) -> Unit
) {
    val settingsState = LocalSettingsState.current
    Box {
        AspectRatioSelection(
            modifier = modifier,
            selectedIndex = settingsState.aspectRatios.indexOfFirst { aspect: AspectRatio ->
                aspect.value == value.value
            },
            contentPadding = PaddingValues(
                start = 10.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 10.dp
            ),
            unselectedCardColor = MaterialTheme.colorScheme.surfaceContainer,
            onAspectRatioChange = { aspect ->
                onValueChange(aspect.toDomainAspect())
            }
        )
    }
}