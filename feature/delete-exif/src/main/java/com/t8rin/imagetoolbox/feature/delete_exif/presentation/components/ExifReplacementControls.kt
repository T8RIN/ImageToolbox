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

package com.t8rin.imagetoolbox.feature.delete_exif.presentation.components

import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.Metadata
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.resources.icons.Shuffle
import com.t8rin.imagetoolbox.core.resources.icons.Tune
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.WandStars
import com.t8rin.imagetoolbox.core.ui.utils.helper.Clipboard
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.localizedName
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.StylePresetSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EditExifSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifLocation
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifMetering
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifReplacementPreset
import com.t8rin.imagetoolbox.feature.delete_exif.domain.model.ExifReplacementSettings
import com.t8rin.imagetoolbox.feature.delete_exif.presentation.screenLogic.DeleteExifComponent

@Composable
internal fun ExifReplacementControls(component: DeleteExifComponent) {
    var showEditor by rememberSaveable { mutableStateOf(false) }
    var showSettings by rememberSaveable { mutableStateOf(false) }
    var showPreview by rememberSaveable { mutableStateOf(false) }

    val isCustom = component.replacementPreset == ExifReplacementPreset.Custom

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DataSelector(
            value = component.replacementPreset,
            onValueChange = component::setReplacementPreset,
            entries = ExifReplacementPreset.entries,
            title = stringResource(R.string.exif_camera_profile),
            titleIcon = Icons.Rounded.WandStars,
            itemContentText = {
                when (it) {
                    ExifReplacementPreset.Custom -> stringResource(R.string.custom)
                    ExifReplacementPreset.Smartphone -> stringResource(R.string.exif_profile_smartphone)
                    ExifReplacementPreset.Dslr -> stringResource(R.string.exif_profile_dslr)
                    ExifReplacementPreset.Mirrorless -> stringResource(R.string.exif_profile_mirrorless)
                    ExifReplacementPreset.Compact -> stringResource(R.string.exif_profile_compact)
                    ExifReplacementPreset.ActionCamera -> stringResource(R.string.exif_profile_action_camera)
                    ExifReplacementPreset.RandomPhone -> stringResource(R.string.exif_profile_random_phone)
                    ExifReplacementPreset.RandomCamera -> stringResource(R.string.exif_profile_random_camera)
                    else -> it.phoneName.orEmpty()
                }
            },
            spanCount = 1,
            modifier = Modifier.fillMaxWidth(),
            shape = ShapeDefaults.extraLarge
        )

        val tags = component.generatedTags
        Box(modifier = Modifier.fillMaxWidth()) {
            val enter = fadeIn() + expandVertically(expandFrom = Alignment.Top)
            val exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            BoxAnimatedVisibility(
                visible = isCustom,
                enter = enter,
                exit = exit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExifTemplateControls(
                        component = component,
                        showEditor = showEditor,
                        onShowEditorChange = { showEditor = it }
                    )
                }
            }
            BoxAnimatedVisibility(
                visible = !isCustom,
                enter = enter,
                exit = exit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PreferenceItem(
                        title = stringResource(R.string.exif_generation_settings),
                        subtitle = component.replacementSettings.summary(),
                        startIcon = Icons.Rounded.Tune,
                        endIcon = Icons.Rounded.MiniEdit,
                        onClick = { showSettings = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.extraLarge
                    )
                    PreferenceItem(
                        title = stringResource(R.string.exif_generated_tags),
                        subtitle = listOfNotNull(
                            tags[MetadataTag.Model],
                            tags[MetadataTag.DatetimeOriginal]
                        )
                            .joinToString(" • "),
                        startIcon = Icons.Rounded.Visibility,
                        onClick = { showPreview = true },
                        enabled = component.canSave,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ShapeDefaults.extraLarge
                    )
                }
            }
        }
        if (!isCustom) {
            ExifGenerationSheet(
                visible = showSettings,
                onDismiss = { showSettings = false },
                component = component
            )
            GeneratedExifSheet(
                visible = showPreview,
                onDismiss = { showPreview = false },
                tags = tags,
                onRegenerate = component::regenerateTags,
                onEdit = {
                    showPreview = false
                    component.editGeneratedTags()
                    showEditor = true
                }
            )
        }
    }
}

