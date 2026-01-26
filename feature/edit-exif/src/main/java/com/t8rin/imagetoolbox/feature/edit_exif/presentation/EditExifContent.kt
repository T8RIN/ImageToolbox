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

package com.t8rin.imagetoolbox.feature.edit_exif.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.data.utils.safeAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exif
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberFileSize
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.FormatExifWarning
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.sheets.EditExifSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.edit_exif.presentation.screenLogic.EditExifComponent

@Composable
fun EditExifContent(
    component: EditExifComponent,
) {
    val essentials = rememberLocalEssentials()

    AutoContentBasedColors(component.uri)

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val imagePicker = rememberImagePicker(onSuccess = component::setUri)
    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = component.initialUri != null
    )

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showZoomSheet by rememberSaveable { mutableStateOf(false) }

    ZoomModalSheet(
        data = component.uri,
        visible = showZoomSheet,
        onDismiss = {
            showZoomSheet = false
        }
    )

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    var showEditExifDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.edit_exif_screen),
                input = component.uri.takeIf { it != Uri.EMPTY },
                isLoading = component.isImageLoading,
                size = rememberFileSize(component.uri)
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (component.uri == Uri.EMPTY) {
                TopAppBarEmoji()
            }
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.uri != Uri.EMPTY
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.uri != Uri.EMPTY,
                onShare = {
                    component.shareBitmap(essentials::showConfetti)
                },
                onCopy = {
                    component.cacheCurrentImage(essentials::copyToClipboard)
                },
                onEdit = {
                    component.cacheCurrentImage { uri ->
                        editSheetData = listOf(uri)
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = { editSheetData = emptyList() },
                onNavigate = component.onNavigate
            )
        },
        imagePreview = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                var aspectRatio by remember {
                    mutableFloatStateOf(1f)
                }
                Picture(
                    model = component.uri,
                    modifier = Modifier
                        .container(MaterialTheme.shapes.medium)
                        .aspectRatio(aspectRatio),
                    onSuccess = {
                        aspectRatio = it.result.image.toBitmap().safeAspectRatio
                    },
                    isLoadingFromDifferentPlace = component.isImageLoading,
                    shape = MaterialTheme.shapes.medium,
                    contentScale = ContentScale.FillBounds
                )
                if (component.isImageLoading) EnhancedLoadingIndicator()
            }
        },
        controls = {
            PreferenceItem(
                onClick = {
                    showEditExifDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.edit_exif),
                subtitle = stringResource(R.string.edit_exif_tag),
                shape = ShapeDefaults.extraLarge,
                enabled = component.imageFormat.canWriteExif,
                onDisabledClick = {
                    essentials.showToast(
                        essentials.getString(
                            R.string.image_exif_warning,
                            component.imageFormat.title
                        )
                    )
                },
                startIcon = Icons.Rounded.Exif,
                endIcon = Icons.Rounded.MiniEdit
            )
            Spacer(Modifier.height(8.dp))
            FormatExifWarning(component.imageFormat)
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uri == Uri.EMPTY,
                onSecondaryButtonClick = pickImage,
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                },
                onPrimaryButtonClick = {
                    saveBitmap(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmap
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.uri != Uri.EMPTY,
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        }
    )

    EditExifSheet(
        visible = showEditExifDialog,
        onDismiss = {
            showEditExifDialog = false
        },
        exif = component.exif,
        onClearExif = component::clearExif,
        onUpdateTag = component::updateExifByTag,
        onRemoveTag = component::removeExifTag
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}