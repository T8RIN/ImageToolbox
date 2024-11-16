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

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isLandscapeOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberQrCodeScanner
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.LinkPreviewList
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.components.QrCodePreview
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import kotlin.math.roundToInt

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ScanQrCodeContent(
    onGoBack: () -> Unit,
    component: ScanQrCodeComponent
) {
    val context = LocalComponentActivity.current

    val essentials = rememberLocalEssentials()
    val scope = essentials.coroutineScope
    val showConfetti: () -> Unit = essentials::showConfetti

    var qrContent by rememberSaveable(component.initialQrCodeContent) {
        mutableStateOf(component.initialQrCodeContent ?: "")
    }

    val scanner = rememberQrCodeScanner {
        qrContent = it
    }

    LaunchedEffect(qrContent) {
        component.addTemplateFilterFromString(
            string = qrContent,
            onSuccess = { filterName, filtersCount ->
                essentials.showToast(
                    message = context.getString(
                        R.string.added_filter_template,
                        filterName,
                        filtersCount
                    ),
                    icon = Icons.Outlined.AutoFixHigh
                )
            },
            onFailure = {}
        )
    }

    var qrImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var qrDescription by rememberSaveable {
        mutableStateOf("")
    }

    var qrCornersSize by rememberSaveable {
        mutableIntStateOf(4)
    }

    val settingsState = LocalSettingsState.current
    var qrDescriptionFont by rememberSaveable(
        inputs = arrayOf(settingsState.font),
        stateSaver = UiFontFamily.Saver
    ) {
        mutableStateOf(settingsState.font)
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

    val isLandscape by isLandscapeOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = true,
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.qr_code),
                input = null,
                isLoading = false,
                size = null,
            )
        },
        onGoBack = onGoBack,
        actions = {
            ShareButton(
                enabled = qrContent.isNotEmpty(),
                onShare = {
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        component.shareImage(
                            bitmap = bitmap,
                            onComplete = showConfetti
                        )
                    }
                },
                onCopy = { manager ->
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        component.cacheImage(bitmap) { uri ->
                            manager.setClip(uri.asClip(context))
                            showConfetti()
                        }
                    }
                }
            )
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        showImagePreviewAsStickyHeader = false,
        imagePreview = {
            if (isLandscape) {
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = true,
                    qrImageUri = qrImageUri,
                    qrDescription = qrDescription,
                    qrContent = qrContent,
                    qrCornersSize = qrCornersSize,
                    qrDescriptionFont = qrDescriptionFont
                )
            }
        },
        controls = {
            if (!isLandscape) {
                Spacer(modifier = Modifier.height(20.dp))
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = false,
                    qrImageUri = qrImageUri,
                    qrDescription = qrDescription,
                    qrContent = qrContent,
                    qrCornersSize = qrCornersSize,
                    qrDescriptionFont = qrDescriptionFont
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            LinkPreviewList(
                text = qrContent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            RoundedTextField(
                modifier = Modifier
                    .container(shape = RoundedCornerShape(24.dp))
                    .padding(8.dp),
                value = qrContent,
                onValueChange = {
                    qrContent = it
                },
                singleLine = false,
                label = {
                    Text(stringResource(id = R.string.code_content))
                },
                keyboardOptions = KeyboardOptions(),
                endIcon = {
                    AnimatedVisibility(qrContent.isNotBlank()) {
                        EnhancedIconButton(
                            onClick = { qrContent = "" },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = stringResource(R.string.cancel)
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .container(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                        resultPadding = 0.dp,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.scan_qr_code_to_replace_content),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedVisibility(visible = qrContent.isNotEmpty()) {
                Column {
                    Row(
                        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
                    ) {
                        ImageSelector(
                            value = qrImageUri,
                            subtitle = stringResource(id = R.string.watermarking_image_sub),
                            onValueChange = {
                                qrImageUri = it
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        BoxAnimatedVisibility(visible = qrImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        qrImageUri = null
                                    }
                                    .container(
                                        color = MaterialTheme.colorScheme.errorContainer,
                                        resultPadding = 0.dp
                                    )
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RoundedTextField(
                        modifier = Modifier
                            .container(
                                shape = animateShape(
                                    if (qrDescription.isNotEmpty()) ContainerShapeDefaults.topShape
                                    else ContainerShapeDefaults.defaultShape
                                )
                            )
                            .padding(8.dp),
                        value = qrDescription,
                        onValueChange = {
                            qrDescription = it
                        },
                        singleLine = false,
                        label = {
                            Text(stringResource(id = R.string.qr_description))
                        }
                    )
                    BoxAnimatedVisibility(visible = qrDescription.isNotEmpty()) {
                        FontSelector(
                            font = qrDescriptionFont,
                            onValueChange = { qrDescriptionFont = it },
                            color = Color.Unspecified,
                            shape = ContainerShapeDefaults.bottomShape,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    EnhancedSliderItem(
                        value = qrCornersSize,
                        title = stringResource(R.string.corners),
                        valueRange = 0f..24f,
                        onValueChange = {
                            qrCornersSize = it.toInt()
                        },
                        internalStateTransformation = {
                            it.roundToInt()
                        },
                        steps = 22
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (qrContent.isEmpty()) to !isLandscape,
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
                    if (!isLandscape) actions()
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
        },
        canShowScreenData = true,
        isPortrait = !isLandscape
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )

}