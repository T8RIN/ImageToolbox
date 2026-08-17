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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.ui.utils.animation.fancySlideTransition
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.shareUris
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalScreenSize
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.ImageNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePager
import com.t8rin.imagetoolbox.core.ui.widget.image.UrisPreview
import com.t8rin.imagetoolbox.core.ui.widget.image.urisPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.ai_tools.domain.model.NeuralModel
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.AiDetectionResults
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.AiToolsControls
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.AiToolsResultCompareSheet
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.AiToolsResultsPreview
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.components.NeuralSaveProgressDialog
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic.AiToolsComponent

@Composable
fun AiToolsContent(
    component: AiToolsComponent
) {
    val imagePicker = rememberImagePicker { uris: List<Uri> ->
        component.updateUris(
            uris = uris
        )
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showPreviewExitDialog by rememberSaveable { mutableStateOf(false) }
    var compareUris by remember { mutableStateOf<Pair<Uri, Uri>?>(null) }
    var previewUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it
        )
    }

    val isPortrait by isPortraitOrientationAsState()

    val addImagesImagePicker = rememberImagePicker { uris: List<Uri> ->
        component.addUris(
            uris = uris
        )
    }

    val selectedModel by component.selectedModel.collectAsStateWithLifecycle()
    val canProcess = selectedModel != null && (
            selectedModel?.isStyleTransfer != true || component.styleUri != null
            )
    val isAiDetectionMode by component.isAiDetectionMode.collectAsStateWithLifecycle()

    val resultsMode = when {
        component.aiDetectionResults.isNotEmpty() -> ResultsMode.AiDetection
        component.previewResults.isNotEmpty() -> ResultsMode.ProcessedImages
        else -> ResultsMode.None
    }
    val isPreviewMode = resultsMode == ResultsMode.ProcessedImages
    val screenWidthPx = LocalScreenSize.current.widthPx

    val onBack = {
        when {
            resultsMode == ResultsMode.AiDetection -> component.clearAiDetectionResults()
            isPreviewMode -> showPreviewExitDialog = true
            component.haveChanges -> showExitDialog = true
            else -> component.onGoBack()
        }
    }

    AnimatedContent(
        targetState = resultsMode,
        transitionSpec = {
            fancySlideTransition(
                isForward = targetState != ResultsMode.None,
                screenWidthPx = screenWidthPx,
                duration = 400
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { currentResultsMode ->
        val resultsVisible = currentResultsMode != ResultsMode.None
        val processedImagesVisible = currentResultsMode == ResultsMode.ProcessedImages
        val aiDetectionResultsVisible = currentResultsMode == ResultsMode.AiDetection
        AdaptiveLayoutScreen(
            shouldDisableBackHandler = !component.haveChanges && !resultsVisible,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.marquee()
                ) {
                    Text(
                        text = stringResource(R.string.ai_tools)
                    )
                    EnhancedBadge(
                        content = {
                            Text(NeuralModel.entries.size.toString())
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
            },
            onGoBack = onBack,
            actions = {
                if (!isAiDetectionMode) {
                    var editSheetData by remember {
                        mutableStateOf(listOf<Uri>())
                    }
                    ShareButton(
                        onShare = {
                            if (processedImagesVisible) component.sharePreviewResults()
                            else component.shareBitmaps()
                        },
                        onEdit = {
                            if (processedImagesVisible) {
                                editSheetData = component.previewResults.map { it.cachedUri }
                            } else {
                                component.cacheImages {
                                    editSheetData = it
                                }
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
                }
            },
            showImagePreviewAsStickyHeader = false,
            imagePreview = {
                UrisPreview(
                    modifier = Modifier.urisPreview(isPortrait = isPortrait),
                    uris = component.uris.orEmpty(),
                    isPortrait = true,
                    onRemoveUri = component::removeUri,
                    onAddUris = addImagesImagePicker::pickImage,
                    onNavigate = component.onNavigate
                )
            },
            placeImagePreview = !resultsVisible,
            controls = {
                when (currentResultsMode) {
                    ResultsMode.ProcessedImages -> {
                        AiToolsResultsPreview(
                            results = component.previewResults,
                            onCompare = {
                                compareUris = it.originalUri to it.cachedUri
                            },
                            onRemove = component::removePreviewResult,
                            modifier = if (isPortrait) {
                                Modifier.padding(top = 20.dp)
                            } else Modifier
                        )
                    }

                    ResultsMode.AiDetection -> {
                        AiDetectionResults(
                            results = component.aiDetectionResults,
                            onOpen = { uri -> previewUri = uri },
                            modifier = if (isPortrait) {
                                Modifier.padding(top = 20.dp)
                            } else Modifier
                        )
                    }

                    ResultsMode.None -> AiToolsControls(component = component)
                }
            },
            noDataControls = {
                ImageNotPickedWidget(onPickImage = pickImage)
            },
            buttons = { actions ->
                if (!aiDetectionResultsVisible) {
                    var showFolderSelectionDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    var showOneTimeImagePickingDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    val save: (String?) -> Unit = if (processedImagesVisible) {
                        component::savePreviewResults
                    } else {
                        saveBitmaps
                    }
                    BottomButtonsBlock(
                        isNoData = component.uris.isNullOrEmpty() && !resultsVisible,
                        isPrimaryButtonVisible = canProcess || resultsVisible,
                        isSecondaryButtonVisible = !resultsVisible,
                        onSecondaryButtonClick = pickImage,
                        onPrimaryButtonClick = {
                            if (isAiDetectionMode) component.detectAiImages()
                            else save(null)
                        },
                        onPrimaryButtonLongClick = if (isAiDetectionMode) null else {
                            { showFolderSelectionDialog = true }
                        },
                        primaryButtonIcon = if (isAiDetectionMode) {
                            Icons.Outlined.ImageSearch
                        } else Icons.Rounded.Save,
                        primaryButtonText = if (isAiDetectionMode) {
                            stringResource(R.string.ai_detector_analyze)
                        } else "",
                        middleFab = if (
                            !component.uris.isNullOrEmpty() && canProcess &&
                            !resultsVisible && !isAiDetectionMode
                        ) {
                            {
                                EnhancedFloatingActionButton(
                                    onClick = component::processToPreview,
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Preview,
                                        contentDescription = null
                                    )
                                }
                            }
                        } else null,
                        showMiddleFabInRow = true,
                        actions = {
                            if (isPortrait) actions()
                        },
                        onSecondaryButtonLongClick = if (resultsVisible) {
                            null
                        } else {
                            { showOneTimeImagePickingDialog = true }
                        }
                    )
                    OneTimeSaveLocationSelectionDialog(
                        visible = showFolderSelectionDialog,
                        onDismiss = { showFolderSelectionDialog = false },
                        onSaveRequest = save,
                        formatForFilenameSelection = component.getFormatForFilenameSelection()
                    )
                    OneTimeImagePickingDialog(
                        onDismiss = { showOneTimeImagePickingDialog = false },
                        picker = Picker.Multiple,
                        imagePicker = imagePicker,
                        visible = showOneTimeImagePickingDialog
                    )
                }
            },
            topAppBarPersistentActions = {
                if (component.uris.isNullOrEmpty() || resultsVisible) {
                    TopAppBarEmoji()
                }
            },
            canShowScreenData = !component.uris.isNullOrEmpty() || resultsVisible
        )
    }

    AiToolsResultCompareSheet(
        uris = compareUris,
        onDismiss = { compareUris = null }
    )

    ImagePager(
        visible = previewUri != null,
        selectedUri = previewUri,
        uris = component.aiDetectionResults.map { it.uri },
        onNavigate = component.onNavigate,
        onUriSelected = { previewUri = it },
        onShare = { context.shareUris(listOf(it)) },
        onDismiss = { previewUri = null }
    )

    NeuralSaveProgressDialog(
        component = component
    )

    LoadingDialog(
        visible = component.isImageLoading,
        canCancel = false
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    ExitWithoutSavingDialog(
        onExit = component::clearPreviewResults,
        onDismiss = { showPreviewExitDialog = false },
        visible = showPreviewExitDialog,
        respectSettings = false
    )
}

private enum class ResultsMode {
    None,
    ProcessedImages,
    AiDetection
}