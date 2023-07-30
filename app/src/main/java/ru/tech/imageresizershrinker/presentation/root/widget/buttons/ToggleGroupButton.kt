package ru.tech.imageresizershrinker.presentation.root.widget.buttons

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ToggleGroupButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: String? = null,
    indexChanged: (Int) -> Unit
) {
    ToggleGroupButton(
        enabled = enabled,
        items = items,
        selectedIndex = selectedIndex,
        indexChanged = indexChanged,
        modifier = modifier,
        title = {
            title?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleGroupButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: @Composable () -> Unit = {},
    indexChanged: (Int) -> Unit
) {
    val settingsState = LocalSettingsState.current

    val disColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = 0.38f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) disColor
            else Color.Unspecified
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title()
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                items.forEachIndexed { index, item ->
                    SegmentedButton(
                        enabled = enabled,
                        onClick = { indexChanged(index) },
                        border = SegmentedButtonBorder(max(settingsState.borderWidth, 1.dp)),
                        selected = index == selectedIndex,
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                6.dp
                            )
                        ),
                        shape = SegmentedButtonDefaults.shape(index, items.size)
                    ) {
                        Text(text = item)
                    }
                }
            }
        }
    }
}

private var defaultModifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)