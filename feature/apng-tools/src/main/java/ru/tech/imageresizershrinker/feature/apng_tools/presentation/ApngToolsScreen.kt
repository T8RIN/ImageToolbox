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

package ru.tech.imageresizershrinker.feature.apng_tools.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.outlined.PhotoSizeSelectLarge
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Timelapse
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Stream
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Quality
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.Apng
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.ImageReorderCarousel
import ru.tech.imageresizershrinker.core.ui.widget.controls.QualityWidget
import ru.tech.imageresizershrinker.core.ui.widget.controls.ResizeImageField
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.withModifier
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.apng_tools.domain.ApngFrames
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.components.ApngConvertedImagesPreview
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.viewModel.ApngToolsViewModel
import kotlin.math.roundToInt

@Composable
fun ApngToolsScreen(
    typeState: Screen.ApngTools.Type?,
    onGoBack: () -> Unit,
    viewModel: ApngToolsViewModel = hiltViewModel()
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

    val pickSingleImageLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.firstOrNull()?.let {
            if (it.isApng(context)) {
                viewModel.setApngUri(it)
            } else {
                scope.launch {
                    toastHostState.showToast(
                        message = context.getString(R.string.select_apng_image_to_start),
                        icon = Icons.Rounded.Apng
                    )
                }
            }
        }
    }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument(),
        onResult = {
            it?.let { uri ->
                viewModel.saveApngTo(
                    outputStream = context.contentResolver.openOutputStream(uri, "rw")
                ) { t ->
                    if (t != null) {
                        scope.launch {
                            toastHostState.showError(context, t)
                        }
                    } else {
                        scope.launch {
                            confettiHostState.showConfetti()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.saved_to_without_filename,
                                    ""
                                ),
                                Icons.Rounded.Save
                            )
                            ReviewHandler.showReview(context)
                        }
                    }
                }
            }
        }
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.type != null) showExitDialog = true
        else onGoBack()
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = when (viewModel.type) {
                    is Screen.ApngTools.Type.ApngToImage -> {
                        stringResource(R.string.apng_type_to_image)
                    }

                    is Screen.ApngTools.Type.ImageToApng -> {
                        stringResource(R.string.apng_type_to_apng)
                    }

                    null -> stringResource(R.string.apng_tools)
                },
                input = viewModel.type,
                isLoading = viewModel.isLoading,
                size = null
            )
        },
        onGoBack = onBack,
        topAppBarPersistentActions = {
            if (viewModel.type == null) TopAppBarEmoji()
            val pagesSize by remember(viewModel.apngFrames, viewModel.convertedImageUris) {
                derivedStateOf {
                    viewModel.apngFrames.getFramePositions(viewModel.convertedImageUris.size).size
                }
            }
            val isApngToImage = viewModel.type is Screen.ApngTools.Type.ApngToImage
            AnimatedVisibility(
                visible = isApngToImage && pagesSize != viewModel.convertedImageUris.size,
                enter = fadeIn() + scaleIn() + expandHorizontally(),
                exit = fadeOut() + scaleOut() + shrinkHorizontally()
            ) {
                EnhancedIconButton(
                    containerColor = Color.Transparent,
                    contentColor = LocalContentColor.current,
                    enableAutoShadowAndBorder = false,
                    onClick = viewModel::selectAllConvertedImages
                ) {
                    Icon(Icons.Outlined.SelectAll, null)
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
                visible = isApngToImage && pagesSize != 0
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
                        Icon(Icons.Rounded.Close, null)
                    }
                }
            }
        },
        actions = {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.performSharing(showConfetti)
                },
                enabled = !viewModel.isLoading && viewModel.type != null
            ) {
                Icon(Icons.Rounded.Share, null)
            }
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
                            is Screen.ApngTools.Type.ApngToImage -> {
                                ApngConvertedImagesPreview(
                                    imageUris = viewModel.convertedImageUris,
                                    apngFrames = viewModel.apngFrames,
                                    onApngFramesChange = viewModel::updateApngFrames,
                                    isPortrait = isPortrait,
                                    isLoadingApngImages = viewModel.isLoadingApngImages
                                )
                            }

                            is Screen.ApngTools.Type.ImageToApng -> Unit
                        }
                    }
                }
            }
        },
        placeImagePreview = viewModel.type is Screen.ApngTools.Type.ApngToImage,
        showImagePreviewAsStickyHeader = false,
        autoClearFocus = false,
        controls = {
            when (val type = viewModel.type) {
                is Screen.ApngTools.Type.ApngToImage -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageFormatSelector(
                        value = viewModel.imageFormat,
                        onValueChange = viewModel::setImageFormat,
                        entries = ImageFormat.alphaContainedEntries
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    QualityWidget(
                        imageFormat = viewModel.imageFormat,
                        enabled = true,
                        quality = viewModel.params.quality,
                        onQualityChange = viewModel::setQuality
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                is Screen.ApngTools.Type.ImageToApng -> {
                    val addImagesToPdfPicker = rememberImagePicker(
                        mode = localImagePickerMode(Picker.Multiple)
                    ) { list ->
                        list.takeIf { it.isNotEmpty() }?.let(viewModel::addImageToUris)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    ImageReorderCarousel(
                        images = type.imageUris,
                        onReorder = viewModel::reorderImageUris,
                        onNeedToAddImage = { addImagesToPdfPicker.pickImage() },
                        onNeedToRemoveImageAt = viewModel::removeImageAt
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val size = viewModel.params.size ?: IntegerSize.Undefined
                    AnimatedVisibility(size.isDefined()) {
                        ResizeImageField(
                            imageInfo = ImageInfo(size.width, size.height),
                            originalSize = null,
                            onWidthChange = {
                                viewModel.updateParams(
                                    viewModel.params.copy(
                                        size = size.copy(width = it)
                                    )
                                )
                            },
                            onHeightChange = {
                                viewModel.updateParams(
                                    viewModel.params.copy(
                                        size = size.copy(height = it)
                                    )
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    PreferenceRowSwitch(
                        title = stringResource(id = R.string.use_size_of_first_frame),
                        subtitle = stringResource(id = R.string.use_size_of_first_frame_sub),
                        checked = viewModel.params.size == null,
                        onClick = viewModel::setUseOriginalSize,
                        startIcon = Icons.Outlined.PhotoSizeSelectLarge,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Unspecified,
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EnhancedSliderItem(
                        value = viewModel.params.quality.qualityValue,
                        title = stringResource(R.string.effort),
                        icon = Icons.Rounded.Stream,
                        valueRange = 0f..9f,
                        steps = 9,
                        internalStateTransformation = {
                            it.toInt().coerceIn(0..9).toFloat()
                        },
                        onValueChange = {
                            viewModel.setQuality(Quality.Base(it.toInt()))
                        }
                    ) {
                        Text(
                            text = stringResource(
                                R.string.effort_sub,
                                0, 9
                            ),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            color = LocalContentColor.current.copy(0.5f),
                            modifier = Modifier
                                .padding(4.dp)
                                .container(RoundedCornerShape(20.dp))
                                .padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    EnhancedSliderItem(
                        value = viewModel.params.repeatCount,
                        icon = Icons.Outlined.RepeatOne,
                        title = stringResource(id = R.string.repeat_count),
                        valueRange = 1f..10f,
                        steps = 9,
                        internalStateTransformation = { it.roundToInt() },
                        onValueChange = {
                            viewModel.updateParams(
                                viewModel.params.copy(
                                    repeatCount = it.roundToInt()
                                )
                            )
                        },
                        shape = RoundedCornerShape(24.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    EnhancedSliderItem(
                        value = viewModel.params.delay,
                        icon = Icons.Outlined.Timelapse,
                        title = stringResource(id = R.string.frame_delay),
                        valueRange = 1f..4000f,
                        internalStateTransformation = { it.roundToInt() },
                        onValueChange = {
                            viewModel.updateParams(
                                viewModel.params.copy(
                                    delay = it.roundToInt()
                                )
                            )
                        },
                        shape = RoundedCornerShape(24.dp)
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
            BottomButtonsBlock(
                targetState = (viewModel.type == null) to isPortrait,
                onSecondaryButtonClick = {
                    if (viewModel.type !is Screen.ApngTools.Type.ApngToImage) {
                        pickImagesLauncher.pickImage()
                    } else pickSingleImageLauncher.pickImage()
                },
                isPrimaryButtonVisible = viewModel.canSave,
                onPrimaryButtonClick = {
                    viewModel.saveBitmaps(
                        onApngSaveResult = { name ->
                            runCatching {
                                runCatching {
                                    savePdfLauncher.launch("image/apng#$name.png")
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
                        onResult = { results, savingPath ->
                            context.failedToSaveImages(
                                scope = scope,
                                results = results,
                                toastHostState = toastHostState,
                                savingPathString = savingPath,
                                isOverwritten = settingsState.overwriteFiles,
                                showConfetti = showConfetti
                            )
                        }
                    )
                },
                actions = {
                    if (isPortrait) it()
                },
                showNullDataButtonAsContainer = true
            )
        },
        noDataControls = {
            val types = remember {
                Screen.ApngTools.Type.entries
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
                    onClick = pickSingleImageLauncher::pickImage
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

private fun Uri.isApng(context: Context): Boolean {
    return context.getFilename(this).toString().endsWith(".png")
        .or(context.contentResolver.getType(this)?.contains("png") == true)
        .or(context.contentResolver.getType(this)?.contains("apng") == true)
}

private class CreateDocument : ActivityResultContracts.CreateDocument("*/*") {
    override fun createIntent(
        context: Context,
        input: String
    ): Intent {
        return super.createIntent(
            context = context,
            input = input.split("#")[0]
        ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
    }
}

private val ApngToolsViewModel.canSave: Boolean
    get() = (apngFrames == ApngFrames.All)
        .or(type is Screen.ApngTools.Type.ImageToApng)
        .or((apngFrames as? ApngFrames.ManualSelection)?.framePositions?.isNotEmpty() == true)