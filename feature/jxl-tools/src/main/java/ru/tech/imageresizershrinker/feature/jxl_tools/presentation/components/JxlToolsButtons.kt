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

package ru.tech.imageresizershrinker.feature.jxl_tools.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.ImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedChip
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.screenLogic.JxlToolsComponent

@Composable
internal fun JxlToolsButtons(
    component: JxlToolsComponent,
    actions: @Composable RowScope.() -> Unit,
    onPickImage: () -> Unit,
    imagePicker: ImagePicker
) {
    val uris = when (val type = component.type) {
        is Screen.JxlTools.Type.JpegToJxl -> type.jpegImageUris
        is Screen.JxlTools.Type.JxlToJpeg -> type.jxlImageUris
        is Screen.JxlTools.Type.ImageToJxl -> type.imageUris
        is Screen.JxlTools.Type.JxlToImage -> listOfNotNull(type.jxlUri)
        null -> null
    } ?: emptyList()

    val essentials = rememberLocalEssentials()

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
        isNoData = component.type == null,
        onSecondaryButtonClick = onPickImage,
        isPrimaryButtonVisible = component.canSave,
        onPrimaryButtonClick = {
            save(null)
        },
        onPrimaryButtonLongClick = {
            showFolderSelectionDialog = true
        },
        actions = {
            if (component.type is Screen.JxlTools.Type.JxlToImage) {
                actions()
            } else {
                EnhancedChip(
                    selected = true,
                    onClick = null,
                    selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(uris.size.toString())
                }
            }
        },
        showNullDataButtonAsContainer = true,
        onSecondaryButtonLongClick = if (component.type is Screen.JxlTools.Type.ImageToJxl || component.type == null) {
            {
                showOneTimeImagePickingDialog = true
            }
        } else null
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
}