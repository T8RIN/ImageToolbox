package ru.tech.imageresizershrinker.core.ui.widget.palette_selection

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.PaletteStyle
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun PaletteStyleSelectionItem(style: PaletteStyle, onClick: () -> Unit) {
    val settingsState = LocalSettingsState.current
    val selected = settingsState.themeStyle == style
    val context = LocalContext.current

    PreferenceItem(
        onClick = onClick,
        title = style.getTitle(context),
        subtitle = style.getSubtitle(context),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = animateFloatAsState(
                if (selected) 0.7f
                else 0.2f
            ).value
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = settingsState.borderWidth,
                color = animateColorAsState(
                    if (selected) MaterialTheme.colorScheme
                        .onSecondaryContainer
                        .copy(alpha = 0.5f)
                    else Color.Transparent
                ).value,
                shape = RoundedCornerShape(16.dp)
            ),
        endIcon = if (selected) Icons.Rounded.RadioButtonChecked else Icons.Rounded.RadioButtonUnchecked
    )
}