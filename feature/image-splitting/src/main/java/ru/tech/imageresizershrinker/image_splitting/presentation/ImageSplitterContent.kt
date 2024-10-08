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

package ru.tech.imageresizershrinker.image_splitting.presentation

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.helper.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.localImagePickerMode
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResults
import ru.tech.imageresizershrinker.core.ui.utils.helper.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TopAppBarTitle
import ru.tech.imageresizershrinker.image_splitting.presentation.components.SplitParamsSelector
import ru.tech.imageresizershrinker.image_splitting.presentation.viewModel.ImageSplitterViewModel

@Composable
fun ImageSplitterContent(
    uriState: Uri?,
    onGoBack: () -> Unit,
    onNavigate: (Screen) -> Unit,
    viewModel: ImageSplitterViewModel = hiltViewModel(),
) {
    val context = LocalContext.current as ComponentActivity
    val toastHostState = LocalToastHostState.current

    val settingsState = LocalSettingsState.current

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }

    LaunchedEffect(uriState) {
        uriState?.let {
            viewModel.updateUri(it)
        }
    }

    val imagePicker = rememberImagePicker(
        mode = localImagePickerMode(Picker.Single)
    ) { list ->
        list.firstOrNull()?.let { uri ->
            viewModel.updateUri(uri)
        }
    }

    val pickImage = imagePicker::pickImage

    AutoFilePicker(
        onAutoPick = pickImage,
        isPickedAlready = uriState != null
    )

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        viewModel.saveBitmaps(it) { results ->
            context.parseSaveResults(
                scope = scope,
                results = results,
                toastHostState = toastHostState,
                isOverwritten = settingsState.overwriteFiles,
                showConfetti = showConfetti
            )
        }
    }

    val isPortrait by isPortraitOrientationAsState()

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (viewModel.haveChanges) showExitDialog = true
        else onGoBack()
    }

    @Composable
    fun ImagePreview(showUrisPreview: Boolean) {
        if (showUrisPreview) {
            var aspectRatio by remember {
                mutableFloatStateOf(2f / 1f)
            }
            Picture(
                model = viewModel.uri,
                contentDescription = null,
                modifier = Modifier
                    .padding(top = if (isPortrait) 20.dp else 0.dp)
                    .heightIn(max = 200.dp)
                    .aspectRatio(aspectRatio)
                    .container()
                    .padding(4.dp)
                    .clip(MaterialTheme.shapes.medium),
                onSuccess = {
                    aspectRatio = it.result.drawable.safeAspectRatio
                }
            )
        } else {
            UrisPreview(
                uris = viewModel.uris.ifEmpty {
                    listOf(Uri.EMPTY, Uri.EMPTY)
                },
                modifier = Modifier
                    .animateContentSize()
                    .padding(2.dp),
                isPortrait = true,
                onRemoveUri = null,
                onAddUris = null,
                isAddUrisVisible = viewModel.isImageLoading,
                addUrisContent = { width ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading(modifier = Modifier.size(width / 2))
                    }
                }
            )
        }
    }

    AdaptiveLayoutScreen(
        title = {
            TopAppBarTitle(
                title = stringResource(R.string.image_splitting),
                input = viewModel.uri,
                isLoading = viewModel.isImageLoading,
                size = null
            )
        },
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }
            ShareButton(
                enabled = viewModel.uri != null,
                onShare = {
                    viewModel.shareBitmaps(showConfetti)
                },
                onEdit = {
                    viewModel.cacheImages {
                        editSheetData = it
                    }
                }
            )
            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    if (!it) {
                        editSheetData = emptyList()
                    }
                },
                onNavigate = { screen ->
                    scope.launch {
                        editSheetData = emptyList()
                        delay(200)
                        onNavigate(screen)
                    }
                }
            )
        },
        showImagePreviewAsStickyHeader = false,
        imagePreview = {
            ImagePreview(isPortrait)
        },
        controls = {
            Spacer(Modifier.height(8.dp))
            ImagePreview(!isPortrait)
            Spacer(Modifier.height(8.dp))
            val params = viewModel.params
            SplitParamsSelector(
                value = params,
                onValueChange = viewModel::updateParams
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
                targetState = (viewModel.uris.isEmpty()) to isPortrait,
                onSecondaryButtonClick = pickImage,
                onPrimaryButtonClick = {
                    saveBitmaps(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) actions()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )
            if (showFolderSelectionDialog) {
                OneTimeSaveLocationSelectionDialog(
                    onDismiss = { showFolderSelectionDialog = false },
                    onSaveRequest = saveBitmaps
                )
            }
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Single,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        topAppBarPersistentActions = {
            if (viewModel.uri == null) TopAppBarEmoji()
        },
        canShowScreenData = viewModel.uri != null,
        noDataControls = {
            if (!viewModel.isImageLoading) {
                ImageNotPickedWidget(onPickImage = pickImage)
            }
        },
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
            left = viewModel.uris.size,
            onCancelLoading = viewModel::cancelSaving
        )
    }
}