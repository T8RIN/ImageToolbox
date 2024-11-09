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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Gif
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Jxl
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
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
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagesPreviewWithSelection
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.components.GifParamsSelector
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.screenLogic.GifToolsComponent

@Composable
fun GifToolsContent(
    onGoBack: () -> Unit,
    component: GifToolsComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val imagePicker =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let(component::setImageUris)
        }

    val pickSingleGifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            if (it.isGif(context)) {
                component.setGifUri(it)
            } else {
                essentials.showToast(
                        message = context.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Rounded.Gif
                    )
            }
        }
    }

    val pickMultipleGifToJxlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isGif(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                        message = context.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Jxl
                    )
            } else {
                component.setType(
                    Screen.GifTools.Type.GifToJxl(uris)
                )
            }
        }
    }

    val pickMultipleGifToWebpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isGif(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                        message = context.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Jxl
                    )
            } else {
                component.setType(
                    Screen.GifTools.Type.GifToWebp(uris)
                )
            }
        }
    }

    val addGifsToJxlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isGif(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                        message = context.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Jxl
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

    val addGifsToWebpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.filter {
            it.isGif(context)
        }?.let { uris ->
            if (uris.isEmpty()) {
                essentials.showToast(
                        message = context.getString(R.string.select_gif_image_to_start),
                        icon = Icons.Filled.Jxl
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

    val saveGifLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/gif"),
        onResult = {
            it?.let { uri ->
                component.saveGifTo(uri) { result ->
                    context.parseFileSaveResult(
                        saveResult = result,
                        essentials = essentials
                    )
                }
            }
        }
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else onGoBack()
    }

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            TopAppBarTitle(
                title = when (component.type) {
                    is Screen.GifTools.Type.GifToImage -> {
                        stringResource(R.string.gif_type_to_image)
                    }

                    is Screen.GifTools.Type.ImageToGif -> {
                        stringResource(R.string.gif_type_to_gif)
                    }

                    is Screen.GifTools.Type.GifToJxl -> {
                        stringResource(R.string.gif_type_to_jxl)
                    }

                    is Screen.GifTools.Type.GifToWebp -> {
                        stringResource(R.string.gif_type_to_webp)
                    }

                    null -> stringResource(R.string.gif_tools)
                },
                input = component.type,
                isLoading = component.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (component.type == null) TopAppBarEmoji()
            val pagesSize by remember(component.gifFrames, component.convertedImageUris) {
                derivedStateOf {
                    component.gifFrames.getFramePositions(component.convertedImageUris.size).size
                }
            }
            val isGifToImage = component.type is Screen.GifTools.Type.GifToImage
            AnimatedVisibility(
                visible = isGifToImage && pagesSize != component.convertedImageUris.size,
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
                visible = isGifToImage && pagesSize != 0
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
            ShareButton(
                enabled = !component.isLoading && component.type != null,
                onShare = {
                    component.performSharing(showConfetti)
                }
            )
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
                            is Screen.GifTools.Type.GifToImage -> {
                                ImagesPreviewWithSelection(
                                    imageUris = component.convertedImageUris,
                                    imageFrames = component.gifFrames,
                                    onFrameSelectionChange = component::updateGifFrames,
                                    isPortrait = isPortrait,
                                    isLoadingImages = component.isLoadingGifImages
                                )
                            }

                            is Screen.GifTools.Type.GifToJxl -> {
                                UrisPreview(
                                    modifier = Modifier
                                        .then(
                                            if (!isPortrait) {
                                                Modifier
                                                    .layout { measurable, constraints ->
                                                        val placeable = measurable.measure(
                                                            constraints = constraints.copy(
                                                                maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                                                            )
                                                        )
                                                        layout(placeable.width, placeable.height) {
                                                            placeable.place(0, 0)
                                                        }
                                                    }
                                                    .verticalScroll(rememberScrollState())
                                            } else Modifier
                                        )
                                        .padding(vertical = 24.dp),
                                    uris = type.gifUris ?: emptyList(),
                                    isPortrait = true,
                                    onRemoveUri = {
                                        component.setType(
                                            Screen.GifTools.Type.GifToJxl(type.gifUris?.minus(it))
                                        )
                                    },
                                    onAddUris = {
                                        addGifsToJxlLauncher.launch(arrayOf("image/gif"))
                                    }
                                )
                            }

                            is Screen.GifTools.Type.GifToWebp -> {
                                UrisPreview(
                                    modifier = Modifier
                                        .then(
                                            if (!isPortrait) {
                                                Modifier
                                                    .layout { measurable, constraints ->
                                                        val placeable = measurable.measure(
                                                            constraints = constraints.copy(
                                                                maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                                                            )
                                                        )
                                                        layout(placeable.width, placeable.height) {
                                                            placeable.place(0, 0)
                                                        }
                                                    }
                                                    .verticalScroll(rememberScrollState())
                                            } else Modifier
                                        )
                                        .padding(vertical = 24.dp),
                                    uris = type.gifUris ?: emptyList(),
                                    isPortrait = true,
                                    onRemoveUri = {
                                        component.setType(
                                            Screen.GifTools.Type.GifToWebp(type.gifUris?.minus(it))
                                        )
                                    },
                                    onAddUris = {
                                        addGifsToWebpLauncher.launch(arrayOf("image/gif"))
                                    }
                                )
                            }

                            is Screen.GifTools.Type.ImageToGif -> Unit
                        }
                    }
                }
            }
        },
        placeImagePreview = component.type !is Screen.GifTools.Type.ImageToGif,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            when (val type = component.type) {
                is Screen.GifTools.Type.GifToImage -> {
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
                        onQualityChange = component::setQuality
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is Screen.GifTools.Type.ImageToGif -> {
                    val addImagesToGifPicker = rememberImagePicker(
                        mode = localImagePickerMode(Picker.Multiple)
                    ) { list ->
                        list.takeIf { it.isNotEmpty() }?.let(component::addImageToUris)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageReorderCarousel(
                        images = type.imageUris,
                        onReorder = component::reorderImageUris,
                        onNeedToAddImage = { addImagesToGifPicker.pickImage() },
                        onNeedToRemoveImageAt = component::removeImageAt
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    GifParamsSelector(
                        value = component.params,
                        onValueChange = component::updateParams
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is Screen.GifTools.Type.GifToJxl -> {
                    QualitySelector(
                        imageFormat = ImageFormat.Jxl.Lossy,
                        enabled = true,
                        quality = component.jxlQuality,
                        onQualityChange = component::setJxlQuality
                    )
                }

                is Screen.GifTools.Type.GifToWebp -> {
                    QualitySelector(
                        imageFormat = ImageFormat.Jpg,
                        enabled = true,
                        quality = component.webpQuality,
                        onQualityChange = component::setWebpQuality
                    )
                }

                null -> Unit
            }
        },
        contentPadding = animateDpAsState(
            if (component.type == null) 12.dp
            else 20.dp
        ).value,
        buttons = {
            val settingsState = LocalSettingsState.current

            val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
                component.saveBitmaps(
                    oneTimeSaveLocationUri = it,
                    onGifSaveResult = { name ->
                        runCatching {
                            runCatching {
                                saveGifLauncher.launch("$name.gif")
                            }.onFailure {
                                essentials.showToast(
                                        message = context.getString(R.string.activate_files),
                                        icon = Icons.Outlined.FolderOff,
                                        duration = ToastDuration.Long
                                    )
                            }
                        }.onFailure {
                            essentials.showToast(
                                    message = context.getString(R.string.activate_files),
                                    icon = Icons.Outlined.FolderOff,
                                    duration = ToastDuration.Long
                                )
                        }
                    },
                    onResult = { results ->
                        context.parseSaveResults(
                            results = results,
                            isOverwritten = settingsState.overwriteFiles,
                            essentials = essentials
                        )
                    }
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
                onSecondaryButtonClick = {
                    when (component.type) {
                        is Screen.GifTools.Type.GifToImage -> pickSingleGifLauncher.launch(arrayOf("image/gif"))
                        is Screen.GifTools.Type.GifToJxl -> pickMultipleGifToJxlLauncher.launch(
                            arrayOf("image/gif")
                        )

                        is Screen.GifTools.Type.GifToWebp -> pickMultipleGifToWebpLauncher.launch(
                            arrayOf("image/gif")
                        )

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
                onSecondaryButtonLongClick = if (component.type is Screen.GifTools.Type.ImageToGif) {
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
        noDataControls = {
            val types = remember {
                Screen.GifTools.Type.entries
            }
            val preference1 = @Composable {
                PreferenceItem(
                    title = stringResource(types[0].title),
                    subtitle = stringResource(types[0].subtitle),
                    startIcon = types[0].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = imagePicker::pickImage
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(types[1].title),
                    subtitle = stringResource(types[1].subtitle),
                    startIcon = types[1].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickSingleGifLauncher.launch(arrayOf("image/gif"))
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
                        pickMultipleGifToJxlLauncher.launch(arrayOf("image/gif"))
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
                        pickMultipleGifToWebpLauncher.launch(arrayOf("image/gif"))
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

private fun Uri.isGif(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".gif")
        .or(context.contentResolver.getType(this)?.contains("gif") == true)
}

private val GifToolsComponent.canSave: Boolean
    get() = (gifFrames == ImageFrames.All)
        .or(type is Screen.GifTools.Type.ImageToGif)
        .or((gifFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)