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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.extendedcolors.util.roundToTwoDigits
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isLandscapeOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberQrCodeScanner
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalComponentActivity
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.DataSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.scaleOnTap
import ru.tech.imageresizershrinker.core.ui.widget.other.BarcodeType
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.InfoContainer
import ru.tech.imageresizershrinker.core.ui.widget.other.LinkPreviewList
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.components.QrCodePreview
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.screenLogic.ScanQrCodeComponent
import kotlin.math.roundToInt

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

    val scanner = rememberQrCodeScanner {
        component.updateParams(
            params = params.copy(
                content = it
            )
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

    val isLandscape by isLandscapeOrientationAsState()

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
                            manager.copyToClipboard(uri.asClip(context))
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
                    params = params
                )
            }
        },
        controls = {
            if (!isLandscape) {
                Spacer(modifier = Modifier.height(20.dp))
                QrCodePreview(
                    captureController = captureController,
                    isLandscape = false,
                    params = params
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            LinkPreviewList(
                text = params.content,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            RoundedTextField(
                modifier = Modifier
                    .container(
                        shape = MaterialTheme.shapes.large,
                        resultPadding = 0.dp
                    )
                    .padding(
                        top = 8.dp,
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 6.dp
                    ),
                value = params.content,
                onValueChange = {
                    component.updateParams(
                        params.copy(
                            content = it
                        )
                    )
                },
                maxSymbols = 2500,
                singleLine = false,
                label = {
                    Text(stringResource(id = R.string.code_content))
                },
                keyboardOptions = KeyboardOptions(),
                endIcon = {
                    AnimatedVisibility(params.content.isNotBlank()) {
                        EnhancedIconButton(
                            onClick = {
                                component.updateParams(
                                    params.copy(
                                        content = ""
                                    )
                                )
                            },
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
            InfoContainer(
                text = stringResource(R.string.scan_qr_code_to_replace_content),
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            val params by rememberUpdatedState(params)

            AnimatedVisibility(visible = params.content.isNotEmpty()) {
                Column {
                    DataSelector(
                        value = params.type,
                        onValueChange = {
                            component.updateParams(
                                params.copy(
                                    type = it,
                                    heightRatio = if (params.type == BarcodeType.DATA_MATRIX) 1f
                                    else params.heightRatio
                                )
                            )
                        },
                        spanCount = 2,
                        entries = BarcodeType.entries,
                        title = stringResource(R.string.barcode_type),
                        titleIcon = Icons.Rounded.QrCode2,
                        itemContentText = {
                            remember {
                                it.name.replace("_", " ")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BoxAnimatedVisibility(
                        visible = !params.type.isSquare || params.type == BarcodeType.DATA_MATRIX,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EnhancedSliderItem(
                            value = params.heightRatio,
                            title = stringResource(R.string.height_ratio),
                            valueRange = 1f..4f,
                            onValueChange = {},
                            onValueChangeFinished = {
                                component.updateParams(
                                    params.copy(
                                        heightRatio = it
                                    )
                                )
                            },
                            internalStateTransformation = {
                                it.roundToTwoDigits()
                            },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    PreferenceRowSwitch(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        startIcon = Icons.Outlined.InvertColors,
                        title = stringResource(R.string.enforce_bw),
                        subtitle = stringResource(R.string.enforce_bw_sub),
                        checked = params.enforceBlackAndWhite,
                        onClick = {
                            component.updateParams(
                                params.copy(
                                    enforceBlackAndWhite = it
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.height(intrinsicSize = IntrinsicSize.Max)
                    ) {
                        ImageSelector(
                            value = params.imageUri,
                            subtitle = stringResource(id = R.string.watermarking_image_sub),
                            onValueChange = {
                                component.updateParams(
                                    params.copy(
                                        imageUri = it
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        BoxAnimatedVisibility(visible = params.imageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(start = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .hapticsClickable {
                                        component.updateParams(
                                            params.copy(
                                                imageUri = null
                                            )
                                        )
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
                                    if (params.description.isNotEmpty()) ContainerShapeDefaults.topShape
                                    else ContainerShapeDefaults.defaultShape
                                ),
                                resultPadding = 8.dp
                            ),
                        value = params.description,
                        onValueChange = {
                            component.updateParams(
                                params.copy(
                                    description = it
                                )
                            )
                        },
                        singleLine = false,
                        label = {
                            Text(stringResource(id = R.string.qr_description))
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BoxAnimatedVisibility(
                        visible = params.description.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FontSelector(
                            value = params.descriptionFont,
                            onValueChange = {
                                component.updateParams(
                                    params.copy(
                                        descriptionFont = it
                                    )
                                )
                            },
                            color = Color.Unspecified,
                            shape = ContainerShapeDefaults.bottomShape,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    EnhancedSliderItem(
                        value = params.cornersSize,
                        title = stringResource(R.string.corners),
                        valueRange = 0f..24f,
                        onValueChange = {
                            component.updateParams(
                                params.copy(
                                    cornersSize = it.toInt()
                                )
                            )
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
                targetState = (params.content.isEmpty()) to !isLandscape,
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