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

package com.t8rin.imagetoolbox.core.ui.widget.dialogs

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreateNewFolder
import androidx.compose.material.icons.outlined.DriveFileRenameOutline
import androidx.compose.material.icons.outlined.SaveAs
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.FileReplace
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFolderPicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.toUiPath
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealDirection
import com.t8rin.imagetoolbox.core.ui.widget.other.RevealValue
import com.t8rin.imagetoolbox.core.ui.widget.other.SwipeToReveal
import com.t8rin.imagetoolbox.core.ui.widget.other.rememberRevealState
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlinx.coroutines.launch


@Composable
fun OneTimeSaveLocationSelectionDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onSaveRequest: ((String?) -> Unit)?,
    formatForFilenameSelection: ImageFormat? = null
) {
    val settingsState = LocalSettingsState.current
    val settingsInteractor = LocalSimpleSettingsInteractor.current
    val essentials = rememberLocalEssentials()
    var tempSelectedSaveFolderUri by rememberSaveable(visible) {
        mutableStateOf(settingsState.saveFolderUri?.toString())
    }
    var selectedSaveFolderUri by rememberSaveable(visible) {
        mutableStateOf(settingsState.saveFolderUri?.toString())
    }
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        confirmButton = {
            onSaveRequest?.let {
                EnhancedButton(
                    onClick = {
                        onDismiss()
                        onSaveRequest(selectedSaveFolderUri)
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
        },
        dismissButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(text = stringResource(id = R.string.close))
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.SaveAs,
                contentDescription = stringResource(id = R.string.folder)
            )
        },
        title = {
            Text(text = stringResource(id = R.string.folder))
        },
        text = {
            val data by remember(settingsState.oneTimeSaveLocations, tempSelectedSaveFolderUri) {
                derivedStateOf {
                    settingsState.oneTimeSaveLocations.plus(
                        tempSelectedSaveFolderUri?.let {
                            OneTimeSaveLocation(
                                uri = it,
                                date = null,
                                count = 0
                            )
                        }
                    ).plus(
                        settingsState.saveFolderUri?.toString()?.let {
                            OneTimeSaveLocation(
                                uri = it,
                                date = null,
                                count = 0
                            )
                        }
                    ).distinctBy { it?.uri }
                }
            }

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fadingEdges(
                        scrollableState = scrollState,
                        isVertical = true
                    )
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(4.dp))
                data.forEachIndexed { index, item ->
                    val title by remember(item) {
                        derivedStateOf {
                            val default = essentials.getString(R.string.default_folder)
                            item?.uri?.toUri()?.toUiPath(default = default) ?: default
                        }
                    }
                    val subtitle by remember(item) {
                        derivedStateOf {
                            if (item?.uri == settingsState.saveFolderUri?.toString()) {
                                essentials.getString(R.string.default_value)
                            } else {
                                val time = item?.date?.let {
                                    timestamp(
                                        format = "dd MMMM yyyy",
                                        date = it
                                    )
                                } ?: ""

                                "$time ${
                                    item?.count?.takeIf { it > 0 }
                                        ?.let { "($it)" } ?: ""
                                }".trim()
                                    .takeIf { it.isNotEmpty() }
                            }
                        }
                    }
                    val selected = selectedSaveFolderUri == item?.uri
                    val state = rememberRevealState()
                    val interactionSource = remember {
                        MutableInteractionSource()
                    }
                    val isDragged by interactionSource.collectIsDraggedAsState()
                    val shape = ShapeDefaults.byIndex(
                        index = index,
                        size = data.size + 1,
                        forceDefault = isDragged
                    )
                    val canDeleteItem by remember(item, settingsState) {
                        derivedStateOf {
                            item != null && item in settingsState.oneTimeSaveLocations
                        }
                    }

                    SwipeToReveal(
                        state = state,
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
                                        essentials.launch {
                                            state.animateTo(RevealValue.Default)
                                        }
                                        essentials.launch {
                                            settingsInteractor.setOneTimeSaveLocations((settingsState.oneTimeSaveLocations - item).filterNotNull())
                                            if (item?.uri == selectedSaveFolderUri) {
                                                selectedSaveFolderUri = null
                                                tempSelectedSaveFolderUri = null
                                            }
                                        }
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
                        directions = setOf(RevealDirection.EndToStart),
                        swipeableContent = {
                            PreferenceItem(
                                title = title,
                                subtitle = subtitle,
                                shape = shape,
                                titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
                                onClick = {
                                    if (item != null) {
                                        tempSelectedSaveFolderUri = item.uri
                                    }
                                    selectedSaveFolderUri = item?.uri
                                },
                                onLongClick = if (item != null) {
                                    {
                                        essentials.launch {
                                            state.animateTo(RevealValue.FullyRevealedStart)
                                        }
                                    }
                                } else null,
                                enabled = settingsState.filenameBehavior !is FilenameBehavior.Overwrite,
                                startIconTransitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                startIcon = if (selected) {
                                    Icons.Rounded.Folder
                                } else Icons.Rounded.FolderOpen,
                                endIcon = if (selected) Icons.Rounded.RadioButtonChecked
                                else Icons.Rounded.RadioButtonUnchecked,
                                containerColor = takeColorFromScheme {
                                    if (selected) surface
                                    else surfaceContainer
                                }
                            )
                        },
                        enableSwipe = canDeleteItem && settingsState.filenameBehavior !is FilenameBehavior.Overwrite,
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .fadingEdges(
                                scrollableState = null,
                                length = 4.dp
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                val currentFolderUri = selectedSaveFolderUri?.toUri() ?: settingsState.saveFolderUri
                val launcher = rememberFolderPicker(
                    onSuccess = { uri ->
                        tempSelectedSaveFolderUri = uri.toString()
                        selectedSaveFolderUri = uri.toString()
                    }
                )

                PreferenceItem(
                    title = stringResource(id = R.string.add_new_folder),
                    startIcon = Icons.Outlined.CreateNewFolder,
                    shape = ShapeDefaults.bottom,
                    titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
                    onClick = {
                        launcher.pickFolder(currentFolderUri)
                    },
                    enabled = settingsState.filenameBehavior !is FilenameBehavior.Overwrite,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )

                if (formatForFilenameSelection != null) {
                    val createLauncher = rememberFileCreator(
                        mimeType = formatForFilenameSelection.mimeType,
                        onSuccess = { uri ->
                            onSaveRequest?.invoke(uri.toString())
                            onDismiss()
                        }
                    )

                    val imageString = stringResource(R.string.image)
                    PreferenceItem(
                        title = stringResource(id = R.string.custom_filename),
                        subtitle = stringResource(id = R.string.custom_filename_sub),
                        startIcon = Icons.Outlined.DriveFileRenameOutline,
                        shape = ShapeDefaults.default,
                        titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
                        enabled = settingsState.filenameBehavior !is FilenameBehavior.Overwrite,
                        onClick = {
                            createLauncher.make("$imageString.${formatForFilenameSelection.extension}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }

                PreferenceRowSwitch(
                    title = stringResource(id = R.string.overwrite_files),
                    subtitle = stringResource(id = R.string.overwrite_files_sub_short),
                    startIcon = Icons.Outlined.FileReplace,
                    enabled = settingsState.filenameBehavior is FilenameBehavior.Overwrite || settingsState.filenameBehavior is FilenameBehavior.None,
                    shape = ShapeDefaults.default,
                    titleFontStyle = PreferenceItemDefaults.TitleFontStyleSmall,
                    checked = settingsState.filenameBehavior is FilenameBehavior.Overwrite,
                    onClick = {
                        essentials.launch { settingsInteractor.toggleOverwriteFiles() }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            }
        }
    )
}