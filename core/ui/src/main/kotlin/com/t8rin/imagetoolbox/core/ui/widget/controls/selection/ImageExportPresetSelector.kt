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

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ImagePreset
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Add
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun ImagePresetSelector(
    profiles: List<ImagePreset>,
    imageInfo: ImageInfo,
    preset: Preset,
    enabled: Boolean,
    onApplyProfile: (ImagePreset) -> Unit,
    onSaveProfile: (String) -> Unit,
    onDeleteProfile: (ImagePreset) -> Unit,
    onExportProfile: (ImagePreset) -> Unit,
    onImportProfile: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = onImportProfile
    )

    PreferenceItemOverload(
        modifier = modifier.fillMaxWidth(),
        title = stringResource(R.string.export_profiles),
        startIcon = {
            Icon(
                imageVector = Icons.Rounded.Bookmark,
                contentDescription = null
            )
        },
        endIcon = {
            EnhancedIconButton(
                onClick = { showSheet = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = stringResource(R.string.add)
                )
            }
        },
        bottomContent = if (profiles.isNotEmpty()) {
            {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 14.dp
                    )
                ) {
                    items(profiles, key = { it.name }) { item ->
                        ImagePresetChip(
                            preset = item,
                            enabled = enabled,
                            onClick = {
                                onApplyProfile(item)
                            }
                        )
                    }
                }
            }
        } else null,
        shape = ShapeDefaults.large
    )

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it }
    ) {
        TitleItem(
            icon = Icons.Rounded.Bookmark,
            text = stringResource(R.string.export_profiles),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            endContent = {
                EnhancedIconButton(
                    onClick = importPicker::pickFile,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = stringResource(R.string.import_word)
                    )
                }
            }
        )

        SaveImagePresetBlock(
            enabled = enabled,
            preset = preset,
            imageInfo = imageInfo,
            onSave = { name ->
                showSheet = false
                onSaveProfile(name)
            }
        )

        if (profiles.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(profiles, key = { it.name }) { item ->
                    PreferenceItemOverload(
                        title = item.name,
                        subtitle = item.subtitle,
                        onClick = {
                            showSheet = false
                            onApplyProfile(item)
                        },
                        endIcon = {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                EnhancedIconButton(onClick = { onExportProfile(item) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.DownloadFile,
                                        contentDescription = stringResource(R.string.export)
                                    )
                                }
                                EnhancedIconButton(onClick = { onDeleteProfile(item) }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Delete,
                                        contentDescription = stringResource(R.string.delete)
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SaveImagePresetBlock(
    enabled: Boolean,
    preset: Preset,
    imageInfo: ImageInfo,
    onSave: (String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    val canSave = enabled && name.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RoundedTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(R.string.name),
            startIcon = Icons.Rounded.Bookmark,
            singleLine = true
        )
        Text(
            text = imageInfo.summary(preset),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            EnhancedButton(
                onClick = {
                    onSave(name)
                    name = ""
                },
                enabled = canSave
            ) {
                Text(stringResource(R.string.save_export_profile))
            }
        }
    }
}

@Composable
private fun ImagePresetChip(
    preset: ImagePreset,
    enabled: Boolean,
    onClick: () -> Unit
) {
    EnhancedChip(
        selected = false,
        onClick = onClick.takeIf { enabled },
        modifier = Modifier.widthIn(min = 132.dp, max = 220.dp),
        selectedColor = MaterialTheme.colorScheme.primaryContainer,
        contentPadding = PaddingValues(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f, false)) {
                Text(
                    text = preset.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private val ImagePreset.subtitle: String
    get() = "${resolutionLabel()}, ${imageInfo.imageFormat.title}, ${imageInfo.quality.qualityValue}%"

private fun ImagePreset.resolutionLabel(): String {
    return if (preset.isEmpty()) {
        "${imageInfo.width}x${imageInfo.height}"
    } else {
        preset.asString()
    }
}

private fun ImageInfo.summary(preset: Preset): String {
    val size = if (preset.isEmpty()) {
        "${width}x$height"
    } else {
        preset.asString()
    }
    return "$size, ${imageFormat.title}"
}
