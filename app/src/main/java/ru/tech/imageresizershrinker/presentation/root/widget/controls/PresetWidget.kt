package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.theme.icons.CreateAlt
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.other.RevealValue
import ru.tech.imageresizershrinker.presentation.root.widget.other.SwipeToReveal
import ru.tech.imageresizershrinker.presentation.root.widget.other.rememberRevealState
import ru.tech.imageresizershrinker.presentation.root.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalEditPresetsState
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PresetWidget(
    selectedPreset: Int,
    onPresetSelected: (Int) -> Unit
) {
    val settingsState = LocalSettingsState.current
    val editPresetsState = LocalEditPresetsState.current
    val data = settingsState.presets

    val state = rememberRevealState()
    val scope = rememberCoroutineScope()

    SwipeToReveal(
        maxRevealDp = 88.dp,
        state = state,
        swipeableContent = {
            Column(
                modifier = Modifier
                    .block(shape = RoundedCornerShape(24.dp))
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
                Spacer(Modifier.size(8.dp))
                Text(
                    stringResource(R.string.presets),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .block()
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(
                            8.dp, Alignment.CenterHorizontally
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        data.forEach {
                            item {
                                val selected = selectedPreset == it
                                OutlinedIconButton(
                                    shape = MaterialTheme.shapes.medium,
                                    onClick = {
                                        onPresetSelected(it)
                                    },
                                    border = BorderStroke(
                                        max(settingsState.borderWidth, 1.dp),
                                        animateColorAsState(
                                            if (!selected) MaterialTheme.colorScheme.outlineVariant()
                                            else MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.9f).compositeOver(Color.Black)
                                        ).value
                                    ),
                                    colors = IconButtonDefaults.outlinedIconButtonColors(
                                        containerColor = animateColorAsState(
                                            if (selected) MaterialTheme.colorScheme.tertiaryContainer
                                            else MaterialTheme.colorScheme.surfaceVariant
                                        ).value,
                                        contentColor = animateColorAsState(
                                            if (selected) MaterialTheme.colorScheme.onTertiaryContainer
                                            else MaterialTheme.colorScheme.onSurface
                                        ).value,
                                    ),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    AutoSizeText(it.toString())
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .width(6.dp)
                            .height(44.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    0f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                                    1f to Color.Transparent
                                )
                            )
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .width(6.dp)
                            .height(44.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    1f to MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                                )
                            )
                    )
                }
            }
        },
        revealedContentStart = {
            Box(
                Modifier
                    .fillMaxSize()
                    .block(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                OutlinedIconButton(
                    colors = IconButtonDefaults.filledTonalIconButtonColors(),
                    border = BorderStroke(
                        settingsState.borderWidth,
                        MaterialTheme.colorScheme.outlineVariant(onTopOf = MaterialTheme.colorScheme.secondaryContainer)
                    ),
                    onClick = { editPresetsState.value = true },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Rounded.CreateAlt, null)
                }
            }
        }
    )
}