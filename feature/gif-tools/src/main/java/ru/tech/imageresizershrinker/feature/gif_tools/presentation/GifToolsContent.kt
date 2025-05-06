/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.gif_tools.presentation

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
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFileCreator
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFilePicker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isGif
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.components.GifToolsControls
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.components.GifToolsImagePreview
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.components.GifToolsNoDataControls
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.components.GifToolsTopAppBarActions
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
fun GifToolsContent(
    component: GifToolsComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val imagePicker = rememberImagePicker(onSuccess = component::setImageUris)

    val pickSingleGifLauncher = rememberFilePicker(
        mimeTypes = listOf("image/gif")
    ) { uri: Uri ->
        if (uri.isGif(context)) {
            component.setGifUri(uri)
        } else {
            essentials.showToast(
                message = context.getString(R.string.select_gif_image_to_start),
                icon = Icons.Rounded.Gif
            )
        }
    }

    val pickMultipleGifToJxlLauncher = rememberFilePicker(
        mimeTypes = listOf("image/gif")
    ) { list: List<Uri> ->
        list.filter {
            it.isGif(context)
        }.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_gif_image_to_start),
                    icon = Icons.Filled.Gif
                )
            } else {
                component.setType(
                    Screen.GifTools.Type.GifToJxl(uris)
                )
            }
        }
    }

    val pickMultipleGifToWebpLauncher = rememberFilePicker(
        mimeTypes = listOf("image/gif")
    ) { list: List<Uri> ->
        list.filter {
            it.isGif(context)
        }.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_gif_image_to_start),
                    icon = Icons.Filled.Gif
                )
            } else {
                component.setType(
                    Screen.GifTools.Type.GifToWebp(uris)
                )
            }
        }
    }

    val addGifsToJxlLauncher = rememberFilePicker(
        mimeTypes = listOf("image/gif")
    ) { list: List<Uri> ->
        list.filter {
            it.isGif(context)
        }.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_gif_image_to_start),
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

    val addGifsToWebpLauncher = rememberFilePicker(
        mimeTypes = listOf("image/gif")
    ) { list: List<Uri> ->
        list.filter {
            it.isGif(context)
        }.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_gif_image_to_start),
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

    val saveGifLauncher = rememberFileCreator(
        mimeType = "image/gif",
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
        buttons = {
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
                    if (isPortrait) it()
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