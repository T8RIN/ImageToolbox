/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.crop.components

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.RectModel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.CropSmall
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropPreset
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun CropPresetSelector(
    rect: RectModel,
    presets: List<PdfCropPreset>,
    onApplyPreset: (PdfCropPreset) -> Unit,
    onSavePreset: (String) -> Unit,
    onDeletePreset: (PdfCropPreset) -> Unit,
    shape: Shape
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var presetToDelete by remember { mutableStateOf<PdfCropPreset?>(null) }
    val selectedPreset = remember(rect, presets) {
        presets.firstOrNull { it.rect == rect }
    }
    val actions = remember(onApplyPreset, onSavePreset, onDeletePreset) {
        CropPresetActions(
            onApply = onApplyPreset,
            onSave = onSavePreset,
            onDelete = onDeletePreset
        )
    }

    PreferenceItem(
        title = stringResource(R.string.crop_presets),
        subtitle = selectedPreset?.name ?: stringResource(R.string.crop_presets_sub),
        onClick = { showSheet = true },
        shape = shape,
        startIcon = Icons.Outlined.Loyalty,
        endIcon = Icons.Rounded.MiniEdit,
        modifier = Modifier.fillMaxWidth()
    )

    CropPresetsSheet(
        visible = showSheet,
        rect = rect,
        presets = presets,
        onDismiss = { showSheet = false },
        onWantDelete = { presetToDelete = it },
        actions = actions
    )

    DeleteCropPresetDialog(
        preset = presetToDelete,
        onDismiss = { presetToDelete = null },
        onConfirm = actions.onDelete
    )
}

@Composable
private fun DeleteCropPresetDialog(
    preset: PdfCropPreset?,
    onDismiss: () -> Unit,
    onConfirm: (PdfCropPreset) -> Unit
) {
    EnhancedAlertDialog(
        visible = preset != null,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.delete_crop_preset))
        },
        text = {
            Text(
                stringResource(
                    R.string.delete_crop_preset_sub,
                    preset?.name ?: ""
                )
            )
        },
        onDismissRequest = onDismiss,
        confirmButton = {
            EnhancedButton(
                onClick = {
                    preset?.let(onConfirm)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}

@Composable
private fun CropPresetsSheet(
    visible: Boolean,
    rect: RectModel,
    presets: List<PdfCropPreset>,
    onDismiss: () -> Unit,
    onWantDelete: (PdfCropPreset) -> Unit,
    actions: CropPresetActions
) {
    val selectedPreset = remember(rect, presets) {
        presets.firstOrNull { it.rect == rect }
    }
    val showAddBlock = selectedPreset == null

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                icon = Icons.Rounded.CropSmall,
                text = stringResource(R.string.crop_presets)
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .animateContentSizeNoClip()
                .clearFocusOnTap(),
            state = rememberRetainedLazyListState("PDF_CROP_PRESETS")
        ) {
            if (showAddBlock) {
                item("AddCropPreset") {
                    AddCropPresetBlock(
                        rect = rect,
                        onSave = actions.onSave,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .animateItem()
                    )
                }
            }

            itemsIndexed(
                items = presets,
                key = { _, item -> item.name }
            ) { index, preset ->
                CropPresetItem(
                    index = index,
                    presetsCount = presets.size,
                    preset = preset,
                    selected = preset == selectedPreset,
                    onApply = actions.onApply,
                    onWantDelete = onWantDelete
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.CropPresetItem(
    index: Int,
    presetsCount: Int,
    preset: PdfCropPreset,
    selected: Boolean,
    onApply: (PdfCropPreset) -> Unit,
    onWantDelete: (PdfCropPreset) -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = rememberRevealState()
    val shape = ShapeDefaults.byIndex(index, presetsCount)

    SwipeToReveal(
        state = state,
        directions = setOf(RevealDirection.EndToStart),
        modifier = Modifier.animateItem(),
        revealedContentEnd = {
            Box(
                Modifier
                    .fillMaxSize()
                    .container(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = shape,
                        autoShadowElevation = 0.dp,
                        resultPadding = 0.dp
                    )
                    .hapticsClickable {
                        scope.launch {
                            state.animateTo(RevealValue.Default)
                        }
                        onWantDelete(preset)
                    }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(end = 8.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        swipeableContent = {
            PreferenceItemOverload(
                title = preset.name,
                subtitle = preset.rect.summary(),
                onClick = { onApply(preset) },
                drawStartIconContainer = false,
                modifier = Modifier.fillMaxWidth(),
                startIcon = {
                    AnimatedContent(
                        targetState = selected,
                        modifier = Modifier.size(24.dp)
                    ) { isSelected ->
                        Icon(
                            imageVector = if (isSelected) {
                                Icons.Rounded.RadioButtonChecked
                            } else {
                                Icons.Rounded.RadioButtonUnchecked
                            },
                            contentDescription = null
                        )
                    }
                },
                shape = shape,
                containerColor = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Unspecified
                },
                contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    Color.Unspecified
                }
            )
        }
    )
}

@Composable
private fun AddCropPresetBlock(
    rect: RectModel,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier.container(
            shape = ShapeDefaults.default,
            resultPadding = 12.dp
        )
    ) {
        TitleItem(
            icon = Icons.Rounded.CropSmall,
            text = stringResource(R.string.save_crop_preset),
            subtitle = rect.summary()
        )

        Spacer(Modifier.height(12.dp))

        RoundedTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.name),
            endIcon = {
                EnhancedIconButton(
                    onClick = {
                        onSave(name)
                        name = ""
                    },
                    enabled = name.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = stringResource(R.string.save)
                    )
                }
            },
            singleLine = true
        )
    }
}

@Composable
private fun RectModel.summary(): String = stringResource(
    R.string.crop_preset_summary,
    (left * PercentMultiplier).roundToInt(),
    (top * PercentMultiplier).roundToInt(),
    ((1f - right) * PercentMultiplier).roundToInt(),
    ((1f - bottom) * PercentMultiplier).roundToInt()
)

private data class CropPresetActions(
    val onApply: (PdfCropPreset) -> Unit,
    val onSave: (String) -> Unit,
    val onDelete: (PdfCropPreset) -> Unit
)

private const val PercentMultiplier = 100
