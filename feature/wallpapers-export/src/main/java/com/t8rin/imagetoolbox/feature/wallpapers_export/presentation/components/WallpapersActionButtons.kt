/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ImageEdit
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.feature.wallpapers_export.domain.model.WallpapersResult
import com.t8rin.imagetoolbox.feature.wallpapers_export.presentation.screenLogic.WallpapersExportComponent

@Composable
internal fun WallpapersActionButtons(
    component: WallpapersExportComponent,
    actions: @Composable RowScope.() -> Unit
) {
    if (component.wallpapersState is WallpapersResult.Loading) return
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
    val noData = component.wallpapers.isEmpty()
    val canRefresh = if (noData) true else component.selectedImages.isNotEmpty()
    BottomButtonsBlock(
        isNoData = noData,
        isPrimaryButtonVisible = !noData,
        isPrimaryButtonEnabled = component.selectedImages.isNotEmpty(),
        isSecondaryButtonVisible = canRefresh,
        secondaryButtonIcon = if (noData) Icons.Rounded.Refresh else Icons.Outlined.ImageEdit,
        secondaryButtonText = if (noData) stringResource(R.string.refresh) else stringResource(R.string.edit),
        showNullDataButtonAsContainer = true,
        isScreenHaveNoDataContent = true,
        onSecondaryButtonClick = {
            if (canRefresh && noData) {
                component.loadWallpapers()
            } else {
                component.cacheImages {
                    editSheetData = it
                }
            }
        },
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