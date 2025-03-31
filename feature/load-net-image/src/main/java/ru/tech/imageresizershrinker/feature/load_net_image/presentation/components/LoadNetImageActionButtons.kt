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

package ru.tech.imageresizershrinker.feature.load_net_image.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import ru.tech.imageresizershrinker.core.resources.icons.ImageEdit
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.screenLogic.LoadNetImageComponent

@Composable
internal fun LoadNetImageActionButtons(
    component: LoadNetImageComponent,
    actions: @Composable RowScope.() -> Unit
) {
    val isPortrait by isPortraitOrientationAsState()
    val essentials = rememberLocalEssentials()

    val saveBitmap: (oneTimeSaveLocationUri: String?) -> Unit = {
        component.saveBitmaps(
            oneTimeSaveLocationUri = it,
            onResult = essentials::parseSaveResults
        )
    }

    var showFolderSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var editSheetData by remember {
        mutableStateOf(listOf<Uri>())
    }
    BottomButtonsBlock(
        isNoData = false,
        onSecondaryButtonClick = {
            component.cacheImages {
                editSheetData = it
            }
        },
        isPrimaryButtonVisible = component.parsedImages.isNotEmpty(),
        isSecondaryButtonVisible = component.parsedImages.isNotEmpty(),
        secondaryButtonIcon = Icons.Outlined.ImageEdit,
        onPrimaryButtonClick = {
            saveBitmap(null)
        },
        onPrimaryButtonLongClick = {
            showFolderSelectionDialog = true
        },
        actions = {
            if (isPortrait) actions()
        }
    )
    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = saveBitmap,
        formatForFilenameSelection = component.getFormatForFilenameSelection()
    )

    ProcessImagesPreferenceSheet(
        uris = editSheetData,
        visible = editSheetData.isNotEmpty(),
        onDismiss = {
            editSheetData = emptyList()
        },
        onNavigate = component.onNavigate
    )
}