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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FileOpen
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.ResultLauncher
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
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
    contentPicker: ResultLauncher,
    isPickedAlready: Boolean,
    canShowScreenData: Boolean,
    title: String,
    actions: @Composable RowScope.() -> Unit = {},
    topAppBarPersistentActions: @Composable RowScope.() -> Unit = {},
    imagePreview: @Composable () -> Unit = {},
    placeImagePreview: Boolean = false,
    showImagePreviewAsStickyHeader: Boolean = false,
    controls: (@Composable ColumnScope.(LazyListState) -> Unit)?,
    canSave: Boolean = true,
    canShare: Boolean = canSave,
    onFilledPassword: () -> Unit = {},
    forceImagePreviewToMax: Boolean = false,
    placeControlsSeparately: Boolean = false,
    addHorizontalCutoutPaddingIfNoPreview: Boolean = placeImagePreview && showImagePreviewAsStickyHeader,
    secondaryButtonIcon: ImageVector = Icons.Rounded.FileOpen,
    secondaryButtonText: String = stringResource(R.string.pick_file),
    noDataText: String = stringResource(R.string.pick_file_to_start),
    onPrimaryButtonClick: (() -> Unit)? = null,
    onPrimaryButtonLongClick: (() -> Unit)? = null,
    drawBottomShadow: Boolean = true,
    shareDialogTitle: String = "PDF",
    shareDialogIcon: ImageVector = Icons.Outlined.Pdf
) {
    val saveLauncher = rememberFileCreator(
        mimeType = component.mimeType,
        onSuccess = component::saveTo
    )

    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val isPortrait by isPortraitOrientationAsState()

    val onBack = {
        if (component.haveChanges) showExitDialog = true
        else component.onGoBack()
    }

    AutoFilePicker(
        onAutoPick = contentPicker::launch,
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
        addHorizontalCutoutPaddingIfNoPreview = addHorizontalCutoutPaddingIfNoPreview,
        showImagePreviewAsStickyHeader = showImagePreviewAsStickyHeader,
        title = {
            TopAppBarTitle(
                title = title,
                input = null,
                isLoading = component.isImageLoading,
                size = null
            )
        },
        forceImagePreviewToMax = forceImagePreviewToMax,
        placeControlsSeparately = placeControlsSeparately,
        onGoBack = onBack,
        actions = {
            var editSheetData by remember {
                mutableStateOf(listOf<Uri>())
            }

            ShareButton(
                enabled = canShare,
                onShare = {
                    component.performSharing(
                        onSuccess = AppToastHost::showConfetti,
                        onFailure = AppToastHost::showFailureToast
                    )
                },
                onEdit = {
                    component.prepareForSharing(
                        onSuccess = {
                            editSheetData = it
                        },
                        onFailure = AppToastHost::showFailureToast
                    )
                },
                dialogTitle = shareDialogTitle,
                dialogIcon = shareDialogIcon
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
        controls = controls?.let {
            {
                Column(
                    modifier = if (!placeImagePreview && !isPortrait) {
                        Modifier.windowInsetsPadding(
                            WindowInsets.displayCutout.only(WindowInsetsSides.Start)
                        )
                    } else {
                        Modifier
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    controls(this, it)
                }
            }
        },
        buttons = { actions ->
            BottomButtonsBlock(
                isNoData = !canShowScreenData,
                isPrimaryButtonVisible = canSave,
                secondaryButtonIcon = secondaryButtonIcon,
                secondaryButtonText = secondaryButtonText,
                onSecondaryButtonClick = contentPicker::launch,
                onPrimaryButtonClick = onPrimaryButtonClick ?: {
                    saveLauncher.make(component.createTargetFilename())
                },
                onPrimaryButtonLongClick = onPrimaryButtonLongClick,
                actions = {
                    if (isPortrait) actions()
                },
                enableHorizontalStroke = drawBottomShadow
            )
        },
        noDataControls = {
            FileNotPickedWidget(
                text = noDataText,
                onPickFile = contentPicker::launch
            )
        },
        portraitTopPadding = 20.dp,
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