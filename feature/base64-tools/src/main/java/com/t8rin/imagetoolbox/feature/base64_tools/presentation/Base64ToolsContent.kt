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

package com.t8rin.imagetoolbox.feature.base64_tools.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
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
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrokenImageAlt
import com.t8rin.imagetoolbox.core.ui.theme.takeUnless
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ZoomButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ZoomModalSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.utils.AutoContentBasedColors
import com.t8rin.imagetoolbox.feature.base64_tools.presentation.components.Base64ToolsTiles
import com.t8rin.imagetoolbox.feature.base64_tools.presentation.screenLogic.Base64ToolsComponent

@Composable
fun Base64ToolsContent(
    component: Base64ToolsComponent
) {
    AutoContentBasedColors(component.uri)

    val isPortrait by isPortraitOrientationAsState()

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val imagePicker = rememberImagePicker(onSuccess = component::setUri)
    val pickImage = imagePicker::pickImage

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.base_64_tools),
                input = component.uri,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = component.onGoBack,
        topAppBarPersistentActions = {
            if (component.uri == null) {
                TopAppBarEmoji()
            }
            var showZoomSheet by rememberSaveable { mutableStateOf(false) }
            ZoomButton(
                onClick = { showZoomSheet = true },
                visible = component.uri != null
            )
            ZoomModalSheet(
                data = component.base64String,
                visible = showZoomSheet,
                onDismiss = {
                    showZoomSheet = false
                }
            )
        },
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = component.uri != null,
                onShare = {
                    component.shareBitmap(showConfetti)
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
            AnimatedContent(component.base64String.isEmpty()) { isEmpty ->
                if (isEmpty) {
                    ImageNotPickedWidget(
                        onPickImage = pickImage,
                        modifier = Modifier.padding(20.dp),
                        text = stringResource(R.string.pick_image_or_base64),
                        containerColor = MaterialTheme
                            .colorScheme
                            .surfaceContainerLowest
                            .takeUnless(isPortrait)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .container()
                            .padding(4.dp)
                            .animateContentSizeNoClip(
                                alignment = Alignment.Center
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        var aspectRatio by remember {
                            mutableFloatStateOf(1f)
                        }
                        Picture(
                            model = component.base64String,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.aspectRatio(aspectRatio),
                            onSuccess = {
                                aspectRatio = it.result.image.toBitmap().safeAspectRatio
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Rounded.BrokenImageAlt,
                                    contentDescription = null,
                                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                )
                            },
                            shape = MaterialTheme.shapes.medium,
                            isLoadingFromDifferentPlace = component.isImageLoading
                        )
                    }
                }
            }
        },
        showImagePreviewAsStickyHeader = component.base64String.isNotEmpty(),
        controls = {
            if (isPortrait) Spacer(Modifier.height(8.dp))
            Base64ToolsTiles(component)
            Spacer(Modifier.height(8.dp))
            if (component.uri != null) {
                if (component.imageFormat.canChangeCompressionValue) {
                    Spacer(Modifier.height(8.dp))
                }
                QualitySelector(
                    imageFormat = component.imageFormat,
                    quality = component.quality,
                    onQualityChange = component::setQuality
                )
                Spacer(Modifier.height(8.dp))
                ImageFormatSelector(
                    value = component.imageFormat,
                    quality = component.quality,
                    onValueChange = component::setImageFormat
                )
            } else {
                InfoContainer(
                    modifier = Modifier.padding(8.dp),
                    text = stringResource(R.string.base_64_tips),
                )
            }
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.base64String.isEmpty() && isPortrait,
                isPrimaryButtonVisible = isPortrait || component.base64String.isNotEmpty(),
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
                onSaveRequest = saveBitmap,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )
}