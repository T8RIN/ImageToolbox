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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Pages
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.PresetSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitBackHandler
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.components.PageInputField
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.components.PagesSelectionParser
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.components.PdfToImagesPreference
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.components.PdfToolsContentImpl
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.components.PreviewPdfPreference
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.screenLogic.PdfToolsComponent
import kotlinx.coroutines.delay

@Composable
fun PdfToolsContent(
    component: PdfToolsComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    val savePdfLauncher = rememberFileCreator(
        mimeType = MimeType.Pdf,
        onSuccess = { uri ->
            component.savePdfTo(
                uri = uri,
                onResult = essentials::parseFileSaveResult
            )
        }
    )

    val pdfToImagesPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = component::setPdfToImagesUri
    )

    val pdfPreviewPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = component::setPdfPreview
    )

    var tempSelectionUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showSelectionPdfPicker by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showSelectionPdfPicker) {
        if (!showSelectionPdfPicker) tempSelectionUri = null
    }
    val selectionPdfPicker = rememberFilePicker(
        mimeType = MimeType.Pdf,
        onSuccess = { uri: Uri ->
            tempSelectionUri = uri
            showSelectionPdfPicker = true
        }
    )

    EnhancedModalBottomSheet(
        visible = showSelectionPdfPicker,
        onDismiss = {
            showSelectionPdfPicker = it
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSelectionPdfPicker = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(id = R.string.close))
            }
        },
        sheetContent = {
            if (tempSelectionUri == null) showSelectionPdfPicker = false

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 12.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                verticalItemSpacing = 12.dp,
                contentPadding = PaddingValues(12.dp),
                flingBehavior = enhancedFlingBehavior()
            ) {
                item {
                    PreviewPdfPreference(
                        onClick = {
                            component.setPdfPreview(tempSelectionUri)
                            showSelectionPdfPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PdfToImagesPreference(
                        onClick = {
                            component.setPdfToImagesUri(tempSelectionUri)
                            showSelectionPdfPicker = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.pick_file),
                icon = Icons.Rounded.FileOpen
            )
        }
    )

    val imagesToPdfPicker = rememberImagePicker(onSuccess = component::setImagesToPdf)

    val addImagesToPdfPicker = rememberImagePicker(onSuccess = component::addImagesToPdf)

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        canScroll = { (component.pdfType !is Screen.PdfTools.Type.Preview && isPortrait) || component.pdfType == null }
    )

    LaunchedEffect(component.pdfType) {
        while (component.pdfType is Screen.PdfTools.Type.Preview || (component.pdfType != null && !isPortrait)) {
            topAppBarState.apply {
                heightOffset = (heightOffset - 10).coerceAtLeast(heightOffsetLimit)
            }
            delay(10)
        }
    }

    Surface(
        modifier = Modifier
            .clearFocusOnTap()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        color = MaterialTheme.colorScheme.background
    ) {
        PdfToolsContentImpl(
            component = component,
            scrollBehavior = scrollBehavior,
            onGoBack = onBack,
            onForceClearType = component::clearAll,
            isPortrait = isPortrait,
            actionButtons = { pdfType ->
                val visible by rememberCanSaveOrShare(
                    selectedPages = component.pdfToImageState?.selectedPages,
                    pdfType = pdfType
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn() + scaleIn() + expandHorizontally(),
                    exit = fadeOut() + scaleOut() + shrinkHorizontally()
                ) {
                    ShareButton(
                        onShare = {
                            component.preformSharing(
                                onSuccess = showConfetti,
                                onFailure = essentials::showFailureToast
                            )
                        }
                    )
                }
            },
            onPickContent = {
                when (it) {
                    is Screen.PdfTools.Type.ImagesToPdf -> imagesToPdfPicker.pickImage()
                    is Screen.PdfTools.Type.PdfToImages -> pdfToImagesPicker.pickFile()
                    is Screen.PdfTools.Type.Preview -> pdfPreviewPicker.pickFile()
                }
            },
            onSelectPdf = selectionPdfPicker::pickFile,
            buttons = { pdfType ->
                val isPreview = pdfType !is Screen.PdfTools.Type.Preview

                EnhancedFloatingActionButton(
                    onClick = {
                        when (pdfType) {
                            is Screen.PdfTools.Type.ImagesToPdf -> imagesToPdfPicker.pickImage()
                            is Screen.PdfTools.Type.Preview -> pdfPreviewPicker.pickFile()
                            else -> pdfToImagesPicker.pickFile()
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    type = if (isPreview) {
                        if (isPortrait) EnhancedFloatingActionButtonType.SecondaryHorizontal
                        else EnhancedFloatingActionButtonType.SecondaryVertical
                    } else {
                        EnhancedFloatingActionButtonType.Primary
                    }
                ) {
                    Icon(
                        imageVector = when (pdfType) {
                            is Screen.PdfTools.Type.ImagesToPdf -> Icons.Rounded.AddPhotoAlt
                            else -> Icons.Rounded.FileOpen
                        },
                        contentDescription = stringResource(R.string.pick)
                    )
                }
                if (isPreview) {
                    val visible by rememberCanSaveOrShare(
                        selectedPages = component.pdfToImageState?.selectedPages,
                        pdfType = pdfType
                    )

                    if (visible) {
                        if (isPortrait) {
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn() + scaleIn() + expandIn(),
                        exit = fadeOut() + scaleOut() + shrinkOut()
                    ) {
                        val savePdfToImages: (oneTimeSaveLocationUri: String?) -> Unit = {
                            component.savePdfToImages(
                                oneTimeSaveLocationUri = it,
                                onComplete = essentials::parseSaveResults
                            )
                        }
                        var showFolderSelectionDialog by rememberSaveable {
                            mutableStateOf(false)
                        }
                        EnhancedFloatingActionButton(
                            onClick = {
                                if (pdfType is Screen.PdfTools.Type.ImagesToPdf && component.imagesToPdfState != null) {
                                    component.convertImagesToPdf {
                                        savePdfLauncher.make(component.generatePdfFilename())
                                    }
                                } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                                    savePdfToImages(null)
                                }
                            },
                            onLongClick = if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                                { showFolderSelectionDialog = true }
                            } else null
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Save,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                        OneTimeSaveLocationSelectionDialog(
                            visible = showFolderSelectionDialog,
                            onDismiss = { showFolderSelectionDialog = false },
                            onSaveRequest = savePdfToImages
                        )
                    }
                }
            },
            controls = { pdfType ->
                if (pdfType is Screen.PdfTools.Type.ImagesToPdf) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ImageReorderCarousel(
                            images = component.imagesToPdfState,
                            onReorder = component::reorderImagesToPdf,
                            onNeedToAddImage = { addImagesToPdfPicker.pickImage() },
                            onNeedToRemoveImageAt = component::removeImageToPdfAt,
                            onNavigate = component.onNavigate
                        )
                        Spacer(Modifier.height(8.dp))
                        PresetSelector(
                            value = component.presetSelected,
                            includeTelegramOption = false,
                            onValueChange = {
                                if (it is Preset.Percentage) {
                                    component.selectPreset(it)
                                }
                            },
                            showWarning = component.showOOMWarning
                        )
                        Spacer(
                            Modifier.height(8.dp)
                        )
                        ScaleSmallImagesToLargeToggle(
                            checked = component.scaleSmallImagesToLarge,
                            onCheckedChange = {
                                component.toggleScaleSmallImagesToLarge()
                            }
                        )
                    }
                } else if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var showSelector by rememberSaveable {
                            mutableStateOf(false)
                        }
                        PreferenceItem(
                            title = stringResource(R.string.pages_selection),
                            subtitle = remember(component.pdfToImageState) {
                                derivedStateOf {
                                    component.pdfToImageState?.takeIf { it.selectedPages.isNotEmpty() }
                                        ?.let {
                                            if (it.selectedPages.size == it.pagesCount) {
                                                essentials.getString(R.string.all)
                                            } else {
                                                PagesSelectionParser.formatPageOutput(it.selectedPages)
                                            }
                                        } ?: essentials.getString(R.string.none)
                                }
                            }.value,
                            onClick = {
                                showSelector = true
                            },
                            modifier = Modifier.fillMaxWidth(),
                            startIcon = Icons.Rounded.Pages,
                            endIcon = Icons.Rounded.MiniEdit
                        )
                        var pages by rememberSaveable(showSelector) {
                            mutableStateOf(component.pdfToImageState?.selectedPages ?: emptyList())
                        }
                        EnhancedAlertDialog(
                            visible = showSelector,
                            onDismissRequest = { showSelector = false },
                            title = {
                                Text(stringResource(R.string.pages_selection))
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.Pages,
                                    contentDescription = null
                                )
                            },
                            text = {
                                PageInputField(
                                    selectedPages = pages,
                                    onPagesChanged = { pages = it }
                                )
                            },
                            dismissButton = {
                                EnhancedButton(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    onClick = {
                                        showSelector = false
                                    }
                                ) {
                                    Text(stringResource(R.string.close))
                                }
                            },
                            confirmButton = {
                                EnhancedButton(
                                    onClick = {
                                        component.updatePdfToImageSelection(pages)
                                        showSelector = false
                                    }
                                ) {
                                    Text(stringResource(R.string.apply))
                                }
                            }
                        )
                        Spacer(Modifier.height(8.dp))
                        PresetSelector(
                            value = component.presetSelected,
                            includeTelegramOption = false,
                            onValueChange = {
                                if (it is Preset.Percentage) {
                                    component.selectPreset(it)
                                }
                            },
                            showWarning = component.showOOMWarning
                        )
                        if (component.imageInfo.imageFormat.canChangeCompressionValue) {
                            Spacer(Modifier.height(8.dp))
                        }
                        QualitySelector(
                            imageFormat = component.imageInfo.imageFormat,
                            quality = component.imageInfo.quality,
                            onQualityChange = component::setQuality
                        )
                        Spacer(Modifier.height(8.dp))
                        ImageFormatSelector(
                            value = component.imageInfo.imageFormat,
                            onValueChange = component::updateImageFormat,
                            quality = component.imageInfo.quality,
                        )
                    }
                }
            }
        )
    }

    if (component.left != 0) {
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

    ExitBackHandler(
        enabled = component.haveChanges,
        onBack = onBack
    )

    ExitWithoutSavingDialog(
        onExit = component::clearAll,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )
}

@Composable
private fun rememberCanSaveOrShare(
    selectedPages: List<Int>?,
    pdfType: Screen.PdfTools.Type?
) = remember(selectedPages, pdfType) {
    derivedStateOf {
        (selectedPages?.isNotEmpty() != false && pdfType is Screen.PdfTools.Type.PdfToImages) || pdfType !is Screen.PdfTools.Type.PdfToImages
    }
}