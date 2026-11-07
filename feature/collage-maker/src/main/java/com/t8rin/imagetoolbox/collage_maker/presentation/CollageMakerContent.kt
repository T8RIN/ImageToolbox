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

package com.t8rin.imagetoolbox.collage_maker.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.collages.config.CollageConstants
import com.t8rin.collages.layout.CollageLayoutStore
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageMakerActions
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageMakerControls
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageMakerNoDataControls
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageMakerPreview
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageSlotSheet
import com.t8rin.imagetoolbox.collage_maker.presentation.components.CollageStyleApplyDialog
import com.t8rin.imagetoolbox.collage_maker.presentation.screenLogic.CollageMakerComponent
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Collage
import com.t8rin.imagetoolbox.core.resources.icons.ImageReset
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveBottomScaffoldLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.rememberStylePresetSelectorState
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.core.utils.getString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CollageMakerContent(
    component: CollageMakerComponent
) {
    val scope = rememberCoroutineScope()

    var editSlot by rememberSaveable { mutableIntStateOf(-1) }
    var showSlotSettings by rememberSaveable { mutableStateOf(false) }
    var styleToApplyName by rememberSaveable { mutableStateOf<String?>(null) }
    val styleToApply = component.library.styles.firstOrNull { it.name == styleToApplyName }
    var showStyleApply by rememberSaveable { mutableStateOf(false) }
    val styleSelectorState = rememberStylePresetSelectorState()
    var closeControls by remember { mutableStateOf(false) }

    LaunchedEffect(component.uris?.size, component.collageType.imageCount) {
        val count = component.uris.orEmpty().size
        if (count > 0 && component.collageType.imageCount != count) {
            CollageLayoutStore
                .getDefaultLayout(count)
                ?.takeIf { component.collageType.imageCount != count }
                ?.let(component::setCollageType)
        }
    }

    CollageSlotSheet(
        component = component,
        index = editSlot,
        visible = showSlotSettings,
        onDismiss = { showSlotSettings = it }
    )

    CollageStyleApplyDialog(
        preset = styleToApply,
        imageCount = component.uris.orEmpty().size,
        visible = showStyleApply && styleToApply != null,
        onDismiss = { showStyleApply = false },
        onApply = { includeLayout ->
            styleToApply?.style?.let { style ->
                scope.launch {
                    if (includeLayout && style.imageCount == component.uris.orEmpty().size) {
                        CollageLayoutStore.load(style.imageCount)
                    }
                    component.applyStyle(style, includeLayout)
                    styleSelectorState.hide()
                    closeControls = true
                }
            }
        }
    )

    var isLoading by rememberSaveable { mutableStateOf(true) }
    var outputSize by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    LaunchedEffect(component.uris) {
        outputSize = null
    }

    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        if (uris.size in 1..CollageConstants.MAX_IMAGE_COUNT) {
            isLoading = true
            component.updateUris(uris)
        } else {
            AppToastHost.showToast(
                message = if (uris.size > CollageConstants.MAX_IMAGE_COUNT) {
                    getString(
                        R.string.pick_up_to_n_collage_images,
                        CollageConstants.MAX_IMAGE_COUNT,
                    )
                } else {
                    getString(R.string.pick_at_least_two_images)
                },
                icon = Icons.Outlined.Collage
            )
        }
    }

    val pickImage = imagePicker::pickImage
    val projectOpener = rememberFilePicker(
        mimeType = MimeType.CollageProjectList,
        onSuccess = component::openProject
    )
    val projectSaver = rememberFileCreator(
        mimeType = MimeType.CollageProject,
        onSuccess = component::saveProject
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmap(
            oneTimeSaveLocationUri = it
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

    val settings = LocalSettingsState.current

    LaunchedEffect(
        settings.isNightMode,
        settings.appColorTuple,
        settings.isDynamicColors
    ) {
        delay(500)
        resettingTrigger++
    }


    val canShowScreenData = !component.uris.isNullOrEmpty()

    AdaptiveBottomScaffoldLayoutScreen(
        isLoading = component.isImageLoading,
        title = {
            AnimatedContent(
                targetState = component.uris.isNullOrEmpty()
            ) { noData ->
                if (noData) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.marquee()
                    ) {
                        Text(
                            text = stringResource(R.string.collage_maker)
                        )
                        EnhancedBadge(
                            content = {
                                Text(
                                    text = CollageLayoutStore.layoutCount.toString()
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .padding(bottom = 12.dp)
                                .scaleOnTap {
                                    AppToastHost.showConfetti()
                                }
                        )
                    }
                } else {
                    TopAppBarTitle(
                        title = stringResource(R.string.collage_maker),
                        input = component.uris,
                        isLoading = component.isImageLoading,
                        size = null
                    )
                }
            }
        },
        onGoBack = onBack,
        shouldDisableBackHandler = !component.haveChanges,
        actions = { scaffoldState ->
            CollageMakerActions(
                component = component,
                onToggleControls = {
                    scope.launch {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                            scaffoldState.bottomSheetState.partialExpand()
                        } else {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                }
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
                        component.resetCollage()
                    }
                )
            }
        },
        mainContent = {
            CollageMakerPreview(
                component = component,
                isPortrait = isPortrait,
                isLoading = isLoading,
                resettingTrigger = resettingTrigger,
                onLoadingChange = { isLoading = it },
                onOutputSizeChanged = { width, height ->
                    outputSize = width to height
                },
                onEditSlot = {
                    editSlot = it
                    showSlotSettings = true
                }
            )
        },
        controls = { scaffoldState ->
            LaunchedEffect(closeControls) {
                if (closeControls) {
                    scaffoldState.bottomSheetState.partialExpand()
                    closeControls = false
                }
            }
            CollageMakerControls(
                component = component,
                isPortrait = isPortrait,
                outputSize = outputSize,
                styleSelectorState = styleSelectorState,
                onSaveProject = {
                    projectSaver.make(component.createProjectFilename())
                },
                onApplyStyle = { style ->
                    styleToApplyName =
                        component.library.styles.firstOrNull { it.style == style }?.name
                    showStyleApply = styleToApplyName != null
                }
            )
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
                },
                showNullDataButtonAsContainer = true,
                drawBothStrokes = true
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps,
                formatForFilenameSelection = component.getFormatForFilenameSelection(),
                hasOriginalUri = false
            )

            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        enableNoDataScroll = false,
        noDataControls = {
            CollageMakerNoDataControls(
                onPickImages = pickImage,
                onOpenProject = projectOpener::pickFile
            )
        },
        canShowScreenData = canShowScreenData
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving || component.isImageLoading,
        onCancelLoading = component::cancelLoading
    )
}