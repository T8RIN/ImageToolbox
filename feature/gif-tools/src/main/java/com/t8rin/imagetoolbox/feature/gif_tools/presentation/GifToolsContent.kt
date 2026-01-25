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

package com.t8rin.imagetoolbox.feature.gif_tools.presentation

import android.net.Uri
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.rounded.Gif
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFrames
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isGif
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.components.GifToolsControls
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.components.GifToolsImagePreview
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.components.GifToolsNoDataControls
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.components.GifToolsTopAppBarActions
import com.t8rin.imagetoolbox.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
fun GifToolsContent(
    component: GifToolsComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val imagePicker = rememberImagePicker(onSuccess = component::setImageUris)

    val pickSingleGifLauncher = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { uri: Uri ->
            if (uri.isGif()) {
                component.setGifUri(uri)
            } else {
                essentials.showToast(
                    message = essentials.getString(R.string.select_gif_image_to_start),
                    icon = Icons.Rounded.Gif
                )
            }
        }
    )

    val pickMultipleGifToJxlLauncher = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { list: List<Uri> ->
            list.filter {
                it.isGif()
            }.let { uris ->
                if (uris.isEmpty()) {
                    essentials.showToast(
                        message = essentials.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Gif
                    )
                } else {
                    component.setType(
                        Screen.GifTools.Type.GifToJxl(uris)
                    )
                }
            }
        }
    )

    val pickMultipleGifToWebpLauncher = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { list: List<Uri> ->
            list.filter {
                it.isGif()
            }.let { uris ->
                if (uris.isEmpty()) {
                    essentials.showToast(
                        message = essentials.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Gif
                    )
                } else {
                    component.setType(
                        Screen.GifTools.Type.GifToWebp(uris)
                    )
                }
            }
        }
    )

    val addGifsToJxlLauncher = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { list: List<Uri> ->
            list.filter {
                it.isGif()
            }.let { uris ->
                if (uris.isEmpty()) {
                    essentials.showToast(
                        message = essentials.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Gif
                    )
                } else {
                    component.setType(
                        Screen.GifTools.Type.GifToJxl(
                            (component.type as? Screen.GifTools.Type.GifToJxl)?.gifUris?.plus(uris)
                                ?.distinct()
                        )
                    )
                }
            }
        }
    )

    val addGifsToWebpLauncher = rememberFilePicker(
        mimeType = MimeType.Gif,
        onSuccess = { list: List<Uri> ->
            list.filter {
                it.isGif()
            }.let { uris ->
                if (uris.isEmpty()) {
                    essentials.showToast(
                        message = essentials.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Gif
                    )
                } else {
                    component.setType(
                        Screen.GifTools.Type.GifToWebp(
                            (component.type as? Screen.GifTools.Type.GifToWebp)?.gifUris?.plus(uris)
                                ?.distinct()
                        )
                    )
                }
            }
        }
    )

    val saveGifLauncher = rememberFileCreator(
        mimeType = MimeType.Gif,
        onSuccess = { uri ->
            component.saveGifTo(
                uri = uri,
                onResult = essentials::parseSaveResult
            )
        }
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = when (val type = component.type) {
                    null -> stringResource(R.string.gif_tools)
                    else -> stringResource(type.title)
                },
                input = component.type,
                isLoading = component.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            GifToolsTopAppBarActions(component)
        },
        actions = {
            ShareButton(
                enabled = !component.isLoading && component.type != null,
                onShare = {
                    component.performSharing(showConfetti)
                }
            )
        },
        imagePreview = {
            GifToolsImagePreview(
                component = component,
                onAddGifsToJxl = addGifsToJxlLauncher::pickFile,
                onAddGifsToWebp = addGifsToWebpLauncher::pickFile
            )
        },
        placeImagePreview = component.type !is Screen.GifTools.Type.ImageToGif,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            GifToolsControls(component)
        },
        contentPadding = animateDpAsState(
            if (component.type == null) 12.dp
            else 20.dp
        ).value,
        buttons = { actions ->
            val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
                component.saveBitmaps(
                    oneTimeSaveLocationUri = it,
                    onGifSaveResult = saveGifLauncher::make,
                    onResult = essentials::parseSaveResults
                )
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }

            BottomButtonsBlock(
                isNoData = component.type == null,
                onSecondaryButtonClick = {
                    when (component.type) {
                        is Screen.GifTools.Type.GifToImage -> pickSingleGifLauncher.pickFile()
                        is Screen.GifTools.Type.GifToJxl -> pickMultipleGifToJxlLauncher.pickFile()
                        is Screen.GifTools.Type.GifToWebp -> pickMultipleGifToWebpLauncher.pickFile()
                        else -> imagePicker.pickImage()
                    }
                },
                isPrimaryButtonVisible = component.canSave,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    if (component.type is Screen.GifTools.Type.ImageToGif) {
                        saveBitmaps(null)
                    } else showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                showNullDataButtonAsContainer = true,
                onSecondaryButtonLongClick = if (component.type is Screen.GifTools.Type.ImageToGif || component.type == null) {
                    {
                        showOneTimeImagePickingDialog = true
                    }
                } else null
            )

            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        insetsForNoData = WindowInsets(0),
        noDataControls = {
            GifToolsNoDataControls(
                onClickType = { type ->
                    when (type) {
                        is Screen.GifTools.Type.GifToImage -> pickSingleGifLauncher.pickFile()
                        is Screen.GifTools.Type.GifToJxl -> pickMultipleGifToJxlLauncher.pickFile()
                        is Screen.GifTools.Type.GifToWebp -> pickMultipleGifToWebpLauncher.pickFile()
                        is Screen.GifTools.Type.ImageToGif -> imagePicker.pickImage()
                    }
                }
            )
        },
        canShowScreenData = component.type != null
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )

    ExitWithoutSavingDialog(
        onExit = component::resetState,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

private val GifToolsComponent.canSave: Boolean
    get() = (gifFrames == ImageFrames.All)
        .or(type is Screen.GifTools.Type.ImageToGif)
        .or((gifFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)