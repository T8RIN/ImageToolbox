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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.ImagePicker
import ru.tech.imageresizershrinker.core.ui.utils.content_pickers.Picker
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberLocalEssentials
import ru.tech.imageresizershrinker.core.ui.widget.buttons.BottomButtonsBlock
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeImagePickingDialog
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FiltersComponent

@Composable
internal fun FiltersContentActionButtons(
    component: FiltersComponent,
    actions: @Composable RowScope.() -> Unit,
    imagePicker: ImagePicker,
    pickSingleImagePicker: ImagePicker,
    selectionFilterPicker: ImagePicker,
) {
    val isPortrait by isPortraitOrientationAsState()
    val essentials = rememberLocalEssentials()

    val filterType = component.filterType

    val saveBitmaps: (oneTimeSaveLocationUri: String?) -> Unit = {
        when (filterType) {
            is Screen.Filter.Type.Basic -> {
                component.saveBitmaps(
                    oneTimeSaveLocationUri = it,
                    onResult = essentials::parseSaveResults
                )
            }

            is Screen.Filter.Type.Masking -> {
                component.saveMaskedBitmap(
                    oneTimeSaveLocationUri = it,
                    onComplete = essentials::parseSaveResult
                )
            }

            else -> Unit
        }
    }
    var showFolderSelectionDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showOneTimeImagePickingDialog by rememberSaveable {
        mutableStateOf(false)
    }
    BottomButtonsBlock(
        isNoData = component.basicFilterState.uris.isNullOrEmpty() && component.maskingFilterState.uri == null,
        onSecondaryButtonClick = {
            when (filterType) {
                is Screen.Filter.Type.Basic -> imagePicker.pickImage()
                is Screen.Filter.Type.Masking -> pickSingleImagePicker.pickImage()
                null -> selectionFilterPicker.pickImage()
            }
        },
        onPrimaryButtonClick = {
            saveBitmaps(null)
        },
        onPrimaryButtonLongClick = {
            showFolderSelectionDialog = true
        },
        isPrimaryButtonVisible = component.canSave,
        columnarFab = {
            EnhancedFloatingActionButton(
                onClick = component::showAddFiltersSheet,
                containerColor = MaterialTheme.colorScheme.mixedContainer
            ) {
                when (filterType) {
                    is Screen.Filter.Type.Basic -> {
                        Icon(
                            imageVector = Icons.Rounded.AutoFixHigh,
                            contentDescription = null
                        )
                    }

                    is Screen.Filter.Type.Masking -> {
                        Icon(
                            imageVector = Icons.Rounded.Texture,
                            contentDescription = null
                        )
                    }

                    null -> Unit
                }
            }

        },
        actions = {
            if (isPortrait) actions()
        },
        onSecondaryButtonLongClick = {
            showOneTimeImagePickingDialog = true
        },
        showNullDataButtonAsContainer = true
    )
    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelectionDialog,
        onDismiss = { showFolderSelectionDialog = false },
        onSaveRequest = saveBitmaps,
        formatForFilenameSelection = component.getFormatForFilenameSelection()
    )
    OneTimeImagePickingDialog(
        onDismiss = { showOneTimeImagePickingDialog = false },
        picker = if (filterType !is Screen.Filter.Type.Masking) {
            Picker.Multiple
        } else {
            Picker.Single
        },
        imagePicker = selectionFilterPicker,
        visible = showOneTimeImagePickingDialog
    )
}