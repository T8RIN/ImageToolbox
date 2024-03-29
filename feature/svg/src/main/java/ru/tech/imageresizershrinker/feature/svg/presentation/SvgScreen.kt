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

package ru.tech.imageresizershrinker.feature.svg.presentation

import android.app.Activity
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FolderOff
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.failedToSaveImages
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.feature.svg.presentation.viewModel.SvgViewModel


@Composable
fun SvgScreen(
    uriState: List<Uri>?,
    onGoBack: () -> Unit,
    viewModel: SvgViewModel = hiltViewModel()
) {
    val context = LocalContext.current as Activity

    LaunchedEffect(uriState) {
        uriState?.let { viewModel.setUris(it) }
    }

    val toastHostState = LocalToastHostState.current

    val settingsState = LocalSettingsState.current
    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }
    val onError: (Throwable) -> Unit = {
        scope.launch {
            toastHostState.showError(context, it)
        }
    }

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.uris.isNotEmpty()) {
            showExitDialog = true
        } else onGoBack()
    }

    val pickImagesLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let(viewModel::setUris)
    }

    AutoFilePicker(
        onAutoPick = pickImagesLauncher::pickImage,
        isPickedAlready = !uriState.isNullOrEmpty()
    )

    val addImagesLauncher = rememberImagePicker(
        mode = localImagePickerMode(Picker.Multiple)
    ) { list ->
        list.takeIf { it.isNotEmpty() }?.let(viewModel::addUris)
    }

    val isPortrait =
        LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

    AdaptiveLayoutScreen(
        title = {
            Text(stringResource(R.string.images_to_svg))
        },
        topAppBarPersistentActions = {
            TopAppBarEmoji()
        },
        onGoBack = onBack,
        actions = {
            EnhancedIconButton(
                containerColor = Color.Transparent,
                contentColor = LocalContentColor.current,
                enableAutoShadowAndBorder = false,
                onClick = {
                    viewModel.performSharing(
                        onError = onError,
                        onComplete = showConfetti
                    )
                },
                enabled = !viewModel.isSaving && viewModel.uris.isNotEmpty()
            ) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        },
        imagePreview = {},
        showImagePreviewAsStickyHeader = false,
        placeImagePreview = false,
        noDataControls = {
            ImageNotPickedWidget(onPickImage = pickImagesLauncher::pickImage)
        },
        controls = {
            Spacer(modifier = Modifier.height(20.dp))
            UrisPreview(
                uris = viewModel.uris,
                isPortrait = isPortrait,
                onRemoveUri = viewModel::removeUri,
                onAddUris = {
                    runCatching {
                        addImagesLauncher.pickImage()
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
            )
        },
        buttons = {
            BottomButtonsBlock(
                targetState = viewModel.uris.isEmpty() to isPortrait,
                onSecondaryButtonClick = {
                    runCatching {
                        pickImagesLauncher.pickImage()
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
                isPrimaryButtonVisible = viewModel.uris.isNotEmpty(),
                onPrimaryButtonClick = {
                    viewModel.save { results, savingPath ->
                        context.failedToSaveImages(
                            scope = scope,
                            results = results,
                            toastHostState = toastHostState,
                            savingPathString = savingPath,
                            isOverwritten = settingsState.overwriteFiles,
                            showConfetti = showConfetti
                        )
                    }
                },
                actions = {
                    it()
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