@Composable
private fun ExifReplacementSettings.summary(): String {
    val date = captureTime?.let { "${it.toLocalDate()} ${it.toLocalTime()}" }
        ?: stringResource(R.string.exif_random_date)
    val location = when {
        location == ExifLocation.Manual && coordinates != null -> {
            "${latitude.trim()} / ${longitude.trim()}"
        }

        else -> stringResource(location.titleRes())
    }
    return listOfNotNull(
        stringResource(lighting.titleRes()),
        date,
        location,
        if (manualWhiteBalance) stringResource(R.string.exif_manual_white_balance) else null,
        if (metering != ExifMetering.MultiSegment) stringResource(metering.titleRes()) else null,
        if (!includeSoftware) stringResource(R.string.exif_no_software) else null,
        if (sameForBatch) stringResource(R.string.exif_same_for_batch) else null
    ).joinToString(" • ")
}

@Composable
private fun ExifTemplateControls(
    component: DeleteExifComponent,
    showEditor: Boolean,
    onShowEditorChange: (Boolean) -> Unit
) {
    val draft = component.customTags
    val templates = component.templates
    val editorMetadata = remember(showEditor) { Metadata(draft) }

    PreferenceItem(
        title = stringResource(R.string.edit_exif_template),
        subtitle = draft.size.takeIf { it > 0 }?.let {
            stringResource(R.string.exif_tag_count, it)
        },
        startIcon = Icons.Rounded.Exif,
        endIcon = Icons.Rounded.MiniEdit,
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.extraLarge,
        onClick = { onShowEditorChange(true) }
    )
    if (draft.isNotEmpty() || templates.isNotEmpty()) {
        StylePresetSelector(
            currentStyle = component.customTemplate,
            styles = templates,
            listStateKey = "EXIF_TEMPLATES",
            summary = { tags ->
                stringResource(R.string.exif_tag_count, tags.size)
            },
            onApply = component::applyTemplate,
            onSave = component::saveTemplate,
            onDelete = component::deleteTemplate,
            shape = ShapeDefaults.extraLarge,
            icon = Icons.Outlined.Loyalty,
            titleIcon = Icons.Rounded.Loyalty
        )
    }
    EditExifSheet(
        visible = showEditor,
        onDismiss = { onShowEditorChange(false) },
        exif = editorMetadata,
        onClearExif = component::clearCustomTags,
        onUpdateTag = component::updateCustomTag,
        onRemoveTag = component::removeCustomTag
    )
}

@Composable
private fun GeneratedExifSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    tags: Map<MetadataTag, String>,
    onRegenerate: () -> Unit,
    onEdit: () -> Unit
) {
    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        title = {
            TitleItem(
                text = stringResource(R.string.metadata),
                icon = Icons.Rounded.Exif
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = onEdit,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.edit))
            }
            Spacer(Modifier.width(4.dp))
            EnhancedButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            item {
                PreferenceItem(
                    title = stringResource(R.string.exif_shuffle_tags),
                    startIcon = Icons.Rounded.Shuffle,
                    onClick = onRegenerate,
                    shape = ShapeDefaults.extraLarge,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            itemsIndexed(
                tags.entries.toList(),
                key = { _, (tag, _) -> tag.key }) { index, (tag, value) ->
                PreferenceItem(
                    title = tag.localizedName,
                    subtitle = value,
                    subtitleFontStyle = PreferenceItemDefaults.SubtitleFontStyleMedium,
                    onClick = { Clipboard.copy(value) },
                    shape = ShapeDefaults.byIndex(index, tags.size),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}