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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.common

import android.net.Uri
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.FilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.AdaptiveLayoutScreen
import com.t8rin.imagetoolbox.core.ui.widget.buttons.BottomButtonsBlock
import com.t8rin.imagetoolbox.core.ui.widget.buttons.ShareButton
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitBackHandler
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ExitWithoutSavingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.LoadingDialog
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.PasswordRequestDialog
import com.t8rin.imagetoolbox.core.ui.widget.image.AutoFilePicker
import com.t8rin.imagetoolbox.core.ui.widget.image.FileNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesPreferenceSheet
import com.t8rin.imagetoolbox.core.ui.widget.text.TopAppBarTitle

@Composable
internal fun BasePdfToolContent(
    component: BasePdfToolComponent,
    pdfPicker: FilePicker,
    isPickedAlready: Boolean,
    canShowScreenData: Boolean,
    title: String,
    actions: @Composable RowScope.() -> Unit,
    topAppBarPersistentActions: @Composable RowScope.() -> Unit = {},
    imagePreview: @Composable () -> Unit,
    placeImagePreview: Boolean = true,
    showImagePreviewAsStickyHeader: Boolean = true,
    controls: (@Composable ColumnScope.(LazyListState) -> Unit)?,
    canSave: Boolean = true,
    onFilledPassword: () -> Unit = {}
) {
    val essentials = rememberLocalEssentials()
    val showConfetti: () -> Unit = essentials::showConfetti

    val saveLauncher = rememberFileCreator(
        mimeType = component.mimeType,
        onSuccess = { uri ->
            component.saveTo(
                uri = uri,
                onResult = essentials::parseFileSaveResult
            )
        }
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    AutoFilePicker(
        onAutoPick = pdfPicker::pickFile,
        isPickedAlready = isPickedAlready
    )

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    LaunchedEffect(component) {
        snapshotFlow { isRtl }
            .collect { component.updateIsRtl(it) }
    }


    AdaptiveLayoutScreen(
        shouldDisableBackHandler = !component.haveChanges,
        placeImagePreview = placeImagePreview,
        showImagePreviewAsStickyHeader = showImagePreviewAsStickyHeader,
        title = {
            TopAppBarTitle(
                title = title,
                input = null,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }

            ShareButton(
                enabled = canSave,
                onShare = {
                    component.performSharing(
                        onSuccess = showConfetti,
                        onFailure = essentials::showFailureToast
                    )
                },
                onEdit = {
                    component.prepareForSharing(
                        onSuccess = {
                            editSheetData = it
                        },
                        onFailure = essentials::showFailureToast
                    )
                },
                dialogTitle = "PDF",
                dialogIcon = Icons.Outlined.Pdf
            )

            ProcessImagesPreferenceSheet(
                uris = editSheetData,
                visible = editSheetData.isNotEmpty(),
                onDismiss = {
                    editSheetData = emptyList()
                },
                extraDataType = component.extraDataType,
                onNavigate = component.onNavigate
            )

            actions()
        },
        topAppBarPersistentActions = {
            if (!canShowScreenData) {
                TopAppBarEmoji()
            } else {
                topAppBarPersistentActions()
            }
        },
        imagePreview = imagePreview,
        controls = controls,
        buttons = { actions ->
            BottomButtonsBlock(
                isNoData = !canShowScreenData,
                isPrimaryButtonVisible = canSave,
                secondaryButtonIcon = Icons.Rounded.FileOpen,
                secondaryButtonText = stringResource(R.string.pick_file),
                onSecondaryButtonClick = pdfPicker::pickFile,
                onPrimaryButtonClick = {
                    saveLauncher.make(component.generatePdfFilename())
                },
                actions = {
                    if (isPortrait) actions()
                }
            )
        },
        noDataControls = {
            FileNotPickedWidget(
                onPickFile = pdfPicker::pickFile
            )
        },
        canShowScreenData = canShowScreenData
    )

    LoadingDialog(
        visible = component.isSaving,
        onCancelLoading = component::cancelSaving
    )

    ExitBackHandler(
        enabled = component.haveChanges,
        onBack = onBack
    )

    ExitWithoutSavingDialog(
        onExit = component.onGoBack,
        onDismiss = { showExitDialog = false },
        visible = showExitDialog
    )

    PasswordRequestDialog(
        isVisible = component.showPasswordRequestDialog,
        onDismiss = {
            component.hidePasswordRequestDialog()
            component.onGoBack()
        },
        onFillPassword = {
            component.setPassword(it)
            onFilledPassword()
        }
    )
}