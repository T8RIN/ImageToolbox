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

package com.t8rin.imagetoolbox.collage_maker.presentation

import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.FormatLineSpacing
import androidx.compose.material.icons.rounded.PhotoSizeSelectSmall
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import com.t8rin.collages.Collage
import com.t8rin.collages.CollageTypeSelection
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormatGroup
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.image.AspectRatioSelector
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.other.LockScreenOrientation
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun CollageMakerContent(
    component: CollageMakerComponent
) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val context = LocalContext.current

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    LaunchedEffect(component.initialUris) {
        component.initialUris?.takeIf { it.isNotEmpty() }?.let {
            if (it.size in 1..10) {
                component.updateUris(it)
            } else {
                essentials.showToast(
                    message = context.getString(R.string.pick_up_to_ten_images),
                    icon = Icons.Outlined.AutoAwesomeMosaic
                )
            }
        }
    }

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        if (uris.size in 1..10) {
            component.updateUris(uris)
        } else {
            essentials.showToast(
                message = if (uris.size > 10) {
                    context.getString(R.string.pick_up_to_ten_images)
                } else {
                    context.getString(R.string.pick_at_least_two_images)
                },
                icon = Icons.Outlined.AutoAwesomeMosaic
            )
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResult
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    var resettingTrigger by rememberSaveable {
        mutableIntStateOf(0)
    }

    AdaptiveBottomScaffoldLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.collage_maker),
                input = component.uris,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = onBack,
        shouldDisableBackHandler = !component.haveChanges,
        actions = { scaffoldState ->
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            EnhancedIconButton(
                onClick = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            scaffoldState.bottomSheetState.partialExpand()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tune,
                    contentDescription = stringResource(R.string.properties)
                )
            }
            ShareButton(
                onShare = {
                    component.performSharing(showConfetti)
                },
                onCopy = {
                    component.cacheImage(essentials::copyToClipboard)
                },
                onEdit = {
                    component.cacheImage {
                        editSheetData = listOf(it)
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    editSheetData = emptyList()
                },
                onNavigate = component.onNavigate
            )
        },
        topAppBarPersistentActions = {
            if (component.uris.isNullOrEmpty()) {
                TopAppBarEmoji()
            } else {
                var showResetDialog by rememberSaveable {
                    mutableStateOf(false)
                }
                EnhancedIconButton(
                    onClick = { showResetDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ImageReset,
                        contentDescription = stringResource(R.string.reset_image)
                    )
                }
                ResetDialog(
                    visible = showResetDialog,
                    onDismiss = { showResetDialog = false },
                    onReset = {
                        resettingTrigger++
                    }
                )
            }
        },
        mainContent = {
            var isLoading by rememberSaveable(component.uris) {
                mutableStateOf(true)
            }
            LaunchedEffect(isLoading) {
                if (isLoading) {
                    delay(500)
                    isLoading = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                var bottomPadding by remember {
                    mutableStateOf(0.dp)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            bottom = bottomPadding
                        )
                ) {
                    AnimatedContent(
                        targetState = component.uris to resettingTrigger,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { (uris) ->
                        Box(
                            modifier = Modifier
                                .zoomable(rememberZoomState())
                                .container(
                                    shape = ShapeDefaults.extraSmall,
                                    resultPadding = 0.dp
                                )
                                .shimmer(visible = isLoading),
                            contentAlignment = Alignment.Center
                        ) {
                            Collage(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(ShapeDefaults.extraSmall)
                                    .transparencyChecker(),
                                images = uris ?: emptyList(),
                                collageType = component.collageType,
                                collageCreationTrigger = component.collageCreationTrigger,
                                onCollageCreated = component::updateCollageBitmap,
                                backgroundColor = component.backgroundColor,
                                spacing = component.spacing,
                                cornerRadius = component.cornerRadius,
                                aspectRatio = component.aspectRatio.value,
                                outputScaleRatio = component.outputScaleRatio
                            )
                        }
                    }
                }
                val density = LocalDensity.current
                InfoContainer(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .onGloballyPositioned {
                            bottomPadding = with(density) { it.size.height.toDp() + 20.dp }
                        },
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    text = stringResource(R.string.collages_info)
                )
            }
        },
        controls = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.container(
                        resultPadding = 0.dp,
                        shape = ShapeDefaults.extraLarge
                    )
                ) {
                    Text(
                        fontWeight = FontWeight.Medium,
                        text = stringResource(R.string.collage_type),
                        modifier = Modifier.padding(top = 16.dp),
                        fontSize = 18.sp
                    )
                    val state = rememberLazyListState()
                    CollageTypeSelection(
                        state = state,
                        imagesCount = component.uris?.size ?: 0,
                        value = component.collageType,
                        onValueChange = component::setCollageType,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .fadingEdges(state),
                        contentPadding = PaddingValues(16.dp),
                        shape = ShapeDefaults.small,
                        itemModifierFactory = { isSelected ->
                            Modifier
                                .container(
                                    resultPadding = 0.dp,
                                    color = animateColorAsState(
                                        targetValue = if (isSelected) {
                                            MaterialTheme.colorScheme.secondaryContainer
                                        } else MaterialTheme.colorScheme.surfaceContainerLowest,
                                    ).value,
                                    shape = ShapeDefaults.small
                                )
                                .padding(8.dp)
                                .clip(ShapeDefaults.extremeSmall)
                        }
                    )
                }
                ColorRowSelector(
                    modifier = Modifier
                        .fillMaxWidth()
                        .container(
                            shape = ShapeDefaults.extraLarge
                        ),
                    icon = Icons.Rounded.FormatColorFill,
                    value = component.backgroundColor,
                    onValueChange = component::setBackgroundColor
                )
                AspectRatioSelector(
                    selectedAspectRatio = component.aspectRatio,
                    onAspectRatioChange = { aspect, _ ->
                        component.setAspectRatio(aspect)
                    },
                    unselectedCardColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    aspectRatios = remember {
                        DomainAspectRatio.defaultList - setOf(
                            DomainAspectRatio.Free,
                            DomainAspectRatio.Original
                        )
                    }
                )
                EnhancedSliderItem(
                    modifier = Modifier.fillMaxWidth(),
                    value = component.spacing,
                    title = stringResource(R.string.spacing),
                    valueRange = 0f..50f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = component::setSpacing,
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.FormatLineSpacing,
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    modifier = Modifier.fillMaxWidth(),
                    value = component.cornerRadius,
                    title = stringResource(R.string.corners),
                    valueRange = 0f..50f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = component::setCornerRadius,
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.RoundedCorner,
                    shape = ShapeDefaults.extraLarge
                )
                EnhancedSliderItem(
                    modifier = Modifier.fillMaxWidth(),
                    value = component.outputScaleRatio,
                    title = stringResource(R.string.output_image_scale),
                    valueRange = 0.5f..4f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = component::setOutputScaleRatio,
                    sliderModifier = Modifier
                        .padding(
                            top = 14.dp,
                            start = 12.dp,
                            end = 12.dp,
                            bottom = 10.dp
                        ),
                    icon = Icons.Rounded.PhotoSizeSelectSmall,
                    shape = ShapeDefaults.extraLarge
                )
                QualitySelector(
                    imageFormat = component.imageFormat,
                    quality = component.quality,
                    onQualityChange = component::setQuality
                )
                ImageFormatSelector(
                    modifier = Modifier.navigationBarsPadding(),
                    value = component.imageFormat,
                    onValueChange = component::setImageFormat,
                    entries = if (component.backgroundColor.alpha != 1f) {
                        ImageFormatGroup.alphaContainedEntries
                    } else ImageFormatGroup.entries,
                    forceEnabled = true
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
                isNoData = component.uris.isNullOrEmpty(),
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        noDataControls = {
            if (!component.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
        canShowScreenData = !component.uris.isNullOrEmpty()
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelSaving
    )
}