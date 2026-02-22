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

package com.t8rin.imagetoolbox.feature.document_scanner.presentation

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberDocumentScanner
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageFormatSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.QualitySelector
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.FileNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.image.ImagePager
import com.t8rin.imagetoolbox.core.ui.widget.image.UrisPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.document_scanner.presentation.screenLogic.DocumentScannerComponent


@Composable
fun DocumentScannerContent(
    component: DocumentScannerComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

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

    val documentScanner = rememberDocumentScanner {
        component.parseScanResult(it)
    }

    val additionalDocumentScanner = rememberDocumentScanner {
        component.addScanResult(it)
    }

    AutoFilePicker(
        onAutoPick = documentScanner::scan,
        isPickedAlready = false
    )

    val isPortrait by isPortraitOrientationAsState()

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it,
            onComplete = essentials::parseSaveResults
        )
    }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            Text(
                text = stringResource(R.string.document_scanner),
                modifier = Modifier.marquee()
            )
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        onGoBack = onBack,
        actions = {},
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
        addHorizontalCutoutPaddingIfNoPreview = false,
        noDataControls = {
            FileNotPickedWidget(
                onPickFile = documentScanner::scan,
                text = stringResource(R.string.click_to_start_scanning)
            )
        },
        controls = {
            var selectedUriForPreview by remember {
                mutableStateOf<Uri?>(null)
            }
            ImagePager(
                visible = selectedUriForPreview != null,
                selectedUri = selectedUriForPreview,
                uris = component.uris,
                onNavigate = {
                    selectedUriForPreview = null
                    component.onNavigate(it)
                },
                onUriSelected = { selectedUriForPreview = it },
                onShare = component::shareUri,
                onDismiss = { selectedUriForPreview = null }
            )
            Spacer(modifier = Modifier.height(24.dp))
            UrisPreview(
                uris = component.uris,
                isPortrait = isPortrait,
                onRemoveUri = component::removeImageUri,
                onAddUris = additionalDocumentScanner::scan,
                addUrisContent = { width ->
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlt,
                        contentDescription = stringResource(R.string.add),
                        modifier = Modifier.size(width / 3f)
                    )
                },
                onClickUri = { uri ->
                    selectedUriForPreview = uri
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .container(
                        resultPadding = 0.dp,
                        shape = ShapeDefaults.large,
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                    .padding(8.dp),
            ) {
                EnhancedButton(
                    onClick = {
                        component.sharePdf(showConfetti)
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 12.dp,
                        end = 20.dp
                    ),
                    shape = ShapeDefaults.top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = stringResource(R.string.share_as_pdf)
                    )
                    Spacer(Modifier.width(8.dp))
                    AutoSizeText(
                        text = stringResource(id = R.string.share_as_pdf),
                        maxLines = 2
                    )
                }
                Spacer(Modifier.height(4.dp))
                EnhancedButton(
                    onClick = {
                        savePdfLauncher.make(component.generatePdfFilename())
                    },
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 20.dp
                    ),
                    shape = ShapeDefaults.bottom,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Pdf,
                        contentDescription = stringResource(R.string.save_as_pdf)
                    )
                    Spacer(Modifier.width(8.dp))
                    AutoSizeText(
                        text = stringResource(id = R.string.save_as_pdf),
                        maxLines = 2
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            InfoContainer(
                modifier = Modifier.padding(8.dp),
                text = stringResource(R.string.options_below_is_for_images)
            )
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
                onValueChange = component::setImageFormat,
                quality = component.quality,
            )
        },
        buttons = {
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = documentScanner::scan,
                secondaryButtonIcon = Icons.Rounded.DocumentScanner,
                secondaryButtonText = stringResource(R.string.start_scanning),
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    ShareButton(
                        enabled = component.uris.isNotEmpty(),
                        onShare = {
                            component.shareBitmaps(showConfetti)
                        }
                    )
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = saveBitmaps,
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    LoadingDialog(
        visible = component.isSaving,
        done = component.done,
        left = component.left,
        onCancelLoading = component::cancelSaving
    )

}