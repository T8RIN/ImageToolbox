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

import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.shreyaspatil.capturable.capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.QrCode
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberQrCodeScanner
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.viewModel.ScanQrCodeViewModel

@OptIn(ExperimentalComposeUiApi::class, ExperimentalComposeApi::class)
@Composable
fun ScanQrCodeScreen(
    qrCodeContent: String?,
    onGoBack: () -> Unit,
    viewModel: ScanQrCodeViewModel = hiltViewModel()
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current

    val confettiHostState = LocalConfettiHostState.current

    val scope = rememberCoroutineScope()

    val isLandscape =
        LocalWindowSizeClass.current.widthSizeClass != WindowWidthSizeClass.Compact || LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    var qrContent by rememberSaveable(qrCodeContent) { mutableStateOf(qrCodeContent ?: "") }

    val scanner = rememberQrCodeScanner {
        qrContent = it
    }

    var qrImageUri by rememberSaveable {
        mutableStateOf<Uri?>(null)
    }

    var qrDescription by rememberSaveable {
        mutableStateOf("")
    }

    //TODO: Add fields above

    val captureController = rememberCaptureController()

    val saveBitmap: (oneTimeSaveLocationUri: String?, bitmap: Bitmap) -> Unit =
        { oneTimeSaveLocationUri, bitmap ->
            viewModel.saveBitmap(bitmap, oneTimeSaveLocationUri) { saveResult ->
                context.parseSaveResult(
                    saveResult = saveResult,
                    onSuccess = {
                        confettiHostState.showConfetti()
                    },
                    toastHostState = toastHostState,
                    scope = scope
                )
            }
        }

    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    AdaptiveLayoutScreen(
        title = {
            Text(
                text = stringResource(R.string.qr_code),
                textAlign = TextAlign.Center
            )
        },
        onGoBack = onGoBack,
        actions = {
            ShareButton(
                enabled = qrCodeContent?.isNotEmpty() == true,
                onShare = {
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        viewModel.shareImage(bitmap, showConfetti)
                    }
                },
                onCopy = { manager ->
                    scope.launch {
                        val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                        viewModel.cacheImage(bitmap) { uri ->
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
        imagePreview = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(Modifier.capturable(captureController)) {
                    if (qrImageUri != null) {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    BoxWithConstraints(
                        modifier = Modifier
                            .then(
                                if (qrImageUri != null) {
                                    Modifier
                                        .background(
                                            color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp)
                                } else Modifier
                            )
                    ) {
                        val targetSize = min(min(maxWidth, maxHeight), 300.dp)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            QrCode(
                                content = qrContent,
                                modifier = Modifier
                                    .padding(
                                        top = if (qrImageUri != null) 36.dp else 0.dp,
                                        bottom = if (qrDescription.isNotEmpty()) 16.dp else 0.dp
                                    )
                                    .size(targetSize)
                            )

                            BoxAnimatedVisibility(visible = qrDescription.isNotEmpty()) {
                                Text(
                                    text = qrDescription,
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(targetSize)
                                )
                            }
                        }

                        if (qrImageUri != null) {
                            Picture(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-48).dp)
                                    .size(64.dp),
                                model = qrImageUri,
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                shape = MaterialTheme.shapes.medium
                            )
                        }
                    }
                }
            }
        },
        controls = {
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
                endIcon = {
                    AnimatedVisibility(qrContent.isNotBlank()) {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
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
        },
        buttons = { actions ->
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                targetState = (false) to !isLandscape,
                secondaryButtonIcon = Icons.Outlined.QrCodeScanner,
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
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = {
                        scope.launch {
                            val bitmap = captureController.captureAsync().await().asAndroidBitmap()
                            saveBitmap(it, bitmap)
                        }
                    }
                )
            }
        },
        canShowScreenData = true,
        isPortrait = !isLandscape
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            onCancelLoading = viewModel::cancelSaving
        )
    }
}