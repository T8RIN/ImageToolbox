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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.capturable.rememberCaptureController
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.rememberBarcodeScanner
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.scaleOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.BarcodeType
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.other.renderAsQr
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.QrCodePreview
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.ScanQrCodeControls
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("StringFormatInvalid")
@Composable
fun ScanQrCodeContent(
    component: ScanQrCodeComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val params = component.params

    val scanner = rememberBarcodeScanner {
        component.updateParams(
            params = params.copy(
                content = it
            )
        )
    }

    val isNotScannable = params.content.raw.isNotEmpty() && component.mayBeNotScannable
    val isSaveEnabled = params.content.raw.isNotEmpty() && component.isSaveEnabled

    val analyzerImagePicker = rememberImagePicker { uri: Uri ->
        component.readBarcodeFromImage(
            image = uri,
            onFailure = {
                essentials.showFailureToast(
                    Throwable(essentials.getString(R.string.no_barcode_found), it)
                )
            }
        )
    }

    val captureController = rememberCaptureController()

    LaunchedEffect(params) {
        if (params.content.raw.isEmpty()) return@LaunchedEffect
        delay(500)
        component.syncReadBarcodeFromImage(
            image = params.qrParams.renderAsQr(
                content = params.content.raw,
                type = params.type
            )
        )
    }

    LaunchedEffect(params.content) {
        component.processFilterTemplateFromQrContent(
            onSuccess = { filterName, filtersCount ->
                essentials.showToast(
                    message = essentials.getString(
                        R.string.added_filter_template,
                        filterName,
                        filtersCount
                    ),
                    icon = Icons.Outlined.AutoFixHigh
                )
            }
        )
    }

    val saveBitmap: (oneTimeSaveLocationUri: String?, bitmap: Bitmap) -> Unit =
        { oneTimeSaveLocationUri, bitmap ->
            component.saveBitmap(
                bitmap = bitmap,
                oneTimeSaveLocationUri = oneTimeSaveLocationUri,
                onComplete = essentials::parseSaveResult
            )
        }

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.marquee()
            ) {
                Text(
                    text = stringResource(R.string.qr_code)
                )
                EnhancedBadge(
                    content = {
                        Text(
                            text = BarcodeType.entries.size.toString()
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .padding(bottom = 12.dp)
                        .scaleOnTap {
                            showConfetti()
                        }
                )
            }
        },
        onGoBack = component.onGoBack,
        actions = {
            ShareButton(
                enabled = params.content.raw.isNotEmpty(),
                onShare = {
                    essentials.launch {
                        component.shareImage(
                            bitmap = captureController.bitmap(),
                            onComplete = showConfetti
                        )
                    }
                },
                onCopy = {
                    essentials.launch {
                        component.cacheImage(
                            bitmap = captureController.bitmap(),
                            onComplete = essentials::copyToClipboard
                        )
                    }
                }
            )
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        showImagePreviewAsStickyHeader = false,
        imagePreview = {
            if (!isPortrait) {
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = true,
                    params = params,
                    onStartScan = scanner::scan
                )
            }
        },
        controls = {
            if (isPortrait) {
                Spacer(modifier = Modifier.height(20.dp))
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = false,
                    params = params,
                    onStartScan = scanner::scan
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            ScanQrCodeControls(
                component = component
            )
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = params.content.raw.isEmpty() && isPortrait,
                secondaryButtonIcon = Icons.Outlined.QrCodeScanner,
                secondaryButtonText = stringResource(R.string.start_scanning),
                onSecondaryButtonClick = scanner::scan,
                isPrimaryButtonEnabled = isSaveEnabled,
                onPrimaryButtonClick = {
                    essentials.launch {
                        saveBitmap(null, captureController.bitmap())
                    }
                },
                primaryButtonContainerColor = takeColorFromScheme {
                    if (isNotScannable) error
                    else primaryContainer
                },
                primaryButtonContentColor = takeColorFromScheme {
                    if (isNotScannable) onError
                    else onPrimaryContainer
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                isPrimaryButtonVisible = isPortrait || params.content.raw.isNotEmpty(),
                actions = {
                    if (isPortrait) actions()
                },
                showMiddleFabInRow = true,
                middleFab = {
                    EnhancedFloatingActionButton(
                        onClick = analyzerImagePicker::pickImage,
                        onLongClick = {
                            showOneTimeImagePickingDialog = true
                        },
                        containerColor = takeColorFromScheme {
                            if (params.content.raw.isEmpty()) tertiaryContainer
                            else secondaryContainer
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ImageSearch,
                            contentDescription = null
                        )
                    }
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = {
                    essentials.launch {
                        saveBitmap(it, captureController.bitmap())
                    }
                },
                formatForFilenameSelection = component.getFormatForFilenameSelection()
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = analyzerImagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = true
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )

}