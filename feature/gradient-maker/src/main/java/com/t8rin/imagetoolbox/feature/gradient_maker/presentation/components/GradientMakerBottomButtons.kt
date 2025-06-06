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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ImagePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.Picker
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeImagePickingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components.model.canPickImage
import com.t8rin.imagetoolbox.feature.gradient_maker.presentation.screenLogic.GradientMakerComponent

@Composable
internal fun GradientMakerBottomButtons(
    component: GradientMakerComponent,
    actions: @Composable RowScope.() -> Unit,
    imagePicker: ImagePicker
) {
    val essentials = rememberLocalEssentials()
    val isPortrait by isPortraitOrientationAsState()

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        if (component.brush != null) {
            component.saveBitmaps(
                oneTimeSaveLocationUri = it,
                onStandaloneGradientSaveResult = essentials::parseSaveResult,
                onResult = essentials::parseSaveResults
            )
        }
    }
    var showFolderSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    BottomButtonsBlock(
        isNoData = component.screenType == null,
        onSecondaryButtonClick = imagePicker::pickImage,
        isSecondaryButtonVisible = component.screenType.canPickImage(),
        isPrimaryButtonVisible = component.brush != null,
        showNullDataButtonAsContainer = true,
        onPrimaryButtonClick = {
            saveBitmap(null)
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
    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = saveBitmap,
        formatForFilenameSelection = component.getFormatForFilenameSelection()
    )
    OneTimeImagePickingDialog(
        onDismiss = { showOneTimeImagePickingDialog = false },
        picker = Picker.Multiple,
        imagePicker = imagePicker,
        visible = showOneTimeImagePickingDialog
    )
}