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

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.twotone.FileOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberFilePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.zip.presentation.screenLogic.ZipComponent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ZipContent(
    component: ZipComponent
) {
    val settingsState = LocalSettingsState.current

    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.uris.isNotEmpty() && component.byteArray != null) {
            showExitDialog = true
        } else component.onGoBack()
    }

    val saveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = {
            it?.let { uri ->
                component.saveResultTo(
                    uri = uri,
                    onResult = essentials::parseFileSaveResult
                )
            }
        }
    )

    val filePicker = rememberFilePicker(onSuccess = component::setUris)

    AutoFilePicker(
        onAutoPick = filePicker::pickFile,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val additionalFilePicker = rememberFilePicker(onSuccess = component::addUris)

    val isPortrait by isPortraitOrientationAsState()

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !(component.uris.isNotEmpty() && component.byteArray != null),
        title = {
            Text(
                text = stringResource(R.string.zip),
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
                        .hapticsClickable(onClick = filePicker::pickFile)
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
            AnimatedVisibility(visible = component.byteArray != null) {
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
                    var name by rememberSaveable(component.byteArray, component.uris) {
                        val count = component.uris.size.let {
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
                                    contentDescription = stringResource(R.string.share)
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
            Spacer(modifier = Modifier.height(24.dp))
            UrisPreview(
                uris = component.uris,
                isPortrait = isPortrait,
                onRemoveUri = component::removeUri,
                onAddUris = additionalFilePicker::pickFile
            )
        },
        buttons = {
            BottomButtonsBlock(
                targetState = component.uris.isEmpty() to isPortrait,
                onSecondaryButtonClick = filePicker::pickFile,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                isPrimaryButtonVisible = component.uris.isNotEmpty(),
                onPrimaryButtonClick = {
                    component.startCompression {
                        if (it != null) {
                            essentials.showFailureToast(
                                throwable = it
                            )
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
                        Text(component.uris.size.toString())
                    }
                }
            )
        },
        canShowScreenData = component.uris.isNotEmpty(),
        isPortrait = isPortrait
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