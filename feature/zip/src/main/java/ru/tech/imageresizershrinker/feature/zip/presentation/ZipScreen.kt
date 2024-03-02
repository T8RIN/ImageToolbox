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

package ru.tech.imageresizershrinker.feature.zip.presentation

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ContextualFlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.NoteAdd
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiController
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.ReviewHandler.showReview
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.feature.zip.presentation.viewModel.ZipViewModel
import java.security.InvalidKeyException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ZipScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: ZipViewModel = hiltViewModel()
) {
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(uriState) {
        uriState?.let { viewModel.setUris(it) }
    }

    val context = LocalContext.current
    val settingsState = LocalSettingsState.current
    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val confettiController = LocalConfettiController.current

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.uris.isNotEmpty() && viewModel.byteArray != null) {
            showExitDialog = true
        } else onGoBack()
    }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = CreateDocument(),
        onResult = {
            it?.let { uri ->
                viewModel.saveResultTo(
                    outputStream = context.contentResolver.openOutputStream(uri, "rw")
                ) { t ->
                    if (t != null) {
                        scope.launch {
                            toastHostState.showError(context, t)
                        }
                    } else {
                        scope.launch {
                            confettiController.showEmpty()
                        }
                        scope.launch {
                            toastHostState.showToast(
                                context.getString(
                                    R.string.saved_to_without_filename,
                                    ""
                                ),
                                Icons.Rounded.Save
                            )
                            showReview(context)
                        }
                    }
                }
            }
        }
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            uris.takeIf { it.isNotEmpty() }?.let(viewModel::setUris)
        }
    )

    val additionalFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments(),
        onResult = { uris ->
            uris.takeIf { it.isNotEmpty() }?.let(viewModel::addUris)
        }
    )

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    AdaptiveLayoutScreen(
        title = {
            Text(stringResource(R.string.zip))
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        onGoBack = onBack,
        actions = {},
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
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
                            runCatching {
                                filePicker.launch(arrayOf("*/*"))
                            }.onFailure {
                                scope.launch {
                                    toastHostState.showToast(
                                        message = context.getString(R.string.activate_files),
                                        icon = Icons.Outlined.FolderOff,
                                        duration = ToastDuration.Long
                                    )
                                }
                            }
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
            AnimatedVisibility(visible = viewModel.byteArray != null) {
                LaunchedEffect(it) {
                    it.animateScrollToItem(0)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                        .container(
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme
                                .colorScheme
                                .surfaceColorAtElevation(10.dp),
                            resultPadding = 0.dp
                        )
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.CheckCircle,
                            null,
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
                    var name by rememberSaveable(viewModel.byteArray, viewModel.uris) {
                        val count = viewModel.uris.size.let {
                            if (it > 1) "($it)"
                            else ""
                        }
                        val timeStamp = SimpleDateFormat(
                            "yyyy-MM-dd_HH-mm-ss",
                            Locale.getDefault()
                        ).format(Date())

                        mutableStateOf("ZIP${count}_$timeStamp.zip")
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
                                    saveLauncher.launch("*/*#$name")
                                }.onFailure {
                                    scope.launch {
                                        toastHostState.showToast(
                                            message = context.getString(
                                                R.string.activate_files
                                            ),
                                            icon = Icons.Outlined.FolderOff,
                                            duration = ToastDuration.Long
                                        )
                                    }
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
                                    Icons.Rounded.FileDownload,
                                    null
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
                                viewModel.byteArray?.let {
                                    viewModel.shareFile(
                                        it = it,
                                        filename = name
                                    ) {
                                        scope.launch {
                                            confettiController.showEmpty()
                                        }
                                    }
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
                                Icon(Icons.Rounded.Share, null)
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
            Spacer(modifier = Modifier.height(24.dp))
            BoxWithConstraints {
                val size = viewModel.uris.size + 1f

                val count = if (isPortrait) {
                    size.coerceAtLeast(2f).coerceAtMost(3f)
                } else {
                    size.coerceAtLeast(2f).coerceAtMost(8f)
                }

                val width = maxWidth / count - 2.dp * (count - 1)

                ContextualFlowRow(
                    itemCount = viewModel.uris.size + 1,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) { index ->
                    val uri = viewModel.uris.getOrNull(index)
                    if (uri != null) {
                        Box(
                            modifier = Modifier.container(
                                shape = RoundedCornerShape(4.dp),
                                resultPadding = 0.dp,
                                color = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        ) {
                            Picture(
                                model = uri,
                                error = {
                                    Box {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(width / 3f)
                                                .align(Alignment.Center),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .width(width)
                                    .aspectRatio(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Color.Black.copy(0.5f)),
                            ) {
                                Text(
                                    text = (index + 1).toString(),
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart)
                                )
                                Icon(
                                    imageVector = Icons.Rounded.RemoveCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(0.2f))
                                        .clickable {
                                            viewModel.removeUri(uri)
                                        }
                                        .padding(4.dp)
                                        .align(Alignment.TopEnd),
                                    tint = Color.White.copy(0.7f),
                                )
                                val filename by remember(uri) {
                                    derivedStateOf {
                                        context.getFilename(uri)
                                    }
                                }
                                filename?.let {
                                    AutoSizeText(
                                        text = it,
                                        style = LocalTextStyle.current.copy(
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            lineHeight = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.End
                                        ),
                                        maxLines = 3,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .align(Alignment.BottomEnd)
                                    )
                                }
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .container(
                                    shape = RoundedCornerShape(4.dp),
                                    resultPadding = 0.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                                )
                                .width(width)
                                .aspectRatio(1f)
                                .clickable {
                                    runCatching {
                                        additionalFilePicker.launch(arrayOf("*/*"))
                                    }.onFailure {
                                        scope.launch {
                                            toastHostState.showToast(
                                                message = context.getString(R.string.activate_files),
                                                icon = Icons.Outlined.FolderOff,
                                                duration = ToastDuration.Long
                                            )
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.NoteAdd,
                                contentDescription = null,
                                modifier = Modifier.size(width / 3f)
                            )
                        }
                    }
                }
            }
        },
        buttons = {
            BottomButtonsBlock(
                targetState = viewModel.uris.isEmpty() to isPortrait,
                onSecondaryButtonClick = {
                    runCatching {
                        filePicker.launch(arrayOf("*/*"))
                    }.onFailure {
                        scope.launch {
                            toastHostState.showToast(
                                message = context.getString(R.string.activate_files),
                                icon = Icons.Outlined.FolderOff,
                                duration = ToastDuration.Long
                            )
                        }
                    }
                },
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                isPrimaryButtonVisible = viewModel.uris.isNotEmpty(),
                onPrimaryButtonClick = {
                    viewModel.startCompression {
                        if (it is InvalidKeyException) {
                            scope.launch {
                                toastHostState.showToast(
                                    context.getString(R.string.invalid_password_or_not_encrypted),
                                    Icons.Rounded.ErrorOutline
                                )
                            }
                        } else if (it != null) {
                            scope.launch {
                                toastHostState.showError(context, it)
                            }
                        }
                    }
                },
                actions = {
                    EnhancedChip(
                        selected = true,
                        onClick = null,
                        selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(viewModel.uris.size.toString())
                    }
                }
            )
        },
        canShowScreenData = viewModel.uris.isNotEmpty(),
        isPortrait = isPortrait
    )

    ExitWithoutSavingDialog(
        onExit = onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    if (viewModel.isSaving) {
        LoadingDialog(
            done = viewModel.done,
            left = viewModel.left,
            onCancelLoading = viewModel::cancelSaving
        )
    }

}

private class CreateDocument : ActivityResultContracts.CreateDocument("*/*") {
    override fun createIntent(
        context: Context,
        input: String
    ): Intent {
        return super.createIntent(
            context = context,
            input = input.split("#")[0]
        ).putExtra(Intent.EXTRA_TITLE, input.split("#")[1])
    }
}