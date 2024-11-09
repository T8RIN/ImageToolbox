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

package ru.tech.imageresizershrinker.feature.jxl_tools.presentation

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Jxl
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagesPreviewWithSelection
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.components.AnimatedJxlParamsSelector
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent

@Composable
fun JxlToolsContent(
    onGoBack: () -> Unit,
    component: JxlToolsComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val onError: (Throwable) -> Unit = essentials::showErrorToast

    val pickJpegsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            component.setType(
                type = Screen.JxlTools.Type.JpegToJxl(uris),
                onError = onError
            )
        }
    }

    val pickJxlsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isJxl(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_jxl_image_to_start),
                    icon = Icons.Filled.Jxl
                )
            } else {
                component.setType(
                    type = Screen.JxlTools.Type.JxlToJpeg(uris),
                    onError = onError
                )
            }
        }
    }

    val pickSingleJxlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.takeIf { it.isJxl(context) }?.let {
            component.setType(
                type = Screen.JxlTools.Type.JxlToImage(it),
                onError = onError
            )
        } ?: essentials.showToast(
            message = context.getString(R.string.select_jxl_image_to_start),
            icon = Icons.Filled.Jxl
        )
    }

    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            component.setType(
                type = Screen.JxlTools.Type.ImageToJxl(uris),
                onError = onError
            )
        }
    }

    val addImagesImagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            component.setType(
                type = Screen.JxlTools.Type.ImageToJxl(
                    (component.type as? Screen.JxlTools.Type.ImageToJxl)?.imageUris?.plus(uris)
                        ?.distinct()
                ),
                onError = onError
            )
        }
    }

    val addJpegsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let { uris ->
            component.setType(
                type = (component.type as? Screen.JxlTools.Type.JpegToJxl)?.let {
                    it.copy(it.jpegImageUris?.plus(uris)?.distinct())
                },
                onError = onError
            )
        }
    }

    val addJxlsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isJxl(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                    message = context.getString(R.string.select_jxl_image_to_start),
                    icon = Icons.Filled.Jxl
                )
            } else {
                component.setType(
                    type = (component.type as? Screen.JxlTools.Type.JxlToJpeg)?.let {
                        it.copy(it.jxlImageUris?.plus(uris)?.distinct())
                    },
                    onError = onError
                )
            }
        }
    }

    fun pickImage(type: Screen.JxlTools.Type? = null) {
        runCatching {
            when (type ?: component.type) {
                is Screen.JxlTools.Type.ImageToJxl -> imagePicker.pickImage()
                is Screen.JxlTools.Type.JpegToJxl -> pickJpegsLauncher.launch(
                    arrayOf(
                        "image/jpeg",
                        "image/jpg"
                    )
                )

                is Screen.JxlTools.Type.JxlToImage -> pickSingleJxlLauncher.launch(arrayOf("*/*"))
                else -> pickJxlsLauncher.launch(arrayOf("*/*"))
            }
        }.onFailure {
            essentials.showActivateFilesToast()
        }
    }

    val addImages: () -> Unit = {
        runCatching {
            when (component.type) {
                is Screen.JxlTools.Type.ImageToJxl -> addImagesImagePicker.pickImage()
                is Screen.JxlTools.Type.JpegToJxl -> addJpegsLauncher.launch(
                    arrayOf(
                        "image/jpeg",
                        "image/jpg"
                    )
                )

                else -> addJxlsLauncher.launch(arrayOf("*/*"))
            }
        }.onFailure {
            essentials.showActivateFilesToast()
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else onGoBack()
    }

    val isPortrait by isPortraitOrientationAsState()

    val uris = when (val type = component.type) {
        is Screen.JxlTools.Type.JpegToJxl -> type.jpegImageUris
        is Screen.JxlTools.Type.JxlToJpeg -> type.jxlImageUris
        is Screen.JxlTools.Type.ImageToJxl -> type.imageUris
        is Screen.JxlTools.Type.JxlToImage -> listOfNotNull(type.jxlUri)
        null -> null
    } ?: emptyList()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = when (component.type) {
                    is Screen.JxlTools.Type.JpegToJxl -> {
                        stringResource(R.string.jpeg_type_to_jxl)
                    }

                    is Screen.JxlTools.Type.JxlToJpeg -> {
                        stringResource(R.string.jxl_type_to_jpeg)
                    }

                    is Screen.JxlTools.Type.ImageToJxl -> {
                        stringResource(R.string.jxl_type_to_jxl)
                    }

                    is Screen.JxlTools.Type.JxlToImage -> {
                        stringResource(R.string.jxl_type_to_images)
                    }

                    null -> stringResource(R.string.jxl_tools)
                },
                input = component.type,
                isLoading = component.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            val isJxlToImage = component.type is Screen.JxlTools.Type.JxlToImage
            if (component.type == null) TopAppBarEmoji()
            else if (!isJxlToImage) {
                ShareButton(
                    enabled = !component.isLoading && component.type != null,
                    onShare = {
                        component.performSharing(
                            onError = onError,
                            onComplete = showConfetti
                        )
                    }
                )
            }
            val pagesSize by remember(component.imageFrames, component.convertedImageUris) {
                derivedStateOf {
                    component.imageFrames.getFramePositions(component.convertedImageUris.size).size
                }
            }
            AnimatedVisibility(
                visible = isJxlToImage && pagesSize != component.convertedImageUris.size,
                enter = fadeIn() + scaleIn() + expandHorizontally(),
                exit = fadeOut() + scaleOut() + shrinkHorizontally()
            ) {
                EnhancedIconButton(
                    onClick = component::selectAllConvertedImages
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SelectAll,
                        contentDescription = "Select All"
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier
                    .padding(8.dp)
                    .container(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        resultPadding = 0.dp
                    ),
                visible = isJxlToImage && pagesSize != 0
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    pagesSize.takeIf { it != 0 }?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = it.toString(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    EnhancedIconButton(
                        onClick = component::clearConvertedImagesSelection
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            }
        },
        actions = {
            if (component.type is Screen.JxlTools.Type.JxlToImage) {
                ShareButton(
                    enabled = !component.isLoading && component.type != null,
                    onShare = {
                        component.performSharing(
                            onError = onError,
                            onComplete = showConfetti
                        )
                    }
                )
            }
        },
        imagePreview = {
            AnimatedContent(
                targetState = component.isLoading to component.type
            ) { (loading, type) ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if (loading) {
                        Modifier.padding(32.dp)
                    } else Modifier
                ) {
                    if (loading || type == null) {
                        LoadingIndicator()
                    } else {
                        when (type) {
                            is Screen.JxlTools.Type.JxlToImage -> {
                                ImagesPreviewWithSelection(
                                    imageUris = component.convertedImageUris,
                                    imageFrames = component.imageFrames,
                                    onFrameSelectionChange = component::updateJxlFrames,
                                    isPortrait = isPortrait,
                                    isLoadingImages = component.isLoadingJxlImages
                                )
                            }

                            is Screen.JxlTools.Type.ImageToJxl -> {
                                ImageReorderCarousel(
                                    images = uris,
                                    modifier = Modifier
                                        .padding(top = if (isPortrait) 24.dp else 0.dp)
                                        .container(
                                            shape = RoundedCornerShape(size = 24.dp),
                                            color = if (isPortrait) {
                                                Color.Unspecified
                                            } else MaterialTheme.colorScheme.surface
                                        ),
                                    onReorder = {
                                        component.setType(
                                            Screen.JxlTools.Type.ImageToJxl(it)
                                        )
                                    },
                                    onNeedToAddImage = addImages,
                                    onNeedToRemoveImageAt = {
                                        component.setType(
                                            Screen.JxlTools.Type.ImageToJxl(
                                                (component.type as Screen.JxlTools.Type.ImageToJxl)
                                                    .imageUris?.toMutableList()
                                                    ?.apply {
                                                        removeAt(it)
                                                    }
                                            )
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            else -> Unit
                        }
                    }
                }
            }
        },
        placeImagePreview = component.type is Screen.JxlTools.Type.JxlToImage
                || component.type is Screen.JxlTools.Type.ImageToJxl,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            when (component.type) {
                is Screen.JxlTools.Type.JxlToImage -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageFormatSelector(
                        value = component.imageFormat,
                        onValueChange = component::setImageFormat
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QualitySelector(
                        imageFormat = component.imageFormat,
                        enabled = true,
                        quality = component.params.quality,
                        onQualityChange = {
                            component.updateParams(
                                component.params.copy(
                                    quality = it
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is Screen.JxlTools.Type.JpegToJxl,
                is Screen.JxlTools.Type.JxlToJpeg -> {
                    UrisPreview(
                        modifier = Modifier
                            .padding(
                                vertical = if (isPortrait) 24.dp else 8.dp
                            ),
                        uris = uris,
                        isPortrait = true,
                        onRemoveUri = component::removeUri,
                        onAddUris = addImages
                    )
                }

                is Screen.JxlTools.Type.ImageToJxl -> {
                    AnimatedJxlParamsSelector(
                        value = component.params,
                        onValueChange = component::updateParams
                    )
                }

                else -> Unit
            }
        },
        contentPadding = animateDpAsState(
            if (component.type == null) 12.dp
            else 20.dp
        ).value,
        buttons = { actions ->
            val save: (oneTimeSaveLocationUri: String?) -> Unit = {
                component.save(
                    oneTimeSaveLocationUri = it,
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
                targetState = (component.type == null) to isPortrait,
                onSecondaryButtonClick = ::pickImage,
                isPrimaryButtonVisible = component.canSave,
                onPrimaryButtonClick = {
                    save(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (component.type is Screen.JxlTools.Type.JxlToImage) {
                        actions()
                    } else {
                        EnhancedChip(
                            selected = true,
                            onClick = null,
                            selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(uris.size.toString())
                        }
                    }
                },
                showNullDataButtonAsContainer = true,
                onSecondaryButtonLongClick = if (component.type is Screen.JxlTools.Type.ImageToJxl) {
                    {
                        showOneTimeImagePickingDialog = true
                    }
                } else null
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = save
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        noDataControls = {
            val types = remember {
                Screen.JxlTools.Type.entries
            }
            val preference1 = @Composable {
                PreferenceItem(
                    title = stringResource(types[0].title),
                    subtitle = stringResource(types[0].subtitle),
                    startIcon = types[0].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[0])
                    }
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(types[1].title),
                    subtitle = stringResource(types[1].subtitle),
                    startIcon = types[1].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[1])
                    }
                )
            }
            val preference3 = @Composable {
                PreferenceItem(
                    title = stringResource(types[2].title),
                    subtitle = stringResource(types[2].subtitle),
                    startIcon = types[2].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[2])
                    }
                )
            }
            val preference4 = @Composable {
                PreferenceItem(
                    title = stringResource(types[3].title),
                    subtitle = stringResource(types[3].subtitle),
                    startIcon = types[3].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickImage(types[3])
                    }
                )
            }
            if (isPortrait) {
                Column {
                    preference1()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference2()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference3()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference4()
                }
            } else {
                val direction = LocalLayoutDirection.current
                val cutout = WindowInsets.displayCutout.asPaddingValues().let {
                    PaddingValues(
                        start = it.calculateStartPadding(direction),
                        end = it.calculateEndPadding(direction)
                    )
                }

                Row(
                    modifier = Modifier.padding(cutout)
                ) {
                    preference1.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference2.withModifier(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(cutout)
                ) {
                    preference3.withModifier(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    preference4.withModifier(modifier = Modifier.weight(1f))
                }
            }
        },
        isPortrait = isPortrait,
        canShowScreenData = component.type != null
    )

    if (component.left != -1) {
        LoadingDialog(
            visible = component.isSaving,
            done = component.done,
            left = component.left,
            onCancelLoading = component::cancelSaving
        )
    } else {
        LoadingDialog(
            visible = component.isSaving,
            onCancelLoading = component::cancelSaving
        )
    }

    ExitWithoutSavingDialog(
        onExit = component::clearAll,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

private fun Uri.isJxl(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".jxl")
        .or(context.contentResolver.getType(this)?.contains("jxl") == true)
}

private val JxlToolsComponent.canSave: Boolean
    get() = (imageFrames == ImageFrames.All)
        .or(type !is Screen.JxlTools.Type.JxlToImage)
        .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)