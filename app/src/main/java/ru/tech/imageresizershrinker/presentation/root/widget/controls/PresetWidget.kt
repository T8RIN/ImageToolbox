@file:Suppress("AnimateAsStateLabel")

package ru.tech.imageresizershrinker.presentation.root.widget.controls


import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.domain.model.Preset
import ru.tech.imageresizershrinker.presentation.root.icons.material.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.icons.material.Telegram
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.LocalNavController
import ru.tech.imageresizershrinker.presentation.root.utils.navigation.Screen
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.alertDialogBorder
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.fadingEdges
import ru.tech.imageresizershrinker.presentation.root.widget.other.RevealValue
import ru.tech.imageresizershrinker.presentation.root.widget.other.SwipeToReveal
import ru.tech.imageresizershrinker.presentation.root.widget.other.rememberRevealState
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

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
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                CircleShape
                            )
                            .clip(CircleShape)
                            .clickable {
                                showPresetInfoDialog = true
                            }
                            .padding(1.dp)
                            .size(
                                with(LocalDensity.current) {
                                    LocalTextStyle.current.fontSize.toDp()
                                }
                            )
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
                                Chip(
                                    selected = selected,
                                    onClick = { onPresetSelected(Preset.Telegram) }
                                ) {
                                    Icon(Icons.Rounded.Telegram, null)
                                }
                            }
                        }
                        data.forEach {
                            item {
                                val selected = selectedPreset.value() == it
                                Chip(
                                    selected = selected,
                                    onClick = { onPresetSelected(Preset.Numeric(it)) }
                                ) {
                                    AutoSizeText(it.toString())
                                }
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

@Composable
private fun Chip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    val color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = 0.6f
        )
    )

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.onTertiary
            else MaterialTheme.colorScheme.onSurface
        ),
        LocalContentColor provides animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ).value,
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(36.dp, 36.dp)
                .container(
                    color = color,
                    resultPadding = 0.dp,
                    borderColor = animateColorAsState(
                        if (!selected) MaterialTheme.colorScheme.outlineVariant()
                        else MaterialTheme.colorScheme.primary
                            .copy(
                                alpha = 0.9f
                            )
                            .compositeOver(Color.Black)
                    ).value,
                    shape = MaterialTheme.shapes.medium,
                    autoShadowElevation = 0.5.dp
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                label()
            }
        }
    }
}