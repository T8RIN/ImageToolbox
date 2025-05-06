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

package ru.tech.imageresizershrinker.feature.scan_qr_code.presentation

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
import androidx.compose.material3.Badge
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberBarcodeScanner
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.other.BarcodeType
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.components.QrCodePreview
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.components.ScanQrCodeControls
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent

@SuppressLint("StringFormatInvalid")
@Composable
fun ScanQrCodeContent(
    component: ScanQrCodeComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    val params = component.params

    val scanner = rememberBarcodeScanner {
        component.updateParams(
            params = params.copy(
                content = it
            )
        )
    }

    val analyzerImagePicker = rememberImagePicker { uri: Uri ->
        component.readBarcodeFromImage(
            imageUri = uri,
            onFailure = {
                essentials.showFailureToast(
                    Throwable(context.getString(R.string.no_barcode_found), it)
                )
            }
        )
    }

    LaunchedEffect(params.content) {
        component.processFilterTemplateFromQrContent(
            onSuccess = { filterName, filtersCount ->
                essentials.showToast(
                    message = context.getString(
                        R.string.added_filter_template,
                        filterName,
                        filtersCount
                    ),
                    icon = Icons.Outlined.AutoFixHigh
                )
            }
        )
    }

    val captureController = rememberCaptureController()

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
                Badge(
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
                enabled = params.content.isNotEmpty(),
                onShare = {
                    scope.launch {
                        component.shareImage(
                            bitmap = captureController.bitmap(),
                            onComplete = showConfetti
                        )
                    }
                },
                onCopy = {
                    scope.launch {
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
                    params = params
                )
            }
        },
        controls = {
            if (isPortrait) {
                Spacer(modifier = Modifier.height(20.dp))
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = false,
                    params = params
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
                isNoData = params.content.isEmpty() && isPortrait,
                secondaryButtonIcon = Icons.Outlined.QrCodeScanner,
                secondaryButtonText = stringResource(R.string.start_scanning),
                onSecondaryButtonClick = scanner::scan,
                onPrimaryButtonClick = {
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        saveBitmap(null, bitmap)
                    }
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                showColumnarFabInRow = true,
                isPrimaryButtonVisible = isPortrait || params.content.isNotEmpty(),
                columnarFab = {
                    EnhancedFloatingActionButton(
                        onClick = analyzerImagePicker::pickImage,
                        onLongClick = {
                            showOneTimeImagePickingDialog = true
                        },
                        containerColor = takeColorFromScheme {
                            if (params.content.isEmpty()) tertiaryContainer
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
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        saveBitmap(it, bitmap)
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

private suspend fun CaptureController.bitmap(): Bitmap = captureAsync().await().asAndroidBitmap()