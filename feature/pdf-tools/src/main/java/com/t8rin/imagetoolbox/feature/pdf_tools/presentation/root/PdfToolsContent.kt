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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root

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
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.rounded.FileOpen
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
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.ArtTrack
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.ImageReorderCarousel
import com.t8rin.imagetoolbox.core.ui.widget.controls.ScaleSmallImagesToLargeToggle
import com.t8rin.imagetoolbox.core.ui.widget.controls.page.PageSelectionItem
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.PresetSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitBackHandler
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.PdfToolsContentImpl
import com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.screenLogic.PdfToolsComponent
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
                    PreferenceItem(
                        onClick = {
                            component.setPdfPreview(tempSelectionUri)
                            showSelectionPdfPicker = false
                        },
                        startIcon = Icons.Outlined.Preview,
                        title = stringResource(R.string.preview_pdf),
                        subtitle = stringResource(R.string.preview_pdf_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    PreferenceItem(
                        onClick = {
                            component.setPdfToImagesUri(tempSelectionUri)
                            showSelectionPdfPicker = false
                        },
                        startIcon = Icons.Outlined.ArtTrack,
                        title = stringResource(R.string.pdf_to_images),
                        subtitle = stringResource(R.string.pdf_to_images_sub),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                items(Screen.PdfTools.options) { screen ->
                    PreferenceItem(
                        title = stringResource(screen.title),
                        subtitle = stringResource(screen.subtitle),
                        startIcon = screen.icon,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            showSelectionPdfPicker = false
                            component.onNavigate(
                                when (screen) {
                                    is Screen.PdfTools.Merge -> screen.copy(
                                        uris = tempSelectionUri?.let(
                                            ::listOf
                                        )
                                    )

                                    is Screen.PdfTools.RemovePages -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Split -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Rotate -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Rearrange -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Crop -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.PageNumbers -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Watermark -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Signature -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Compress -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Flatten -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Grayscale -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Repair -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Protect -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Unlock -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.Metadata -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.ExtractImages -> screen.copy(uri = tempSelectionUri)
                                    is Screen.PdfTools.OCR -> screen.copy(uri = tempSelectionUri)
                                    else -> screen
                                }
                            )
                        }
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
                    var editSheetData by remember {
                        mutableStateOf(listOf<Uri>())
                    }

                    ShareButton(
                        onShare = {
                            component.performSharing(
                                onSuccess = showConfetti,
                                onFailure = essentials::showFailureToast
                            )
                        },
                        onEdit = {
                            component.prepareForSharing(
                                onSuccess = {
                                    editSheetData = it
                                },
                                onFailure = essentials::showFailureToast
                            )
                        },
                        dialogTitle = if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                            stringResource(R.string.image)
                        } else {
                            "PDF"
                        },
                        dialogIcon = if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                            Icons.Outlined.Image
                        } else {
                            Icons.Outlined.Pdf
                        }
                    )

                    ProcessImagesPreferenceSheet(
                        uris = editSheetData,
                        visible = editSheetData.isNotEmpty(),
                        onDismiss = {
                            editSheetData = emptyList()
                        },
                        extraDataType = if (pdfType is Screen.PdfTools.Type.PdfToImages) {
                            null
                        } else {
                            ExtraDataType.Pdf
                        },
                        onNavigate = component.onNavigate
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
                        Spacer(Modifier.height(8.dp))
                        QualitySelector(
                            imageFormat = ImageFormat.Jpg,
                            quality = Quality.Base(component.quality),
                            onQualityChange = {
                                component.setQuality(it.qualityValue)
                            },
                            autoCoerce = false
                        )
                        Spacer(Modifier.height(8.dp))
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
                        PageSelectionItem(
                            value = component.pdfToImageState?.selectedPages,
                            onValueChange = component::updatePdfToImageSelection,
                            pagesCount = component.pdfToImageState?.pagesCount ?: 0
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