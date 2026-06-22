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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
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
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Loyalty
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.ui.utils.ImageExportProfilesHolder
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem

@Composable
fun ImageExportProfileSelector(
    profiles: List<ImageExportProfile>,
    selectedProfile: ImageExportProfile?,
    imageInfo: ImageInfo,
    preset: Preset,
    imageExportProfilesHolder: ImageExportProfilesHolder,
    modifier: Modifier = Modifier
) {
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var profileToDelete by remember { mutableStateOf<ImageExportProfile?>(null) }
    val importPicker = rememberFilePicker(
        mimeType = MimeType.All,
        onSuccess = imageExportProfilesHolder::importProfile
    )

    EnhancedChip(
        selected = selectedProfile != null,
        onClick = { showSheet = true },
        selectedColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Outlined.Loyalty,
            contentDescription = stringResource(R.string.export_profiles)
        )
    }

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        title = {
            TitleItem(
                icon = Icons.Rounded.Loyalty,
                text = stringResource(R.string.export_profiles)
            )
        },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = importPicker::pickFile,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.UploadFile,
                        contentDescription = stringResource(R.string.import_word)
                    )
                }
                EnhancedButton(
                    onClick = {
                        showSheet = false
                    }
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(12.dp),
            modifier = Modifier
                .animateContentSizeNoClip()
                .clearFocusOnTap()
        ) {
            if (selectedProfile == null || profiles.isEmpty()) {
                item("AddImagePresetBlock") {
                    AddImagePresetBlock(
                        preset = preset,
                        imageInfo = imageInfo,
                        onSave = imageExportProfilesHolder::saveProfile,
                        modifier = Modifier
                            .padding(
                                bottom = 4.dp
                            )
                            .animateItem()
                    )
                }
            }
            itemsIndexed(
                items = profiles,
                key = { _, item -> item.name }
            ) { index, item ->
                ImagePresetItem(
                    index = index,
                    profilesCount = profiles.size,
                    item = item,
                    selected = item == selectedProfile,
                    onApplyProfile = imageExportProfilesHolder::applyProfile,
                    onExportProfile = imageExportProfilesHolder::exportProfile,
                    onShareProfile = imageExportProfilesHolder::shareProfile,
                    onWantDelete = { profileToDelete = it }
                )
            }
        }
    }

    EnhancedAlertDialog(
        visible = profileToDelete != null,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.delete_export_profile))
        },
        text = {
            Text(
                stringResource(
                    R.string.delete_export_profile_sub,
                    profileToDelete?.name ?: ""
                )
            )
        },
        onDismissRequest = { profileToDelete = null },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    profileToDelete?.let(imageExportProfilesHolder::deleteProfile)
                    profileToDelete = null
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { profileToDelete = null }
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        placeAboveAll = true
    )
}