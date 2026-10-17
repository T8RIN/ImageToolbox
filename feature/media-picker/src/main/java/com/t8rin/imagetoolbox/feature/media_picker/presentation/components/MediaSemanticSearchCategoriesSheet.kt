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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Neurology
import com.t8rin.imagetoolbox.core.resources.icons.Restore
import com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory
import com.t8rin.imagetoolbox.core.settings.domain.model.SemanticSearchSettings
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategory
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import java.util.UUID
import kotlin.math.roundToInt

@Composable
internal fun MediaSemanticSearchCategoriesSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    component: MediaPickerComponent
) {
    val settings = component.settingsState.semanticSearchSettings
    var editingId by rememberSaveable { mutableStateOf("") }
    var showEditor by rememberSaveable { mutableStateOf(false) }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_search_manage_categories),
                icon = Icons.Outlined.Neurology
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = { onDismiss(false) },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                PreferenceItem(
                    title = stringResource(R.string.gallery_search_add_category),
                    subtitle = stringResource(R.string.gallery_search_add_category_description),
                    startIcon = Icons.Rounded.Add,
                    onClick = {
                        editingId = UUID.randomUUID().toString()
                        showEditor = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (settings.customCategories.isNotEmpty()) {
                item {
                    TitleItem(
                        text = stringResource(R.string.gallery_search_custom_categories),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                itemsIndexed(
                    items = settings.customCategories,
                    key = { _, category -> category.id }
                ) { index, category ->
                    PreferenceItem(
                        title = category.name,
                        subtitle = stringResource(
                            R.string.gallery_search_threshold_value,
                            (category.threshold * 100).roundToInt()
                        ),
                        endIcon = Icons.Rounded.MiniEdit,
                        onClick = {
                            editingId = category.id
                            showEditor = true
                        },
                        shape = ShapeDefaults.byIndex(index, settings.customCategories.size),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                TitleItem(
                    text = stringResource(R.string.gallery_search_builtin_categories),
                    modifier = Modifier.padding(12.dp)
                )
            }
            itemsIndexed(
                items = SemanticSearchCategory.BuiltIn.entries,
                key = { _, category -> category.id }
            ) { index, category ->
                PreferenceItem(
                    title = category.title(),
                    subtitle = stringResource(
                        R.string.gallery_search_threshold_value,
                        ((settings.categoryThresholds[category.id]
                            ?: category.defaultThreshold) * 100).roundToInt()
                    ),
                    endIcon = Icons.Rounded.MiniEdit,
                    onClick = {
                        editingId = category.id
                        showEditor = true
                    },
                    shape = ShapeDefaults.byIndex(
                        index,
                        SemanticSearchCategory.BuiltIn.entries.size
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    SemanticSearchCategoryEditor(
        visible = visible && showEditor,
        onDismiss = { showEditor = it },
        id = editingId,
        component = component
    )
}

@Composable
private fun SemanticSearchCategoryEditor(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    id: String,
    component: MediaPickerComponent
) {
    val settings = component.settingsState.semanticSearchSettings
    val builtIn = SemanticSearchCategory.BuiltIn.entries.find { it.id == id }
    val custom = settings.customCategories.find { it.id == id }
    val defaultThreshold = builtIn?.defaultThreshold ?: SemanticSearchSettings.DefaultTextThreshold
    val initialThreshold = custom?.threshold ?: settings.categoryThresholds[id] ?: defaultThreshold
    val focusManager = LocalFocusManager.current
    var name by rememberSaveable(id, visible) { mutableStateOf(custom?.name.orEmpty()) }
    var description by rememberSaveable(
        id,
        visible
    ) { mutableStateOf(custom?.description.orEmpty()) }
    var threshold by rememberSaveable(id, visible) { mutableFloatStateOf(initialThreshold) }
    var showDeleteConfirmation by rememberSaveable(id, visible) { mutableStateOf(false) }
    val builtInNames = SemanticSearchCategory.BuiltIn.entries.map { it.title() }
    val duplicateName = name.isNotBlank() && (
            builtInNames.any { it.equals(name.trim(), ignoreCase = true) } ||
                    settings.customCategories.any {
                        it.id != id && it.name.equals(name.trim(), ignoreCase = true)
                    }
            )
    val canSave =
        builtIn != null || (name.isNotBlank() && description.isNotBlank() && !duplicateName)

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            focusManager.clearFocus()
            onDismiss(it)
        },
        title = {
            TitleItem(
                text = stringResource(R.string.gallery_search_category),
                icon = Icons.Outlined.Neurology
            )
        },
        confirmButton = {
            if (custom != null) {
                EnhancedIconButton(
                    onClick = {
                        focusManager.clearFocus()
                        showDeleteConfirmation = true
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.gallery_search_delete_category)
                    )
                }
            }
            EnhancedButton(
                enabled = canSave,
                onClick = {
                    focusManager.clearFocus()
                    val savedThreshold = threshold
                    val category = CustomSemanticSearchCategory(
                        id = id,
                        name = name.trim(),
                        description = description.trim(),
                        threshold = savedThreshold
                    )
                    component.updateSemanticSearchSettings { current ->
                        if (builtIn != null) {
                            current.copy(
                                categoryThresholds = if (savedThreshold == defaultThreshold) {
                                    current.categoryThresholds - id
                                } else current.categoryThresholds + (id to savedThreshold)
                            )
                        } else {
                            current.copy(
                                customCategories = if (current.customCategories.any { it.id == id }) {
                                    current.customCategories.map { if (it.id == id) category else it }
                                } else current.customCategories + category
                            )
                        }
                    }
                    onDismiss(false)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .enhancedVerticalScroll(rememberScrollState())
                .clearFocusOnTap()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (builtIn != null) {
                PreferenceItem(
                    title = builtIn.title(),
                    subtitle = stringResource(R.string.gallery_search_builtin_category_description),
                    shape = ShapeDefaults.top,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                RoundedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.gallery_search_category_name),
                    isError = duplicateName,
                    supportingText = if (duplicateName) {
                        { Text(stringResource(R.string.gallery_search_category_name_exists)) }
                    } else null,
                    shape = ShapeDefaults.top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.top,
                            resultPadding = 8.dp
                        )
                )
                RoundedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = stringResource(R.string.gallery_search_category_description),
                    hint = stringResource(R.string.gallery_search_category_description_hint),
                    singleLine = false,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = ShapeDefaults.center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.center,
                            resultPadding = 8.dp
                        )
                )
            }
            EnhancedSliderItem(
                value = (threshold * 100).roundToInt(),
                title = stringResource(R.string.gallery_search_threshold),
                valueRange = SemanticSearchSettings.TextThresholdRange.let {
                    (it.start * 100).roundToInt().toFloat()..
                            (it.endInclusive * 100).roundToInt().toFloat()
                },
                onValueChange = { threshold = it.roundToInt() / 100f },
                internalStateTransformation = Float::roundToInt,
                valueSuffix = "%",
                shape = ShapeDefaults.center,
                modifier = Modifier.fillMaxWidth()
            )
            PreferenceItem(
                title = stringResource(R.string.reset),
                startIcon = Icons.Rounded.Restore,
                onClick = { threshold = defaultThreshold },
                shape = ShapeDefaults.bottom,
                modifier = Modifier.fillMaxWidth()
            )
            InfoContainer(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.85f),
                text = stringResource(R.string.gallery_search_category_threshold_description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }

    EnhancedAlertDialog(
        visible = visible && showDeleteConfirmation,
        onDismissRequest = { showDeleteConfirmation = false },
        icon = { Icon(Icons.Outlined.Delete, contentDescription = null) },
        title = { Text(stringResource(R.string.gallery_search_delete_category)) },
        text = {
            Text(
                stringResource(
                    R.string.gallery_search_delete_category_description,
                    custom?.name.orEmpty()
                )
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.error,
                onClick = {
                    component.updateSemanticSearchSettings { current ->
                        current.copy(customCategories = current.customCategories.filterNot { it.id == id })
                    }
                    showDeleteConfirmation = false
                    onDismiss(false)
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showDeleteConfirmation = false }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}