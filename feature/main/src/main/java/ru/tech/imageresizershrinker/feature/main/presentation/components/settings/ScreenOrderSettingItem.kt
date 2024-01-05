package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataArray
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.icons.material.Stacks
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHost
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun ScreenOrderSettingItem(
    updateOrder: (List<Screen>) -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(start = 8.dp, end = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val screenList = settingsState.screenList
    val showArrangementSheet = rememberSaveable { mutableStateOf(false) }

    val toastHostState = LocalToastHost.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val enabled = !settingsState.groupOptionsByTypes
    PreferenceItem(
        shape = shape,
        modifier = modifier
            .alpha(
                animateFloatAsState(
                    if (enabled) 1f
                    else 0.5f
                ).value
            ),
        autoShadowElevation = if (enabled) 1.dp else 0.dp,
        onClick = {
            if (enabled) {
                showArrangementSheet.value = true
            } else scope.launch {
                toastHostState.showToast(
                    icon = Icons.Rounded.Stacks,
                    message = context.getString(R.string.cannot_change_arrangement_while_options_grouping_enabled)
                )
            }
        },
        icon = Icons.Outlined.DataArray,
        title = stringResource(R.string.order),
        subtitle = stringResource(R.string.order_sub),
        color = MaterialTheme.colorScheme
            .secondaryContainer
            .copy(alpha = 0.2f),
        endIcon = Icons.Rounded.CreateAlt,
    )

    SimpleSheet(
        visible = showArrangementSheet,
        title = {
            TitleItem(
                text = stringResource(R.string.order),
                icon = Icons.Rounded.Stacks
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = Color.Transparent,
                onClick = { showArrangementSheet.value = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            Box {
                val data = remember { mutableStateOf(screenList) }
                val state = rememberReorderableLazyListState(
                    onMove = { from, to ->
                        data.value = data.value.toMutableList().apply {
                            add(to.index, removeAt(from.index))
                        }
                    },
                    onDragEnd = { _, _ ->
                        updateOrder(data.value)
                    }
                )
                LazyColumn(
                    state = state.listState,
                    modifier = Modifier
                        .reorderable(state)
                        .detectReorderAfterLongPress(state),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(data.value, key = { it }) { screen ->
                        ReorderableItem(state, key = screen) { isDragging ->
                            val elevation by animateDpAsState(if (isDragging) 16.dp else 0.dp)
                            val tonalElevation by animateDpAsState(if (isDragging) 16.dp else 1.dp)
                            PreferenceItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(elevation, RoundedCornerShape(16.dp)),
                                title = stringResource(screen.title),
                                subtitle = stringResource(screen.subtitle),
                                icon = screen.icon,
                                endIcon = Icons.Rounded.DragHandle,
                                color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                    tonalElevation
                                )
                            )
                        }
                    }
                }
            }
        }
    )
}