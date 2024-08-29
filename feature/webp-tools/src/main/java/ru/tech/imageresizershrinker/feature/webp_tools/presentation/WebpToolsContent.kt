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

package ru.tech.imageresizershrinker.feature.webp_tools.presentation

import android.content.Context
import android.net.Uri
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormatGroup
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Webp
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.ImagesPreviewWithSelection
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.components.WebpParamsSelector
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.viewModel.WebpToolsViewModel

@Composable
fun WebpToolsContent(
    typeState: Screen.WebpTools.Type?,
    onGoBack: () -> Unit,
    viewModel: WebpToolsViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(typeState) {
        typeState?.let { viewModel.setType(it) }
    }

    val pickImagesLauncher =
        rememberImagePicker(
            mode = localImagePickerMode(Picker.Multiple)
        ) { list ->
            list.takeIf { it.isNotEmpty() }?.let(viewModel::setImageUris)
        }

    val pickSingleWebpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        uri?.let {
            if (it.isWebp(context)) {
                viewModel.setWebpUri(it)
            } else {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.select_webp_image_to_start),
                        icon = Icons.Rounded.Webp
                    )
                }
            }
        }
    }

    val saveWebpLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/webp"),
        onResult = {
            it?.let { uri ->
                viewModel.saveWebpTo(uri) { result ->
                    context.parseFileSaveResult(
                        saveResult = result,
                        onSuccess = {
                            confettiHostState.showConfetti()
                        },
                        toastHostState = toastHostState,
                        scope = scope
                    )
                }
            }
        }
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = when (viewModel.type) {
                    is Screen.WebpTools.Type.WebpToImage -> {
                        stringResource(R.string.webp_type_to_image)
                    }

                    is Screen.WebpTools.Type.ImageToWebp -> {
                        stringResource(R.string.webp_type_to_webp)
                    }

                    null -> stringResource(R.string.webp_tools)
                },
                input = viewModel.type,
                isLoading = viewModel.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (viewModel.type == null) TopAppBarEmoji()
            val pagesSize by remember(viewModel.imageFrames, viewModel.convertedImageUris) {
                derivedStateOf {
                    viewModel.imageFrames.getFramePositions(viewModel.convertedImageUris.size).size
                }
            }
            val isWebpToImage = viewModel.type is Screen.WebpTools.Type.WebpToImage
            AnimatedVisibility(
                visible = isWebpToImage && pagesSize != viewModel.convertedImageUris.size,
                enter = fadeIn() + scaleIn() + expandHorizontally(),
                exit = fadeOut() + scaleOut() + shrinkHorizontally()
            ) {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = viewModel::selectAllConvertedImages
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
                visible = isWebpToImage && pagesSize != 0
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
                        containerColor = Color.Transparent,
                        contentColor = LocalContentColor.current,
                        enableAutoShadowAndBorder = false,
                        onClick = viewModel::clearConvertedImagesSelection
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
                enabled = !viewModel.isLoading && viewModel.type != null,
                onShare = {
                    viewModel.performSharing(showConfetti)
                }
            )
        },
        imagePreview = {
            AnimatedContent(
                targetState = viewModel.isLoading to viewModel.type
            ) { (loading, type) ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = if (loading) {
                        Modifier.padding(32.dp)
                    } else Modifier
                ) {
                    if (loading || type == null) {
                        Loading()
                    } else {
                        when (type) {
                            is Screen.WebpTools.Type.WebpToImage -> {
                                ImagesPreviewWithSelection(
                                    imageUris = viewModel.convertedImageUris,
                                    imageFrames = viewModel.imageFrames,
                                    onFrameSelectionChange = viewModel::updateWebpFrames,
                                    isPortrait = isPortrait,
                                    isLoadingImages = viewModel.isLoadingWebpImages
                                )
                            }

                            is Screen.WebpTools.Type.ImageToWebp -> Unit
                        }
                    }
                }
            }
        },
        placeImagePreview = viewModel.type !is Screen.WebpTools.Type.ImageToWebp,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            when (val type = viewModel.type) {
                is Screen.WebpTools.Type.WebpToImage -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageFormatSelector(
                        value = viewModel.imageFormat,
                        onValueChange = viewModel::setImageFormat,
                        entries = ImageFormatGroup.alphaContainedEntries
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QualitySelector(
                        imageFormat = viewModel.imageFormat,
                        enabled = true,
                        quality = viewModel.params.quality,
                        onQualityChange = viewModel::setQuality
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is Screen.WebpTools.Type.ImageToWebp -> {
                    val addImagesToPdfPicker = rememberImagePicker(
                        mode = localImagePickerMode(Picker.Multiple)
                    ) { list ->
                        list.takeIf { it.isNotEmpty() }?.let(viewModel::addImageToUris)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageReorderCarousel(
                        images = type.imageUris,
                        onReorder = viewModel::reorderImageUris,
                        onNeedToAddImage = addImagesToPdfPicker::pickImage,
                        onNeedToRemoveImageAt = viewModel::removeImageAt
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    WebpParamsSelector(
                        value = viewModel.params,
                        onValueChange = viewModel::updateParams
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                null -> Unit
            }
        },
        contentPadding = animateDpAsState(
            if (viewModel.type == null) 12.dp
            else 20.dp
        ).value,
        buttons = {
            val settingsState = LocalSettingsState.current
            val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
                viewModel.saveBitmaps(
                    oneTimeSaveLocationUri = it,
                    onWebpSaveResult = { name ->
                        runCatching {
                            runCatching {
                                saveWebpLauncher.launch("$name.webp")
                            }.onFailure {
                                scope.launch {
                                    toastHostState.showToast(
                                        message = context.getString(R.string.activate_files),
                                        icon = Icons.Outlined.FolderOff,
                                        duration = ToastDuration.Long
                                    )
                                }
                            }
                        }.onFailure {
                            scope.launch {
                                toastHostState.showToast(
                                    message = context.getString(R.string.activate_files),
                                    icon = Icons.Outlined.FolderOff,
                                    duration = ToastDuration.Long
                                )
                            }
                        }
                    },
                    onResult = { results ->
                        context.parseSaveResults(
                            scope = scope,
                            results = results,
                            toastHostState = toastHostState,
                            isOverwritten = settingsState.overwriteFiles,
                            showConfetti = showConfetti
                        )
                    }
                )
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (viewModel.type == null) to isPortrait,
                onSecondaryButtonClick = {
                    when (viewModel.type) {
                        is Screen.WebpTools.Type.WebpToImage -> {
                            pickSingleWebpLauncher.launch(
                                arrayOf("image/webp")
                            )
                        }

                        else -> pickImagesLauncher.pickImage()
                    }
                },
                isPrimaryButtonVisible = viewModel.canSave,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    if (viewModel.type is Screen.WebpTools.Type.ImageToWebp) {
                        saveBitmaps(null)
                    } else showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                showNullDataButtonAsContainer = true
            )
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmaps
                )
            }
        },
        noDataControls = {
            val types = remember {
                Screen.WebpTools.Type.entries
            }
            val preference1 = @Composable {
                PreferenceItem(
                    title = stringResource(types[0].title),
                    subtitle = stringResource(types[0].subtitle),
                    startIcon = types[0].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = pickImagesLauncher::pickImage
                )
            }
            val preference2 = @Composable {
                PreferenceItem(
                    title = stringResource(types[1].title),
                    subtitle = stringResource(types[1].subtitle),
                    startIcon = types[1].icon,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        pickSingleWebpLauncher.launch(arrayOf("image/webp"))
                    }
                )
            }

            if (isPortrait) {
                Column {
                    preference1()
                    Spacer(modifier = Modifier.height(8.dp))
                    preference2()
                }
            } else {
                val direction = LocalLayoutDirection.current
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(
                            WindowInsets.displayCutout.asPaddingValues().let {
                                PaddingValues(
                                    start = it.calculateStartPadding(direction),
                                    end = it.calculateEndPadding(direction)
                                )
                            }
                        )
                    ) {
                        preference1.withModifier(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        preference2.withModifier(modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        isPortrait = isPortrait,
        canShowScreenData = viewModel.type != null
    )

    if (viewModel.isSaving) {
        if (viewModel.left != -1) {
            LoadingDialog(
                done = viewModel.done,
                left = viewModel.left,
                onCancelLoading = viewModel::cancelSaving
            )
        } else {
            LoadingDialog(
                onCancelLoading = viewModel::cancelSaving
            )
        }
    }

    ExitWithoutSavingDialog(
        onExit = viewModel::clearAll,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

private fun Uri.isWebp(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".webp")
        .or(context.contentResolver.getType(this)?.contains("webp") == true)
}

private val WebpToolsViewModel.canSave: Boolean
    get() = (imageFrames == ImageFrames.All)
        .or(type is Screen.WebpTools.Type.ImageToWebp)
        .or((imageFrames as? ImageFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)