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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePager
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.duplicate_finder.domain.model.DuplicateType
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.components.DuplicateFinderControls
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.components.DuplicateSelectionActions
import com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.screenLogic.DuplicateFinderComponent

@Composable
fun DuplicateFinderContent(
    component: DuplicateFinderComponent
) {
    val imagePicker = rememberImagePicker(onSuccess = component::addUris)
    val pickImages = imagePicker::pickImage
    val isPortrait by isPortraitOrientationAsState()
    val context = LocalContext.current

    AutoFilePicker(
        onAutoPick = pickImages,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }
    var previewUri by rememberSaveable { mutableStateOf<String?>(null) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = component.sourceUris.isEmpty(),
        onGoBack = {
            if (component.sourceUris.isEmpty()) component.onGoBack()
            else showExitDialog = true
        },
        title = {
            Text(
                text = stringResource(R.string.duplicate_finder),
                modifier = Modifier.marquee()
            )
        },
        actions = {},
        topAppBarPersistentActions = {
            if (component.groups.isNotEmpty()) {
                DuplicateSelectionActions(
                    selectedCount = component.selectedUris.size,
                    canSelectExact = remember(component.groups, component.selectedUris) {
                        component.groups.any { it.type == DuplicateType.Exact } && component.groups.any { group ->
                            group.type == DuplicateType.Exact && group.items.any { it.uri != group.recommendedUri && it.uri !in component.selectedUris }
                        }
                    },
                    onSelectExact = component::selectExactDuplicates,
                    onSelectAllExceptRecommended = component::selectAllExceptRecommended,
                    onClearSelection = component::clearSelection,
                    canSelectAll = remember(component.groups, component.selectedUris) {
                        component.groups.any { group ->
                            group.items.any { it.uri != group.recommendedUri && it.uri !in component.selectedUris }
                        }
                    }
                )
            } else if (component.sourceUris.isEmpty()) {
                TopAppBarEmoji()
            }
        },
        imagePreview = {},
        placeImagePreview = false,
        showImagePreviewAsStickyHeader = false,
        addHorizontalCutoutPaddingIfNoPreview = component.sourceUris.isNotEmpty(),
        placeControlsSeparately = true,
        controls = {
            DuplicateFinderControls(
                component = component,
                isPortrait = isPortrait,
                onPickImages = pickImages,
                onPreview = { previewUri = it }
            )
        },
        noDataControls = {
            ImageNotPickedWidget(
                onPickImage = pickImages,
                text = stringResource(R.string.pick_images_for_duplicates)
            )
        },
        buttons = {
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = component.sourceUris.isEmpty(),
                onSecondaryButtonClick = pickImages,
                onPrimaryButtonClick = { showDeleteConfirmation = true },
                primaryButtonIcon = Icons.Outlined.Delete,
                primaryButtonText = stringResource(R.string.delete_selected_duplicates),
                isPrimaryButtonEnabled = component.selectedUris.isNotEmpty() && !component.isDeleting,
                actions = {
                    AnimatedVisibility(component.selectedUris.isNotEmpty()) {
                        EnhancedChip(
                            selected = true,
                            onClick = null,
                            selectedColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.padding(8.dp),
                            contentPadding = PaddingValues(
                                vertical = 6.dp,
                                horizontal = 8.dp
                            )
                        ) {
                            Text(
                                stringResource(
                                    R.string.duplicate_selected_summary,
                                    component.selectedUris.size,
                                    rememberHumanFileSize(component.selectedSizeBytes)
                                )
                            )
                        }
                    }
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.sourceUris.isNotEmpty()
    )

    val progress = component.progress
    LoadingDialog(
        visible = component.isAnalyzing,
        progress = { progress?.fraction ?: 1f },
        loaderSize = 72.dp,
        canCancel = false,
        additionalContent = { size ->
            AutoSizeText(
                text = "${progress?.processed ?: 0} / ${progress?.total ?: component.sourceUris.size}",
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.width(size * 0.7f),
            )
        }
    )
    LoadingDialog(
        visible = component.isDeleting,
        canCancel = false
    )

    EnhancedAlertDialog(
        visible = showDeleteConfirmation,
        onDismissRequest = { showDeleteConfirmation = false },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        },
        title = { Text(stringResource(R.string.confirm_duplicate_deletion_title)) },
        text = {
            Text(stringResource(R.string.confirm_duplicate_deletion_text))
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showDeleteConfirmation = false
                    component.deleteSelected()
                }
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            EnhancedButton(
                onClick = { showDeleteConfirmation = false },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )

    ImagePager(
        visible = previewUri != null,
        selectedUri = previewUri?.toUri(),
        uris = component.previewUris,
        onNavigate = component.onNavigate,
        onUriSelected = { previewUri = it?.toString() },
        onShare = { context.shareUris(listOf(it)) },
        onDismiss = { previewUri = null }
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}