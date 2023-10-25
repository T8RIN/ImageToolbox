package ru.tech.imageresizershrinker.presentation.root.widget.buttons

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonBorder
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.presentation.draw_screen.components.materialShadow
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ToggleGroupButton(
    modifier: Modifier = defaultModifier,
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
                Text(it, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleGroupButton(
    modifier: Modifier = defaultModifier,
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
            val scrollState = rememberScrollState()
            SingleChoiceSegmentedButtonRow(
                space = max(settingsState.borderWidth, 1.dp),
                modifier = Modifier
                    .fadingEdges(scrollState)
                    .horizontalScroll(scrollState)
                    .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val shape = SegmentedButtonDefaults.itemShape(index, items.size)
                    SegmentedButton(
                        enabled = enabled,
                        onClick = { indexChanged(index) },
                        border = SegmentedButtonBorder(settingsState.borderWidth),
                        selected = index == selectedIndex,
                        colors = SegmentedButtonDefaults.colors(
                            activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                            inactiveContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                6.dp
                            ),
                            activeContainerColor = if (enabled) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                            },
                            activeContentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.materialShadow(
                            shape = shape,
                            elevation = animateDpAsState(
                                if (settingsState.borderWidth >= 0.dp || !settingsState.allowShowingShadowsInsteadOfBorders) 0.dp
                                else if (selectedIndex == index) 2.dp
                                else 1.dp
                            ).value
                        ),
                        shape = shape
                    ) {
                        Text(text = item, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

private var defaultModifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)