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

package com.t8rin.imagetoolbox.feature.zip.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.FileNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.zip.presentation.components.ZipControls
import com.t8rin.imagetoolbox.feature.zip.presentation.screenLogic.ZipComponent


@Composable
fun ZipContent(
    component: ZipComponent
) {
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val onBack = {
        if (component.uris.isNotEmpty() && component.compressedArchiveUri != null) {
            showExitDialog = true
        } else component.onGoBack()
    }

    val essentials = rememberLocalEssentials()

    val filePicker = rememberFilePicker(onSuccess = component::setUris)

    AutoFilePicker(
        onAutoPick = filePicker::pickFile,
        isPickedAlready = !component.initialUris.isNullOrEmpty()
    )

    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !(component.uris.isNotEmpty() && component.compressedArchiveUri != null),
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
        addHorizontalCutoutPaddingIfNoPreview = false,
        noDataControls = {
            FileNotPickedWidget(onPickFile = filePicker::pickFile)
        },
        controls = {
            ZipControls(
                component = component,
                lazyListState = it
            )
        },
        buttons = {
            BottomButtonsBlock(
                isNoData = component.uris.isEmpty(),
                onSecondaryButtonClick = filePicker::pickFile,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                isPrimaryButtonVisible = component.uris.isNotEmpty(),
                onPrimaryButtonClick = {
                    component.startCompression(essentials::showFailureToast)
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
        canShowScreenData = component.uris.isNotEmpty()
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