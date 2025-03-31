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

package ru.tech.imageresizershrinker.feature.svg_maker.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.ImageReset
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.rememberImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.AdaptiveLayoutScreen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ExitWithoutSavingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.LoadingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.ResetDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.AutoFilePicker
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageNotPickedWidget
import ru.tech.imageresizershrinker.core.ui.widget.image.UrisPreview
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.feature.svg_maker.domain.SvgParams
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.components.SvgParamsSelector
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.screenLogic.SvgMakerComponent


@Composable
fun SvgMakerContent(
    component: SvgMakerComponent
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val onFailure: (Throwable) -> Unit = essentials::showFailureToast

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    val imagePicker = rememberImagePicker(onSuccess = component::setUris)

    AutoFilePicker(
        onAutoPick = imagePicker::pickImage,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    val addImagesImagePicker = rememberImagePicker(onSuccess = component::addUris)

    val isPortrait by isPortraitOrientationAsState()

    var showResetDialog by rememberSaveable { mutableStateOf(false) }

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        title = {
            Text(
                text = stringResource(R.string.images_to_svg),
                modifier = Modifier.marquee()
            )
        },
        topAppBarPersistentActions = {
            if (isPortrait) {
                TopAppBarEmoji()
            }
        },
        onGoBack = onBack,
        actions = {
            ShareButton(
                onShare = {
                    component.performSharing(
                        onFailure = onFailure,
                        onComplete = showConfetti
                    )
                },
                enabled = !component.isSaving && component.uris.isNotEmpty()
            )
            EnhancedIconButton(
                enabled = component.params != SvgParams.Default,
                onClick = { showResetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ImageReset,
                    contentDescription = stringResource(R.string.reset_image)
                )
            }
        },
        imagePreview = {
            UrisPreview(
                modifier = Modifier
                    .then(
                        if (!isPortrait) {
                            Modifier
                                .layout { measurable, constraints ->
                                    val placeable = measurable.measure(
                                        constraints = constraints.copy(
                                            maxHeight = constraints.maxHeight + 48.dp.roundToPx()
                                        )
                                    )
                                    layout(placeable.width, placeable.height) {
                                        placeable.place(0, 0)
                                    }
                                }
                                .verticalScroll(rememberScrollState())
                        } else Modifier
                    )
                    .padding(vertical = 24.dp),
                uris = component.uris,
                isPortrait = true,
                onRemoveUri = component::removeUri,
                onAddUris = addImagesImagePicker::pickImage
            )
        },
        showImagePreviewAsStickyHeader = false,
        noDataControls = {
            ImageNotPickedWidget(onPickImage = imagePicker::pickImage)
        },
        controls = {
            SvgParamsSelector(
                value = component.params,
                onValueChange = component::updateParams
            )
        },
        buttons = {
            val save: (oneTimeSaveLocationUri: String?) -> Unit = {
                component.save(
                    oneTimeSaveLocationUri = it,
                    onResult = essentials::parseSaveResults
                )
            }
            var showFolderSelectionDialog by rememberSaveable {
                mutableStateOf(false)
            }
            var showOneTimeImagePickingDialog by rememberSaveable {
                mutableStateOf(false)
            }
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = imagePicker::pickImage,
                isPrimaryButtonVisible = component.uris.isNotEmpty(),
                onPrimaryButtonClick = {
                    save(null)
                },
                onPrimaryButtonLongClick = {
                    showFolderSelectionDialog = true
                },
                actions = {
                    if (isPortrait) it()
                },
                onSecondaryButtonLongClick = {
                    showOneTimeImagePickingDialog = true
                }
            )
            OneTimeSaveLocationSelectionDialog(
                visible = showFolderSelectionDialog,
                onDismiss = { showFolderSelectionDialog = false },
                onSaveRequest = save
            )
            OneTimeImagePickingDialog(
                onDismiss = { showOneTimeImagePickingDialog = false },
                picker = Picker.Multiple,
                imagePicker = imagePicker,
                visible = showOneTimeImagePickingDialog
            )
        },
        canShowScreenData = component.uris.isNotEmpty()
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        title = stringResource(R.string.reset_properties),
        text = stringResource(R.string.reset_properties_sub),
        onReset = {
            component.updateParams(SvgParams.Default)
        }
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