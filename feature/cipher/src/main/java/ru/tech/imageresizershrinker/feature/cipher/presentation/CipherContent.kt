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

package ru.tech.imageresizershrinker.feature.cipher.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.InsertDriveFile
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.domain.utils.toInt
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ShieldKey
import ru.tech.imageresizershrinker.core.resources.icons.ShieldOpen
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFilePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.fileSize
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.cipher.presentation.components.CipherTipSheet
import ru.tech.imageresizershrinker.feature.cipher.presentation.screenLogic.CipherComponent
import java.security.InvalidKeyException
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CipherContent(
    onGoBack: () -> Unit,
    component: CipherComponent
) {
    val haptics = LocalHapticFeedback.current

    val context = LocalContext.current
    val settingsState = LocalSettingsState.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }
    var showTip by rememberSaveable { mutableStateOf(false) }

    var key by rememberSaveable { mutableStateOf("") }

    val canGoBack = component.uri != null && (key.isNotEmpty() || component.byteArray != null)

    val onBack = {
        if (!canGoBack) showExitDialog = true
        else onGoBack()
    }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*"),
        onResult = {
            it?.let { uri ->
                component.saveCryptographyTo(
                    uri = uri,
                    onResult = essentials::parseFileSaveResult
                )
            }
        }
    )

    val filePicker = rememberFilePicker(onSuccess = component::setUri)

    AutoFilePicker(
        onAutoPick = filePicker::pickFile,
        isPickedAlready = component.initialUri != null
    )

    val focus = LocalFocusManager.current

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        title = {
            AnimatedContent(
                targetState = component.uri to component.isEncrypt,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.marquee()
            ) { (uri, isEncrypt) ->
                Text(
                    if (uri == null) {
                        stringResource(R.string.cipher)
                    } else {
                        listOf(
                            stringResource(R.string.encryption),
                            stringResource(R.string.decryption)
                        )[if (isEncrypt) 0 else 1]
                    },
                    textAlign = TextAlign.Center
                )
            }
        },
        onGoBack = onBack,
        shouldDisableBackHandler = canGoBack,
        topAppBarPersistentActions = {
            if (!isPortrait || component.uri == null) TopAppBarEmoji()
        },
        actions = {},
        buttons = {
            BottomButtonsBlock(
                targetState = (component.uri == null) to isPortrait,
                onSecondaryButtonClick = filePicker::pickFile,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                onPrimaryButtonClick = {
                    focus.clearFocus()
                    component.startCryptography(
                        key = key,
                        onFileRequest = { uri ->
                            context
                                .contentResolver
                                .openInputStream(uri)
                                ?.use { it.readBytes() }
                        }
                    ) {
                        if (it is InvalidKeyException) {
                            essentials.showToast(
                                message = context.getString(R.string.invalid_password_or_not_encrypted),
                                icon = Icons.Rounded.ErrorOutline
                            )
                        } else if (it != null) {
                            essentials.showFailureToast(
                                throwable = it
                            )
                        }
                    }
                },
                primaryButtonIcon = if (component.isEncrypt) {
                    Icons.Rounded.ShieldKey
                } else {
                    Icons.Rounded.ShieldOpen
                },
                primaryButtonText = if (isPortrait) {
                    if (component.isEncrypt) {
                        stringResource(R.string.encrypt)
                    } else {
                        stringResource(R.string.decrypt)
                    }
                } else "",
                isPrimaryButtonEnabled = key.isNotEmpty(),
                actions = {
                    if (isPortrait) {
                        Spacer(Modifier.width(12.dp))
                        TopAppBarEmoji()
                    }
                }
            )
        },
        canShowScreenData = component.uri != null,
        noDataControls = {
            Column(
                modifier = Modifier.container(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.TwoTone.FileOpen,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .container(
                            shape = CloverShape,
                            resultPadding = 0.dp,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        .clickable {
                            haptics.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                            filePicker.pickFile()
                        }
                        .padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(R.string.pick_file_to_start),
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        controls = {
            AnimatedContent(targetState = component.uri != null) { hasUri ->
                if (hasUri) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isPortrait) Spacer(Modifier.height(20.dp))
                        Row(
                            modifier = Modifier
                                .container(CircleShape)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val items = listOf(
                                stringResource(R.string.encryption),
                                stringResource(R.string.decryption)
                            )
                            ToggleGroupButton(
                                enabled = true,
                                itemCount = items.size,
                                selectedIndex = (!component.isEncrypt).toInt(),
                                onIndexChange = {
                                    component.setIsEncrypt(it == 0)
                                },
                                itemContent = {
                                    Text(
                                        text = items[it],
                                        fontSize = 12.sp
                                    )
                                },
                                isScrollable = false,
                                modifier = Modifier.weight(1f)
                            )
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    showTip = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.HelpOutline,
                                    contentDescription = "Info"
                                )
                            }
                        }

                        component.uri?.let { uri ->
                            PreferenceItem(
                                modifier = Modifier.padding(top = 16.dp),
                                title = context.getFilename(uri)
                                    ?: stringResource(R.string.something_went_wrong),
                                onClick = null,
                                titleFontStyle = LocalTextStyle.current.copy(
                                    lineHeight = 16.sp,
                                    fontSize = 15.sp
                                ),
                                subtitle = component.uri?.let {
                                    stringResource(
                                        id = R.string.size,
                                        readableByteCount(
                                            it.fileSize(context) ?: 0L
                                        )
                                    )
                                },
                                startIcon = Icons.AutoMirrored.Rounded.InsertDriveFile
                            )

                            RoundedTextField(
                                modifier = Modifier
                                    .padding(top = 16.dp)
                                    .container(shape = RoundedCornerShape(24.dp))
                                    .padding(8.dp),
                                value = key,
                                startIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            key = component.generateRandomPassword()
                                            component.resetCalculatedData()
                                        },
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Shuffle,
                                            contentDescription = stringResource(R.string.shuffle),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                },
                                endIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            key = ""
                                            component.resetCalculatedData()
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Cancel,
                                            contentDescription = stringResource(R.string.cancel),
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                singleLine = false,
                                onValueChange = {
                                    key = it
                                    component.resetCalculatedData()
                                },
                                label = {
                                    Text(stringResource(R.string.key))
                                }
                            )
                        }
                        AnimatedVisibility(visible = component.byteArray != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp)
                                    .container(
                                        shape = MaterialTheme.shapes.extraLarge,
                                        color = MaterialTheme
                                            .colorScheme
                                            .surfaceContainerHighest,
                                        resultPadding = 0.dp
                                    )
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Rounded.CheckCircle,
                                        contentDescription = null,
                                        tint = Green,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surface,
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = settingsState.borderWidth,
                                                color = MaterialTheme.colorScheme.outlineVariant(),
                                                shape = CircleShape
                                            )
                                            .padding(4.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        stringResource(R.string.file_proceed),
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.store_file_desc),
                                    fontSize = 13.sp,
                                    color = LocalContentColor.current.copy(alpha = 0.7f),
                                    lineHeight = 14.sp,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                                var name by rememberSaveable(component.byteArray) {
                                    mutableStateOf(
                                        if (component.isEncrypt) {
                                            "enc-"
                                        } else {
                                            "dec-"
                                        } + (component.uri?.let {
                                            context.getFilename(it)
                                        } ?: Random.nextInt())
                                    )
                                }
                                RoundedTextField(
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .container(shape = RoundedCornerShape(24.dp))
                                        .padding(8.dp),
                                    value = name,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    singleLine = false,
                                    onValueChange = { name = it },
                                    label = {
                                        Text(stringResource(R.string.filename))
                                    }
                                )

                                Row(
                                    modifier = Modifier
                                        .padding(top = 24.dp)
                                        .fillMaxWidth()
                                ) {
                                    EnhancedButton(
                                        onClick = {
                                            runCatching {
                                                saveLauncher.launch(name)
                                            }.onFailure {
                                                essentials.showActivateFilesToast()
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .fillMaxWidth(0.5f)
                                            .height(50.dp),
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.FileDownload,
                                                contentDescription = null
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            AutoSizeText(
                                                text = stringResource(id = R.string.save),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                    EnhancedButton(
                                        onClick = {
                                            component.byteArray?.let {
                                                component.shareFile(
                                                    it = it,
                                                    filename = name,
                                                    onComplete = showConfetti
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Share,
                                                contentDescription = stringResource(
                                                    R.string.share
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            AutoSizeText(
                                                text = stringResource(id = R.string.share),
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
        isPortrait = isPortrait,
        showActionsInTopAppBar = false
    )

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    CipherTipSheet(
        visible = showTip,
        onDismiss = {
            showTip = false
        }
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )

}