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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Palette
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.resources.icons.Save
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
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.rememberRetainedLazyListState
import kotlinx.coroutines.launch

@Composable
internal fun <T> StylePresetsSheet(
    visible: Boolean,
    currentStyle: T,
    styles: List<Pair<String, T>>,
    selectedName: String?,
    showSelection: Boolean,
    listStateKey: String,
    summary: @Composable (T) -> String,
    onDismiss: () -> Unit,
    onApply: (T) -> Unit,
    onSave: (String) -> Unit,
    onWantDelete: (String) -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                icon = Icons.Rounded.Palette,
                text = stringResource(R.string.style_presets)
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
            state = rememberRetainedLazyListState(listStateKey)
        ) {
            if (selectedName == null) {
                item("AddStylePreset") {
                    AddStylePresetBlock(
                        subtitle = summary(currentStyle),
                        onSave = onSave,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .animateItem()
                    )
                }
            }

            itemsIndexed(
                items = styles,
                key = { _, (name, _) -> "StylePreset:$name" }
            ) { index, (name, style) ->
                StylePresetItem(
                    name = name,
                    subtitle = summary(style),
                    selected = name == selectedName,
                    showSelection = showSelection,
                    shape = ShapeDefaults.byIndex(index, styles.size),
                    onApply = { onApply(style) },
                    onWantDelete = { onWantDelete(name) }
                )
            }
        }
    }
}

@Composable
private fun AddStylePresetBlock(
    subtitle: String,
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
            icon = Icons.Outlined.Palette,
            text = stringResource(R.string.save_style_preset),
            modifier = Modifier,
            subtitle = subtitle
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
                    Icon(Icons.Rounded.Save, contentDescription = stringResource(R.string.save))
                }
            },
            singleLine = true
        )
    }
}

@Composable
private fun LazyItemScope.StylePresetItem(
    name: String,
    subtitle: String,
    selected: Boolean,
    showSelection: Boolean,
    shape: Shape,
    onApply: () -> Unit,
    onWantDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val state = rememberRevealState()

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
                        onWantDelete()
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
                title = name,
                subtitle = subtitle,
                onClick = onApply,
                modifier = Modifier.fillMaxWidth(),
                drawStartIconContainer = false,
                startIcon = if (!showSelection) null else {
                    {
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