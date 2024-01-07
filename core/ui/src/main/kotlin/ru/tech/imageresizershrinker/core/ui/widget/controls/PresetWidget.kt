@file:Suppress("AnimateAsStateLabel")

package ru.tech.imageresizershrinker.core.ui.widget.controls


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.Preset
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.CreateAlt
import ru.tech.imageresizershrinker.core.ui.icons.material.Telegram
import ru.tech.imageresizershrinker.core.ui.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.core.ui.widget.other.RevealValue
import ru.tech.imageresizershrinker.core.ui.widget.other.SwipeToReveal
import ru.tech.imageresizershrinker.core.ui.widget.other.rememberRevealState
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PresetWidget(
    selectedPreset: Preset,
    includeTelegramOption: Boolean,
    showWarning: Boolean = false,
    onPresetSelected: (Preset) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val editPresetsState = LocalEditPresetsState.current
    val data = settingsState.presets

    val screen = LocalNavController.current.backstack.entries.last().destination

    val state = rememberRevealState()
    val scope = rememberCoroutineScope()

    var showPresetInfoDialog by remember { mutableStateOf(false) }

    SwipeToReveal(
        maxRevealDp = 88.dp,
        state = state,
        swipeableContent = {
            Column(
                modifier = Modifier
                    .container(shape = RoundedCornerShape(24.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                scope.launch {
                                    state.animateTo(RevealValue.FullyRevealedEnd)
                                }
                            }
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.presets),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    SupportingButton(
                        onClick = {
                            showPresetInfoDialog = true
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    val listState = rememberLazyListState()
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fadingEdges(listState)
                            .padding(vertical = 1.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp, Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        if (includeTelegramOption) {
                            item {
                                val selected = selectedPreset.isTelegram()
                                EnhancedChip(
                                    selected = selected,
                                    onClick = { onPresetSelected(Preset.Telegram) },
                                    selectedColor = MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Rounded.Telegram, null)
                                }
                            }
                        }
                        items(data) {
                            val selected = selectedPreset.value() == it
                            EnhancedChip(
                                selected = selected,
                                onClick = { onPresetSelected(Preset.Numeric(it)) },
                                selectedColor = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                AutoSizeText(it.toString())
                            }
                        }
                    }
                }

                OOMWarning(visible = showWarning)
            }
        },
        revealedContentStart = {
            Box(
                Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                EnhancedIconButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { editPresetsState.value = true },
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 8.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Rounded.CreateAlt, null)
                }
            }
        }
    )

    if (showPresetInfoDialog) {
        AlertDialog(
            modifier = Modifier.alertDialogBorder(),
            onDismissRequest = { showPresetInfoDialog = false },
            confirmButton = {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = { showPresetInfoDialog = false }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            title = {
                Text(stringResource(R.string.presets))
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            },
            text = {
                if (screen !is Screen.ResizeByBytes) Text(stringResource(R.string.presets_sub))
                else Text(stringResource(R.string.presets_sub_bytes))
            }
        )
    }
}