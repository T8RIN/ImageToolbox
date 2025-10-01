/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CopyAll
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.feature.recognize.text.presentation.screenLogic.RecognizeTextComponent

@Composable
internal fun RecognizeTextButtons(
    component: RecognizeTextComponent,
    multipleImagePicker: ImagePicker,
    actions: @Composable RowScope.() -> Unit
) {
    val essentials = rememberLocalEssentials()
    val isPortrait by isPortraitOrientationAsState()
    val type = component.type
    val isExtraction = type is Screen.RecognizeText.Type.Extraction

    val isHaveText = component.editedText.orEmpty().isNotEmpty()

    val copyText: () -> Unit = {
        component.editedText?.let(essentials::copyToClipboard)
    }

    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showFolderSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val save: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.save(
            oneTimeSaveLocationUri = it,
            onResult = essentials::parseSaveResults
        )
    }
    BottomButtonsBlock(
        isNoData = type == null,
        onSecondaryButtonClick = multipleImagePicker::pickImage,
        onSecondaryButtonLongClick = {
            showOneTimeImagePickingDialog = true
        },
        onPrimaryButtonClick = {
            if (isExtraction) {
                copyText()
            } else {
                save(null)
            }
        },
        onPrimaryButtonLongClick = {
            if (isExtraction) {
                copyText()
            } else {
                showFolderSelectionDialog = true
            }
        },
        primaryButtonIcon = if (isExtraction) {
            Icons.Rounded.CopyAll
        } else {
            Icons.Rounded.Save
        },
        isPrimaryButtonVisible = if (isExtraction) isHaveText else type != null,
        actions = {
            if (isPortrait) actions()
        },
        showNullDataButtonAsContainer = true
    )
    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = save
    )
    OneTimeImagePickingDialog(
        onDismiss = { showOneTimeImagePickingDialog = false },
        picker = Picker.Multiple,
        imagePicker = multipleImagePicker,
        visible = showOneTimeImagePickingDialog
    )
}